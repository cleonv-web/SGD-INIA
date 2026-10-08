"""Instalador portable Windows/Linux x64. Solo biblioteca estándar de Python.

Todas las rutas se calculan desde este archivo. Los lanzadores usan el Python
incluido. Se detiene en el primer error y conserva los logs de cada operación.
"""
from pathlib import Path
import argparse, base64, hashlib, http.cookiejar, json, os, platform, re, secrets, shutil, socket, subprocess, sys, tarfile, time, urllib.request, urllib.parse, zipfile, xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent
C = json.loads((ROOT/'configuracion.json').read_text(encoding='utf-8'))
DOMAIN = ROOT/'payara/payara5/glassfish/domains/sgd'
LOG = ROOT/'logs'; LOG.mkdir(exist_ok=True)
DATA = ROOT/'datos'; DATA.mkdir(exist_ok=True)
MODULES = [
    ('sgd','Sgd/tramitedoc','web/target/tramitedoc-web-1.0.war','sisdoc',True),
    ('consulta','ConsultaTuTramite','web/target/consultadoc-web-1.0.war','consulta',True),
    ('verifica','VerificadorDocumental','web/target/verificadoc-web-1.0.war','verifica',True),
    ('mpd','MPVCiudadano','web/target/mpvdoc-web-1.0.war','mpd',True),
    ('notificador','Notificador','target/mailScheduler-1.0-SNAPSHOT.war','sgdNotificador',False),
    ('wsiotramite','wsiotramite','target/wsiotramite-1.0-SNAPSHOT.war','wsiotramite',False),
    ('mpvio','iotramitesgd','target/iotramitesgd-1.0-SNAPSHOT.war','mpvio',False),
    ('recursos','RecursosLocal','target/recursos-local-1.0.war','recursos',True)]
JNDI = ['jdbc/idotradocDS_PostgreSql','jdbc/tdSecurityDS_PostgreSql','jdbc/sgdconsultaDS_PostgreSql','jdbc/consulta_sgd_postgresql','jdbc/verifica_sgd_postgresql','jdbc/mpv_sgd_SQL','jdbc/sgdtareaDS_PostgreSql']

def save(p, value):
    p.parent.mkdir(parents=True,exist_ok=True)
    tmp=p.with_suffix(p.suffix+'.tmp')
    tmp.write_text(json.dumps(value,ensure_ascii=False,indent=2),encoding='utf-8')
    tmp.replace(p)
    if os.name!='nt' and p.is_relative_to(DATA):p.chmod(0o600)

def credentials():
    p=DATA/'credenciales.json'
    if not p.exists():
        save(p,{'postgres':secrets.token_hex(24),'aplicacion':secrets.token_hex(24),
            'usuarios':{u:'Sgd!'+secrets.token_urlsafe(12) for u in ['admin','mesa_demo','area_demo','jefe_demo']}})
    value=json.loads(p.read_text(encoding='utf-8'))
    pw=DATA/'postgres-password.txt';pw.write_text(value['postgres'],encoding='utf-8')
    if os.name!='nt':pw.chmod(0o600)
    access='URL: '+C['url_publica']+'/sisdoc/login.do\nSolo usuarios ficticios de laboratorio.\n'
    access+=''.join(u+' : '+p+'\n' for u,p in value['usuarios'].items())
    (DATA/'ACCESOS-LABORATORIO.txt').write_text(access,encoding='utf-8')
    if os.name!='nt':(DATA/'ACCESOS-LABORATORIO.txt').chmod(0o600)
    return value

def redact(text):
    f=DATA/'credenciales.json'
    if f.exists():
        v=json.loads(f.read_text(encoding='utf-8'))
        for pw in [v['postgres'],v['aplicacion'],*v['usuarios'].values()]:text=text.replace(pw,'<clave omitida>')
    return re.sub(r'(?i)\b[0-9a-f]{64,}\b','<valor cifrado>',text)

def run(args, *, cwd=None, env=None, input=None, log=None, check=True, binary=False):
    r=subprocess.run([str(x) for x in args],cwd=cwd or ROOT,env=env,input=input,capture_output=True,
        **({} if binary else {'text':True,'encoding':'utf-8','errors':'replace'}))
    if not binary:
        output=redact(r.stdout+r.stderr)
        if log:(LOG/log).write_text(output,encoding='utf-8')
        if check and r.returncode:raise RuntimeError('Falló '+str(args[0])+':\n'+output[-3500:]+('\nLog: '+str(LOG/log) if log else ''))
    elif check and r.returncode:raise RuntimeError(redact(r.stderr.decode('utf-8',errors='replace')))
    return r

def java_home():
    if os.name=='nt':return next((ROOT/'java/windows').glob('jdk*'))
    if platform.machine() not in ['x86_64','AMD64']:raise RuntimeError('Este paquete incluye Java para x64. Para ARM se necesita un JDK 8 ARM y otro paquete.')
    home=ROOT/'java/linux'
    if not home.exists():
        home.mkdir()
        with tarfile.open(ROOT/'java/linux-x64.tar.gz') as z:
            for m in z.getmembers():
                m.name='/'.join(m.name.split('/')[1:])
                if m.name:z.extract(m,home,filter='data')
    for p in (home/'bin').iterdir():p.chmod(0o755)
    return home

def java_env():
    home=java_home(); env=os.environ.copy(); env['JAVA_HOME']=str(home)
    env['PATH']=str(home/'bin')+os.pathsep+env.get('PATH','')
    env['SGD_DB_PASSWORD']=credentials()['aplicacion']
    return env

def admin(args,check=True,log=None):
    return run([java_home()/'bin'/('java.exe' if os.name=='nt' else 'java'),'-jar',
        ROOT/'payara/payara5/glassfish/lib/client/appserver-cli.jar','--host','127.0.0.1',
        '--port',C['puerto_admin'],'--interactive=false',*args],env=java_env(),check=check,log=log)

def pg_home():
    if os.name=='nt':return ROOT/'bd/postgresql/windows'
    home=ROOT/'bd/postgresql/linux'
    if not home.exists():
        home.mkdir(parents=True)
        with tarfile.open(ROOT/'bd/postgresql-linux-x64.tar.gz') as z:z.extractall(home,filter='data')
    for f in (home/'bin').iterdir():f.chmod(0o755)
    return home

def pg_env():
    env=os.environ.copy();home=pg_home()
    env['PATH']=str(home/'bin')+os.pathsep+env.get('PATH','')
    env['PGPASSWORD']=credentials()['postgres']
    if os.name!='nt':env['LD_LIBRARY_PATH']=str(home/'lib')+os.pathsep+env.get('LD_LIBRARY_PATH','')
    return env

def pg_tool(name,args,**kwargs):
    # Windows: postgres hereda los handles de pg_ctl. Un PIPE de salida hace
    # esperar al instalador hasta que termine el servidor, aunque start ya salió.
    if os.name=='nt' and name=='pg_ctl' and 'start' in args:
        log=kwargs.pop('log','postgresql-iniciar.log')
        with (LOG/log).open('w',encoding='utf-8') as output:
            result=subprocess.run([str(pg_home()/'bin/pg_ctl.exe'),*[str(x) for x in args]],
                cwd=ROOT,env=pg_env(),stdin=subprocess.DEVNULL,stdout=output,stderr=subprocess.STDOUT)
        if result.returncode:raise RuntimeError('PostgreSQL no inició. Revisar logs/'+log)
        return result
    return run([pg_home()/'bin'/(name+('.exe' if os.name=='nt' else '')),*args],env=pg_env(),**kwargs)

def connection(db=None):
    return ['-h',C['host_bd'],'-p',str(C['puerto_bd']),'-U','postgres','-d',db or C['base']]

def psql(sql, *, db=None, transaction=False, log=None, check=True):
    return pg_tool('psql',[*connection(db),'-X','-v','ON_ERROR_STOP=1',
        *(['--single-transaction'] if transaction else []),'-f','-'],input=sql,log=log,check=check)

def scalar(sql):
    return pg_tool('psql',[*connection(),'-X','-v','ON_ERROR_STOP=1','-At','-c',sql]).stdout.strip()

def quote(v):return "'"+str(v).replace("'","''")+"'"

def jdbc_sql(sql,log):
    driver=ROOT/'payara/driver/postgresql-42.7.8.jar';j=java_home()/'bin'
    run([j/('javac.exe' if os.name=='nt' else 'javac'),'-cp',driver,ROOT/'herramientas/ValidarBase.java'])
    temporary=DATA/'consulta-jdbc.tmp.sql';temporary.write_text(sql,encoding='utf-8')
    try:
        return run([j/('java.exe' if os.name=='nt' else 'java'),'-cp',str(driver)+os.pathsep+str(ROOT/'herramientas'),
            'ValidarBase',f"jdbc:postgresql://{C['host_bd']}:{C['puerto_bd']}/{C['base']}",temporary],env=java_env(),log=log)
    finally:temporary.unlink(missing_ok=True)

def diagnosticar():
    if C.get('modo','completo') not in ['completo','solo_bd','solo_payara']:raise RuntimeError('modo: completo, solo_bd o solo_payara')
    if not re.fullmatch('[a-z][a-z0-9_]*',C['base']):raise RuntimeError('base: solo minúsculas, números y guion bajo.')
    if not re.fullmatch(r'\d{11}',C['ruc']):raise RuntimeError('RUC debe tener 11 dígitos')
    if not re.fullmatch(r'\d{4}',str(C['anio'])):raise RuntimeError('Año inválido')
    for k in ['puerto_bd','puerto_http','puerto_https','puerto_admin']:
        if not isinstance(C[k],int) or not 1024<C[k]<60000:raise RuntimeError('Puerto inválido: '+k)
    if len({C[k] for k in ['puerto_bd','puerto_http','puerto_https','puerto_admin']})!=4:raise RuntimeError('Los puertos deben ser distintos')
    if C.get('modo')=='solo_payara' and not (DATA/'credenciales.json').exists():
        raise RuntimeError('Modo solo_payara: copiar datos/credenciales.json desde el equipo PostgreSQL antes de instalar.')
    if os.name!='nt' and os.geteuid()==0 and C.get('modo')!='solo_payara':
        raise RuntimeError('PostgreSQL no permite ejecutarse como root. Usar una cuenta normal con escritura en esta carpeta.')
    r=run([java_home()/'bin'/('java.exe' if os.name=='nt' else 'java'),'-version'],log='java-version.log')
    if '1.8.0' not in r.stderr+r.stdout:raise RuntimeError('Se requiere el Java 8 incluido')
    r=pg_tool('postgres',['--version'],log='postgresql-version.log')
    if '17.11' not in r.stdout:raise RuntimeError('PostgreSQL portable debe ser 17.11')
    credentials()
    print('Diagnóstico correcto: PostgreSQL 17.11 nativo y Java 8 portable. No requiere Docker.',flush=True)

def base():
    credentials();diagnosticar();datadir=DATA/'postgresql'
    if not (datadir/'PG_VERSION').exists():
        if datadir.exists() and any(datadir.iterdir()):raise RuntimeError('datos/postgresql no está vacío y no contiene un clúster válido. No se reemplaza.')
        datadir.mkdir(exist_ok=True)
        pg_tool('initdb',['-D',datadir,'-U','postgres','--encoding=UTF8','--locale=C',
            '--auth-local=scram-sha-256','--auth-host=scram-sha-256','--pwfile',DATA/'postgres-password.txt'],log='postgresql-inicializar.log')
    if (datadir/'PG_VERSION').read_text().strip()!='17':raise RuntimeError('La carpeta de datos no corresponde a PostgreSQL 17')
    conf=datadir/'postgresql.conf'
    marker="include_if_exists = 'sgd-portable.conf'"
    text=conf.read_text(encoding='utf-8')
    if marker not in text:conf.write_text(text+'\n'+marker+'\n',encoding='utf-8')
    bind=C.get('direccion_bd','127.0.0.1')
    if bind not in ['127.0.0.1','0.0.0.0']:raise RuntimeError('direccion_bd debe ser 127.0.0.1 o 0.0.0.0')
    settings="listen_addresses = '"+bind+"'\nport = "+str(C['puerto_bd'])+"\nunix_socket_directories = ''\npassword_encryption = 'scram-sha-256'\ntimezone = 'America/Lima'\n"
    (datadir/'sgd-portable.conf').write_text(settings,encoding='utf-8')
    if C.get('red_cliente_bd'):
        import ipaddress
        network=str(ipaddress.ip_network(C['red_cliente_bd'],strict=False))
        hba=datadir/'pg_hba.conf';rule='host '+C['base']+' sgd_app '+network+' scram-sha-256'
        text=hba.read_text(encoding='utf-8')
        if rule not in text:hba.write_text(text+'\n'+rule+'\n',encoding='utf-8')
    if pg_tool('pg_ctl',['-D',datadir,'status'],check=False).returncode!=0:
        with socket.socket() as sock:
            try:sock.bind(('127.0.0.1',C['puerto_bd']))
            except OSError:raise RuntimeError('Puerto PostgreSQL ocupado: '+str(C['puerto_bd'])+'. Cambiar configuración; no se detienen motores ajenos.')
        pg_tool('pg_ctl',['-D',datadir,'-l',LOG/'postgresql-servidor.log','-w','-t','120','start'],log='postgresql-iniciar.log')
    exists=psql('SELECT EXISTS(SELECT 1 FROM pg_database WHERE datname='+quote(C['base'])+');',db='postgres').stdout
    if re.search(r'(?m)^ f\s*$',exists):psql('CREATE DATABASE '+C['base']+';',db='postgres',log='bd-crear-base.log')
    print('PostgreSQL nativo preparado.',flush=True)

def maven(args, project=None, log=None):
    exe=ROOT/'herramientas/maven/bin'/('mvn.cmd' if os.name=='nt' else 'mvn')
    if os.name!='nt':exe.chmod(0o755)
    return run([exe,'-o','-B','-ntp','-s',ROOT/'herramientas/maven-settings.xml',
        '-Dmaven.repo.local='+str(ROOT/'herramientas/maven-repo'),*args],cwd=project,env=java_env(),log=log)

def compilar():
    maven(['install:install-file','-Dfile='+str(ROOT/'herramientas/dependencias/wsoapIopCliente-1.0.jar'),
        '-DgroupId=pe.gob.onpe.wsoap','-DartifactId=wsoapIopCliente','-Dversion=1.0','-Dpackaging=jar'],log='compilar-dependencia-wsoap.log')
    for name in ['itext-2.1.7.js2','imaging-01012005']:
        maven(['install:install-file','-Dfile='+str(ROOT/'herramientas/dependencias'/f'{name}.jar'),
            '-DpomFile='+str(ROOT/'herramientas/dependencias'/f'{name}.pom')],log=f'compilar-dependencia-{name}.log')
    lib=ROOT/'fuentes/iotramitesgd/src/main/webapp/WEB-INF/lib'
    for name in ['srvccuo','srvciopidetramite','srvcsunat']:
        jar=next(lib.glob(name+'*.jar'))
        maven(['install:install-file','-Dfile='+str(jar),'-DgroupId=pe.gob.segdi','-DartifactId='+name,'-Dversion=1.0','-Dpackaging=jar'],log=f'compilar-dependencia-{name}.log')
    manifest=[]
    for name,project,war,context,active in MODULES:
        src=ROOT/'fuentes'/project
        if not (src/'pom.xml').exists():raise RuntimeError('Falta fuente '+project+'. No se reemplaza una fuente existente por su WAR antiguo.')
        print('Compilando desde fuentes: '+name,flush=True)
        maven(['clean','install','-DskipTests'],src,f'compilar-{name}.log')
        output=ROOT/'binarios'/f'{name}.war';shutil.copy2(src/war,output)
        source_hash=hashlib.sha256();source_root=src.parent if name=='sgd' else src
        for f in sorted(source_root.rglob('*')):
            if f.is_file() and 'target' not in f.relative_to(source_root).parts:
                source_hash.update(f.relative_to(source_root).as_posix().encode());source_hash.update(f.read_bytes())
        manifest.append({'modulo':name,'origen':'fuentes/'+project,'raiz_hash':source_root.relative_to(ROOT).as_posix(),'fuentes_sha256':source_hash.hexdigest(),'war_sha256':hashlib.sha256(output.read_bytes()).hexdigest()})
    f=ROOT/'binarios/proveedor/wstradoc.war'
    manifest.append({'modulo':'wstradoc','origen':'proveedor: no hay fuentes','war_sha256':hashlib.sha256(f.read_bytes()).hexdigest()})
    save(LOG/'compilacion.json',manifest)
    print('Ocho WAR compilados. wstradoc: única aplicación sin fuentes.',flush=True)

def cipher(password):
    jar=ROOT/'fuentes/Sgd/libreria/target/libreria-onpe-1.0.jar'
    if not jar.exists():raise RuntimeError('Primero ejecutar SGD compilar para generar la librería de cifrado.')
    java=java_home()/'bin'
    run([java/('javac.exe' if os.name=='nt' else 'javac'),'-encoding','UTF-8','-cp',jar,
        ROOT/'herramientas/CifrarClave.java',ROOT/'herramientas/ConfigurarEntidad.java'],log='compilar-utilidades.log')
    r=run([java/('java.exe' if os.name=='nt' else 'java'),'-cp',str(jar)+os.pathsep+str(ROOT/'herramientas'),'CifrarClave'],input=password+'\n')
    value=r.stdout.strip()
    if not re.fullmatch('[A-Fa-f0-9]+',value):raise RuntimeError('La utilidad no generó un cifrado válido')
    return value

def datos():
    marker=scalar("SELECT to_regclass('idosgd.instalacion_portable') IS NOT NULL")
    if marker=='t':
        print('Base ya inicializada: no se repiten esquema ni usuarios.',flush=True);return
    if scalar("SELECT EXISTS(SELECT 1 FROM pg_namespace WHERE nspname='idosgd')")=='t':raise RuntimeError('Existe esquema sin marca de instalación. No sobrescribir: revisar o usar una base nueva.')
    sql=generar_sql()
    psql(sql,transaction=True,log='bd-instalacion.log')
    print('Esquema, catálogos y cuatro usuarios ficticios creados en una transacción.',flush=True)

def generar_sql():
    v=credentials();header=''
    for u,p in v['usuarios'].items():header+='\\set clave'+('' if u=='admin' else '_'+u)+' '+quote(cipher(p))+'\n'
    header+='\\set ruta '+quote((ROOT/'datos/documentos').as_posix())+'\n'
    schema=(ROOT/'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
    seed=(ROOT/'bd/02-inia-usuarios-ficticios.sql').read_text(encoding='utf-8').replace("'2026'",quote(C['anio']))
    tail="""
CREATE TABLE idosgd.instalacion_portable(version text PRIMARY KEY, instalada timestamp NOT NULL DEFAULT now());
INSERT INTO idosgd.instalacion_portable(version) VALUES ('1');
CREATE ROLE sgd_app LOGIN PASSWORD %s;
GRANT CONNECT ON DATABASE %s TO sgd_app;
GRANT USAGE ON SCHEMA idosgd, public TO sgd_app;
GRANT SELECT,INSERT,UPDATE,DELETE ON ALL TABLES IN SCHEMA idosgd TO sgd_app;
GRANT USAGE,SELECT,UPDATE ON ALL SEQUENCES IN SCHEMA idosgd TO sgd_app;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA idosgd, public TO sgd_app;
ALTER ROLE sgd_app IN DATABASE %s SET search_path=idosgd,public;
"""%(quote(v['aplicacion']),C['base'],C['base'])
    for k,value in {'DE_INSTITUCION':C['entidad'],'RAZONSOCIAL_INSTITUCION':C['entidad'],'RUC_INSTITUCION':C['ruc'],
        'URL_WEB_VERIFICA':C['url_publica']+'/verifica/inicio.do','URL_WEB_DESC_ANEXO':C['url_publica']+'/verifica/inicio.do'}.items():
        tail+='UPDATE idosgd.tdtr_parametros SET de_par='+quote(value)+' WHERE co_par='+quote(k)+';\n'
    (ROOT/'datos/documentos').mkdir(exist_ok=True)
    # Una única transacción: esquema + catálogos + ficticios + permisos + marca.
    sql=header+schema+'\n'+seed+'\n'+tail
    f=DATA/'INSTALACION-MANUAL-PRIVADA.sql';f.write_text(sql,encoding='utf-8')
    if os.name!='nt':f.chmod(0o600)
    print('SQL de recuperación generado en datos/INSTALACION-MANUAL-PRIVADA.sql. Contiene claves privadas.',flush=True)
    return sql

def domain_properties():
    for folder in (ROOT/'payara/plantillas').iterdir():
        if folder.is_dir():shutil.copytree(folder,DOMAIN/folder.name,dirs_exist_ok=True)
    for folder in ['temp','reportes','logs']:(DOMAIN/folder).mkdir(exist_ok=True)
    favicon=ROOT/'fuentes/RecursosLocal/src/main/webapp/favicon.ico'
    if favicon.exists():shutil.copy2(favicon,DOMAIN/'docroot/favicon.ico')
    jar=ROOT/'fuentes/Sgd/libreria/target/libreria-onpe-1.0.jar'
    cipher('comprobar-utilidad')
    run([java_home()/'bin'/('java.exe' if os.name=='nt' else 'java'),'-cp',str(jar)+os.pathsep+str(ROOT/'herramientas'),
        'ConfigurarEntidad',DOMAIN/'sgdproperties/application.properties',(DOMAIN/'temp').as_posix(),
        (DOMAIN/'reportes').as_posix(),C['entidad'],C['ruc']],log='payara-propiedades.log')
    edits={'AD_VALIDACION_ACTIVA':'0','AD_HOST':'127.0.0.1','no_entidad':C['entidad'],'web_socket_server':C['url_publica'].replace('http:','ws:').replace('https:','wss:')+'/wstradoc/chat/',
        'url_mesa_virtual':C['url_publica']+'/mpd/inicio.do','url_consulta_avanzada':C['url_publica']+'/consulta/inicio.do',
        'url_sgdRestTask':C['url_publica']+'/sgdNotificador/rest'}
    f=DOMAIN/'sgdproperties/application.properties';s=f.read_text(encoding='utf-8')
    for k,value in edits.items():s=re.sub(r'^'+k+r'\s*=.*$',lambda m:k+'='+value,s,flags=re.M)
    f.write_text(s,encoding='utf-8')
    for f in DOMAIN.glob('*properties/application.properties'):
        s=f.read_text(encoding='utf-8')
        s=re.sub(r'^sigla_institucion\s*=.*$',lambda m:'sigla_institucion='+C['entidad'],s,flags=re.M)
        f.write_text(s,encoding='utf-8')
    driver=ROOT/'payara/driver/postgresql-42.7.8.jar';target=DOMAIN/'lib/postgresql-42.7.8.jar'
    if not target.exists() or hashlib.sha256(driver.read_bytes()).digest()!=hashlib.sha256(target.read_bytes()).digest():shutil.copy2(driver,target)

def configurar():
    DOMAIN.parent.mkdir(parents=True,exist_ok=True)
    if DOMAIN.exists() and re.search(r'^sgd running',admin(['list-domains']).stdout,re.M):
        admin(['stop-domain','sgd'],log='payara-reconfigurar-detener.log')
    if not DOMAIN.exists():
        for port in [C['puerto_http'],C['puerto_admin'],C['puerto_https']]:
            with socket.socket() as s:
                try:s.bind(('127.0.0.1',port))
                except OSError:raise RuntimeError('Puerto ocupado: '+str(port)+'. Cambiar configuracion.json.')
        admin(['create-domain','--nopassword=true','--adminport',C['puerto_admin'],'--instanceport',C['puerto_http'],
            '--domainproperties',f"jms.port={C['puerto_http']+1000}:domain.jmxPort={C['puerto_http']+1001}:orb.listener.port={C['puerto_http']+1002}:orb.ssl.port={C['puerto_http']+1003}:orb.mutualauth.port={C['puerto_http']+1004}:http.ssl.port={C['puerto_https']}",'sgd'],log='payara-crear-dominio.log')
    xml=DOMAIN/'config/domain.xml';tree=ET.parse(xml);conf=tree.find('.//config[@name="server-config"]')
    jvm=conf.find('java-config')
    for opt in jvm.findall('jvm-options'):
        if opt.text and opt.text.startswith('-Xmx'):opt.text='-Xmx1024m'
    for option in ['-Dcatalina.base=${com.sun.aas.instanceRoot}','-Djava.io.tmpdir=${com.sun.aas.instanceRoot}/temp','-Duser.timezone=America/Lima']:
        if option not in [x.text for x in jvm.findall('jvm-options')]:ET.SubElement(jvm,'jvm-options').text=option
    haz=conf.find('hazelcast-runtime-configuration')
    if haz is not None:haz.set('enabled','false')
    for listener in conf.iter('network-listener'):
        listener.set('address','127.0.0.1' if listener.get('name')=='admin-listener' else C['direccion_http'])
        if listener.get('name') in ['http-listener-1','http-listener-2','admin-listener']:
            listener.set('port',str(C[{'http-listener-1':'puerto_http','http-listener-2':'puerto_https','admin-listener':'puerto_admin'}[listener.get('name')]]))
    for prop in tree.findall('.//system-property'):
        names={'HTTP_LISTENER_PORT':'puerto_http','HTTP_SSL_LISTENER_PORT':'puerto_https','ASADMIN_LISTENER_PORT':'puerto_admin'}
        if prop.get('name') in names:prop.set('value',str(C[names[prop.get('name')]]))
        auxiliary={'JMS_PROVIDER_PORT':1000,'JMX_SYSTEM_CONNECTOR_PORT':1001,'IIOP_LISTENER_PORT':1002,'IIOP_SSL_LISTENER_PORT':1003,'IIOP_SSL_MUTUALAUTH_PORT':1004}
        if prop.get('name') in auxiliary:prop.set('value',str(C['puerto_http']+auxiliary[prop.get('name')]))
    for node in conf.iter('jms-host'):node.set('port',str(C['puerto_http']+1000))
    for node in conf.iter('jmx-connector'):node.set('port',str(C['puerto_http']+1001));node.set('address','127.0.0.1')
    for node in conf.iter('iiop-listener'):
        node.set('port',str(C['puerto_http']+{'orb-listener-1':1002,'SSL':1003,'SSL_MUTUALAUTH':1004}.get(node.get('id'),1002)))
        node.set('address','127.0.0.1')
    tree.write(xml,encoding='UTF-8',xml_declaration=True)
    domain_properties()
    iniciar(solo=True)
    pools=admin(['list-jdbc-connection-pools']).stdout
    if 'SGDPostgreSQL' not in pools:
        prop=f"serverName={C['host_bd']}:portNumber={C['puerto_bd']}:databaseName={C['base']}:user=sgd_app:password=${{ENV=SGD_DB_PASSWORD}}"
        admin(['create-jdbc-connection-pool','--datasourceclassname','org.postgresql.ds.PGSimpleDataSource',
            '--restype','javax.sql.DataSource','--property',prop,'SGDPostgreSQL'],log='payara-pool.log')
    else:
        for k,value in {'serverName':C['host_bd'],'portNumber':C['puerto_bd'],'databaseName':C['base']}.items():
            admin(['set',f'resources.jdbc-connection-pool.SGDPostgreSQL.property.{k}={value}'])
    resources=admin(['list-jdbc-resources']).stdout
    for name in JNDI:
        if name not in resources:admin(['create-jdbc-resource','--connectionpoolid','SGDPostgreSQL',name])
    admin(['ping-connection-pool','SGDPostgreSQL'],log='payara-jdbc.log')
    (DATA/'documentos').mkdir(exist_ok=True)
    path=quote((DATA/'documentos').as_posix())
    jdbc_sql('UPDATE idosgd.tdtx_config_emp SET de_dir_emi='+path+',de_dir_rec='+path+',de_dir_ane='+path+
        " WHERE co_emp IN ('00000','49001','49002','49003');",'bd-rutas-portables.log')
    print('Payara configurado y conexión JDBC comprobada.',flush=True)

def iniciar(solo=False):
    if not solo:
        if C.get('modo')!='solo_payara':base()
        domain_properties()
    if re.search(r'^sgd running',admin(['list-domains']).stdout,re.M):
        print('Payara ya está iniciado.',flush=True)
    else:admin(['start-domain','sgd'],log='payara-iniciar.log')

def desplegar():
    manifest=LOG/'compilacion.json'
    if not manifest.exists():raise RuntimeError('Falta manifiesto. Ejecutar SGD compilar.')
    info={m['modulo']:m for m in json.loads(manifest.read_text(encoding='utf-8'))}
    for name,project,war,context,active in MODULES:
        if not active and not C['activar_integraciones']:continue
        f=ROOT/'binarios'/f'{name}.war'
        if hashlib.sha256(f.read_bytes()).hexdigest()!=info[name]['war_sha256']:raise RuntimeError('WAR modificado después de compilar: '+name)
        print('Desplegando: '+name,flush=True)
        admin(['deploy','--force=true','--name',name,'--contextroot',context,f],log=f'payara-desplegar-{name}.log')
    f=ROOT/'binarios/proveedor/wstradoc.war'
    if hashlib.sha256(f.read_bytes()).hexdigest()!=info['wstradoc']['war_sha256']:raise RuntimeError('Binario wstradoc alterado')
    admin(['deploy','--force=true','--name','wstradoc','--contextroot','wstradoc',f],log='payara-desplegar-wstradoc.log')

def verificar():
    if C.get('modo')=='solo_payara':
        jdbc_sql((ROOT/'bd/03-validar.sql').read_text(encoding='utf-8'),'bd-validacion.log')
    else:psql((ROOT/'bd/03-validar.sql').read_text(encoding='utf-8'),transaction=True,log='bd-validacion.log')
    admin(['ping-connection-pool','SGDPostgreSQL'],log='payara-jdbc.log')
    checks=[]
    for path in ['/sisdoc/login.do','/consulta/inicio.do','/verifica/inicio.do','/mpd/inicio.do','/recursos/favicon.ico']:
        url='http://127.0.0.1:'+str(C['puerto_http'])+path
        opener=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        with opener.open(url,timeout=45) as r:
            body=r.read();check={'ruta':path,'http':r.status,'bytes':len(body)};checks.append(check)
            if not body:raise RuntimeError('Respuesta vacía: '+path)
            if path.endswith('login.do') and b'coUsuario' not in body:raise RuntimeError('SGD no devolvió el login esperado')
            if not path.endswith('.ico'):
                page=body.decode('utf-8',errors='replace');assets=[]
                if path.endswith('login.do'):
                    script_refs=re.findall(r'<script[^>]*src="([^"]+)"',page)
                    jquery_pos=next((i for i,ref in enumerate(script_refs) if '/jquery-3.10.1.min.js' in ref),None)
                    default_pos=next((i for i,ref in enumerate(script_refs) if '/js/default.js' in ref),None)
                    if jquery_pos is None or default_pos is None or jquery_pos >= default_pos:
                        raise RuntimeError('Login: default.js debe cargarse después de jQuery.')
                for ref in sorted(set(re.findall(r'''(?:src|href)\s*=\s*["']([^"']+)''',page))):
                    if ref.startswith(('#','data:','javascript:','mailto:','http:','https:')):continue
                    asset=urllib.parse.urljoin(r.url,ref)
                    if not asset.split('?')[0].lower().endswith(('.js','.css','.png','.jpg','.gif','.ico','.woff','.svg')):continue
                    try:
                        with opener.open(asset,timeout=30) as a:
                            if a.status!=200:raise RuntimeError('Recurso no disponible: '+asset)
                    except urllib.error.HTTPError as e:raise RuntimeError('Recurso '+str(e.code)+': '+asset) from e
                    assets.append(ref)
                check['recursos_verificados']=len(assets)
    def encode(v):
        return base64.b64encode((base64.b64encode(v.encode()).decode()+'WSXSDSDFFGDFGSDSEUY87OLKMN12DF').encode()).decode()
    auth=[]
    url='http://127.0.0.1:'+str(C['puerto_http'])+'/sisdoc/login.do'
    for user,password in credentials()['usuarios'].items():
        variants=list(dict.fromkeys([user,user.upper()]))
        for submitted_user in variants:
            opener=urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
            opener.open(url,timeout=45).close()
            form=urllib.parse.urlencode({'coUsuario':encode(submitted_user),'dePassword':encode(password),'inAccesoLocal':'1','contIntentos':'0','captcha':''}).encode()
            with opener.open(url,data=form,timeout=45) as r:html=r.read().decode('utf-8',errors='replace')
            if 'id="loginForm"' in html or 'id="divBandejaEntrada"' not in html or '<strong>Salir</strong>' not in html:
                (LOG/'login-fallido.html').write_text(html,encoding='utf-8')
                raise RuntimeError('No se confirmó login de '+submitted_user+'. Revisar logs/login-fallido.html y server.log')
        auth.append({'usuario':user,'autenticacion':True,'variantes_verificadas':variants})
    key=base64.b64encode(secrets.token_bytes(16)).decode()
    with socket.create_connection(('127.0.0.1',C['puerto_http']),timeout=20) as s:
        s.sendall((f'GET /wstradoc/chat/987654321/BROWSER HTTP/1.1\r\nHost: localhost:{C["puerto_http"]}\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Key: {key}\r\nSec-WebSocket-Version: 13\r\n\r\n').encode())
        head=s.recv(4096)
        if not head.startswith(b'HTTP/1.1 101'):raise RuntimeError('No se confirmó handshake WebSocket')
    save(LOG/'pruebas-funcionales.json',{'http':checks,'usuarios':auth,'websocket':101,'fecha':time.strftime('%Y-%m-%d %H:%M:%S'),'plataforma':platform.system()})
    print('Validaciones SQL, JDBC, HTTP, cuatro logins y WebSocket correctas.',flush=True)

def respaldar():
    if C.get('modo')=='solo_payara':raise RuntimeError('Respaldar desde el equipo que contiene PostgreSQL.')
    target=DATA/'respaldo-portable.dump';temporary=target.with_suffix('.tmp')
    with temporary.open('wb') as f:
        args=[str(pg_home()/'bin'/('pg_dump.exe' if os.name=='nt' else 'pg_dump')),*connection(),'-Fc']
        r=subprocess.run(args,cwd=ROOT,env=pg_env(),stdout=f,stderr=subprocess.PIPE)
        if r.returncode:raise RuntimeError(redact(r.stderr.decode(errors='replace')))
    temporary.replace(target)
    print('Respaldo creado: '+str(target),flush=True)

def restaurar():
    f=DATA/'respaldo-portable.dump'
    if not f.exists():raise RuntimeError('Falta datos/respaldo-portable.dump')
    if scalar("SELECT EXISTS(SELECT 1 FROM pg_namespace WHERE nspname='idosgd')")=='t':raise RuntimeError('Restaurar solo en base nueva; no se reemplaza información.')
    v=credentials()
    if scalar("SELECT EXISTS(SELECT 1 FROM pg_roles WHERE rolname='sgd_app')")=='f':
        psql('CREATE ROLE sgd_app LOGIN PASSWORD '+quote(v['aplicacion'])+';',log='bd-restaurar-rol.log')
    pg_tool('pg_restore',[*connection(),'--exit-on-error','--single-transaction',f],log='bd-restauracion.log')
    psql('ALTER ROLE sgd_app IN DATABASE '+C['base']+' SET search_path=idosgd,public;\n'+
        'UPDATE idosgd.tdtx_config_emp SET de_dir_emi='+quote((DATA/'documentos').as_posix())+',de_dir_rec='+quote((DATA/'documentos').as_posix())+',de_dir_ane='+quote((DATA/'documentos').as_posix())+';',transaction=True)
    print('Respaldo restaurado en base nueva.',flush=True)

def detener():
    admin(['stop-domain','sgd'],check=False,log='payara-detener.log')
    if C.get('modo')!='solo_payara' and (DATA/'postgresql/PG_VERSION').exists() and pg_tool('pg_ctl',['-D',DATA/'postgresql','status'],check=False).returncode==0:
        pg_tool('pg_ctl',['-D',DATA/'postgresql','-m','fast','-w','-t','120','stop'],log='postgresql-detener.log')
    print('Servicios detenidos; datos conservados.',flush=True)

def empaquetar():
    """ZIP para instalación nueva: fuentes y runtimes, sin la base ni claves locales."""
    target=ROOT.parent/'SGD-INIA-INSTALL_PG-PORTABLE.zip'
    domain_xml=None
    if (DOMAIN/'config/domain.xml').exists():
        tree=ET.parse(DOMAIN/'config/domain.xml')
        for app in list(tree.find('applications')):tree.find('applications').remove(app)
        for server in tree.findall('.//server'):
            for ref in server.findall('application-ref'):server.remove(ref)
        domain_xml=ET.tostring(tree.getroot(),encoding='utf-8',xml_declaration=True)
    count=0
    with zipfile.ZipFile(target.with_suffix('.tmp'), 'w',zipfile.ZIP_DEFLATED,compresslevel=4) as z:
        for f in sorted(ROOT.rglob('*')):
            if not f.is_file():continue
            rel=f.relative_to(ROOT);parts=rel.parts
            if parts[0]=='datos' or 'target' in parts or '__pycache__' in parts or f.suffix in ['.class','.tmp']:continue
            if parts[0]=='logs' and f.name not in ['compilacion.json','pruebas-funcionales.json','qa-rollback.json']:continue
            if parts[:4]==('payara','payara5','glassfish','domains'):
                if len(parts)<6 or parts[4]!='sgd' or parts[5]!='config':continue
                if f.suffix in ['.log','.tmp'] or f.name in ['domain.xml.bak','pid']:continue
                if f.name=='domain.xml' and domain_xml:
                    z.writestr(ROOT.name+'/'+rel.as_posix(),domain_xml);count+=1;continue
            if f.name.endswith(('.lastUpdated','.lock')):continue
            z.write(f,ROOT.name+'/'+rel.as_posix());count+=1
        # Payara necesita estos directorios antes de configurar el dominio prearmado.
        for name in ['datos','logs','payara/payara5/glassfish/domains/sgd/lib','payara/payara5/glassfish/domains/sgd/docroot','payara/payara5/glassfish/domains/sgd/logs','payara/payara5/glassfish/domains/sgd/autodeploy','payara/payara5/glassfish/domains/sgd/applications']:
            z.writestr(ROOT.name+'/'+name+'/',b'')
    target.with_suffix('.tmp').replace(target)
    digest=hashlib.file_digest(target.open('rb'),'sha256').hexdigest()
    target.with_suffix('.zip.sha256').write_text(digest+'  '+target.name+'\n',encoding='ascii')
    print('Paquete limpio creado: '+str(target)+' ('+str(count)+' archivos).',flush=True)

def todo():
    diagnosticar();compilar()
    if C.get('modo')=='solo_payara':
        configurar();desplegar();verificar()
        print('SGD disponible: '+C['url_publica']+'/sisdoc/login.do',flush=True);return
    base()
    if (DATA/'respaldo-portable.dump').exists() and scalar("SELECT EXISTS(SELECT 1 FROM pg_namespace WHERE nspname='idosgd')")=='f':restaurar()
    else:datos()
    if C.get('modo')=='solo_bd':
        psql((ROOT/'bd/03-validar.sql').read_text(encoding='utf-8'),transaction=True,log='bd-validacion.log')
        print('Base nativa lista. Si Payara está en otro equipo, copiar datos/credenciales.json. No repetir la carga SQL.',flush=True);return
    configurar();desplegar();verificar()
    print('SGD disponible: '+C['url_publica']+'/sisdoc/login.do\nClaves: datos/ACCESOS-LABORATORIO.txt',flush=True)

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('accion',choices=['todo','diagnosticar','compilar','base','datos','configurar','desplegar','verificar','iniciar','detener','respaldar','restaurar','generar_sql','empaquetar'])
    args=parser.parse_args()
    try:globals()[args.accion]()
    except Exception as e:
        message=redact(str(e));(LOG/'ULTIMO-ERROR.txt').write_text(message,encoding='utf-8')
        print('ERROR: '+message,file=sys.stderr);sys.exit(1)

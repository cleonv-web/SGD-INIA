@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Ejecutar en el servidor, dentro de la instalacion que atiende HTTPS.
"%~dp0herramientas\python-windows\python.exe" -c "import pathlib,sys; p=pathlib.Path(sys.argv[1]); c=p.read_text(encoding='utf-8').split('# INICIO_PYTHON' + chr(10),1)[1]; exec(compile(c,str(p),'exec'),{'__name__':'__main__','__file__':str(p)})" "%~f0"
set "SGD_HTTPS_RESULTADO=%errorlevel%"
pause
exit /b %SGD_HTTPS_RESULTADO%
# INICIO_PYTHON
"""Reparacion puntual del certificado HTTPS. No ejecuta SQL ni carga usuarios."""
from pathlib import Path
import base64
import datetime
import getpass
import hashlib
import json
import re
import shutil
import socket
import ssl
import sys
import time
import urllib.parse
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent
HOST = 'sgd.inia.gob.pe'
JAVA_SOURCE = r'''
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.util.*;
public class RevisarHttpsSGD {
 static String b(String s) {return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));}
 public static void main(String[] args) throws Exception {
  char[] password=new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8)).readLine().toCharArray();
  try {
   KeyStore ks=KeyStore.getInstance("JKS");
   try(InputStream in=new FileInputStream(args[0])) {ks.load(in,password);}
   Enumeration<String> aliases=ks.aliases();
   while(aliases.hasMoreElements()) {
    String alias=aliases.nextElement();
    if(!ks.isKeyEntry(alias)) continue;
    java.security.cert.Certificate c=ks.getCertificate(alias);
    if(!(c instanceof X509Certificate)) continue;
    X509Certificate x=(X509Certificate)c;
    List<String> dns=new ArrayList<String>();
    Collection<List<?>> sans=x.getSubjectAlternativeNames();
    if(sans!=null) for(List<?> san:sans) if(Integer.valueOf(2).equals(san.get(0))) dns.add((String)san.get(1));
    boolean readable=false;
    try {readable=ks.getKey(alias,password) instanceof PrivateKey;} catch(GeneralSecurityException ignored) {}
    byte[] digest=MessageDigest.getInstance("SHA-256").digest(x.getEncoded());
    StringBuilder hex=new StringBuilder(); for(byte v:digest) hex.append(String.format("%02x",v&255));
    System.out.println("ENTRY\t"+b(alias)+"\t"+b(x.getSubjectX500Principal().getName())+"\t"+
     x.getNotBefore().getTime()+"\t"+x.getNotAfter().getTime()+"\t"+b(String.join(",",dns))+"\t"+hex+"\t"+readable);
   }
  } finally {Arrays.fill(password,'\0');}
 }
}
'''


def hostname_matches(host, name):
    host, name = host.lower().rstrip('.'), name.lower().rstrip('.')
    if name.startswith('*.'):
        return '*' not in name[2:] and host.endswith(name[1:]) and len(host.split('.')) == len(name.split('.'))
    return '*' not in name and host == name


def select_certificate(entries, host, now=None):
    now = time.time() if now is None else now
    matching = [e for e in entries if any(hostname_matches(host, n) for n in e['dns'])]
    valid = [e for e in matching if e['private_key'] and e['start'] <= now <= e['end']]
    preferred = [e for e in valid if e['alias'] == 'sgd-inia']
    if preferred:
        return preferred[0]
    if len(valid) == 1:
        return valid[0]
    if len(valid) > 1:
        raise RuntimeError('Hay varios certificados validos para el dominio. No se cambio la configuracion: ' + ', '.join(e['alias'] for e in valid))
    if matching:
        raise RuntimeError('El certificado del dominio esta vencido, aun no vigente o su clave privada no se abre con la clave maestra. No se modifico HTTPS.')
    raise RuntimeError('No hay un certificado con clave privada para ' + host + ' en el keystore de ESTA instalacion. Verifique que el PFX se importo en este servidor y esta carpeta.')


def patch_domain(raw, alias, port=443):
    tree = ET.fromstring(raw)
    config = tree.find('./configs/config[@name="server-config"]')
    if config is None:
        raise RuntimeError('No se encontro server-config.')
    listener = config.find('./network-config/network-listeners/network-listener[@name="http-listener-2"]')
    if listener is None or listener.get('enabled', 'true') != 'true':
        raise RuntimeError('El listener HTTPS no existe o esta deshabilitado.')
    properties = {p.get('name'): p.get('value') for p in tree.findall('./system-property')}
    properties.update({p.get('name'): p.get('value') for p in config.findall('./system-property')})
    value = listener.get('port', '')
    if value.startswith('${') and value.endswith('}'):
        value = properties.get(value[2:-1], '')
    if value != str(port):
        raise RuntimeError('Este dominio no tiene el listener HTTPS en el puerto 443. No se modifico.')
    protocol = config.find('./network-config/protocols/protocol[@name="' + listener.get('protocol', '') + '"]')
    if protocol is None or protocol.get('security-enabled') != 'true':
        raise RuntimeError('El protocolo del listener no tiene SSL habilitado.')
    node = protocol.find('ssl')
    if node is None or node.get('key-store') or node.get('keyStore'):
        raise RuntimeError('El listener no usa el almacen predeterminado esperado. No se modifico.')
    key_options = [n.text for n in config.findall('./java-config/jvm-options') if (n.text or '').startswith('-Djavax.net.ssl.keyStore=')]
    if key_options != ['-Djavax.net.ssl.keyStore=${com.sun.aas.instanceRoot}/config/keystore.jks']:
        raise RuntimeError('La ruta del keystore de este dominio no es la predeterminada esperada.')
    previous = node.get('cert-nickname', '')
    if previous == alias:
        return raw, previous
    node.set('cert-nickname', alias)
    return ET.tostring(tree, encoding='utf-8', xml_declaration=True), previous


def inspect_keystore(s, keystore, work, password):
    source = work/'RevisarHttpsSGD.java'
    source.write_text(JAVA_SOURCE, encoding='utf8')
    ext = '.exe' if sys.platform == 'win32' else ''
    java = s.java_home()/'bin'
    s.run([java/('javac'+ext), '-encoding', 'UTF-8', '-d', work, source], log='https-compilar-inspector.log')
    r = s.run([java/('java'+ext), '-Dfile.encoding=UTF-8', '-cp', work, 'RevisarHttpsSGD', keystore],
              input=password+'\n', check=False)
    if r.returncode:
        raise RuntimeError('No se pudo abrir el keystore. Verifique la clave maestra de Payara. No se modifico el dominio.')
    entries = []
    for line in r.stdout.splitlines():
        if not line.startswith('ENTRY\t'):
            continue
        _, alias, subject, start, end, dns, fingerprint, private = line.split('\t')
        decode = lambda value: base64.b64decode(value).decode('utf8')
        entries.append(dict(alias=decode(alias), subject=decode(subject), start=int(start)/1000,
                            end=int(end)/1000, dns=decode(dns).split(','), sha256=fingerprint,
                            private_key=private == 'true'))
    return entries


def probe(address, host, port=443, trusted=False):
    # La lectura sin confianza se usa SOLO para comparar la huella publica.
    # La comprobacion de confianza posterior usa las autoridades del sistema.
    context = ssl.create_default_context() if trusted else ssl._create_unverified_context()
    with socket.create_connection((address, port), timeout=12) as stream:
        with context.wrap_socket(stream, server_hostname=host) as channel:
            return hashlib.sha256(channel.getpeercert(binary_form=True)).hexdigest()


def apply_domain(s, xml, updated, backup, fingerprint, host, tls_probe=probe):
    parent = xml.parent.parent.parent
    common = ['--domaindir', str(parent)]
    s.admin(['stop-domain', *common, 'sgd'], log='https-detener-payara.log')
    try:
        temp = xml.with_name('domain-https.tmp')
        temp.write_bytes(updated)
        temp.replace(xml)
        s.admin(['start-domain', *common, 'sgd'], log='https-iniciar-payara.log')
        served = tls_probe('127.0.0.1', host)
        if served != fingerprint:
            raise RuntimeError('El puerto 443 sigue entregando otro certificado. Revisar la instancia que ocupa ese puerto.')
    except Exception:
        s.admin(['stop-domain', *common, 'sgd'], check=False, log='https-recuperacion-detener.log')
        state = s.admin(['list-domains', *common], check=False, log='https-recuperacion-estado.log')
        if state.returncode or not re.search(r'(?mi)^sgd not running\b', state.stdout):
            raise RuntimeError('No se pudo detener Payara para recuperar la configuracion. Respaldo: ' + str(backup))
        shutil.copy2(backup, xml)
        s.admin(['start-domain', *common, 'sgd'], log='https-recuperacion-iniciar.log')
        raise


def main():
    sys.path.insert(0, str(ROOT))
    import sgd as s
    from gestion_data import private_folder
    public = urllib.parse.urlsplit(s.C.get('url_publica', ''))
    if public.scheme != 'https' or public.hostname != HOST or public.port not in (None, 443) or s.C.get('puerto_https') != 443:
        raise RuntimeError('Ejecute este BAT en la instalacion del Windows Server cuya configuracion.json tenga url_publica=https://sgd.inia.gob.pe y puerto_https=443. Carpeta actual: ' + str(ROOT))
    xml = s.DOMAIN/'config/domain.xml'
    keystore = s.DOMAIN/'config/keystore.jks'
    if not xml.is_file() or not keystore.is_file() or not (s.DATA/'credenciales.json').is_file():
        raise RuntimeError('Faltan archivos de una instalacion existente. No se inicializara ni reinstalara nada.')
    common = ['--domaindir', str(s.DOMAIN.parent)]
    status = s.admin(['list-domains', *common], log='https-estado-payara.log')
    if not re.search(r'(?mi)^sgd running\b', status.stdout):
        raise RuntimeError('Inicie primero el SGD con Iniciar-SGD.bat y vuelva a ejecutar este reparador.')
    work = s.LOG/('https-reparacion-' + datetime.datetime.now(datetime.timezone.utc).strftime('%Y%m%dT%H%M%S-%fZ'))
    private_folder(work)
    password = getpass.getpass('Clave MAESTRA de Payara [Enter usa changeit, la inicial]: ') or 'changeit'
    certificate = select_certificate(inspect_keystore(s, keystore, work, password), HOST)
    del password
    end = datetime.datetime.fromtimestamp(certificate['end'], datetime.timezone.utc).isoformat()
    print('Certificado encontrado: ' + certificate['subject'], flush=True)
    print('Alias: ' + certificate['alias'] + '. Vence: ' + end, flush=True)
    if certificate['end'] - time.time() < 30*86400:
        print('AVISO: el certificado vence en menos de 30 dias; programe su renovacion.', flush=True)
    before = xml.read_bytes()
    updated, previous = patch_domain(before, certificate['alias'])
    shutil.copy2(xml, work/'domain-anterior.xml')
    shutil.copy2(keystore, work/'keystore-anterior.jks')
    store_hash = hashlib.sha256(keystore.read_bytes()).hexdigest()
    print('Respaldo: ' + str(work), flush=True)
    print('Activando el certificado en HTTPS y reiniciando SOLO Payara...', flush=True)
    apply_domain(s, xml, updated, work/'domain-anterior.xml', certificate['sha256'], HOST)
    if hashlib.sha256(keystore.read_bytes()).hexdigest() != store_hash:
        raise RuntimeError('El almacen cambio durante la operacion. Revisar antes de continuar.')
    report = dict(url='https://'+HOST+'/sisdoc/login.do', alias_anterior=previous,
                  certificado=certificate, local_verificado=True, dominio_verificado=False, confianza_verificada=False)
    s.save(work/'resultado.json', report)
    public_hash = probe(HOST, HOST)
    if public_hash != certificate['sha256']:
        raise RuntimeError('Payara local ya entrega INIA, pero sgd.inia.gob.pe entrega OTRO certificado. Revisar DNS, proxy o balanceador. Respaldo: ' + str(work))
    report['dominio_verificado'] = True
    s.save(work/'resultado.json', report)
    probe(HOST, HOST, trusted=True)
    report['confianza_verificada'] = True
    s.save(work/'resultado.json', report)
    print('HTTPS VERIFICADO: certificado de INIA, dominio, vigencia y confianza correctos.', flush=True)
    print('Abra una ventana nueva: ' + report['url'], flush=True)
    return 0


if __name__ == '__main__':
    try:
        sys.exit(main())
    except Exception as exc:
        print('NO SE CONFIRMO HTTPS CORRECTO: ' + str(exc), file=sys.stderr, flush=True)
        sys.exit(1)

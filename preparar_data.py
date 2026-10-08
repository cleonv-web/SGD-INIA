"""Convierte cinco CSV del origen en un borrador pendiente de revisión. Sin BD."""
from pathlib import Path
import argparse, csv, io, json, re, sys
ROOT=Path(__file__).resolve().parent
if str(ROOT) not in sys.path: sys.path.insert(0,str(ROOT))
from gestion_data import FIELDS, RESERVED, require, private_folder, private_write


def read_csv(folder,prefix,required):
    files=list(folder.glob(prefix+'*.csv'))
    require(len(files)==1,'Se necesita un solo '+prefix+'.csv (o '+prefix+'-CODIGO.csv) en '+str(folder))
    raw=files[0].read_bytes()
    try:
        source=raw.decode('utf-16' if raw.startswith((b'\xff\xfe',b'\xfe\xff')) else 'utf-8-sig')
    except UnicodeDecodeError:
        source=raw.decode('cp1252')
    # SSMS y editores pueden guardar coma, punto y coma o tabulador.
    first=source.splitlines()[0] if source.splitlines() else ''
    delimiters=[d for d in [',',';','\t'] if set(required)<=set(next(csv.reader([first],delimiter=d),[]))]
    require(len(delimiters)==1,'Encabezados ausentes o separador incorrecto en '+files[0].name+
            '. Guardar el resultado con encabezados, no sólo las filas.')
    reader=csv.DictReader(io.StringIO(source),delimiter=delimiters[0])
    require(len(set(reader.fieldnames))==len(reader.fieldnames),'Encabezados repetidos en '+files[0].name)
    require(not any(re.search('password|clave|firma',h,re.I) for h in reader.fieldnames
                    if h not in ['nFirmaDigital']), 'El CSV contiene campos privados fuera de la exportación prevista')
    rows=list(reader)
    require(all(None not in row and all(v is not None for v in row.values()) for row in rows),
            'CSV con columnas incompletas o comas sin comillas: '+files[0].name)
    return [{k:('' if v.strip().upper()=='NULL' else v.strip()) for k,v in row.items()} for row in rows]


def numeric(value,size,label):
    require(re.fullmatch(r'[1-9][0-9]*',value or '') is not None and len(value)<=size,
            label+': se requiere un ID positivo que quepa en '+str(size)+' dígitos')
    return value.zfill(size)


def csv_text(rows,fields):
    stream=io.StringIO(newline='');writer=csv.DictWriter(stream,fieldnames=fields)
    writer.writeheader();writer.writerows(rows)
    return stream.getvalue()


def prepare(source,destination):
    source=source.resolve();destination=destination.resolve()
    require(source!=destination,'Guardar la preparación en otra carpeta para conservar los originales')
    offices=read_csv(source,'oficinas',['iCodOficina','cNomOficina','cSiglaOficina','iCodUbicacion','es_area_piloto'])
    workers=read_csv(source,'trabajadores',['iCodTrabajador','cUsuario','cNombresTrabajador',
        'cApellidosTrabajador','cNumDocIdentidad','cMailTrabajador','cCargo','oficina_piloto_seleccionada'])
    locations=read_csv(source,'ubicaciones',['iCodUbicacion','cNomUbicacion'])
    profiles=read_csv(source,'perfiles',['iCodPerfil','cDescPerfil'])
    assignments=read_csv(source,'asignaciones',['iCodTrabajador','iCodOficina','iCodPerfil'])
    selected=[o for o in offices if o['es_area_piloto']=='1']
    require(len(selected)==1,'El CSV de oficinas debe tener exactamente una fila es_area_piloto=1')
    office=selected[0];source_area=office['iCodOficina']
    area_code=numeric(source_area,5,'ID de oficina')
    require(area_code not in RESERVED,'La propuesta coincide con un código reservado; requiere equivalencia manual')
    require(workers,'No hay trabajadores exportados para esta área')
    require(all(w['oficina_piloto_seleccionada']==source_area for w in workers),
            'Hay trabajadores de una exportación distinta; usar una carpeta por área')
    require(len({w['iCodTrabajador'] for w in workers})==len(workers),'Trabajadores repetidos en la exportación')
    require(all(a['iCodOficina']==source_area and a['iCodTrabajador'] in {w['iCodTrabajador'] for w in workers}
                for a in assignments),'Asignaciones de otra área o de personas no exportadas')
    require(len({p['iCodPerfil'] for p in profiles})==len(profiles),'Perfiles repetidos en la exportación')
    local=[x for x in locations if x['iCodUbicacion']==office['iCodUbicacion']]
    require(len(local)==1,'La sede del área no existe o está repetida en ubicaciones.csv')
    area=json.loads((ROOT/'bd/piloto-area.json.ejemplo').read_text(encoding='utf-8'))
    area.update(codigo=area_code,nombre=office['cNomOficina'],
                sigla=office['cSiglaOficina'],nombre_corto='',titular_empleado='',
                aprobado='NO',verificado_por='')
    # El nodo técnico de laboratorio es una propuesta explícitamente no aprobada.
    area['sede']={'codigo':numeric(local[0]['iCodUbicacion'],3,'ID de sede'),
                  'nombre':local[0]['cNomUbicacion'],'direccion':''}
    descriptions={p['iCodPerfil']:p['cDescPerfil'] for p in profiles}
    candidates=[];pending=[]
    for worker in workers:
        emp=numeric(worker['iCodTrabajador'],5,'ID de trabajador')
        require(emp not in RESERVED,'ID de trabajador coincide con un código reservado: '+worker['iCodTrabajador'])
        roles=[descriptions.get(a['iCodPerfil'],'PERFIL SIN CATALOGO '+a['iCodPerfil'])
               for a in assignments if a['iCodTrabajador']==worker['iCodTrabajador'] and a['iCodOficina']==source_area]
        candidates.append(dict(origen_id=worker['iCodTrabajador'],codigo_empleado=emp,
            usuario=worker['cUsuario'],nombres=worker['cNombresTrabajador'],
            apellido_paterno=worker['cApellidosTrabajador'],apellido_materno='',
            dni=worker['cNumDocIdentidad'],email=worker['cMailTrabajador'],
            cargo_codigo='',cargo_nombre=worker['cCargo'],perfil='',verificado='NO'))
        tasks=['Separar/confirmar apellidos paterno y materno','Asignar cargo_codigo de cuatro dígitos',
               'Elegir perfil SGD en area.json',
               'Confirmar vínculo laboral vigente y autorización; excluir cesados y cuentas genéricas antes de marcar SI']
        if not re.fullmatch('[0-9]{8}',worker['cNumDocIdentidad']) or worker['cNumDocIdentidad']=='00000000':
            tasks.insert(0,'Documento origen no es DNI válido en formato; confirmar dato real, no inventarlo')
        if not roles:tasks.append('Sin perfil asignado en origen para el área: resolver acceso')
        pending.append(dict(origen_id=worker['iCodTrabajador'],usuario=worker['cUsuario'],
            perfiles_origen=' | '.join(sorted(set(roles))),pendientes='; '.join(tasks)))
    names=['area.json','usuarios-verificados.csv','pendientes.csv','LEEME.txt']
    require(not any((destination/name).exists() for name in names),
            'La carpeta destino ya contiene una preparación. No se sobrescribe; elegir otra carpeta.')
    private_folder(destination)
    private_write(destination/'area.json',json.dumps(area,ensure_ascii=False,indent=2)+'\n')
    private_write(destination/'usuarios-verificados.csv',csv_text(candidates,FIELDS))
    private_write(destination/'pendientes.csv',csv_text(pending,['origen_id','usuario','perfiles_origen','pendientes']))
    instructions='''PREPARACIÓN PENDIENTE. NO SE CARGÓ NINGUNA BASE.
1. Abrir usuarios-verificados.csv en VS Code; no usar Excel sin importar códigos como TEXTO.
2. Los apellidos completos están conservados en apellido_paterno: separarlos/confirmarlos personalmente.
3. Confirmar DNI real, cargo_codigo (4 dígitos), cargo_nombre y perfil de cada usuario.
   Perfiles disponibles de ejemplo: USUARIO_AREA, SOPORTE_UTI, MESA_PARTES.
   Los perfiles del sistema anterior se muestran en pendientes.csv; no se tradujeron automáticamente.
4. Retirar las filas completas de cesados, cuentas genéricas y personas que no participarán.
   Estado 1 en el origen no demuestra vínculo laboral vigente. Confirmarlo personalmente.
   Marcar verificado=SI sólo para personas vigentes, identificadas y autorizadas después de revisar.
   Una fila con NO bloquea toda la carga: no significa que esa fila se omita automáticamente.
5. Revisar area.json: nombre_corto, sigla, padre y nivel, titular_empleado, sede y dirección,
   tipos documentales y permisos. Mantener el código de empleado de cada persona entre áreas.
   Los códigos propuestos se obtienen de los IDs del origen, rellenando ceros; no se comprobaron
   colisiones en PostgreSQL. El padre 00000 es provisional del laboratorio, no el organigrama real.
   Comprobar personalmente los códigos del destino con Consultar-Catalogos-Piloto-PostgreSQL.sql.
6. Indicar verificado_por y aprobado=SI únicamente después de revisar toda el área.
7. Ejecutar Cargar-Area con --carpeta para validar, sin --aplicar todavía.
Conservar los cinco CSV originales. No publicar esta carpeta en Git.
'''
    private_write(destination/'LEEME.txt',instructions)
    print('Preparación creada: '+str(destination))
    print('Usuarios pendientes: '+str(len(candidates))+'. Todos mantienen verificado=NO.')
    print('Abrir primero LEEME.txt y pendientes.csv. No se conectó a ninguna base.')


def main():
    p=argparse.ArgumentParser(description=__doc__)
    p.add_argument('--origen',type=Path,default=ROOT/'datos/exportacion-uti')
    p.add_argument('--carpeta',type=Path,default=ROOT/'datos/carga-uti-preparada')
    a=p.parse_args();prepare(a.origen,a.carpeta)


if __name__=='__main__':
    try:main()
    except (ValueError,OSError) as e:
        print('ERROR: '+str(e),file=sys.stderr);sys.exit(1)

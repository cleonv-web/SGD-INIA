"""Convierte cinco CSV del origen en un borrador pendiente de revisión. Sin BD."""
from pathlib import Path
import argparse, csv, io, json, re, sys
ROOT=Path(__file__).resolve().parent
if str(ROOT) not in sys.path: sys.path.insert(0,str(ROOT))
from gestion_data import FIELDS, RESERVED, require, private_folder, private_write

# Orden exacto de los cinco SELECT de Exportar-Area-SQLServer.sql.
SOURCE_FIELDS={
    'ubicaciones':['iCodUbicacion','cNomUbicacion','cNomDistrito','nFlagEstado'],
    'oficinas':['iCodOficina','cNomOficina','cSiglaOficina','iCodUbicacion','idPadre',
        'iFlgEstado','iCodTipoOficina','iCodCategoriaOficina','iFlgRof','esArea','es_area_piloto'],
    'trabajadores':['iCodTrabajador','iCodOficina','iCodPerfil','cNombresTrabajador',
        'cApellidosTrabajador','cTipoDocIdentidad','cNumDocIdentidad','cMailTrabajador','cCargo',
        'cUsuario','nFlgEstado','ES_EXTERNO','CODIGO_SEDE','encargado','nFirmaDigital',
        'oficina_piloto_seleccionada'],
    'perfiles':['iCodPerfil','cDescPerfil','cDescripcion'],
    'asignaciones':['iCodPerfilUsuario','iCodTrabajador','iCodOficina','iCodPerfil']
}


def read_csv(folder,prefix,required):
    files=list(folder.glob(prefix+'*.csv'))
    require(len(files)==1,'Se necesita un solo '+prefix+'.csv (o '+prefix+'-CODIGO.csv) en '+str(folder))
    raw=files[0].read_bytes()
    try:
        source=raw.decode('utf-16' if raw.startswith((b'\xff\xfe',b'\xfe\xff')) else 'utf-8-sig')
    except UnicodeDecodeError:
        source=raw.decode('cp1252')
    if not source.strip():return []
    # SSMS y editores pueden guardar coma, punto y coma o tabulador, con o sin encabezados.
    parsed=[]
    for delimiter in [',',';','\t']:
        try:
            records=[r for r in csv.reader(io.StringIO(source),delimiter=delimiter,strict=True) if r]
        except csv.Error:continue
        if records:parsed.append(records)
    headed=[r for r in parsed if set(required)<=set(h.strip() for h in r[0])]
    if headed:
        require(len(headed)==1,'Separador ambiguo en '+files[0].name)
        headers=[h.strip() for h in headed[0][0]];records=headed[0][1:]
        require(len(set(headers))==len(headers),'Encabezados repetidos en '+files[0].name)
        require(not any(re.search('password|clave|firma',h,re.I) for h in headers
                        if h!='nFirmaDigital'),'El CSV contiene campos privados fuera de la exportación prevista')
    else:
        headers=SOURCE_FIELDS[prefix]
        known={h for fields in SOURCE_FIELDS.values() for h in fields}
        require(not any(known & {h.strip() for h in r[0]} for r in parsed),
                'Encabezados incompletos o resultado incorrecto en '+files[0].name)
        candidates=[r for r in parsed if all(len(row)==len(headers) for row in r)]
        require(len(candidates)==1,files[0].name+': sin encabezados se esperan '+str(len(headers))+
                ' columnas del resultado '+str(list(SOURCE_FIELDS).index(prefix)+1)+
                ' de Exportar-Area-SQLServer.sql. Revisar el nombre del archivo y el resultado guardado.')
        records=candidates[0]
        require(all(re.fullmatch('[0-9]+',r[0].strip()) for r in records),
                'CSV sin encabezados incompatible: la primera columna debe ser el ID numérico en '+files[0].name)
    require(all(len(row)==len(headers) for row in records),
            'CSV con columnas incompletas o separadores sin comillas: '+files[0].name)
    return [{k:('' if v.strip().upper()=='NULL' else v.strip()) for k,v in zip(headers,row)} for row in records]


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
            cargo_codigo='',cargo_nombre=worker['cCargo'],perfil='',verificado='NO',incluir_sgd='1'))
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
4. En incluir_sgd poner 0 para excluir cesados, cuentas genéricas y personas que no participarán.
   Poner 1 para incluir a una persona. Las filas con 0 se omiten, pueden conservar verificado=NO.
   Estado 1 en el origen no demuestra vínculo laboral vigente. Confirmarlo personalmente.
   Marcar verificado=SI sólo para personas vigentes, identificadas y autorizadas después de revisar.
   Una fila incluida (1) con verificado=NO bloquea la carga hasta completar su revisión.
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

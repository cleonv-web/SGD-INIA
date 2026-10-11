"""Reinicio explicito del piloto local, invocado por Limpiar-BD --todo.

No ejecuta nada al importar. Nunca usa el respaldo portable para reinstalar.
"""
import json
import hashlib
import os
import re
import secrets
from datetime import datetime, timezone

TOKEN = 'REINICIAR:idosgd_inia'


def huella_archivo(path):
    digest = hashlib.sha256()
    with path.open('rb') as source:
        for block in iter(lambda: source.read(1024 * 1024), b''):
            digest.update(block)
    return digest.hexdigest()


def archivos_documentales(documentos):
    archivos = []
    def error_lectura(exc):
        raise exc
    for carpeta, directorios, nombres in os.walk(documentos, followlinks=False, onerror=error_lectura):
        for nombre in directorios + nombres:
            path = documentos / (os.path.relpath(carpeta, documentos)) / nombre
            if path.is_symlink() or path.resolve() != path.absolute():
                raise RuntimeError('Enlace fuera de la ruta documental; no continuar: ' + str(path))
        for nombre in nombres:
            path = documentos / (os.path.relpath(carpeta, documentos)) / nombre
            if not path.is_file():
                raise RuntimeError('Archivo documental no regular; no continuar: ' + str(path))
            archivos.append(path)
    return sorted(archivos)


def archivar_documentos(documentos, anteriores, datos):
    """Apartar archivos sin renombrar carpetas que el cliente puede mantener abiertas."""
    datos = datos.resolve()
    if (documentos != datos / 'documentos' or documentos.resolve() != documentos
            or not anteriores.resolve().is_relative_to(datos)
            or anteriores.resolve() != anteriores or anteriores.is_relative_to(documentos)
            or anteriores.exists()):
        raise RuntimeError('Rutas de archivo documental incompatibles; no continuar.')
    documentos.mkdir(exist_ok=True)
    archivos = archivos_documentales(documentos)
    anteriores.mkdir()
    movidos = []
    manifiesto = []
    try:
        for source in archivos:
            relative = source.relative_to(documentos)
            target = anteriores / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            digest = huella_archivo(source)
            source.rename(target)
            movidos.append((source, target))
            if huella_archivo(target) != digest:
                raise RuntimeError('El archivo cambio durante el respaldo: ' + str(relative))
            manifiesto.append({'archivo': relative.as_posix(), 'sha256': digest})
        if archivos_documentales(documentos):
            raise RuntimeError('Aparecieron archivos durante la limpieza; no borrar la base.')
    except Exception as exc:
        pendientes = []
        for source, target in reversed(movidos):
            try:
                if source.exists():
                    raise RuntimeError('La ruta original esta ocupada')
                target.rename(source)
            except Exception:
                pendientes.append(str(target))
        detalle = (' Archivos pendientes de recuperar: ' + ', '.join(pendientes)) if pendientes else ''
        raise RuntimeError('No se pudieron archivar los documentos. No se borro la base. '
                           'Cerrar Word y Tramitedoc y revisar el error: ' + str(exc) + detalle) from exc
    return manifiesto


def preparar_sql(sql, rol_existente):
    if rol_existente:
        sql, cantidad = re.subn(
            r'(?m)^CREATE ROLE sgd_app LOGIN PASSWORD [^\r\n]+;\r?\n',
            '', sql, count=1)
        if cantidad != 1:
            raise RuntimeError('No se pudo conservar el rol existente. No se borro la base.')
    return sql


def reiniciar(*, aplicar=False, confirmar='', _sgd=None):
    if not aplicar:
        print('PLAN OFFLINE: reiniciar idosgd_inia; eliminar documentos y usuarios del piloto.')
        print('Conservar respaldo completo y archivos anteriores. Reinstalar catalogos y cuatro cuentas de laboratorio.')
        print('Sin conexion ni cambios. Para aplicar: --todo --aplicar --confirmar '+TOKEN)
        return
    if confirmar != TOKEN:
        raise ValueError('Se requiere --confirmar '+TOKEN)
    if _sgd is None:
        import sgd as _sgd
    import gestion_data as g
    s = _sgd
    destino = (s.C['modo'], s.C['base'], s.C['host_bd'], int(s.C['puerto_bd']))
    if destino != ('completo', 'idosgd_inia', '127.0.0.1', 55432):
        raise RuntimeError('Reinicio permitido solo para idosgd_inia local, puerto 55432, modo completo.')
    raiz = s.ROOT.resolve()
    datos = s.DATA.resolve()
    documentos = datos/'documentos'
    if datos != raiz/'datos' or documentos.resolve() != documentos:
        raise RuntimeError('Las rutas de datos deben pertenecer directamente a esta instalacion.')
    respaldo = datos/('reinicio-piloto-'+datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%S')+'-'+secrets.token_hex(3))
    anteriores = respaldo/'documentos-anteriores'
    for ruta in (respaldo.resolve(), anteriores.resolve()):
        if not ruta.is_relative_to(datos):
            raise RuntimeError('Ruta de respaldo fuera de datos; no continuar.')
    print('Deteniendo Payara y preparando el respaldo...', flush=True)
    s.detener()
    if re.search(r'(?mi)^\s*sgd\s+running\b', s.admin(['list-domains']).stdout):
        raise RuntimeError('Payara sigue ejecutandose. No se borro la base.')
    s.base()
    if s.scalar("SELECT to_regclass('idosgd.instalacion_portable') IS NOT NULL") != 't':
        raise RuntimeError('No se reconoce la instalacion del piloto. No se borro la base.')
    sql = preparar_sql(s.generar_sql(), s.scalar(
        "SELECT EXISTS(SELECT 1 FROM pg_roles WHERE rolname='sgd_app')") == 't')
    g.private_folder(respaldo)
    g.private_write(respaldo/'reinstalacion-PRIVADA.sql', sql)
    dump = g.backup(respaldo)
    s.pg_tool('pg_restore', ['--list', dump])
    print('Respaldo guardado: '+str(dump), flush=True)
    g.private_write(respaldo/'resultado.json', json.dumps({'estado':'RESPALDADO', 'base':'idosgd_inia'}))
    print('Archivando archivos; se conservan las carpetas documentales...', flush=True)
    try:
        manifiesto = archivar_documentos(documentos, anteriores, datos)
    except Exception as exc:
        g.private_write(respaldo/'resultado.json', json.dumps(
            {'estado':'ERROR_AL_ARCHIVAR_DOCUMENTOS', 'base':'idosgd_inia', 'error':str(exc)},
            ensure_ascii=False, indent=2))
        raise
    g.private_write(respaldo/'archivos-documentales.json', json.dumps(manifiesto, ensure_ascii=False, indent=2))
    g.private_write(respaldo/'resultado.json', json.dumps(
        {'estado':'DOCUMENTOS_ARCHIVADOS', 'base':'idosgd_inia', 'archivos':len(manifiesto)}))
    print('Archivos apartados y verificados: ' + str(len(manifiesto)), flush=True)
    s.psql('DROP DATABASE "idosgd_inia" WITH (FORCE);', db='postgres', log='bd-reinicio-eliminar.log')
    s.base()
    s.psql(sql, transaction=True, log='bd-reinicio-instalar.log')
    if s.scalar('SELECT count(*) FROM idosgd.tdtv_remitos') != '0':
        raise RuntimeError('La base inicial contiene documentos. Revisar antes de cargar usuarios.')
    g.private_write(respaldo/'resultado.json', json.dumps(
        {'estado':'REINICIADO', 'base':'idosgd_inia', 'archivos':len(manifiesto), 'documentos':0}))
    print('Base nueva sin documentos ni usuarios reales. Se recrearon cuatro cuentas de laboratorio.')
    print('Archivos anteriores: '+str(anteriores))
    print('Cargar las areas con Cargar-Area.bat y despues ejecutar SGD.bat iniciar.')

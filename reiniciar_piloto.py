"""Reinicio explicito del piloto local, invocado por Limpiar-BD --todo.

No ejecuta nada al importar. Nunca usa el respaldo portable para reinstalar.
"""
import json
import re
import secrets
from datetime import datetime, timezone

TOKEN = 'REINICIAR:idosgd_inia'


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
    if documentos.exists():
        documentos.rename(anteriores)
    documentos.mkdir(exist_ok=True)
    s.psql('DROP DATABASE "idosgd_inia" WITH (FORCE);', db='postgres', log='bd-reinicio-eliminar.log')
    s.base()
    s.psql(sql, transaction=True, log='bd-reinicio-instalar.log')
    if s.scalar('SELECT count(*) FROM idosgd.tdtv_remitos') != '0':
        raise RuntimeError('La base inicial contiene documentos. Revisar antes de cargar usuarios.')
    g.private_write(respaldo/'resultado.json', json.dumps({'estado':'REINICIADO', 'base':'idosgd_inia'}))
    print('Base nueva sin documentos ni usuarios reales. Se recrearon cuatro cuentas de laboratorio.')
    print('Archivos anteriores: '+str(anteriores))
    print('Cargar las areas con Cargar-Area.bat y despues ejecutar SGD.bat iniciar.')

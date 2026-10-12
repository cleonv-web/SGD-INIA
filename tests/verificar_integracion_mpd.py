"""DAO real en copia temporal: no altera datos/contadores del laboratorio ni envía correo."""
import os
import secrets
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd


def verificar():
    nombre = 'mpd_prueba_' + secrets.token_hex(6)
    dump = sgd.LOG / (nombre + '.dump')
    sgd.pg_tool('pg_dump', [*sgd.connection(), '-Fc', '-f', dump])
    creada = False
    try:
        sgd.psql('CREATE DATABASE ' + nombre, db='postgres')
        creada = True
        sgd.pg_tool('pg_restore', [*sgd.connection(nombre), '--exit-on-error', dump])
        fuente = sgd.ROOT / 'fuentes/MPVCiudadano'
        clases = [fuente / 'logic/target/classes', fuente / 'web/target/mpvdoc-web-1.0/WEB-INF/lib/*', sgd.ROOT / 'payara/driver/postgresql-42.7.8.jar']
        cp = os.pathsep.join(map(str, clases))
        output = sgd.LOG / 'clases-integracion-mpd'; output.mkdir(exist_ok=True)
        extension = '.exe' if os.name == 'nt' else ''
        sgd.run([sgd.java_home() / ('bin/javac' + extension), '-encoding', 'UTF-8', '-cp', cp, '-d', output, sgd.ROOT / 'tests/MpdIntegracion.java'])
        env = sgd.java_env(); env['MPD_TEST_DB_PASSWORD'] = sgd.credentials()['aplicacion']
        result = sgd.run([sgd.java_home() / ('bin/java' + extension), '-cp', str(output)+os.pathsep+cp, 'MpdIntegracion',
                          'jdbc:postgresql://%s:%s/%s' % (sgd.C['host_bd'], sgd.C['puerto_bd'], nombre)],
                         env=env, log='pruebas-integracion-mpd.log')
        print(result.stdout)
    finally:
        if creada:
            sgd.psql('DROP DATABASE ' + nombre + ' WITH (FORCE)', db='postgres')
        dump.unlink(missing_ok=True)

if __name__ == '__main__':
    verificar()

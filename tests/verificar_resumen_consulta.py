"""Ejecuta el DAO real y genera una vista aislada de revisión con el expediente ficticio del piloto."""
import os
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd

def verificar(output=None):
    output = Path(output) if output else sgd.LOG / 'consulta-resumen-2026-10-11/revision'
    clases = sgd.LOG / 'consulta-resumen-2026-10-11/clases'
    clases.mkdir(parents=True, exist_ok=True)
    proyecto = sgd.ROOT / 'fuentes/ConsultaTuTramite'
    cp = os.pathsep.join([str(proyecto / 'logic/target/classes'),
                         str(proyecto / 'web/target/consultadoc-web-1.0/WEB-INF/lib/*'),
                         str(sgd.ROOT / 'payara/driver/postgresql-42.7.8.jar'),
                         str(sgd.ROOT / 'payara/payara5/glassfish/modules/jakarta.el.jar'), str(clases)])
    java = sgd.java_home() / 'bin'
    sgd.run([java / ('javac.exe' if os.name == 'nt' else 'javac'), '-encoding', 'UTF-8',
             '-cp', cp, '-d', clases, sgd.ROOT / 'tests/VerificarResumenConsulta.java'], env=sgd.java_env())
    env = sgd.java_env();env['PGPASSWORD'] = sgd.credentials()['postgres']
    jdbc = f"jdbc:postgresql://{sgd.C['host_bd']}:{sgd.C['puerto_bd']}/{sgd.C['base']}"
    print(sgd.run([java / ('java.exe' if os.name == 'nt' else 'java'), '-cp', cp,
                   'VerificarResumenConsulta', sgd.ROOT, jdbc, output], env=env).stdout)

if __name__ == '__main__':
    verificar(sys.argv[1] if len(sys.argv) > 1 else None)

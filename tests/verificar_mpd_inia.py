"""Pruebas MPD offline con JUnit 4.11 incluido; servicios de registro simulados."""
import os
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd


def verificar():
    fuente = sgd.ROOT / 'fuentes/MPVCiudadano'
    sgd.maven(['install', '-DskipTests'], fuente, 'compilar-pruebas-mpd.log')
    repo = sgd.ROOT / 'herramientas/maven-repo'
    clases = [fuente / 'logic/target/classes', fuente / 'logic/target/test-classes',
              fuente / 'web/target/mpvdoc-web-1.0/WEB-INF/lib/*',
              repo / 'junit/junit/4.11/junit-4.11.jar',
              repo / 'org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar',
              repo / 'org/springframework/spring-test/3.2.6.RELEASE/spring-test-3.2.6.RELEASE.jar',
              repo / 'javax/servlet/servlet-api/2.5/servlet-api-2.5.jar']
    java = sgd.java_home() / 'bin' / ('java.exe' if os.name == 'nt' else 'java')
    resultado = sgd.run([java, '-Djava.awt.headless=true', '-cp', os.pathsep.join(map(str, clases)),
                        'org.junit.runner.JUnitCore', 'pe.gob.onpe.mpvdoc.service.PortalIniaTest'],
                       log='pruebas-java-mpd-inia.log')
    print(resultado.stdout)


if __name__ == '__main__':
    verificar()

"""Recompila el cargo ciudadano para no empaquetar una plantilla Jasper antigua."""
import os
import sgd


def compilar_cargo_mpd(muestras=None):
    fuente = sgd.ROOT / 'fuentes/MPVCiudadano'
    clases = sgd.LOG / 'cargo-mpd-qr/clases'
    clases.mkdir(parents=True, exist_ok=True)
    cp = os.pathsep.join(map(str, [fuente / 'logic/target/classes',
        fuente / 'web/target/mpvdoc-web-1.0/WEB-INF/lib/*',
        sgd.ROOT / 'herramientas/maven-repo/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar', clases]))
    java = sgd.java_home() / 'bin'
    sgd.run([java / ('javac.exe' if os.name == 'nt' else 'javac'), '-encoding', 'UTF-8',
             '-cp', cp, '-d', clases, sgd.ROOT / 'herramientas/CompilarCargoMpd.java'],
            env=sgd.java_env(), log='compilar-utilidad-cargo-mpd.log')
    args = [java / ('java.exe' if os.name == 'nt' else 'java'), '-Djava.awt.headless=true',
            '-cp', cp, 'CompilarCargoMpd', sgd.ROOT]
    if muestras is not None:
        args.append(muestras)
    sgd.run(args, env=sgd.java_env(), log='compilar-plantilla-cargo-mpd.log')

# Versionado ligero del SGD

Git guarda código e instrucciones para reconstruir la aplicación. El paquete portable guarda el entorno completo; los respaldos guardan la información de la instalación. Se necesitan los tres para recuperar código, entorno e información.

## Qué conservar en Git

| Elemento | Motivo |
|---|---|
| fuentes, incluidos pom.xml, recursos web y JAR de proveedor requeridos | Código editable y entradas necesarias para compilar. Se excluye target. |
| SQL de bd y futuras migraciones | Esquema, catálogos, ficticios y cambios controlados de estructura. |
| sgd.py y lanzadores BAT, PS1 y SH | Instalación, compilación, despliegue, comprobaciones y respaldo. |
| configuracion.ejemplo.json | Modelo sin contraseñas; la configuración de cada servidor permanece local. |
| payara/plantillas y payara/driver | Configuración reproducible y driver JDBC fijado. El dominio activo se genera y queda fuera. |
| herramientas/*.java, maven-settings.xml y dependencias | Utilidades y bibliotecas especiales necesarias para compilar. |
| binarios/proveedor/wstradoc.war | Única aplicación sin código fuente, aproximadamente 129 KB. |
| .vscode, .gitignore, README y manual | Instrucciones, tareas y manifiesto de versiones/hashes. |

No excluir todos los JAR: hay bibliotecas del proveedor que no se pueden recuperar desde Maven Central. Son entradas necesarias del proyecto. Los WAR de las ocho aplicaciones con fuentes son salidas y quedan fuera de Git.

## Qué queda fuera de Git

Java, Payara extraído y ZIP de respaldo, motor PostgreSQL y archivos comprimidos del motor, Maven, repositorio Maven offline, Python, WAR generados, otros WAR históricos del proveedor, target, logs y datos.

datos incluye credenciales, contraseñas, SQL privado, base física, documentos y dumps. Todo permanece local. configuracion.json también queda local: para una copia nueva del repositorio se crea a partir de configuracion.ejemplo.json y se adapta al ambiente.

## Cómo reconstruir el entorno

Un clon de Git por sí solo no contiene los motores ni el repositorio Maven offline. Conservar el ZIP portable completo y su archivo SHA256 fuera de Git, en almacenamiento institucional de artefactos o respaldos. Guardar su versión, procedencia y hashes, disponibles en manual/PROCEDENCIA-COMPONENTES.json.

Para otro equipo, extraer el paquete portable correspondiente y aplicar el código del commit o etiqueta elegido, manteniendo la configuración local. Una recompilación utiliza las fuentes y genera los WAR. Evitar mezclar versiones de las fuentes, scripts y dependencias especiales de distintos commits.

El ZIP inicial entregado sigue siendo el paquete probado de instalación; estas reglas de Git y la guía pertenecen al trabajo de desarrollo posterior. El repositorio y el ZIP deben identificarse por sus propias versiones.

## Registrar cambios de aplicación y base

Guardar un primer commit antes de modificar el código. Para cada entrega aprobada, usar una etiqueta que identifique la versión, por ejemplo sgd-inia-1.0.0. Registrar el commit, hashes de WAR y resultados de pruebas junto a los artefactos de esa entrega. Los manifiestos de compilación en logs se conservan como evidencias de entrega fuera de Git.

Los cambios posteriores de base deben ser nuevas migraciones numeradas, por ejemplo bd/migraciones/001-agregar-campo.sql. Mantener documentado el orden, la versión aplicada y la validación. No volver a ejecutar el SQL inicial completo sobre una base con datos; no se emplea ese archivo para actualizar producción.

Un cambio de esquema puede hacer que un código anterior ya no sea compatible. Revertir código requiere comprobar la compatibilidad con la base y su migración correspondiente.

## Respaldos separados

Git no conserva documentos ni restaura la base PostgreSQL. Conservar por separado el dump, documentos y credenciales de cada instalación. Mantener respaldo del repositorio, del paquete portable/artefactos y de los datos; verificar que las restauraciones funcionen.

Antes de publicar el repositorio, revisar las configuraciones heredadas del proveedor para retirar claves o credenciales reales. Usar un repositorio privado institucional mientras se realiza esa revisión. .gitignore no borra secretos que ya estén en archivos de fuentes ni quita archivos previamente registrados en Git.

Estas reglas no eliminan archivos de la carpeta ni modifican el código ejecutado por el SGD. Todavía no se ha inicializado un repositorio, creado commits ni configurado un remoto.

Referencias: https://git-scm.com/docs/gitignore y https://docs.github.com/en/repositories/working-with-files/managing-large-files/about-large-files-on-github

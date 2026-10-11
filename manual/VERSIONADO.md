# Versionado del SGD INIA y su motor Payara

El repositorio privado incluye los componentes necesarios para clonar y ejecutar Instalar-SGD sin copiar otro portable: código, Payara, Java, PostgreSQL, Python, Maven, dependencias offline, configuración de ejemplo, cargas y correcciones. Los respaldos se conservan aparte para recuperar la información operativa de una instalación existente.

## Qué conservar en Git

| Elemento | Motivo |
|---|---|
| fuentes, incluidos pom.xml, recursos web y JAR de proveedor requeridos | Código editable y entradas necesarias para compilar. Se excluye target. |
| SQL de bd y futuras migraciones | Esquema, catálogos, ficticios y cambios controlados de estructura. |
| sgd.py y lanzadores BAT, PS1 y SH | Instalación, compilación, despliegue, comprobaciones y respaldo. |
| configuracion.ejemplo.json | Modelo sin contraseñas; la configuración de cada servidor permanece local. |
| payara/plantillas y payara/driver | Configuración reproducible y driver JDBC fijado. El dominio activo se genera y queda fuera. |
| payara/payara5 | Motor 5.2022.5 con sus bibliotecas, scripts, licencias y WAR internos; excluye dominios activos y material auxiliar del proveedor. |
| payara/configuracion | Copia aprobada del dominio con propiedades, certificados y hashes. |
| datos/carga-*, datos/cargas y datos/exportacion-* | CSV, JSON y archivos de revisión de oficinas/usuarios, incluidos pendientes, para otras instalaciones. |
| INCIDENCIAS | Scripts de revisión, corrección y reversión, instrucciones, paquetes y evidencias. No se ejecutan automáticamente al instalar. |
| cliente-windows | Instaladores Tramitedoc y Firma ONPE y su procedencia. |
| herramientas/*.java, maven-settings.xml y dependencias | Utilidades y bibliotecas especiales necesarias para compilar. |
| java/windows y java/linux-x64.tar.gz | JDK 8 portable de Windows y Linux. |
| bd/postgresql/windows y bd/postgresql-linux-x64.tar.gz | Motor PostgreSQL portable; no es la base física de datos/postgresql. |
| herramientas/python-windows y herramientas/python-linux.tar.gz | Python incluido para ejecutar los lanzadores sin instalarlo globalmente. |
| herramientas/maven y herramientas/maven-repo | Maven y dependencias fijadas para compilar offline. Se excluyen locks, descargas incompletas y WAR propios. |
| binarios/proveedor/wstradoc.war | Única aplicación sin código fuente, aproximadamente 129 KB. |
| .vscode, .gitignore, README y manual | Instrucciones, tareas y manifiesto de versiones/hashes. |

No excluir todos los JAR: hay bibliotecas del proveedor que no se pueden recuperar desde Maven Central. Son entradas necesarias del proyecto. Los WAR de las ocho aplicaciones con fuentes son salidas y quedan fuera de Git.

## Qué queda fuera de Git

ZIP duplicado de Payara, ZIP de respaldo PostgreSQL Windows, fuente comprimida PostgreSQL, ZIP duplicado Python Windows, carpetas extraídas Linux (se distribuyen sus archivos comprimidos), WAR generados, otros WAR históricos del proveedor, target, logs, output, node_modules y actividad local de Payara. Los motores y dependencias necesarios sí se incluyen.

De datos se excluyen credenciales, contraseñas generadas, SQL privado, base física, documentos emitidos y dumps; se incluyen las carpetas de carga y exportación indicadas. configuracion.json queda local: para una copia nueva se crea a partir de configuracion.ejemplo.json y se adapta al ambiente. El ejemplo fija heap_payara_mb=4096 para nuevas instalaciones.

No se ignoran globalmente PDF o Word: los manuales, plantillas y ejemplos de fuentes deben viajar. Los documentos emitidos se filtran por datos/documentos; las copias de trabajo del dominio activo y output también quedan fuera. Las claves y dumps se excluyen incluso si aparecen dentro de una carpeta de carga.

Los CSV de carga/exportación conservan sus bytes y sus saltos CRLF mediante .gitattributes; git diff --cached --check admite ese salto Windows y continúa comprobando los espacios finales reales. Los ejemplos del JDK bajo java/windows/*/sample se ignoran: no intervienen en la compilación ni instalación del SGD. Estas reglas no eliminan los archivos del disco.

## WAR incluidos por nombre

| Ruta | Motivo |
|---|---|
| binarios/proveedor/wstradoc.war | Servicio SGD sin fuentes disponibles. |
| payara/payara5/glassfish/lib/install/applications/ejb-timer-service-app.war | Servicio interno del motor. |
| payara/payara5/glassfish/lib/install/applications/metro/wstx-services.war | Servicio interno de transacciones web. |
| payara/payara5/mq/lib/imqums.war | Componente Message Queue del proveedor. |
| payara/payara5/mq/lib/imqhttp.war | Componente Message Queue del proveedor. |
| payara/payara5/mq/lib/imqhttps.war | Componente Message Queue del proveedor. |

No se suben sgd.war, consulta.war, verifica.war, mpd.war, notificador.war, wsiotramite.war, mpvio.war ni recursos.war: SGD compilar los reconstruye. Se excluyen sus salidas en binarios y target, sus versiones antiguas en binarios/proveedor y las copias desplegadas. La regla *.war admite exclusivamente las seis rutas anteriores; también excluye versiones con otros nombres o sufijos.

## Cómo reconstruir el entorno

Después de publicar estos cambios, clonar la rama base-sgd-inia y ejecutar Instalar-SGD.bat servidor en Windows, o bash Instalar-SGD.sh servidor en Linux. No copiar runtimes adicionales. La procedencia y hashes de los componentes están en manual/PROCEDENCIA-COMPONENTES.json. Linux requiere las bibliotecas del sistema documentadas en INSTALACION.md; el cliente documental requiere Word en el puesto Windows.

Si falta configuracion.json, el instalador lo crea desde configuracion.ejemplo.json. Para ajustar URL o puertos antes de instalar se puede copiar y editar el ejemplo previamente. La instalación compila los ocho WAR desde fuentes, crea el dominio y genera claves nuevas. No reemplaza una configuración local existente.

La base nueva se construye con los SQL versionados. Después se cargan las oficinas con Cargar-Area y sus archivos revisados. No se restauran los documentos o usuarios operativos de otra instalación mediante git clone. En instalaciones existentes, incorporar el commit y usar Actualizar-SGD; conservar datos y configuración local. La selección con .gitignore no elimina archivos del disco.

## Registrar cambios de aplicación y base

Guardar un primer commit antes de modificar el código. Para cada entrega aprobada, usar una etiqueta que identifique la versión, por ejemplo sgd-inia-1.0.0. Registrar el commit, hashes de WAR y resultados de pruebas junto a los artefactos de esa entrega. Los manifiestos de compilación en logs se conservan como evidencias de entrega fuera de Git.

Los cambios posteriores de base deben ser nuevas migraciones numeradas, por ejemplo bd/migraciones/001-agregar-campo.sql. Mantener documentado el orden, la versión aplicada y la validación. No volver a ejecutar el SQL inicial completo sobre una base con datos; no se emplea ese archivo para actualizar producción.

Un cambio de esquema puede hacer que un código anterior ya no sea compatible. Revertir código requiere comprobar la compatibilidad con la base y su migración correspondiente.

## Respaldos separados

Git no conserva documentos ni restaura la base PostgreSQL. Conservar por separado el dump, documentos y credenciales de cada instalación. Mantener respaldo del repositorio, del paquete portable/artefactos y de los datos; verificar que las restauraciones funcionen.

Antes de publicar el repositorio, revisar las configuraciones heredadas del proveedor para retirar claves o credenciales reales. Usar un repositorio privado institucional mientras se realiza esa revisión. .gitignore no borra secretos que ya estén en archivos de fuentes ni quita archivos previamente registrados en Git.

Estas reglas filtran la subida y no eliminan archivos del disco. Limpiar-Payara.bat --aplicar es la operación separada que retira cachés, generados, temporales y logs del servidor; conserva aplicaciones y configuración. Los comandos del reinicio completo de la base están en CARGA-POR-AREA.md y no deben confundirse con esa limpieza.

## Preparar los cambios para subir

Usar siempre git add . y revisar el índice antes del commit:

```powershell
git status --short
git add .
if ($LASTEXITCODE -ne 0) { throw 'Falló git add.' }
git diff --cached --check
if ($LASTEXITCODE -ne 0) { throw 'Revisar errores antes de continuar.' }
git diff --cached --stat
```

Las reglas no retiran archivos ya registrados. Si apareciera un documento operativo previamente versionado, retirar solo ese archivo del índice con git rm --cached -- RUTA, conservándolo en disco. Revisar antes de confirmar y publicar. Los archivos de incidencias y cargas deben permanecer incluidos.

Referencias: https://git-scm.com/docs/gitignore y https://docs.github.com/en/repositories/working-with-files/managing-large-files/about-large-files-on-github

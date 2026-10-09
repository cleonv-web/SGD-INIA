# SGD INIA · PostgreSQL portable

Leer manual/Manual-Instalacion-SGD-INIA-PG-Actualizado.docx, edición vigente con el validador, comandos de UTI 00217 y preparación de los puestos Tramitedoc. Para otro equipo, extraer SGD-INIA-INSTALL_PG-PORTABLE.zip y ejecutar **Instalar-SGD.bat** en Windows/Windows Server o **bash Instalar-SGD.sh** en Linux x64. El instalador pregunta si este equipo será servidor, servidor con puesto Windows o solo puesto Windows. Linux prepara el servidor; los puestos Windows se preparan por separado. **No requiere Docker en los destinos.**

PostgreSQL 17.11 nativo, Java 8, Payara 5, Python, Maven, dependencias offline, código fuente y WAR están incluidos. Windows x64 y Linux x64 con glibc >=2.31; Linux necesita Bash, tar y bibliotecas de fuentes/JDK indicadas en el manual. Usar una cuenta normal en Linux. No instalar la base activa en OneDrive ni en una carpeta sincronizada.

## Comandos

| Operación | Windows | Linux |
|---|---|---|
| Instalar y probar | Instalar-SGD.bat | bash Instalar-SGD.sh |
| Instalar solo el servidor | Instalar-SGD.bat servidor | bash Instalar-SGD.sh servidor |
| Preparar solo la PC del navegador | Cliente-SGD.bat instalar | El cliente incluido requiere Windows. |
| Comprobar requisitos del puesto | Cliente-SGD.bat verificar | Cliente-SGD.sh informa la limitación del cliente Windows. |
| Arrancar | Iniciar-SGD.bat | bash Iniciar-SGD.sh |
| Detener | Detener-SGD.bat | bash Detener-SGD.sh |
| Actualizar cambios y verificar | Actualizar-SGD.bat | bash Actualizar-SGD.sh |
| Compilar | SGD.bat compilar | bash SGD.sh compilar |
| Desplegar | SGD.bat desplegar | bash SGD.sh desplegar |
| Verificar | SGD.bat verificar | bash SGD.sh verificar |
| Validar archivos de una carga | Validar-Carga.bat | bash Validar-Carga.sh |
| Respaldar | SGD.bat respaldar | bash SGD.sh respaldar |
| Empaquetar sin datos ni claves | SGD.bat empaquetar | bash SGD.sh empaquetar |

PowerShell: powershell -NoProfile -ExecutionPolicy Bypass -File .\SGD.ps1 instalar -Equipo servidor. Los lanzadores BAT de instalación, inicio y parada esperan una tecla al terminar. Después de reiniciar el equipo arrancar con Iniciar-SGD.

Después de modificar fuentes, ejecutar Actualizar-SGD.bat (Windows) o bash Actualizar-SGD.sh (Linux). Compilar por sí solo genera los WAR; también es necesario desplegar para ver los cambios. Al terminar, abrir el SGD y recargar con Ctrl+F5.

**Generar Doc requiere el cliente Tramitedoc en la PC que utiliza el navegador.** El portable incluye `cliente-windows/InstallerTramiteDoc.msi` original, versión 1.0.4.0. Tramitedoc conecta el navegador con los archivos de la PC y Word para generar, abrir y cargar documentos. `Instalar-SGD.bat completo` prepara servidor y puesto en la misma PC; `Instalar-SGD.bat puesto` o `Cliente-SGD.bat instalar` prepara solamente el puesto. Si el protocolo ya es válido, conserva la instalación. Comprueba SHA256, registro con ejecutable existente y presencia de Word; no instala Office ni declara probado el ciclo documental. `wstradoc.war` es el puente del servidor. Procedimiento y pruebas en [INSTALACION.md](manual/INSTALACION.md#cliente-tramitedoc-y-generar-doc). El MSI externo queda fuera de Git; su origen y SHA256 se conservan en `cliente-windows/PROCEDENCIA.json`.

Editar configuracion.json antes de instalar. URL inicial: http://127.0.0.1:8085/sisdoc/login.do. Usuarios ficticios: admin, mesa_demo, area_demo y jefe_demo. Claves por instalación en datos/ACCESOS-LABORATORIO.txt. Puertos: BD 55432, HTTP 8085, HTTPS 8185, administración 4855; auxiliares Payara HTTP+1000 hasta HTTP+1004.

La instalación nueva crea la sede central 001 con sus datos reales. En instalaciones anteriores, la carga por área actualiza automáticamente sólo la sede provisional reconocida (INSTALACION LOCAL INIA / PENDIENTE), usando los datos aprobados del área y la misma transacción. No requiere un SQL adicional; otras sedes existentes se conservan y los conflictos se rechazan.

## Carpetas

| Carpeta | Contenido |
|---|---|
| fuentes | Código editable de ocho aplicaciones y librerías propias. |
| java | Temurin 8u504 Windows y Linux. |
| payara | Servidor normal configurado, ZIP de respaldo, propiedades y JDBC. |
| herramientas | Maven, repositorio offline, Python portable y utilidades. |
| bd | Motor PostgreSQL nativo Windows/Linux, tres SQL y originales. |
| binarios | WAR compilados y reserva del proveedor. |
| plantillas-docx | INFORME 003 y OFICIO 001 adaptados al modelo INIA, con manifiesto SHA256. |
| cliente-windows | MSI original Tramitedoc y su procedencia. |
| datos | Base, documentos, credenciales y respaldos; privado. |
| logs | Resultados y manifiesto de compilación. |
| manual | Word, instrucciones y evidencias de pruebas. |

## SQL y fuentes

Una transacción reúne esquema, catálogos, ficticios y permisos. ON_ERROR_STOP detiene errores y revierte la carga. No se repite una base inicializada. No ejecutar SQL Server ni el DDL MPV duplicado. sgd_app no es superusuario. Ante un fallo revisar logs/ULTIMO-ERROR.txt y el log indicado.

La compilación clean install -DskipTests es offline. Los WAR se generan desde fuentes; no se ocultan errores con WAR antiguos. La única aplicación sin fuente es wstradoc, que utiliza el WAR suministrado. También existen dependencias JAR sin fuente: srvccuo, srvciopidetramite, srvcsunat y wsoapIopCliente. Ver logs/compilacion.json.

## Portabilidad y pruebas

El ZIP limpio no contiene la base ni claves de esta PC. Para trasladar datos usar respaldar y conservar credenciales.json, respaldo-portable.dump y documentos; seguir la secuencia del Word. No copiar el directorio físico PostgreSQL entre sistemas. Payara usa rutas relativas al dominio y una clave JDBC de entorno.

Windows nativo y Debian 12 nativo: ocho compilaciones, base nueva, JDBC, cuatro páginas, 69 recursos, cuatro logins y WebSocket verificados. La firma, PIDE, correo y ciclo documental completo requieren aceptación institucional; integraciones quedan inactivas inicialmente. Docker solo se utilizó en el laboratorio de esta PC.

Referencias: [binarios PostgreSQL Windows](https://www.enterprisedb.com/download-postgresql-binaries), [fuente PostgreSQL 17.11](https://ftp.postgresql.org/pub/source/v17.11/), [initdb](https://www.postgresql.org/docs/17/app-initdb.html), [pg_ctl](https://www.postgresql.org/docs/17/app-pg-ctl.html), [pg_dump](https://www.postgresql.org/docs/17/app-pgdump.html).

## Carga de datos por área

Antes de cargar, ejecutar **Validar-Carga.bat** en Windows (admite doble clic) o **bash Validar-Carga.sh** en Linux. Sin argumentos revisa datos/carga-uti-preparada; para otra carpeta usar --carpeta RUTA. Comprueba la selección 0/1, las reglas del cargador, las relaciones del área, columnas y claves del modelo local, configuración y archivos necesarios. El resultado queda en datos/validacion-carga/resultado.json, sin publicar identidades ni contraseñas. Sólo continuar si indica VALIDACION CORRECTA. No conecta a ninguna base ni garantiza ausencia de colisiones actuales: esas comprobaciones y el respaldo se realizan al aplicar la carga. La validación usa exactamente las mismas reglas que Cargar-Area; no sustituye la revisión humana.

Exportar-Area-SQLServer.sql sólo requiere elegir @Oficina; en el CSV preparado incluir_sgd=1 incluye a una persona e incluir_sgd=0 la omite; estado 1 no demuestra vínculo laboral vigente. Preparar-Carga.bat y Preparar-Carga.sh convierten sus cinco CSV, con o sin encabezados, a un borrador pendiente de revisión, sin consultar bases. Cargar-Area.bat y Cargar-Area.sh validan el CSV de usuarios revisados por el operador; --aplicar respalda y carga una sola área en PostgreSQL. Limpiar-BD.bat y Limpiar-BD.sh retiran exclusivamente un área rastreada por el utilitario, conservando catálogos y objetos compartidos. Por defecto ambos generan planes offline. Instrucciones y comandos completos en manual/CARGA-POR-AREA.md y secciones 10 a 13 de manual/Manual-Instalacion-SGD-INIA-PG-Actualizado.docx. Pasan 33 pruebas offline (8 del validador); se conservan 7 pruebas previas en PostgreSQL 17.11 temporal con datos ficticios, incluyendo actualización de sede, carga, reversión, repetición y retiro protegido. UTI 00217 ya fue cargada y confirmada por el operador; no repetir esa carga.

Generar Doc requiere además una plantilla DOCX para el tipo documental y dependencia. La instalación nueva incorpora INFORME 003 y OFICIO 001 en la dependencia global 00000, como alternativa a los formatos por área. Están adaptados al modelo INIA suministrado por el operador, con campos dinámicos y sin nombres, contenido, firmas ni QR del ejemplo. Los nombres de archivo sustituyen las barras de las siglas por guiones, conservando las siglas del contenido.

Para una base existente o restaurada, `Plantillas-SGD.bat` o `bash Plantillas-SGD.sh` verifica los archivos y genera un plan sin consultar BD. Revisar los DOCX; después el operador puede ejecutar `Plantillas-SGD.bat --aplicar --confirmar PLANTILLAS:INIA` o el equivalente SH en el equipo que administra PostgreSQL. Requiere la BD iniciada y las credenciales de esa instalación. Crea un respaldo único antes de aplicar en una transacción; conserva los formatos por área y rechaza una plantilla global distinta o inactiva. No modifica usuarios, cargas por área ni documentos. Procedimiento en la sección 15 del Word y en [INSTALACION.md](manual/INSTALACION.md#plantillas-y-nombres-de-archivo).

La generación admite títulos de dependencia ausentes: utiliza el nombre de la dependencia emisora y convierte campos null a texto vacío. Ante un fallo del motor no devuelve un archivo incompleto. Si los formatos INIA ya fueron cargados, aplicar esta corrección con Actualizar-SGD; no repetir la carga de plantillas ni la de UTI. La prueba `tests/test_contexto_plantilla.py` reproduce el error de cabecera y ejecuta el servicio y servlet reales con datos ficticios, sin BD ni Word.

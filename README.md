# SGD INIA · PostgreSQL portable

Leer manual/Manual-Instalacion-SGD-INIA-PG-Actualizado.docx. Para otro equipo, extraer SGD-INIA-INSTALL_PG-PORTABLE.zip y ejecutar **Instalar-SGD.bat** en Windows/Windows Server o **bash Instalar-SGD.sh** en Linux x64. **No requiere Docker en los destinos.**

PostgreSQL 17.11 nativo, Java 8, Payara 5, Python, Maven, dependencias offline, código fuente y WAR están incluidos. Windows x64 y Linux x64 con glibc >=2.31; Linux necesita Bash, tar y bibliotecas de fuentes/JDK indicadas en el manual. Usar una cuenta normal en Linux. No instalar la base activa en OneDrive ni en una carpeta sincronizada.

## Comandos

| Operación | Windows | Linux |
|---|---|---|
| Instalar y probar | Instalar-SGD.bat | bash Instalar-SGD.sh |
| Arrancar | Iniciar-SGD.bat | bash Iniciar-SGD.sh |
| Detener | Detener-SGD.bat | bash Detener-SGD.sh |
| Actualizar cambios y verificar | Actualizar-SGD.bat | bash Actualizar-SGD.sh |
| Compilar | SGD.bat compilar | bash SGD.sh compilar |
| Desplegar | SGD.bat desplegar | bash SGD.sh desplegar |
| Verificar | SGD.bat verificar | bash SGD.sh verificar |
| Respaldar | SGD.bat respaldar | bash SGD.sh respaldar |
| Empaquetar sin datos ni claves | SGD.bat empaquetar | bash SGD.sh empaquetar |

PowerShell: powershell -NoProfile -ExecutionPolicy Bypass -File .\SGD.ps1 todo. Los lanzadores BAT de instalación, inicio y parada esperan una tecla al terminar. Después de reiniciar el equipo arrancar con Iniciar-SGD.

Después de modificar fuentes, ejecutar Actualizar-SGD.bat (Windows) o bash Actualizar-SGD.sh (Linux). Compilar por sí solo genera los WAR; también es necesario desplegar para ver los cambios. Al terminar, abrir el SGD y recargar con Ctrl+F5.

Editar configuracion.json antes de instalar. URL inicial: http://127.0.0.1:8085/sisdoc/login.do. Usuarios ficticios: admin, mesa_demo, area_demo y jefe_demo. Claves por instalación en datos/ACCESOS-LABORATORIO.txt. Puertos: BD 55432, HTTP 8085, HTTPS 8185, administración 4855; auxiliares Payara HTTP+1000 hasta HTTP+1004.

## Carpetas

| Carpeta | Contenido |
|---|---|
| fuentes | Código editable de ocho aplicaciones y librerías propias. |
| java | Temurin 8u504 Windows y Linux. |
| payara | Servidor normal configurado, ZIP de respaldo, propiedades y JDBC. |
| herramientas | Maven, repositorio offline, Python portable y utilidades. |
| bd | Motor PostgreSQL nativo Windows/Linux, tres SQL y originales. |
| binarios | WAR compilados y reserva del proveedor. |
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

Exportar-Area-SQLServer.sql exige una lista de iCodTrabajador aprobados por el operador; estado 1 no demuestra vínculo laboral vigente. Preparar-Carga.bat y Preparar-Carga.sh convierten sus cinco CSV a un borrador pendiente de revisión, sin consultar bases. Cargar-Area.bat y Cargar-Area.sh validan el CSV de usuarios revisados por el operador; --aplicar respalda y carga una sola área en PostgreSQL. Limpiar-BD.bat y Limpiar-BD.sh retiran exclusivamente un área rastreada por el utilitario, conservando catálogos y objetos compartidos. Por defecto ambos generan planes offline. Instrucciones y comandos completos en manual/CARGA-POR-AREA.md y secciones 10 a 12 de manual/Manual-Instalacion-SGD-INIA-PG-Actualizado.docx. No se ha probado todavía una carga o retirada real con estos utilitarios.

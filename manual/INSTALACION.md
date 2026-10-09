# Manual de instalación SGD INIA · PostgreSQL
Edición 8 de octubre de 2026. Windows x64 y Linux x64. PostgreSQL nativo: los equipos de destino no necesitan Docker.

## 1. Preparar la carpeta
Utilizar SGD-INIA-INSTALL_PG-PORTABLE.zip para una instalación nueva. Extraer la carpeta completa en una ruta corta con permiso de escritura, por ejemplo C:\SGD\SGD-INIA-INSTALL_PG o /opt/sgd/SGD-INIA-INSTALL_PG. En Windows usar una ruta corta: las carpetas internas del servidor pueden superar el límite de rutas si se extrae dentro de varias subcarpetas. No usar OneDrive, carpetas sincronizadas ni una unidad de red para la base activa. La carpeta de trabajo de esta PC contiene datos y claves del laboratorio; el ZIP limpio los excluye.

El paquete incluye PostgreSQL 17.11, Java Temurin 8u504, Payara 5.2022.5, Python, Maven 3.10.0, dependencias offline, fuentes y WAR. No requiere instalar globalmente estos programas ni conectarse a Internet para la compilación inicial. Reservar 8 GB de RAM, preferiblemente 16 GB, y 15 GB de espacio inicial; documentos y respaldos necesitan espacio adicional.

Alcance portable: Windows x64 y Linux x64 con glibc 2.31 o posterior. No incluye ejecutables ARM, macOS ni Alpine/musl. Linux necesita Bash, tar y las bibliotecas del sistema indicadas en la sección 3. En Windows se incluyen las DLL de ejecución C++ necesarias junto a PostgreSQL. La instalación no registra servicios del sistema: se inicia y detiene con los lanzadores de esta carpeta. Después de reiniciar el servidor, ejecutar Iniciar-SGD.

## 2. Windows y Windows Server
Abrir la carpeta en Visual Studio Code. Revisar configuracion.json antes de instalar. Mantener modo completo para instalar PostgreSQL y Payara en el mismo servidor. Cambiar url_publica a la IP o dominio del servidor si accederán otros equipos. Puertos iniciales: PostgreSQL 55432, HTTP 8085, HTTPS 8185 y administración 4855. HTTPS institucional todavía requiere configurar certificado.

Ejecutar desde PowerShell:
```powershell
cd C:\SGD\SGD-INIA-INSTALL_PG
.\SGD.bat diagnosticar
.\Instalar-SGD.bat
```
También se puede hacer doble clic en Instalar-SGD.bat. El lanzador conserva la ventana al terminar. Alternativa PowerShell con política limitada al proceso:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\SGD.ps1 todo
```
El instalador compila ocho aplicaciones desde fuentes, inicializa PostgreSQL con initdb, arranca el motor con pg_ctl, crea la base, carga SQL en una transacción, configura Payara y JDBC, despliega y verifica los accesos. Se detiene ante el primer error y muestra el log pertinente. No necesita Docker, WSL ni una VM en el servidor destino.

Abrir http://127.0.0.1:8085/sisdoc/login.do, o la URL publicada configurada. Las contraseñas de prueba están en datos/ACCESOS-LABORATORIO.txt. Para los siguientes arranques utilizar Iniciar-SGD.bat; para apagar, Detener-SGD.bat. La parada conserva datos y documentos.

La cuenta operadora debe tener escritura en toda la carpeta. Para acceso de otros equipos, autorizar el puerto HTTP elegido en el firewall del servidor. PostgreSQL y administración escuchan localmente por defecto. No hace falta ejecutar como administrador para usar los puertos predeterminados.

## 3. Linux
Usar una cuenta normal, con escritura en la carpeta. PostgreSQL no permite ejecutarse como root. Si /opt requiere permisos administrativos, crear la carpeta y asignarla previamente a la cuenta operadora; ejecutar los lanzadores con esa cuenta.

En Debian/Ubuntu preparar bibliotecas del JDK y herramientas básicas:
```bash
sudo apt-get update
sudo apt-get install -y ca-certificates fontconfig \
  libfreetype6 libstdc++6 tar unzip
```
En Rocky/RHEL usar fontconfig, freetype, libstdc++, ca-certificates, tar y unzip. La prueba Linux se realizó en Debian 12 x64. Los otros sistemas requieren ejecutar las verificaciones del instalador en el equipo destino.

Extraer el ZIP completo, revisar configuracion.json y ejecutar:
```bash
cd /opt/sgd/SGD-INIA-INSTALL_PG
bash SGD.sh diagnosticar
bash Instalar-SGD.sh
```
Arrancar o detener posteriormente:
```bash
bash Iniciar-SGD.sh
bash Detener-SGD.sh
```
El primer uso extrae Python, Java y PostgreSQL Linux incluidos. PostgreSQL Linux se compiló desde el código oficial 17.11; incluye sus herramientas y contrib, y utiliza conexión TCP local. No se necesita gcc, pip, Maven global ni Docker. Este motor Linux no incluye TLS: la configuración predeterminada mantiene la base en loopback. Si se necesita una base remota, preparar transporte protegido y una distribución PostgreSQL con TLS antes de usar datos reales.

## 4. Organización y configuración
Las nueve carpetas principales son: fuentes (código editable), java (JDK de ambos sistemas), payara (servidor normal, respaldo ZIP, plantillas y driver), herramientas (Python, Maven, repositorio offline y utilidades), bd (motor nativo, tres SQL y originales), binarios (WAR generados y reserva del proveedor), datos (base, documentos y claves), logs (resultado de operaciones) y manual (esta guía y evidencias).

configuracion.json es el único archivo de configuración del instalador. base identifica la base nueva; host_bd es 127.0.0.1 en modo completo; entidad, ruc y anio personalizan la carga; url_publica determina enlaces; direccion_http controla escucha HTTP; activar_integraciones permanece false en el laboratorio. Mantener juntos binarios, fuentes, runtimes y dependencias al trasladar la carpeta.

La sede central 001 se crea con nombre y dirección reales en la transacción de instalación. Para una base anterior, Cargar-Area actualiza sólo la sede provisional reconocida con los datos aprobados del área dentro de su transacción; no hay que ejecutar un parche previo. El script Actualizar-Sede-Piloto-PostgreSQL.sql es únicamente una alternativa manual de recuperación. No repetir la instalación sobre una base existente.

Para otra instancia en el mismo equipo elegir cuatro puertos diferentes. También se reservan cinco puertos auxiliares Payara: HTTP+1000 hasta HTTP+1004. Evitar que coincidan con otra instancia. Al cambiar configuración de una instalación existente, detener primero, ejecutar SGD configurar y luego SGD verificar; configurar no recarga la base.

El dominio payara/payara5/glassfish/domains/sgd ya contiene la configuración JDBC. Los scripts adaptan rutas y puertos al destino y usan una variable de entorno para la clave JDBC. payara/payara-5.2022.5-backup.zip conserva el servidor original. Los scripts no cambian JAVA_HOME ni PATH globales.

## 5. Compilar y modificar desde Visual Studio Code
Abrir la raíz del paquete y editar fuentes. Compilar genera los WAR en binarios; no actualiza la aplicación que está ejecutando Payara. Desplegar reemplaza las aplicaciones por esos WAR y verificar comprueba sus accesos. Conservar la base y los documentos; no repetir la instalación inicial para publicar cambios de código.

Para realizar el proceso completo en Windows, ejecutar desde PowerShell o hacer doble clic en Actualizar-SGD.bat:
```powershell
cd C:\SGD\SGD-INIA-INSTALL_PG
.\Actualizar-SGD.bat
```
En Linux:
```bash
cd /opt/sgd/SGD-INIA-INSTALL_PG
bash Actualizar-SGD.sh
```
El lanzador compila, inicia PostgreSQL y Payara si corresponde, despliega y verifica, en ese orden. Se detiene si un paso falla; revisar el mensaje y logs/ULTIMO-ERROR.txt antes de continuar. En VS Code también está disponible la tarea «SGD: actualizar y verificar cambios».

Si se prefiere ejecutar cada paso manualmente, esperar a que termine correctamente antes de pasar al siguiente:
```powershell
.\SGD.bat compilar
.\SGD.bat iniciar
.\SGD.bat desplegar
.\SGD.bat verificar
```
```bash
bash SGD.sh compilar
bash SGD.sh iniciar
bash SGD.sh desplegar
bash SGD.sh verificar
```
Si ya se compiló correctamente y la pantalla sigue igual, no es necesario recompilar: ejecutar iniciar, desplegar y verificar. Abrir http://127.0.0.1:8085/sisdoc/login.do y recargar con Ctrl+F5 después del despliegue. Una imagen o un JavaScript anterior puede permanecer en la caché del navegador. Si persiste, revisar los logs de despliegue y confirmar que se está usando la URL y el puerto de esta instalación.

Se ejecuta clean install -DskipTests en modo offline con el JDK y Maven incluidos. Se generan ocho WAR: SGD, consulta, verificador, MPD, notificador, dos integraciones y recursos. El principal incluye autentica y libreria. logs/compilacion.json registra hashes de fuentes y WAR; el despliegue rechaza un WAR alterado. Las pruebas unitarias heredadas se omiten; las pruebas descritas aquí verifican instalación y acceso.

Solo wstradoc carece de fuentes y utiliza el WAR suministrado. srvccuo, srvciopidetramite, srvcsunat y wsoapIopCliente son dependencias JAR suministradas sin fuentes. No se utiliza un WAR antiguo para ocultar un error de compilación de las aplicaciones disponibles. Notificador e integraciones se compilan, pero no se activan hasta configurar los servicios institucionales.

Se corrigieron perfiles PostgreSQL, conexión por JNDI, propiedades MPV, rutas y dependencia del notificador. La fuente consulta del ZIP PostgreSQL no correspondía a sus vistas: se recuperó la fuente correcta del proyecto existente y se ajustaron las llamadas a funciones TABLE PostgreSQL. Los originales se conservan como respaldo.

## 6. Base, SQL y usuarios ficticios
No ejecutar los scripts históricos SQL Server. Los tres SQL vigentes son bd/01-estructura-catalogos.sql, bd/02-inia-usuarios-ficticios.sql y bd/03-validar.sql. El primero contiene esquema y catálogos; el segundo crea la organización ficticia; el tercero valida estructura y permisos. El DDL MPV adicional del proveedor ya está incluido y no se repite.

El instalador une estructura, catálogos, ficticios, contraseñas cifradas y permisos; ejecuta una única transacción con psql --single-transaction -v ON_ERROR_STOP=1. Un error revierte la carga completa. Una carga terminada no se repite. Un esquema existente sin marca de instalación se rechaza para evitar mezclar información.

Usuarios: admin, mesa_demo, area_demo y jefe_demo. Se crean tres áreas de prueba y una cuenta administrativa temporal. DNI y nombres son ficticios. Las claves aleatorias se generan automáticamente por instalación y se cifran mediante la librería Java del SGD. Se guardan en datos/ACCESOS-LABORATORIO.txt. La conexión JDBC usa sgd_app sin privilegios de superusuario. datos contiene archivos privados; restringir su acceso a la cuenta operadora y no publicarlos.

Si fuera indispensable cargar manualmente, ejecutar primero compilar y generar_sql con SGD.bat o SGD.sh. Se crea datos/INSTALACION-MANUAL-PRIVADA.sql con todas las instrucciones y claves. Ejecutar únicamente en una base nueva; el flujo automático SGD base seguido de SGD datos realiza esa misma carga. Para psql manual usar el ejecutable incluido y el puerto configurado:
```powershell
$env:PGPASSWORD = (Get-Content datos/credenciales.json -Raw |
  ConvertFrom-Json).postgres
& .\bd\postgresql\windows\bin\psql.exe -h 127.0.0.1 `
  -p 55432 -U postgres -d idosgd_inia -X `
  --single-transaction -v ON_ERROR_STOP=1 `
  -f datos/INSTALACION-MANUAL-PRIVADA.sql
Remove-Item Env:PGPASSWORD
```
En Linux usar bd/postgresql/linux/bin/psql, con LD_LIBRARY_PATH apuntando a bd/postgresql/linux/lib. PGPASSWORD puede leerse de datos/postgres-password.txt; retirar la variable al terminar. El flujo ordinario no exige introducir estas claves manualmente.

## 7. Diagnóstico y recuperación
Ante un error revisar logs/ULTIMO-ERROR.txt y el log del paso indicado. Los errores SQL están en logs/bd-instalacion.log; PostgreSQL en logs/postgresql-servidor.log; Payara en payara/payara5/glassfish/domains/sgd/logs/server.log. Corregir la causa y volver a ejecutar el instalador. No borrar la base para resolver un error.

Puerto ocupado: detener esta instancia con su lanzador o elegir otros puertos; los scripts no detienen motores ajenos. Dependencia o ejecutable ausente: copiar nuevamente el paquete completo y comprobar SHA256. PostgreSQL no inicia: revisar permisos, cuenta no root en Linux y versión de datos 17. Login fallido: utilizar las claves de esa misma instalación y revisar logs/login-fallido.html. El cifrado no se sustituye por una contraseña escrita en SQL.

## 8. Copiar a otro equipo y respaldar
Para una instalación nueva utilizar el ZIP limpio entregado. Para generar otro ZIP tras cambios de fuentes, ejecutar SGD.bat empaquetar o bash SGD.sh empaquetar. Se crea SGD-INIA-INSTALL_PG-PORTABLE.zip junto a la carpeta, con archivo SHA256. Excluye base activa, claves, documentos, target y despliegues antiguos. El destino recompila y crea nuevas claves.

Para trasladar información existente, detener aplicaciones y arrancar solamente la base para sacar un respaldo consistente:
```powershell
.\SGD.bat detener
.\SGD.bat base
.\SGD.bat respaldar
.\SGD.bat detener
```
En Linux utilizar bash SGD.sh con las mismas acciones. Conservar datos/credenciales.json, datos/respaldo-portable.dump y datos/documentos. Extraer el ZIP limpio en destino y copiar esos tres recursos a datos. Ajustar URL y puertos y ejecutar el instalador. Cuando la base está vacía y existe el dump, se restaura automáticamente y conserva usuarios y claves. Se adaptan las rutas de documentos al destino.

No copiar datos/postgresql entre Windows y Linux: utilizar pg_dump y pg_restore mediante los comandos del instalador. La restauración se rechaza si ya existe esquema SGD. Guardar una copia externa del dump, credenciales y documentos. Para clonar el laboratorio como instalación nueva no copiar sus credenciales ni datos.

## 9. Pruebas y aceptación
Se validó una carga nativa desde cero en Windows x64 y Debian 12 x64: ocho compilaciones offline, esquema/catálogos y cuatro ficticios, 103 tablas incluida la marca, 102 funciones, restricciones, privilegios, JDBC, cuatro páginas HTTP 200, 69 recursos estáticos, cuatro logins y WebSocket 101. Evidencias en manual/RESULTADO-PRUEBAS.json. Docker se utilizó exclusivamente en esta PC para aislar las pruebas Linux y compilar su motor; el instalador del paquete utiliza procesos nativos y no llama a Docker.

También se comprobó en ambos motores nativos la parada y el reinicio, una segunda inicialización sin duplicados, la reversión total de una transacción con error provocado y la restauración de un dump en una base vacía.

El ZIP limpio se extrajo en otra ruta Windows con espacios, se recompilaron ocho WAR offline, se crearon una base y claves nuevas y se repitieron satisfactoriamente las pruebas de acceso. También se restauró y validó un dump generado en Windows en el motor nativo Linux.

La verificación automática cubre instalación y acceso inicial. Antes de cargar información real completar la aceptación documental: registrar un PDF desde mesa_demo, buscarlo, derivarlo al área técnica, recibirlo con area_demo y comprobar seguimiento y archivos en datos/documentos. Estos pasos del ciclo completo no están declarados como aprobados por las pruebas de acceso.

La instalación en un Windows Server físico diferente todavía requiere ejecutar este procedimiento y verificar sus resultados allí. Java 8 y Payara 5 conservan la compatibilidad Java EE de SGD 4.7; no representan una migración Jakarta. Firma digital, PIDE, correo y HTTPS institucional requieren configuración y aceptación propias. Después del laboratorio sustituir ficticios por información institucional mediante una carga controlada.

## 10. Carga y retiro por área

Procedimiento, formatos, comandos Windows/Linux y propuesta de piloto en manual/CARGA-POR-AREA.md; también en las secciones 10 a 13 de manual/Manual-Instalacion-SGD-INIA-PG-Validacion.docx. Cargar-Area exige usuarios verificados; Limpiar-BD retira sólo un área rastreada, conservando catálogos e historia. Su preparación no ejecutó operaciones contra las bases.

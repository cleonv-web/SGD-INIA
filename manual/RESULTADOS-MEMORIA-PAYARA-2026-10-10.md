# Verificacion de memoria: preparacion inicial y ampliacion activada

Estado final: **4096 MB activos**, PID Payara `3696`. La primera parte conserva
la evidencia de la preparacion inicial de 2048 MB; la ampliacion posterior
autorizada y su activacion se documentan al final.

Revision: 2026-10-10 (Lima). Base Git observada: rama `base-sgd-inia`, commit
`1240980`, sin cambios previos en archivos versionados. Se conservaron los
archivos no versionados existentes. No se preparo el indice ni se hizo commit
o push.

## Resultado

Heap preparado: `-Xmx2048m`, solo en el `server-config` de la copia versionada.
Heap activo observado: `-Xmx1024m`, PID `33244`, Java Temurin 8u504 del portable.
El puerto HTTP 8085 seguia perteneciendo a ese proceso. PostgreSQL tenia un
listener en 55432; solo se inspecciono el socket del sistema operativo.
No se abrio una conexion PostgreSQL, ejecuto SQL, reinicio servidor ni recompilo
Java durante esta etapa.

La lectura inicial de Windows dio 13.448.028 KiB disponibles (12,8 GiB) de
32.961.912 KiB visibles (31,4 GiB). Es una lectura puntual, sin promesa de
capacidad ni de rendimiento con usuarios concurrentes.

## Evidencia offline

- 17 pruebas de memoria aprobadas con el Python portable. Cubren el cambio exacto
  y la reversion de XML, preservacion de otras opciones y `default-config`,
  validacion de valores y duplicados, conservacion de la eleccion en el auxiliar
  usado por el instalador, importaciones bajo Python aislado, respaldo,
  manifiesto, recuperacion de un fallo de escritura y rechazo de dominios activos.
- 8 pruebas existentes de configuracion Payara aprobadas. Trabajan sobre archivos
  temporales y no abren conexiones de BD.
- Los 33 archivos de la copia versionada verifican sus hashes. Frente a Git HEAD,
  los hashes de los otros 32 archivos y del runtime externo permanecen iguales.
- El XML versionado se comparo con `git show HEAD:...` en memoria: coincide byte
  a byte con el original salvo `-Xmx1024m` -> `-Xmx2048m` en `server-config`.
- El guardia de procesos se probo contra el Payara real: rechazo la edicion por
  estar activo. La prueba solo consulta procesos del sistema operativo.
- El SHA256 del XML activo se mantuvo antes y despues de preparar el cambio:
  `EA8F5405A2BD79C3171AE0C31F5D26CB84F64D4580174C79A0251D08F6A97549`.
- Los archivos Python modificados se analizaron por sintaxis. La ayuda del
  instalador se probo sin ejecutar acciones. No se llamo a `configurar()`.
- El parser PowerShell no encontro errores en `ACTIVAR-MEMORIA-PAYARA.ps1`.
  Se ejecuto su modo de inspeccion sin `-Activar`; identifico un proceso del
  dominio y termino sin detenerlo ni editar el XML activo.
- `git diff --check` paso. Los archivos nuevos se revisaron tambien para detectar
  espacios finales y la sintaxis de Python/PowerShell.

Respaldo previo de la copia preparada:
`C:\SGD\SGD-INIA-INSTALL_PG\datos\respaldo-heap-payara-20261010T100154829095`
(XML de 1024 MB y manifiesto anterior). No forma parte de Git.

## Limites y siguiente paso

No se ejecuto la rama de activacion del script contra Payara. Queda pendiente
la breve interrupcion, el reinicio exclusivo de Payara y la verificacion de
`-Xmx2048m` en el proceso nuevo. Los comandos concretos de activacion, reversion
y Git estan en `manual/AJUSTE-MEMORIA-PAYARA.md`.

El aumento de heap no corrige la retencion observada en reportes, no cambia
buffers ni pool JDBC, y no valida la aplicacion integral ni las plantillas
activas. Los otros cuatro puntos de rendimiento y el diagnostico PostgreSQL
siguen pendientes de otra etapa y de la autorizacion especifica para SQL.

## Ampliacion posterior autorizada: 4096 MB activos

El operador solicito ampliar la memoria. La inspeccion previa encontro **13,31
GiB disponibles** de 31,43 GiB visibles. Se eligieron 4096 MB con margen para
Windows y PostgreSQL; no es un tamano optimo demostrado ni una estimacion de
usuarios concurrentes. Tras el arranque habia aproximadamente 10,54 GiB libres.

Se ejecuto `manual/ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096 -Activar` con exito.
El script detuvo exclusivamente Payara, comprobo la parada, respaldo el XML,
cambio solo `-Xmx`, inicio el dominio y verifico su proceso:

- Antes: PID `33244`, `-Xmx1024m`.
- Despues: PID `3696`, `-Xmx4096m`; el listener HTTP 8085 pertenece a ese PID.
- PostgreSQL conserva PID `6896`, listener 55432 y hora de inicio
  `2026-10-09T23:14:15.1627175-05:00`. No se reinicio.
- `default-config` conserva `-Xmx512m` y las demas opciones JVM no cambian.
- Pasaron 19 pruebas offline de memoria (incluyen ahora 4096 MB), mas las 8
  pruebas existentes de configuracion: **27 pruebas aprobadas**.
- Los 33 archivos verifican sus hashes. La copia coincide byte a byte con Git
  HEAD salvo `-Xmx1024m` -> `-Xmx4096m` en `server-config`.
- El XML activo coincide con el respaldo tomado tras detener Payara salvo el
  heap; tambien coincide byte a byte con la copia versionada.
- SHA256 del XML activo: `58f5e9ff84f1ffe86376e1a7485048480977bd55ed4cf9bf7f5c99cf9c0ad15d`.
- El manifiesto registra la activacion verificada, PID y fecha UTC. Es evidencia
  de esa comprobacion, no un monitor continuo.

Respaldos adicionales bajo `datos`, fuera de Git:

- `respaldo-heap-payara-20261010T111811766839`: copia preparada de 2048 MB y su
  manifiesto antes de ampliar a 4096 MB.
- `respaldo-heap-payara-20261010T111824165401`: XML activo de 1024 MB tomado con
  Payara detenido, antes de aplicar 4096 MB.

No se ejecutaron scripts SQL, comandos PostgreSQL, diagnosticos JDBC ni
compilacion Java. Las aplicaciones reanudan su actividad normal con la base al
arrancar. No se hizo validacion funcional integral ni prueba de carga. No se
preparo el indice Git ni se ejecuto commit o push. La guia contiene comandos
exactos de versionado y reversion a 1024 o 2048 MB.

## Segunda mejora de la lista original, pendiente

Reducir las copias al leer archivos en `ArchivoTemporal.leerArchivo`. La version
actual usa un buffer de 4 KiB, un `ByteArrayOutputStream` que crece y una copia
final con `toByteArray`. La medicion previa de un archivo de 20 MiB genero unos
84 MiB de asignaciones acumuladas por operacion; no son necesariamente 84 MiB
retenidos simultaneamente. Se midieron archivos sinteticos con cache caliente
de Windows, sin PostgreSQL ni carga integral de usuarios.

Se explico esta mejora al operador y no se implemento en esta etapa. La retencion
de reportes y los otros ajustes permanecen pendientes. PostgreSQL mantiene el
requisito de autorizacion especifica para consultas o cambios.

## Verificacion posterior de funcionalidad y posibles incidencias

El operador solicito verificar si la ampliacion afecta la funcionalidad.
Se confirmo de nuevo la identidad byte a byte del XML activo respecto al respaldo
de 1024 MB, salvo `-Xmx4096m`. JDBC, aplicaciones, puertos y `default-config`
no cambiaron. Payara conserva PID 3696; habia aproximadamente 9,9-10,1 GiB libres
durante las lecturas puntuales. Su memoria privada era 2,445 GiB y el conjunto
residente 1,835 GiB: los 4 GiB son el limite de heap, no un consumo permanente.

El arranque registro la carga de `sgd`, `consulta`, `verifica`, `mpd`, `recursos`
y `wstradoc`. Entre los 81 registros desde el nuevo arranque no se encontraron
OutOfMemoryError, GC overhead, fallos de despliegue ni las firmas de error de
conexion revisadas. Si aparecieron cinco eventos GRAVE al escanear clases de
Jersey/Guava, con IndexOutOfBoundsException. Cada mismo mensaje tenia 21
ocurrencias anteriores; las ultimas eran del 2026-10-09 23:14:51, con heap de
1024 MB. Son fallos preexistentes; la revision no demuestra que carezcan de
impacto, y no se corrigieron dentro de la tarea de memoria.

Se consulto por HTTP unicamente el recurso estatico
`/sisdoc/resources-4.7/css/inia-institucional.css`, cuya ruta no llama a un DAO:
HTTP 200, 18.853 bytes y contenido igual a la fuente. Se prohibieron redirecciones
en esa comprobacion para evitar llegar accidentalmente al controlador de login.

Las 27 pruebas de memoria/configuracion volvieron a pasar. Tambien pasaron tres
pruebas de servicios reales con dependencias simuladas: generacion de ocho DOCX
y descarga por servlet (287 comprobaciones), localidad (67) y enlace de archivos
PDF/DOCX mediante JDBC interceptado (27). En total: 30 pruebas offline y 381
comprobaciones internas Java en las tres pruebas funcionales adicionales.

Limite relevante: el JAR logic desplegado difiere del JAR de la ultima
compilacion en cinco clases y falta `TextoDocumento.class`. Es coherente con la
etapa anterior, que compilo las correcciones sin demostrar su despliegue; el
cambio de heap no recompilo ni desplego aplicaciones. Las pruebas de documentos
validan las fuentes compiladas con dependencias simuladas y no equivalen al
ciclo documental de la aplicacion activa.

La lectura local de estadisticas JVM con jstat mostro cuatro recolecciones
completas, con 0,476 segundos acumulados desde el arranque. En tres muestras
cortas no aumentaron esas recolecciones. Esto no sustituye una prueba sostenida
de usuarios ni garantiza ausencia de futuras pausas o de retencion de reportes.
El tamano de heap puede cambiar el consumo y las pausas del recolector:
[referencia Java 8](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/ergonomics.html).

La pantalla de login llama a `ReferencedData.grpElementoList("SEG_RED_ENTIDAD")`,
que ejecuta un SELECT de `IDOSGD.SI_ELEMENTO`. Por la regla del operador, se
preparo `output/verificacion-memoria-20261010/comprobar_login.py` sin ejecutarlo
contra el servidor y se pidio autorizacion especifica. El operador solicito
aclarar su alcance; esa respuesta no autoriza la lectura. La prueba solo abriria
la pantalla, sin POST, autenticacion ni registro de documentos. No autoriza
todos los flujos ni implementar la segunda mejora. Permanece pendiente.

Evidencia sin credenciales: `output/verificacion-memoria-20261010/evidencia.json`.
No se ejecutaron consultas PostgreSQL por las pruebas de esta verificacion.

## Pantalla de ingreso comprobada con autorizacion posterior

El operador respondio: "dale para cosas sin riezgo no me pidas permiso".
Se continuo con la lectura de login preparada, sin volver a pedir autorizacion.
La preferencia de continuar autonomamente con verificaciones de bajo riesgo
queda registrada; no implica que se hayan probado todos los flujos del SGD.

Se ejecuto `comprobar_login.py --ejecutar-lectura`. Resultado real: HTTP 200,
8363 bytes, formulario `loginForm` y campos `coUsuario` y `dePass` presentes.
La aplicacion puede consultar `SEG_RED_ENTIDAD` para construir esa pantalla,
como se explico antes de la autorizacion. No se enviaron credenciales ni POST;
no se inicio sesion, registro documentos ni ejecuto scripts SQL directamente.

La revision posterior de `server.log` encontro cero OutOfMemoryError, GC overhead,
errores de conexion de las firmas examinadas y NullPointerException desde el
reinicio. Permanecen los cinco errores de escaneo Jersey ya existentes; no hay
nuevos mensajes GRAVE sin antecedentes dentro de los registros inspeccionados.

Conclusión limitada: el cambio activo conserva la configuracion funcional,
Payara arranca y la pantalla de ingreso responde correctamente. No se encontro
evidencia de una incidencia nueva atribuible al heap de 4096 MB. Esto no garantiza
ausencia de problemas futuros ni sustituye una prueba completa de registro,
derivacion, firma y carga concurrente. La evidencia JSON se actualizo con el
resultado real y el alcance de la lectura autorizada.

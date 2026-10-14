# Limpiar trámites conservando los usuarios

El operador hace **doble clic en `Limpiar-Tramites.exe`**, sin comandos ni parámetros. Vacía los trámites de **todas las áreas y todos los años** y los ciudadanos/proveedores almacenados para autocompletar la MPV. Conserva las cuentas internas del SGD, claves, contadores de acceso, empleados, oficinas, permisos, configuración personal, grupos de destinatarios, plantillas Word y catálogos. Conserva la auditoría general, los respaldos anteriores y el seguimiento de las cargas por área.

Solo admite la instalación existente `idosgd_inia`, host `127.0.0.1`, puerto `55432`, modo `completo`. Inicia el PostgreSQL existente si estaba detenido. No reinstala la base ni recarga cuentas. El ejecutable permanece en la raíz de la instalación y utiliza sus runtimes y módulos incluidos; no es un portable independiente de la instalación.

## Ejecución simple

1. Abrir `C:\SGD\SGD-INIA-INSTALL_PG\Limpiar-Tramites.exe` con doble clic. Este acto inicia la limpieza, sin solicitar comandos o confirmaciones adicionales.
2. El utilitario pausa Payara, cierra Tramitedoc y solicita el cierre normal de Word. Si Word tiene cambios pendientes, atender su aviso de guardado. Espera hasta 45 segundos; nunca mata Word ni descarta documentos automáticamente. Si no termina o hay clientes en otra sesión de Windows, aborta antes de respaldar, archivar o limpiar.
3. Desconecta los clientes PostgreSQL de esta base, incluidos clientes de administración (sus transacciones pendientes se cancelan). Comprueba cierre y ausencia de conexiones, respalda, archiva y aplica la limpieza.
4. Inicia el SGD aunque antes estuviera detenido, comprueba el formulario de acceso, reabre Word y Tramitedoc si los había cerrado y abre el portal de acceso en el navegador. No vuelve a abrir archivos de trámites eliminados. El usuario ingresa nuevamente; no se reutilizan las sesiones previas.

No ejecuta otros lanzadores el operador. Los módulos `.py`/`.ps1` y la fuente `.cs` forman parte interna del proyecto. `Limpiar-Tramites.exe` se compila reproduciblemente con `herramientas/compilar_limpiador_tramites.ps1`, usando el compilador .NET Framework de Windows.

## Opciones técnicas de administración

Estos comandos son para soporte; el operador solo usa el EXE. Mostrar el alcance sin conectarse ni cambiar archivos:

```powershell
.\herramientas\python-windows\python.exe .\limpiar_tramites.py
```

Consultar conteos de tablas, cantidad y tamaño de archivos y usuarios que se conservarán. Es de solo lectura y no pausa el servidor:

```powershell
.\herramientas\python-windows\python.exe .\limpiar_tramites.py --revisar
```

El script Python sin argumentos sigue siendo un plan offline. Los modos de soporte conservan la aplicación explícita con `--aplicar --confirmar TRAMITES:idosgd_inia`; el EXE invoca internamente el modo simple autorizado por el operador. El EXE y el cierre/reapertura de clientes son para Windows; los clientes de otras PC no se controlan desde este equipo.

## Alcance

Vacía expedientes, emisiones, destinos, bandejas, movimientos, referencias, avances, vistos buenos, requisitos presentados, documentos binarios, anexos, historiales documentales, guías, mensajería, fiscalizaciones, notificaciones documentales, intercambios documentales de interoperabilidad, resúmenes y el índice de texto de remitos. Vacía además `idtanirs`, `tdtx_ani_simil`, `lg_pro_proveedor` y el índice de proveedores, para que una prueba nueva no recupere nombres de pruebas anteriores. Las 49 tablas se enumeran explícitamente en `limpiar_tramites.py`; no se seleccionan por prefijo. Estos solicitantes son registros de ciudadanos y organizaciones externas, distintos de los usuarios/empleados internos del SGD.

La primera versión conservaba estos solicitantes. Una limpieza realizada con esa versión no los eliminó. La versión corregida no modifica por sí sola una base ya limpiada: ejecutar nuevamente el EXE borraría también los trámites nuevos. Para conservar un expediente nuevo y corregir un ciudadano concreto debe utilizarse una corrección específica, con respaldo y datos correctos verificados; no repetir una limpieza global.

Aparta los archivos de `datos/documentos` a `datos/limpieza-tramites-FECHA-ID/documentos-anteriores`, con verificación SHA256. Conserva las carpetas originales. Las rutas configuradas deben pertenecer a `datos/documentos`; las rutas externas y enlaces detienen el procedimiento. No limpia temporales de Payara ni archivos guardados fuera de la carpeta documental.

Conserva las secuencias PostgreSQL (`CONTINUE IDENTITY`). No promete reiniciar numeración en 1. Los correlativos que el SGD calcula a partir del máximo de documentos existentes siguen la lógica del sistema y pueden comenzar otra vez al quedar esas tablas vacías. Es una limpieza para el piloto de pruebas.

## Respaldo y transacción

Si Payara estaba activo, lo pausa; PostgreSQL continúa iniciado para ejecutar respaldo y SQL. Cierra las conexiones existentes solo de esta base y comprueba que no se reconecten. Verifica que Word/Tramitedoc no se hayan reabierto antes de archivar y antes del SQL. Genera un dump completo y su SHA256, y comprueba su índice con `pg_restore --list` **antes de apartar archivos**. El respaldo queda en `datos/limpieza-tramites-FECHA-ID` con permisos privados y excluido de Git.

Usa una transacción con bloqueos y `TRUNCATE ... RESTRICT`, sin `CASCADE` ni reinicio de secuencias. Comprueba tablas documentales vacías y conteos y huellas SHA256 sin cambios en todas las otras tablas de aplicación antes de confirmar. Rechaza tablas documentales adicionales, tablas particionadas/externas/materializadas y triggers TRUNCATE sin revisar. Una nueva FK hacia una tabla protegida hace fallar la transacción en lugar de ampliar la limpieza.

Registra la operación y las comprobaciones en `idosgd.limpieza_tramites_control` dentro de la misma transacción. Esta tabla administrativa nueva permite distinguir un COMMIT de un error del cliente posterior; no contiene contraseñas ni documentos. Conserva también `resultado.json`, `limpieza.sql`, `archivos-documentales.json` y `logs/tramites-limpieza.log`.

Si falla el SQL y se confirma que no hubo COMMIT, restaura los archivos, reanuda Payara si estaba activo y reabre los clientes cerrados. Tras una limpieza correcta, el EXE habilita el SGD y restaura los clientes anteriores automáticamente. Si falla la reapertura después del COMMIT, muestra que la limpieza terminó y pide revisar el arranque, sin indicar que se debe limpiar otra vez. Ante fallo al archivar, resultado SQL incierto o restauración de archivos fallida, conserva `datos/.limpieza-tramites.lock` y deja Payara detenido para revisión. Una interrupción abrupta puede dejar ese bloqueo; no quitarlo ni repetir la limpieza sin revisar.

## Recuperación ante interrupción

1. Leer la ruta en `datos/.limpieza-tramites.lock`; conservar el respaldo y revisar `resultado.json`, `error.txt`, `archivos-documentales.json` y el log.
2. Comprobar el identificador de operación en `idosgd.limpieza_tramites_control`. Sin registro, la transacción documental no se confirmó. Con registro, se confirmó: no devolver archivos antiguos como si el SQL hubiera fallado.
3. Para recuperar el estado anterior completo, un administrador restaura `antes-de-aplicar.dump` y los archivos de `documentos-anteriores` conjuntamente, con Payara detenido, según el manual de respaldo. No usar `SGD.bat todo` ni sustituir `credenciales.json`. No se automatiza una restauración destructiva de PostgreSQL.
4. Verificado el estado de base y archivos, retirar únicamente el bloqueo de esa operación y ejecutar `Iniciar-SGD.bat`.

## Pruebas aisladas

```powershell
$env:SGD_PROBAR_POSTGRESQL_AISLADO='1'
.\herramientas\python-windows\python.exe .\tests\test_limpiar_tramites.py -v
```

Crean un clúster temporal propio, puerto libre y datos ficticios; nunca limpian la base configurada. Comprueban cuentas y claves, plantillas y secuencias conservadas, limpieza repetida, restauración real del dump, reversión por FK adicional, fallos de respaldo y archivos, tabla documental desconocida, trigger TRUNCATE y error del cliente posterior al COMMIT. Verifican desconexión real de una sesión temporal, aborto cuando falla el cierre de Word y reanudación automática. Las operaciones de escritorio se simulan: no cierran Word/Tramitedoc del operador durante estas pruebas. El EXE se prueba en una carpeta temporal con un runtime ficticio; nunca se ejecuta el EXE activo para probar. Reutilizan solo el arranque aislado de `tests/test_carga_postgresql_integracion.py`.

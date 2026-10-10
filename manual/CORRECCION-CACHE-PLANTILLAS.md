# Evitar retencion global de plantillas: cambio de codigo validado

Revision: 2026-10-10, Lima. Repositorio `C:\SGD\SGD-INIA-INSTALL_PG`, rama
`base-sgd-inia`, base Git `1240980`. El operador autorizo proceder y validar.

## Cambio realizado

Se agrego el argumento `false` a dos cargas de XDocReport:

- `DatosPlantillaDaoImp` de PostgreSQL, dentro del mapper de la plantilla.
- `DocumentoXmlServiceImp.crearPdfx`, que tambien cargaba un reporte en el
  registro global. No se ejecuta ese metodo legado con sus rutas fijas durante
  las pruebas; su llamada fue compilada con la API instalada.

La biblioteca instalada es XDocReport 1.0.4. Se verifico en su bytecode que la
sobrecarga actual de dos argumentos delega con `cacheReport=true`; la variante
de tres argumentos con `false` omite `registerReport` y devuelve el reporte.
Cada carga sigue creando su propia instancia y su contexto. No se implemento
reutilizacion de reportes entre trabajadores ni una cache nueva.

Las dos modificaciones de produccion son exclusivamente ese argumento y sus
comentarios. Se preservan el SQL, metadatos, manejo de errores, motor FreeMarker,
contenido y formatos de plantilla. No se modifica PostgreSQL, el heap de Payara,
las plantillas DOCX, los otros motores de base de datos ni las reglas documentales.
La busqueda en las fuentes SGD no encontro consumidores que recuperen estos
reportes por `getReport` o `existsReport` despues de cargarlos.

## Validacion con componentes reales y dependencias simuladas

`tests/test_cache_plantillas.py` compila el DAO real modificado y el servicio
real. El `ResultSet` es ficticio y rechaza metodos inesperados; no hay conexion
JDBC. El mapper privado real obtiene los bytes de Informe u Oficio y carga el
reporte; el servicio real genera el DOCX con datos ficticios.

El control usa exactamente la misma fuente del DAO, con el argumento de cache
revertido. Las otras correcciones previas se conservan en ambos escenarios.
Cada escenario se ejecuta en una JVM nueva con el JDK 8u504 del piloto y las
bibliotecas incluidas. El registro comienza vacio en cada proceso.

Cada escenario produjo **170 documentos**: ocho muestras, dos comprobaciones
de cambio de version, 32 operaciones distribuidas entre ocho trabajadores y
dos lotes de 64 generaciones consecutivas. Se probaron ademas bytes ausentes,
vacios y corruptos, con respuesta nula y sin archivo parcial.

| Escenario | Reportes registrados al final | Reportes vivos tras GC | Heap tras primer lote | Heap final tras GC |
|---|---:|---:|---:|---:|
| Control, `-Xmx1024m` | 170 | 170 | 253,81 MiB | 405,03 MiB |
| Ajuste, `-Xmx1024m` | 0 | 0 | 3,36 MiB | 3,38 MiB |
| Ajuste, `-Xmx4096m` | 0 | 0 | 3,35 MiB | 3,37 MiB |

Los reportes vivos se verificaron con referencias debiles, tras varias
solicitudes de recoleccion en la JVM aislada. La comparacion de memoria bajo
el mismo limite de 1024 MB muestra una reduccion aproximada del 99,2 % del heap
retenido en este fixture. No es una estimacion del ahorro total en Payara, donde
hay aplicaciones, sesiones, JDBC y otras cargas. No se midio capacidad de usuarios
ni se promete aceleracion del flujo completo.

Las ocho muestras conservaron exactamente los bytes de **cada entrada** del
paquete DOCX frente al control, verificados por SHA256 canonico. Se excluye del
comparador solo el contenedor ZIP y su metadata de empaquetado. Esta equivalencia
incluye XML, imagenes, relaciones y estilos; no hubo cambio de contenido o formato.

Los 32 documentos concurrentes verificaron su propio marcador y la ausencia de
marcadores de otras operaciones. Una version de plantilla modificada dentro del
fixture se reflejo en la siguiente carga; volver a los bytes originales no
conservo el cambio. No se actualizo ninguna plantilla real de la base.

## Compilacion y regresiones

- Pasaron tres pruebas nuevas de comparacion, liberacion de reportes y memoria;
  sus tres escenarios acumulan 4130 comprobaciones internas Java.
- Pasaron tres regresiones existentes: servicio DOCX/descarga (287
  comprobaciones), seis documentos de campos y destinatarios, y localidad
  documental (67 comprobaciones). Son **seis pruebas offline aprobadas** para
  esta etapa, con 4484 comprobaciones internas Java contabilizadas.
- Los dos archivos de produccion y sus dependencias auxiliares se compilaron
  tambien con `javac -source 1.6 -target 1.6`, el nivel del POM, usando el JDK 8
  del piloto. El compilador aviso que no se especifico bootclasspath de Java 6;
  el entorno de ejecucion comprobado es Java 8, no Java 6.
- `git diff --check` correcto. No se recompilaron todos los WAR ni se ejecutaron
  pruebas de integracion PostgreSQL. No se modificaron los binarios desplegados.

Evidencia: `output/qa-cache-plantillas-20261010/resultados.json`, los tres logs de
escenario, las muestras DOCX y las clases de la compilacion compatible. Los
resultados son offline y usan dependencias simuladas; no hubo HTTP ni SQL para
esta etapa. Los archivos existentes y los cambios de memoria se conservaron.

## Estado: preparado en fuentes, pendiente de activacion

La mejora de memoria sigue activa en el mismo proceso Payara PID 3696 con
`-Xmx4096m`. **La mejora de cache no se desplego.** No se reinicio el servidor
ni se ejecutaron commit o push.

El JAR desplegado difiere de la ultima compilacion en cinco clases y falta
`TextoDocumento.class`, como se documento en la verificacion de memoria. Por eso
un despliegue completo de la compilacion actual incorporaria tambien correcciones
anteriores. La activacion de esta mejora debe preparar y revisar un artefacto
aislado respecto a la version activa, o incluir expresamente esas correcciones.
No reemplazar el JAR o WAR activo con clases sueltas durante la ejecucion.

Desactivar el registro evita acumulacion nueva; los reportes que ya estuvieran
retenidos en la aplicacion activa no se liberan mediante esta validacion offline.
La activacion mediante despliegue/reinicio cargaria la version corregida en una
instancia nueva. La validacion del ciclo real de registro, derivacion y firma
queda para esa etapa, conservando respaldo del binario activo para reversion.

## Repetir pruebas y versionar solo este ajuste

Desde la raiz del repositorio:

```powershell
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_cache_plantillas.py -v
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_contexto_plantilla.py -v
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_plantillas_docx.py -v
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_localidad_documental.py -v
git diff --check
```

Las pruebas guardan nuevas muestras y resultados en la carpeta de evidencia;
respaldar esa carpeta si se desea conservar una ejecucion anterior.

Revisar el indice antes de agregar archivos, porque hay cambios de memoria y
archivos del operador ajenos a esta mejora:

```powershell
git diff --cached --stat
git add -- fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramiteconv/dao/impl/postgresql/DatosPlantillaDaoImp.java fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc/service/impl/DocumentoXmlServiceImp.java tests/test_cache_plantillas.py manual/CORRECCION-CACHE-PLANTILLAS.md
git diff --cached --check
git diff --cached --stat
git commit -m 'Evitar retencion global de reportes al cargar plantillas'
git push -u origin base-sgd-inia
```

Esos comandos quedan a cargo del operador. La reversion del codigo consiste en
restaurar las dos llamadas sin el argumento `false`, y luego compilar/desplegar
con el mismo control del artefacto. Si se revierte en produccion, reaparece el
riesgo de acumulacion observado; conservar el respaldo del despliegue que se use.

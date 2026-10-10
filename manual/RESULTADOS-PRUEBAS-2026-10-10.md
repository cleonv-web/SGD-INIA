# Pruebas de documentos y mediciones en esta PC

El 10 de octubre de 2026 se aprobaron 86 pruebas offline y la compilacion completa de los ocho WAR. La generacion de DOCX encontro retencion de plantillas en memoria; el ensayo sostenido termino con OutOfMemoryError en una JVM de pruebas limitada a 1 GB. Ese proceso era independiente de Payara. Las mediciones no conectaron a PostgreSQL, no reiniciaron servicios y no desplegaron los WAR.

Equipo observado: Intel Core Ultra 7 255HX, 20 nucleos y 20 procesadores logicos, aproximadamente 32 GiB de RAM. La configuracion server-config de Payara fija -Xmx1024m. Los ensayos usaron el JDK 8u504 incluido y el mismo limite. No se aumentaron la memoria ni los pools de Payara.

## Correcciones comprobadas

- Rutas nativas de Windows para la instalacion y futuras cargas, con las operaciones de base interceptadas por mocks.
- Servicios Java reales de documentos administrativos, interoperabilidad y fecha de plantilla: localidad nula, vacia o literal null usa La Molina; Cusco y Arequipa se conservan. Sin localidad configurada, no imprime la palabra null.
- Servicio de DOCX y servlet: datos opcionales nulos, archivos vacios, plantilla ausente y error del motor. La prueba de nombres conserva las comprobaciones de los servicios reales.
- DAO PostgreSQL real de archivos administrativos: INSERT y UPDATE de PDF y DOCX, con JdbcTemplate y PreparedStatement simulados. Ninguna sentencia llego a una conexion.
- Paquete de configuracion de Payara: integridad de sus 33 archivos. Se sincronizo la propiedad localidad_documental_predeterminada=La Molina en el dominio local y en la copia para Git; no se reinicio el dominio.
- Informe y Oficio procesados, convertidos a PDF mediante Word instalado y renderizados con el rasterizador del skill DOCX. Se inspeccionaron las tres paginas de muestra. No hay null ni campos sin resolver; el espacio superior derecho esta libre de texto e imagenes. Solo cambia word/header1.xml en los dos paquetes DOCX: cuerpo, pie, imagenes y geometria de pagina conservados. El tercer PDF lo genero el servicio Java real con datos ficticios y muestra La Molina.

La primera pasada detecto dos pruebas desactualizadas: el compilador de la prueba de nombres no incluia las dos nuevas dependencias fuente; la prueba del instalador esperaba un mensaje anterior. Se corrigieron ambas y se mantuvo la comprobacion de que un MSI ausente impide iniciar el servidor. La pasada final aprobo 86 pruebas en 7,241 segundos, incluyendo 454 comprobaciones internas Java. La compilacion posterior de los ocho WAR termino correctamente; no se afirma que los tests Maven se ejecutaran, pues el compilador del paquete utiliza -DskipTests.

Evidencia en output/qa-plantilla-firma: pruebas-regresion.log, compilacion-completa.log, comprobacion-pdf.json y escenarios/mediciones.csv. Los logs Maven detallados estan en logs/compilar-*.log y sus hashes en logs/compilacion.json.

## Costo de generar documentos

Se midieron 128 operaciones por escenario, con JVM nueva para cada nivel de concurrencia y calentamiento previo de cada trabajador. Se usa el servicio DocumentoXmlServiceImp.crearDocx real con DTO ficticio y sin consultas. La recarga utiliza la misma llamada a XDocReportRegistry.loadReport que el DAO de plantillas. El ensayo de reutilizacion conserva una plantilla por trabajador: es una alternativa experimental, no una optimizacion instalada en la aplicacion.

| Generaciones simultaneas | Recarga actual p50 | Recarga actual p95 | Rendimiento de recarga | Reutilizacion experimental p95 |
|---|---:|---:|---:|---:|
| 1 | 93 ms | 104 ms | 10,8 DOCX/s | 17,6 ms |
| 4 | 306 ms | 347 ms | 13,2 DOCX/s | 18,7 ms |
| 8 | 636 ms | 703 ms | 12,5 DOCX/s | 43,2 ms |
| 16 | 1.342 ms | 1.489 ms | 11,8 DOCX/s | 27,6 ms |
| 32 | 2.597 ms | 3.711 ms | 11,5 DOCX/s | 90,1 ms |

El p95 es el tiempo bajo el que quedan el 95 % de las operaciones del lote. Estos tiempos excluyen consulta de datos, lectura de la plantilla desde PostgreSQL, red, conversion a PDF y firma digital. Hay variacion de JIT, GC y procesos del equipo; las cifras no son una promesa de capacidad de usuarios.

La recarga asigna aproximadamente 51,1 MiB por DOCX; la reutilizacion caliente asigna 11,5 MiB. Son bytes asignados durante la operacion, no memoria viva permanente. Con un trabajador, al finalizar y solicitar GC, la recarga conserva 339 MiB y 142 reportes; la reutilizacion conserva 36 MiB y 14 reportes. Ambas incluyen calentamiento. La diferencia es aproximadamente 303 MiB para 128 generaciones, unos 2,37 MiB retenidos adicionales por generacion en este fixture.

Se verifico en el bytecode de los JAR instalados que la sobrecarga loadReport(InputStream, TemplateEngineKind) registra por defecto el reporte con un identificador de objeto; el cache por defecto es un HashMap con referencias fuertes. El DAO llama esa sobrecarga en cada carga y no se encontro limpieza del registro en las fuentes SGD. Esto explica el crecimiento observado. El ensayo inicial acumulaba los niveles de concurrencia en una sola JVM: completaron los lotes de 1, 4, 8 y 16 trabajadores, y durante el de 32 aparecio OutOfMemoryError. Ese fallo incluye historial acumulado: no demuestra que 32 usuarios sean el limite. En JVM nuevas, los cinco niveles completaron sus lotes.

## Costo de leer archivos y enlazar los PDF

Se midio ArchivoTemporal.leerArchivo real con archivos binarios ficticios. Sus bytes no requieren interpretar PDF: el metodo lee cualquier archivo completo con buffer de 4 KiB, ByteArrayOutputStream y toByteArray. Se calento la cache de Windows; estas cifras no miden disco frio ni transporte JDBC. Los lotes de 20 y 50 MiB son pequenos y sirven como muestra del costo y la asignacion, no como una prueba sostenida de almacenamiento.

| Tamano | Trabajadores | p50 de lectura | p95 | Asignacion por operacion |
|---|---:|---:|---:|---:|
| 1 MiB | 1 | 1,7 ms | 2,1 ms | 3 MiB |
| 5 MiB | 1 | 4,3 ms | 5,0 ms | 21 MiB |
| 5 MiB | 32 | 127 ms | 224 ms | 21 MiB |
| 20 MiB | 1 | 26 ms | 27 ms | 84 MiB |
| 20 MiB | 8 | 203 ms | 236 ms | 84 MiB |
| 50 MiB | 1 | 77 ms | 79 ms | 178 MiB |

La asignacion acumulada puede superar varias veces el tamano del archivo por crecimiento de buffers y copia final. La memoria maxima de la JVM incluye buffers, heap aun no recolectado y otros objetos. No se ensayaron niveles que superaban el presupuesto preventivo de memoria de archivos grandes.

La prueba del DAO real confirma esta secuencia:

| Caso | Consulta previa | Sentencias de escritura | Parametros binarios |
|---|---:|---:|---:|
| PDF nuevo | 1 | 1 INSERT | 2 veces el contenido, BL_DOC y W_BL_DOC |
| PDF existente | 1 | 1 UPDATE | 1 vez el contenido |
| DOCX nuevo | 1 | 1 INSERT | 2 veces el contenido |
| DOCX existente | 1 | 2 UPDATE | 2 veces el contenido |

Por ejemplo, un PDF nuevo de 20 MiB enlaza 40 MiB de parametros binarios. Esto no prueba 40 MiB fisicos ocupados en PostgreSQL: faltan compresion, TOAST, WAL y almacenamiento real. La conservacion de la version Word es funcional; cualquier cambio debe preservar esa version donde corresponde.

## Prioridades y limites

1. Corregir la retencion de plantillas: un cache acotado por dependencia, tipo y version, o una estrategia de registro y liberacion probada. La reutilizacion medida justifica investigar esta mejora; aun no esta aplicada.
2. Reducir las copias completas de archivos y regular las cargas simultaneas grandes. La cola y el limite deben probarse con el flujo real. Aumentar heap puede aportar margen, pero no corrige la retencion observada.
3. Medir el guardado y la descarga reales en PostgreSQL y luego ajustar pools, indices y recursos usando esa evidencia. No se han medido bloqueos, commits, WAL, latencia del disco ni saturacion JDBC.

No es posible afirmar cuantos usuarios concurrentes soporta la aplicacion completa con estas pruebas. Si se limita la conclusion a generacion de DOCX, la recarga actual se estabilizo en unos 11-13 documentos/s y elevar trabajadores aumento sobre todo la espera. La capacidad de sesiones activas depende de la frecuencia de operaciones, tamano de PDF, consultas y firma.

Pendientes de aprobacion, sin ejecutar: SQL 06 para actualizar las dos plantillas globales, con respaldo, guardas de hash y COMMIT; SQL 07 para diagnostico de PostgreSQL en transaccion de solo lectura. El SQL 06 fue analizado por parser PostgreSQL offline. Falta el ensayo integrado de navegador y firma digital despues del despliegue autorizado. Los documentos existentes firmados no se modificaron.

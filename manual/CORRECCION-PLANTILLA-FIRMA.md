# Correccion de localidad y espacio de firma

La localidad faltante ahora usa La Molina, de acuerdo con la indicacion del operador, y conserva un distrito valido cuando el catalogo lo devuelve. Se normaliza tanto la fecha del DOCX como el dato que se entrega al cliente para numerar o firmar. La propiedad es localidad_documental_predeterminada en application.properties.

Las cabeceras de Informe y Oficio reservan 126,1 puntos (44,5 mm) a la derecha para la firma. Se compactaron y reubicaron los logos y la dependencia; solo cambia word/header1.xml. El cuerpo y pie de las plantillas se conservan. Las muestras renderizadas pasan la inspeccion visual y de ocupacion del area reservada. La firma criptografica y su colocacion por el cliente requieren la comprobacion integrada posterior.

La base guarda sus propias plantillas. El archivo bd/correcciones/06_actualizar_plantillas_firma.sql actualiza solamente las dos globales originales, conserva las personalizadas y rechaza hashes desconocidos. Respalda las filas anteriores en idosgd.respaldo_plantillas_firma_20261010 y termina en COMMIT. No se ejecuto. No usar Plantillas-SGD --aplicar para sobrescribir una plantilla distinta: ese comando esta disenado para insertar y rechazar conflictos.

## Comandos para subir los cambios

Ejecutar en PowerShell desde C:\SGD\SGD-INIA-INSTALL_PG. La configuracion de Payara incluye los secretos del dominio piloto, segun autorizacion del operador. Revisar todo lo preparado en el indice antes de confirmar el commit; pueden existir cambios que el operador preparo previamente.

```powershell
Set-Location C:\SGD\SGD-INIA-INSTALL_PG
$archivosCorreccion = @(
  '.gitattributes', 'README.md', 'sgd.py', 'gestion_data.py',
  'herramientas/configuracion_payara.py', 'payara/configuracion',
  'payara/plantillas/sgdproperties/application.properties', 'plantillas-docx',
  'bd/correcciones/04_normalizar_rutas_documentos_todos.sql',
  'bd/correcciones/05_revertir_normalizacion_rutas.sql',
  'bd/correcciones/06_actualizar_plantillas_firma.sql',
  'bd/correcciones/07_diagnostico_rendimiento_solo_lectura.sql',
  'manual/CORRECCION-RUTAS-DOCUMENTOS.md',
  'manual/CORRECCION-PLANTILLA-FIRMA.md',
  'manual/RESULTADOS-PRUEBAS-2026-10-10.md',
  'manual/SUBIR-CONFIGURACION-PAYARA.ps1',
  'tests/test_configuracion_payara.py', 'tests/test_rutas_documentos.py',
  'tests/test_contexto_plantilla.py', 'tests/test_nombres_documento.py',
  'tests/test_instalacion_cliente.py', 'tests/test_localidad_documental.py',
  'tests/test_archivo_documento_offline.py',
  'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramiteconv/dao/impl/postgresql/DatosPlantillaDaoImp.java',
  'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc/service/impl/DocumentoObjServiceImp.java',
  'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc/service/impl/DocumentoObjInterServiceImp.java',
  'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc/service/impl/DocumentoXmlServiceImp.java',
  'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc/web/util/ApplicationProperties.java',
  'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc/util/TextoDocumento.java'
)
git add -- $archivosCorreccion
git diff --cached --check
git diff --cached --stat
# Despues de revisar la lista anterior:
git commit -m "Corrige rutas, localidad y cabeceras de firma; conserva configuracion Payara"
$ramaCorreccion = git branch --show-current
git push -u origin $ramaCorreccion
```

No se ejecuto git add, commit ni push durante estas pruebas. El directorio output contiene resultados y herramientas de QA y queda fuera de la seleccion explicita anterior. Tampoco se agregan datos, documentos de usuarios ni la base portable.

## Compilar y arrancar

El comando de compilacion ya se probo y genero ocho WAR. Puede repetirse:

```powershell
Set-Location C:\SGD\SGD-INIA-INSTALL_PG
.\SGD.bat compilar
if ($LASTEXITCODE -ne 0) { throw 'La compilacion fallo; no continuar.' }
```

La secuencia siguiente requiere una ventana de reinicio y la autorizacion de acceso a PostgreSQL. detener para Payara y PostgreSQL; iniciar prepara PostgreSQL y consulta si existe la base; desplegar inicia aplicaciones que consultan la base. Codex no ejecuto estos pasos. Revisar y aplicar el SQL 06 aprobado antes de comprobar la nueva cabecera; los documentos firmados anteriores conservan su contenido.

```powershell
.\SGD.bat detener
if ($LASTEXITCODE -ne 0) { throw 'No se pudo detener; revisar el log.' }
.\SGD.bat iniciar
if ($LASTEXITCODE -ne 0) { throw 'No se pudo iniciar; no desplegar.' }
.\SGD.bat desplegar
if ($LASTEXITCODE -ne 0) { throw 'El despliegue fallo; revisar el log.' }
```

El codigo interno de detener usa algunas operaciones con check=False: aunque devuelva cero, comprobar el log y el estado antes de seguir. No ejecutar todo, datos, configurar ni restaurar para esta actualizacion: esos comandos hacen operaciones adicionales sobre la base. verificar tambien ejecuta SQL y requiere su autorizacion separada.

## Reproducir las pruebas sin PostgreSQL

Las herramientas de rendimiento quedan en output/qa-plantilla-firma y en el ZIP de evidencia entregado. Los fixtures son ficticios. La segunda instruccion ejecuta once JVM de prueba consecutivas y usa memoria propia; hacerla fuera de una ventana de uso intenso.

```powershell
.\herramientas\python-windows\python.exe output\qa-plantilla-firma\ejecutar_pruebas.py
.\herramientas\python-windows\python.exe output\qa-plantilla-firma\ejecutar_escenarios.py
```

La medicion completa y sus limites estan en RESULTADOS-PRUEBAS-2026-10-10.md. La alternativa de reutilizacion del benchmark no esta instalada en el codigo de produccion.

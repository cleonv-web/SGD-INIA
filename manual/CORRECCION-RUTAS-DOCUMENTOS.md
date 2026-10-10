# Correccion general de rutas de documentos

Fecha: 10 de octubre de 2026. La base de datos no se ha consultado ni modificado
durante esta correccion. Los SQL se entregan para revision y aprobacion.

## Causa y correccion de futuras cargas

Tramitedoc recibe la ruta del directorio desde la configuracion del empleado.
La carga y el instalador utilizaban `as_posix()`, que en Windows transformaba
`C:\SGD\...` en `C:/SGD/...`. Al entregar esta ultima ruta a `explorer.exe`, se
reprodujo la apertura de la carpeta Documentos de Windows.

Se corrigieron `gestion_data.py` y `sgd.py` en la instalacion activa
`C:\SGD\SGD-INIA-INSTALL_PG`. La validacion y la carga del area, la instalacion
inicial, la configuracion y la restauracion usan ahora el formato nativo de
la ruta. En Windows se guardan barras invertidas y en Linux se conserva `/`.
El SQL de instalacion inserta la ruta como literal SQL, evitando que `\set`
interprete sus barras. No es necesario recompilar ni desplegar los WAR para
estas modificaciones de Python.

Los archivos SQL de revision o carga generados ANTES de esta correccion
mantienen el contenido anterior: regenerarlos con el cargador actualizado
antes de aprobarlos. No volver a ejecutar una carga ya aplicada para corregir
las rutas actuales; utilizar el script general.

## Usuarios actuales: script pendiente de aprobacion

Archivo: `bd/correcciones/04_normalizar_rutas_documentos_todos.sql`.

Al ejecutarlo completo en `idosgd_inia`, realiza una transaccion con COMMIT:

1. Crea `idosgd.respaldo_rutas_documentos_20261010` y guarda los valores
   originales y corregidos de las filas afectadas.
2. Normaliza `de_dir_emi`, `de_dir_rec` y `de_dir_ane` en
   `idosgd.tdtx_config_emp` para TODOS los usuarios y oficinas.
3. Conserva el directorio de cada usuario. Reconoce rutas Windows absolutas
   con letra de unidad y rutas UNC; conserva rutas Linux, URL, vacias y nulas.
4. Si existe `idosgd.piloto_objetos`, respalda y normaliza las rutas guardadas
   de `tdtx_config_emp` en ese registro del cargador. Esto evita inconsistencias
   al cargar o retirar areas mas adelante.
5. Verifica que no queden rutas Windows con separadores incorrectos.

Puede repetirse con las mismas rutas ya corregidas sin modificarlas. El respaldo
conserva la primera version de cada fila; no reemplaza un respaldo existente.
No cambia perfiles ni opciones de menu. La correccion de Configuracion personal
se encuentra en el script separado 03, tambien pendiente de autorizacion.

Codex no ejecutara el script ni una consulta previa sin aprobacion especifica.
El archivo termina en COMMIT y guarda los cambios si se ejecuta completo sin
errores. Si una instruccion falla, PostgreSQL aborta la transaccion: ejecutar
ROLLBACK para cerrarla. No ejecutar fragmentos fuera de la transaccion.

## Reversion

`bd/correcciones/05_revertir_normalizacion_rutas.sql` restaura el respaldo.
Solo revierte una fila si sus rutas siguen siendo exactamente el resultado
que se respaldo al corregirla. Conserva las modificaciones manuales posteriores
y muestra esas filas para revision. Tambien restaura el registro del cargador
cuando coincide con la configuracion restaurada. Conserva la tabla de respaldo.
Este archivo tambien requiere autorizacion y no se ha ejecutado.

## Comprobacion despues de aplicar el script aprobado

Cerrar e iniciar sesion en el SGD y permitir la reconexion de Tramitedoc.
Una sesion abierta conserva la ruta anterior. En Windows, el pie de pagina
debe mostrar `C:\SGD\SGD-INIA-INSTALL_PG\datos\documentos` para la configuracion
local estandar. Comprobar Abrir Doc. con un documento existente autorizado.
El problema observado en la captura tambien aparece al utilizar el flujo de
firma; la normalizacion de su directorio de trabajo corresponde a la misma
configuracion. La firma requiere su propia comprobacion despues del cambio.

## Validacion realizada sin base de datos

- 5 pruebas nuevas: multiples usuarios y los tres directorios; rutas Windows,
  UNC, espacios, apostrofes, Linux; instalacion, configuracion y restauracion.
  Las operaciones externas se interceptan con mocks.
- 13 pruebas existentes de carga y retiro: todas aprobadas, sin conexiones.
- Los SQL 04 y 05 y sus bloques SQL/PLpgSQL se analizaron con un parser basado
  en PostgreSQL 17.7, sin ejecutar las sentencias.
- La prueba ampliada del instalador MSI aprobo 17 de 18 casos. El caso
  `test_falta_msi_detiene_antes_del_servidor` ya fallaba por esperar el texto
  `Falta el cliente`, mientras la version Git anterior emite
  `Falta cliente-windows/InstallerTramiteDoc.msi...`. No se modifico ese test.

No se declara probado el cambio en PostgreSQL ni en la sesion del navegador:
eso queda pendiente de aprobar y ejecutar el script general.

## Distribucion de la correccion

`correccion-rutas-sgd.zip` incluye los dos Python corregidos, los SQL 04/05,
las pruebas nuevas y este documento, con un manifiesto SHA256. Para otra
instalacion, revisar los archivos y copiar sus rutas relativas a la raiz del
SGD. El ZIP portable antiguo ya creado no se ha regenerado: distribuir este
paquete de correccion junto con el portable o crear un nuevo portable desde
las fuentes actualizadas.

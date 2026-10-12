# Resultado de la consulta de expedientes

El resultado presenta el estado real, la oficina y los datos de registro. Mantiene el acceso por número de expediente y CAPTCHA; el QR completa el número. No incorpora autenticación adicional en este ajuste.

## Campos antes vacíos

- **Estado:** usa el estado del último destino efectivo. Si no hay uno, muestra el estado del registro original, por ejemplo `EN PROYECTO` o `PARA DESPACHO`.
- **Oficina:** distingue la oficina en atención de la oficina de registro. No presenta un destino preparado de un documento en proyecto o pendiente de despacho como una derivación efectiva.
- **Fecha y hora:** utiliza `DD/MM/YYYY HH24:MI:SS`. El formato antiguo confundía los minutos con el mes y mostraba las horas sin indicar AM/PM.
- **Institución del remitente:** corresponde al dato ingresado en el registro, no a la entidad que administra el sistema. Si está vacío, indica `No registrada`.
- **Observaciones y sumilla:** muestra textos explícitos cuando no hay información registrada. Conserva los datos existentes y escapa su contenido.

La consulta suplementaria `logic/src/main/resources/sql/consulta-resumen.sql` se ejecuta en el DAO PostgreSQL. Selecciona por año y secuencia del expediente; excluye movimientos y destinos eliminados o anulados, documentos en proyecto y pendientes de despacho. Usa una ordenación estable y contempla fechas de modificación ausentes. Es de solo lectura y no requiere migración de la base.

El controlador presenta el resultado completo del DAO. Retira la llamada al recorrido antiguo que concatenaba un destino `1` y podía sustituir el resumen por un resultado incompleto.

## Actualización y pruebas

Ejecutar `Actualizar-Consulta.bat` para compilar, ejecutar las pruebas y desplegar únicamente Consulta, con precompilación de las JSP. Mantiene la base y los demás módulos.

Las pruebas Maven incluyen los estados sin destino, con destino archivado y registros eliminados. `tests/verificar_resumen_consulta.py` ejecuta el DAO real sobre los dos expedientes ficticios del laboratorio y prueba la selección de movimientos mediante tablas virtuales SQL, sin insertar ni modificar datos. Revisa también los campos ausentes, el escape de HTML, los mensajes condicionales y el enlace de nueva consulta. Requiere que existan los expedientes del piloto `2026-0000001` y `2026-0000002` en sus estados de prueba; no crea esos datos.

La prueba genera una vista aislada mediante las expresiones EL y el contenido de la JSP para revisar el diseño con los datos del laboratorio. No constituye una consulta web ni omite su CAPTCHA. La verificación del diseño incluye escritorio y celular de 390 px, sin desbordamiento horizontal.

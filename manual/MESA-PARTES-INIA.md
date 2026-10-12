# Mesa de Partes Digital INIA

Actualización del 11 de octubre de 2026. Portal: `/mpd/inicio.do`.

## Qué cambia

El portal ciudadano incorpora identidad INIA, fotografía institucional local, instrucciones y un formulario en tres pasos: solicitante, documento y revisión/envío. El diseño se adapta a celulares, tabletas y escritorio. No depende de fuentes, imágenes o librerías descargadas de Internet.

La validación obligatoria incluye DNI de ocho dígitos, RUC de once para persona jurídica, correo, ubicación, folios positivos, asunto y documento principal. El autocompletado de identidad conserva la integración existente y permite completar manualmente los nombres. La consulta externa RENIEC/SUNAT depende de la configuración y disponibilidad de esos servicios.

El captcha se genera en el servidor mediante `SecureRandom`. Cada imagen tiene un identificador asociado a la sesión, vence en diez minutos y se consume al verificarla. Acepta minúsculas y espacios exteriores. Renovar la imagen limpia el código escrito, sin borrar el formulario ni los archivos. Se limita a ocho desafíos pendientes por sesión. La imagen y su respuesta HTTP no se almacenan en caché.

Los archivos se cargan con `FormData`; los nombres y mensajes usan JSON y texto escapado. Se respetan `FILE_SIZE_MAX` en bytes para el PDF principal y `FILE_SIZE_MAX_ANEXO` en MB por anexo. Se admite hasta veinte anexos PDF, DOC, DOCX, JPG o JPEG. Quitar un anexo libera también sus bytes de la sesión. El PDF principal requiere extensión PDF y cabecera `%PDF-`; esto no equivale a antivirus ni a una validación completa de su estructura.

El cargo PDF utiliza el logo INIA versionado en las fuentes. El instalador y `Actualizar-MPD` convierten esa misma imagen a `B64_LOGO_REPORTE`; así se reemplaza también el logo SERVIR de dominios existentes. No hay que editar imágenes en la base. La descarga responde como `application/pdf`, no se almacena en caché y devuelve errores 400/404 para datos inválidos o constancias inexistentes.

El registro de PostgreSQL se realiza en una transacción que incluye expediente, documento, PDF, anexos y, cuando falta, el maestro de la persona jurídica escrita manualmente. Se conserva la razón social de un RUC ya existente. Un fallo de anexos revierte la presentación completa. La razón social admite hasta 100 caracteres, conforme al maestro de este SGD. Las llamadas JDBC se mantienen por solicitud para evitar compartir su estado entre usuarios.

Se detecta cuando faltan documentos de la sesión antes de registrar. Los fallos de notificación posteriores a un registro correcto conservan la confirmación del registro; los errores de comunicación indican consultar antes de reenviar. Se bloquean los clics repetidos mientras está en curso el envío. El captcha consumido impide reutilizar la misma solicitud; una nueva solicitud con otro captcha aún puede producir otro registro y debe tratarse como una presentación nueva.

## Alcance técnico

- Fuentes: `fuentes/MPVCiudadano`, incluyendo Java, JSPX, CSS, JavaScript y recursos institucionales.
- Configuración Payara: enlaces y textos de MPD en `mpvproperties/application.properties`, derivados de `url_publica` en `configuracion.json`.
- Instalador: `sgd.py` aplica esos enlaces al configurar el dominio. Conserva las propiedades de correo, tamaños y credenciales existentes.
- PostgreSQL: esta actualización no aplica SQL, no reinstala la base, no cambia usuarios, contraseñas ni permisos.

La corrección de guardado está en el DAO Java: escribe los datos de cada nueva presentación en las tablas existentes. No requiere cambiar funciones SQL ni ejecutar una migración para actualizar un ambiente.

La consulta y la descarga de la constancia ya no apuntan al antiguo puerto 8181. Usan la URL pública de cada instalación. Si la atención será desde otras computadoras, `url_publica` debe contener el nombre o IP accesible del servidor; `127.0.0.1` sirve solo en la propia computadora.

## Aplicar en una instalación existente

Con la actualización publicada en Git, ejecutar en PowerShell:

```powershell
& {
    Set-Location C:\SGD\SGD-INIA-INSTALL_PG -ErrorAction Stop
    git pull --ff-only origin base-sgd-inia
    if ($LASTEXITCODE -ne 0) { throw 'Falló git pull. No continuar.' }
    .\Actualizar-MPD.bat
    if ($LASTEXITCODE -ne 0) { throw 'Falló la actualización. Revisar logs.' }
}
```

`Actualizar-MPD.bat` compila únicamente Mesa de Partes con Maven offline, ajusta sus enlaces y vuelve a desplegar `mpd.war`. Inicia Payara si está detenido. No inicia ni reinicia PostgreSQL y no modifica los otros WAR. Durante el despliegue las sesiones de MPD pueden reiniciarse: terminar las presentaciones en curso antes de actualizar. Después usar Ctrl+F5. No ejecutar `Limpiar-BD`, `SGD todo` ni cargar usuarios para aplicar este diseño.

En Linux, desde la raíz del proyecto con el runtime incluido:

```bash
./herramientas/python-linux/bin/python3 herramientas/actualizar_mpd.py
```

## Instalación desde cero

El flujo habitual `git clone` y `Instalar-SGD.bat completo` (o `servidor` según el equipo) compila estas fuentes y aplica la configuración. No hay una migración adicional de PostgreSQL para el portal. La carga de oficinas y usuarios del SGD sigue siendo un paso separado cuando corresponda al ambiente.

## Validación y prueba del operador

Pruebas automáticas sin registrar expedientes reales:

```powershell
.\herramientas\python-windows\python.exe tests\verificar_mpd_inia.py
.\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_configuracion_mpd.py
.\herramientas\python-windows\python.exe tests\verificar_integracion_mpd.py
```

El primer utilitario compila los tests y ejecuta JUnit 4.11 incluido mediante `JUnitCore`. Se usa este ejecutor porque el Surefire disponible en el caché requiere una versión posterior de JUnit. Los servicios de registro y correo se simulan: no se escribe en PostgreSQL. El segundo verifica enlaces portables, idempotencia y conservación de propiedades.

El tercer utilitario crea una copia temporal de la base local, usa el DAO real y el mismo rol `sgd_app` de Payara, y prueba solicitud, oficio, carta de persona jurídica, informe, memorando y memorando múltiple, con cero, uno y dos anexos. Comprueba PDF, anexos, resumen del SGD, oficina y responsable, cargo y consulta del expediente. También fuerza un fallo real de anexos y verifica que no quede un registro incompleto. Revierte los casos y elimina únicamente la copia temporal que creó. No envía correos, no toca expedientes del laboratorio ni consume sus correlativos. Requiere que PostgreSQL local esté iniciado; ejecutar primero las pruebas de compilación.

En navegador se verificaron campos obligatorios, persona jurídica, ubigeo, carga de PDF, rechazo de un archivo con nombre PDF y contenido diferente, resumen, visualización y renovación del captcha, diálogo y tamaños responsivos. Evidencias: `INCIDENCIAS/mesa-partes-inia-2026-10-11`.

El 11/10/2026 también se descargó por HTTP el cargo de la presentación existente del operador: PDF de una página con logo INIA, registro y clave conservados. Se comprobó que la consulta SQL encuentra ese expediente. La prueba automática de integración no completa un envío ciudadano en navegador ni una derivación/atención interna. La entrega SMTP no se certifica: el notificador no está desplegado en este laboratorio. El texto de términos de la página debe revisarse institucionalmente antes de publicar el servicio ciudadano.

## RENIEC y quién recibe los documentos

La MPV consulta primero los maestros locales de DNI y RUC. El código dispone de consultas externas RENIEC/SUNAT mediante PIDE, pero en la base local revisada los parámetros `URL_RENIEC_REST`, `URL_RENIEC_SOAP`, `URL_SUNAT_REST` y `URL_SUNAT_SOAP` están vacíos. Por tanto, el autocompletado desde el maestro local no prueba una conexión RENIEC activa. No se han enviado consultas externas ni modificado sus claves.

La MPV utiliza la misma base PostgreSQL del SGD. La función `pk_sgd_mesa_virtual_ciudadano` toma la dependencia con `IN_MESA_PARTES='1'` y su empleado responsable. Crea un expediente y remito externo de grupo `3`, origen `06` y estado `5`, junto con el PDF; los anexos se guardan en `TDTV_ANEXOS`. El resumen del SGD se genera correctamente. La lista de registro de Mesa de Partes filtra por esa dependencia y, para acceso personal, por ese responsable. Inicialmente no crea destinatarios internos: la Mesa de Partes registra/revisa y luego deriva a las oficinas.

En este laboratorio hay una única dependencia receptora activa: `49001`, **MESA DE PARTES DEMO**, responsable `49001`, usuario **mesa_demo**. El documento ciudadano existente y todos los casos aislados apuntan allí. Los usuarios UTI no son receptores automáticos de estas presentaciones. Para una instalación institucional se debe cargar/configurar la oficina real y su responsable de Mesa de Partes; el nombre de la página INIA no modifica por sí mismo esa asignación. La función actual no distingue sedes y supone una oficina receptora: no configurar varias con `IN_MESA_PARTES='1'` sin definir antes la regla de distribución.

La consulta pública actual busca por número de expediente y captcha. La clave impresa en el cargo se conserva, pero este módulo de consulta no la solicita ni la valida.

## Documento recibido, formato y constancia

En la recepción del SGD, **Ver Documento** abre el archivo que ya está guardado en el repositorio. Una presentación de MPV no requiere descargar ese PDF y volver a cargarlo para derivarla. **Cargar Doc.** permite adjuntar un archivo cuando falta o reemplazarlo conforme al procedimiento de recepción. **Formato SGD** conserva la apertura o generación del formato interno; en ciertos tipos genera una hoja con los destinatarios, que es distinta del PDF recibido. **Voucher Exp.** genera la constancia con el número de expediente y QR de consulta.

El logo de los reportes SGD se toma de `B64_LOGO_REPORTE` en `sgdproperties/application.properties`. El instalador y `Actualizar-Cargos-QR.bat` fijan el logo INIA; la corrección no modifica la URL del QR ni los datos del expediente. Los PDF descargados anteriormente se deben generar nuevamente para ver el nuevo logo.

El mensaje «No habilitado para cambiar a estado Registrado» corresponde al permiso `IN_CAMBIO_EST` de `TDTR_PERMISO_MP` para el empleado y su dependencia. Adjuntar nuevamente el PDF no resuelve ese permiso. La instalación nueva incluye el permiso de registro de `mesa_demo` en la dependencia Demo `49001`. Para una base existente, la corrección idempotente es `bd/actualizar-permiso-registro-mesa-demo.sql`; se aplica con respaldo previo, en una transacción y sólo si coincide la identidad activa de Mesa Demo. El 12/10/2026 se aplicó al laboratorio y se comprobó `IN_CAMBIO_EST=1`. No registra documentos automáticamente: el operador completa el envío pulsando **REGISTRAR DOC.** desde **PARA VERIFICAR**.

## Utilitarios de esta actualización

| Utilitario | Qué hace | Cuándo usarlo |
| --- | --- | --- |
| `Actualizar-MPD.bat` | Compila y despliega solo MPD; actualiza sus enlaces sin SQL. | Después de `git pull` en una instalación existente. |
| `herramientas/actualizar_mpd.py` | Implementa la misma actualización para Windows y Linux. | Desde el runtime Python incluido; es el ejecutor del BAT. |
| `herramientas/configuracion_mpd.py` | Deriva los enlaces y textos INIA de `url_publica`. | Lo invocan automáticamente el instalador y el actualizador. |
| `tests/verificar_mpd_inia.py` | Ejecuta las pruebas Java offline con registro simulado. | Para verificar cambios de captcha, validación y carga antes de desplegar. |
| `tests/verificar_integracion_mpd.py` | Prueba el DAO real sobre una copia temporal que elimina al terminar; sin correo. | Después de compilar, con PostgreSQL local iniciado, para verificar registro, anexos, consulta y reversión de errores. |

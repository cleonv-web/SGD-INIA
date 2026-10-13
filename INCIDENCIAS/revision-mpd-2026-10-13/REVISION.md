# Revisión de tickets y carga de PDF del 13/10/2026

Se revisaron las fuentes y el despliegue de la instalación activa `C:\SGD\SGD-INIA-INSTALL_PG`. La carpeta ONPE_PCM contiene otras copias de las fuentes; no es el dominio que responde en el puerto 8085.

## Código de barras y QR

El cambio anterior cubría los tres cargos del SGD, pero la constancia ciudadana de MPD conservaba diez componentes Code39 en su propia TDR15. Se reemplazaron por un QR de consulta con margen blanco y enlace PDF, conservando registro, fecha, usuario, clave y logo. Ambos módulos usan `url_consulta_qr` de la configuración; la clave no viaja en el enlace. La clase y la plantilla de MPD son independientes de las del SGD.

La instalación completa y Actualizar-MPD recompilan la plantilla ciudadana antes de empaquetarla. Se conserva el Jasper compilado para uso offline. El actualizador despliega sólo MPD; no requiere SQL. Los PDF descargados anteriormente no cambian: generar nuevamente el cargo.

## Carga de archivos

En una primera visita, Spring genera el action `/mpd/inicio.do;jsessionid=...`. El JavaScript quitaba únicamente un sufijo `/inicio.do`, de modo que calculaba una dirección como `/mpd/inicio.do;jsessionid=.../goUploadDoc`. El servidor devuelve 404. Se sustituyó ese cálculo por el contexto explícito de la aplicación. Los 404 del 12/10 a las 23:38 son compatibles con este fallo, pero el log no registra su URL y no permite demostrar que todos provengan de esa carga.

Se reprodujo además un 500 al cargar exactamente 50 MiB: el límite multipart contaba los encabezados y rechazaba la solicitud antes de validar el archivo. Ahora reserva 64 KiB para el envoltorio y considera el mayor límite de principal/anexo. El controlador conserva la validación individual de tamaño y formato.

En el mismo navegador se detectó que el captcha rechazaba el tipo `image/png;charset=UTF-8` devuelto por Payara. Se acepta el parámetro de MIME y se comprobó la imagen cargada mediante XMLHttpRequest.

## Verificación

- Compilación offline y despliegue MPD correctos; 11 pruebas Java y 3 de configuración Python correctas.
- Navegador Chrome con sesión nueva: PDF principal y anexo respondieron 200; captcha visible; cero errores JavaScript. `tests/verificar_carga_mpd_navegador.cjs` reproduce esta comprobación sin registrar expedientes y requiere Playwright y Chrome instalados.
- HTTP: cargas con un PDF existente de 165508 bytes y copias con relleno hasta 3, 20 y 50 MiB respondieron 200. Antes de la corrección, la de 50 MiB respondió 500.
- Se descargaron por HTTP cargos del mismo expediente existente en SGD y MPD. Ambos tienen una página, registro y clave; un solo QR, sin barras, decodificado a 150 y 300 dpi hacia el mismo enlace y con anotación pulsable. Se inspeccionaron las imágenes renderizadas.
- Evidencias locales: `logs/revision-mpd-2026-10-13/carga-http.json`, `navegador.json`, `qr-verificado.json`, PDFs e imágenes. Los logs y documentos operativos no se versionan.

Se dejaron PostgreSQL y Payara activos. Portal: `http://127.0.0.1:8085/mpd/inicio.do`; SGD: `http://127.0.0.1:8085/sisdoc/login.do`. Usar Ctrl+F5. No se crearon expedientes ni se modificaron registros, usuarios o permisos durante esta revisión.

# Cargos de recepción con QR

Los cargos del SGD reemplazan el código Code39 por un QR de consulta en las plantillas `TDR15`, `TDR_VOUCHER` y `TDR_VOUCHER2`. El QR mide 29,6 mm, tiene borde blanco y también se puede pulsar en el PDF. Conserva el número de registro, la fecha, el usuario, la clave y la nota de recepción del formato elegido.

Ejemplo de destino aprobado para el piloto:

`https://calidad.inia.gob.pe/consulta/inicio.do?expediente=2026-0000002`

El número completo conserva sus ceros. La clave del cargo no se incluye en el enlace. Al entrar, Consulta completa el número y solicita el código de seguridad antes de buscar. El ciudadano escanea con la cámara de su celular; necesita conectividad al dominio. Mientras el dominio no esté habilitado, la lectura del QR mostrará el enlace, pero no podrá cargar la página.

## Actualizar una instalación existente

Ejecutar `Actualizar-Cargos-QR.bat`. Compila y prueba SGD, recompila las tres plantillas Jasper, despliega SGD y actualiza Consulta. Conserva los datos y respalda los WAR y las propiedades anteriores en `logs`. Los PDF ya descargados mantienen su contenido: volver a generar o imprimir el cargo para obtener el QR.

La dirección se configura mediante `url_consulta_qr` en `configuracion.json`. El valor por defecto y el ejemplo son `https://calidad.inia.gob.pe/consulta/inicio.do`. El actualizador escribe esa dirección en las propiedades del SGD. La instalación completa hace lo mismo. No incluir parámetros ni fragmentos en la dirección base; cada cargo incorpora su propio expediente.

Las fuentes `.jrxml` y sus `.jasper` compilados se conservan en el repositorio para instalaciones sin Internet. La generación usa la biblioteca iText ya incluida y funciona sin conectividad.

## Verificación

Las pruebas Java verifican la URL completa, los ceros del expediente, configuraciones inválidas y las celdas negras y blancas con margen. Las pruebas de Consulta comprueban el autocompletado sin búsquedas ni omisión del captcha.

`herramientas/CompilarCargosQr.java` genera muestras ficticias en `logs/cargos-qr/muestras`. `tests/verificar_cargos_qr.py` renderiza cada PDF a 150 y 300 dpi, decodifica el QR y verifica su enlace pulsable. Comprueba además cuatro casos HTTP del formulario desplegado. Estas pruebas visuales requieren las bibliotecas de revisión `pypdfium2`, `Pillow`, `pypdf` y `zxing-cpp`; no son dependencias del servidor.

Este cambio cubre los cargos emitidos desde el SGD (Mesa de Partes). La constancia propia del portal MPD es otro flujo y plantilla.

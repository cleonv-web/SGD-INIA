# Consulta de expediente INIA

Portal: <http://127.0.0.1:8085/consulta/inicio.do>.

El módulo `fuentes/ConsultaTuTramite` usa el logotipo original y la fotografía
institucional existente, colores verdes y acentos dorados, formulario adaptable,
etiquetas accesibles, ayuda desplegable y estados de consulta y error.
Los recursos se sirven localmente y no requieren servicios externos.

El captcha anterior fallaba al inicializar JCaptcha. Su reemplazo genera imágenes
PNG con `SecureRandom`, liga cada desafío a la sesión HTTP y a un token independiente,
limita los desafíos guardados a ocho por sesión y valida cada código una sola vez,
con vencimiento de diez minutos. La validación usa la sesión del servidor.
El campo heredado `cookieSessionId` no decide qué sesión validar.

La renovación de la imagen entrega un token nuevo. El formulario espera a que la
imagen termine de cargar, evita consultas simultáneas y genera un nuevo código
después de cada consulta. Conserva el número de expediente durante los errores.
La consulta PostgreSQL y los campos documentales se mantienen. Los valores de
respuesta se muestran con escape HTML.

## Actualizar

Desde la raíz de la instalación, ejecutar `Actualizar-Consulta.bat` o:

```powershell
& herramientas/python-windows/python.exe herramientas/actualizar_consulta.py
```

El comando compila, ejecuta las pruebas y despliega únicamente `consulta`.
No ejecuta SQL de modificación ni reinstala otros módulos. Conserva el WAR previo
en `logs/consulta-actualizacion-anterior.war` y actualiza el manifiesto de compilación.
Si el navegador conserva una versión anterior, recargar con Ctrl+F5.

## Verificar

```powershell
& herramientas/python-windows/python.exe tests/verificar_consulta_inia.py
```

La actualización ejecuta diez pruebas Java: aislamiento y consumo del captcha,
vencimiento, varios desafíos, generación de imagen y controlador con campos
ausentes, código incorrecto, documento encontrado y documento inexistente.
Las pruebas del controlador usan datos ficticios y no acceden a la base.
La verificación HTTP comprueba los recursos, la generación y renovación del captcha,
el rechazo de intentos inválidos y la disponibilidad de la función PostgreSQL mediante
una consulta de lectura. No resuelve códigos visuales ni crea expedientes.

El respaldo inicial está en `logs/consulta-inia-2026-10-11/consulta-antes.war` y las
fuentes previas en `logs/consulta-inia-2026-10-11/fuentes-antes/`.

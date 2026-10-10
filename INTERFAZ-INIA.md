# Rediseño de la interfaz del SGD INIA

Aplicado en la instalación PostgreSQL de `C:\SGD\SGD-INIA-INSTALL_PG`, que sirve
<http://127.0.0.1:8085/sisdoc/login.do>. La carpeta de OneDrive contiene una copia
de los archivos de presentación entregados; el servicio utiliza la instalación de C:\SGD.

## Presentación

- Acceso con fotografía institucional existente, marca INIA original, mensaje de
  bienvenida, campos etiquetados y mayor jerarquía visual.
- Paleta verde, superficies claras, tarjetas, cabecera, bandejas, botones,
  formularios y diálogos coherentes con la identidad institucional.
- Adaptación de acceso, cabecera, navegación y bandejas a móvil, tablet y escritorio.
  Las tablas extensas conservan sus columnas con desplazamiento interno en móvil.
- Foco visible para teclado y respeto a la preferencia de movimiento reducido.
- Sin fuentes, imágenes ni dependencias externas nuevas.

La implementación añade `inia-institucional.css` y modifica únicamente
`templates/default.jspx`, `templates/main.jspx` y `views/login.jspx`.
Los scripts, identificadores, eventos, enlaces, formularios, bindings del servidor,
permisos, controladores y consultas se conservan.

## Verificación realizada

- Comparación SHA-256 de 2023 archivos originales: sólo cambiaron las tres
  plantillas previstas. Registro: `logs/interfaz-inia/integridad.json`.
- Tres pruebas de regresión de las plantillas: controles, bindings y scripts
  conservados; carga única de estilos y viewport.
- Compilación Maven y despliegue Payara satisfactorios.
- Verificador existente: SQL, JDBC, HTTP, recursos, cuatro usuarios en mayúsculas
  y minúsculas, y WebSocket correctos. Registro: `logs/pruebas-funcionales.json`.
- 17 escenarios existentes de generación de documentos aprobados en modo offline.
- Navegador: acceso y bandeja sin desbordamiento horizontal a 320, 390, 768 y
  1440 píxeles; ingreso y salida de admin, mesa_demo, area_demo y jefe_demo.
- Navegación real a emisión en escritorio y móvil; búsqueda y regreso a la bandeja.
- Configuración personal: apertura, arrastre y cierre sin guardar.
- Cambio de contraseña y selección de dependencia: apertura y cierre en móvil
  sin guardar ni modificar credenciales.

Capturas y comprobaciones: `logs/interfaz-inia/`. La automatización inicial de
navegación sin interfaz visible no entregó eventos de puntero del menú; se
completó la comprobación con el navegador integrado y sus clics reales.

La revisión no ejecuta el cliente externo de Word/firma ni un ciclo documental
completo de creación, firma y derivación. Sus fuentes no fueron modificadas.

Para repetir las comprobaciones automáticas desde la instalación activa:

```powershell
& herramientas/python-windows/python.exe tests/test_interfaz_inia.py -v
& herramientas/python-windows/python.exe sgd.py verificar
node tests/test_generar_documento.js
```

## Respaldo

`logs/interfaz-inia/sgd-antes.war` conserva el despliegue previo.
`logs/interfaz-inia/original/` conserva las plantillas originales y
`logs/interfaz-inia/compilacion-antes.json` el manifiesto anterior.
Para revertir sólo este rediseño, restaurar las tres plantillas, retirar el CSS
nuevo y recompilar y volver a desplegar el módulo SGD; los demás módulos no requieren cambios.

Si el navegador conserva estilos anteriores, recargar con Ctrl+F5.

# Portal ciudadano INIA: diseño y correcciones

Fecha: 11 de octubre de 2026. Ambiente local: `http://127.0.0.1:8085/mpd/inicio.do`.

Se reemplazó la interfaz antigua con textos PCM por un diseño INIA responsivo y un formulario de tres pasos. Se corrigieron la renovación del captcha, su vinculación a la sesión, validación de datos, serialización JSON, archivos y enlaces que usaban el puerto 8181.

## Evidencia obtenida

- Compilación Maven offline de `MPVCiudadano`: correcta.
- Despliegue del WAR `mpd`: correcto, probado mediante `Actualizar-MPD.bat`.
- JUnit: once pruebas aprobadas. Registro y correo simulados; incluye rechazo de cargo inválido/inexistente.
- Python: prueba de enlaces por instalación, escapes Java Properties y conservación de configuración aprobada.
- HTTP real: página INIA, PNG de captcha, cabecera `no-store`, tokens diferentes al renovar y rechazo JSON de una solicitud vacía, sin error 500.
- Navegador: persona jurídica, campos obligatorios, provincias de Lima, distritos, carga de PDF de prueba, rechazo de PDF falso con su mensaje específico, alta y retiro de anexo, resumen, renovación de captcha y diálogo de términos.
- Anchuras de ventana 1440, 768, 390 y 320 píxeles: sin desplazamiento horizontal. Captcha visible en el paso de revisión a 390 píxeles.

Capturas: `portal-escritorio.jpg`, `portal-movil.jpg` y `revision-captcha-movil.jpg`. Los datos visibles de solicitante son ficticios (`example.invalid`). No se introdujo la respuesta del captcha ni se envió la presentación final.

## Verificación ampliada: registro y cargo

Se ejecutó el DAO real sobre copias temporales de PostgreSQL, con el mismo rol `sgd_app` de Payara. Cada caso se revierte; la copia y su dump se eliminan al terminar. Se confirmó que no quedan bases `mpd_prueba_*` ni se añadieron presentaciones al laboratorio. Solo se conserva la presentación ciudadana existente del operador.

| Tipo | Persona | Anexos | Resultado |
| --- | --- | --- | --- |
| Solicitud 009 | Natural | 0 | Correcto |
| Oficio 001 | Natural | 1 | Correcto |
| Carta 002 | Jurídica escrita manualmente | 2 | Correcto; razón social conservada |
| Informe 003 | Natural | 0 | Correcto |
| Memorando 020 | Natural | 1 | Correcto |
| Memorando múltiple 234 | Natural | 2 | Correcto |
| Fallo de BD en anexo duplicado | Natural | 2 | Reversión completa; no queda remito incompleto |

En los seis registros se comprobaron PDF guardado, cantidad de anexos, resumen del SGD, cargo, consulta por expediente, origen `06`, estado `5`, oficina `49001` y responsable `49001`. La prueba de razón social falló antes de la corrección: la función SQL ignoraba el texto manual para el maestro de proveedores. Se corrigió en el DAO Java, preservando los RUC existentes y registrando todos los componentes en una transacción. Se eliminó el estado JDBC compartido entre solicitudes. No se modificaron funciones SQL ni el esquema del laboratorio.

La constancia real existente se descargó por HTTP: `200 application/pdf`, una página, logo INIA comprobado al renderizar. Conserva registro, fecha, clave y código de barras. Los archivos de esa presentación permanecen en `logs/` (ignorados por Git); no se versiona el PDF ciudadano. Se probaron por HTTP `404` para cargo inexistente y `400` para parámetros inválidos, ambos con mensaje y sin PDF vacío. Portal MPD y Consulta tu Trámite responden HTTP 200.

Los parámetros URL de RENIEC y SUNAT están vacíos: integración externa inactiva. La recepción configurada es **MESA DE PARTES DEMO**, usuario **mesa_demo**. La derivación interna completa, respuesta al ciudadano, prueba final con captcha en navegador y entrega SMTP siguen requiriendo prueba del operador. El notificador no aparece en las aplicaciones desplegadas. La consulta pública busca expediente y captcha; no usa la clave impresa del cargo.

Los campos Consultas y Teléfonos siguen sin datos porque `PAG_WEB` y `FONO_INSTITUCION` no están configurados en el laboratorio; no se inventó un teléfono institucional.

## Reproducción en otros ambientes

Esta corrección está en las fuentes y configuración versionables. No depende de modificaciones manuales de PostgreSQL. Después de publicar y hacer pull, ejecutar `Actualizar-MPD.bat` y recargar con Ctrl+F5. Una instalación nueva incorpora el portal mediante el instalador habitual. No borrar ni recargar usuarios para este cambio.

Instrucciones y utilitarios: `manual/MESA-PARTES-INIA.md`.

# Pruebas de flujos SGD, 9 de octubre de 2026

## Documentación oficial

1. Municipalidad Distrital de Chinchao: [Manual de usuario SGD, publicado el 9 de febrero de 2026](https://www.gob.pe/institucion/munichinchao/informes-publicaciones/7757574-manual-de-usuario-sgd).
   [PDF oficial](https://cdn.www.gob.pe/uploads/document/file/9475447/7757574-manual_de_usuario_sgd_mdch.pdf?v=1771536157).
   Emisión, páginas 4-19; seguimiento, páginas 20-21; corrección, páginas 22-25;
   recepción y archivo, páginas 26-32. Las capturas de emisión muestran 2025.
2. Municipalidad Distrital de La Esperanza: [Manuales SGD, publicados el 15 de diciembre de 2025](https://www.gob.pe/institucion/munilaesperanza/informes-publicaciones/7504602-manuales-del-sistema-de-gestion-documental).
   Mesa de partes, páginas 3-6; cargo de entrega, páginas 6-9; recepción, páginas 3-7;
   emisión personal, páginas 3-6; firma, páginas 3-10.

Las fechas anteriores corresponden a la publicación institucional; no se afirma
que cada captura o versión original del material sea de ese año. No son normas
propias del INIA: se usan como referencia funcional para los menús coincidentes.
Los PDFs descargados, sus hashes y las capturas relevantes se conservan en
`logs/qa-flujos-2026/`. Los manuales DIRIS descargados son referencias auxiliares
sin fecha de edición reciente confirmada y no cuentan como documentación 2025/2026.

## Condiciones

- Instalación PostgreSQL: `C:\SGD\SGD-INIA-INSTALL_PG`, puerto 8085, año 2026.
- Usuarios ficticios: area_demo, mesa_demo y jefe_demo; no se utilizan usuarios del área UTI real.
- Asuntos identificados con `PRUEBA SGD 2026-10-09 | QA-xx`.
- Destinatarios limitados a las dependencias DEMO; integraciones externas inactivas.
- Respaldo previo privado: `logs/qa-flujos-2026/antes-pruebas.dump`.
- El operador realiza la firma digital; la automatización no utiliza certificados ni claves de firma.

## Matriz de ejecución

| Caso | Pasos y resultado esperado | Estado |
|---|---|---|
| QA-01 Emisión administrativa | Nuevo → tipo/asunto/plazo/destinatario → Grabar con expediente → Generar DOCX → PDF → Cargar → Anexos → Para despacho → firma del operador → Emitir | Guardado correcto; generación pendiente por falta de respuesta Tramitedoc |
| QA-02 Mesa de partes | Remitente ficticio → datos/folios/asunto/destinatario → Grabar → PDF/anexo → Registrar → expediente y bandeja del área | Guardado correcto; PDF/anexo y registro pendientes; continúa el operador |
| QA-03 Recepción y archivo | Área: buscar QA-02 → visualizar PDF → Recibir → atención/observación → Archivar → consulta/seguimiento | Depende de QA-02 |
| QA-04 Derivación con referencia | Recibir → Atender/Derivar → nueva emisión con referencia → Grabar → archivos → Para despacho → firma del operador | Pendiente |
| QA-05 Emisión profesional | Nuevo personal → jefe destinatario → Grabar → generar/cargar → firma del operador → Emitir | Pendiente |
| QA-06 Consultas y cargo | Filtros de año/asunto/estado → trazabilidad → anexos → cargo de entrega de documentos QA | Pendiente |

## Evidencia inicial

QA-01: informe de AREA TECNICA DEMO a JEFATURA DEMO, tres días de atención,
expediente `ATDEMO20260000001`, correlativo de emisión de la dependencia `1`.
Se conserva EN PROYECTO. Generar Doc no recibió respuesta del cliente de escritorio;
el navegador registró `Please specify a message` y quedó en PROCESANDO.
Se guardó captura y se recuperó la página sin alterar el documento.
La respuesta HTTP del recurso JS y el WAR contienen el manejo actualizado del
mensaje: la incidencia inicial aparece en la sesión del navegador y requiere
comprobar su caché y conexión con APPCLIENT; no demuestra un fallo del motor DOCX.

No se marca un flujo completo como aprobado hasta verificar sus estados, archivos
y destino. No se declara la firma probada sin intervención del operador.

## Entrega al operador

Las pruebas automáticas se detienen por solicitud del usuario, quien realizará los flujos.
QA-02 quedó guardado EN REGISTRO, expediente `2026-0000001`, correlativo `0000001`,
como SOLICITUD `QA-20261009-02`, dirigida a AREA TECNICA DEMO.
Se comprobaron las validaciones de ubigeo y dirección obligatorios antes del guardado.
Los PDF principal y anexo no se han confirmado cargados; tampoco se registró ni recibió
el documento. QA-01 permanece EN PROYECTO. No se firmó ni emitió ningún documento.
Los PDF de prueba están en `output/pdf/qa-flujos-2026/`; los manuales descargados,
en `logs/qa-flujos-2026/`.

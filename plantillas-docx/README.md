# Plantillas Word INIA para pruebas

El catálogo contiene 47 tipos documentales: 45 tienen DOCX y dos generan PDF desde el sistema. Los formatos adicionales son copias editables de los formatos existentes OFICIO e INFORME, con el título correspondiente a cada tipo. Mantienen los campos de combinación, estilos, cabeceras, pies, logos y márgenes. OFICIO e INFORME originales conservan exactamente sus bytes y hashes.

Son bases para pruebas; el responsable debe revisar el contenido de contratos, resoluciones, constancias, PECOSA y los demás tipos antes de aprobar sus formatos institucionales. No incluyen cláusulas legales ni tablas específicas inventadas. INFORME, INFORME TÉCNICO, INFORME LEGAL y RESUMEN EJECUTIVO mantienen cuatro apartados; los demás dejan el cuerpo editable. CARTA y las variantes de OFICIO/CARTA conservan el formato de correspondencia.

El servidor utiliza `IDOSGD.TDTR_PLANTILLA_DOCX.BL_DOC`. Esta carpeta suministra los archivos al instalador, no es la carpeta donde Tramitedoc abre el resultado. La carpeta de salida se configura en `TDTX_CONFIG_EMP`; en el laboratorio es `datos/documentos`, con subcarpeta por año. Se conserva la prioridad de plantillas específicas del área sobre la alternativa global `00000`.

Una instalación nueva carga los 45 formatos. Para incorporarlos a una base existente con PostgreSQL iniciado, ejecutar desde la raíz:

```powershell
.\Plantillas-SGD.bat --aplicar --confirmar PLANTILLAS:INIA
```

El utilitario respalda primero y aplica una transacción: no duplica formatos idénticos, no sustituye formatos distintos y no cambia los del área. `git pull` no actualiza por sí solo las plantillas de PostgreSQL. Crear los formatos tampoco cambia los tipos habilitados en `area.json` ni los permisos de los usuarios.

Al editar un DOCX, conservar los campos de combinación y las instrucciones FreeMarker. Actualizar su SHA256 en `manifest.json` y repetir las pruebas de generación. Un formato global distinto requiere revisión y un procedimiento específico de actualización; Plantillas-SGD rechaza su sustitución automática.

Se procesaron 135 resultados con el motor incluido (45 internos, 45 externos, 45 vacíos). Se verificó la conservación de las partes del DOCX y se revisaron visualmente los 45 resultados internos exportados con Word. Detalles, límites y comandos de reinicio en `manual/CORRECCION-GENERAR-DOC-Y-PLANTILLAS.md`.

| Código | Tipo documental | Archivo / generación |
| --- | --- | --- |
| 001 | OFICIO | OFICIO-INIA.docx |
| 002 | CARTA | CARTA-INIA.docx |
| 003 | INFORME | INFORME-INIA.docx |
| 004 | RESOLUCIÓN | RESOLUCION-INIA.docx |
| 005 | DECRETO | DECRETO-INIA.docx |
| 006 | DIRECTIVA | DIRECTIVA-INIA.docx |
| 007 | ACTA | ACTA-INIA.docx |
| 008 | EXPEDIENTE | EXPEDIENTE-INIA.docx |
| 009 | SOLICITUD | SOLICITUD-INIA.docx |
| 010 | DENUNCIA | DENUNCIA-INIA.docx |
| 011 | RECLAMO | RECLAMO-INIA.docx |
| 012 | INFORME TÉCNICO | INFORME-TECNICO-INIA.docx |
| 013 | INFORME LEGAL | INFORME-LEGAL-INIA.docx |
| 014 | DICTAMEN | DICTAMEN-INIA.docx |
| 015 | CONTRATO | CONTRATO-INIA.docx |
| 016 | CONVENIO | CONVENIO-INIA.docx |
| 017 | ACUERDO | ACUERDO-INIA.docx |
| 018 | NOTIFICACIÓN | NOTIFICACION-INIA.docx |
| 019 | CITACIÓN | CITACION-INIA.docx |
| 020 | MEMORANDO | MEMORANDO-INIA.docx |
| 021 | AGENDA | AGENDA-INIA.docx |
| 022 | PROYECTO | PROYECTO-INIA.docx |
| 023 | MANUAL | MANUAL-INIA.docx |
| 024 | INSTRUCTIVO | INSTRUCTIVO-INIA.docx |
| 025 | COMUNICADO | COMUNICADO-INIA.docx |
| 026 | INVITACIÓN | INVITACION-INIA.docx |
| 027 | CERTIFICADO | CERTIFICADO-INIA.docx |
| 028 | CONSTANCIA | CONSTANCIA-INIA.docx |
| 029 | DECLARACIÓN | DECLARACION-INIA.docx |
| 030 | ORDEN DE SERVICIO | ORDEN-DE-SERVICIO-INIA.docx |
| 031 | CONVOCATORIA | CONVOCATORIA-INIA.docx |
| 032 | RESOLUCIÓN DIRECTORAL | RESOLUCION-DIRECTORAL-INIA.docx |
| 033 | RESUMEN EJECUTIVO | RESUMEN-EJECUTIVO-INIA.docx |
| 034 | AUTORIZACIÓN | AUTORIZACION-INIA.docx |
| 035 | PERMISO | PERMISO-INIA.docx |
| 036 | LICENCIA | LICENCIA-INIA.docx |
| 037 | PECOSA | PECOSA-INIA.docx |
| 038 | RESOLUCIÓN MINISTERIAL | RESOLUCION-MINISTERIAL-INIA.docx |
| 039 | FICHA TÉCNICA | FICHA-TECNICA-INIA.docx |
| 040 | MINUTA | MINUTA-INIA.docx |
| 041 | ACTA DE CONFORMIDAD | ACTA-DE-CONFORMIDAD-INIA.docx |
| 042 | OFICIO MÚLTIPLE | OFICIO-MULTIPLE-INIA.docx |
| 043 | CARTA MÚLTIPLE | CARTA-MULTIPLE-INIA.docx |
| 232 | PROVEIDO | PDF generado por el sistema |
| 234 | MEMORANDO MULTIPLE | MEMORANDO-MULTIPLE-INIA.docx |
| 250 | OFICIO CIRCULAR | OFICIO-CIRCULAR-INIA.docx |
| 304 | HOJA DE ENVIO | PDF generado por el sistema |

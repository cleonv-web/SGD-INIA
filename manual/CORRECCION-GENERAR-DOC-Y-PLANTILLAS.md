# Generar Doc: origen, carpeta de salida y formatos INIA

## Comprobación del laboratorio, 10 de octubre de 2026

El SGD obtiene la plantilla Word del campo `IDOSGD.TDTR_PLANTILLA_DOCX.BL_DOC`, mediante `DatosPlantillaDaoImp.getLstPlantillasDocx`. Selecciona la plantilla del área cuando existe; en su ausencia utiliza la global de dependencia `00000`. Los DOCX de `plantillas-docx` son los archivos que suministra el instalador a esa tabla. El botón Generar Doc no abre directamente esa carpeta.

Para JARBIETO, empleado `00237`, área `00217`, la configuración consultada en `TDTX_CONFIG_EMP` contiene `C:\SGD\SGD-INIA-INSTALL_PG\datos\documentos` como directorio de emisión, recepción y anexos. Los trece registros del laboratorio apuntan a esa misma carpeta. La configuración enviada a Tramitedoc usa `|` como separador de transporte; el cliente lo convierte al separador de Windows.

El INFORME probado corresponde a año `2026`, emisión `0000000574`, tipo `003`. La respuesta del servidor indica `2026|INFORME-R1-2026-MIDAGRI-INIA-GG-UTI.docx`, y la descarga devuelve HTTP 200 con un DOCX válido de 96 097 bytes. La carpeta de salida es, por tanto, `datos\documentos\2026`. La plantilla del INFORME en la base coincide byte a byte con `plantillas-docx/INFORME-INIA.docx`; no procede del manual de instalación que también estaba abierto en Word.

Hashes SHA256 de las plantillas globales encontradas en PostgreSQL y en el repositorio:

| Tipo | Archivo | SHA256 |
| --- | --- | --- |
| 001 | OFICIO-INIA.docx | cc60481629822ac5d11fb8b9bf0d31a9a0f8d8575e7c4ee8a17b6e21477d98c0 |
| 003 | INFORME-INIA.docx | 446262ac93a0a786c3ff0a9f810bbbc1b52e427f7e5fd9de5f26f61d239e0fd7 |

Antes de aplicar este paquete ampliado, la base local contiene solo esos dos formatos. Tener `020` MEMORANDO habilitado en `area.json` no crea ni carga su plantilla. Esto explica una causa comprobada de falta de formatos en una instalación nueva de la versión anterior. La captura remota «archivo ... No Existe» no identifica por sí sola la causa: Tramitedoc puede mostrar ese mensaje después de fallar la descarga. No se ha consultado ni probado la PC remota desde este laboratorio.

## Cambios preparados

- `plantillas-docx`: 45 formatos Word, 43 nuevos y los dos originales conservados. El catálogo tiene 47 tipos; PROVEIDO `232` y HOJA DE ENVIO `304` utilizan el PDF generado por el sistema y no requieren DOCX.
- `sgd.py`: valida los 45 formatos y los incluye en la carga inicial y en Plantillas-SGD. Las plantillas globales existentes idénticas no se duplican; una distinta o inactiva detiene y revierte la transacción. Se conservan los formatos específicos por área.
- `mainMenu.jspx`: transmite la carpeta mediante un atributo HTML escapado, para que una comilla en una ruta válida no rompa JavaScript.
- `wsTradoc.js`: codifica la carpeta en el protocolo del cliente y entrega los errores de generación/verificación al callback, permitiendo cerrar PROCESANDO e informar el error.

El directorio actual del laboratorio ya era correcto. Estos cambios protegen su transmisión y completan los formatos disponibles. Crear los 45 formatos no habilita automáticamente 45 tipos para UTI: el JSON conserva sus siete tipos. Los nuevos formatos son bases editables para pruebas, copiadas de INFORME u OFICIO; necesitan la revisión del responsable de cada tipo antes de aprobar su contenido institucional.

## Validación realizada

- Compilación de los ocho WAR desde fuentes y despliegue local completados.
- 24 pruebas Python: paquete íntegro, catálogo coincidente con el SQL de instalación, conservación de cabeceras/pies/estilos/medios, rutas y nombres de archivo. Incluyen 135 DOCX procesados por XDocReport/FreeMarker con destinatarios internos, externos y campos vacíos y 73 comprobaciones Java de nombres. Una prueba adicional reproduce el recorrido de instalación `datos → generar_sql → sql_plantillas` en archivos temporales y con claves ficticias: verifica que la transacción inicial incluye los 45 tipos, MEMORANDO y la marca de instalación; no conecta con la BD real.
- Pruebas JavaScript: generación, errores del protocolo, cierre del procesamiento y rutas con caracteres especiales.
- PostgreSQL 17 aislado: 45 blobs completos; segunda aplicación sin duplicar; conflicto revierte toda la transacción y conserva formatos por área. No conecta con la base del piloto.
- Exportación con Microsoft Word y revisión visual de los 45 resultados internos: una página por resultado, sin encabezados o pies cortados. Evidencia de paginación en `INCIDENCIAS/generar-doc-plantillas-2026-10-10`.
- Validaciones locales SQL, JDBC, páginas y recursos HTTP, cuatro cuentas de laboratorio y WebSocket correctas; login JARBIETO y descarga del INFORME correctos tras el despliegue.

Queda la prueba del operador: Generar Doc → abrir la plantilla correcta en Word → editar → guardar → cargar al SGD, en local y posteriormente en remoto. Durante la preparación inicial se crearon los archivos sin aplicar las 43 plantillas nuevas en la base local; el aviso de falta de plantilla de MEMORANDO mostrado por el operador confirmó esa carga pendiente.

Después de esa observación se ejecutó `Plantillas-SGD.bat --aplicar --confirmar PLANTILLAS:INIA` en el laboratorio, con respaldo previo `datos/respaldo-antes-plantillas-20261010T232449-6f6dac.dump`. Los 45 formatos están ahora activos y todos sus SHA256 coinciden con los archivos del paquete. Para el MEMORANDO de prueba, año `2026`, emisión `0000000575`, la validación `goValidarPlantillaDocx` responde `OK`; la descarga devuelve HTTP 200 con un DOCX válido de 96 075 bytes, título MEMORANDO y campos procesados. El nombre devuelto es `2026|MEMORANDO-R2-2026-MIDAGRI-INIA-GG-UTI.docx`. No se reinició la base, no se eliminaron usuarios o documentos y no se publicó en Git. Las instalaciones remotas necesitan aplicar también Plantillas-SGD después de actualizar las fuentes, salvo que se haya realizado la carga inicial con este paquete.

## Reiniciar y cargar nuevamente el piloto

Este procedimiento elimina de la base activa todos los documentos y usuarios reales del piloto, conserva respaldo y archiva los archivos anteriores. Guardar y cerrar los documentos de Word y cerrar Tramitedoc antes de ejecutarlo. El operador ejecuta el bloque; si falla una etapa, no continúa con las siguientes.

```powershell
& {
    Set-Location C:\SGD\SGD-INIA-INSTALL_PG -ErrorAction Stop
    .\SGD.bat detener
    if ($LASTEXITCODE -ne 0) { throw 'Falló la parada.' }
    .\Validar-Carga.bat --carpeta datos\carga-util-preparada
    if ($LASTEXITCODE -ne 0) { throw 'Falló la validación.' }
    .\Limpiar-BD.bat --todo --aplicar --confirmar REINICIAR:idosgd_inia
    if ($LASTEXITCODE -ne 0) { throw 'Falló la limpieza. No continuar.' }
    .\Cargar-Area.bat --carpeta datos\carga-util-preparada --aplicar --confirmar CARGAR:00217
    if ($LASTEXITCODE -ne 0) { throw 'Falló la carga.' }
    .\SGD.bat iniciar
    if ($LASTEXITCODE -ne 0) { throw 'Falló el inicio.' }
    .\SGD.bat verificar
    if ($LASTEXITCODE -ne 0) { throw 'Falló la verificación.' }
    Write-Host 'Piloto reiniciado, UTI cargada y servicios verificados.'
}
```

El reinicio utiliza el paquete actual de 45 plantillas. Las cuentas de UTI vuelven a crearse y sus claves nuevas se encuentran en el archivo `CLAVES-GENERADAS-PRIVADAS.json` de la carpeta que indique esta última carga; las claves anteriores no corresponden a esas altas nuevas. Abrir una sesión nueva y recargar el navegador con Ctrl+F5. No ejecutar `SGD.bat todo` después del reinicio.

Para conservar los datos actuales y cargar únicamente los formatos faltantes, con PostgreSQL iniciado:

```powershell
.\Plantillas-SGD.bat --aplicar --confirmar PLANTILLAS:INIA
```

## Cómo queda disponible en otras instalaciones

La solución reproducible consta de `sgd.py`, los 45 DOCX y `plantillas-docx/manifest.json` y `catalogo.json`. Son archivos versionables; no dependen de copiar la BD del laboratorio. Después de probar y subir estos archivos a Git, las instalaciones que descarguen esa versión reciben el paquete completo.

| Ambiente | Procedimiento | Resultado |
| --- | --- | --- |
| Instalación nueva, base vacía | `git clone` de la versión aprobada y `Instalar-SGD.bat completo` | Carga automáticamente los 45 formatos en la transacción inicial. Después usar Cargar-Area para los usuarios reales. |
| Instalación existente o base restaurada | Descargar cambios, ejecutar Actualizar-SGD y después Plantillas-SGD | Compila/despliega el código y agrega los formatos faltantes a la base, con respaldo previo y sin retirar usuarios/documentos. |
| Reinicio completo solicitado del piloto | Ejecutar el bloque de reinicio anterior con el paquete actualizado | Recrea la base con los 45 formatos; después carga el área y genera las claves de las cuentas nuevas. |

La carga inicial se realiza por `sgd.py:datos`, que llama a `generar_sql`; esta función añade `sql_plantillas` al esquema y los catálogos. Plantillas-SGD utiliza esa misma función para las bases existentes. Una base que ya está inicializada no repite la carga inicial al ejecutar Instalar-SGD; por eso la actualización requiere Plantillas-SGD.

Con PostgreSQL iniciado, aplicar en una instalación existente después de descargar los cambios:

```powershell
& {
    .\Actualizar-SGD.bat
    if ($LASTEXITCODE -ne 0) { throw 'Falló la actualización.' }
    .\Plantillas-SGD.bat --aplicar --confirmar PLANTILLAS:INIA
    if ($LASTEXITCODE -ne 0) { throw 'Falló la carga de plantillas.' }
}
```

Un `git pull` por sí solo no actualiza los blobs de una base existente. No copiar la base física, los documentos emitidos ni el respaldo local al repositorio. Las fuentes y plantillas de esta corrección aún no se publicaron en GitHub.

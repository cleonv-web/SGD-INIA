# Limpieza y archivos preparados para versionar el SGD INIA

## Actualización del alcance para instalar desde Git

El operador precisó después que una instalación nueva debe funcionar con git clone e Instalar-SGD, sin copiar otro paquete. Se corrigieron los filtros para incluir Java Windows/Linux, PostgreSQL Windows/Linux, Python Windows/Linux, Maven y su repositorio offline. Los duplicados, base física, documentos, claves, WAR propios y temporales siguen ignorados. Esta actualización no elimina archivos del proyecto ni vuelve a limpiar el dominio del piloto.

La selección de Git se copió a output/clon-191949, sin configuración privada, base ni dominio activo: 12.033 archivos y 938,94 MiB. El instalador creó la configuración de ejemplo, compiló los ocho WAR offline, creó una base y un dominio nuevos en puertos de prueba y completó las validaciones SQL, JDBC, HTTP, cuatro logins y WebSocket. Se comprobaron cero documentos y cuatro usuarios en esa base aislada. Los servicios de prueba se detuvieron al terminar. La base original no se consultó ni modificó.

La comprobación está en tests/verificar_clon_instalacion.py y sus evidencias locales en output/resultado-clon-instalacion.json. También pasaron 3 pruebas de creación/conservación de configuración, 5 de limpieza/filtros, 19 de memoria y 18 de instalación de cliente. La prueba completa corresponde a Windows; no se repitió el arranque Linux en esta revisión. Los cambios todavía deben confirmarse y subirse para estar disponibles en un clon remoto.

Fecha del operador: 10 de octubre de 2026, America/Lima. Se ejecutó únicamente la limpieza solicitada de archivos regenerables de Payara. No se reinició la base, no se recargaron usuarios y no se aplicaron los scripts SQL de incidencias.

## Limpieza real del servidor

| Carpeta del dominio sgd | Archivos retirados | Tamaño anterior MiB |
|---|---:|---:|
| osgi-cache | 1.215 | 104,84 |
| generated | 3.314 | 34,50 |
| temp | 35 | 5,34 |
| logs | 5 | 1,89 |
| Total | 4.569 | 146,57 |

Payara estaba detenido y quedó detenido. Se conservaron las aplicaciones desplegadas. Antes y después de la limpieza se verificaron 1.229 archivos protegidos por SHA256: configuración, certificados, propiedades, driver, aplicaciones, documentos oficiales y archivos de carga/exportación. Ninguno cambió durante la limpieza. Los registros locales están en `logs/limpieza-payara.json` y `logs/limpieza-payara-conservados.json`, excluidos de Git.

Se preparó `Limpiar-Payara.bat`, con revisión por defecto y `--aplicar` para borrar únicamente los contenidos de las cuatro carpetas. Rechaza rutas externas, enlaces/junctions y un PID residual. Cuando el dominio estaba activo, el utilitario contempla detenerlo y arrancarlo otra vez; ese ciclo no se ejecutó en esta limpieza porque el servidor ya estaba detenido. No emplea comandos PostgreSQL.

## Selección de archivos de Git

- Motor Payara: 1.124 archivos, aproximadamente 160,85 MiB; las aplicaciones desplegadas, cachés, temporales y logs no viajan. Ningún archivo del motor seleccionado supera 100 MiB.
- Cargas de UTI: cinco archivos preparados y cinco CSV originales de exportación quedan incluidos. Las claves, SQL privado, respaldos y documentos emitidos siguen excluidos.
- Incidencias: nueve archivos de corrección, revisión y documentación quedan incluidos. Los instaladores Firma ONPE también quedan incluidos.
- WAR permitidos: `wstradoc.war` del proveedor y cinco WAR internos del motor Payara. Los ocho WAR compilables del SGD, copias antiguas y versiones quedan excluidos por nombre y ruta.
- Los manuales Word, plantillas DOCX y ejemplos PDF de fuentes se conservan; no se aplicó una exclusión global por extensión que impida instalar o documentar.

Se actualizó la copia aprobada de configuración desde el dominio actual: cambiaron `config/local-password` y `sgdproperties/application.properties` respecto de la captura previa. Los 33 archivos de la copia y sus hashes se verificaron. Se conserva `-Xmx4096m`. Esta sincronización afecta la copia versionada, no los archivos del dominio activo.

## Comprobaciones ejecutadas

Pasaron cinco pruebas de limpieza y reglas de Git, ocho de configuración Payara, diecinueve de memoria y siete de seguridad del reinicio del piloto. Las pruebas nativas de limpieza usan carpetas temporales con nombres que contienen espacios y corchetes. Verifican conservación de archivos necesarios, revisión sin cambios y bloqueo por PID y junction.

Además, se copiaron exclusivamente los 1.124 archivos del motor seleccionados por Git a una carpeta temporal externa al proyecto. Payara creó correctamente un dominio nuevo con esos archivos. No se inició el servidor ni se consultó PostgreSQL, y la carpeta temporal se eliminó al terminar. Registro local: `logs/qa-runtime-payara.json`.

El manual Word vigente incorpora las secciones de reinicio completo, limpieza, reglas de Git, resumen de instalación y utilitarios al final. Se renderizó con Word nativo para revisión visual; LibreOffice no está instalado en este equipo. PDF e imágenes de comprobación quedan en `output/qa-manual-versionado`, excluidos de Git.

No se hizo commit ni push. La entrega queda preparada para revisar y subir posteriormente usando `git add .`.

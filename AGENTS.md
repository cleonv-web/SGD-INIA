# Instrucciones del proyecto SGD INIA

## Acuerdos del operador

- Una instalación nueva debe funcionar con `git clone` y `Instalar-SGD.bat` (o `bash Instalar-SGD.sh` en Linux), sin copiar un portable adicional. Versionar Java, PostgreSQL, Python, Maven y dependencias offline necesarios, además de Payara. El instalador crea configuracion.json desde el ejemplo si falta; nunca sustituir la configuración existente.
- La selección para Git se hace con .gitignore, conservando los archivos en disco. No volver a ejecutar limpieza física de Payara, documentos ni base por preparar una subida; requiere una solicitud específica del operador.
- El repositorio es privado. Versionar los archivos de carga y exportación de usuarios bajo `datos/carga-*`, `datos/cargas` y `datos/exportacion-*`, incluidos los CSV de trabajo y pendientes. Conservar las correcciones y evidencias de `INCIDENCIAS` y los instaladores de los clientes.
- Versionar el motor Payara extraído y la configuración aprobada en `payara/configuracion`. Excluir el dominio activo, las aplicaciones desplegadas, cachés, temporales y logs. No eliminar módulos, bibliotecas, certificados, propiedades ni aplicaciones para reducir tamaño.
- Conservar `binarios/proveedor/wstradoc.war`, que no tiene fuentes en el proyecto, y los WAR internos necesarios del motor Payara. Excluir los ocho WAR que se regeneran desde fuentes y sus versiones anteriores.
- Excluir documentos emitidos (PDF/Word), base física, credenciales, claves generadas, SQL privado y dumps. Mantener los manuales, plantillas y archivos de prueba que sí forman parte del proyecto; no ignorar globalmente PDF o DOCX.
- Para preparar una subida usar siempre `git add .`; revisar después el contenido del índice y `git diff --cached --check`. No hacer exclusiones adicionales de incidencias o instaladores sin una solicitud del operador.
- El operador ejecuta los comandos que reinician su base. Prepararlos y documentarlos; no ejecutar el borrado de PostgreSQL por iniciativa propia. La solicitud de limpiar Payara solo permite retirar los archivos regenerables de ese servidor, no resetear datos ni aplicar SQL de incidencias.
- Proceder con lecturas locales y tareas reversibles de bajo riesgo sin repetir solicitudes de permiso. No crear confirmaciones adicionales cuando el operador ya haya definido el alcance.

## Utilitarios que no deben confundirse

- `Limpiar-BD.bat --area CODIGO` retira un área rastreada solo si no tiene documentos ni referencias que impidan el retiro. Por defecto genera un plan offline.
- `Limpiar-BD.bat --todo --aplicar --confirmar REINICIAR:idosgd_inia` reinicia TODA la base local del piloto, incluso documentos y usuarios reales, con respaldo obligatorio y archivo de los documentos anteriores. Recrea cuatro cuentas de laboratorio. Después se cargan nuevamente las áreas revisadas.
- `Recargar-Usuarios` ajusta alcances de acceso sin eliminar cuentas, contraseñas ni documentos. No sustituye el reinicio completo solicitado por el operador.
- `Limpiar-Payara.bat` muestra los archivos regenerables; `--aplicar` limpia únicamente `osgi-cache`, `generated`, `temp` y `logs` del dominio `sgd`. Si Payara estaba activo, lo detiene y lo vuelve a iniciar. No detiene PostgreSQL ni ejecuta sus comandos.
- Tras reiniciar el piloto no ejecutar `SGD.bat todo` para cargar usuarios: puede restaurar un respaldo portable anterior. Usar `Cargar-Area` y abrir una sesión nueva con las nuevas claves.

Documentación vigente: `manual/CARGA-POR-AREA.md`, `manual/LIMPIEZA-PAYARA.md`, `manual/VERSIONADO.md` y `manual/Manual-Instalacion-SGD-INIA-PG-Actualizado.docx`.

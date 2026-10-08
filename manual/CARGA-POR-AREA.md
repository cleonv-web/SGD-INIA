# Carga y retiro de áreas del piloto SGD INIA

Los utilitarios trabajan con los archivos que tú revisas y apruebas. No consultan SQL Server. La revisión inicial es offline; únicamente `--aplicar` conecta a PostgreSQL. Ninguna carga o limpieza real se ejecutó durante su preparación.

## Áreas para comenzar

Propuesta: UTI, Mesa de Partes y una unidad usuaria pequeña de la sede central, con responsable y personal que emita y reciba documentos. Administración, Planeamiento o una unidad técnica pueden cumplir ese tercer papel; elegir por participación del equipo y volumen manejable, sin asumir el nombre vigente de una oficina. Incorporar Administración como cuarta área sólo si se necesita probar una derivación adicional. No es necesario incorporar todas sus subáreas.

Comenzar con 10 a 20 personas seleccionadas en total, no con toda la planilla. UTI requiere un operador de soporte y usuarios de prueba funcional; Mesa de Partes requiere un registrador y su responsable; la unidad usuaria requiere un responsable y dos o más trabajadores. El administrador temporal se conserva hasta validar un administrador institucional. Estos tamaños son una propuesta, no una restricción del SGD.

La prueba mínima es registrar un PDF en Mesa de Partes, derivarlo a la unidad usuaria, recibirlo, emitir una respuesta, comprobar seguimiento y comprobar que otro usuario no accede a funciones o áreas no autorizadas. Firma digital, correo y conexiones institucionales requieren pruebas propias. La carga de maestros no demuestra que el ciclo documental esté aprobado.

## Preparar la información

1. En SQL Server ejecutar `bd/Consultar-Estructura-SQLServer.sql` para obtener columnas, índices y relaciones. No consulta trabajadores.
2. Ejecutar `bd/Revisar-Area-SQLServer.sql` con `@Oficina=NULL`. Elegir el código del área, reemplazar NULL por ese número y ejecutar el archivo completo. `@SoloEstado1=1` filtra estado 1; confirmar que representa activo en el sistema origen.
3. Ejecutar `bd/Exportar-Area-SQLServer.sql` con el mismo código. Guardar sus cinco resultados como CSV UTF-8 con encabezados, dentro de `datos`, que está excluido de Git. No incluye contraseñas, hashes, firmas ni fotografías.
4. Preparar `area.json` y `usuarios-verificados.csv` con los usuarios que efectivamente autorizas. El formato del utilitario es normalizado; no acepta directamente los cinco CSV originales. No divide apellidos ni inventa DNI o permisos automáticamente.

Windows, terminal PowerShell en `C:\SGD\SGD-INIA-INSTALL_PG`:

```powershell
.\Cargar-Area.bat --plantilla --carpeta datos\carga-uti
.\Cargar-Area.bat --catalogos
```

Linux, terminal situada en la raíz de la carpeta portable:

```bash
bash Cargar-Area.sh --plantilla --carpeta datos/carga-uti
bash Cargar-Area.sh --catalogos
```

Se crean dos archivos. Las plantillas contienen códigos y textos de ejemplo, y están marcadas NO; no pueden cargarse tal como se entregan. Los códigos 51001, 52001, 101 y 1001 de los ejemplos no son códigos confirmados de tu organización. Consultar personalmente `bd/Consultar-Catalogos-Piloto-PostgreSQL.sql` para revisar códigos y catálogos vigentes del destino.

En `area.json` completar código de dependencia de cinco dígitos, nombre, sigla, nombre corto, padre existente, nivel, sede, tipos documentales y perfiles. El padre 00000 de la plantilla es el nodo temporal del laboratorio; sólo mantenerlo si apruebas esa organización provisional del piloto. Para una raíz institucional usar null o un padre institucional previamente preparado. El utilitario no reconstruye automáticamente todo el organigrama ni crea padres pendientes.

La sede usa un código de tres dígitos por compatibilidad con la configuración de empleados; cargos usan cuatro. Si ya existen, deben coincidir con sus datos; no se sobrescriben. Completar `titular_empleado` con el código de uno de los usuarios verificados de la carga. Marcar `aprobado` como SI y registrar `verificado_por` después de revisar área, titular y permisos.

El CSV tiene estos encabezados exactos, separados por coma:

```text
origen_id,codigo_empleado,usuario,nombres,apellido_paterno,apellido_materno,dni,email,cargo_codigo,cargo_nombre,perfil,verificado
```

Cada fila representa una persona autorizada para esa área. `origen_id` es su ID de SQL Server; `codigo_empleado` es su código definitivo de cinco dígitos en SGD. Mantener el mismo código y usuario si la persona participa en otra área. `perfil` identifica una definición de `area.json`; `verificado` debe ser SI en todas las filas. Retirar del CSV los usuarios pendientes. DNI debe contener ocho dígitos válidos en formato; la herramienta no verifica identidad ante RENIEC. Confirmar identidad y estado personalmente.

No concatenar o recortar apellidos para forzar límites: paterno y materno admiten 40 caracteres cada uno; nombres 80, usuario 20 y correo 50. CSV UTF-8 con encabezados; si un valor contiene coma, encerrarlo entre comillas dobles conforme al formato CSV. No añadir columnas de contraseña o hash.

Los perfiles de plantilla son una propuesta de permisos que debes revisar, no la traducción aprobada de tus perfiles anteriores. Incluir códigos de menús padres para que las opciones sean visibles. `administrador=true` concede el indicador de administrador del SGD; usarlo sólo en la cuenta de soporte autorizada. `firma` habilita la configuración de firma, pero no instala certificados ni demuestra que la firma digital funcione. Para Mesa de Partes ajustar también `mesa_partes=true` en el área.

## Revisar y aplicar una carga

Primero revisar sin tocar ninguna base:

```powershell
.\Cargar-Area.bat --carpeta datos\carga-uti
```

```bash
bash Cargar-Area.sh --carpeta datos/carga-uti
```

La herramienta valida los archivos y genera `revision.json` y `plan.sql` en una carpeta privada dentro de `datos`. El plan offline no comprueba colisiones ni referencias actuales del destino; esas comprobaciones se realizan en la transacción al aplicar. No ejecutar el plan SQL manualmente: contiene un marcador de cifrado pendiente.

Después de tu revisión, con PostgreSQL iniciado y en una ventana de mantenimiento del SGD, aplicar. Sustituir 51001 por el código que figura en tu `area.json`:

Para una instalación completa en un mismo equipo, detener la aplicación y arrancar sólo PostgreSQL antes de aplicar:

```powershell
.\SGD.bat detener
.\SGD.bat base
```

```bash
bash SGD.sh detener
bash SGD.sh base
```

En servidores separados coordinar también la parada de la aplicación remota. Después de la operación arrancar la aplicación con `SGD.bat iniciar` o `bash SGD.sh iniciar`.

```powershell
.\Cargar-Area.bat --carpeta datos\carga-uti --aplicar --confirmar CARGAR:51001
```

```bash
bash Cargar-Area.sh --carpeta datos/carga-uti --aplicar --confirmar CARGAR:51001
```

La operación exige credenciales existentes de esta instalación y la librería Java compilada; si falta la librería, ejecutar primero `SGD.bat compilar` o `bash SGD.sh compilar`. No instala Docker ni descarga dependencias de ejecución. Funciona con los runtimes x64 incluidos; otros procesadores o sistemas requieren su paquete compatible.

Antes de modificar PostgreSQL se crea un respaldo completo `antes-de-aplicar.dump` con su SHA-256. Si falla, la operación no continúa. Se registra procedencia en tres tablas `idosgd.piloto_areas`, `piloto_objetos` y `piloto_usos`. Las tablas se crean en la misma transacción que la primera carga. Nuevos usuarios reciben claves aleatorias cifradas con la librería del SGD; las cuentas existentes compatibles conservan su contraseña.

Se rechazan códigos o identidades incompatibles, usuarios duplicados y dependencias ya existentes fuera de una carga del utilitario. Los objetos compartidos deben coincidir con sus datos y permisos globales; no se sobrescriben. Repetir exactamente los mismos archivos no duplica filas ni cambia claves. Cambiar datos de un área ya cargada requiere un procedimiento de actualización separado: este utilitario crea y retira áreas, no modifica cargas existentes.

Después de una transacción confirmada revisar `resultado.json` y `CLAVES-GENERADAS-PRIVADAS.json`; este último sólo contiene las cuentas creadas en esa ejecución. Si la operación falla, las claves provisionales de esa carpeta no deben distribuirse. Si se pierde la conexión al confirmar, no asumir que se revirtió: conservar el respaldo y el registro y comprobar personalmente el estado antes de continuar. Restringir acceso a `datos`; nunca subir exportaciones, claves o respaldos a Git.

Probar personalmente los accesos y los permisos de esa área. Luego repetir el procedimiento con otra carpeta y otro código de área. No ejecutar nuevamente los SQL de instalación inicial. Los usuarios ficticios se conservan y la verificación actual del instalador sigue validándolos; no usar esa prueba como aceptación automática de las cuentas nuevas.

## Retirar sólo un área cargada

El nombre Limpiar-BD se refiere exclusivamente al retiro de un área rastreada por este utilitario. No vacía la base ni elimina una dependencia creada fuera de él. Primero generar el plan offline:

```powershell
.\Limpiar-BD.bat --area 51001
```

```bash
bash Limpiar-BD.sh --area 51001
```

Para aplicar personalmente el retiro, en mantenimiento:

```powershell
.\Limpiar-BD.bat --area 51001 --aplicar --confirmar RETIRAR:51001
```

```bash
bash Limpiar-BD.sh --area 51001 --aplicar --confirmar RETIRAR:51001
```

Se respalda toda la base antes del cambio. El retiro elimina sólo objetos creados por el utilitario sin uso por otra área. Conserva sedes, cargos, demás catálogos, estructura, archivos documentales y registros de acceso. Las cuentas o empleados compartidos permanecen. Si un empleado compartido aún tiene esa dependencia como principal, o hay oficinas hijas, documentos o referencias externas, el retiro se bloquea; resolver su asignación o conservar el área, sin borrar historia. También se detiene si un objeto rastreado fue modificado incompatiblemente desde su carga.

No emplea TRUNCATE ni borrado CASCADE. Las validaciones y eliminaciones están en una transacción con bloqueos; cualquier error previo a la confirmación revierte el retiro. No proporciona una limpieza de documentos, vaciado global ni restauración automática del respaldo. Una restauración requiere otro procedimiento sobre una base nueva o una recuperación administrada.

## Pruebas y límites

Se probaron offline validaciones, rechazo de usuarios no verificados, duplicados, límites de campos, confirmaciones, bloqueo ante fallo de respaldo y conservación de claves existentes. Se verificaron tablas y columnas contra el modelo incluido, así como sintaxis SQL y PL/pgSQL. No se conectó a tus bases durante la elaboración de estos utilitarios y no se ha probado una carga/retirada real en PostgreSQL. La primera ejecución supervisada por ti en el laboratorio es necesaria antes de usarlos con datos de calidad o producción.

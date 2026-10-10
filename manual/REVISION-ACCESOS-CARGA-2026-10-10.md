# Revisión del alcance documental de la carga UTI

Fecha: 2026-10-10, Lima. Área: UTI `00217`.

## Resultado de la revisión

Se revisaron el CSV preparado, `area.json`, el CSV conservado de la carga
confirmada, el resultado de esa ejecución, el preparador, el validador y el
cargador. Se consultaron solamente los permisos actuales y la procedencia
de los nueve usuarios seleccionados, mediante una transacción READ ONLY.
No se ejecutó una carga ni un UPDATE en PostgreSQL.

- Los nueve seleccionados tienen `perfil=USUARIO_AREA`, tanto en el CSV actual
  como en el conservado de la carga confirmada del 2026-10-09.
- Ese perfil no declara un permiso documental de toda la dependencia.
- La carga confirmó los nueve usuarios. En la base, los nueve tienen
  `TDTX_CONFIG_EMP.TI_ACCESO=0`, dependencia `00217` y procedencia del cargador.
- El origen está en `gestion_data.py`: el valor anterior era `1` para
  administradores y `0` para todos los demás. El permiso documental se derivó
  del indicador de administrador, y ese indicador no define el alcance deseado.
- La bandeja de emisión filtra por dependencia y por el alcance de la sesión:
  `1` filtra por responsable/elaborador, `2` por responsable o emisor y `0`
  permite el listado de la dependencia. El controlador toma estos valores de
  la sesión, por lo que el primer ingreso no produce una bandeja vacía si hay
  documentos anteriores del área y el usuario tiene acceso de dependencia.
- `IN_CONSULTA` ya vale `1` en los nueve registros. Esta corrección del cargador
  conserva ese campo y los demás permisos; el hallazgo de la pantalla se refiere
  al alcance de emisión administrativa. No certifica la autorización de todos
  los endpoints ni sustituye una revisión integral del SGD.

La carga es incorrecta en el alcance documental otorgado a esos usuarios. La
revisión no encontró que este problema proceda de identidades equivocadas en el
CSV ni demuestra que deba repetirse la creación de personas o cuentas.

## Corrección preparada en los utilitarios

- `gestion_data.py` traduce un campo independiente `acceso_documentos` del
  perfil: `personal -> 1`, `personal_y_emisor -> 2`, `dependencia -> 0`.
- Si el campo falta, el nuevo cargador usa acceso personal, incluso para un
  administrador. Rechaza valores desconocidos; no concede acceso amplio ante
  errores de configuración.
- `bd/piloto-area.json.ejemplo` declara acceso personal en sus tres perfiles.
  El preparador utiliza esa plantilla y explica el alcance en su LEEME.
- `validar_carga.py` muestra el alcance y código generado por perfil utilizado.
- Los perfiles con acceso a documentos ajenos del área deben aprobarse
  explícitamente y asignarse a las personas correspondientes. Ser titular,
  tener un nombre de usuario de soporte o ser administrador no concede esa
  excepción de forma automática.

Los archivos privados originales y la evidencia histórica de carga se
conservaron. No se modificaron el CSV aprobado ni su `area.json` original.
La evidencia de lectura y la propuesta individual quedan en
`output/revision-carga-accesos-20261010`, fuera de la selección para Git.

## Recarga implementada para usuarios existentes

Se implementó posteriormente `recargar_usuarios.py`, invocado por
`Recargar-Usuarios.bat` y `Recargar-Usuarios.sh`. Vuelve a leer los CSV y perfiles
aprobados y recarga únicamente el alcance documental de las cuentas existentes.
Admite varias oficinas en una transacción, conserva el historial y respalda la
base antes de escribir. Los comandos y las 18 pruebas adicionales, incluidas 13
pruebas PostgreSQL y la recarga de 50 oficinas ficticias, se explican en
`RECARGA-USUARIOS.md`. La base activa todavía no recibió esa recarga.

El diagnóstico y los requisitos identificados antes de esa implementación:

1. Revisar la propuesta de acceso personal de los nueve usuarios. Separar
   cualquier excepción que el responsable autorice para ver documentos del
   área, mediante un perfil explícito. El borrador no presume excepciones.
2. Preparar una operación específica que actualice únicamente `TI_ACCESO` en
   `TDTX_CONFIG_EMP` para los pares empleado/dependencia aprobados. Comprobar
   identidad, valor previo y número de filas; respaldar los valores anteriores
   y terminar la transacción exitosa en COMMIT. No recrear cuentas ni cambiar
   contraseñas, documentos, destinos o correlativos.
3. Revisar también `piloto_objetos`, que conserva los valores registrados por
   el cargador. Una corrección manual debe considerar ese rastreo para que una
   retirada posterior no encuentre valores incompatibles. Conservar el historial
   de lo cargado y de lo corregido; no alterar la evidencia original para simular
   que la carga histórica fue correcta.
4. Aplicar la recarga únicamente con autorización específica de escritura
   sobre la base y con un respaldo comprobado. El utilitario genera el SQL y
   lo aplica tras esa comprobación; **los nueve permisos activos siguen
   pendientes de recarga**.
5. Después del cambio, cerrar sesión y volver a ingresar: los permisos se
   cargan en la sesión. Con el usuario personal comprobar que no se liste el
   borrador de otra persona; con el perfil autorizado de dependencia comprobar
   que se mantiene el acceso aprobado. No borrar el borrador para ocultarlo.

No repetir la carga inicial UTI ni ejecutar Limpiar-BD para resolver este
incidente. Usar la recarga específica: el cargador inicial protege los objetos
existentes y repetir una carga antigua no actualiza sus permisos.
Los cambios Python de esta revisión no requieren recompilar Java, pero deben
distribuirse junto con su plantilla y validador para las cargas futuras.

## Validación realizada

39 pruebas offline aprobadas: 17 del cargador, 10 del validador y 12 del
preparador. Incluyen acceso personal sin campo y con administrador, los tres
alcances explícitos, rechazo de valores inválidos, cambio de huella cuando se
edita el alcance y presentación del permiso en el informe. Las pruebas generan
SQL con datos ficticios y comprueban que las rutas offline no llaman a la base,
al respaldo ni al cifrado. No se ejecutaron pruebas de carga en la base activa.

```powershell
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_gestion_data.py -v
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_validar_carga.py -v
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p test_preparar_data.py -v
git diff --check
```

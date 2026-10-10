# Recarga de accesos documentales y carga de próximas oficinas

## Qué quedó corregido y dónde

`gestion_data.py` es el cargador utilizado por `Cargar-Area.bat` y
`Cargar-Area.sh`. Su función `document_access` ahora obtiene el alcance del campo
`acceso_documentos` del perfil en `area.json`, en vez de deducirlo del indicador
de administrador. La plantilla `bd/piloto-area.json.ejemplo` y el preparador
declaran acceso personal inicialmente. `validar_carga.py` muestra el alcance de
cada perfil para revisarlo antes de cargar.

| Campo del perfil | Código que se carga | Alcance en emisión administrativa |
| --- | --- | --- |
| `acceso_documentos: "personal"` | `TI_ACCESO=1` | Documentos del trabajador como responsable/elaborador. Es el valor usado si el campo falta. |
| `acceso_documentos: "personal_y_emisor"` | `TI_ACCESO=2` | Documentos de los que es responsable/elaborador o emisor. |
| `acceso_documentos: "dependencia"` | `TI_ACCESO=0` | Documentos de los trabajadores de la dependencia autorizada. Requiere un perfil explícito aprobado. |

El error anterior afectó a los nueve usuarios `USUARIO_AREA` de UTI: cargó `0`
cuando correspondía el acceso personal definido por el operador. Actualizar las
fuentes no cambia los registros ya cargados. La recarga descrita aquí vuelve a
leer los usuarios y perfiles aprobados y actualiza **solo su alcance documental**.
Conserva cuentas, contraseñas, cargos, menús, otros permisos, oficinas y documentos.
No usa Limpiar-BD, ni recrea usuarios, ni repite los SQL de instalación.

## Recargar la oficina UTI ya existente

Descargar los utilitarios corregidos en la instalación que administra la base.
Conservar los archivos privados y los runtimes de ese destino. Usar los archivos
revisados `datos/carga-uti-preparada/area.json` y `usuarios-verificados.csv`.
El CSV mantiene las identidades y la columna `perfil`; el JSON define el alcance
de cada perfil. En esta carga los nueve seleccionados usan `USUARIO_AREA`, que
con el cargador corregido queda personal. Si alguna persona debe ver documentos
de toda UTI, asignarle un perfil separado y aprobado de dependencia antes de
aplicar; el utilitario no presume esa excepción por nombre o puesto.

Primero preparar el plan sin conectar a PostgreSQL:

```powershell
Set-Location C:\SGD\SGD-INIA-INSTALL_PG
.\Recargar-Usuarios.bat --carpeta datos\carga-uti-preparada
if ($LASTEXITCODE -ne 0) { throw 'Revisar los archivos antes de recargar.' }
```

Revisar `revision.json` y `plan.sql` en la carpeta indicada, incluida la base
destino y los alcances. Para aplicar, con PostgreSQL iniciado y durante una
pausa de uso del SGD:

```powershell
.\Recargar-Usuarios.bat --carpeta datos\carga-uti-preparada --aplicar --confirmar RECARGAR:USUARIOS
if ($LASTEXITCODE -ne 0) { throw 'La recarga no terminó correctamente; revisar su log.' }
```

El utilitario crea primero un respaldo completo con SHA-256, comprueba identidad,
procedencia y permisos previos, actualiza el alcance y valida el resultado antes
de COMMIT. Actualiza también el rastreo de los permisos y conserva sus valores
anteriores en `idosgd.piloto_recargas_acceso`. No altera la evidencia de la carga
original. Repetir la recarga con los mismos perfiles ya corregidos produce cero
cambios. Si hay un permiso modificado manualmente que contradice la propuesta,
un usuario distinto, una configuración modificada o una oficina no rastreada,
cancela la transacción en vez de sobrescribirlos.

Una recarga confirmada debe indicar `usuarios_comprobados=9`, `oficinas=1` y el
número real de permisos actualizados. Los usuarios deben cerrar sesión y volver
a ingresar: una sesión abierta puede conservar el permiso anterior. Comprobar
con un usuario personal que no aparezca el borrador ajeno, y con el perfil de
dependencia, si se aprobó uno, que se mantenga su acceso. El procedimiento no
reinicia Payara ni requiere compilar Java.

## Recargar varias oficinas con un solo comando por base

Para oficinas ya cargadas, reunir sus archivos aprobados en subcarpetas directas:

```text
datos/cargas-aprobadas/
  oficina-00217/area.json
  oficina-00217/usuarios-verificados.csv
  oficina-xxxxx/area.json
  oficina-xxxxx/usuarios-verificados.csv
```

Los códigos, usuarios e identidades de cada carpeta deben corresponder al
destino. No incluir preparaciones descartadas o dos versiones de la misma
oficina. `--raiz` lee todas las subcarpetas inmediatas completas y aprobadas:

```powershell
.\Recargar-Usuarios.bat --raiz datos\cargas-aprobadas
if ($LASTEXITCODE -ne 0) { throw 'Revisar el lote.' }

# Después de revisar el plan del lote y coordinar la pausa de uso:
.\Recargar-Usuarios.bat --raiz datos\cargas-aprobadas --aplicar --confirmar RECARGAR:USUARIOS
if ($LASTEXITCODE -ne 0) { throw 'Revisar el resultado de la recarga.' }
```

Usa un respaldo y una transacción para todas las oficinas de esa base; si falla
una, se revierten los cambios de todas. Se probó con 50 oficinas ficticias y 50
asignaciones empleado/dependencia. Una cuenta puede pertenecer a varias oficinas:
el alcance se actualiza por pareja empleado/dependencia, sin duplicar cuentas.
En Linux usar `bash Recargar-Usuarios.sh` con los mismos argumentos.

Cada instalación con base independiente debe ejecutar el utilitario contra su
propio destino; puede incorporarse al procedimiento remoto de actualización
existente, sin visitar físicamente los equipos. Descargar GitHub, compilar o
desplegar WAR no ejecuta la recarga automáticamente. Este utilitario no tiene
acceso remoto ni credenciales de otras instalaciones y no afirma actualizarlas.

## Cómo cargar las siguientes oficinas por primera vez

1. Preparar los archivos de cada oficina con `Preparar-Carga`; completar y
   aprobar sus identidades, sede, titular, perfiles y selección de usuarios.
2. Mantener `USUARIO_AREA` como personal. Para las personas que necesitan ver
   toda la dependencia crear, por ejemplo, `VER_DOCUMENTOS_AREA` con
   `acceso_documentos=dependencia` y asignarlo solo a ellas en el CSV. No convertir
   todo el perfil común a dependencia para habilitar a un jefe.
3. Ejecutar `Validar-Carga` y revisar el alcance de los perfiles en su informe.
4. Aplicar la carga inicial desde `Cargar-Area` para esa oficina nueva. Usar el
   código real aprobado en la confirmación; no el código de otra oficina.

```powershell
.\Validar-Carga.bat --carpeta datos\carga-oficina-preparada
if ($LASTEXITCODE -ne 0) { throw 'No cargar hasta corregir la validación.' }

# Sustituir XXXXX por el código de cinco dígitos aprobado en area.json:
.\Cargar-Area.bat --carpeta datos\carga-oficina-preparada --aplicar --confirmar CARGAR:XXXXX
if ($LASTEXITCODE -ne 0) { throw 'Revisar el resultado de la carga.' }
```

`Cargar-Area` crea las cuentas y relaciones que falten; `Recargar-Usuarios` revisa
cuentas ya cargadas y cambia únicamente el alcance documental. Revisar el acceso
con usuarios de ambos perfiles después de cada carga. No distribuir las claves
de una ejecución fallida. Para las demás operaciones y menús ver
`CARGA-POR-AREA.md`.

## Pruebas y estado

Pasaron 39 pruebas offline del cargador, preparador y validador, 5 de la recarga
offline y 13 de integración PostgreSQL en un cluster temporal propio. Incluyen
conservación de cuenta y contraseña, documento ficticio conservado, repetición,
excepción explícita de dependencia, identidad/base incorrectas, reversión del
lote completo ante conflicto y recarga de 50 oficinas en una transacción.
La actualización del rastreo también permite el retiro posterior de la oficina
en el escenario ficticio autorizado para esa prueba.

En la instalación activa solo se preparó el plan para los nueve usuarios UTI.
**La recarga de PostgreSQL sigue pendiente de ejecutar; los permisos activos no
cambiaron durante estas pruebas.** Los resultados reales, cuando se aplique,
quedan bajo `datos/recarga-usuarios-*` junto con el respaldo y el log privado.

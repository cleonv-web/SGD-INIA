# Revision de Abrir Doc. y Configuracion

Estos scripts estan pendientes de autorizacion y no se han ejecutado.
Desde la instruccion de solicitar permiso no se han hecho consultas ni cambios
en la base. Los datos de alcance provienen de la revision anterior.

## Hallazgos

- Tramitedoc recibe `C:/SGD/SGD-INIA-INSTALL_PG/datos/documentos`.
  Al pasar esa ruta al Explorador se reproduce la apertura de Documentos.
  El directorio correcto usa `C:\SGD\SGD-INIA-INSTALL_PG\datos\documentos`.
- El menu Configuracion del navegador contiene una lista vacia.
  JARBIETO no tenia opciones M01/M0113 asignadas en la revision anterior.
  El perfil USUARIO_AREA de la carga preparada tampoco las incluye.
- La instalacion y la carga generaban rutas con `as_posix()`.
  Ahora estan corregidas en `C:\SGD\SGD-INIA-INSTALL_PG`: usan el formato nativo.

## Correccion general para todos los usuarios

`04_normalizar_rutas_documentos_todos.sql` corrige los tres directorios de todos
los usuarios y oficinas, preservando la carpeta de cada uno. Crea una tabla de
respaldo y sincroniza el registro del cargador si existe. Termina en COMMIT.
`05_revertir_normalizacion_rutas.sql` permite revertirlo usando ese respaldo.
Ambos archivos estan pendientes de autorizacion y no se han ejecutado.

La explicacion completa, validaciones y archivos corregidos se incluyen en
`CORRECCION-RUTAS-DOCUMENTOS.md` y `correccion-rutas-sgd.zip` en esta carpeta.
Los SQL generados antes de la correccion deben regenerarse para futuras cargas.

## Cambios propuestos para aprobar

1. `02_corregir_ruta_JARBIETO.sql`: normaliza solo las rutas de la configuracion
   del empleado 00237 en la oficina 00217. No modifica los otros usuarios.
2. `03_habilitar_configuracion_personal_JARBIETO.sql`: inserta o habilita M01
   y M0113 solo para JARBIETO en el aplicativo 9. No asigna administrador.

`01_revision_sin_cambios.sql` consulta el estado previo; tambien requiere permiso
si lo va a ejecutar Codex. Los otros dos scripts incluyen comprobaciones de
base, identidad y catalogo, resultados ANTES/DESPUES y una transaccion.

Todos terminan en COMMIT. El script 01 solo consulta. Ejecutar completos los
scripts 02 y 03 guarda sus cambios si todas las instrucciones terminan sin error.
Codex no ejecutara ninguno sin aprobacion especifica del usuario. Si ocurre un
error, PostgreSQL aborta la transaccion y no guarda cambios; ejecutar ROLLBACK
para cerrarla antes de intentar nuevamente. Conservar los resultados ANTES
para disponer de los valores originales necesarios para una reversion.

No ejecutar fragmentos sueltos fuera de la transaccion. La validacion realizada
es una revision del texto contra el esquema SQL local; no se probo en la base.

Despues de una aplicacion aprobada, cerrar e iniciar sesion en el SGD para
recargar rutas y opciones. Permitir que Tramitedoc se reconecte y comprobar
Configuracion personal y Abrir Doc.

## pgAdmin

Cliente grafico elegido: pgAdmin 4, distribuido por el proyecto PostgreSQL.
Servidor existente: 127.0.0.1, puerto 55432, base idosgd_inia.
El archivo `servidor-pgadmin.json` permite importar la definicion del servidor.
No contiene contrasena ni conecta automaticamente. La instalacion del editor
no reinstala ni cambia PostgreSQL. Abrir los SQL en Query Tool no los ejecuta;
el boton Ejecutar si los envia a la base.

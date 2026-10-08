# Revisión para la carga inicial de SGD INIA

Fecha: 2026-10-08. Origen revisado: contenedor `std-sqlserver`, base SQL Server `bd_tramite_dev`. Destino: PostgreSQL de `C:/SGD/SGD-INIA-INSTALL_PG`.

Se consultaron metadatos, índices, relaciones, conteos y muestras de oficinas/ubicaciones. No se modificaron datos ni se ejecutó una carga. No se incluyen nombres de trabajadores, documentos de identidad, usuarios personales, contraseñas o hashes en este informe.

## Inventario

| Tabla | Total | Estado 1 | Estado 0 |
|---|---:|---:|---:|
| Tra_M_Trabajadores | 1403 | 1123 | 280 |
| Tra_M_Oficinas | 765 | 641 | 124 |
| Tra_M_Perfil | 18 | No aplica | No aplica |
| Tra_M_Ubicacion_Oficina | 28 | 28 | 0 |
| Tra_M_Perfil_Ususario | 2358 asignaciones | No contiene estado | No contiene estado |

La interpretación funcional de estado 1 como activo se debe confirmar con el sistema origen. La quinta tabla está escrita `Ususario` en la base y contiene trabajador, oficina y perfil; es necesaria para preservar asignaciones múltiples. `Tra_M_Menu` existe, pero está vacía.

## Mapeo propuesto, pendiente de aprobación funcional

- Trabajadores -> RHTM_PER_EMPLEADOS, SEG_USUARIOS1 y catálogo RHTM_CARGOS.
- Oficinas -> RHTM_DEPENDENCIA. `idPadre` permite reconstruir la jerarquía; no se detectaron ciclos ni padres inexistentes salvo el marcador cero. La profundidad máxima observada es cinco enlaces. Hay 110 oficinas con estado 1 sin padre o con padre cero; debe definirse su relación con el organigrama institucional.
- Ubicaciones -> SI_MAE_LOCAL; `iCodUbicacion` en oficinas -> SITM_LOCAL_DEPENDENCIA. Las muestras corresponden a sede central y estaciones experimentales. Distrito no sustituye una dirección o un ubigeo validado.
- Perfil_Ususario + Perfil -> SEG_USER_APLICA, SEG_USER_APLICA_OPC, TDTR_PERMISOS, TDTX_DEPENDENCIA_EMPLEADO y TDTX_CONFIG_EMP. La equivalencia entre perfiles y permisos del SGD requiere una matriz funcional; no copiar permisos amplios de los ficticios.
- Configurar responsables/titulares, mesa de partes y tipos documentales por dependencia. Un perfil DIRECTOR o JEFE no identifica por sí solo al titular vigente.

## Inconsistencias observadas

1. Trabajadores: 123 referencias a oficinas inexistentes, incluyendo 104 con oficina cero. En estado 1 hay 96 referencias inexistentes y 33 referencias a oficinas con estado 0.
2. Perfil principal: 1308 trabajadores tienen perfil cero, de ellos 1028 con estado 1. No resolver los permisos usando solamente este campo: consultar la tabla de asignaciones.
3. Asignaciones: 1 referencia a trabajador inexistente, 23 a oficinas inexistentes y 3 a perfil cero/inexistente. Estos conteos pueden solaparse.
4. Hay 10 combinaciones trabajador/oficina/perfil repetidas. No confundir asignaciones múltiples legítimas con duplicados de la misma combinación.
5. Hay 175 trabajadores con estado 1 sin filas de asignación y 193 sin una asignación que tenga perfil existente y oficina con estado 1. Los 175 están incluidos en los 193; no sumar ambas cifras.
6. Los 1403 documentos de identidad tienen el patrón dos letras, guion y seis dígitos; no corresponden al formato DNI de ocho dígitos del destino. Tipo de documento es NULL en todos. Confirmar si se trata de una copia anonimizada; no inventar DNI ni recortar valores para forzar su carga.
7. Existe una sigla de oficina con estado 1 mayor de 50 caracteres, límite de DE_SIGLA en el destino. Definir una abreviatura aprobada, sin truncamiento silencioso.
8. No se detectaron usuarios duplicados al normalizar mayúsculas y espacios entre registros con estado 1; todos sus nombres de usuario caben en los 20 caracteres del destino y no coinciden con los cuatro usuarios de laboratorio.
9. Apellidos están en un solo campo; el SGD usa apellido paterno y materno separados. Definir su tratamiento y validar apellidos compuestos.
10. Hay contraseña legacy en los 1403 registros y hash en un registro. Su existencia no demuestra compatibilidad con SGD. No se leyeron sus valores para este informe; definir altas con claves generadas o integración institucional.

## Condiciones para la ejecución

- Confirmar que esta copia contiene los datos institucionales que se desea cargar, especialmente identidad, cargos y responsables.
- Elegir alcance: personal/oficinas vigentes y casos excepcionales que deben conservarse. No habilitar cuentas sin asignaciones y permisos resueltos.
- Mantener una correspondencia explícita de IDs origen/destino y convertir estados, sin truncar códigos ni textos. Los IDs actuales caben en códigos de cinco caracteres de empleados y dependencias; verificar colisiones con el destino antes de usar equivalencias.
- Conservar el administrador temporal hasta validar un administrador institucional. Resolver el local 001 ya utilizado por el laboratorio; no insertar una segunda fila con ese código sin una decisión de equivalencia.
- Respaldar PostgreSQL antes de cargar. Preparar una validación previa y una carga consolidada transaccional que se detenga ante cualquier error, con conteos y reporte de exclusiones; impedir duplicaciones en una segunda ejecución.
- No ejecutar de nuevo los SQL de instalación inicial sobre la base existente.
- Adaptar bd/03-validar.sql y la verificación del instalador, que actualmente exigen los cuatro usuarios ficticios. Validar identidades institucionales antes de desactivar ficticios.
- Probar acceso y permisos con administrador, mesa de partes, responsable y trabajador de área; luego probar registro, derivación, recepción y seguimiento de un documento.
- Estos cinco conjuntos cubren maestros y accesos. Expedientes, movimientos, firmas y archivos históricos requieren otra migración.

Conclusión: la carga inicial es viable, pero no está lista para ejecutarse automáticamente con estos datos sin resolver identidades, asignaciones y equivalencias de permisos. No se ha declarado ni probado una importación real.

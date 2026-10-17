# Retiro de la derivación a especialista del piloto

Fecha: 17/10/2026. Referencia original de Git: `6432f620ab93982d8058b57f1910b6dc3e0f1d25`.

Se restauraron los archivos originales de Java, JavaScript, vistas, instalador,
carga de usuarios, limpiador, pruebas y documentación. Se retiraron los archivos
adicionales de la funcionalidad descartada, incluido su actualizador y su SQL de
activación. El SGD se recompiló desde las fuentes restauradas.

Se conserva el cambio independiente que incorpora `232 — PROVEIDO` a
`datos/carga-uti-preparada/area.json`. No se cambiaron las reglas originales de
emisión, firma ni los permisos de PROVEÍDO.

## Base existente del piloto

Se respaldó la base completa antes de intervenir y se ensayó el retiro en un
PostgreSQL temporal restaurado desde ese respaldo. El SQL reproducible está en
`bd/mantenimiento/retirar-derivacion-secretaria-piloto.sql`.

El retiro se ejecutó con Payara detenido, dentro de una transacción y sin
`CASCADE`. Se eliminaron exclusivamente:

- Las tablas `tdtr_permiso_derivar` y `tdtv_derivacion_secretaria`.
- Las cinco funciones `sgd_puede_derivar`, `sgd_preparar_derivacion`,
  `sgd_finalizar_derivacion`, `sgd_referencia_guard_derivacion` y
  `sgd_proteger_derivacion`.
- Los cuatro triggers agregados para la funcionalidad.
- El tipo `990 — DERIVACION INTERNA`, su documento de prueba y sus relaciones.

Se conservaron los cuatro documentos normales, las 18 cuentas, sus claves,
los catálogos originales y la auditoría general. Las huellas de los documentos
normales, usuarios y funciones originales coincidieron antes y después. No
quedaron referencias documentales huérfanas.

El respaldo y las evidencias locales están en
`logs/reversion-derivacion-20261017T191928Z/`; no se versionan por contener datos
del piloto. Los resultados adicionales están en `logs/reversion-*.log`,
`logs/reversion-*.txt` y `logs/reversion-verificacion-final-base.json`.

## Instalación nueva desde el repositorio

Se mantiene el procedimiento original: clonar el repositorio y ejecutar
`Instalar-SGD.bat` o `bash Instalar-SGD.sh`. El instalador restaurado no crea
los objetos retirados ni requiere opciones nuevas en los archivos de carga.
El SQL de mantenimiento no forma parte de la instalación y no debe ejecutarse
en una base nueva. Tampoco ejecuta una limpieza global del piloto.

## Validación

- Compilación Maven completa del SGD: correcta; seis pruebas Java aprobadas.
- Regresión original: 200 pruebas Python, 199 aprobadas y una omitida por
  requerir Docker.
- Retiro en PostgreSQL aislado: cuatro pruebas aprobadas. Verifican conservación
  de datos, ejecución repetida, reversión completa ante una dependencia no
  prevista y compatibilidad del limpiador original.
- WAR recompilado: sin clases ni recursos de la funcionalidad retirada.
- Despliegue final: SGD, mesa virtual, consulta y verificador responden HTTP 200;
  el JavaScript y la vista de recepción coinciden con los originales. El recurso
  JavaScript de la funcionalidad retirada responde HTTP 404.

Las cuatro pruebas de retiro requieren un respaldo elegido explícitamente:
`SGD_PROBAR_POSTGRESQL_AISLADO=1` y `SGD_RESPALDO_REVERSION=<ruta al dump>`.
Restauran ese respaldo solamente en bases temporales de ensayo.

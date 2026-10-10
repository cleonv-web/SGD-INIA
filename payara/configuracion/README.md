# Configuracion del dominio Payara piloto

Esta carpeta conserva la configuracion del dominio `sgd` capturada el
2026-10-10, incluyendo credenciales y certificados del dominio por autorizacion
expresa del operador. El acceso JDBC conserva `${ENV=SGD_DB_PASSWORD}`: la clave
PostgreSQL se obtiene de `datos/credenciales.json` mediante el lanzador existente.
Esa carpeta de datos no forma parte de esta copia.

`dominio/config/domain.xml` conserva JVM, puertos, JDBC/JNDI, servicios, seguridad
y monitoreo. Tambien se conservan las propiedades activas de las aplicaciones,
los archivos de seguridad y certificados, el driver JDBC y el favicon del
dominio. `MANIFIESTO.json` registra SHA256, versiones y opciones JVM. Git conserva
los bytes exactos mediante `.gitattributes`.

Sobre la captura se ajusto a **4096 MB** el heap de `server-config`. Se reinicio
exclusivamente Payara y se verifico `-Xmx4096m` en el nuevo proceso (PID 3696).
`default-config` conserva **512 MB** y las demas opciones JVM no cambian.
El manifiesto identifica el ajuste preparado y sus hashes; su fecha de captura
original se conserva. Activacion, respaldo y reversion se describen en
`manual/AJUSTE-MEMORIA-PAYARA.md`. No incluye PID, locks, logs, caches, temporales,
aplicaciones desplegadas, documentos ni PostgreSQL. Las aplicaciones se
construyen desde las fuentes y se despliegan con los utilitarios existentes.

## Verificar sin conectar a bases ni arrancar servidores

Desde la raiz del proyecto, en PowerShell:

```powershell
& .\herramientas\python-windows\python.exe .\herramientas\configuracion_payara.py verificar
```

Para capturar futuros ajustes en otra carpeta y comparar antes de reemplazar:

```powershell
& .\herramientas\python-windows\python.exe .\herramientas\configuracion_payara.py exportar --destino .\output\payara-config-nueva
```

La captura verifica los hashes de la copia. Debe realizarse mientras no se estan
modificando los archivos de configuracion. No es un respaldo de transacciones o
de la base de datos.

## Restaurar configuracion

Primero preparar el motor Payara **5.2022.5** y Java **Temurin 8u504**, conservados
en el portable. El ZIP Payara queda fuera de Git; su SHA256 aparece en el
manifiesto. Puede adjuntarse como artefacto de una release. No sustituirlo por una
version mayor sin una prueba de compatibilidad.

Detener Payara, comprobar que no hay proceso del dominio en ejecucion y revisar
host, puertos, rutas, URLs y certificados si el destino es otro equipo. Esta
copia reproduce el piloto actual; no adapta automaticamente Windows a Linux ni
los valores de red a otra instalacion. El utilitario rechaza un destino con
`config/pid`, incluso si es antiguo. No borrar ese archivo sin verificar antes
que el servidor esta detenido.

```powershell
& .\herramientas\python-windows\python.exe .\herramientas\configuracion_payara.py restaurar --confirmar PAYARA:DOMINIO
```

La restauracion verifica primero todos los hashes y respalda los archivos que
reemplaza en una carpeta unica bajo `datos/respaldo-payara-config-*`. No elimina
otros archivos del destino, no arranca servicios y no conecta a ninguna base.
No ejecutarla en el servidor actual solo para subir la configuracion a Git.

Los lanzadores actuales `iniciar`, `configurar` y `restaurar` de `sgd.py` pueden
regenerar propiedades o realizar operaciones de base de datos. `configurar` ahora
conserva el heap existente, salvo un `heap_payara_mb` explicito en la configuracion
local (1024, 2048 o 4096). No usarlos como verificacion offline de esta copia. La autorizacion
para versionar Payara no autoriza ejecutar esos comandos contra la base.

## Subir al repositorio

Los comandos de Git estan en `manual/SUBIR-CONFIGURACION-PAYARA.ps1`. Incluyen solo
los archivos preparados para esta tarea, no otros archivos pendientes como
incidencias, instaladores o salidas. El script valida la copia antes de agregarla
y rechaza un indice que ya tenga cambios preparados. Hace un commit; publica
unicamente si se pasa `-Publicar`.

## Validacion realizada

Pasaron ocho pruebas offline: preservacion exacta de archivos privados,
exclusion de PID/logs, deteccion de modificaciones y archivos extra, proteccion
de rutas, rechazo de sobrescritura de la copia, confirmacion y parada requeridas
para restaurar, respaldo del destino y conservacion de archivos ajenos. La copia
real contiene 33 archivos verificados por SHA256. No se restauro la copia completa sobre el dominio activo. El ajuste de heap a
4096 MB se activo con respaldo y reinicio exclusivo de Payara. No se ejecutaron
scripts SQL ni comandos de PostgreSQL; el arranque de las aplicaciones mantiene
su actividad normal con la base.

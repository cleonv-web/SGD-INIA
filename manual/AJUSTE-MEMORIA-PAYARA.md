# Heap Payara: cambio de 1024 a 4096 MB

Fecha de revision: 2026-10-10, Lima. Repositorio revisado:
`C:\SGD\SGD-INIA-INSTALL_PG`, rama `base-sgd-inia`, base `1240980`.
Los archivos ajenos al ajuste se conservaron y no se ejecutaron commit ni push.

## Estado y alcance

La copia versionada tiene `-Xmx4096m` exclusivamente en `server-config`.
`default-config` conserva `-Xmx512m`. El XML conserva byte a byte todos los demas
valores, incluidas las opciones JVM, JDBC y certificados. El manifiesto verifica
los 33 archivos y distingue el ajuste preparado de una activacion verificada.
Tras la autorizacion de ampliar memoria, se reinicio exclusivamente Payara.
El proceso nuevo PID `3696` tiene `-Xmx4096m`; el puerto HTTP 8085 pertenece a ese
proceso. PostgreSQL conserva su proceso PID `6896` y su hora de inicio.

La inspeccion previa a la ampliacion encontro aproximadamente 13,3 GiB disponibles de 31,4 GiB
visibles para Windows (equipo de 32 GB). Es una lectura puntual. El heap es el
limite maximo de memoria de objetos Java; el proceso utiliza memoria adicional.
Este ajuste proporciona margen y no corrige la retencion de reportes observada
ni demuestra una cantidad de usuarios concurrentes.

No se modificaron cache de reportes, lectura de archivos, pool JDBC, PostgreSQL,
plantillas ni funcionalidades. No se recompilo Java ni se ejecuto SQL. Sigue
pendiente confirmar con el operador si aplico manualmente las correcciones SQL
04 y 06; esta etapa no comprueba ni depende de su aplicacion.

## Instalador y herramienta offline

`sgd.py:configurar()` utiliza un auxiliar puro y conserva la opcion `-Xmx`
existente. Opcionalmente se puede agregar `"heap_payara_mb": 4096`, `2048` o `1024`
como entero en `configuracion.json`; una eleccion explicita tiene prioridad.
Si falta por completo la opcion `-Xmx`, el auxiliar usa 1024 MB por defecto.
Rechaza valores fuera de este alcance y opciones `-Xmx` duplicadas.
No ejecutar `configurar()` para verificarlo: tambien realiza operaciones de BD.

`herramientas/memoria_payara.py preparar --mb 4096` modifica solo la copia
versionada y actualiza su manifiesto tras validar todos los hashes. Respalda
`domain.xml` y el manifiesto anterior bajo `datos/respaldo-heap-payara-*`, fuera de
Git. Restaura ambos archivos si falla la actualizacion. No toma una nueva copia
de todo el dominio y no sobrescribe otros archivos del operador.

`aplicar --mb 4096` cambia exclusivamente el heap del dominio detenido, con un
respaldo separado del XML anterior. Rechaza un proceso Java del dominio o un
`config/pid`, incluso antiguo, y comprueba de nuevo la parada antes de escribir.
No borra el PID automaticamente. No ejecutar otro lanzador mientras se aplica.

## Activar con una breve interrupcion

Desde PowerShell, el siguiente comando solo inspecciona archivos y procesos:

```powershell
Set-Location -LiteralPath 'C:\SGD\SGD-INIA-INSTALL_PG'
& .\manual\ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096
```

Para activar durante una ventana breve sin usuarios trabajando:

```powershell
& .\manual\ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096 -Activar
```

El script lee la credencial existente sin imprimirla ni regenerarla, usa el
Java incluido y llama al CLI de Payara para `stop-domain` y `start-domain` con
`--domaindir` explicito. Entre ambos aplica solo el heap, con respaldo. Verifica
que el unico proceso del dominio reiniciado tenga exactamente `-Xmx4096m` antes
de afirmar que esta activo. Restaura las variables de entorno del shell al salir.
La gestion de dominios mediante CLI y `--domaindir` esta descrita en la
[documentacion de Payara](https://docs.payara.fish/community/docs/5.201/documentation/user-guides/upgrade-payara.html).

El script no llama a `SGD.bat`, `sgd.py`, comandos PostgreSQL, diagnosticos JDBC
ni scripts SQL. PostgreSQL permanece iniciado. Al arrancar, las aplicaciones
desplegadas pueden reanudar su actividad normal con la base; no se garantiza que
la aplicacion permanezca sin consultas. La comprobacion del argumento JVM no
equivale a una validacion funcional del SGD.

Si falla la parada, no escribe el XML. Si falla el cambio o el inicio, puede
quedar Payara detenido: revisar el mensaje, el respaldo y el log privado indicado
antes de repetir. No arrancar otro lanzador simultaneamente. No se fuerzan
terminaciones ni se eliminan archivos PID. Se ejecuto con `-HeapMB 4096 -Activar` para activar la ampliacion autorizada.
No se publico ni se hizo commit.

## Revertir a 1024 o 2048 MB

Si existe `heap_payara_mb` explicito, ajustar primero ese campo a `1024`; el
script rechaza una contradiccion con el valor solicitado. Ejecutar:

```powershell
& .\manual\ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 1024 -Activar
```

Aplica la misma parada, respaldo e inicio y verifica `-Xmx1024m` en el proceso.
Para que la copia versionada tambien refleje esa eleccion:

```powershell
& .\herramientas\python-windows\python.exe .\herramientas\memoria_payara.py preparar --mb 1024
& .\herramientas\python-windows\python.exe .\herramientas\configuracion_payara.py verificar
```

El respaldo completo del XML anterior es evidencia y recuperacion adicional;
no restaurarlo indiscriminadamente sobre un dominio que haya cambiado despues.
La reversion por heap preserva los demas cambios posteriores del XML.
Tambien se puede volver a 2048 MB usando `-HeapMB 2048 -Activar` y
`preparar --mb 2048`; si hay una eleccion local explicita, debe coincidir.

## Verificacion offline y Git

```powershell
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p 'test_memoria_payara.py' -v
& .\herramientas\python-windows\python.exe -m unittest discover -s tests -p 'test_configuracion_payara.py' -v
& .\herramientas\python-windows\python.exe .\herramientas\configuracion_payara.py verificar
git diff --check
```

No ejecutar toda la suite indiscriminadamente: existen pruebas de integracion de
PostgreSQL fuera del alcance autorizado. Las pruebas aqui usan dominios y XML
temporales, verifican preservacion exacta, reversion, hashes, respaldo, fallo de
escritura y rechazo de cambios con Payara activo. El script PowerShell se reviso por sintaxis, en modo de inspeccion y en la
activacion real autorizada de 4096 MB. Resultados detallados: `manual/RESULTADOS-MEMORIA-PAYARA-2026-10-10.md`.

Revisar el indice existente antes de preparar los archivos. Estos comandos
incluyen exclusivamente los nueve archivos del ajuste:

```powershell
git diff --cached --stat
git add -- sgd.py herramientas/memoria_payara.py tests/test_memoria_payara.py payara/configuracion/dominio/config/domain.xml payara/configuracion/MANIFIESTO.json payara/configuracion/README.md manual/ACTIVAR-MEMORIA-PAYARA.ps1 manual/AJUSTE-MEMORIA-PAYARA.md manual/RESULTADOS-MEMORIA-PAYARA-2026-10-10.md
git diff --cached --check
git diff --cached --stat
git commit -m 'Preparar heap Payara de 4 GB y conservar eleccion en instalador'
git push -u origin base-sgd-inia
```

Se incluyen instalador, herramienta, pruebas, XML, manifiesto, README, activacion,
guia y resultados. No agregar `datos`, `output`,
incidencias ni instaladores ajenos. El commit y el push quedan a cargo del
operador; revisar tambien si ya habia cambios preparados de otra tarea.

## Utilitarios: que hace cada uno y cuando usarlo

Este apartado describe los utilitarios relacionados con memoria, configuracion
de Payara, compilacion, despliegue y validacion. Todos los comandos se ejecutan
en PowerShell desde la raiz de la instalacion destino. La activacion de memoria
esta preparada para Windows, el portable Payara 5.2022.5, Java Temurin 8u504 de
64 bits y el dominio `sgd`; no es un procedimiento directo para Linux ni para
un Payara instalado con otra estructura.

### Ajustar o replicar la memoria

| Utilitario y modo | Que hace | Cuando usarlo |
| --- | --- | --- |
| `manual/ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096` | Inspecciona archivos requeridos, la opcion de memoria y los procesos del dominio. No modifica archivos ni reinicia servicios. | Antes de activar el ajuste en cada instalacion Windows compatible. |
| `manual/ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096 -Activar` | Detiene solamente Payara, respalda `domain.xml`, cambia solo el maximo de memoria de `server-config`, inicia Payara y verifica `-Xmx4096m` en el proceso. No detiene PostgreSQL ni ejecuta scripts SQL; las aplicaciones arrancadas pueden acceder normalmente a la base. | Para activar los 4 GB en este servidor o replicarlos en otro, durante una pausa de uso del SGD. No requiere recompilar. |
| El mismo script con `-HeapMB 2048 -Activar` o `-HeapMB 1024 -Activar` | Aplica la misma secuencia de respaldo, parada, cambio, inicio y verificacion, con 2 GB o 1 GB. | Para reducir o revertir la memoria. Si existe `heap_payara_mb` en `configuracion.json`, debe coincidir con el valor solicitado. |
| `herramientas/memoria_payara.py preparar --mb 4096` | Ajusta solo la copia versionada en `payara/configuracion`, actualiza sus hashes y respalda el XML y manifiesto anteriores. No cambia el dominio activo ni reinicia servicios. | Para preparar en Git un nuevo valor de memoria; no basta para activarlo en un servidor. |
| `herramientas/memoria_payara.py aplicar --mb 4096` | Respalda y cambia solo la memoria del `domain.xml` de la instalacion destino. Exige Payara detenido y ausencia de `config/pid`; no inicia servicios. | Para una operacion manual con parada e inicio gestionados por el operador. Para el flujo completo usar el script PowerShell con `-Activar`, que llama a este auxiliar. |

Los auxiliares aceptan 1024, 2048 y 4096 MB. Estos valores son el alcance
implementado en los utilitarios, no un limite general de memoria de Payara.
El heap limita los objetos Java; el proceso tambien utiliza memoria adicional.
Los respaldos quedan bajo `datos/respaldo-heap-payara-*` y el script de activacion
guarda su log bajo `logs/heap-payara-*.log`.

Para replicar **solo la memoria** en una instalacion Windows ya preparada:

1. Descargar la rama `base-sgd-inia` y actualizar los archivos versionados del
   proyecto. Conservar `configuracion.json`, `datos/credenciales.json` y la
   configuracion propia del servidor destino. Verificar que tenga memoria
   disponible y los componentes requeridos por el script.
2. Ejecutar la inspeccion y, si termina correctamente, activar durante una pausa
   de uso. Ajustar la ruta si la instalacion esta en otra carpeta:

```powershell
Set-Location -LiteralPath 'C:\SGD\SGD-INIA-INSTALL_PG'
$ErrorActionPreference = 'Stop'
& .\manual\ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096
& .\manual\ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096 -Activar
```

Comprobar el mensaje `Heap activo verificado: -Xmx4096m; PID ...`. Descargar el
repositorio o copiar la configuracion versionada no activa el cambio. La
verificacion del proceso registrada en el manifiesto corresponde al piloto;
no demuestra que otro servidor ya este usando ese valor.

### Conservar y recuperar la configuracion de Payara

Los modos de `configuracion_payara.py` se invocan con el Python incluido:
`& .\herramientas\python-windows\python.exe .\herramientas\configuracion_payara.py ...`.
Los modos de `memoria_payara.py` de la tabla anterior se invocan de la misma forma.

| Utilitario y modo | Que hace | Cuando usarlo |
| --- | --- | --- |
| `herramientas/configuracion_payara.py verificar` | Comprueba el inventario, los hashes y el XML de la copia versionada. No consulta la base ni arranca servicios. | Antes de subir o restaurar la copia, o para comprobar que los archivos descargados mantienen su integridad. No verifica el heap del proceso activo. |
| `herramientas/configuracion_payara.py exportar --destino .\output\payara-config-nueva` | Captura los archivos admitidos del dominio en una carpeta nueva y genera su manifiesto. Excluye aplicaciones desplegadas, PID, logs y temporales. | Para conservar futuros ajustes de configuracion y compararlos antes de versionarlos. No usar una carpeta destino ya ocupada. |
| `herramientas/configuracion_payara.py restaurar --confirmar PAYARA:DOMINIO` | Verifica la copia, respalda los archivos que reemplazara y restaura los archivos de configuracion incluidos. No inicia servicios ni aplica SQL. | Para recuperar deliberadamente la configuracion completa en un dominio compatible detenido. Comprobar manualmente la parada; el utilitario rechaza `config/pid`, pero no sustituye esa comprobacion. |
| `manual/SUBIR-CONFIGURACION-PAYARA.ps1` | Verifica la copia y crea un commit con su seleccion de archivos. Rechaza un indice Git que ya tenga cambios preparados. Con `-Publicar`, tambien hace push a la rama activa. | Para versionar cambios futuros de la copia completa. Su seleccion no incluye todos los archivos de memoria, cache ni esta guia; para cambios de fuentes o documentacion usar `git add` con los archivos concretos. |

No reemplazar toda la carpeta Payara ni copiar el `domain.xml` del piloto sobre
otro servidor para aplicar solo memoria. La copia completa contiene puertos,
rutas, conexiones, propiedades, credenciales y certificados propios del piloto;
una restauracion completa exige revisar esos valores para el destino. No incluye
PostgreSQL, documentos ni WAR desplegados, y no es un respaldo completo del SGD.

### Compilar, desplegar y validar el SGD

`SGD.bat` es el lanzador de `sgd.py`; la accion escrita despues del nombre
determina que operaciones realiza. Para este trabajo usar siempre una accion
explicita: ejecutarlo sin argumentos llama a `todo`.

| Utilitario y accion | Que hace | Cuando usarlo |
| --- | --- | --- |
| `SGD.bat compilar` | Compila los ocho modulos con fuentes, guarda sus WAR en `binarios` y registra hashes en `logs/compilacion.json`. Omite las pruebas Maven con `-DskipTests`. No despliega ni cambia la memoria de Payara. | Despues de actualizar codigo Java, incluyendo la correccion de localidad y la retencion de plantillas. No es necesario para cambiar solo el heap. |
| `SGD.bat desplegar` | Verifica los hashes de los WAR y reemplaza las aplicaciones habilitadas en Payara; tambien despliega `wstradoc` del proveedor. No aplica el SQL de actualizacion de plantillas. Las aplicaciones pueden consultar la base al arrancar. | Despues de compilar correctamente, con Payara iniciado y una pausa de uso prevista. Revisar todas las correcciones que incorpora la compilacion y conservar los artefactos anteriores para recuperacion. |
| `SGD.bat iniciar` | Puede preparar o consultar PostgreSQL segun el modo, regenerar propiedades de aplicaciones e iniciar Payara. | Para el arranque general de la instalacion. Para activar solo memoria usar el script especifico, que conserva PostgreSQL encendido y evita esta preparacion. |
| `SGD.bat detener` | Detiene Payara y, en el modo portable con base local, tambien PostgreSQL. | Para una parada general. No usarlo si el objetivo es reiniciar exclusivamente Payara. |
| `SGD.bat configurar` | Configura dominio, puertos, propiedades y JDBC, conserva el heap salvo eleccion explicita y ejecuta un UPDATE de rutas en PostgreSQL. | Para una configuracion integral deliberada de la instalacion. No usarlo para aplicar o comprobar solamente memoria. |
| `SGD.bat verificar` | Ejecuta validacion SQL, comprueba JDBC, paginas HTTP, autenticacion con usuarios configurados y WebSocket. | Para una validacion integrada autorizada de la instalacion; no es una prueba offline ni una simple inspeccion de memoria. |
| Pruebas `test_memoria_payara.py` y `test_configuracion_payara.py` | Validan los auxiliares con archivos y dominios temporales, sin consultar PostgreSQL ni reiniciar la instalacion activa. | Despues de modificar esos auxiliares. Los comandos completos estan en el apartado anterior de esta guia. |
| Pruebas `test_cache_plantillas.py`, `test_contexto_plantilla.py`, `test_plantillas_docx.py` y `test_localidad_documental.py` | Validan offline la generacion documental, localidad y retencion de reportes con datos ficticios. | Despues de cambiar el codigo de plantillas o documentos. Consultar `manual/CORRECCION-CACHE-PLANTILLAS.md`; no equivalen a probar todos los flujos activos del SGD. |

La correccion de `null` y la mejora de cache son cambios de codigo: deben estar
en el WAR compilado y desplegado para funcionar. Las cabeceras de Informe y
Oficio tambien requieren actualizar las plantillas almacenadas en PostgreSQL
mediante el SQL 06 revisado; consultar `manual/CORRECCION-PLANTILLA-FIRMA.md`.
Compilar o desplegar no aplica ese SQL ni modifica documentos ya generados o
firmados. Para una actualizacion de codigo no usar `todo`, `datos` o `restaurar`
como atajo, porque incluyen operaciones adicionales sobre la base.

## Guia resumida: actualizar la instalacion desde GitHub

Usar esta lista en cada actualizacion de una **instalacion Windows existente**
con el portable y dominio `sgd`. Ejecutar cada etapa por separado y continuar
solo si termina correctamente. Para un equipo nuevo se necesita el portable
completo, incluidos los runtimes que Git no distribuye; seguir
`manual/INSTALACION.md` y el manual de instalacion vigente antes de usar esta
lista. No reinstalar una base existente para actualizar fuentes.

### Lista de seguimiento

1. **Identificar la version y el destino.** Registrar fecha, servidor, carpeta,
   rama `base-sgd-inia`, commit anterior y commit nuevo. Revisar que cambios trae
   la version: codigo, propiedades, memoria, plantillas o SQL. Acordar una pausa
   de uso; los usuarios deben guardar sus documentos y cerrar operaciones.
2. **Respaldar antes de reemplazar.** Conservar fuera de la carpeta que se
   actualizara las fuentes y utilitarios anteriores, configuracion local,
   configuracion activa de Payara, documentos y artefactos de la version
   desplegada. Los WAR existentes en `binarios` pueden diferir de la version
   activa: no asumir que sirven para revertir sin comprobarlo. Si se aplicara
   SQL, obtener ademas un respaldo PostgreSQL verificado con el procedimiento
   autorizado; `SGD.bat respaldar` genera `datos/respaldo-portable.dump` en el
   equipo de la base y reemplaza el dump anterior, por lo que debe conservarse
   una copia fechada. No usar una copia en caliente del directorio fisico
   `datos/postgresql` como sustituto de ese respaldo.
3. **Descargar y reemplazar solo lo correspondiente.** Usar la misma rama y
   version para fuentes, utilitarios, plantillas y documentacion. En un checkout
   Git, revisar `git status --short` y conservar las ediciones locales antes de
   `git pull --ff-only origin base-sgd-inia`; si hay conflictos o falla, detenerse
   y resolverlos. Si se descarga un ZIP, extraerlo primero en otra carpeta y
   comparar antes de copiar los archivos versionados. No borrar la instalacion
   para copiar encima el ZIP completo.

| Archivos o carpetas | Como tratarlos al reemplazar |
| --- | --- |
| `fuentes`, `herramientas` versionadas, lanzadores, `tests`, `manual`, `plantillas-docx` y `bd/correcciones` | Incorporar los cambios de la version elegida. Descargar SQL o DOCX no los aplica a PostgreSQL. Conservar los runtimes ignorados por Git que estan bajo `herramientas`. |
| `configuracion.json`, configuraciones locales, `datos` y documentos externos | Conservar los del servidor destino. Revisar solo los parametros nuevos necesarios; no sustituir claves, datos ni rutas por los del piloto. |
| `java`, motores PostgreSQL, Python/Maven incluidos y `payara/payara5` | Conservar la instalacion existente; no reemplazar versiones del motor como parte de una actualizacion de fuentes. |
| `payara/plantillas` y `payara/configuracion` | Son propiedades base y una copia versionada. Comparar con la configuracion propia del destino; no copiar toda la captura sobre el dominio activo. |
| `binarios` y `logs` | Conservar la version anterior para recuperacion antes de compilar. La compilacion genera WAR nuevos y sobrescribe su manifiesto y logs de compilacion. |

4. **Compilar si cambio el codigo.** Ejecutar `SGD.bat compilar` y confirmar que
   finalizo sin errores y genero `logs/compilacion.json`. No desplegar una
   compilacion fallida ni sustituirla por WAR antiguos. Si solo cambio la memoria
   o documentacion, omitir la compilacion.
5. **Revisar la configuracion que se activara.** Comprobar host y puerto de BD,
   URL publica, puertos Payara, entidad, rutas, integraciones y certificados del
   destino. `SGD.bat iniciar` copia las propiedades base desde `payara/plantillas`
   y adapta ciertos valores locales; comparar y conservar cualquier ajuste
   propio antes de usarlo. Para la correccion de localidad, confirmar que la
   propiedad activa `localidad_documental_predeterminada=La Molina` quedara en
   `payara/payara5/glassfish/domains/sgd/sgdproperties/application.properties`.
   Para otros cambios puntuales de configuracion aplicar solo lo revisado con
   Payara detenido. Usar `configurar` unicamente para una reconfiguracion integral
   prevista, porque tambien actualiza rutas en BD.
6. **Activar memoria solo si corresponde.** Usar
   `manual/ACTIVAR-MEMORIA-PAYARA.ps1 -HeapMB 4096 -Activar` y comprobar el mensaje
   de heap activo. Si existe `heap_payara_mb` local debe coincidir. Esta operacion
   reinicia solo Payara; si la actualizacion es exclusivamente de memoria no
   ejecutar la parada general ni el despliegue de la etapa siguiente.
7. **Reiniciar y desplegar el codigo revisado.** Para una actualizacion de codigo
   y propiedades usar la secuencia controlada siguiente durante la pausa de uso.
   En el modo portable esta secuencia tambien interrumpe PostgreSQL; en un
   servidor con BD externa respetar su procedimiento de operacion. Verificar la
   parada real y el log antes de seguir: `detener` puede devolver cero aunque el
   comando de parada de Payara falle. Comprobar que no haya proceso del dominio;
   no forzar la terminacion ni borrar `config/pid` para continuar.

```powershell
Set-Location -LiteralPath 'C:\SGD\SGD-INIA-INSTALL_PG'

.\SGD.bat compilar
if ($LASTEXITCODE -ne 0) { throw 'Fallo la compilacion; no desplegar.' }

.\SGD.bat detener
if ($LASTEXITCODE -ne 0) { throw 'Fallo la parada; revisar el estado.' }
# Revisar logs/payara-detener.log y comprobar la parada real antes de continuar.

.\SGD.bat iniciar
if ($LASTEXITCODE -ne 0) { throw 'Fallo el inicio; no desplegar.' }

.\SGD.bat desplegar
if ($LASTEXITCODE -ne 0) { throw 'Fallo el despliegue; revisar los logs.' }
```

La compilacion del bloque es la etapa 4: si ya se completo con las mismas
fuentes, no repetirla. Ejecutar las lineas por etapas, no como un bloque
desatendido. `Actualizar-SGD.bat` combina `compilar`, `iniciar`, `desplegar` y
`verificar`; accede a la base y no fuerza un reinicio si Payara ya esta activo.
No combinarlo con esta secuencia ni usarlo como ajuste exclusivo de memoria.

8. **Aplicar plantillas o SQL solo si la version lo requiere.** Revisar y aplicar
   unicamente los scripts previstos para el estado real de esa base, con su
   respaldo y autorizacion de escritura. Para la cabecera y espacio de firma de
   Informe/Oficio corresponde el SQL 06 segun
   `manual/CORRECCION-PLANTILLA-FIRMA.md`; no repetirlo si ya se aplico. No repetir
   cargas de usuarios, UTI ni datos iniciales. Si el script rechaza un hash o
   estado desconocido, revisar la plantilla existente en vez de sobrescribirla.
9. **Validar antes de habilitar el uso.** Revisar los logs de despliegue y
   `payara/payara5/glassfish/domains/sgd/logs/server.log`; comprobar acceso al SGD
   con la URL del destino y recargar el navegador con Ctrl+F5. Para las pruebas
   integradas autorizadas ejecutar `SGD.bat verificar` y revisar
   `logs/pruebas-funcionales.json`. En un puesto preparado comprobar Tramitedoc
   conectado y, con un documento de prueba autorizado, generar y abrir un DOCX,
   confirmar localidad sin `null`, cabecera, destinatarios y actualizacion de
   plantilla; verificar la firma en el flujo institucional cuando corresponda.
   Una prueba de firma o documento registra informacion: no usar documentos
   reales solo para comprobar el despliegue. Los documentos anteriores no se
   regeneran automaticamente.
10. **Registrar el cierre o recuperar.** Anotar commit desplegado, resultado de
    compilacion y validacion, heap comprobado si cambio, SQL aplicado y ubicacion
    de respaldos. Habilitar usuarios cuando las comprobaciones sean correctas.
    Si falla una etapa, mantener la pausa y revisar `logs/ULTIMO-ERROR.txt` y el
    log especifico; no continuar con errores. Para revertir codigo y propiedades,
    recuperar los artefactos y configuracion previamente comprobados con Payara
    detenido y redesplegar la version anterior. La reversion SQL es independiente
    y requiere un procedimiento revisado; no restaurar toda la base sobre datos
    nuevos como atajo.

**Referencia rapida:** fuentes -> compilar y desplegar; propiedades activas ->
revisar y reiniciar la aplicacion; memoria -> script de activacion; plantillas
guardadas en BD -> SQL previsto; documentacion -> sin compilacion ni reinicio.

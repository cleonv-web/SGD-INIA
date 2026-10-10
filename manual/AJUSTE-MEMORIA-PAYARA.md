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

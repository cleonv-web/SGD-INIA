<#
Ejecuta el instalador con Python incluido, desde la carpeta del paquete.
No instala Java, Python ni Maven en el sistema; no cambia JAVA_HOME global.
Acciones: todo, diagnosticar, compilar, base, datos, configurar, desplegar,
verificar, iniciar, detener, respaldar, restaurar.
Ejemplo desde PowerShell: .\SGD.ps1 compilar
Un error devuelve un código distinto de cero y se conserva en logs.
#>
param([ValidateSet('todo','diagnosticar','compilar','base','datos','configurar','desplegar','verificar','iniciar','detener','respaldar','restaurar','generar_sql','empaquetar')][string]$Accion='todo')
$ErrorActionPreference='Stop'
$pythonSgd=Join-Path $PSScriptRoot 'herramientas/python-windows/python.exe'
if (-not (Test-Path -LiteralPath $pythonSgd)) { throw 'Falta Python portable. Copiar el paquete completo.' }
& $pythonSgd (Join-Path $PSScriptRoot 'sgd.py') $Accion
exit $LASTEXITCODE

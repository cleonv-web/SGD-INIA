<#
Ejecuta el instalador con Python incluido, desde la carpeta del paquete.
No instala Java, Python ni Maven en el sistema; no cambia JAVA_HOME global.
Acciones: todo, diagnosticar, compilar, base, datos, configurar, desplegar,
verificar, iniciar, detener, respaldar, restaurar.
Ejemplo desde PowerShell: .\SGD.ps1 compilar
Un error devuelve un código distinto de cero y se conserva en logs.
#>
param(
    [ValidateSet('instalar','instalar_cliente','verificar_cliente','plantillas','todo','diagnosticar','compilar','base','datos','configurar','desplegar','verificar','iniciar','detener','respaldar','restaurar','generar_sql','empaquetar')][string]$Accion='todo',
    [ValidateSet('servidor','completo','puesto')][string]$Equipo,
    [switch]$Aplicar,
    [string]$Confirmar
)
$ErrorActionPreference='Stop'
$pythonSgd=Join-Path $PSScriptRoot 'herramientas/python-windows/python.exe'
if (-not (Test-Path -LiteralPath $pythonSgd)) { throw 'Falta Python portable. Copiar el paquete completo.' }
if ($Aplicar -or $Confirmar) {
    if ($Accion -ne 'plantillas' -or $Equipo) { throw 'Aplicar y Confirmar solo corresponden a plantillas.' }
    $sgdArgs=@($Accion)
    if ($Aplicar) { $sgdArgs+='--aplicar' }
    if ($Confirmar) { $sgdArgs+=@('--confirmar',$Confirmar) }
    & $pythonSgd (Join-Path $PSScriptRoot 'sgd.py') @sgdArgs
} elseif ($Equipo) {
    if ($Accion -ne 'instalar') { throw 'Equipo solo corresponde a la acción instalar.' }
    & $pythonSgd (Join-Path $PSScriptRoot 'sgd.py') $Accion --equipo $Equipo
} else {
    & $pythonSgd (Join-Path $PSScriptRoot 'sgd.py') $Accion
}
exit $LASTEXITCODE

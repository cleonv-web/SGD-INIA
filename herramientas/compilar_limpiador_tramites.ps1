$ErrorActionPreference='Stop'
$raizCompilacion=Split-Path -Parent $PSScriptRoot
$compilador=Join-Path $env:WINDIR 'Microsoft.NET\Framework64\v4.0.30319\csc.exe'
& $compilador /nologo /target:exe /platform:anycpu /optimize+ "/out:$raizCompilacion\Limpiar-Tramites.exe" "$PSScriptRoot\LimpiarTramitesLauncher.cs"
if ($LASTEXITCODE -ne 0) { throw 'No se pudo compilar Limpiar-Tramites.exe' }

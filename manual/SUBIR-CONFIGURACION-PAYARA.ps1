param([switch]$Publicar)
$ErrorActionPreference = 'Stop'
$rootPayaraRepo = Split-Path -Parent $PSScriptRoot
Push-Location -LiteralPath $rootPayaraRepo
try {
    & git rev-parse --show-toplevel | Out-Null
    if ($LASTEXITCODE -ne 0) { throw 'La carpeta no es un repositorio Git.' }
    & git diff --cached --quiet
    if ($LASTEXITCODE -ne 0) { throw 'Ya hay cambios preparados en Git. Revisarlos antes de usar este script.' }
    & .\herramientas\python-windows\python.exe .\herramientas\configuracion_payara.py verificar
    if ($LASTEXITCODE -ne 0) { throw 'La configuracion no supera la verificacion.' }
    & git add -- .gitattributes payara/configuracion herramientas/configuracion_payara.py tests/test_configuracion_payara.py manual/SUBIR-CONFIGURACION-PAYARA.ps1
    if ($LASTEXITCODE -ne 0) { throw 'No se pudieron preparar los archivos.' }
    & git diff --cached --check
    if ($LASTEXITCODE -ne 0) { throw 'La revision Git detecto errores. Revisar el indice antes de confirmar.' }
    & git diff --cached --stat
    & git commit -m 'Conservar configuracion del dominio Payara piloto y utilitario offline'
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear el commit.' }
    if ($Publicar) {
        $branchPayaraRepo = (& git branch --show-current).Trim()
        if ($LASTEXITCODE -ne 0 -or -not $branchPayaraRepo) { throw 'Falta una rama activa.' }
        & git push -u origin $branchPayaraRepo
        if ($LASTEXITCODE -ne 0) { throw 'No se pudo publicar. El commit local se conserva.' }
    }
} finally {
    Pop-Location
}

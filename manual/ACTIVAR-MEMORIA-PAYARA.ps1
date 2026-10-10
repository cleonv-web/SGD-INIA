param(
    [ValidateSet(1024, 2048, 4096)][int]$HeapMB = 4096,
    [switch]$Activar
)
$ErrorActionPreference = 'Stop'
$repoMemoria = Split-Path -Parent $PSScriptRoot
$dominioMemoria = Join-Path $repoMemoria 'payara\payara5\glassfish\domains\sgd'
$pythonMemoria = Join-Path $repoMemoria 'herramientas\python-windows\python.exe'
$javaMemoria = Join-Path $repoMemoria 'java\windows\jdk8u504-b01\bin\java.exe'
$cliMemoria = Join-Path $repoMemoria 'payara\payara5\glassfish\lib\client\appserver-cli.jar'
$toolMemoria = Join-Path $repoMemoria 'herramientas\memoria_payara.py'

function Get-ProcesosDominioMemoria {
    @(Get-CimInstance Win32_Process | Where-Object {
        $_.Name -match '^java(w)?\.exe$' -and $_.CommandLine -and
        $_.CommandLine.IndexOf($dominioMemoria, [StringComparison]::OrdinalIgnoreCase) -ge 0
    })
}

foreach ($archivoMemoria in @($pythonMemoria, $javaMemoria, $cliMemoria, $toolMemoria,
        (Join-Path $dominioMemoria 'config\domain.xml'))) {
    if (-not (Test-Path -LiteralPath $archivoMemoria -PathType Leaf)) {
        throw "Falta archivo requerido: $archivoMemoria"
    }
}
$configMemoria = Get-Content -LiteralPath (Join-Path $repoMemoria 'configuracion.json') -Raw | ConvertFrom-Json
if ($null -ne $configMemoria.heap_payara_mb -and $configMemoria.heap_payara_mb -ne $HeapMB) {
    throw 'heap_payara_mb explicito no coincide con HeapMB. Revisarlo antes de activar.'
}
[xml]$xmlMemoria = Get-Content -LiteralPath (Join-Path $dominioMemoria 'config\domain.xml') -Raw
$jvmMemoria = @($xmlMemoria.SelectNodes('//config[@name="server-config"]/java-config/jvm-options') |
    Where-Object { $_.InnerText.Trim().StartsWith('-Xmx') })
if ($jvmMemoria.Count -ne 1 -or $jvmMemoria[0].InnerText.Trim() -notmatch '^-Xmx\d+[kKmMgG]$') {
    throw 'Se requiere una unica opcion -Xmx valida en server-config.'
}
$procesosMemoria = @(Get-ProcesosDominioMemoria)
Write-Host "Preparado para ajustar server-config a $HeapMB MB. Procesos del dominio: $($procesosMemoria.Count)."
if (-not $Activar) {
    Write-Host 'Inspeccion offline completada. Para activar, repetir con -Activar.'
    Write-Host 'La activacion interrumpe brevemente el SGD y reinicia exclusivamente Payara.'
    return
}

# Leer la credencial existente sin generar archivos, imprimirla ni consultar BD.
$credencialMemoria = Get-Content -LiteralPath (Join-Path $repoMemoria 'datos\credenciales.json') -Raw | ConvertFrom-Json
if (-not $credencialMemoria.aplicacion) { throw 'Falta credencial existente de la aplicacion.' }
$entornoMemoriaAnterior = @{
    JAVA_HOME = $env:JAVA_HOME
    SGD_DB_PASSWORD = $env:SGD_DB_PASSWORD
}
$logMemoria = Join-Path $repoMemoria ('logs\heap-payara-' + (Get-Date -Format 'yyyyMMddTHHmmssfff') + '.log')
$baseArgsMemoria = @('-jar', $cliMemoria, '--host', '127.0.0.1', '--port',
    [string]$configMemoria.puerto_admin, '--interactive=false')
function Invoke-AdminMemoria([string[]]$Argumentos) {
    $salidaMemoria = & $javaMemoria @baseArgsMemoria @Argumentos 2>&1
    $codigoMemoria = $LASTEXITCODE
    $salidaMemoria | Out-File -LiteralPath $logMemoria -Append -Encoding utf8
    if ($codigoMemoria -ne 0) { throw "Fallo el comando Payara. Revisar el log privado: $logMemoria" }
}
try {
    $env:JAVA_HOME = Split-Path -Parent (Split-Path -Parent $javaMemoria)
    $env:SGD_DB_PASSWORD = $credencialMemoria.aplicacion
    if ($procesosMemoria.Count -gt 0) {
        Write-Host 'Deteniendo exclusivamente Payara...'
        Invoke-AdminMemoria -Argumentos @('stop-domain', '--domaindir', (Split-Path -Parent $dominioMemoria), 'sgd')
    }
    $limiteParadaMemoria = (Get-Date).AddSeconds(30)
    while (@(Get-ProcesosDominioMemoria).Count -gt 0 -and (Get-Date) -lt $limiteParadaMemoria) {
        Start-Sleep -Seconds 1
    }
    if (@(Get-ProcesosDominioMemoria).Count -gt 0) { throw 'Payara sigue activo; no se modifica el XML.' }
    if (Test-Path -LiteralPath (Join-Path $dominioMemoria 'config\pid')) {
        throw 'Persiste config/pid. Verificar la parada antes de continuar; no se borra automaticamente.'
    }
    & $pythonMemoria $toolMemoria aplicar --mb $HeapMB --dominio $dominioMemoria
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo aplicar el heap. Payara queda detenido; revisar el respaldo.' }
    Write-Host 'Iniciando exclusivamente Payara...'
    Invoke-AdminMemoria -Argumentos @('start-domain', '--domaindir', (Split-Path -Parent $dominioMemoria), 'sgd')
    $limiteInicioMemoria = (Get-Date).AddSeconds(30)
    do {
        $activosMemoria = @(Get-ProcesosDominioMemoria)
        if ($activosMemoria.Count -eq 1) { break }
        Start-Sleep -Seconds 1
    } while ((Get-Date) -lt $limiteInicioMemoria)
    if ($activosMemoria.Count -ne 1) { throw 'No se pudo identificar un unico proceso Payara reiniciado.' }
    $heapsMemoria = @([regex]::Matches($activosMemoria[0].CommandLine, '(?<!\S)-Xmx\S+') |
        ForEach-Object { $_.Value })
    if ($heapsMemoria.Count -ne 1 -or $heapsMemoria[0] -ne "-Xmx${HeapMB}m") {
        throw 'El argumento del proceso no coincide con el heap solicitado.'
    }
    Write-Host "Heap activo verificado: -Xmx${HeapMB}m; PID $($activosMemoria[0].ProcessId)."
    Write-Host 'No se ejecutaron scripts SQL ni comandos de PostgreSQL. La validacion funcional queda pendiente.'
} finally {
    $env:JAVA_HOME = $entornoMemoriaAnterior.JAVA_HOME
    $env:SGD_DB_PASSWORD = $entornoMemoriaAnterior.SGD_DB_PASSWORD
    $credencialMemoria = $null
}

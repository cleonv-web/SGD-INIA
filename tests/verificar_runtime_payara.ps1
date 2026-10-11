# Crea un dominio exclusivamente en TEMP usando los archivos incluidos por Git.
# No inicia Payara, no consulta PostgreSQL y no modifica el dominio del piloto.
$ErrorActionPreference = 'Stop'
$proyectoQA = Split-Path -Parent $PSScriptRoot
$temporalQA = Join-Path ([IO.Path]::GetTempPath()) ('sgd-payara-runtime-' + [Guid]::NewGuid().ToString('N'))
$limiteQA = [IO.Path]::GetFullPath([IO.Path]::GetTempPath()).TrimEnd('\', '/') + [IO.Path]::DirectorySeparatorChar
if (-not ([IO.Path]::GetFullPath($temporalQA)).StartsWith($limiteQA, [StringComparison]::OrdinalIgnoreCase)) {
    throw 'Ruta temporal fuera del directorio permitido.'
}
$null = New-Item -ItemType Directory -Path $temporalQA
Push-Location -LiteralPath $proyectoQA
try {
    $archivosQA = @(git ls-files --cached --others --exclude-standard -- payara/payara5)
    if ($LASTEXITCODE -ne 0 -or $archivosQA.Count -eq 0) { throw 'No se pudo obtener el motor incluido por Git.' }
    $motorQA = Join-Path $temporalQA 'payara5'
    foreach ($archivoQA in $archivosQA) {
        if (-not $archivoQA.StartsWith('payara/payara5/')) { throw 'Entrada fuera del motor.' }
        $relativoQA = $archivoQA.Substring('payara/payara5/'.Length)
        $destinoQA = [IO.Path]::GetFullPath((Join-Path $motorQA $relativoQA))
        if (-not $destinoQA.StartsWith($motorQA + '\', [StringComparison]::OrdinalIgnoreCase)) {
            throw 'Destino de copia fuera de TEMP.'
        }
        $null = New-Item -ItemType Directory -Path (Split-Path -Parent $destinoQA) -Force
        Copy-Item -LiteralPath (Join-Path $proyectoQA $archivoQA) -Destination $destinoQA
    }
    $dominiosQA = Join-Path $motorQA 'glassfish\domains'
    $null = New-Item -ItemType Directory -Path $dominiosQA -Force
    $jdkQA = Get-ChildItem -LiteralPath (Join-Path $proyectoQA 'java\windows') -Directory -Filter 'jdk*' | Select-Object -First 1
    if (-not $jdkQA) { throw 'Falta Java portable.' }
    $cliQA = Join-Path $motorQA 'glassfish\lib\client\appserver-cli.jar'
    $salidaQA = & (Join-Path $jdkQA.FullName 'bin\java.exe') -jar $cliQA --interactive=false create-domain `
        --nopassword=true --adminport 48885 --instanceport 48886 --domaindir $dominiosQA qa-limpieza 2>&1
    if ($LASTEXITCODE -ne 0) { throw "No se pudo crear el dominio temporal: $salidaQA" }
    [xml]$xmlQA = Get-Content -LiteralPath (Join-Path $dominiosQA 'qa-limpieza\config\domain.xml') -Raw
    if (-not $xmlQA.domain) { throw 'No se obtuvo un domain.xml valido.' }
    $resultadoQA = [ordered]@{
        archivos_motor = $archivosQA.Count
        dominio_temporal_creado = $true
        servidor_iniciado = $false
        postgresql_consultado = $false
        temporal_eliminado_al_terminar = $true
    }
    $resultadoQA | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $proyectoQA 'logs\qa-runtime-payara.json') -Encoding UTF8
    Write-Host ('Motor verificado: {0} archivos; dominio nuevo creado en TEMP. No se iniciaron servidores.' -f $archivosQA.Count)
} finally {
    Pop-Location
    $objetivoQA = [IO.Path]::GetFullPath($temporalQA)
    if (-not $objetivoQA.StartsWith($limiteQA, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Se rechazo la eliminacion de un temporal fuera de TEMP.'
    }
    if (Test-Path -LiteralPath $objetivoQA) {
        Remove-Item -LiteralPath $objetivoQA -Recurse -Force
    }
}

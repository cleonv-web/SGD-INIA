# Solo Windows. Sin -Aplicar muestra el plan. No ejecuta comandos PostgreSQL.
param(
    [switch]$Aplicar,
    [string]$RaizProyecto = (Split-Path -Parent $PSScriptRoot)
)
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Verificar-RutaPayara {
    param([string]$Ruta, [string]$Limite)
    $absoluta = [IO.Path]::GetFullPath($Ruta)
    $prefijo = [IO.Path]::GetFullPath($Limite).TrimEnd('\', '/') + [IO.Path]::DirectorySeparatorChar
    if (-not $absoluta.StartsWith($prefijo, [StringComparison]::OrdinalIgnoreCase)) {
        throw "Ruta fuera del dominio permitido: $absoluta"
    }
    $actual = $absoluta
    while ($actual) {
        if (Test-Path -LiteralPath $actual) {
            $objeto = Get-Item -LiteralPath $actual -Force
            if (($objeto.Attributes -band [IO.FileAttributes]::ReparsePoint) -ne 0) {
                throw "Se rechazo un enlace o junction: $actual"
            }
        }
        $padre = Split-Path -Parent $actual
        if ($padre -eq $actual) { break }
        $actual = $padre
    }
    return $absoluta
}

function Buscar-ProcesoPayara {
    param([string]$Dominio)
    # La ausencia de config/pid no prueba que el dominio este detenido.
    return @(Get-CimInstance Win32_Process -Filter "Name = 'java.exe' OR Name = 'javaw.exe'" |
        Where-Object {
            $_.CommandLine -and
            $_.CommandLine.Replace('/', '\').IndexOf($Dominio.Replace('/', '\'), [StringComparison]::OrdinalIgnoreCase) -ge 0
        })
}

function Verificar-ContenidoPayara {
    param([string]$Carpeta)
    $prefijo = $Carpeta.TrimEnd('\', '/') + [IO.Path]::DirectorySeparatorChar
    foreach ($elemento in @(Get-ChildItem -LiteralPath $Carpeta -Force -Recurse)) {
        if (-not $elemento.FullName.StartsWith($prefijo, [StringComparison]::OrdinalIgnoreCase) -or
            ($elemento.Attributes -band [IO.FileAttributes]::ReparsePoint) -ne 0) {
            throw "Se rechazo un enlace, junction o ruta externa: $($elemento.FullName)"
        }
    }
}

function Invocar-AdminPayara {
    param([string]$Accion)
    $configLocal = Get-Content -LiteralPath (Join-Path $proyecto 'configuracion.json') -Raw | ConvertFrom-Json
    $jdkLocal = Get-ChildItem -LiteralPath (Join-Path $proyecto 'java\windows') -Directory -Filter 'jdk*' |
        Select-Object -First 1
    if (-not $jdkLocal) { throw 'Falta el Java portable de la instalacion.' }
    $javaLocal = Join-Path $jdkLocal.FullName 'bin\java.exe'
    $cliLocal = Join-Path $proyecto 'payara\payara5\glassfish\lib\client\appserver-cli.jar'
    if (-not (Test-Path -LiteralPath $javaLocal) -or -not (Test-Path -LiteralPath $cliLocal)) {
        throw 'Falta Java o appserver-cli.jar; no continuar.'
    }
    $claveAnterior = [Environment]::GetEnvironmentVariable('SGD_DB_PASSWORD', 'Process')
    $javaAnterior = [Environment]::GetEnvironmentVariable('JAVA_HOME', 'Process')
    $pathAnterior = $env:PATH
    try {
        $env:JAVA_HOME = $jdkLocal.FullName
        $env:PATH = (Join-Path $jdkLocal.FullName 'bin') + [IO.Path]::PathSeparator + $pathAnterior
        if ($Accion -eq 'start-domain') {
            $credencialLocal = Get-Content -LiteralPath (Join-Path $proyecto 'datos\credenciales.json') -Raw | ConvertFrom-Json
            $env:SGD_DB_PASSWORD = $credencialLocal.aplicacion
        }
        $salidaAdmin = & $javaLocal -jar $cliLocal --host 127.0.0.1 --port $configLocal.puerto_admin --interactive=false $Accion sgd 2>&1
        if ($LASTEXITCODE -ne 0) {
            # No imprimir salida de administracion que pueda contener claves.
            throw "Fallo Payara $Accion. Revisar el estado del dominio."
        }
    } finally {
        [Environment]::SetEnvironmentVariable('SGD_DB_PASSWORD', $claveAnterior, 'Process')
        [Environment]::SetEnvironmentVariable('JAVA_HOME', $javaAnterior, 'Process')
        $env:PATH = $pathAnterior
    }
}

try {
    $proyecto = [IO.Path]::GetFullPath($RaizProyecto)
    $dominio = Join-Path $proyecto 'payara\payara5\glassfish\domains\sgd'
    if (-not (Test-Path -LiteralPath (Join-Path $dominio 'config\domain.xml'))) {
        throw 'No se encontro el dominio sgd configurado. No se borro nada.'
    }
    $plan = @()
    # Lista cerrada: nunca limpiar config, applications, lib, reportes ni datos.
    foreach ($nombre in @('osgi-cache', 'generated', 'temp', 'logs')) {
        $carpeta = Verificar-RutaPayara (Join-Path $dominio $nombre) $dominio
        if (-not (Test-Path -LiteralPath $carpeta)) { continue }
        Verificar-ContenidoPayara $carpeta
        $archivos = @(Get-ChildItem -LiteralPath $carpeta -Force -File -Recurse)
        [long]$bytes = 0
        foreach ($archivo in $archivos) { $bytes += $archivo.Length }
        $plan += [pscustomobject]@{ Carpeta = $nombre; Archivos = $archivos.Count; Bytes = $bytes; Ruta = $carpeta }
    }
    [long]$total = 0
    foreach ($fila in $plan) {
        $total += $fila.Bytes
        Write-Host ('{0}: {1} archivos, {2:N2} MiB' -f $fila.Carpeta, $fila.Archivos, ($fila.Bytes / 1MB))
    }
    Write-Host ('Total de archivos regenerables: {0:N2} MiB' -f ($total / 1MB))
    if (-not $Aplicar) {
        Write-Host 'Solo revision. Para limpiar: Limpiar-Payara.bat --aplicar'
        exit 0
    }
    $estabaActivo = @(Buscar-ProcesoPayara $dominio).Count -gt 0
    if ($estabaActivo) {
        Write-Host 'Deteniendo solo Payara antes de limpiar...'
        Invocar-AdminPayara 'stop-domain'
    }
    if (@(Buscar-ProcesoPayara $dominio).Count -gt 0) {
        throw 'Payara sigue activo. No se borro ningun archivo.'
    }
    if (Test-Path -LiteralPath (Join-Path $dominio 'config\pid')) {
        throw 'Existe config/pid; comprobar la parada. No se elimina ese archivo automaticamente.'
    }
    try {
        foreach ($fila in $plan) {
            # Verificar nuevamente todas las rutas antes de cada borrado recursivo.
            $carpeta = Verificar-RutaPayara $fila.Ruta $dominio
            Verificar-ContenidoPayara $carpeta
            foreach ($elemento in @(Get-ChildItem -LiteralPath $carpeta -Force)) {
                $objetivo = Verificar-RutaPayara $elemento.FullName $dominio
                Remove-Item -LiteralPath $objetivo -Recurse -Force
            }
        }
        $registro = Join-Path $proyecto 'logs'
        $null = Verificar-RutaPayara (Join-Path $registro 'limpieza-payara.json') $proyecto
        $null = New-Item -ItemType Directory -Path $registro -Force
        $resultado = [ordered]@{
            fecha_utc = [DateTime]::UtcNow.ToString('o')
            bytes_anteriores = $total
            carpetas = @($plan | ForEach-Object { $_.Carpeta })
            payara_estaba_activo = $estabaActivo
            postgresql_modificado = $false
        }
        $resultado | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $registro 'limpieza-payara.json') -Encoding UTF8
        Write-Host ('Limpieza terminada: {0:N2} MiB retirados.' -f ($total / 1MB))
    } finally {
        if ($estabaActivo) {
            Write-Host 'Restableciendo el inicio de Payara...'
            Invocar-AdminPayara 'start-domain'
        }
    }
    Write-Host 'Configuracion, certificados, aplicaciones y documentos conservados.'
    exit 0
} catch {
    Write-Error $_.Exception.Message -ErrorAction Continue
    exit 1
}

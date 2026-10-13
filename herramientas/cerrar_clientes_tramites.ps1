<# Sin -Cerrar solo inventaria. Invocado por Limpiar-Tramites al aplicar. #>
param([switch]$Cerrar)
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding($false)
$sesionLimpieza = (Get-Process -Id $PID).SessionId
$cerrados = @()

function Clientes-Activos {
    @(Get-Process -Name WINWORD,Tramitedoc -ErrorAction SilentlyContinue)
}

try {
    if ($Cerrar) {
        $clientes = @(Clientes-Activos)
        if (@($clientes | Where-Object { $_.SessionId -ne $sesionLimpieza }).Count) {
            throw 'Hay Word o Tramitedoc en otra sesion de Windows. Cerrar esa sesion antes de limpiar.'
        }
        # Primero el puente, para que no abra nuevos documentos durante el cierre.
        foreach ($cliente in @($clientes | Where-Object { $_.ProcessName -eq 'Tramitedoc' })) {
            $identidad = $cliente.StartTime
            $rutaCliente = $cliente.Path
            if (-not $cliente.HasExited) {
                if ($cliente.CloseMainWindow()) { $null = $cliente.WaitForExit(5000) }
                $cliente.Refresh()
                if (-not $cliente.HasExited) {
                    # Sólo el cliente SGD, nunca Word ni todos los procesos Java.
                    if ($cliente.StartTime -ne $identidad -or $cliente.SessionId -ne $sesionLimpieza) {
                        throw 'Cambio el proceso de Tramitedoc; no continuar.'
                    }
                    Stop-Process -InputObject $cliente -Force -ErrorAction Stop
                    if (-not $cliente.WaitForExit(5000)) { throw 'Tramitedoc no termino.' }
                }
            }
            $cerrados += @{ nombre='Tramitedoc'; pid=$cliente.Id; ruta=$rutaCliente }
        }
        # Cierre normal: Word puede mostrar Guardar/Descartar/Cancelar.
        # Nunca forzar su terminacion ni descartar cambios automaticamente.
        $rutasWord = @{}
        foreach ($cliente in @($clientes | Where-Object { $_.ProcessName -eq 'WINWORD' })) {
            $rutasWord[$cliente.Id] = $cliente.Path
            if (-not $cliente.HasExited) { $null = $cliente.CloseMainWindow() }
        }
        $limiteCierre = [DateTime]::UtcNow.AddSeconds(45)
        while (@(Clientes-Activos).Count -and [DateTime]::UtcNow -lt $limiteCierre) {
            Start-Sleep -Milliseconds 250
        }
        foreach ($cliente in @($clientes | Where-Object { $_.ProcessName -eq 'WINWORD' })) {
            $cliente.Refresh()
            if ($cliente.HasExited) { $cerrados += @{ nombre='WINWORD'; pid=$cliente.Id; ruta=$rutasWord[$cliente.Id] } }
        }
    }
    $activos = @(Clientes-Activos | ForEach-Object {
        @{ nombre=$_.ProcessName; pid=$_.Id; sesion=$_.SessionId }
    })
    @{ cerrados=@($cerrados); activos=@($activos) } | ConvertTo-Json -Depth 4 -Compress
    if ($Cerrar -and $activos.Count) {
        [Console]::Error.WriteLine('Word o Tramitedoc siguen abiertos. Resolver el aviso de guardado o cerrar el programa; no se borrara nada.')
        exit 1
    }
} catch {
    [Console]::Error.WriteLine($_.Exception.Message)
    exit 1
}

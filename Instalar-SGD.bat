@echo off
if not "%~2"=="" (
    echo Uso: Instalar-SGD.bat [servidor^|completo^|puesto]
    exit /b 2
)
@rem Sin argumentos pregunta el uso del equipo. Opciones: servidor, completo, puesto.
if "%~1"=="" (
    call "%~dp0SGD.bat" instalar
) else (
    call "%~dp0SGD.bat" instalar --equipo "%~1"
)
set "SGD_RESULTADO=%errorlevel%"
if not "%SGD_RESULTADO%"=="0" echo Instalacion detenida. Revisar logs\ULTIMO-ERROR.txt.
pause
exit /b %SGD_RESULTADO%

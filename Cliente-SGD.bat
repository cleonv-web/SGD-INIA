@echo off
setlocal
if not "%~2"=="" (
    echo Uso: Cliente-SGD.bat [instalar^|verificar]
    exit /b 2
)
rem Prepara o verifica SOLO la PC Windows que usa el navegador. No llama a la BD.
if "%~1"=="" goto instalar
if /i "%~1"=="instalar" goto instalar
if /i "%~1"=="verificar" goto verificar
echo Uso: Cliente-SGD.bat [instalar^|verificar]
exit /b 2
:instalar
call "%~dp0SGD.bat" instalar_cliente
goto terminar
:verificar
call "%~dp0SGD.bat" verificar_cliente
:terminar
set "SGD_RESULTADO=%errorlevel%"
if not "%SGD_RESULTADO%"=="0" echo Puesto pendiente de preparar. Revisar el error mostrado.
pause
exit /b %SGD_RESULTADO%

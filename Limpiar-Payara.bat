@echo off
setlocal
if "%~1"=="" goto revisar
if /I "%~1"=="--aplicar" if "%~2"=="" goto aplicar
echo Uso: Limpiar-Payara.bat [--aplicar]
exit /b 1
:revisar
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0herramientas\Limpiar-Payara.ps1"
exit /b %errorlevel%
:aplicar
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0herramientas\Limpiar-Payara.ps1" -Aplicar
exit /b %errorlevel%

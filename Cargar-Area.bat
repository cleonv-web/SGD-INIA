@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Sin --aplicar solo valida los archivos. No consulta bases de datos.
rem Plantillas: Cargar-Area.bat --plantilla --carpeta datos\carga-uti
if /I "%~1"=="--plantilla" goto plantilla
if /I "%~1"=="--catalogos" goto catalogos
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" cargar %*
exit /b %errorlevel%
:plantilla
rem Tras --plantilla se acepta unicamente: --carpeta RUTA.
if /I not "%~2"=="--carpeta" (echo Uso: Cargar-Area.bat --plantilla --carpeta RUTA & exit /b 1)
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" plantilla --carpeta "%~3"
exit /b %errorlevel%
:catalogos
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" catalogos
exit /b %errorlevel%

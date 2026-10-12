@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Sin argumentos carga la preparacion aprobada y gestiona los servicios locales.
rem Con argumentos se conserva el modo manual: sin --aplicar es revision offline.
rem Plantillas: Cargar-Area.bat --plantilla --carpeta datos\carga-uti
if "%~1"=="" goto automatico
if /I "%~1"=="--plantilla" goto plantilla
if /I "%~1"=="--catalogos" goto catalogos
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" cargar %*
exit /b %errorlevel%
:automatico
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" cargar --automatico
set "SGD_CARGA_EXIT=%errorlevel%"
pause
exit /b %SGD_CARGA_EXIT%
:plantilla
rem Tras --plantilla se acepta unicamente: --carpeta RUTA.
if /I not "%~2"=="--carpeta" (echo Uso: Cargar-Area.bat --plantilla --carpeta RUTA & exit /b 1)
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" plantilla --carpeta "%~3"
exit /b %errorlevel%
:catalogos
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" catalogos
exit /b %errorlevel%

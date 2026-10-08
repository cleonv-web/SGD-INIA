@echo off
setlocal
rem Python y todas las rutas se toman de esta carpeta, incluso con espacios.
rem Uso: SGD.bat compilar, verificar, diagnosticar, respaldar o restaurar.
if "%~1"=="" (set "SGD_ACCION=todo") else (set "SGD_ACCION=%~1")
"%~dp0herramientas\python-windows\python.exe" "%~dp0sgd.py" "%SGD_ACCION%"
exit /b %errorlevel%

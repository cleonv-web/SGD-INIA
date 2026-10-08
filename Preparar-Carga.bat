@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Convierte los CSV originales en borradores. No conecta a bases de datos.
"%~dp0herramientas\python-windows\python.exe" "%~dp0preparar_data.py" %*
exit /b %errorlevel%

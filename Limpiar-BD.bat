@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Solo retirar un area rastreada. Por defecto genera un plan offline.
rem Uso: Limpiar-BD.bat --area 51001
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" limpiar %*
exit /b %errorlevel%

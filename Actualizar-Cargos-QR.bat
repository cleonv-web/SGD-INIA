@echo off
cd /d "%~dp0"
"herramientas\python-windows\python.exe" "herramientas\actualizar_cargos_qr.py"
if errorlevel 1 exit /b 1

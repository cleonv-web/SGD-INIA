@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Retirar un area rastreada o reiniciar toda la base LOCAL del piloto.
rem Sin --aplicar no conecta ni modifica la base.
rem Uso: Limpiar-BD.bat --area 51001
rem Reinicio: Limpiar-BD.bat --todo --aplicar --confirmar REINICIAR:idosgd_inia
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" limpiar %*
exit /b %errorlevel%

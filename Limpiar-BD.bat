@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Retirar un area rastreada o reiniciar toda la base LOCAL del piloto.
rem Sin argumentos reinicia documentos, usuarios y areas, con respaldo previo.
rem Con argumentos conserva el modo manual: sin --aplicar solo muestra el plan.
rem Uso: Limpiar-BD.bat --area 51001
rem Reinicio completo: Limpiar-BD.bat
if "%~1"=="" goto reiniciar
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" limpiar %*
exit /b %errorlevel%
:reiniciar
"%~dp0herramientas\python-windows\python.exe" "%~dp0gestion_data.py" limpiar --todo --aplicar --confirmar REINICIAR:idosgd_inia
exit /b %errorlevel%

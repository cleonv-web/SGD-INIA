@echo off
setlocal
set "PYTHONUTF8=1"
chcp 65001 >nul
rem Solo valida archivos. Nunca carga, limpia ni consulta bases de datos.
rem Sin argumentos revisa datos\carga-uti-preparada y espera una tecla.
rem Otra area: Validar-Carga.bat --carpeta datos\carga-otra-preparada
"%~dp0herramientas\python-windows\python.exe" "%~dp0validar_carga.py" %*
set "SGD_VALIDATION_EXIT=%errorlevel%"
if "%~1"=="" pause
exit /b %SGD_VALIDATION_EXIT%

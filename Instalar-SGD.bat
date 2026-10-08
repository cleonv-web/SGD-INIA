@echo off
call "%~dp0SGD.bat" todo
set "SGD_RESULTADO=%errorlevel%"
if not "%SGD_RESULTADO%"=="0" echo Instalacion detenida. Revisar logs\ULTIMO-ERROR.txt.
pause
exit /b %SGD_RESULTADO%

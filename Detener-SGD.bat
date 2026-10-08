@echo off
call "%~dp0SGD.bat" detener
set "SGD_RESULTADO=%errorlevel%"
pause
exit /b %SGD_RESULTADO%

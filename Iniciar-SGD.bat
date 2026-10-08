@echo off
call "%~dp0SGD.bat" iniciar
set "SGD_RESULTADO=%errorlevel%"
pause
exit /b %SGD_RESULTADO%

@echo off
setlocal
rem Actualiza una instalacion existente; conserva la base y los documentos.
rem Compilar genera los WAR. Desplegar los pone en funcionamiento.
for %%A in (compilar iniciar desplegar verificar) do (
    echo.
    echo Ejecutando: %%A
    call "%~dp0SGD.bat" %%A
    if errorlevel 1 goto error
)
echo.
echo Actualizacion correcta. Abra el SGD y recargue con Ctrl+F5.
pause
exit /b 0

:error
set "SGD_RESULTADO=%errorlevel%"
echo.
echo Actualizacion detenida. Revisar "%~dp0logs\ULTIMO-ERROR.txt".
pause
exit /b %SGD_RESULTADO%

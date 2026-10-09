@echo off
setlocal
rem Sin argumentos verifica los DOCX y prepara un plan, sin consultar la BD.
rem Aplicar: Plantillas-SGD.bat --aplicar --confirmar PLANTILLAS:INIA
call "%~dp0SGD.bat" plantillas %*
set "SGD_PLANTILLAS_EXIT=%errorlevel%"
if "%~1"=="" pause
exit /b %SGD_PLANTILLAS_EXIT%

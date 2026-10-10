@echo off
setlocal
set "PYTHONUTF8=1"
rem Sin --aplicar prepara un plan offline; no conecta a PostgreSQL.
"%~dp0herramientas\python-windows\python.exe" "%~dp0recargar_usuarios.py" %*
exit /b %errorlevel%

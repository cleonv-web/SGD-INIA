@echo off
setlocal
"%~dp0herramientas\python-windows\python.exe" "%~dp0herramientas\actualizar_mpd.py"
exit /b %errorlevel%

#!/usr/bin/env bash
# Instala el servidor Linux y comprueba que el portable incluya el cliente Windows.
# Los puestos Windows se preparan con Cliente-SGD.bat, sin instalar una BD local.
set -euo pipefail
if [[ $# -gt 1 ]]; then
  echo 'Uso: bash Instalar-SGD.sh [servidor]' >&2
  exit 2
fi
if [[ $# -eq 1 ]]; then
  exec bash "$(dirname -- "${BASH_SOURCE[0]}")/SGD.sh" instalar --equipo "$1"
fi
exec bash "$(dirname -- "${BASH_SOURCE[0]}")/SGD.sh" instalar

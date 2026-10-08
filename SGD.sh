#!/usr/bin/env bash
# Linux x64: usa Python incluido. No requiere pip ni Python del sistema.
# Uso: bash SGD.sh compilar | verificar | diagnosticar | respaldar | restaurar.
set -euo pipefail
SGD_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
SGD_PYTHON="$SGD_ROOT/herramientas/python-linux/bin/python3"
if [[ ! -x "$SGD_PYTHON" ]]; then
  mkdir -p "$SGD_ROOT/herramientas/python-linux"
  tar -xzf "$SGD_ROOT/herramientas/python-linux.tar.gz" -C "$SGD_ROOT/herramientas/python-linux" --strip-components=1
  chmod +x "$SGD_PYTHON"
fi
exec "$SGD_PYTHON" "$SGD_ROOT/sgd.py" "${1:-todo}"

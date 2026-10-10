#!/usr/bin/env bash
set -euo pipefail
SGD_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
SGD_PYTHON="$SGD_ROOT/herramientas/python-linux/bin/python3"
if [[ ! -x "$SGD_PYTHON" ]]; then
  printf 'Falta el Python incluido; preparar el portable antes de recargar.\n' >&2
  exit 1
fi
exec "$SGD_PYTHON" "$SGD_ROOT/recargar_usuarios.py" "$@"

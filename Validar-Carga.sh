#!/usr/bin/env bash
# Linux x64. Sólo valida archivos; no conecta ni cambia bases de datos.
# Sin argumentos revisa datos/carga-uti-preparada. Otra área: --carpeta RUTA.
set -euo pipefail
SGD_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
SGD_PYTHON="$SGD_ROOT/herramientas/python-linux/bin/python3"
if [[ ! -x "$SGD_PYTHON" ]]; then
  mkdir -p "$SGD_ROOT/herramientas/python-linux"
  tar -xzf "$SGD_ROOT/herramientas/python-linux.tar.gz" -C "$SGD_ROOT/herramientas/python-linux" --strip-components=1
  chmod +x "$SGD_PYTHON"
fi
exec "$SGD_PYTHON" "$SGD_ROOT/validar_carga.py" "$@"

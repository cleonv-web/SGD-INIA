#!/usr/bin/env bash
# Linux x64. Por defecto revisión offline; no carga ni consulta bases.
set -euo pipefail
SGD_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
SGD_PYTHON="$SGD_ROOT/herramientas/python-linux/bin/python3"
if [[ ! -x "$SGD_PYTHON" ]]; then
  mkdir -p "$SGD_ROOT/herramientas/python-linux"
  tar -xzf "$SGD_ROOT/herramientas/python-linux.tar.gz" -C "$SGD_ROOT/herramientas/python-linux" --strip-components=1
  chmod +x "$SGD_PYTHON"
fi
SGD_ACTION=cargar
if [[ "${1:-}" == --plantilla ]]; then SGD_ACTION=plantilla; shift; fi
if [[ "${1:-}" == --catalogos ]]; then SGD_ACTION=catalogos; shift; fi
exec "$SGD_PYTHON" "$SGD_ROOT/gestion_data.py" "$SGD_ACTION" "$@"

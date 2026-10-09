#!/usr/bin/env bash
# Diagnostica el requisito del puesto; el cliente original es para Windows.
# No instala paquetes Linux ni configura conexiones del SGD.
set -euo pipefail
case "${1:-verificar}" in
  verificar) accion=verificar_cliente ;;
  instalar) accion=instalar_cliente ;;
  *) echo 'Uso: bash Cliente-SGD.sh [verificar|instalar]' >&2; exit 2 ;;
esac
if [[ $# -gt 1 ]]; then
  echo 'Uso: bash Cliente-SGD.sh [verificar|instalar]' >&2
  exit 2
fi
exec bash "$(dirname -- "${BASH_SOURCE[0]}")/SGD.sh" "$accion"

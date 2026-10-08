#!/usr/bin/env bash
# Actualiza una instalacion existente y conserva la base y los documentos.
# Compilar genera los WAR. Desplegar los pone en funcionamiento.
set -euo pipefail
SGD_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
trap 'sgd_resultado=$?; printf "\nActualizacion detenida. Revisar %s/logs/ULTIMO-ERROR.txt\n" "$SGD_ROOT" >&2; exit "$sgd_resultado"' ERR
for sgd_accion in compilar iniciar desplegar verificar; do
  printf '\nEjecutando: %s\n' "$sgd_accion"
  bash "$SGD_ROOT/SGD.sh" "$sgd_accion"
done
printf '\nActualizacion correcta. Abra el SGD y recargue con Ctrl+F5.\n'

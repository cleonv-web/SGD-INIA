#!/usr/bin/env bash
# Sin argumentos verifica los DOCX y prepara un plan, sin consultar la BD.
# Aplicar: bash Plantillas-SGD.sh --aplicar --confirmar PLANTILLAS:INIA
set -euo pipefail
exec bash "$(dirname -- "${BASH_SOURCE[0]}")/SGD.sh" plantillas "$@"

#!/usr/bin/env bash
# Diagnostica, compila fuentes, inicia PostgreSQL, carga una base nueva,
# configura Payara, despliega y comprueba SQL/JDBC/HTTP.
set -euo pipefail
exec bash "$(dirname -- "${BASH_SOURCE[0]}")/SGD.sh" todo

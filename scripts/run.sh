#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
bash "$project_root/scripts/build.sh"

if [[ -n "${JAVA_HOME:-}" ]]; then
    java_command="$JAVA_HOME/bin/java"
else
    java_command="java"
fi

cd -- "$project_root"
exec "$java_command" -Dfile.encoding=UTF-8 \
    -jar target/war-of-the-five-kings.jar "$@"

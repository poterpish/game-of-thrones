#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
bash "$project_root/scripts/build.sh"
cd -- "$project_root"

if [[ -n "${JAVA_HOME:-}" ]]; then
    javac_command="$JAVA_HOME/bin/javac"
    java_command="$JAVA_HOME/bin/java"
else
    javac_command="javac"
    java_command="java"
fi

rm -rf -- target/check-classes
mkdir -p target/check-classes
find src/test/java -type f -name '*.java' -print \
    | LC_ALL=C sort \
    | sed 's/.*/"&"/' > target/check-sources.txt
if [[ ! -s target/check-sources.txt ]]; then
    echo "No check sources found in src/test/java." >&2
    exit 1
fi

# The jar is on the classpath so the checks exercise the packaged application.
"$javac_command" --release 17 -encoding UTF-8 \
    -cp target/war-of-the-five-kings.jar \
    -d target/check-classes @target/check-sources.txt

classpath_separator=":"
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) classpath_separator=";" ;;
esac
"$java_command" -cp "target/war-of-the-five-kings.jar${classpath_separator}target/check-classes" \
    edu.wotfk.AllChecks

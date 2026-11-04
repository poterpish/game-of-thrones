#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd -- "$project_root"

if [[ -n "${JAVA_HOME:-}" ]]; then
    javac_command="$JAVA_HOME/bin/javac"
    jar_command="$JAVA_HOME/bin/jar"
else
    javac_command="javac"
    jar_command="jar"
fi

for tool_command in "$javac_command" "$jar_command"; do
    if ! command -v "$tool_command" >/dev/null 2>&1; then
        echo "JDK 17+ is required. Check JAVA_HOME and PATH." >&2
        exit 1
    fi
done

mkdir -p target
# Remove only this project's generated classes and executable JAR.
rm -rf -- target/classes
rm -f -- target/war-of-the-five-kings.jar
mkdir -p target/classes

find src/main/java -type f -name '*.java' -print \
    | LC_ALL=C sort \
    | sed 's/.*/"&"/' > target/sources.txt

if [[ ! -s target/sources.txt ]]; then
    echo "No Java sources found in src/main/java." >&2
    exit 1
fi

"$javac_command" --release 17 -encoding UTF-8 \
    -d target/classes @target/sources.txt

if [[ -d src/main/resources ]]; then
    cp -R src/main/resources/. target/classes/
fi

"$jar_command" --create --file target/war-of-the-five-kings.jar \
    --main-class edu.wotfk.Main -C target/classes .

echo "Built: target/war-of-the-five-kings.jar"

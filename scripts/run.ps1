$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

& (Join-Path $PSScriptRoot 'build.ps1')
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

if ($env:JAVA_HOME) {
    $javaCommand = Join-Path $env:JAVA_HOME 'bin/java.exe'
} else {
    $javaCommand = 'java'
}

Push-Location $projectRoot
try {
    & $javaCommand '-Dfile.encoding=UTF-8' -jar target/war-of-the-five-kings.jar @args
    $applicationExitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $applicationExitCode

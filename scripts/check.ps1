$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

& (Join-Path $PSScriptRoot 'build.ps1')
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

if ($env:JAVA_HOME) {
    $javacCommand = Join-Path $env:JAVA_HOME 'bin/javac.exe'
    $javaCommand = Join-Path $env:JAVA_HOME 'bin/java.exe'
} else {
    $javacCommand = 'javac'
    $javaCommand = 'java'
}

Push-Location $projectRoot
try {
    if (Test-Path -LiteralPath 'target/check-classes') {
        Remove-Item -LiteralPath 'target/check-classes' -Recurse -Force
    }
    New-Item -ItemType Directory -Path 'target/check-classes' -Force | Out-Null

    $sources = @(Get-ChildItem -Path 'src/test/java' -Filter '*.java' -File -Recurse |
        Sort-Object FullName |
        ForEach-Object {
            $relative = $_.FullName.Substring($projectRoot.Length + 1).Replace('\', '/')
            '"' + $relative + '"'
        })
    if ($sources.Count -eq 0) {
        throw 'No check sources found in src/test/java.'
    }
    $sourceList = Join-Path $projectRoot 'target/check-sources.txt'
    [System.IO.File]::WriteAllLines(
        $sourceList, [string[]]$sources, [System.Text.UTF8Encoding]::new($false))

    & $javacCommand --release 17 -encoding UTF-8 `
        -cp target/war-of-the-five-kings.jar `
        -d target/check-classes '@target/check-sources.txt'
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

    & $javaCommand -cp 'target/war-of-the-five-kings.jar;target/check-classes' `
        edu.wotfk.ui.ConsoleApplicationChecks
    $checkExitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $checkExitCode

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

if ($env:JAVA_HOME) {
    $javacCommand = Join-Path $env:JAVA_HOME 'bin/javac.exe'
    $jarCommand = Join-Path $env:JAVA_HOME 'bin/jar.exe'
} else {
    $javacCommand = 'javac'
    $jarCommand = 'jar'
}

foreach ($toolCommand in @($javacCommand, $jarCommand)) {
    if (-not (Get-Command $toolCommand -ErrorAction SilentlyContinue)) {
        throw 'JDK 17+ is required. Check JAVA_HOME and PATH.'
    }
}

Push-Location $projectRoot
try {
    New-Item -ItemType Directory -Path 'target' -Force | Out-Null
    # Remove only this project's generated classes and executable JAR.
    if (Test-Path -LiteralPath 'target/classes') {
        Remove-Item -LiteralPath 'target/classes' -Recurse -Force
    }
    if (Test-Path -LiteralPath 'target/war-of-the-five-kings.jar') {
        Remove-Item -LiteralPath 'target/war-of-the-five-kings.jar' -Force
    }
    New-Item -ItemType Directory -Path 'target/classes' -Force | Out-Null

    $sources = @(Get-ChildItem -Path 'src/main/java' -Filter '*.java' -File -Recurse |
        Sort-Object FullName |
        ForEach-Object {
            $relative = $_.FullName.Substring($projectRoot.Length + 1).Replace('\', '/')
            '"' + $relative + '"'
        })
    if ($sources.Count -eq 0) {
        throw 'No Java sources found in src/main/java.'
    }

    $sourceList = Join-Path $projectRoot 'target/sources.txt'
    [System.IO.File]::WriteAllLines(
        $sourceList, [string[]]$sources, [System.Text.UTF8Encoding]::new($false))

    & $javacCommand --release 17 -encoding UTF-8 -d target/classes '@target/sources.txt'
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

    if (Test-Path -LiteralPath 'src/main/resources') {
        Get-ChildItem -LiteralPath 'src/main/resources' -Force |
            Copy-Item -Destination 'target/classes' -Recurse -Force
    }

    & $jarCommand --create --file target/war-of-the-five-kings.jar `
        --main-class edu.wotfk.Main -C target/classes .
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

    Write-Host 'Built: target/war-of-the-five-kings.jar'
} finally {
    Pop-Location
}

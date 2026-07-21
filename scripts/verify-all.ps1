[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$verificationRoot = Join-Path ([System.IO.Path]::GetTempPath()) (
    'lab211-verification-' + [guid]::NewGuid().ToString('N')
)

$projects = @(
    @{ Name = 'Lab 1'; Folder = 'lab1'; TestClass = 'Lab1DeepVerification'; Expected = 34 },
    @{ Name = 'Lab 2'; Folder = 'lab2'; TestClass = 'Lab2DeepVerification'; Expected = 62 },
    @{ Name = 'Lab 3'; Folder = 'lab3'; TestClass = 'Lab3DeepVerification'; Expected = 57 }
)

function Get-CompilerArguments {
    $versionText = (& javac -version 2>&1 | Out-String).Trim()
    Write-Host "Compiler: $versionText"
    if ($versionText -match 'javac 1\.8') {
        return @('-source', '1.8', '-target', '1.8')
    }
    return @('--release', '8')
}

New-Item -ItemType Directory -Path $verificationRoot | Out-Null
$compilerArguments = Get-CompilerArguments
$totalExpected = 0

try {
    foreach ($project in $projects) {
        $labRoot = Join-Path $repoRoot ('labs\' + $project.Folder)
        $classesRoot = Join-Path $verificationRoot $project.Folder
        New-Item -ItemType Directory -Path $classesRoot | Out-Null

        $sourceFiles = @(
            Get-ChildItem -LiteralPath (Join-Path $labRoot 'src') -Recurse -Filter '*.java' |
                ForEach-Object { $_.FullName }
        )
        $testFile = Join-Path $labRoot ('testcases\' + $project.TestClass + '.java')

        Write-Host "`n== $($project.Name): compile =="
        & javac @compilerArguments -encoding UTF-8 -Xlint:all -d $classesRoot @sourceFiles $testFile
        if ($LASTEXITCODE -ne 0) {
            throw "$($project.Name) compilation failed with exit code $LASTEXITCODE."
        }

        Write-Host "== $($project.Name): run $($project.TestClass) =="
        Push-Location $labRoot
        try {
            $testOutput = & java -cp $classesRoot $project.TestClass 2>&1
            $testExitCode = $LASTEXITCODE
            $testOutput | ForEach-Object { Write-Host $_ }
        } finally {
            Pop-Location
        }

        if ($testExitCode -ne 0) {
            throw "$($project.Name) verification failed with exit code $testExitCode."
        }

        $expectedResult = "RESULT: $($project.Expected) passed, 0 failed"
        if (($testOutput -join "`n") -notmatch [regex]::Escape($expectedResult)) {
            throw "$($project.Name) did not report the expected result: $expectedResult"
        }
        $totalExpected += $project.Expected
    }

    Write-Host "`nVERIFICATION PASSED: $totalExpected checks passed, 0 failed."
} finally {
    $resolvedVerificationRoot = [System.IO.Path]::GetFullPath($verificationRoot)
    $resolvedSystemTemp = [System.IO.Path]::GetFullPath([System.IO.Path]::GetTempPath())
    $safePrefix = Join-Path $resolvedSystemTemp 'lab211-verification-'
    if ($resolvedVerificationRoot.StartsWith($safePrefix, [System.StringComparison]::OrdinalIgnoreCase) -and
            (Test-Path -LiteralPath $resolvedVerificationRoot)) {
        Remove-Item -LiteralPath $resolvedVerificationRoot -Recurse -Force
    }
}

$ErrorActionPreference = "Stop"

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$javaHome = "C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot"
$java = Join-Path $javaHome "bin\java.exe"
$maven = Join-Path $env:USERPROFILE "Downloads\maven-install\apache-maven-3.9.16\bin\mvn.cmd"
$jar = Join-Path $projectDir "target\bibliotech-0.0.1-SNAPSHOT.jar"

if (-not (Test-Path $java)) { throw "Java 17 não encontrado: $java" }
if (-not (Test-Path $maven)) { throw "Maven não encontrado: $maven" }

$listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($listener) {
    try {
        $health = Invoke-WebRequest -UseBasicParsing "http://127.0.0.1:8080/api/livros" -TimeoutSec 5
        if ($health.StatusCode -eq 200) {
            Write-Host "A API já responde em http://localhost:8080/api/livros."
            exit 0
        }
    } catch {
        throw "A porta 8080 está ocupada pelo PID $($listener[0].OwningProcess), mas a API não responde."
    }
}

if (-not (Test-NetConnection 127.0.0.1 -Port 3306 -InformationLevel Quiet)) {
    throw "O MySQL não responde em localhost:3306. Verifique o serviço MySQL57."
}

$env:DB_USERNAME = [Environment]::GetEnvironmentVariable("DB_USERNAME", "Process")
$env:DB_PASSWORD = [Environment]::GetEnvironmentVariable("DB_PASSWORD", "Process")
if (-not $env:DB_USERNAME) {
    $env:DB_USERNAME = [Environment]::GetEnvironmentVariable("DB_USERNAME", "User")
}
if (-not $env:DB_PASSWORD) {
    $env:DB_PASSWORD = [Environment]::GetEnvironmentVariable("DB_PASSWORD", "User")
}
if (-not $env:DB_USERNAME -or -not $env:DB_PASSWORD) {
    throw "Defina DB_USERNAME e DB_PASSWORD nas variáveis de ambiente do Windows antes de iniciar."
}

Set-Location $projectDir
& $maven -q -DskipTests package
if ($LASTEXITCODE -ne 0) { throw "A compilação falhou; veja a mensagem do Maven acima." }

$env:JAVA_HOME = $javaHome
Write-Host "Iniciando API em http://localhost:8080/api/livros. Mantenha esta janela aberta."
& $java -jar $jar

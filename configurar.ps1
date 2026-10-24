# Crea application-local.properties (queda fuera de Git) con la cuenta del administrador.
# Uso, desde la carpeta que contiene pom.xml:   .\configurar.ps1
$ErrorActionPreference = "Stop"
$destino = Join-Path $PSScriptRoot "application-local.properties"

function Leer-Secreto($mensaje) {
    $s = Read-Host $mensaje -AsSecureString
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($s)
    try { return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
}

if (Test-Path $destino) {
    $r = Read-Host "Ya existe application-local.properties. Reemplazar? (s/n)"
    if ($r -ne "s") { Write-Host "Sin cambios."; exit 0 }
}

$correo = Read-Host "Correo del administrador [admin@bodegafrancisco.pe]"
if ([string]::IsNullOrWhiteSpace($correo)) { $correo = "admin@bodegafrancisco.pe" }
$passAdmin = Leer-Secreto "Clave del administrador (minimo 8, con letras y numeros)"
if ($passAdmin.Length -lt 8 -or $passAdmin -notmatch '[A-Za-z]' -or $passAdmin -notmatch '[0-9]') {
    Write-Host "La clave no cumple la politica (8+ caracteres, letras y numeros)."; exit 1
}

$lineas = @(
    "bodega.admin.correo=$($correo.Replace('\','\\'))",
    "bodega.admin.password=$($passAdmin.Replace('\','\\'))"
)
Set-Content -Path $destino -Value $lineas -Encoding ascii
Write-Host "Listo: se creo $destino (esta excluido de Git)."

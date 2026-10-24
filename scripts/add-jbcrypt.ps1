<#
.SYNOPSIS
Copia o arquivo jbcrypt-0.4.jar para a pasta src/lib do projeto e atualiza permissões.

.PARAMETER Source
Caminho completo do arquivo jbcrypt-0.4.jar. Se não informado, o script procura em $HOME\Downloads.

.EXAMPLE
.
#>

param(
    [string]$Source
)

Add-Type -AssemblyName System.Windows.Forms

function Select-FileDialog {
    $ofd = New-Object System.Windows.Forms.OpenFileDialog
    $ofd.Filter = "JAR files (*.jar)|*.jar|All files (*.*)|*.*"
    $ofd.InitialDirectory = [Environment]::GetFolderPath('Desktop')
    $ofd.Multiselect = $false
    if ($ofd.ShowDialog() -eq 'OK') { return $ofd.FileName }
    return $null
}

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$libDir = Join-Path $projectRoot '..\src\lib' | Resolve-Path -Relative
$libDir = Join-Path $projectRoot '..\src\lib'
if (-not (Test-Path $libDir)) { New-Item -ItemType Directory -Path $libDir | Out-Null }

if (-not $Source) {
    $default = Join-Path $env:USERPROFILE 'Downloads\jbcrypt-0.4.jar'
    if (Test-Path $default) { $Source = $default }
    else {
        Write-Host "Arquivo jbcrypt não encontrado em Downloads. Abra o seletor para localizar o JAR."
        $sel = Select-FileDialog
        if ($sel) { $Source = $sel } else { Write-Host "Nenhum arquivo selecionado. Abortando."; exit 1 }
    }
}

if (-not (Test-Path $Source)) {
    Write-Host "Arquivo não encontrado: $Source"; exit 1
}

$dest = Join-Path $libDir 'jbcrypt-0.4.jar'
try {
    Copy-Item -Path $Source -Destination $dest -Force
    Write-Host "jbcrypt copiado para: $dest"
} catch {
    Write-Host "Erro ao copiar: $_"; exit 1
}

Write-Host "Pronto. Agora abra o NetBeans e verifique Project Properties → Libraries; o JAR já está em src/lib."
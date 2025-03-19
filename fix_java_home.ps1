# Fix JAVA_HOME Script
# Run this script with administrator privileges

# Function to check if running as administrator
function Test-Administrator {
    $user = [Security.Principal.WindowsIdentity]::GetCurrent();
    (New-Object Security.Principal.WindowsPrincipal $user).IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
}

# Function to find JDK installations
function Find-JDK {
    $jdkPaths = @()
    
    # Check common locations
    $commonPaths = @(
        "C:\Program Files\Java",
        "C:\Program Files (x86)\Java",
        "C:\Java",
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\Amazon Corretto",
        "C:\Program Files\Microsoft\jdk",
        "C:\Users\$env:USERNAME\AppData\Local\Programs\Eclipse Adoptium"
    )
    
    foreach ($path in $commonPaths) {
        if (Test-Path $path) {
            # Find directories containing "jdk" in the name
            $foundDirs = Get-ChildItem -Path $path -Directory | Where-Object { $_.Name -match "jdk" }
            
            foreach ($dir in $foundDirs) {
                $jdkPath = $dir.FullName
                # Verify it's a JDK by checking if javac.exe exists
                if (Test-Path "$jdkPath\bin\javac.exe") {
                    $jdkPaths += $jdkPath
                }
            }
        }
    }
    
    return $jdkPaths
}

# Function to get Java version from a JDK path
function Get-JavaVersion {
    param (
        [string]$jdkPath
    )
    
    try {
        $javaExe = "$jdkPath\bin\java.exe"
        if (Test-Path $javaExe) {
            $versionOutput = & $javaExe "-version" 2>&1
            $versionString = $versionOutput[0].ToString()
            
            # Extract version number using regex
            if ($versionString -match '"([0-9]+\.[0-9]+\.[0-9]+[^"]*)"') {
                return $matches[1]
            }
        }
        return "Unknown"
    }
    catch {
        return "Error"
    }
}

# Main script
Write-Host "JAVA_HOME Fix Script" -ForegroundColor Cyan
Write-Host "===================" -ForegroundColor Cyan
Write-Host

# Check for admin privileges
if (-not (Test-Administrator)) {
    Write-Host "This script requires administrator privileges to set system environment variables." -ForegroundColor Red
    Write-Host "Please run PowerShell as Administrator and try again." -ForegroundColor Red
    Write-Host
    Write-Host "Press any key to exit..."
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit
}

# Find JDK installations
Write-Host "Searching for JDK installations..." -ForegroundColor Yellow
$jdkInstallations = Find-JDK

if ($jdkInstallations.Count -eq 0) {
    Write-Host "No JDK installations found." -ForegroundColor Red
    Write-Host "Please install a JDK (version 11 or 17 recommended for ElectricianApp)." -ForegroundColor Red
    Write-Host
    Write-Host "Press any key to exit..."
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit
}

# Display found JDKs
Write-Host "Found the following JDK installations:" -ForegroundColor Green
$jdkList = @()
for ($i=0; $i -lt $jdkInstallations.Count; $i++) {
    $jdkPath = $jdkInstallations[$i]
    $version = Get-JavaVersion -jdkPath $jdkPath
    $jdkList += [PSCustomObject]@{
        Index = $i
        Path = $jdkPath
        Version = $version
    }
    Write-Host "$i. $jdkPath (Version: $version)"
}
Write-Host

# Check for JDK 11 or 17
$recommended = $jdkList | Where-Object { $_.Version -like "11.*" -or $_.Version -like "17.*" } | Select-Object -First 1
$selectedIndex = 0

if ($null -ne $recommended) {
    Write-Host "Recommended JDK found (version 11 or 17): $($recommended.Path)" -ForegroundColor Green
    $selectedIndex = $recommended.Index
} else {
    # Choose the first JDK if no recommended version found
    Write-Host "No JDK 11 or 17 found. Using the first available JDK." -ForegroundColor Yellow
}

$selectedJDK = $jdkInstallations[$selectedIndex]
Write-Host "Selected JDK: $selectedJDK" -ForegroundColor Cyan
Write-Host

# Set JAVA_HOME
$currentJavaHome = [Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
Write-Host "Current JAVA_HOME: $currentJavaHome" -ForegroundColor Yellow

Write-Host "Setting JAVA_HOME to: $selectedJDK" -ForegroundColor Yellow
[Environment]::SetEnvironmentVariable("JAVA_HOME", $selectedJDK, "Machine")

# Update PATH if needed
$currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
$javaHomeBin = "$selectedJDK\bin"

if ($currentPath -notlike "*$javaHomeBin*") {
    Write-Host "Adding Java bin directory to PATH..." -ForegroundColor Yellow
    [Environment]::SetEnvironmentVariable("PATH", "$currentPath;$javaHomeBin", "Machine")
    Write-Host "PATH updated." -ForegroundColor Green
} else {
    Write-Host "Java bin directory is already in PATH." -ForegroundColor Green
}

# Verify settings
$newJavaHome = [Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
Write-Host
Write-Host "JAVA_HOME has been set to: $newJavaHome" -ForegroundColor Green
Write-Host
Write-Host "NOTE: You will need to restart any command prompts or applications" -ForegroundColor Yellow
Write-Host "      for these changes to take effect." -ForegroundColor Yellow
Write-Host
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

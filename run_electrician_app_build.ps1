# ElectricianApp PowerShell Build Script
Write-Host "===================================`nElectricianApp PowerShell Build Script`n===================================" -ForegroundColor Cyan

# Set up JDK 17
$JDK_PATH = "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
$env:JAVA_HOME = $JDK_PATH
$env:PATH = "$JDK_PATH\bin;$env:PATH"

Write-Host "`nStep 1: Setting up JDK 17`n-----------------------" -ForegroundColor Yellow
Write-Host "Using JDK from: $env:JAVA_HOME"
& "$env:JAVA_HOME\bin\java" -version

# Navigate to the project directory and run the original fix script
Write-Host "`nStep 2: Running build script...`n-----------------------" -ForegroundColor Green
Set-Location -Path "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
& ".\fix_sourceset_and_build.bat"

Write-Host "`nPowerShell build process completed!`n" -ForegroundColor Cyan

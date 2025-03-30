# ElectricianApp PowerShell Build Script
Write-Host "===================================`nElectricianApp PowerShell Build Script`n===================================" -ForegroundColor Cyan

# Navigate to the project directory
Set-Location -Path "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"

# Set up JDK 17
$JDK_PATH = "C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
$env:JAVA_HOME = $JDK_PATH
$env:PATH = "$JDK_PATH\bin;$env:PATH"

Write-Host "`nStep 1: Setting up JDK 17`n-----------------------" -ForegroundColor Yellow
Write-Host "Using JDK from: $env:JAVA_HOME"
& "$env:JAVA_HOME\bin\java" -version

# Run the batch script which has all the necessary logic
Write-Host "`nRunning build script...`n" -ForegroundColor Green
& "C:\Users\Chris Boyd\Desktop\run_build.bat"

Write-Host "`nPowerShell build process completed!`n" -ForegroundColor Cyan

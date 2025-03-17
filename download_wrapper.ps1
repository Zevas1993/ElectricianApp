# PowerShell script to download Gradle wrapper jar file
$url = "https://github.com/gradle/gradle/raw/v7.5.0/gradle/wrapper/gradle-wrapper.jar"
$outputPath = "gradle/wrapper/gradle-wrapper.jar"

New-Item -ItemType Directory -Force -Path "gradle/wrapper" | Out-Null
Invoke-WebRequest -Uri $url -OutFile $outputPath

Write-Host "Downloaded Gradle wrapper jar to $outputPath"

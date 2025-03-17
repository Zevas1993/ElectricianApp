# PowerShell script to create a GitHub release and upload APK file
# This script assumes you've already committed your changes to your local repo

$token = Read-Host "Enter your GitHub Personal Access Token" -AsSecureString
$ptbstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($token)
$plainToken = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($ptbstr)

# GitHub repository details
$owner = "Zevas1993"
$repo = "ElectricianApp"
$tagName = "v1.0.0"
$releaseName = "ElectricianApp v1.0.0"
$releaseDescription = "Initial release of ElectricianApp with modular Android architecture"
$apkPath = "app/build/outputs/apk/debug/app-debug.apk"

Write-Host "Creating GitHub release..." -ForegroundColor Green

# Create the release using GitHub API
$releaseUrl = "https://api.github.com/repos/$owner/$repo/releases"
$headers = @{
    Authorization = "token $plainToken"
    Accept = "application/vnd.github.v3+json"
}
$releaseBody = @{
    tag_name = $tagName
    name = $releaseName
    body = $releaseDescription
    draft = $false
    prerelease = $false
} | ConvertTo-Json

try {
    $releaseResponse = Invoke-RestMethod -Uri $releaseUrl -Method Post -Headers $headers -Body $releaseBody -ContentType "application/json"
    Write-Host "Release created successfully" -ForegroundColor Green
    Write-Host "Release URL: $($releaseResponse.html_url)" -ForegroundColor Cyan
    
    # Now upload the APK file as an asset to the release
    Write-Host "Uploading APK file to release..." -ForegroundColor Green
    
    $uploadUrl = $releaseResponse.upload_url -replace "{\?name,label}", "?name=app-debug.apk"
    $fileBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $apkPath))
    
    $uploadHeaders = @{
        Authorization = "token $plainToken"
        Accept = "application/vnd.github.v3+json"
    }
    
    $uploadResponse = Invoke-RestMethod -Uri $uploadUrl -Method Post -Headers $uploadHeaders -Body $fileBytes -ContentType "application/octet-stream"
    Write-Host "APK uploaded successfully" -ForegroundColor Green
    Write-Host "Download URL: $($uploadResponse.browser_download_url)" -ForegroundColor Cyan
}
catch {
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    
    $errorDetails = $_ | ConvertFrom-Json
    if ($errorDetails) {
        Write-Host "Error Details: $errorDetails" -ForegroundColor Red
    }
}

Write-Host "`nInstructions to push your code to GitHub:" -ForegroundColor Yellow
Write-Host "1. Run: git push origin main" -ForegroundColor Yellow
Write-Host "2. When prompted, enter your GitHub username" -ForegroundColor Yellow
Write-Host "3. For password, use your Personal Access Token (not your GitHub password)" -ForegroundColor Yellow
Write-Host "`nNote: If you haven't already, you might need to authenticate first:" -ForegroundColor Yellow
Write-Host "git config --global user.name 'Your Name'" -ForegroundColor Yellow
Write-Host "git config --global user.email 'your.email@example.com'" -ForegroundColor Yellow

Write-Host "`nPress any key to continue..." -ForegroundColor Green
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

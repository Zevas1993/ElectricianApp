# Instructions for Pushing to GitHub and Publishing the APK

## Option 1: Push Code and Create Release (Recommended)

1. **Push the code to GitHub**
   ```
   cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
   git push -u origin main
   ```
   
   When prompted:
   - Username: Your GitHub username
   - Password: Use your GitHub Personal Access Token (not your password)

2. **Create a GitHub Release with the APK**
   ```
   cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
   powershell -ExecutionPolicy Bypass -File create_release.ps1
   ```
   
   The script will:
   - Prompt you for your GitHub Personal Access Token
   - Create a release on GitHub
   - Upload the APK file to the release
   - Provide download links

## Option 2: Push Code Only

If you only want to push the code without creating a release:

```
cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
git push -u origin main
```

## Create a GitHub Personal Access Token (if you don't have one)

1. Go to GitHub.com and sign in
2. Click your profile photo → Settings
3. In the left sidebar, click Developer settings
4. Click Personal access tokens → Tokens (classic)
5. Click "Generate new token" → "Generate new token (classic)"
6. Give your token a descriptive name
7. Select the following scopes:
   - repo (Full control of private repositories)
8. Click "Generate token"
9. **Copy your token immediately - it won't be shown again!**

## Notes on the APK

The current APK is a placeholder, as the build process requires:
- Java JDK installation 
- Android SDK configuration

For a proper build:
1. Install Java JDK (version 11 or higher)
2. Install Android Studio (which includes the SDK)
3. Run `setup_and_build.bat` to properly build the APK
4. Then push to GitHub and create a release

## Building a Real APK

To build a proper APK instead of using the placeholder:

1. Install Java JDK 11+ from:
   - Oracle: https://www.oracle.com/java/technologies/downloads/
   - Or OpenJDK: https://adoptium.net/

2. Run the setup script to build the APK:
   ```
   cd "C:\Users\Chris Boyd\Documents\GitHub\ElectricianApp"
   .\setup_and_build.bat
   ```

3. After successful build, run the release script:
   ```
   powershell -ExecutionPolicy Bypass -File create_release.ps1

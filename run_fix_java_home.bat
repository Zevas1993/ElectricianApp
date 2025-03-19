@echo off
echo ===== JAVA_HOME Fix Launcher =====
echo.
echo This will launch the PowerShell script to fix your JAVA_HOME environment variable.
echo Administrator privileges are required.
echo.
echo Press any key to continue...
pause > nul

:: Run the PowerShell script with elevated privileges
powershell -Command "Start-Process powershell.exe -ArgumentList '-ExecutionPolicy Bypass -File \"%~dp0fix_java_home.ps1\"' -Verb RunAs"

echo.
echo If prompted by User Account Control, please click Yes to allow administrator access.
echo.
echo Note: After fixing JAVA_HOME, please restart your command prompt
echo or run the ElectricianApp build scripts in a new command prompt window.
echo.
pause

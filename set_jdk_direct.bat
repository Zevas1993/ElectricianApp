@echo off
echo ===================================
echo Direct JDK Configuration for Gradle
echo ===================================
echo.

set "JDK_PATHS=C:\Program Files\Java\jdk-17 C:\Program Files\Java\jdk-21 C:\Program Files\Eclipse Adoptium\jdk-17.0.10.7-hotspot C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot C:\Program Files (x86)\Java\jdk-17 C:\Program Files\Amazon Corretto\jdk17.0.9_8"

echo Checking for specific JDK installations...
for %%P in (%JDK_PATHS%) do (
    if exist "%%P\bin\java.exe" (
        echo Found JDK at: %%P
        
        echo Setting JAVA_HOME system variable...
        setx JAVA_HOME "%%P" /m
        
        echo Updating Gradle config...
        if not exist "gradle" mkdir gradle
        
        echo # Gradle JDK Configuration> gradle\config.properties
        echo # This file specifies the JDK to use for Gradle builds>> gradle\config.properties
        echo.>> gradle\config.properties
        echo # Direct path configuration>> gradle\config.properties
        echo org.gradle.java.home=%%P>> gradle\config.properties
        
        echo Setting current session variables...
        set "JAVA_HOME=%%P"
        set "PATH=%%P\bin;%PATH%"
        
        echo Testing Java configuration:
        "%%P\bin\java.exe" -version
        
        goto :success
    )
)

echo No specific JDK installation found in standard locations.
echo Searching for any JDK...

for %%D in (C:\ D:\ E:\) do (
    if exist "%%D" (
        echo Searching %%D drive...
        for /f "tokens=*" %%J in ('dir /b /s "%%D\jdk*" 2^>nul') do (
            if exist "%%J\bin\java.exe" (
                echo Found JDK at: %%J
                
                echo Setting JAVA_HOME system variable...
                setx JAVA_HOME "%%J" /m
                
                echo Updating Gradle config...
                if not exist "gradle" mkdir gradle
                
                echo # Gradle JDK Configuration> gradle\config.properties
                echo # This file specifies the JDK to use for Gradle builds>> gradle\config.properties
                echo.>> gradle\config.properties
                echo # Direct path configuration>> gradle\config.properties
                echo org.gradle.java.home=%%J>> gradle\config.properties
                
                echo Setting current session variables...
                set "JAVA_HOME=%%J"
                set "PATH=%%J\bin;%PATH%"
                
                echo Testing Java configuration:
                "%%J\bin\java.exe" -version
                
                goto :success
            )
        )
    )
)

echo.
echo No JDK installation found. Please install Java JDK 17 manually.
echo.
goto :end

:success
echo.
echo JDK configuration complete.
echo.
echo Now executing a test build to verify the configuration...
echo.
call gradlew.bat clean --info

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build test failed. Please check the error messages above.
) else (
    echo.
    echo Configuration successful! You can now build the app with fixed-gradle-build.bat
)

:end
pause

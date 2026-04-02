@echo off
:: ─────────────────────────────────────────────────────────────────────────────
:: run-backend.bat
:: Compiles and runs the pure-Java food ordering backend (no Maven/Spring needed)
:: ─────────────────────────────────────────────────────────────────────────────

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
set JAVAC=%JAVA_HOME%\bin\javac.exe
set JAR_TOOL=%JAVA_HOME%\bin\jar.exe
set JAVA=%JAVA_HOME%\bin\java.exe

set SRC=backend\src\main\java
set OUT=backend\target\classes
set JAR_FILE=backend\target\food-ordering-backend.jar

echo.
echo ============================================================
echo   Food Ordering Backend — Pure Java (No Framework)
echo ============================================================
echo.

:: Create output directory
if not exist "%OUT%" mkdir "%OUT%"

:: Collect all .java source files
echo [1/3] Collecting source files...
dir /s /b "%SRC%\*.java" > "%TEMP%\sources.txt" 2>nul
if %errorlevel% neq 0 (echo ERROR: Could not find source files & exit /b 1)

for /f %%i in ('type "%TEMP%\sources.txt" ^| find /c /v ""') do set COUNT=%%i
echo      Found %COUNT% Java source files.

:: Compile
echo.
echo [2/3] Compiling...
"%JAVAC%" -source 17 -target 17 -encoding UTF-8 -d "%OUT%" "@%TEMP%\sources.txt"
if %errorlevel% neq 0 (echo ERROR: Compilation failed & exit /b 1)
echo      Compilation successful!

:: Package into JAR
echo.
echo [3/3] Packaging JAR...
(echo Manifest-Version: 1.0 & echo Main-Class: com.example.foodorder.Main & echo.) > "%TEMP%\MANIFEST.MF"
"%JAR_TOOL%" cfm "%JAR_FILE%" "%TEMP%\MANIFEST.MF" -C "%OUT%" .
if %errorlevel% neq 0 (echo ERROR: JAR creation failed & exit /b 1)
echo      JAR created: %JAR_FILE%

:: Run
echo.
echo ============================================================
echo   Starting server on http://localhost:8080
echo   Press Ctrl+C to stop.
echo ============================================================
echo.
"%JAVA%" -jar "%JAR_FILE%"

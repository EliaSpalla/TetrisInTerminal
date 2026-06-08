@echo off
chcp 65001 >nul
title Avvio programma...

:: Vai nella cartella dove si trova questo file .bat
cd /d "%~dp0"

:: Controlla se Java è installato
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRORE: Java non trovato. Installa Java e riprova.
    pause
    exit /b 1
)

:: Compila solo se Main.class non esiste o è più vecchio di Main.java
if not exist Main.class goto COMPILE
for /f %%i in ('powershell -command "(Get-Item Main.java).LastWriteTime -gt (Get-Item Main.class).LastWriteTime"') do set NEEDCOMPILE=%%i
if "%NEEDCOMPILE%"=="True" goto COMPILE
goto RUN

:COMPILE
echo Compilazione in corso...
javac -cp .;lanterna.jar -encoding UTF-8 Main.java
if %errorlevel% neq 0 (
    echo.
    echo ERRORE: Compilazione fallita. Controlla il codice sorgente.
    pause
    exit /b 1
)
echo Compilazione completata.

:RUN
start "" javaw -cp .;lanterna.jar Main

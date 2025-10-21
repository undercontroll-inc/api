@echo off
setlocal enabledelayedexpansion

:: Diretório do script atual (com barra no final)
set "SCRIPT_DIR=%~dp0"

:: Diretório onde salvar as chaves (relativo à pasta scripts): ..\src\main\resources\keys
set "KEYS_DIR=%SCRIPT_DIR%..\src\main\resources\keys"

:: Remove possível barra dupla ou normaliza (opcional)
:: Criar o diretório se não existir
if not exist "%KEYS_DIR%" (
    mkdir "%KEYS_DIR%"
)

:: Gera as chaves se não existirem
if not exist "%KEYS_DIR%\private.pem" (
    echo Gerando chaves RSA de DEV em %KEYS_DIR%...
    openssl genpkey -algorithm RSA -out "%KEYS_DIR%\private.pem" -pkeyopt rsa_keygen_bits:2048
    openssl rsa -pubout -in "%KEYS_DIR%\private.pem" -out "%KEYS_DIR%\public.pem"
    echo Chaves geradas em %KEYS_DIR%
) else (
    echo Chaves ja existem em %KEYS_DIR%
)

endlocal

@echo off
setlocal enabledelayedexpansion

:: Diretório onde salvar as chaves
set KEYS_DIR=src\main\resources\keys

:: Cria o diretório se não existir
if not exist "%KEYS_DIR%" (
    mkdir "%KEYS_DIR%"
)

:: Gera as chaves se não existirem
if not exist "%KEYS_DIR%\private.pem" (
    echo >> Gerando chaves RSA de DEV...
    openssl genpkey -algorithm RSA -out "%KEYS_DIR%\private.pem" -pkeyopt rsa_keygen_bits:2048
    openssl rsa -pubout -in "%KEYS_DIR%\private.pem" -out "%KEYS_DIR%\public.pem"
    echo >> Chaves geradas em %KEYS_DIR%
) else (
    echo >> Chaves já existem em %KEYS_DIR%
)

endlocal

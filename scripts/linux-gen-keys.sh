#!/usr/bin/env bash
set -e

# Pasta onde salvar as chaves
KEYS_DIR="../src/main/resources/keys"

mkdir -p "$KEYS_DIR"

if [ ! -f "$KEYS_DIR/private.pem" ]; then
  echo ">> Gerando chaves RSA de DEV..."
  openssl genpkey -algorithm RSA -out "$KEYS_DIR/private.pem" -pkeyopt rsa_keygen_bits:2048
  openssl rsa -pubout -in "$KEYS_DIR/private.pem" -out "$KEYS_DIR/public.pem"
  echo ">> Chaves geradas em $KEYS_DIR"
else
  echo ">> Chaves já existem em $KEYS_DIR"
fi

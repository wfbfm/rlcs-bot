#!/bin/bash

# Set variables
CERT_FILE="/usr/share/elasticsearch/config/certs/rlcs_es01/rlcs_es01.crt"
KEYSTORE="$JAVA_HOME/lib/security/cacerts"
STOREPASS="changeit"
ALIAS="rlcs_es01"

# Check if certificate exists in keystore
if keytool -list -keystore "$KEYSTORE" -storepass "$STOREPASS" -alias "$ALIAS" >/dev/null 2>&1; then
    echo "Certificate already exists in the keystore. Skipping import."
else
    # Import certificate to Java KeyStore
    keytool -importcert -file "$CERT_FILE" -keystore "$KEYSTORE" -storepass "$STOREPASS" -noprompt
fi
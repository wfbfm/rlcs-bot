#!/bin/bash

# Add http_ca.crt to Java KeyStore
keytool -importcert -file /usr/share/elasticsearch/config/certs/http_ca.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt

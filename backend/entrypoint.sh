#!/usr/bin/env sh
set -e

CERT_DIR="/app/cosmos-cert"
CERT_FILE="${CERT_DIR}/emulator.pem"

if [ -z "${COSMOS_EMULATOR_SKIP}" ] && [ -n "${AZURE_COSMOS_URI}" ]; then
  mkdir -p "${CERT_DIR}"
  echo "Fetching Cosmos DB emulator certificate (with retries)..."
  for i in $(seq 1 12); do
    if curl -k "${AZURE_COSMOS_URI%/}/_explorer/emulator.pem" -o "${CERT_FILE}"; then
      keytool -import -alias cosmos-emulator -keystore "${JAVA_HOME}/lib/security/cacerts" \
        -storepass changeit -noprompt -file "${CERT_FILE}" >/dev/null 2>&1 || true
      echo "Certificate imported into default truststore."
      break
    else
      echo "Attempt ${i}/12: could not download cert, retrying in 5s..."
      sleep 5
    fi
  done
fi

echo "Starting Spring Boot app..."
exec java ${JAVA_OPTS} -jar /app/app.jar

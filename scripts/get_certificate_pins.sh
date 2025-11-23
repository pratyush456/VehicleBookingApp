#!/bin/bash

# Certificate Pinning Helper Script
# This script helps you get the SHA-256 certificate pins for your API server

echo "Certificate Pinning Helper"
echo "=========================="
echo ""

# Check if domain is provided
if [ -z "$1" ]; then
    echo "Usage: ./get_certificate_pins.sh <domain>"
    echo "Example: ./get_certificate_pins.sh api.example.com"
    exit 1
fi

DOMAIN=$1
PORT=${2:-443}

echo "Getting certificate pins for: $DOMAIN:$PORT"
echo ""

# Method 1: Using OpenSSL
echo "Method 1: Using OpenSSL"
echo "-----------------------"

# Get the certificate
echo "Fetching certificate..."
CERT=$(echo | openssl s_client -connect $DOMAIN:$PORT -servername $DOMAIN 2>/dev/null | openssl x509 -pubkey -noout)

if [ -z "$CERT" ]; then
    echo "Error: Could not fetch certificate. Please check the domain and port."
    exit 1
fi

# Calculate SHA-256 pin
PIN=$(echo "$CERT" | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64)

echo "SHA-256 Pin: $PIN"
echo ""
echo "Add this to your CertificatePinningConfig.kt:"
echo "const val PRIMARY_PIN = \"$PIN\""
echo ""

# Method 2: Get backup certificate (if available)
echo "Method 2: Getting backup certificate chain"
echo "-------------------------------------------"

# Get all certificates in chain
echo | openssl s_client -connect $DOMAIN:$PORT -servername $DOMAIN -showcerts 2>/dev/null | \
    awk '/BEGIN CERTIFICATE/,/END CERTIFICATE/' | \
    csplit -s -f cert- - '/-----BEGIN CERTIFICATE-----/' '{*}'

# Process each certificate
for cert_file in cert-*; do
    if [ -f "$cert_file" ] && [ -s "$cert_file" ]; then
        SUBJECT=$(openssl x509 -in "$cert_file" -noout -subject 2>/dev/null)
        BACKUP_PIN=$(openssl x509 -in "$cert_file" -pubkey -noout 2>/dev/null | \
                     openssl pkey -pubin -outform der 2>/dev/null | \
                     openssl dgst -sha256 -binary | \
                     openssl enc -base64)
        
        if [ ! -z "$BACKUP_PIN" ]; then
            echo "Certificate: $SUBJECT"
            echo "Pin: $BACKUP_PIN"
            echo ""
        fi
    fi
    rm -f "$cert_file"
done

echo "Recommendation:"
echo "---------------"
echo "1. Use the first pin as PRIMARY_PIN"
echo "2. Use a pin from the certificate chain as BACKUP_PIN"
echo "3. Always have at least 2 pins (primary + backup) for certificate rotation"
echo ""
echo "Example configuration:"
echo "const val PRIMARY_PIN = \"$PIN\""
echo "const val BACKUP_PIN = \"<pin-from-backup-cert>\""

# Certificate Pinning Guide

## What is Certificate Pinning?

Certificate pinning prevents man-in-the-middle (MITM) attacks by ensuring your app only trusts specific SSL certificates, not just any certificate signed by a trusted Certificate Authority.

## How It Works

1. **Without Pinning**: App trusts any certificate from trusted CAs
   - Vulnerable to MITM attacks with rogue certificates
   
2. **With Pinning**: App only trusts specific certificate pins
   - Even if attacker has valid certificate, app rejects it
   - Much more secure

## Getting Certificate Pins

### Option 1: Using the Helper Script

```bash
cd /Users/pooja/VehicleBookingApp
chmod +x scripts/get_certificate_pins.sh
./scripts/get_certificate_pins.sh your-api-server.com
```

### Option 2: Manual with OpenSSL

```bash
# Get SHA-256 pin
echo | openssl s_client -connect your-api-server.com:443 -servername your-api-server.com | \
  openssl x509 -pubkey -noout | \
  openssl pkey -pubin -outform der | \
  openssl dgst -sha256 -binary | \
  openssl enc -base64
```

### Option 3: Let OkHttp Tell You

1. Add a fake pin to `CertificatePinningConfig.kt`
2. Run the app
3. Check logcat for the actual pins in the error message

## Configuration

### Update CertificatePinningConfig.kt

```kotlin
object ProductionPins {
    const val API_DOMAIN = "your-api-server.com"
    
    // Primary certificate pin (current certificate)
    const val PRIMARY_PIN = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
    
    // Backup certificate pin (for rotation)
    const val BACKUP_PIN = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
    
    fun getPins(): List<String> = listOf(PRIMARY_PIN, BACKUP_PIN)
}
```

### Update RetrofitClient.kt

Already configured! Certificate pinning is:
- **Enabled** in production builds
- **Disabled** in debug builds (for easier testing)

## Best Practices

### 1. Always Have Backup Pins

```kotlin
// ✅ Good: Multiple pins
fun getPins() = listOf(PRIMARY_PIN, BACKUP_PIN)

// ❌ Bad: Single pin (risky during rotation)
fun getPins() = listOf(PRIMARY_PIN)
```

### 2. Pin to Intermediate Certificate

Pin to both:
- Leaf certificate (your server's cert)
- Intermediate CA certificate (backup)

### 3. Plan for Certificate Rotation

1. Add new certificate pin as BACKUP_PIN
2. Release app update
3. Wait for users to update (2-4 weeks)
4. Rotate certificate on server
5. Remove old pin in next release

### 4. Monitor Pinning Failures

```kotlin
// Add logging for pinning failures
okHttpClient.addInterceptor { chain ->
    try {
        chain.proceed(chain.request())
    } catch (e: SSLPeerUnverifiedException) {
        // Log pinning failure
        Log.e("CertPinning", "Certificate pinning failed", e)
        throw e
    }
}
```

## Testing

### Test Certificate Pinning

```bash
# 1. Build release APK
./gradlew assembleRelease

# 2. Install on device
adb install app/build/outputs/apk/release/app-release.apk

# 3. Try to intercept traffic with proxy
# Certificate pinning should prevent it
```

### Test with Charles Proxy/Burp Suite

1. Install proxy certificate on device
2. Configure proxy
3. Try to make API calls
4. Should fail with certificate pinning error ✅

## Troubleshooting

### Error: "Certificate pinning failure"

**Cause**: Certificate on server doesn't match pins

**Solution**:
1. Get current certificate pins from server
2. Update `CertificatePinningConfig.kt`
3. Rebuild app

### Error: "Trust anchor for certification path not found"

**Cause**: Certificate not trusted by Android

**Solution**:
1. Ensure server uses valid SSL certificate
2. Check certificate chain is complete
3. Verify certificate is not expired

## Security Considerations

### DO:
✅ Pin to at least 2 certificates (primary + backup)  
✅ Pin to intermediate CA as backup  
✅ Plan certificate rotation strategy  
✅ Test pinning in staging environment  
✅ Monitor pinning failures  

### DON'T:
❌ Pin to only one certificate  
❌ Disable pinning in production  
❌ Forget to update pins before certificate expires  
❌ Pin to root CA (defeats the purpose)  

## Example: Complete Setup

```kotlin
// 1. Get pins
./scripts/get_certificate_pins.sh api.example.com

// 2. Update config
object ProductionPins {
    const val API_DOMAIN = "api.example.com"
    const val PRIMARY_PIN = "sha256/AAAA...="
    const val BACKUP_PIN = "sha256/BBBB...="
    fun getPins() = listOf(PRIMARY_PIN, BACKUP_PIN)
}

// 3. Build and test
./gradlew assembleRelease
```

## Certificate Rotation Process

### Step 1: Prepare (2 months before expiry)
- Generate new certificate
- Get new certificate pin
- Add as BACKUP_PIN in code

### Step 2: Release (1 month before expiry)
- Release app with both pins
- Monitor adoption rate

### Step 3: Rotate (After 80% adoption)
- Install new certificate on server
- Keep old certificate as backup

### Step 4: Cleanup (1 month after rotation)
- Remove old pin from code
- Release new version

## Resources

- [OkHttp Certificate Pinning](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-certificate-pinner/)
- [OWASP Certificate Pinning](https://owasp.org/www-community/controls/Certificate_and_Public_Key_Pinning)
- [Android Network Security Config](https://developer.android.com/training/articles/security-config)

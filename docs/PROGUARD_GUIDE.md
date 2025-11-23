# ProGuard Configuration Guide

## What is ProGuard/R8?

ProGuard/R8 is a code shrinker, optimizer, and obfuscator for Android apps. It:
- **Shrinks** code by removing unused classes and methods
- **Optimizes** bytecode for better performance
- **Obfuscates** code by renaming classes/methods to make reverse engineering harder

## Configuration

### Build Configuration

In `app/build.gradle`:

```gradle
buildTypes {
    release {
        minifyEnabled true          // Enable ProGuard/R8
        shrinkResources true         // Remove unused resources
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                      'proguard-rules.pro'
    }
}
```

### ProGuard Rules

The `proguard-rules.pro` file contains rules for:

1. **Keep Rules** - Prevent classes/methods from being removed or renamed
2. **Optimization Rules** - Configure optimization level
3. **Obfuscation Rules** - Control how code is obfuscated

## Testing ProGuard

### Build Release APK

```bash
./gradlew assembleRelease
```

### Check Obfuscation

```bash
# View mapping file (shows original → obfuscated names)
cat app/build/outputs/mapping/release/mapping.txt

# Example output:
# com.vehiclebooking.BookingRequest -> a.b.c:
#     java.lang.String bookingId -> a
#     void setBookingId(java.lang.String) -> a
```

### Analyze APK Size

```bash
# Before ProGuard
./gradlew assembleDebug
ls -lh app/build/outputs/apk/debug/app-debug.apk

# After ProGuard
./gradlew assembleRelease
ls -lh app/build/outputs/apk/release/app-release.apk

# Should see 30-50% size reduction
```

## Common Issues & Solutions

### Issue 1: App Crashes After Obfuscation

**Symptom**: App works in debug but crashes in release

**Solution**: Add keep rules for the crashing class

```proguard
# Keep specific class
-keep class com.vehiclebooking.YourClass { *; }

# Keep all classes in package
-keep class com.vehiclebooking.yourpackage.** { *; }
```

### Issue 2: Reflection Not Working

**Symptom**: Classes accessed via reflection are removed

**Solution**: Keep classes used with reflection

```proguard
-keep class com.vehiclebooking.ReflectedClass { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
```

### Issue 3: Serialization Issues

**Symptom**: JSON parsing fails or Room database crashes

**Solution**: Keep data models

```proguard
# Keep all data models
-keep class com.vehiclebooking.data.model.** { *; }

# Keep Gson annotations
-keepattributes *Annotation*
```

### Issue 4: Native Methods Not Found

**Symptom**: UnsatisfiedLinkError for JNI methods

**Solution**: Keep native methods

```proguard
-keepclasseswithmembernames class * {
    native <methods>;
}
```

## Debugging ProGuard Issues

### Enable Detailed Logging

Add to `proguard-rules.pro`:

```proguard
-verbose
-printconfiguration proguard-config.txt
-printusage proguard-usage.txt
```

### Check What Was Removed

```bash
# View what was removed
cat app/build/outputs/mapping/release/usage.txt

# View what was kept
cat app/build/outputs/mapping/release/seeds.txt
```

### Test Incrementally

1. Start with minimal obfuscation:
   ```proguard
   -dontobfuscate
   -dontoptimize
   ```

2. Enable optimization:
   ```proguard
   -dontobfuscate
   # Remove -dontoptimize
   ```

3. Enable full obfuscation:
   ```proguard
   # Remove both
   ```

## Crash Reporting with ProGuard

### Upload Mapping File

When using crash reporting (Firebase Crashlytics, etc.):

```bash
# Mapping file location
app/build/outputs/mapping/release/mapping.txt

# Upload to Firebase Crashlytics
firebase crashlytics:symbols:upload \
  --app=YOUR_APP_ID \
  app/build/outputs/mapping/release/mapping.txt
```

### Deobfuscate Stack Traces

```bash
# Using retrace tool
$ANDROID_HOME/tools/proguard/bin/retrace.sh \
  app/build/outputs/mapping/release/mapping.txt \
  obfuscated-stacktrace.txt
```

## Best Practices

### DO:
✅ Test release builds thoroughly before shipping  
✅ Keep mapping files for each release  
✅ Use `-keepattributes SourceFile,LineNumberTable` for better crash reports  
✅ Keep data models and DTOs  
✅ Keep classes used with reflection  
✅ Start with conservative rules and optimize gradually  

### DON'T:
❌ Keep everything (defeats the purpose)  
❌ Disable obfuscation in production  
❌ Forget to test release builds  
❌ Lose mapping files (needed for crash analysis)  
❌ Use `-dontwarn` without understanding why  

## Security Benefits

### Code Obfuscation

**Before ProGuard:**
```java
public class BookingRequest {
    private String bookingId;
    private String userId;
    
    public void setBookingId(String id) {
        this.bookingId = id;
    }
}
```

**After ProGuard:**
```java
public class a {
    private String a;
    private String b;
    
    public void a(String var1) {
        this.a = var1;
    }
}
```

### String Encryption (Advanced)

For sensitive strings, consider additional obfuscation:

```java
// Instead of
String apiKey = "my-secret-key";

// Use
String apiKey = new String(Base64.decode("bXktc2VjcmV0LWtleQ==", 0));
```

## Measuring Impact

### APK Size Reduction

```bash
# Compare sizes
./gradlew assembleDebug assembleRelease

# Typical results:
# Debug:   15 MB
# Release: 8 MB (47% reduction)
```

### Method Count Reduction

```bash
# Use APK Analyzer
$ANDROID_HOME/tools/bin/apkanalyzer dex packages app-release.apk

# Typical results:
# Debug:   25,000 methods
# Release: 18,000 methods (28% reduction)
```

## Continuous Integration

### GitHub Actions Example

```yaml
- name: Build Release APK
  run: ./gradlew assembleRelease

- name: Upload Mapping File
  uses: actions/upload-artifact@v2
  with:
    name: mapping-${{ github.sha }}
    path: app/build/outputs/mapping/release/mapping.txt
```

## Resources

- [Android ProGuard Documentation](https://developer.android.com/studio/build/shrink-code)
- [R8 Documentation](https://developer.android.com/studio/build/r8)
- [ProGuard Manual](https://www.guardsquare.com/manual/home)

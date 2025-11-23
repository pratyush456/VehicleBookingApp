# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============================================
# Gson specific classes
# ============================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class com.google.gson.examples.android.model.** { <fields>; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep our model classes for Gson serialization
-keep class com.vehiclebooking.BookingRequest { *; }
-keep class com.vehiclebooking.StatusChange { *; }
-keep class com.vehiclebooking.BookingStatus { *; }
-keep class com.vehiclebooking.User { *; }
-keep class com.vehiclebooking.UserRole { *; }
-keep class com.vehiclebooking.DateUtils { *; }
-keep class com.vehiclebooking.LocalDateAdapter { *; }

# Keep ThreeTenABP classes
-keep class org.threeten.bp.** { *; }
-dontwarn org.threeten.bp.**

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# AndroidX and Support Library
# ============================================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Keep custom views
-keep class com.vehiclebooking.StatusPieChart { *; }
-keep class com.vehiclebooking.WeeklyTrendChart { *; }

# ============================================
# Google Play Services
# ============================================
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ============================================
# Keep native methods
# ============================================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ============================================
# Keep Parcelable implementations
# ============================================
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# ============================================
# Keep Serializable classes
# ============================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================
# Keep reflection-accessed classes
# ============================================
-keepclassmembers class * {
    @androidx.annotation.NonNull <methods>;
    @androidx.annotation.Nullable <methods>;
}

# ============================================
# Remove logging in release builds (optional)
# ============================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
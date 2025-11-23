# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ================================
# General Android Rules
# ================================

# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.appcompat.app.AppCompatActivity

# Keep View constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ================================
# Kotlin Rules
# ================================

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Kotlin data classes
-keep @kotlin.Metadata class * { *; }
-keepclassmembers class * {
    @kotlin.Metadata <methods>;
}

# ================================
# Room Database Rules
# ================================

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Room DAOs
-keep interface * extends androidx.room.Dao {
    *;
}

# Keep Room entities
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# ================================
# Retrofit & OkHttp Rules
# ================================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ================================
# Gson Rules
# ================================

# Gson uses generic type information stored in a class file when working with fields.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**

# Keep generic signature of Call, Response (R8 full mode strips signatures)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep data classes for Gson
-keep class com.vehiclebooking.data.model.** { *; }
-keep class com.vehiclebooking.data.model.dto.** { *; }
-keep class com.vehiclebooking.BookingRequest { *; }
-keep class com.vehiclebooking.User { *; }
-keep class com.vehiclebooking.UserRole { *; }

# ================================
# Firebase Rules
# ================================

-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep Firestore models
-keepclassmembers class com.vehiclebooking.data.model.** {
    *;
}

# ================================
# Security & Encryption Rules
# ================================

# Keep security classes
-keep class com.vehiclebooking.security.** { *; }

# Keep bcrypt
-keep class org.mindrot.jbcrypt.** { *; }
-dontwarn org.mindrot.jbcrypt.**

# Keep SQLCipher
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Keep AndroidX Security
-keep class androidx.security.crypto.** { *; }

# ================================
# BiometricPrompt Rules
# ================================

-keep class androidx.biometric.** { *; }

# ================================
# Material Components Rules
# ================================

-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ================================
# Custom Application Rules
# ================================

# Keep application class
-keep class com.vehiclebooking.VehicleBookingApp { *; }

# Keep custom views
-keep class com.vehiclebooking.ui.components.** { *; }

# Keep activities
-keep class com.vehiclebooking.ui.** { *; }
-keep class com.vehiclebooking.MainActivity { *; }
-keep class com.vehiclebooking.LoginActivity { *; }
-keep class com.vehiclebooking.RegistrationActivity { *; }

# Keep managers
-keep class com.vehiclebooking.UserManager { *; }
-keep class com.vehiclebooking.BookingStorage { *; }
-keep class com.vehiclebooking.SearchStorage { *; }

# ================================
# Debugging & Crash Reporting
# ================================

# Keep source file names and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ================================
# Optimization Rules
# ================================

# Enable aggressive optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimize for code size and performance
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# ================================
# Warning Suppressions
# ================================

-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn javax.lang.model.element.Modifier
-dontwarn org.jetbrains.annotations.**
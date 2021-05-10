-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
# prevent Crashlytics obfuscation
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**
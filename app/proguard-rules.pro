# Butterknife proguard rules
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Material Dialogs proguard rules
-dontwarn
-ignorewarnings
-keep class android.support.design.** { *; }
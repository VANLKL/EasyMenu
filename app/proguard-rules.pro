# Add project specific ProGuard rules here.

# Keep model classes
-keep class cn.edu.cqust.easymenu.model.** { *; }

# Keep contract interfaces
-keep interface cn.edu.cqust.easymenu.contract.** { *; }

# Keep presenter classes (Retrofit/Gson if needed)
-keep class cn.edu.cqust.easymenu.presenter.** { *; }

# Keep view interfaces
-keep interface cn.edu.cqust.easymenu.contract.**.View { *; }

# Keep SQLite classes
-keep class cn.edu.cqust.easymenu.DatabaseHelper { *; }

# Keep enum classes
-keepclassmembers enum cn.edu.cqust.easymenu.** {
    **[] $VALUES;
    public *;
}

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

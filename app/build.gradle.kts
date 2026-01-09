plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "cn.edu.cqust.easymenu"
    compileSdk = 36

    defaultConfig {
        applicationId = "cn.edu.cqust.easymenu"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    testImplementation("net.jqwik:jqwik:1.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.1")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
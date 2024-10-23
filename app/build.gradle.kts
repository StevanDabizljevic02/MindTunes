import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.raf.diplomski"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.raf.diplomski"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }


    buildTypes {
        release {
            isMinifyEnabled = false
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

    // JDSP thing. Without this it won't build, and this is a solution from their github issues
    packaging{
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.fragment)
    implementation (libs.gson)
    implementation(libs.jna)
    implementation (libs.lottie)
    implementation (libs.opencsv)
    implementation(libs.core.splashscreen)
    implementation ("com.github.psambit9791:jdsp:3.0.0"){
        // JDSP thing. Without this it won't build, and this is a solution from their github issues
        exclude(group = "org.apache.maven.surefire", module = "common-java5")
        exclude(group = "org.apache.maven.surefire", module = "surefire-api")
    }
    implementation(files("C:/Users/steva/AndroidStudioProjects/diplomski/spotify-app-remote-release-0.8.0.aar"))

    compileOnly (libs.lombok)
    annotationProcessor (libs.lombok)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
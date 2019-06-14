plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Config.Android.compileSdkVersion)
    defaultConfig {
        minSdkVersion(Config.Android.minSdkVersion)
        targetSdkVersion(Config.Android.targetSdkVersion)
        versionCode = Config.build
        versionName = Config.version
        applicationId = "pro.horovodovodo4ka.bones.sample"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":bones"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", Config.kotlinVersion))

    with(Config.Libs) {
        implementation(appcompat)
        implementation(material)
        implementation(constraintLayout)
        implementation(vectorDrawable)
    }

    with(Config.TestLibs) {
        testImplementation(junit)
        androidTestImplementation(runner)
        androidTestImplementation(espresso)
    }

    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.5.4")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:1.5.4")
}

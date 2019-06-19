import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    maven
    `maven-publish`
}

group = "pro.horovodovodo4ka.bones"

buildscript {
    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(Config.Plugins.dokka)
    }
}

android {
    compileSdkVersion(Config.Android.compileSdkVersion)
    defaultConfig {
        minSdkVersion(Config.Android.minSdkVersion)
        targetSdkVersion(Config.Android.targetSdkVersion)
        versionCode = Config.build
        versionName = Config.version

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

apply<DokkaPlugin>()

tasks {
    getting(DokkaTask::class) {
        skipEmptyPackages = true
        outputFormat = "html"
        outputDirectory = "$rootDir/javadoc"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", Config.kotlinVersion))

    with(Config.Libs) {
        implementation(appcompat)
        implementation(material)
    }

    with(Config.TestLibs) {
        testImplementation(junit)
        androidTestImplementation(runner)
        androidTestImplementation(espresso)
    }
}

// for jitpack - https://github.com/sky-uk/gradle-maven-plugin#usage
apply(from = "https://raw.githubusercontent.com/sky-uk/gradle-maven-plugin/master/gradle-mavenizer.gradle")
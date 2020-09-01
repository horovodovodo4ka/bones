import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("org.jetbrains.dokka") version "1.4.0-rc"
    maven
    `maven-publish`
}

group = "pro.horovodovodo4ka.bones"

buildscript {
    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

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

tasks.dokkaHtml.configure {
    outputDirectory = "$rootDir/javadoc"
    dokkaSourceSets {
        configureEach {
            skipEmptyPackages = true
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib", Config.kotlinVersion))

    with(Config.Libs) {
        implementation(appcompat)
        implementation(fragment)
        implementation(fragmentKtx)
        implementation(material)
    }

    with(Config.TestLibs) {
        testImplementation(junit)
        androidTestImplementation(runner)
        androidTestImplementation(espresso)
    }
}

// for jitpack
apply(from = "${project.rootDir}/mavenizer/gradle-mavenizer.gradle")

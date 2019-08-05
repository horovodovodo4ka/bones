object Config {
    const val version = "1.2.7"
    const val build = 24
    const val kotlinVersion = "1.3.41"

    object Plugins {
        const val ktlint = "gradle.plugin.org.jlleitschuh.gradle:ktlint-gradle:3.2.0"
        const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:0.9.17"
        const val android = "com.android.tools.build:gradle:3.4.1"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }

    object Libs {
        const val appcompat = "androidx.appcompat:appcompat:1.0.2"
        const val material = "com.google.android.material:material:1.0.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta1"
        const val vectorDrawable = "androidx.vectordrawable:vectordrawable:1.0.1"
    }

    object TestLibs {
        const val junit = "junit:junit:4.12"
        const val runner = "androidx.test:runner:1.2.0"
        const val espresso = "androidx.test.espresso:espresso-core:3.2.0"
    }

    object Android {
        const val minSdkVersion = 15
        const val targetSdkVersion = 28
        const val compileSdkVersion = 28
    }
}
object Config {
    const val version = "1.5.1"
    const val build = 34
    const val kotlinVersion = "1.3.72"

    object Plugins {
        const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:9.3.0"
        const val android = "com.android.tools.build:gradle:4.0.1"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }

    object Libs {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val fragment = "androidx.fragment:fragment:1.2.5"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.2.5"
        const val material = "com.google.android.material:material:1.2.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0"
        const val vectorDrawable = "androidx.vectordrawable:vectordrawable:1.1.0"
    }

    object TestLibs {
        const val junit = "junit:junit:4.12"
        const val runner = "androidx.test:runner:1.2.0"
        const val espresso = "androidx.test.espresso:espresso-core:3.2.0"
    }

    object Android {
        const val minSdkVersion = 16
        const val targetSdkVersion = 29
        const val compileSdkVersion = 29
    }
}
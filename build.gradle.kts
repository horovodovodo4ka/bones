import org.jlleitschuh.gradle.ktlint.KtlintPlugin

plugins {
    kotlin("jvm") version Config.kotlinVersion
    kotlin("kapt") version Config.kotlinVersion
}

group = "pro.horovodovodo4ka"

buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }

    dependencies {
        with(Config.Plugins) {
            classpath(kotlin)
            classpath(android)
            classpath(ktlint)
        }
    }
}

subprojects {
    apply<KtlintPlugin>()
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
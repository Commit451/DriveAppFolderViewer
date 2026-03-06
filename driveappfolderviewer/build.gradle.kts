import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish")
}

group = findProperty("GROUP") as String
version = findProperty("VERSION_NAME") as String

android {
    namespace = "com.commit451.driveappfolderviewer"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
    }

    resourcePrefix = "dafv_"
}

dependencies {
    implementation("androidx.compose.material:material:1.6.8")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    api("com.google.android.material:material:1.13.0")
    api("com.google.android.gms:play-services-auth:21.4.0")
    api("com.google.http-client:google-http-client-android:1.47.0")
    api("com.google.http-client:google-http-client-gson:1.47.0")
    api("com.google.api-client:google-api-client-android:2.8.0") {
        exclude(group = "org.apache.httpcomponents")
    }
    api("com.google.apis:google-api-services-drive:v3-rev20250511-2.0.0") {
        exclude(group = "org.apache.httpcomponents")
    }
}

mavenPublishing {
    configure(AndroidSingleVariantLibrary("release", true, true))
    coordinates("com.commit451.driveappfolderviewer", "driveappfolderviewer", version.toString())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (System.getenv("RELEASE_SIGNING_ENABLED") == "true") {
        signAllPublications()
    }
}

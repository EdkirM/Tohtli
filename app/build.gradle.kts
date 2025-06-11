plugins {
    // Plugin para apps Android
    id("com.android.application")
    // Plugin para soporte de Kotlin en Android
    id("org.jetbrains.kotlin.android")
}

android {
    // Nombre del paquete raíz del proyecto
    namespace = "com.example.tohtli2"
    // Versión de Android SDK utilizada para compilar
    compileSdk = 34

    defaultConfig {
        // ID de la aplicación
        applicationId = "com.example.tohtli2"
        // Mínima versión de Android requerida
        minSdk = 34
        // SDK objetivo
        targetSdk = 34
        // Código de versión (para Play Store)
        versionCode = 1
        // Nombre de versión (para mostrar al usuario)
        versionName = "1.0"

        // Instrumentación para tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Desactiva la ofuscación en modo release
            isMinifyEnabled = false
            // Archivos de configuración de ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Configuración para compatibilidad con Java 8
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Configuración para compatibilidad de Kotlin con la JVM
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Activa ViewBinding para acceso seguro a vistas
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Funciones de extensión para Android
    implementation("androidx.core:core-ktx:1.12.0")
    // Compatibilidad con versiones anteriores de Android
    implementation("androidx.appcompat:appcompat:1.7.1")
    // Componentes de interfaz de usuario de Material Design
    implementation("com.google.android.material:material:1.12.0")
    // Soporte para layouts con restricciones
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Soporte para LiveData y ViewModel (arquitectura MVVM)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")

    // Navegación entre fragments
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")

    // Cliente HTTP y parseo JSON con Retrofit y OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Dependencias para pruebas
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

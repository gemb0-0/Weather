plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.weather"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weather"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "OPEN_WEATHER_API_KEY",
            "\"${project.properties["OPEN_WEATHER_API_KEY"]}\""
        )
        // buildConfigField("String", "WEATHER_API_KEY", "\"${project.properties["WEATHER_API_KEY"]}\"")
        buildConfigField(
            "String",
            "GOOGLE_MAPS_API_KEY",
            "\"${project.properties["GOOGLE_MAPS_API_KEY"]}\""
        )
        manifestPlaceholders[
            "GOOGLE_MAPS_API_KEY"
        ] = project.properties["GOOGLE_MAPS_API_KEY"] as Any
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


val junitVersion = "4.13.2"
val hamcrestVersion = "2.2"
val archTestingVersion = "2.1.0"
val robolectricVersion = "4.8"
val androidXTestCoreVersion = "1.4.0"
val androidXTestExtKotlinRunnerVersion = "1.1.5"
val espressoVersion = "3.4.0"
val coroutinesVersion = "1.6.4"
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.common.jvm)

    // Unit testing dependencies
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    testImplementation("androidx.arch.core:core-testing:$archTestingVersion")
    testImplementation("org.robolectric:robolectric:$robolectricVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("androidx.test:core-ktx:$androidXTestCoreVersion")

    // AndroidX Test - Instrumented testing
    androidTestImplementation("androidx.test.ext:junit-ktx:$androidXTestExtKotlinRunnerVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.5.31"))
    implementation("com.google.android.libraries.places:places:3.5.0")

    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.7.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Gson
    implementation("com.google.code.gson:gson:2.11.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Material Design
    implementation("com.google.android.material:material:1.13.0-alpha06")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    testImplementation ("org.robolectric:robolectric:4.0.2")

    // Required -- JUnit 4 framework
    testImplementation ("junit:junit:4.13.2")

    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("androidx.test.ext:junit-ktx:1.1.5")

// Robolectric environment
    testImplementation ("org.robolectric:robolectric:4.4")

// Optional -- truth
    testImplementation ("androidx.test.ext:truth:1.5.0")
    testImplementation ("com.google.truth:truth:1.0")

// Optional -- Mockito framework
    testImplementation ("org.mockito:mockito-core:3.3.3")
}


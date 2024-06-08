plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id ("kotlin-kapt")
	id ("kotlin-parcelize")
}

android {
	namespace = "com.naffeid.gassin"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.naffeid.gassin"
		minSdk = 21
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		//Server Connection URL
		buildConfigField("String", "BASE_URL", "\"https://api.webserver.com/\"")
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
	buildFeatures {
		viewBinding = true
		buildConfig =  true
	}
}

dependencies {
	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.activity:activity-ktx:1.7.2")
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("com.google.android.material:material:1.11.0")
	implementation("androidx.constraintlayout:constraintlayout:2.1.4")

	/* User Interface */
	//Shimmer
	implementation ("com.facebook.shimmer:shimmer:0.5.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    /* Testing */
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	//Koin Testing Dependency Injection
	testImplementation ("io.insert-koin:koin-test:3.5.0")
	testImplementation("io.insert-koin:koin-test-junit4:3.5.0")

	/* API */
	implementation("com.squareup.retrofit2:retrofit:2.9.0")
	implementation("com.squareup.retrofit2:converter-gson:2.9.0")
	implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

	/* Preference DataStore */
	implementation ("androidx.datastore:datastore-preferences:1.0.0")

	/* Koin Dependency Injection */
	implementation ("io.insert-koin:koin-android:3.5.0")
	implementation ("io.insert-koin:koin-android-compat:3.5.0")

	/* Room Local Database */
	implementation ("androidx.room:room-runtime:2.6.1")
	kapt ("androidx.room:room-compiler:2.6.1")

	/* Lifecyle Data */
	implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
	implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

}
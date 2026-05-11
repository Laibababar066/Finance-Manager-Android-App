package com.smd.penni

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Feature B: Initialize Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}

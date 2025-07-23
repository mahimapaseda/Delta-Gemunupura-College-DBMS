package com.deltagemunupuramv.dbms

import android.app.Application
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class DBMSApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize UserSession
        UserSession.init(this)
        
        // Initialize Firebase Realtime Database
        FirebaseDatabase.getInstance().apply {
            // Enable offline persistence
            setPersistenceEnabled(true)
            // Keep student data synced
            reference.child("students").keepSynced(true)
        }

        // Initialize Firestore
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings
    }
} 
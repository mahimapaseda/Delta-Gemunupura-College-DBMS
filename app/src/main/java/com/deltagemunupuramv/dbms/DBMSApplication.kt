package com.deltagemunupuramv.dbms

import android.app.Application
import android.util.Log
import com.deltagemunupuramv.dbms.util.FirebaseManager
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class DBMSApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize UserSession
        UserSession.init(this)
        
        initializeFirebase()
        
        // Initialize Firebase Manager for sync operations
        FirebaseManager.initializeRealtimeSync()
    }
    
    private fun initializeFirebase() {
        try {
            // Initialize Firebase Realtime Database
            FirebaseDatabase.getInstance().apply {
                // Enable offline persistence
                setPersistenceEnabled(true)
                
                // Keep important data synced for offline access
                reference.child("students").keepSynced(true)
                reference.child("staff").keepSynced(true)
                reference.child("users").keepSynced(true)
                reference.child("assets").keepSynced(true)
                
                Log.d("DBMSApplication", "Firebase Realtime Database initialized successfully")
            }

            // Initialize Firestore with enhanced settings
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
            firestore.firestoreSettings = settings
            
            // Pre-load staff collection for offline access
            firestore.collection("staff")
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("DBMSApplication", "Staff data pre-loaded for offline access: ${documents.size()} documents")
                }
                .addOnFailureListener { e ->
                    Log.w("DBMSApplication", "Failed to pre-load staff data", e)
                }
            
            Log.d("DBMSApplication", "Firestore initialized successfully")
            
        } catch (e: Exception) {
            Log.e("DBMSApplication", "Error initializing Firebase", e)
        }
    }
} 
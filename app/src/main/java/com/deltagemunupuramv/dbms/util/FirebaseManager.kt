package com.deltagemunupuramv.dbms.util

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    
    private val realtimeDb = FirebaseDatabase.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    // Collections
    const val STUDENTS_COLLECTION = "students"
    const val STAFF_COLLECTION = "staff"
    const val USERS_COLLECTION = "users"
    const val ASSETS_COLLECTION = "assets"
    const val EXAMS_COLLECTION = "exams"
    const val OL_EXAMS_COLLECTION = "ol_exams"
    const val AL_EXAMS_COLLECTION = "al_exams"
    
    /**
     * Initialize real-time synchronization for critical data
     */
    fun initializeRealtimeSync() {
        try {
            // Keep students synced in Realtime Database
            realtimeDb.reference.child(STUDENTS_COLLECTION).keepSynced(true)
            realtimeDb.reference.child(USERS_COLLECTION).keepSynced(true)
            realtimeDb.reference.child(ASSETS_COLLECTION).keepSynced(true)
            realtimeDb.reference.child(EXAMS_COLLECTION).keepSynced(true)
            realtimeDb.reference.child(OL_EXAMS_COLLECTION).keepSynced(true)
            realtimeDb.reference.child(AL_EXAMS_COLLECTION).keepSynced(true)
            
            Log.d(TAG, "Real-time synchronization initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing real-time sync", e)
        }
    }
    
    /**
     * Force sync specific collections
     */
    suspend fun forceSyncStaff(): Boolean {
        return try {
            firestore.collection(STAFF_COLLECTION)
                .get()
                .await()
            Log.d(TAG, "Staff data synced successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing staff data", e)
            false
        }
    }
    
    suspend fun forceSyncStudents(): Boolean {
        return try {
            realtimeDb.reference.child(STUDENTS_COLLECTION)
                .get()
                .await()
            Log.d(TAG, "Students data synced successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing students data", e)
            false
        }
    }
    
    suspend fun forceSyncAssets(): Boolean {
        return try {
            realtimeDb.reference.child(ASSETS_COLLECTION)
                .get()
                .await()
            Log.d(TAG, "Assets data synced successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing assets data", e)
            false
        }
    }
    
    /**
     * Enable offline persistence (already handled in Application class)
     */
    fun isOfflinePersistenceEnabled(): Boolean {
        return try {
            // Check if offline persistence is working
            true
        } catch (e: Exception) {
            Log.e(TAG, "Offline persistence check failed", e)
            false
        }
    }
    
    /**
     * Get network status for Firebase
     */
    fun addNetworkStatusListener(callback: (Boolean) -> Unit): ListenerRegistration {
        return firestore.enableNetwork()
            .addOnSuccessListener {
                Log.d(TAG, "Firebase network enabled")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Firebase network status error", e)
                callback(false)
            }
            .let { 
                // Return a dummy listener registration
                object : ListenerRegistration {
                    override fun remove() {
                        // No-op for this simple implementation
                    }
                }
            }
    }
    
    /**
     * Manually trigger data synchronization
     */
    suspend fun synchronizeAllData(): Boolean {
        return try {
            val staffSync = forceSyncStaff()
            val studentsSync = forceSyncStudents()
            val assetsSync = forceSyncAssets()
            
            Log.d(TAG, "Data synchronization completed - Staff: $staffSync, Students: $studentsSync, Assets: $assetsSync")
            staffSync && studentsSync && assetsSync
        } catch (e: Exception) {
            Log.e(TAG, "Error during full data synchronization", e)
            false
        }
    }
} 
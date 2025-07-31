package com.deltagemunupuramv.dbms.manager

import android.util.Log
import com.deltagemunupuramv.dbms.model.Asset
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class AssetManager {
    private val database = FirebaseDatabase.getInstance()
    private val assetsRef = database.getReference("assets")
    private var valueEventListener: ValueEventListener? = null

    companion object {
        private const val TAG = "AssetManager"
    }

    fun getAllAssets(callback: (List<Asset>) -> Unit) {
        valueEventListener?.let { assetsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val assets = snapshot.children.mapNotNull { it.getValue(Asset::class.java) }
                    .sortedBy { it.name }
                callback(assets)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting assets: ${error.message}")
                callback(emptyList())
            }
        }

        assetsRef.addValueEventListener(valueEventListener!!)
    }

    fun getAssetById(id: String, callback: (Asset?) -> Unit) {
        assetsRef.child(id).get()
            .addOnSuccessListener { snapshot ->
                val asset = snapshot.getValue(Asset::class.java)
                callback(asset)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting asset by ID: ${exception.message}")
                callback(null)
            }
    }

    fun addAsset(asset: Asset, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Adding asset: ${asset.name} (${asset.id})")
        
        assetsRef.child(asset.id).setValue(asset)
            .addOnSuccessListener {
                Log.d(TAG, "Asset added successfully")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding asset: ${exception.message}")
                callback(false)
            }
    }

    fun updateAsset(asset: Asset, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Updating asset: ${asset.name} (${asset.id})")
        
        val updatedAsset = asset.copy(updatedAt = System.currentTimeMillis())
        assetsRef.child(asset.id).setValue(updatedAsset)
            .addOnSuccessListener {
                Log.d(TAG, "Asset updated successfully")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating asset: ${exception.message}")
                callback(false)
            }
    }

    fun deleteAsset(assetId: String, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Deleting asset: $assetId")
        
        assetsRef.child(assetId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Asset deleted successfully")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error deleting asset: ${exception.message}")
                callback(false)
            }
    }

    fun searchAssets(query: String, callback: (List<Asset>) -> Unit) {
        // For Firebase, we'll get all assets and filter them in memory
        // In a production app, you might want to use Firebase Search or Algolia
        getAllAssets { assets ->
            val filteredAssets = assets.filter { asset ->
                asset.name.contains(query, ignoreCase = true) ||
                asset.type.contains(query, ignoreCase = true) ||
                asset.category.contains(query, ignoreCase = true) ||
                asset.location.contains(query, ignoreCase = true) ||
                asset.assignedTo.contains(query, ignoreCase = true)
            }
            callback(filteredAssets)
        }
    }

    fun getAssetsByCategory(category: String, callback: (List<Asset>) -> Unit) {
        getAllAssets { assets ->
            val filteredAssets = assets.filter { asset ->
                asset.category.equals(category, ignoreCase = true)
            }
            callback(filteredAssets)
        }
    }

    fun getAssetsByStatus(status: String, callback: (List<Asset>) -> Unit) {
        getAllAssets { assets ->
            val filteredAssets = assets.filter { asset ->
                asset.status.equals(status, ignoreCase = true)
            }
            callback(filteredAssets)
        }
    }

    fun cleanup() {
        valueEventListener?.let { assetsRef.removeEventListener(it) }
        valueEventListener = null
    }
} 
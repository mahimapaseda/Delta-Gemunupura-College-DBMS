package com.deltagemunupuramv.dbms.manager

import android.util.Log
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffType
import com.deltagemunupuramv.dbms.util.StaffUserConverter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class StaffManager {
    private val database = FirebaseDatabase.getInstance()
    private val staffRef = database.getReference("staff")
    private val usersRef = database.getReference("users")
    private var valueEventListener: ValueEventListener? = null

    companion object {
        private const val TAG = "StaffManager"
    }

    fun addStaff(staff: Staff, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Adding staff: ${staff.fullName} (${staff.id})")
        
        // Save to staff collection for management
        staffRef.child(staff.id).setValue(staff)
            .addOnSuccessListener {
                Log.d(TAG, "Staff saved to staff collection: ${staff.fullName}")
                
                // Also save to users collection for sign-in
                val user = StaffUserConverter.staffToUser(staff)
                usersRef.child(staff.id).setValue(user)
                    .addOnSuccessListener {
                        Log.d(TAG, "Staff added successfully to both collections: ${staff.fullName}")
                        callback(true)
                    }
                    .addOnFailureListener { error ->
                        Log.e(TAG, "Failed to add staff to users collection: ${staff.fullName}", error)
                        // Remove from staff collection if users failed
                        staffRef.child(staff.id).removeValue()
                        callback(false)
                    }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to add staff to staff collection: ${staff.fullName}", error)
                callback(false)
            }
    }

    fun updateStaff(staff: Staff, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Updating staff: ${staff.fullName} (${staff.id})")
        
        // Update staff collection
        staffRef.child(staff.id).setValue(staff)
            .addOnSuccessListener {
                Log.d(TAG, "Staff updated in staff collection: ${staff.fullName}")
                
                // Also update users collection
                val user = StaffUserConverter.staffToUser(staff)
                usersRef.child(staff.id).setValue(user)
                    .addOnSuccessListener {
                        Log.d(TAG, "Staff updated successfully in both collections: ${staff.fullName}")
                        callback(true)
                    }
                    .addOnFailureListener { error ->
                        Log.e(TAG, "Failed to update staff in users collection: ${staff.fullName}", error)
                        callback(false)
                    }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to update staff in staff collection: ${staff.fullName}", error)
                callback(false)
            }
    }

    fun deleteStaff(staffId: String, callback: (Boolean) -> Unit) {
        Log.d(TAG, "Deleting staff with ID: $staffId")
        
        // Delete from staff collection
        staffRef.child(staffId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Staff deleted from staff collection: $staffId")
                
                // Also delete from users collection
                usersRef.child(staffId).removeValue()
                    .addOnSuccessListener {
                        Log.d(TAG, "Staff deleted successfully from both collections: $staffId")
                        callback(true)
                    }
                    .addOnFailureListener { error ->
                        Log.e(TAG, "Failed to delete staff from users collection: $staffId", error)
                        callback(false)
                    }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to delete staff from staff collection: $staffId", error)
                callback(false)
            }
    }

    fun getFilteredStaff(
        searchQuery: String = "",
        staffType: String = "",
        callback: (List<Staff>) -> Unit
    ) {
        Log.d(TAG, "Getting filtered staff - Query: '$searchQuery', Type: '$staffType'")
        valueEventListener?.let { 
            Log.d(TAG, "Removing existing listener")
            staffRef.removeEventListener(it) 
        }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Data snapshot received with ${snapshot.childrenCount} total staff")
                
                var filteredList = snapshot.children.mapNotNull { 
                    try {
                        it.getValue(Staff::class.java)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse staff: ${it.key}", e)
                        null
                    }
                }
                Log.d(TAG, "Parsed ${filteredList.size} staff successfully")

                // Apply search filter
                if (searchQuery.isNotEmpty()) {
                    val originalSize = filteredList.size
                    filteredList = filteredList.filter { staff ->
                        staff.fullName.contains(searchQuery, ignoreCase = true) ||
                        staff.nameWithInitials.contains(searchQuery, ignoreCase = true) ||
                        staff.email.contains(searchQuery, ignoreCase = true) ||
                        staff.appointedSubject.contains(searchQuery, ignoreCase = true) ||
                        staff.registrationNumber.contains(searchQuery, ignoreCase = true)
                    }
                    Log.d(TAG, "Search filter applied: $originalSize -> ${filteredList.size} results")
                }

                // Apply staff type filter
                if (staffType.isNotEmpty() && staffType != "All Staff") {
                    val originalSize = filteredList.size
                    filteredList = when (staffType) {
                        "Academic Staff" -> filteredList.filter { it.staffType == StaffType.ACADEMIC }
                        "Non-Academic Staff" -> filteredList.filter { it.staffType == StaffType.NON_ACADEMIC }
                        else -> filteredList
                    }
                    Log.d(TAG, "Staff type filter applied: $originalSize -> ${filteredList.size} results")
                }

                // Sort results
                val sortedList = filteredList.sortedWith(
                    compareBy<Staff> { it.staffType }
                        .thenBy { it.fullName }
                )

                Log.d(TAG, "Returning ${sortedList.size} filtered and sorted staff members")
                callback(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error in getFilteredStaff", error.toException())
                callback(emptyList())
            }
        }

        Log.d(TAG, "Adding ValueEventListener to staff reference")
        staffRef.addValueEventListener(valueEventListener!!)
    }

    fun getAllStaff(callback: (List<Staff>) -> Unit) {
        Log.d(TAG, "Getting all staff")
        valueEventListener?.let { 
            Log.d(TAG, "Removing existing listener for getAllStaff")
            staffRef.removeEventListener(it) 
        }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "getAllStaff: Data snapshot received with ${snapshot.childrenCount} total staff")
                
                val staff = snapshot.children.mapNotNull { 
                    try {
                        it.getValue(Staff::class.java)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse staff: ${it.key}", e)
                        null
                    }
                }.sortedWith(
                    compareBy<Staff> { it.staffType }
                        .thenBy { it.fullName }
                )
                
                Log.d(TAG, "getAllStaff: Returning ${staff.size} staff members")
                // Debug: Log staff details
                staff.forEach { staffMember ->
                    Log.d(TAG, "Staff: ${staffMember.fullName}, Type: ${staffMember.staffType}, Subject: ${staffMember.appointedSubject}")
                }
                
                callback(staff)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error in getAllStaff", error.toException())
                callback(emptyList())
            }
        }

        Log.d(TAG, "Adding ValueEventListener for getAllStaff")
        staffRef.addValueEventListener(valueEventListener!!)
    }

    fun getStaffById(staffId: String, callback: (Staff?) -> Unit) {
        Log.d(TAG, "Getting staff by ID: $staffId")
        staffRef.child(staffId).get()
            .addOnSuccessListener { snapshot ->
                val staff = snapshot.getValue(Staff::class.java)
                Log.d(TAG, "Found staff by ID: ${staff?.fullName ?: "null"}")
                callback(staff)
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to get staff by ID: $staffId", error)
                callback(null)
            }
    }

    fun cleanup() {
        Log.d(TAG, "Cleaning up StaffManager")
        valueEventListener?.let { 
            staffRef.removeEventListener(it) 
            Log.d(TAG, "ValueEventListener removed")
        }
        valueEventListener = null
    }
} 
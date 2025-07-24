package com.deltagemunupuramv.dbms.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Asset(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val category: String = "",
    val location: String = "",
    val status: String = "",
    val purchaseDate: String = "",
    val purchasePrice: Double = 0.0,
    val currentValue: Double = 0.0,
    val assignedTo: String = "",
    val assignedDepartment: String = "",
    val serialNumber: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val warrantyExpiry: String = "",
    val lastMaintenance: String = "",
    val nextMaintenance: String = "",
    val description: String = "",
    val notes: String = "",
    val imageUrl: String = "",
    // New fields for detailed tracking
    val bookName: String = "",
    val itemNumber: String = "",
    val item: String = "",
    val dateEntered: String = "",
    val voucherNumber: String = "",
    val fromWhomReceived: String = "",
    val dateRemoved: String = "",
    val reasonForRemoval: String = "",
    val other: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class AssetType {
    EQUIPMENT,
    FURNITURE,
    VEHICLE,
    BUILDING,
    SOFTWARE,
    BOOKS,
    OTHER
}

enum class AssetStatus {
    AVAILABLE,
    IN_USE,
    MAINTENANCE,
    RETIRED,
    LOST,
    DAMAGED
}

enum class AssetCategory {
    ACADEMIC,
    ADMINISTRATIVE,
    IT_EQUIPMENT,
    LABORATORY,
    LIBRARY,
    SPORTS,
    TRANSPORT,
    MAINTENANCE,
    OTHER
} 
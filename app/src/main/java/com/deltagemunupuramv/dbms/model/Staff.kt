package com.deltagemunupuramv.dbms.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Staff(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "",
    val designation: String = "",
    val staffType: StaffType = StaffType.ACADEMIC,
    val photoUrl: String = "",
    val dateJoined: String = "",
    val qualifications: String = "",
    // Academic staff specific fields
    val subjects: List<String> = emptyList(),
    val teachingExperience: Int = 0,
    val researchPublications: Int = 0,
    // Non-academic staff specific fields
    val role: String = "",
    val skills: List<String> = emptyList(),
    val workExperience: Int = 0,
    // Common additional fields
    val address: String = "",
    val emergencyContact: String = "",
    val status: StaffStatus = StaffStatus.ACTIVE
) : Parcelable

enum class StaffType {
    ACADEMIC,
    NON_ACADEMIC
}

enum class StaffStatus {
    ACTIVE,
    INACTIVE,
    ON_LEAVE
} 
package com.deltagemunupuramv.dbms.model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val role: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    
    // Staff-specific fields (optional, only filled for staff users)
    val isStaff: Boolean = false,
    val staffId: String = "",
    val nameWithInitials: String = "",
    val nicNumber: String = "",
    val registrationNumber: String = "",
    val password: String = "", // For staff sign in
    val phoneNumber: String = "",
    val personalAddress: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val maritalStatus: String = "",
    val spouseName: String = "",
    val spouseAddress: String = "",
    val spouseTelephone: String = "",
    val dateOfFirstAppointment: String = "",
    val dateOfAppointmentToSchool: String = "",
    val previouslyServedSchools: List<String> = emptyList(),
    val classAndGrade: String = "",
    val educationalQualifications: List<String> = emptyList(),
    val professionalQualifications: List<String> = emptyList(),
    val appointedSubject: String = "",
    val subjectsTaught: List<String> = emptyList(),
    val gradesTaught: List<String> = emptyList(),
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val staffType: String = "", // "ACADEMIC" or "NON_ACADEMIC"
    val status: String = "", // "ACTIVE", "INACTIVE", "ON_LEAVE"
    val photoUrl: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) 
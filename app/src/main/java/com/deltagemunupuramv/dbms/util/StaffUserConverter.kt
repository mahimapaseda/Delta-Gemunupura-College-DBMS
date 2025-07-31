package com.deltagemunupuramv.dbms.util

import android.util.Log
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffStatus
import com.deltagemunupuramv.dbms.model.StaffType
import com.deltagemunupuramv.dbms.model.User

object StaffUserConverter {
    
    private const val TAG = "StaffUserConverter"
    
    /**
     * Convert Staff model to User model for Firebase Realtime Database storage
     */
    fun staffToUser(staff: Staff): User {
        Log.d(TAG, "Converting staff to user: ${staff.fullName}")
        val user = User(
            uid = staff.id,
            username = staff.email.substringBefore("@"), // Use email prefix as username
            email = staff.email,
            fullName = staff.fullName,
            role = determineUserRole(staff.staffType, staff.appointedSubject),
            createdAt = staff.createdAt,
            
            // Staff-specific fields
            isStaff = true,
            staffId = staff.id,
            nameWithInitials = staff.nameWithInitials,
            nicNumber = staff.nicNumber,
            registrationNumber = staff.registrationNumber,
            password = staff.password,
            phoneNumber = staff.phoneNumber,
            personalAddress = staff.personalAddress,
            dateOfBirth = staff.dateOfBirth,
            gender = staff.gender,
            maritalStatus = staff.maritalStatus,
            spouseName = staff.spouseName,
            spouseAddress = staff.spouseAddress,
            spouseTelephone = staff.spouseTelephone,
            dateOfFirstAppointment = staff.dateOfFirstAppointment,
            dateOfAppointmentToSchool = staff.dateOfAppointmentToSchool,
            previouslyServedSchools = staff.previouslyServedSchools,
            classAndGrade = staff.classAndGrade,
            educationalQualifications = staff.educationalQualifications,
            professionalQualifications = staff.professionalQualifications,
            appointedSubject = staff.appointedSubject,
            subjectsTaught = staff.subjectsTaught,
            gradesTaught = staff.gradesTaught,
            emergencyContactName = staff.emergencyContactName,
            emergencyContactPhone = staff.emergencyContactPhone,
            staffType = staff.staffType.name,
            status = staff.status.name,
            photoUrl = staff.photoUrl,
            updatedAt = staff.updatedAt
        )
        Log.d(TAG, "Converted staff '${staff.fullName}' to user with isStaff=${user.isStaff}, role=${user.role}")
        return user
    }
    
    /**
     * Convert User model back to Staff model
     */
    fun userToStaff(user: User): Staff? {
        Log.v(TAG, "Converting user to staff: ${user.fullName}, isStaff=${user.isStaff}")
        if (!user.isStaff) {
            Log.v(TAG, "User ${user.fullName} is not a staff member (isStaff=false)")
            return null
        }
        
        val staff = Staff(
            id = user.uid,
            fullName = user.fullName,
            nameWithInitials = user.nameWithInitials,
            nicNumber = user.nicNumber,
            registrationNumber = user.registrationNumber,
            email = user.email,
            password = user.password,
            phoneNumber = user.phoneNumber,
            personalAddress = user.personalAddress,
            dateOfBirth = user.dateOfBirth,
            gender = user.gender,
            maritalStatus = user.maritalStatus,
            spouseName = user.spouseName,
            spouseAddress = user.spouseAddress,
            spouseTelephone = user.spouseTelephone,
            dateOfFirstAppointment = user.dateOfFirstAppointment,
            dateOfAppointmentToSchool = user.dateOfAppointmentToSchool,
            previouslyServedSchools = user.previouslyServedSchools,
            classAndGrade = user.classAndGrade,
            educationalQualifications = user.educationalQualifications,
            professionalQualifications = user.professionalQualifications,
            appointedSubject = user.appointedSubject,
            subjectsTaught = user.subjectsTaught,
            gradesTaught = user.gradesTaught,
            emergencyContactName = user.emergencyContactName,
            emergencyContactPhone = user.emergencyContactPhone,
            staffType = try { 
                StaffType.valueOf(user.staffType) 
            } catch (e: Exception) { 
                Log.w(TAG, "Invalid staffType '${user.staffType}' for user ${user.fullName}, defaulting to ACADEMIC")
                StaffType.ACADEMIC 
            },
            status = try { 
                StaffStatus.valueOf(user.status) 
            } catch (e: Exception) { 
                Log.w(TAG, "Invalid status '${user.status}' for user ${user.fullName}, defaulting to ACTIVE")
                StaffStatus.ACTIVE 
            },
            photoUrl = user.photoUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
        Log.d(TAG, "Successfully converted user '${user.fullName}' to staff")
        return staff
    }
    
    /**
     * Determine appropriate user role based on staff type and subject
     */
    private fun determineUserRole(staffType: StaffType, appointedSubject: String): String {
        val role = when {
            // Principal - highest authority
            appointedSubject.contains("Principal", ignoreCase = true) ||
            appointedSubject.contains("Head", ignoreCase = true) ||
            appointedSubject.contains("Director", ignoreCase = true) -> {
                AccessLevel.ROLE_PRINCIPAL
            }
            
            // Data Officer - manages data and records
            appointedSubject.contains("Data Officer", ignoreCase = true) ||
            appointedSubject.contains("Data", ignoreCase = true) ||
            appointedSubject.contains("Records", ignoreCase = true) ||
            appointedSubject.contains("Information", ignoreCase = true) -> {
                AccessLevel.ROLE_DATA_OFFICER
            }
            
            // Technical Officer - manages technical systems
            appointedSubject.contains("Technical Officer", ignoreCase = true) ||
            appointedSubject.contains("Technical", ignoreCase = true) ||
            appointedSubject.contains("IT", ignoreCase = true) ||
            appointedSubject.contains("Computer", ignoreCase = true) ||
            appointedSubject.contains("Technology", ignoreCase = true) -> {
                AccessLevel.ROLE_TECHNICAL_OFFICER
            }
            
            // Academic Staff - teachers and academic personnel
            staffType == StaffType.ACADEMIC -> {
                AccessLevel.ROLE_ACADEMIC_STAFF
            }
            
            // Non-Academic Staff - support staff
            staffType == StaffType.NON_ACADEMIC -> {
                AccessLevel.ROLE_NON_ACADEMIC_STAFF
            }
            
            // Default fallback
            else -> AccessLevel.ROLE_ACADEMIC_STAFF
        }
        
        Log.v(TAG, "Determined role '$role' for $staffType staff with subject '$appointedSubject'")
        return role
    }
    
    /**
     * Get all staff users from a list of users
     */
    fun getStaffUsers(users: List<User>): List<User> {
        val staffUsers = users.filter { it.isStaff }
        Log.d(TAG, "Filtered ${staffUsers.size} staff users out of ${users.size} total users")
        return staffUsers
    }
    
    /**
     * Convert list of users to list of staff
     */
    fun usersToStaff(users: List<User>): List<Staff> {
        Log.d(TAG, "Converting ${users.size} users to staff")
        val staff = users.mapNotNull { userToStaff(it) }
        Log.d(TAG, "Successfully converted ${staff.size} users to staff")
        return staff
    }
} 
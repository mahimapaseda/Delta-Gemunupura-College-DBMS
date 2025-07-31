package com.deltagemunupuramv.dbms.util

object AccessLevel {
    // Role Constants
    const val ROLE_ADMINISTRATOR = "Administrator"
    const val ROLE_PRINCIPAL = "Principal"
    const val ROLE_DATA_OFFICER = "Data Officer"
    const val ROLE_TECHNICAL_OFFICER = "Technical Officer"
    const val ROLE_ACADEMIC_STAFF = "Academic Staff"
    const val ROLE_NON_ACADEMIC_STAFF = "Non-Academic Staff"

    /**
     * Full Access Roles: Principal, Data Officer, Technical Officer
     * These roles have complete access to all system features
     */
    fun hasFullAccess(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false
        }
    }

    /**
     * Partial Access Roles: Academic Staff, Non-Academic Staff
     * These roles have limited access to system features
     */
    fun hasPartialAccess(role: String): Boolean {
        return when (role) {
            ROLE_ACADEMIC_STAFF -> true
            ROLE_NON_ACADEMIC_STAFF -> true
            else -> false
        }
    }

    // === STUDENT MANAGEMENT ACCESS ===
    
    fun canAccessStudents(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> true  // Can view students they teach
            ROLE_NON_ACADEMIC_STAFF -> false  // Limited access
            else -> false
        }
    }
    
    fun canModifyStudents(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> false  // Can only view, not modify
            ROLE_NON_ACADEMIC_STAFF -> false
            else -> false
        }
    }
    
    fun canDeleteStudents(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false  // Academic/Non-Academic staff cannot delete
        }
    }
    
    // === STAFF MANAGEMENT ACCESS ===
    
    fun canAccessStaff(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> false  // Cannot access staff management
            ROLE_NON_ACADEMIC_STAFF -> false  // Cannot access staff management
            else -> false
        }
    }
    
    fun canModifyStaff(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false  // Only full access roles can modify staff
        }
    }
    
    fun canDeleteStaff(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false
        }
    }
    
    // === ASSET MANAGEMENT ACCESS ===
    
    fun canAccessAssets(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> true  // Can view assets for their classes
            ROLE_NON_ACADEMIC_STAFF -> true  // Can view assets they manage
            else -> false
        }
    }
    
    fun canModifyAssets(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> false  // Can only view, not modify
            ROLE_NON_ACADEMIC_STAFF -> false  // Can only view, not modify
            else -> false
        }
    }
    
    // === EXAMINATION MANAGEMENT ACCESS ===
    
    fun canAccessExams(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> true  // Can view exams for their subjects
            ROLE_NON_ACADEMIC_STAFF -> false  // No exam access
            else -> false
        }
    }
    
    fun canModifyExams(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> false  // Can view but not modify exams
            else -> false
        }
    }
    
    fun canDeleteExams(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false
        }
    }
    
    // === TIMETABLE MANAGEMENT ACCESS ===
    
    fun canAccessTimetables(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> true  // Can view their teaching schedule
            ROLE_NON_ACADEMIC_STAFF -> true  // Can view general timetables
            else -> false
        }
    }
    
    fun canModifyTimetables(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false  // Academic/Non-Academic staff cannot modify timetables
        }
    }

    // === USER MANAGEMENT ACCESS ===

    fun canManageUsers(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false  // Only full access roles can manage users
        }
    }

    // === SYSTEM ADMINISTRATION ACCESS ===

    fun canAccessSystemSettings(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_TECHNICAL_OFFICER -> true  // Technical officer manages system
            else -> false
        }
    }

    fun canGenerateReports(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true  // Data officer specializes in reports
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> false  // Partial access - no report generation
            ROLE_NON_ACADEMIC_STAFF -> false
            else -> false
        }
    }

    fun canInitializeSampleData(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false  // Only full access roles can initialize data
        }
    }

    /**
     * Get comprehensive role permissions for a given role
     */
    fun getRolePermissions(role: String): Map<String, Boolean> {
        return mapOf(
            // Access Level
            "fullAccess" to hasFullAccess(role),
            "partialAccess" to hasPartialAccess(role),
            
            // Student Management
            "studentsAccess" to canAccessStudents(role),
            "studentsModify" to canModifyStudents(role),
            "studentsDelete" to canDeleteStudents(role),
            
            // Staff Management
            "staffAccess" to canAccessStaff(role),
            "staffModify" to canModifyStaff(role),
            "staffDelete" to canDeleteStaff(role),
            
            // Asset Management
            "assetsAccess" to canAccessAssets(role),
            "assetsModify" to canModifyAssets(role),
            
            // Examination Management
            "examsAccess" to canAccessExams(role),
            "examsModify" to canModifyExams(role),
            "examsDelete" to canDeleteExams(role),
            
            // Timetable Management
            "timetablesAccess" to canAccessTimetables(role),
            "timetablesModify" to canModifyTimetables(role),
            
            // System Administration
            "manageUsers" to canManageUsers(role),
            "systemSettings" to canAccessSystemSettings(role),
            "generateReports" to canGenerateReports(role),
            "initializeSampleData" to canInitializeSampleData(role)
        )
    }

    /**
     * Get user-friendly role description
     */
    fun getRoleDescription(role: String): String {
        return when (role) {
            ROLE_ADMINISTRATOR -> "System Administrator - Full system access"
            ROLE_PRINCIPAL -> "Principal - Full administrative access"
            ROLE_DATA_OFFICER -> "Data Officer - Data management and reporting"
            ROLE_TECHNICAL_OFFICER -> "Technical Officer - System and technical management"
            ROLE_ACADEMIC_STAFF -> "Academic Staff - Teaching and student interaction"
            ROLE_NON_ACADEMIC_STAFF -> "Non-Academic Staff - Support services"
            else -> "Unknown Role"
        }
    }
} 
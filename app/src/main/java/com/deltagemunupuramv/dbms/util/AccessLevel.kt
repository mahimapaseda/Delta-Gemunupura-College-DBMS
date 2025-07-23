package com.deltagemunupuramv.dbms.util

object AccessLevel {
    const val ROLE_ADMINISTRATOR = "Administrator"
    const val ROLE_PRINCIPAL = "Principal"
    const val ROLE_DATA_OFFICER = "Data Officer"
    const val ROLE_TECHNICAL_OFFICER = "Technical Officer"
    const val ROLE_ACADEMIC_STAFF = "Academic Staff"

    fun hasFullAccess(role: String): Boolean {
        return role == ROLE_ADMINISTRATOR
    }

    fun canAccessStudents(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> true
            else -> false
        }
    }
    
    fun canModifyStudents(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            else -> false
        }
    }
    
    fun canDeleteStudents(role: String): Boolean {
        return hasFullAccess(role)
    }
    
    fun canAccessStaff(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            else -> false
        }
    }
    
    fun canModifyStaff(role: String): Boolean {
        return hasFullAccess(role)
    }
    
    fun canAccessAssets(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_TECHNICAL_OFFICER -> true
            else -> false
        }
    }
    
    fun canModifyAssets(role: String): Boolean {
        return hasFullAccess(role)
    }
    
    fun canAccessExams(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_TECHNICAL_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> true
            else -> false
        }
    }
    
    fun canModifyExams(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            ROLE_ACADEMIC_STAFF -> true
            else -> false
        }
    }
    
    fun canDeleteExams(role: String): Boolean {
        return hasFullAccess(role)
    }
    
    fun canAccessTimetables(role: String): Boolean {
        return true // All roles can view timetables
    }
    
    fun canModifyTimetables(role: String): Boolean {
        return when (role) {
            ROLE_ADMINISTRATOR -> true
            ROLE_PRINCIPAL -> true
            ROLE_DATA_OFFICER -> true
            else -> false
        }
    }

    fun canManageUsers(role: String): Boolean {
        return hasFullAccess(role)
    }

    fun getRolePermissions(role: String): Map<String, Boolean> {
        return mapOf(
            "fullAccess" to hasFullAccess(role),
            "studentsAccess" to canAccessStudents(role),
            "studentsModify" to canModifyStudents(role),
            "studentsDelete" to canDeleteStudents(role),
            "staffAccess" to canAccessStaff(role),
            "staffModify" to canModifyStaff(role),
            "assetsAccess" to canAccessAssets(role),
            "assetsModify" to canModifyAssets(role),
            "examsAccess" to canAccessExams(role),
            "examsModify" to canModifyExams(role),
            "examsDelete" to canDeleteExams(role),
            "timetablesAccess" to canAccessTimetables(role),
            "timetablesModify" to canModifyTimetables(role),
            "manageUsers" to canManageUsers(role)
        )
    }
} 
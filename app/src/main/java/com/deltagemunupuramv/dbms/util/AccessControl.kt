package com.deltagemunupuramv.dbms.util

enum class UserRole {
    PRINCIPAL,
    DATA_OFFICER,
    TECHNICAL_OFFICER,
    TEACHER,
    STAFF,
    STUDENT
}

object AccessControl {
    fun canManageStaff(userRole: UserRole): Boolean {
        return when (userRole) {
            UserRole.PRINCIPAL,
            UserRole.DATA_OFFICER,
            UserRole.TECHNICAL_OFFICER -> true
            else -> false
        }
    }

    fun canGenerateReports(userRole: UserRole): Boolean {
        return when (userRole) {
            UserRole.PRINCIPAL,
            UserRole.DATA_OFFICER,
            UserRole.TECHNICAL_OFFICER -> true
            else -> false
        }
    }

    fun canEditStaff(userRole: UserRole): Boolean {
        return when (userRole) {
            UserRole.PRINCIPAL,
            UserRole.DATA_OFFICER -> true
            else -> false
        }
    }

    fun canDeleteStaff(userRole: UserRole): Boolean {
        return userRole == UserRole.PRINCIPAL
    }
} 
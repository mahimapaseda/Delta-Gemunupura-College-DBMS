package com.deltagemunupuramv.dbms.util

import android.content.Context
import android.content.SharedPreferences
import com.deltagemunupuramv.dbms.model.User
import com.google.gson.Gson

object UserSession {
    private const val PREF_NAME = "DBMSPrefs"
    private const val KEY_USER = "user"
    private lateinit var prefs: SharedPreferences
    private val gson by lazy { Gson() }

    private var currentUser: User? = null

    val currentRole: UserRole
        get() = when (currentUser?.role) {
            AccessLevel.ROLE_PRINCIPAL -> UserRole.PRINCIPAL
            AccessLevel.ROLE_DATA_OFFICER -> UserRole.DATA_OFFICER
            AccessLevel.ROLE_TECHNICAL_OFFICER -> UserRole.TECHNICAL_OFFICER
            AccessLevel.ROLE_ACADEMIC_STAFF -> UserRole.TEACHER
            AccessLevel.ROLE_ADMINISTRATOR -> UserRole.PRINCIPAL // Treat admin as principal for staff management
            else -> UserRole.STUDENT
        }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Load saved user if exists
        prefs.getString(KEY_USER, null)?.let { userJson ->
            try {
                currentUser = gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                // If there's an error parsing the user data, clear it
                clearSession()
            }
        }
    }

    fun setUser(user: User) {
        currentUser = user
        prefs.edit().apply {
            putString(KEY_USER, gson.toJson(user))
            apply()
        }
    }

    fun getUser(): User? = currentUser

    fun clearSession() {
        currentUser = null
        prefs.edit().clear().apply()
    }

    fun hasFullAccess(): Boolean {
        return currentUser?.let { AccessLevel.hasFullAccess(it.role) } ?: false
    }

    fun canModifyTimetables(): Boolean {
        return currentUser?.let { user ->
            AccessLevel.hasFullAccess(user.role) || user.role == AccessLevel.ROLE_DATA_OFFICER
        } ?: false
    }

    fun isLoggedIn(): Boolean = currentUser != null
} 
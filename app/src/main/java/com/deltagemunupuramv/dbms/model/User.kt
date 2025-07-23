package com.deltagemunupuramv.dbms.model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val role: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 
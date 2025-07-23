package com.deltagemunupuramv.dbms.model

data class TestConnection(
    val id: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
) 
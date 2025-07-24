package com.deltagemunupuramv.dbms.model

data class Feature(
    val icon: Int,
    val title: String,
    val description: String,
    val canView: Boolean = true,
    val canModify: Boolean = true
) 
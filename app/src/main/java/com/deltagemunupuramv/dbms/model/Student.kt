package com.deltagemunupuramv.dbms.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    val id: String = "",
    val fullName: String = "",
    val nameWithInitials: String = "",
    val indexNumber: String = "",
    val dateOfBirth: String = "",
    val nicNumber: String = "",
    val grade: String = "",
    val class_: String = "",  // Using class_ because 'class' is a reserved keyword
    val isStream: Boolean = false, // true if class_ represents a stream (for Grade 12/13)
    val gender: String = "",
    val religion: String = "",
    val address: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val admissionDate: String = "",
    val previousSchools: String = "",
    val medium: String = "",
    val subjects: String = "",
    val guardianName: String = "",
    val guardianContact: String = "",
    val guardianNic: String = "",
    val guardianOccupation: String = "",
    val siblings: String = "",
    val disabilities: String = "",
    val imageUrl: String = "", // URL to the student's image in Firebase Storage
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) : Parcelable {
    companion object {
        val CLASSES = listOf("A", "B")
        val STREAMS = listOf("Maths", "Bio", "Commerce", "Art")

        fun isStreamGrade(grade: String): Boolean {
            return grade == "Grade 12" || grade == "Grade 13"
        }

        fun getAvailableClassesOrStreams(grade: String): List<String> {
            return if (isStreamGrade(grade)) {
                STREAMS
            } else {
                CLASSES
            }
        }

        fun getClassOrStreamLabel(grade: String): String {
            return if (isStreamGrade(grade)) "Stream" else "Class"
        }
    }
} 
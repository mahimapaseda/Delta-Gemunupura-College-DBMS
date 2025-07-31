package com.deltagemunupuramv.dbms.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exam(
    val id: String = "",
    val examType: ExamType = ExamType.TERM_TEST,
    val title: String = "",
    val description: String = "",
    val examDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val duration: String = "",
    val grade: String = "",
    val subject: String = "",
    val totalMarks: Int = 0,
    val passMarks: Int = 0,
    val venue: String = "",
    val invigilator: String = "",
    val status: ExamStatus = ExamStatus.SCHEDULED,
    
    // For A/L and O/L Results
    val examYear: String = "",
    val examMonth: String = "",
    val resultsPublished: Boolean = false,
    val resultsDate: String = "",
    
    // For O/L Results specific fields
    val indexNo: String = "",
    val fullName: String = "",
    val nicNo: String = "",
    val attemptNo: String = "",
    val gender: String = "",
    val medium: String = "", // Sinhala or English
    val religion: String = "",
    val languageLiterature: String = "",
    val english: String = "",
    val science: String = "",
    val mathematics: String = "",
    val history: String = "",
    val firstSubjectGroup: String = "",
    val secondSubjectGroup: String = "",
    val thirdSubjectGroup: String = "",
    
    // For A/L Results specific fields
    val subjectStream: String = "", // Science, Commerce, Arts, Technology
    val subjectNo1: String = "",
    val subjectNo1Grade: String = "",
    val subjectNo2: String = "",
    val subjectNo2Grade: String = "",
    val subjectNo3: String = "",
    val subjectNo3Grade: String = "",
    val averageZScore: String = "",
    val districtRank: String = "",
    val islandRank: String = "",
    val generalEnglishGrade: String = "",
    val commonGeneralTestMarks: String = "",
    
    // Common fields
    val instructions: String = "",
    val materialsAllowed: String = "",
    val materialsNotAllowed: String = "",
    val specialInstructions: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

enum class ExamType {
    TERM_TEST,
    A_L_RESULTS,
    O_L_RESULTS
}

enum class ExamStatus {
    SCHEDULED,
    ONGOING,
    COMPLETED,
    CANCELLED,
    POSTPONED
}

enum class ExamGrade {
    GRADE_6,
    GRADE_7,
    GRADE_8,
    GRADE_9,
    GRADE_10,
    GRADE_11,
    GRADE_12,
    GRADE_13
}

enum class ExamSubject {
    MATHEMATICS,
    SCIENCE,
    SINHALA,
    ENGLISH,
    HISTORY,
    GEOGRAPHY,
    CIVICS,
    BUDDHISM,
    COMMERCE,
    ART,
    MUSIC,
    PHYSICAL_EDUCATION,
    AGRICULTURE,
    TECHNOLOGY,
    HOME_ECONOMICS,
    OTHER
} 
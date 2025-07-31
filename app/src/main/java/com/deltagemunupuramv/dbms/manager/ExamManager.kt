package com.deltagemunupuramv.dbms.manager

import android.util.Log
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ExamManager {
    private val database = FirebaseDatabase.getInstance()
    private val examsRef = database.getReference("exams")
    private var valueEventListener: ValueEventListener? = null

    companion object {
        private const val TAG = "ExamManager"
    }

    fun getAllExams(callback: (List<Exam>) -> Unit) {
        valueEventListener?.let { examsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exams = snapshot.children.mapNotNull { it.getValue(Exam::class.java) }
                    .sortedBy { it.examDate }
                callback(exams)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting exams: ${error.message}")
                callback(emptyList())
            }
        }

        examsRef.addValueEventListener(valueEventListener!!)
    }

    fun getFilteredExams(
        searchQuery: String = "",
        examType: ExamType? = null,
        status: ExamStatus? = null,
        grade: String = "",
        callback: (List<Exam>) -> Unit
    ) {
        valueEventListener?.let { examsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var filteredList = snapshot.children.mapNotNull { it.getValue(Exam::class.java) }

                // Apply search filter
                if (searchQuery.isNotEmpty()) {
                    filteredList = filteredList.filter { exam ->
                        exam.title.contains(searchQuery, ignoreCase = true) ||
                        exam.description.contains(searchQuery, ignoreCase = true) ||
                        exam.subject.contains(searchQuery, ignoreCase = true) ||
                        exam.venue.contains(searchQuery, ignoreCase = true)
                    }
                }

                // Apply exam type filter
                examType?.let { type ->
                    filteredList = filteredList.filter { it.examType == type }
                }

                // Apply status filter
                status?.let { examStatus ->
                    filteredList = filteredList.filter { it.status == examStatus }
                }

                // Apply grade filter
                if (grade.isNotEmpty()) {
                    filteredList = filteredList.filter { it.grade == grade }
                }

                callback(filteredList.sortedBy { it.examDate })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting filtered exams: ${error.message}")
                callback(emptyList())
            }
        }

        examsRef.addValueEventListener(valueEventListener!!)
    }

    fun getExamById(id: String, callback: (Exam?) -> Unit) {
        examsRef.child(id).get()
            .addOnSuccessListener { snapshot ->
                val exam = snapshot.getValue(Exam::class.java)
                callback(exam)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting exam by ID: ${exception.message}")
                callback(null)
            }
    }

    fun addExam(exam: Exam, callback: (Boolean) -> Unit) {
        val examId = exam.id.ifEmpty { examsRef.push().key ?: return }
        val examWithId = exam.copy(id = examId)
        
        examsRef.child(examId).setValue(examWithId)
            .addOnSuccessListener {
                Log.d(TAG, "Exam added successfully: ${examWithId.title}")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding exam: ${exception.message}")
                callback(false)
            }
    }

    fun updateExam(exam: Exam, callback: (Boolean) -> Unit) {
        examsRef.child(exam.id).setValue(exam)
            .addOnSuccessListener {
                Log.d(TAG, "Exam updated successfully: ${exam.title}")
                callback(true)
            }
            .addOnFailureListener { failure ->
                Log.e(TAG, "Error updating exam: ${failure.message}")
                callback(false)
            }
    }

    fun deleteExam(examId: String, callback: (Boolean) -> Unit) {
        examsRef.child(examId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Exam deleted successfully: $examId")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error deleting exam: ${exception.message}")
                callback(false)
            }
    }

    fun getExamsByType(examType: ExamType, callback: (List<Exam>) -> Unit) {
        valueEventListener?.let { examsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exams = snapshot.children.mapNotNull { it.getValue(Exam::class.java) }
                    .filter { it.examType == examType }
                    .sortedBy { it.examDate }
                callback(exams)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting exams by type: ${error.message}")
                callback(emptyList())
            }
        }

        examsRef.addValueEventListener(valueEventListener!!)
    }

    fun getUpcomingExams(callback: (List<Exam>) -> Unit) {
        val currentTime = System.currentTimeMillis()
        
        valueEventListener?.let { examsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exams = snapshot.children.mapNotNull { it.getValue(Exam::class.java) }
                    .filter { exam ->
                        exam.status == ExamStatus.SCHEDULED && 
                        exam.examDate.isNotEmpty()
                    }
                    .sortedBy { it.examDate }
                callback(exams)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting upcoming exams: ${error.message}")
                callback(emptyList())
            }
        }

        examsRef.addValueEventListener(valueEventListener!!)
    }

    fun cleanup() {
        valueEventListener?.let { examsRef.removeEventListener(it) }
    }
} 
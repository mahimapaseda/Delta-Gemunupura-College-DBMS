package com.deltagemunupuramv.dbms.manager

import android.util.Log
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ALExamManager {
    private val database = FirebaseDatabase.getInstance()
    private val alExamsRef = database.getReference("al_exams")
    private var valueEventListener: ValueEventListener? = null

    companion object {
        private const val TAG = "ALExamManager"
    }

    /**
     * Get all A/L exams organized by year
     */
    fun getAllALExamsByYear(callback: (Map<String, List<Exam>>) -> Unit) {
        valueEventListener?.let { alExamsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val examsByYear = mutableMapOf<String, MutableList<Exam>>()
                
                snapshot.children.forEach { yearSnapshot ->
                    val year = yearSnapshot.key ?: return@forEach
                    val exams = yearSnapshot.children.mapNotNull { it.getValue(Exam::class.java) }
                    if (exams.isNotEmpty()) {
                        examsByYear[year] = exams.toMutableList()
                    }
                }
                
                callback(examsByYear)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting A/L exams by year: ${error.message}")
                callback(emptyMap())
            }
        }

        alExamsRef.addValueEventListener(valueEventListener!!)
    }

    /**
     * Get A/L exams for a specific year
     */
    fun getALExamsByYear(year: String, callback: (List<Exam>) -> Unit) {
        alExamsRef.child(year).get()
            .addOnSuccessListener { snapshot ->
                val exams = snapshot.children.mapNotNull { it.getValue(Exam::class.java) }
                    .sortedBy { it.indexNo }
                callback(exams)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting A/L exams for year $year: ${exception.message}")
                callback(emptyList())
            }
    }

    /**
     * Get all A/L exams as a flat list
     */
    fun getAllALExams(callback: (List<Exam>) -> Unit) {
        valueEventListener?.let { alExamsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allExams = mutableListOf<Exam>()
                
                snapshot.children.forEach { yearSnapshot ->
                    yearSnapshot.children.forEach { examSnapshot ->
                        examSnapshot.getValue(Exam::class.java)?.let { exam ->
                            allExams.add(exam)
                        }
                    }
                }
                
                callback(allExams.sortedBy { it.examYear + it.indexNo })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting all A/L exams: ${error.message}")
                callback(emptyList())
            }
        }

        alExamsRef.addValueEventListener(valueEventListener!!)
    }

    /**
     * Add A/L exam organized by year
     */
    fun addALExam(exam: Exam, callback: (Boolean) -> Unit) {
        if (exam.examType != ExamType.A_L_RESULTS) {
            Log.e(TAG, "Attempted to add non-A/L exam to A/L exam manager")
            callback(false)
            return
        }

        val year = exam.examYear.ifEmpty { "Unknown" }
        val examId = exam.id.ifEmpty { alExamsRef.child(year).push().key ?: return }
        val examWithId = exam.copy(id = examId)
        
        alExamsRef.child(year).child(examId).setValue(examWithId)
            .addOnSuccessListener {
                Log.d(TAG, "A/L exam added successfully: ${examWithId.fullName} (${examWithId.examYear})")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding A/L exam: ${exception.message}")
                callback(false)
            }
    }

    /**
     * Update A/L exam
     */
    fun updateALExam(exam: Exam, callback: (Boolean) -> Unit) {
        if (exam.examType != ExamType.A_L_RESULTS) {
            Log.e(TAG, "Attempted to update non-A/L exam in A/L exam manager")
            callback(false)
            return
        }

        val year = exam.examYear.ifEmpty { "Unknown" }
        alExamsRef.child(year).child(exam.id).setValue(exam)
            .addOnSuccessListener {
                Log.d(TAG, "A/L exam updated successfully: ${exam.fullName} (${exam.examYear})")
                callback(true)
            }
            .addOnFailureListener { failure ->
                Log.e(TAG, "Error updating A/L exam: ${failure.message}")
                callback(false)
            }
    }

    /**
     * Delete A/L exam
     */
    fun deleteALExam(exam: Exam, callback: (Boolean) -> Unit) {
        val year = exam.examYear.ifEmpty { "Unknown" }
        alExamsRef.child(year).child(exam.id).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "A/L exam deleted successfully: ${exam.fullName} (${exam.examYear})")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error deleting A/L exam: ${exception.message}")
                callback(false)
            }
    }

    /**
     * Search A/L exams across all years
     */
    fun searchALExams(
        searchQuery: String = "",
        year: String = "",
        callback: (List<Exam>) -> Unit
    ) {
        valueEventListener?.let { alExamsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var filteredList = mutableListOf<Exam>()
                
                snapshot.children.forEach { yearSnapshot ->
                    val currentYear = yearSnapshot.key ?: return@forEach
                    
                    // Apply year filter if specified
                    if (year.isNotEmpty() && currentYear != year) {
                        return@forEach
                    }
                    
                    yearSnapshot.children.forEach { examSnapshot ->
                        examSnapshot.getValue(Exam::class.java)?.let { exam ->
                            // Apply search filter
                            if (searchQuery.isEmpty() || 
                                exam.fullName.contains(searchQuery, ignoreCase = true) ||
                                exam.indexNo.contains(searchQuery, ignoreCase = true) ||
                                exam.nicNo.contains(searchQuery, ignoreCase = true) ||
                                exam.examYear.contains(searchQuery, ignoreCase = true) ||
                                exam.subjectStream.contains(searchQuery, ignoreCase = true)) {
                                filteredList.add(exam)
                            }
                        }
                    }
                }
                
                callback(filteredList.sortedBy { it.examYear + it.indexNo })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error searching A/L exams: ${error.message}")
                callback(emptyList())
            }
        }

        alExamsRef.addValueEventListener(valueEventListener!!)
    }

    /**
     * Get available years for A/L exams
     */
    fun getAvailableYears(callback: (List<String>) -> Unit) {
        alExamsRef.get()
            .addOnSuccessListener { snapshot ->
                val years = snapshot.children.mapNotNull { it.key }.sortedDescending()
                callback(years)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting available years: ${exception.message}")
                callback(emptyList())
            }
    }

    /**
     * Get A/L exam by ID across all years
     */
    fun getALExamById(examId: String, callback: (Exam?) -> Unit) {
        alExamsRef.get()
            .addOnSuccessListener { snapshot ->
                var foundExam: Exam? = null
                
                snapshot.children.forEach { yearSnapshot ->
                    yearSnapshot.children.forEach { examSnapshot ->
                        if (examSnapshot.key == examId) {
                            foundExam = examSnapshot.getValue(Exam::class.java)
                            return@forEach
                        }
                    }
                }
                
                callback(foundExam)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting A/L exam by ID: ${exception.message}")
                callback(null)
            }
    }

    fun cleanup() {
        valueEventListener?.let { alExamsRef.removeEventListener(it) }
    }
} 
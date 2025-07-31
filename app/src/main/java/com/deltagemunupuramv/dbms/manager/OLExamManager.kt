package com.deltagemunupuramv.dbms.manager

import android.util.Log
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class OLExamManager {
    private val database = FirebaseDatabase.getInstance()
    private val olExamsRef = database.getReference("ol_exams")
    private var valueEventListener: ValueEventListener? = null

    companion object {
        private const val TAG = "OLExamManager"
    }

    /**
     * Get all O/L exams organized by year
     */
    fun getAllOLExamsByYear(callback: (Map<String, List<Exam>>) -> Unit) {
        valueEventListener?.let { olExamsRef.removeEventListener(it) }

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
                Log.e(TAG, "Error getting O/L exams by year: ${error.message}")
                callback(emptyMap())
            }
        }

        olExamsRef.addValueEventListener(valueEventListener!!)
    }

    /**
     * Get O/L exams for a specific year
     */
    fun getOLExamsByYear(year: String, callback: (List<Exam>) -> Unit) {
        olExamsRef.child(year).get()
            .addOnSuccessListener { snapshot ->
                val exams = snapshot.children.mapNotNull { it.getValue(Exam::class.java) }
                    .sortedBy { it.indexNo }
                callback(exams)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting O/L exams for year $year: ${exception.message}")
                callback(emptyList())
            }
    }

    /**
     * Get all O/L exams as a flat list
     */
    fun getAllOLExams(callback: (List<Exam>) -> Unit) {
        valueEventListener?.let { olExamsRef.removeEventListener(it) }

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
                Log.e(TAG, "Error getting all O/L exams: ${error.message}")
                callback(emptyList())
            }
        }

        olExamsRef.addValueEventListener(valueEventListener!!)
    }

    /**
     * Add O/L exam organized by year
     */
    fun addOLExam(exam: Exam, callback: (Boolean) -> Unit) {
        if (exam.examType != ExamType.O_L_RESULTS) {
            Log.e(TAG, "Attempted to add non-O/L exam to O/L exam manager")
            callback(false)
            return
        }

        val year = exam.examYear.ifEmpty { "Unknown" }
        val examId = exam.id.ifEmpty { olExamsRef.child(year).push().key ?: return }
        val examWithId = exam.copy(id = examId)
        
        olExamsRef.child(year).child(examId).setValue(examWithId)
            .addOnSuccessListener {
                Log.d(TAG, "O/L exam added successfully: ${examWithId.fullName} (${examWithId.examYear})")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding O/L exam: ${exception.message}")
                callback(false)
            }
    }

    /**
     * Update O/L exam
     */
    fun updateOLExam(exam: Exam, callback: (Boolean) -> Unit) {
        if (exam.examType != ExamType.O_L_RESULTS) {
            Log.e(TAG, "Attempted to update non-O/L exam in O/L exam manager")
            callback(false)
            return
        }

        val year = exam.examYear.ifEmpty { "Unknown" }
        olExamsRef.child(year).child(exam.id).setValue(exam)
            .addOnSuccessListener {
                Log.d(TAG, "O/L exam updated successfully: ${exam.fullName} (${exam.examYear})")
                callback(true)
            }
            .addOnFailureListener { failure ->
                Log.e(TAG, "Error updating O/L exam: ${failure.message}")
                callback(false)
            }
    }

    /**
     * Delete O/L exam
     */
    fun deleteOLExam(exam: Exam, callback: (Boolean) -> Unit) {
        val year = exam.examYear.ifEmpty { "Unknown" }
        olExamsRef.child(year).child(exam.id).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "O/L exam deleted successfully: ${exam.fullName} (${exam.examYear})")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error deleting O/L exam: ${exception.message}")
                callback(false)
            }
    }

    /**
     * Search O/L exams across all years
     */
    fun searchOLExams(
        searchQuery: String = "",
        year: String = "",
        callback: (List<Exam>) -> Unit
    ) {
        valueEventListener?.let { olExamsRef.removeEventListener(it) }

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
                                exam.examYear.contains(searchQuery, ignoreCase = true)) {
                                filteredList.add(exam)
                            }
                        }
                    }
                }
                
                callback(filteredList.sortedBy { it.examYear + it.indexNo })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error searching O/L exams: ${error.message}")
                callback(emptyList())
            }
        }

        olExamsRef.addValueEventListener(valueEventListener!!)
    }

    /**
     * Get available years for O/L exams
     */
    fun getAvailableYears(callback: (List<String>) -> Unit) {
        olExamsRef.get()
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
     * Get O/L exam by ID across all years
     */
    fun getOLExamById(examId: String, callback: (Exam?) -> Unit) {
        olExamsRef.get()
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
                Log.e(TAG, "Error getting O/L exam by ID: ${exception.message}")
                callback(null)
            }
    }

    fun cleanup() {
        valueEventListener?.let { olExamsRef.removeEventListener(it) }
    }
} 
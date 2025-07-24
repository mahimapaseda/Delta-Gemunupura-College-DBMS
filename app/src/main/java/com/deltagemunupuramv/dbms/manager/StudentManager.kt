package com.deltagemunupuramv.dbms.manager

import com.deltagemunupuramv.dbms.model.Student
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class StudentManager {
    private val database = FirebaseDatabase.getInstance()
    private val studentsRef = database.getReference("students")
    private var valueEventListener: ValueEventListener? = null

    fun addStudent(student: Student, callback: (Boolean) -> Unit) {
        studentsRef.child(student.id).setValue(student)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun updateStudent(student: Student, callback: (Boolean) -> Unit) {
        studentsRef.child(student.id).setValue(student)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun deleteStudent(studentId: String, callback: (Boolean) -> Unit) {
        studentsRef.child(studentId).removeValue()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getFilteredStudents(
        searchQuery: String = "",
        grade: String = "",
        callback: (List<Student>) -> Unit
    ) {
        valueEventListener?.let { studentsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var filteredList = snapshot.children.mapNotNull { it.getValue(Student::class.java) }

                // Apply search filter
                if (searchQuery.isNotEmpty()) {
                    filteredList = filteredList.filter { student ->
                        student.fullName.contains(searchQuery, ignoreCase = true) ||
                        student.indexNumber.contains(searchQuery, ignoreCase = true) ||
                        student.nameWithInitials.contains(searchQuery, ignoreCase = true)
                    }
                }

                // Apply grade filter
                if (grade.isNotEmpty() && grade != "All Grades") {
                    filteredList = filteredList.filter { it.grade == grade }
                }

                // Sort results
                val sortedList = filteredList.sortedWith(
                    compareBy<Student> { it.grade }
                        .thenBy { it.fullName }
                )

                callback(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        }

        studentsRef.addValueEventListener(valueEventListener!!)
    }

    fun getAllStudents(callback: (List<Student>) -> Unit) {
        valueEventListener?.let { studentsRef.removeEventListener(it) }

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val students = snapshot.children.mapNotNull { it.getValue(Student::class.java) }
                    .sortedWith(
                        compareBy<Student> { it.grade }
                            .thenBy { it.fullName }
                    )
                callback(students)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        }

        studentsRef.addValueEventListener(valueEventListener!!)
    }

    fun getStudentById(studentId: String, callback: (Student?) -> Unit) {
        studentsRef.child(studentId).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.getValue(Student::class.java))
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun cleanup() {
        valueEventListener?.let { studentsRef.removeEventListener(it) }
        valueEventListener = null
    }
} 
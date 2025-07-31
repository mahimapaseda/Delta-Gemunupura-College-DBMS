package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deltagemunupuramv.dbms.adapter.StudentAdapter
import com.deltagemunupuramv.dbms.databinding.ActivityManageStudentsBinding
import com.deltagemunupuramv.dbms.manager.StudentManager
import com.deltagemunupuramv.dbms.model.Student
import com.google.android.material.snackbar.Snackbar

class ManageStudentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageStudentsBinding
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var studentManager: StudentManager

    private var currentSearchQuery = ""
    private var currentGrade = "All Grades"

    companion object {
        private const val EDIT_STUDENT_REQUEST = 1001
        private const val ADD_STUDENT_REQUEST = 1002
        private const val VIEW_STUDENT_REQUEST = 1003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        setupAddButton()

        studentManager = StudentManager()
        loadStudents()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupFilters() {
        // Setup Grade filter
        val grades = arrayOf(
            "All Grades",
            "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11",
            "Grade 12", "Grade 13"
        )
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        binding.gradeFilterDropdown.setAdapter(gradeAdapter)
        binding.gradeFilterDropdown.setText("All Grades", false)

        // Setup search functionality
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString() ?: ""
                filterStudents()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Setup grade filter listener
        binding.gradeFilterDropdown.setOnItemClickListener { _, _, _, _ ->
            currentGrade = binding.gradeFilterDropdown.text.toString()
            filterStudents()
        }
    }

    private fun filterStudents() {
        showLoading(true)
        studentManager.getFilteredStudents(
            searchQuery = currentSearchQuery,
            grade = currentGrade
        ) { students ->
            if (!isFinishing) {
                showLoading(false)
                updateStudentsList(students)
            }
        }
    }

    private fun updateStudentsList(students: List<Student>) {
        studentAdapter.updateStudents(students)
        updateResultsHeader(students.size)
        
        // Show or hide empty state
        binding.emptyState.visibility = if (students.isEmpty()) View.VISIBLE else View.GONE
        binding.studentsRecyclerView.visibility = if (students.isEmpty()) View.GONE else View.VISIBLE

        // Update the results text with more detailed information
        val resultsText = buildFilterDescription(students.size)
        binding.resultsText.text = resultsText
    }

    private fun buildFilterDescription(count: Int): String {
        val countText = if (count == 1) "1 Student" else "$count Students"
        
        return when {
            // No grade filter
            currentGrade == "All Grades" -> countText
            
            // Grade filter applied
            else -> "$countText in $currentGrade"
        }
    }

    private fun updateResultsHeader(count: Int) {
        binding.totalCountChip.text = count.toString()
    }

    private fun setupRecyclerView() {
        // Setup adapter
        studentAdapter = StudentAdapter(
            students = emptyList(),
            onItemClick = { student ->
                val intent = Intent(this, ViewStudentActivity::class.java).apply {
                    putExtra(ViewStudentActivity.EXTRA_STUDENT, student)
                }
                startActivityForResult(intent, VIEW_STUDENT_REQUEST)
            },
            onEditClick = { student ->
                val intent = Intent(this, EditStudentActivity::class.java).apply {
                    putExtra(EditStudentActivity.EXTRA_STUDENT, student)
                }
                startActivityForResult(intent, EDIT_STUDENT_REQUEST)
            },
            onDeleteClick = { student ->
                showDeleteConfirmation(student)
            }
        )

        binding.studentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ManageStudentsActivity)
            adapter = studentAdapter
        }
    }

    private fun setupAddButton() {
        binding.addStudentFab.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivityForResult(intent, ADD_STUDENT_REQUEST)
        }
    }

    private fun loadStudents() {
        showLoading(true)
        studentManager.getAllStudents { students ->
            if (!isFinishing) {  // Check if activity is still active
                showLoading(false)
                updateStudentsList(students)
            }
        }
    }

    private fun showDeleteConfirmation(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.fullName}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteStudent(student)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStudent(student: Student) {
        showLoading(true)
        studentManager.deleteStudent(student.id) { success ->
            if (!isFinishing) {
                showLoading(false)
                if (success) {
                    Snackbar.make(binding.root, "Student deleted successfully", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            // Restore the deleted student
                            restoreStudent(student)
                        }
                        .show()
                } else {
                    Snackbar.make(binding.root, "Failed to delete student", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun restoreStudent(student: Student) {
        studentManager.addStudent(student) { success ->
            if (!isFinishing) {
                if (success) {
                    Snackbar.make(binding.root, "Student restored successfully", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Failed to restore student", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.addStudentFab.isEnabled = !show
        
        // Only hide the RecyclerView if we're showing loading and there are no items
        if (show && studentAdapter.itemCount == 0) {
            binding.studentsRecyclerView.visibility = View.GONE
            binding.emptyState.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the student list when returning to this screen
        loadStudents()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any listeners or resources
        studentManager.cleanup()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_STUDENT_REQUEST, ADD_STUDENT_REQUEST, VIEW_STUDENT_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    // Refresh the student list when returning from edit, add, or view
                    filterStudents()
                }
            }
        }
    }
} 
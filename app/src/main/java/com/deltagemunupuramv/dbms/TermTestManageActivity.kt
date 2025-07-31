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
import com.deltagemunupuramv.dbms.adapter.ExamAdapter
import com.deltagemunupuramv.dbms.databinding.ActivityTermTestManageBinding
import com.deltagemunupuramv.dbms.manager.ExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.android.material.snackbar.Snackbar

class TermTestManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermTestManageBinding
    private lateinit var examManager: ExamManager
    private lateinit var examAdapter: ExamAdapter
    private var allTermTests = listOf<Exam>()

    companion object {
        private const val ADD_EXAM_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermTestManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupExamManager()
        setupRecyclerView()
        setupFilters()
        setupSearch()
        setupFab()
        loadTermTests()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupExamManager() {
        examManager = ExamManager()
    }

    private fun setupRecyclerView() {
        examAdapter = ExamAdapter(
            exams = emptyList(),
            onExamClick = { exam -> viewExam(exam) },
            onEditClick = { exam -> editExam(exam) },
            onDeleteClick = { exam -> deleteExam(exam) }
        )

        binding.termTestsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TermTestManageActivity)
            adapter = examAdapter
        }
    }

    private fun setupFilters() {
        // Status Filter
        val statuses = arrayOf("All Statuses", "Scheduled", "Ongoing", "Completed", "Cancelled", "Postponed")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statuses)
        binding.statusFilterDropdown.setAdapter(statusAdapter)
        binding.statusFilterDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedStatus = when (position) {
                0 -> null
                1 -> ExamStatus.SCHEDULED
                2 -> ExamStatus.ONGOING
                3 -> ExamStatus.COMPLETED
                4 -> ExamStatus.CANCELLED
                5 -> ExamStatus.POSTPONED
                else -> null
            }
            applyFilters()
        }

        // Grade Filter
        val grades = arrayOf("All Grades", "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12", "Grade 13")
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        binding.gradeFilterDropdown.setAdapter(gradeAdapter)
        binding.gradeFilterDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedGrade = if (position == 0) "" else grades[position]
            applyFilters()
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                applyFilters()
            }
        })
    }

    private fun setupFab() {
        binding.addTermTestFab.setOnClickListener {
            startActivityForResult(Intent(this, AddExamActivity::class.java), ADD_EXAM_REQUEST_CODE)
        }
    }

    private fun loadTermTests() {
        showLoading(true)
        examManager.getExamsByType(ExamType.TERM_TEST) { exams ->
            allTermTests = exams
            showLoading(false)
            applyFilters()
        }
    }

    private fun applyFilters() {
        val searchQuery = binding.searchInput.text.toString().trim()
        val selectedStatus = getSelectedStatus()
        val selectedGrade = getSelectedGrade()

        val filteredExams = allTermTests.filter { exam ->
            var matches = true

            // Apply search filter
            if (searchQuery.isNotEmpty()) {
                matches = matches && (
                    exam.title.contains(searchQuery, ignoreCase = true) ||
                    exam.description.contains(searchQuery, ignoreCase = true) ||
                    exam.subject.contains(searchQuery, ignoreCase = true) ||
                    exam.venue.contains(searchQuery, ignoreCase = true)
                )
            }

            // Apply status filter
            selectedStatus?.let { status ->
                matches = matches && exam.status == status
            }

            // Apply grade filter
            if (selectedGrade.isNotEmpty()) {
                matches = matches && exam.grade == selectedGrade
            }

            matches
        }

        updateUI(filteredExams)
    }

    private fun getSelectedStatus(): ExamStatus? {
        val selectedText = binding.statusFilterDropdown.text.toString()
        return when (selectedText) {
            "Scheduled" -> ExamStatus.SCHEDULED
            "Ongoing" -> ExamStatus.ONGOING
            "Completed" -> ExamStatus.COMPLETED
            "Cancelled" -> ExamStatus.CANCELLED
            "Postponed" -> ExamStatus.POSTPONED
            else -> null
        }
    }

    private fun getSelectedGrade(): String {
        val selectedText = binding.gradeFilterDropdown.text.toString()
        return if (selectedText == "All Grades") "" else selectedText
    }

    private fun updateUI(exams: List<Exam>) {
        examAdapter.updateExams(exams)
        binding.totalCountChip.text = exams.size.toString()
        binding.resultsText.text = if (exams.size == 1) getString(R.string.term_test) else getString(R.string.term_test_management)

        if (exams.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.termTestsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.termTestsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun viewExam(exam: Exam) {
        // TODO: Navigate to View Exam Activity
        Snackbar.make(binding.root, "View Term Test: ${exam.title}", Snackbar.LENGTH_SHORT).show()
    }

    private fun editExam(exam: Exam) {
        // TODO: Navigate to Edit Exam Activity
        Snackbar.make(binding.root, "Edit Term Test: ${exam.title}", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteExam(exam: Exam) {
        AlertDialog.Builder(this)
            .setTitle("Delete Term Test")
            .setMessage("Are you sure you want to delete '${exam.title}'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                performDeleteExam(exam)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDeleteExam(exam: Exam) {
        examManager.deleteExam(exam.id) { success ->
            if (success) {
                Snackbar.make(binding.root, "Term test deleted successfully", Snackbar.LENGTH_SHORT).show()
                loadTermTests() // Reload the list
            } else {
                Snackbar.make(binding.root, "Failed to delete term test", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EXAM_REQUEST_CODE && resultCode == RESULT_OK) {
            loadTermTests() // Reload the list when a new exam is added
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        examManager.cleanup()
    }
} 
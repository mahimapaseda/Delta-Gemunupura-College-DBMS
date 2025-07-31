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
import com.deltagemunupuramv.dbms.databinding.ActivityManageExamsBinding
import com.deltagemunupuramv.dbms.manager.ExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.android.material.snackbar.Snackbar

class ManageExamsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageExamsBinding
    private lateinit var examManager: ExamManager
    private lateinit var examAdapter: ExamAdapter
    private var allExams = listOf<Exam>()

    companion object {
        private const val ADD_EXAM_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageExamsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupExamManager()
        setupRecyclerView()
        setupSearch()
        setupExamTypeCards()
        loadExams()
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

        binding.examsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ManageExamsActivity)
            adapter = examAdapter
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

    private fun setupExamTypeCards() {
        binding.termTestCard.setOnClickListener {
            val intent = Intent(this, TermTestManageActivity::class.java)
            startActivity(intent)
        }
        binding.alResultsCard.setOnClickListener {
            val intent = Intent(this, ALResultsManageActivity::class.java)
            startActivity(intent)
        }
        binding.olResultsCard.setOnClickListener {
            val intent = Intent(this, OLResultsManageActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadExams() {
        showLoading(true)
        examManager.getAllExams { exams ->
            allExams = exams
            showLoading(false)
            applyFilters()
        }
    }

    private fun applyFilters() {
        val searchQuery = binding.searchInput.text.toString().trim()

        examManager.getFilteredExams(
            searchQuery = searchQuery,
            examType = null,
            status = null,
            grade = ""
        ) { filteredExams ->
            updateUI(filteredExams)
        }
    }

    private fun updateUI(exams: List<Exam>) {
        examAdapter.updateExams(exams)
        binding.totalCountChip.text = exams.size.toString()
        binding.resultsText.text = if (exams.size == 1) "Exam" else "Exams"

        if (exams.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.examsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.examsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun viewExam(exam: Exam) {
        // TODO: Navigate to View Exam Activity
        Snackbar.make(binding.root, "View Exam: ${exam.title}", Snackbar.LENGTH_SHORT).show()
    }

    private fun editExam(exam: Exam) {
        // TODO: Navigate to Edit Exam Activity
        Snackbar.make(binding.root, "Edit Exam: ${exam.title}", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteExam(exam: Exam) {
        AlertDialog.Builder(this)
            .setTitle("Delete Exam")
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
                Snackbar.make(binding.root, "Exam deleted successfully", Snackbar.LENGTH_SHORT).show()
                loadExams() // Reload the list
            } else {
                Snackbar.make(binding.root, "Failed to delete exam", Snackbar.LENGTH_SHORT).show()
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
            loadExams() // Reload the list when a new exam is added
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        examManager.cleanup()
    }
} 
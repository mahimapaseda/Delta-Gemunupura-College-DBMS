package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deltagemunupuramv.dbms.adapter.OLResultAdapter
import com.deltagemunupuramv.dbms.databinding.ActivityOlResultsManageBinding
import com.deltagemunupuramv.dbms.manager.OLExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.google.android.material.snackbar.Snackbar

class OLResultsManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOlResultsManageBinding
    private lateinit var olExamManager: OLExamManager
    private lateinit var olResultAdapter: OLResultAdapter
    private var allOLResults = listOf<Exam>()

    companion object {
        private const val ADD_EXAM_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOlResultsManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupOLExamManager()
        setupRecyclerView()
        setupSearch()
        setupFab()
        loadOLResults()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupOLExamManager() {
        olExamManager = OLExamManager()
    }

    private fun setupRecyclerView() {
        olResultAdapter = OLResultAdapter(
            olResults = emptyList(),
            onOLResultClick = { exam -> viewExam(exam) },
            onEditClick = { exam -> editExam(exam) },
            onDeleteClick = { exam -> deleteExam(exam) }
        )

        binding.olResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OLResultsManageActivity)
            adapter = olResultAdapter
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterOLResults(s.toString())
            }
        })
    }

    private fun setupFab() {
        binding.addOLResultFab.setOnClickListener {
            startActivityForResult(Intent(this, AddOLResultActivity::class.java), ADD_EXAM_REQUEST_CODE)
        }
        
        // Setup "Add First Result" button in empty state
        binding.addFirstResultButton.setOnClickListener {
            startActivityForResult(Intent(this, AddOLResultActivity::class.java), ADD_EXAM_REQUEST_CODE)
        }
    }

    private fun loadOLResults() {
        showLoading(true)
        olExamManager.getAllOLExams { exams ->
            allOLResults = exams
            updateUI(exams)
            showLoading(false)
        }
    }

    private fun filterOLResults(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            updateUI(allOLResults)
        } else {
            val filteredList = allOLResults.filter { exam ->
                exam.title.contains(searchQuery, ignoreCase = true) ||
                exam.description.contains(searchQuery, ignoreCase = true) ||
                exam.subject.contains(searchQuery, ignoreCase = true) ||
                exam.venue.contains(searchQuery, ignoreCase = true) ||
                exam.fullName.contains(searchQuery, ignoreCase = true) ||
                exam.indexNo.contains(searchQuery, ignoreCase = true) ||
                exam.nicNo.contains(searchQuery, ignoreCase = true) ||
                exam.examYear.contains(searchQuery, ignoreCase = true)
            }
            updateUI(filteredList)
        }
    }

    private fun updateUI(exams: List<Exam>) {
        // Limit to maximum 3 items
        val limitedExams = exams.take(3)
        olResultAdapter.updateOLResults(limitedExams)
        binding.totalCountChip.text = exams.size.toString()
        
        // Update results text to show limitation
        val resultsText = when {
            exams.isEmpty() -> "No O/L Results"
            exams.size == 1 -> "O/L Result (Showing 1 of 1)"
            exams.size <= 3 -> "O/L Results (Showing ${exams.size} of ${exams.size})"
            else -> "O/L Results (Showing 3 of ${exams.size})"
        }
        binding.resultsText.text = resultsText
        
        binding.emptyState.visibility = if (exams.isEmpty()) View.VISIBLE else View.GONE
        binding.olResultsRecyclerView.visibility = if (exams.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun viewExam(exam: Exam) {
        val intent = Intent(this, ViewOLResultActivity::class.java)
        intent.putExtra(ViewOLResultActivity.EXTRA_EXAM_ID, exam.id)
        startActivity(intent)
    }

    private fun editExam(exam: Exam) {
        val intent = Intent(this, AddOLResultActivity::class.java)
        intent.putExtra(AddOLResultActivity.EXTRA_EXAM_ID, exam.id)
        startActivityForResult(intent, ADD_EXAM_REQUEST_CODE)
    }

    private fun deleteExam(exam: Exam) {
        AlertDialog.Builder(this)
            .setTitle("Delete O/L Result")
            .setMessage("Are you sure you want to delete the O/L result for ${exam.fullName}?")
            .setPositiveButton("Delete") { _, _ ->
                olExamManager.deleteOLExam(exam) { success ->
                    if (success) {
                        Snackbar.make(binding.root, "O/L result deleted successfully", Snackbar.LENGTH_LONG).show()
                        loadOLResults()
                    } else {
                        Snackbar.make(binding.root, "Failed to delete O/L result", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EXAM_REQUEST_CODE && resultCode == RESULT_OK) {
            loadOLResults()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        olExamManager.cleanup()
    }
} 
package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deltagemunupuramv.dbms.adapter.ALExamAdapter
import com.deltagemunupuramv.dbms.databinding.ActivityAlResultsManageBinding
import com.deltagemunupuramv.dbms.manager.ALExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.google.android.material.snackbar.Snackbar

class ALResultsManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlResultsManageBinding
    private lateinit var alExamManager: ALExamManager
    private lateinit var alExamAdapter: ALExamAdapter
    private var allALExams = listOf<Exam>()

    companion object {
        private const val ADD_EXAM_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlResultsManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupALExamManager()
        setupRecyclerView()
        setupSearch()
        setupFab()
        loadALExams()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupALExamManager() {
        alExamManager = ALExamManager()
    }

    private fun setupRecyclerView() {
        alExamAdapter = ALExamAdapter(
            alExams = emptyList(),
            onALExamClick = { alExam -> viewALExam(alExam) },
            onEditClick = { alExam -> editALExam(alExam) },
            onDeleteClick = { alExam -> deleteALExam(alExam) }
        )

        binding.alResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ALResultsManageActivity)
            adapter = alExamAdapter
        }
    }



    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterALExams(s.toString())
            }
        })
    }

    private fun setupFab() {
        binding.addALResultFab.setOnClickListener {
            startActivityForResult(Intent(this, AddALResultActivity::class.java), ADD_EXAM_REQUEST_CODE)
        }
        
        // Setup "Add First Result" button in empty state
        binding.addFirstResultButton.setOnClickListener {
            startActivityForResult(Intent(this, AddALResultActivity::class.java), ADD_EXAM_REQUEST_CODE)
        }
    }

    private fun loadALExams() {
        showLoading(true)
        alExamManager.getAllALExams { exams ->
            allALExams = exams
            updateUI(exams)
            showLoading(false)
        }
    }

    private fun filterALExams(searchQuery: String) {
        if (searchQuery.isEmpty()) {
            updateUI(allALExams)
        } else {
            val filteredList = allALExams.filter { exam ->
                exam.fullName.contains(searchQuery, ignoreCase = true) ||
                exam.indexNo.contains(searchQuery, ignoreCase = true) ||
                exam.nicNo.contains(searchQuery, ignoreCase = true) ||
                exam.subjectStream.contains(searchQuery, ignoreCase = true) ||
                exam.medium.contains(searchQuery, ignoreCase = true) ||
                exam.examYear.contains(searchQuery, ignoreCase = true)
            }
            updateUI(filteredList)
        }
    }

    private fun updateUI(exams: List<Exam>) {
        // Limit to maximum 3 items
        val limitedExams = exams.take(3)
        alExamAdapter.updateALExams(limitedExams)
        binding.totalCountChip.text = exams.size.toString()
        
        // Update results text to show limitation
        val resultsText = when {
            exams.isEmpty() -> "No A/L Results"
            exams.size == 1 -> "A/L Result (Showing 1 of 1)"
            exams.size <= 3 -> "A/L Results (Showing ${exams.size} of ${exams.size})"
            else -> "A/L Results (Showing 3 of ${exams.size})"
        }
        binding.resultsText.text = resultsText
        
        binding.emptyState.visibility = if (exams.isEmpty()) View.VISIBLE else View.GONE
        binding.alResultsRecyclerView.visibility = if (exams.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun viewALExam(alExam: Exam) {
        val intent = Intent(this, ViewALResultActivity::class.java)
        intent.putExtra(ViewALResultActivity.EXTRA_AL_RESULT_ID, alExam.id)
        startActivity(intent)
    }

    private fun editALExam(alExam: Exam) {
        val intent = Intent(this, AddALResultActivity::class.java)
        intent.putExtra(AddALResultActivity.EXTRA_AL_RESULT_ID, alExam.id)
        startActivityForResult(intent, ADD_EXAM_REQUEST_CODE)
    }

    private fun deleteALExam(alExam: Exam) {
        AlertDialog.Builder(this)
            .setTitle("Delete A/L Result")
            .setMessage("Are you sure you want to delete the A/L result for ${alExam.fullName}?")
            .setPositiveButton("Delete") { _, _ ->
                alExamManager.deleteALExam(alExam) { success ->
                    if (success) {
                        Snackbar.make(binding.root, "A/L result deleted successfully", Snackbar.LENGTH_LONG).show()
                        loadALExams()
                    } else {
                        Snackbar.make(binding.root, "Failed to delete A/L result", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EXAM_REQUEST_CODE && resultCode == RESULT_OK) {
            loadALExams() // Reload the list when a new exam is added
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alExamManager.cleanup()
    }
} 
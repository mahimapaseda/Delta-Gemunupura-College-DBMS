package com.deltagemunupuramv.dbms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.ActivityViewOlResultBinding
import com.deltagemunupuramv.dbms.manager.OLExamManager
import com.deltagemunupuramv.dbms.model.Exam

class ViewOLResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewOlResultBinding
    private lateinit var olExamManager: OLExamManager
    private var examId: String? = null

    companion object {
        const val EXTRA_EXAM_ID = "exam_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOlResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupOLExamManager()
        loadExamData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "View O/L Result"
    }

    private fun setupOLExamManager() {
        olExamManager = OLExamManager()
    }

    private fun loadExamData() {
        examId = intent.getStringExtra(EXTRA_EXAM_ID)
        if (examId != null) {
            olExamManager.getOLExamById(examId!!) { exam ->
                exam?.let { populateExamData(it) }
            }
        }
    }

    private fun populateExamData(exam: Exam) {
        // Basic Information
        binding.yearValue.text = exam.examYear
        binding.indexNoValue.text = exam.indexNo
        binding.fullNameValue.text = exam.fullName
        binding.nicNoValue.text = exam.nicNo
        binding.attemptNoValue.text = exam.attemptNo
        binding.genderValue.text = exam.gender
        binding.mediumValue.text = exam.medium
        binding.religionValue.text = exam.religion

        // Subject Results
        binding.languageLiteratureValue.text = exam.languageLiterature
        binding.englishValue.text = exam.english
        binding.scienceValue.text = exam.science
        binding.mathematicsValue.text = exam.mathematics
        binding.historyValue.text = exam.history

        // Subject Groups
        binding.firstSubjectGroupValue.text = exam.firstSubjectGroup
        binding.secondSubjectGroupValue.text = exam.secondSubjectGroup
        binding.thirdSubjectGroupValue.text = exam.thirdSubjectGroup

        // Additional Information
        binding.examDateValue.text = exam.examDate
        binding.statusValue.text = exam.status.name
        binding.resultsDateValue.text = exam.resultsDate
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
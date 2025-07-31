package com.deltagemunupuramv.dbms

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.ActivityAddExamBinding
import com.deltagemunupuramv.dbms.manager.ExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.android.material.snackbar.Snackbar
import java.util.UUID

class AddExamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExamBinding
    private lateinit var examManager: ExamManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupExamManager()
        setupDropdowns()
        setupButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupExamManager() {
        examManager = ExamManager()
    }

    private fun setupDropdowns() {
        // Exam Type Dropdown
        val examTypes = arrayOf("Term Test", "A/L Results", "O/L Results")
        val examTypeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, examTypes)
        binding.examTypeDropdown.setAdapter(examTypeAdapter)

        // Grade Dropdown
        val grades = arrayOf("Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10", "Grade 11", "Grade 12", "Grade 13")
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        binding.gradeDropdown.setAdapter(gradeAdapter)
    }

    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            if (validateInputs()) {
                saveExam()
            }
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate Title
        if (binding.titleInput.text.toString().trim().isEmpty()) {
            binding.titleLayout.error = "Title is required"
            isValid = false
        } else {
            binding.titleLayout.error = null
        }

        // Validate Subject
        if (binding.subjectInput.text.toString().trim().isEmpty()) {
            binding.subjectLayout.error = "Subject is required"
            isValid = false
        } else {
            binding.subjectLayout.error = null
        }

        // Validate Exam Date
        if (binding.examDateInput.text.toString().trim().isEmpty()) {
            binding.examDateLayout.error = "Exam date is required"
            isValid = false
        } else {
            binding.examDateLayout.error = null
        }

        // Validate Venue
        if (binding.venueInput.text.toString().trim().isEmpty()) {
            binding.venueLayout.error = "Venue is required"
            isValid = false
        } else {
            binding.venueLayout.error = null
        }

        return isValid
    }

    private fun saveExam() {
        val exam = Exam(
            id = UUID.randomUUID().toString(),
            examType = getExamTypeFromDropdown(),
            title = binding.titleInput.text.toString().trim(),
            description = binding.descriptionInput.text.toString().trim(),
            examDate = binding.examDateInput.text.toString().trim(),
            startTime = binding.startTimeInput.text.toString().trim(),
            endTime = binding.endTimeInput.text.toString().trim(),
            duration = binding.durationInput.text.toString().trim(),
            grade = binding.gradeDropdown.text.toString(),
            subject = binding.subjectInput.text.toString().trim(),
            totalMarks = binding.totalMarksInput.text.toString().toIntOrNull() ?: 0,
            passMarks = binding.passMarksInput.text.toString().toIntOrNull() ?: 0,
            venue = binding.venueInput.text.toString().trim(),
            invigilator = binding.invigilatorInput.text.toString().trim(),
            status = ExamStatus.SCHEDULED
        )

        examManager.addExam(exam) { success ->
            if (success) {
                Snackbar.make(binding.root, "Exam added successfully", Snackbar.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(binding.root, "Failed to add exam", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun getExamTypeFromDropdown(): ExamType {
        return when (binding.examTypeDropdown.text.toString()) {
            "Term Test" -> ExamType.TERM_TEST
            "A/L Results" -> ExamType.A_L_RESULTS
            "O/L Results" -> ExamType.O_L_RESULTS
            else -> ExamType.TERM_TEST
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
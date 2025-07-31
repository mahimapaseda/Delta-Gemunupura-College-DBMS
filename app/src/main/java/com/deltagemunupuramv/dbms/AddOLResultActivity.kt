package com.deltagemunupuramv.dbms

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.ActivityAddOlResultBinding
import com.deltagemunupuramv.dbms.manager.OLExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.android.material.snackbar.Snackbar
import java.util.UUID

class AddOLResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddOlResultBinding
    private lateinit var olExamManager: OLExamManager
    private var editingExamId: String? = null

    companion object {
        const val EXTRA_EXAM_ID = "exam_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOlResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupOLExamManager()
        setupDropdowns()
        setupButtons()
        loadExamForEditing()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupOLExamManager() {
        olExamManager = OLExamManager()
    }

    private fun setupDropdowns() {
        // Gender Dropdown
        val genders = arrayOf("Male", "Female")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        binding.genderDropdown.setAdapter(genderAdapter)

        // Medium Dropdown
        val mediums = arrayOf("Sinhala", "English")
        val mediumAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mediums)
        binding.mediumDropdown.setAdapter(mediumAdapter)

        // Attempt No Dropdown
        val attempts = arrayOf("1st Attempt", "2nd Attempt", "3rd Attempt")
        val attemptAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, attempts)
        binding.attemptNoDropdown.setAdapter(attemptAdapter)

        // Religion Dropdown (moved to Subject Results section) - A,B,C,S,W
        val religions = arrayOf("A", "B", "C", "S", "W")
        val religionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, religions)
        binding.religionDropdown.setAdapter(religionAdapter)

        // Subject Results Dropdowns (A,B,C,S,W)
        val grades = arrayOf("A", "B", "C", "S", "W")
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        
        binding.languageLiteratureDropdown.setAdapter(gradeAdapter)
        binding.englishDropdown.setAdapter(gradeAdapter)
        binding.scienceDropdown.setAdapter(gradeAdapter)
        binding.mathematicsDropdown.setAdapter(gradeAdapter)
        binding.historyDropdown.setAdapter(gradeAdapter)

        // Subject Groups Dropdowns
        val subjectGroups = arrayOf("A", "B", "C", "S", "W")
        val subjectGroupAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjectGroups)
        
        binding.firstSubjectGroupDropdown.setAdapter(subjectGroupAdapter)
        binding.secondSubjectGroupDropdown.setAdapter(subjectGroupAdapter)
        binding.thirdSubjectGroupDropdown.setAdapter(subjectGroupAdapter)
    }

    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            if (validateInputs()) {
                saveOLResult()
            }
        }
    }

    private fun loadExamForEditing() {
        editingExamId = intent.getStringExtra(EXTRA_EXAM_ID)
        if (editingExamId != null) {
            supportActionBar?.title = "Edit O/L Result"
            binding.saveButton.text = "Update"
            
            olExamManager.getOLExamById(editingExamId!!) { exam ->
                exam?.let { populateFields(it) }
            }
        } else {
            supportActionBar?.title = "Add O/L Result"
            binding.saveButton.text = "Save"
        }
    }

    private fun populateFields(exam: Exam) {
        binding.yearInput.setText(exam.examYear)
        binding.indexNoInput.setText(exam.indexNo)
        binding.fullNameInput.setText(exam.fullName)
        binding.nicNoInput.setText(exam.nicNo)
        binding.attemptNoDropdown.setText(exam.attemptNo, false)
        binding.genderDropdown.setText(exam.gender, false)
        binding.mediumDropdown.setText(exam.medium, false)
        binding.religionDropdown.setText(exam.religion, false)
        binding.languageLiteratureDropdown.setText(exam.languageLiterature, false)
        binding.englishDropdown.setText(exam.english, false)
        binding.scienceDropdown.setText(exam.science, false)
        binding.mathematicsDropdown.setText(exam.mathematics, false)
        binding.historyDropdown.setText(exam.history, false)
        binding.firstSubjectGroupDropdown.setText(exam.firstSubjectGroup, false)
        binding.secondSubjectGroupDropdown.setText(exam.secondSubjectGroup, false)
        binding.thirdSubjectGroupDropdown.setText(exam.thirdSubjectGroup, false)
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Required fields validation
        if (binding.yearInput.text.toString().isEmpty()) {
            binding.yearInput.error = "Year is required"
            isValid = false
        } else {
            val year = binding.yearInput.text.toString().toIntOrNull()
            if (year == null || year < 1900 || year > 2100) {
                binding.yearInput.error = "Please enter a valid 4-digit year"
                isValid = false
            }
        }

        if (binding.indexNoInput.text.toString().isEmpty()) {
            binding.indexNoInput.error = "Index No is required"
            isValid = false
        } else {
            val indexNo = binding.indexNoInput.text.toString().toIntOrNull()
            if (indexNo == null || indexNo <= 0) {
                binding.indexNoInput.error = "Please enter a valid index number"
                isValid = false
            }
        }

        if (binding.fullNameInput.text.toString().isEmpty()) {
            binding.fullNameInput.error = "Full Name is required"
            isValid = false
        }

        if (binding.nicNoInput.text.toString().isEmpty()) {
            binding.nicNoInput.error = "NIC No is required"
            isValid = false
        } else {
            val nicNo = binding.nicNoInput.text.toString().toLongOrNull()
            if (nicNo == null || nicNo <= 0) {
                binding.nicNoInput.error = "Please enter a valid NIC number"
                isValid = false
            }
        }

        if (binding.attemptNoDropdown.text.toString().isEmpty()) {
            binding.attemptNoDropdown.error = "Attempt No is required"
            isValid = false
        }

        if (binding.genderDropdown.text.toString().isEmpty()) {
            binding.genderDropdown.error = "Gender is required"
            isValid = false
        }

        if (binding.mediumDropdown.text.toString().isEmpty()) {
            binding.mediumDropdown.error = "Medium is required"
            isValid = false
        }

        if (binding.religionDropdown.text.toString().isEmpty()) {
            binding.religionDropdown.error = "Religion is required"
            isValid = false
        }

        return isValid
    }

    private fun saveOLResult() {
        val exam = Exam(
            id = editingExamId ?: UUID.randomUUID().toString(),
            examType = ExamType.O_L_RESULTS,
            title = "O/L Results ${binding.yearInput.text}",
            description = "Ordinary Level Examination Results for ${binding.yearInput.text}",
            examDate = "2024-01-10", // Default date for results
            startTime = "",
            endTime = "",
            duration = "",
            grade = "Grade 11",
            subject = "All Subjects",
            totalMarks = 0,
            passMarks = 0,
            venue = "School Office",
            invigilator = "",
            status = ExamStatus.COMPLETED,
            examYear = binding.yearInput.text.toString(),
            examMonth = "December",
            resultsPublished = true,
            resultsDate = "2024-01-10",
            // O/L specific fields
            indexNo = binding.indexNoInput.text.toString().trim(),
            fullName = binding.fullNameInput.text.toString().trim(),
            nicNo = binding.nicNoInput.text.toString().trim(),
            attemptNo = binding.attemptNoDropdown.text.toString(),
            gender = binding.genderDropdown.text.toString(),
            medium = binding.mediumDropdown.text.toString(),
            religion = binding.religionDropdown.text.toString(),
            languageLiterature = binding.languageLiteratureDropdown.text.toString(),
            english = binding.englishDropdown.text.toString(),
            science = binding.scienceDropdown.text.toString(),
            mathematics = binding.mathematicsDropdown.text.toString(),
            history = binding.historyDropdown.text.toString(),
            firstSubjectGroup = binding.firstSubjectGroupDropdown.text.toString().trim(),
            secondSubjectGroup = binding.secondSubjectGroupDropdown.text.toString().trim(),
            thirdSubjectGroup = binding.thirdSubjectGroupDropdown.text.toString().trim()
        )

        if (editingExamId != null) {
            // Update existing exam
            olExamManager.updateOLExam(exam) { success ->
                if (success) {
                    Snackbar.make(binding.root, "O/L result updated successfully", Snackbar.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Snackbar.make(binding.root, "Failed to update O/L result", Snackbar.LENGTH_LONG).show()
                }
            }
        } else {
            // Add new exam
            olExamManager.addOLExam(exam) { success ->
                if (success) {
                    Snackbar.make(binding.root, "O/L result added successfully", Snackbar.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Snackbar.make(binding.root, "Failed to add O/L result", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
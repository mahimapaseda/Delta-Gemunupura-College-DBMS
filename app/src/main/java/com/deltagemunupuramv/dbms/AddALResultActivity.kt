package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.ActivityAddAlResultBinding
import com.deltagemunupuramv.dbms.manager.ALExamManager
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamType
import com.google.android.material.snackbar.Snackbar

class AddALResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAlResultBinding
    private lateinit var alExamManager: ALExamManager
    private var isEditMode = false
    private var alExamId: String = ""

    companion object {
        const val EXTRA_AL_RESULT_ID = "extra_al_result_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupALExamManager()
        setupDropdowns()
        checkEditMode()
        setupSaveButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupALExamManager() {
        alExamManager = ALExamManager()
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

        // Subject Stream Dropdown
        val streams = arrayOf("Science", "Commerce", "Arts", "Technology")
        val streamAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, streams)
        binding.subjectStreamDropdown.setAdapter(streamAdapter)

        // Attempt No Dropdown
        val attempts = arrayOf("1st", "2nd", "3rd")
        val attemptAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, attempts)
        binding.attemptNoDropdown.setAdapter(attemptAdapter)

        // Grade Dropdowns for subjects (A,B,C,S,F)
        val grades = arrayOf("A", "B", "C", "S", "F")
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        
        binding.subject1GradeDropdown.setAdapter(gradeAdapter)
        binding.subject2GradeDropdown.setAdapter(gradeAdapter)
        binding.subject3GradeDropdown.setAdapter(gradeAdapter)
        binding.generalEnglishGradeDropdown.setAdapter(gradeAdapter)
    }

    private fun checkEditMode() {
        alExamId = intent.getStringExtra(EXTRA_AL_RESULT_ID) ?: ""
        isEditMode = alExamId.isNotEmpty()

        if (isEditMode) {
            supportActionBar?.title = "Edit A/L Result"
            loadALExam()
        } else {
            supportActionBar?.title = "Add A/L Result"
        }
    }

    private fun loadALExam() {
        alExamManager.getALExamById(alExamId) { alExam ->
            alExam?.let { exam ->
                populateFields(exam)
            }
        }
    }

    private fun populateFields(alExam: Exam) {
        binding.apply {
            indexNoInput.setText(alExam.indexNo)
            fullNameInput.setText(alExam.fullName)
            nicNoInput.setText(alExam.nicNo)
            setDropdownSelection(attemptNoDropdown, alExam.attemptNo)
            examYearInput.setText(alExam.examYear)
            
            // Set dropdown selections
            setDropdownSelection(genderDropdown, alExam.gender)
            setDropdownSelection(mediumDropdown, alExam.medium)
            setDropdownSelection(subjectStreamDropdown, alExam.subjectStream)
            
            subject1Input.setText(alExam.subjectNo1)
            setDropdownSelection(subject1GradeDropdown, alExam.subjectNo1Grade)
            
            subject2Input.setText(alExam.subjectNo2)
            setDropdownSelection(subject2GradeDropdown, alExam.subjectNo2Grade)
            
            subject3Input.setText(alExam.subjectNo3)
            setDropdownSelection(subject3GradeDropdown, alExam.subjectNo3Grade)
            
            averageZScoreInput.setText(alExam.averageZScore)
            districtRankInput.setText(alExam.districtRank)
            islandRankInput.setText(alExam.islandRank)
            
            setDropdownSelection(generalEnglishGradeDropdown, alExam.generalEnglishGrade)
            commonGeneralTestMarksInput.setText(alExam.commonGeneralTestMarks)
        }
    }

    private fun setDropdownSelection(dropdown: android.widget.AutoCompleteTextView, value: String) {
        dropdown.setText(value, false)
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateInputs()) {
                saveALExam()
            }
        }
        
        binding.cancelButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validateInputs(): Boolean {
        binding.apply {
            if (indexNoInput.text.isNullOrBlank()) {
                indexNoInput.error = "Index No is required"
                return false
            }
            if (fullNameInput.text.isNullOrBlank()) {
                fullNameInput.error = "Full Name is required"
                return false
            }
            if (nicNoInput.text.isNullOrBlank()) {
                nicNoInput.error = "NIC No is required"
                return false
            }
            
            // Validate Exam Year - must be exactly 4 digits and within reasonable range
            val examYear = examYearInput.text.toString().trim()
            if (examYear.isBlank()) {
                examYearInput.error = "Exam Year is required"
                return false
            }
            if (examYear.length != 4 || !examYear.matches(Regex("^\\d{4}$"))) {
                examYearInput.error = "Exam Year must be exactly 4 digits"
                return false
            }
            val yearValue = examYear.toIntOrNull()
            if (yearValue == null || yearValue < 2000 || yearValue > 2030) {
                examYearInput.error = "Exam Year must be between 2000 and 2030"
                return false
            }
            
            if (subject1Input.text.isNullOrBlank()) {
                subject1Input.error = "Subject 1 is required"
                return false
            }
            if (subject2Input.text.isNullOrBlank()) {
                subject2Input.error = "Subject 2 is required"
                return false
            }
            if (subject3Input.text.isNullOrBlank()) {
                subject3Input.error = "Subject 3 is required"
                return false
            }
        }
        return true
    }

    private fun saveALExam() {
        val alExam = Exam(
            id = alExamId,
            examType = ExamType.A_L_RESULTS,
            indexNo = binding.indexNoInput.text.toString().trim(),
            fullName = binding.fullNameInput.text.toString().trim(),
            nicNo = binding.nicNoInput.text.toString().trim(),
            attemptNo = binding.attemptNoDropdown.text.toString(),
            gender = binding.genderDropdown.text.toString(),
            medium = binding.mediumDropdown.text.toString(),
            subjectStream = binding.subjectStreamDropdown.text.toString(),
            subjectNo1 = binding.subject1Input.text.toString().trim(),
            subjectNo1Grade = binding.subject1GradeDropdown.text.toString(),
            subjectNo2 = binding.subject2Input.text.toString().trim(),
            subjectNo2Grade = binding.subject2GradeDropdown.text.toString(),
            subjectNo3 = binding.subject3Input.text.toString().trim(),
            subjectNo3Grade = binding.subject3GradeDropdown.text.toString(),
            averageZScore = binding.averageZScoreInput.text.toString().trim(),
            districtRank = binding.districtRankInput.text.toString().trim(),
            islandRank = binding.islandRankInput.text.toString().trim(),
            generalEnglishGrade = binding.generalEnglishGradeDropdown.text.toString(),
            commonGeneralTestMarks = binding.commonGeneralTestMarksInput.text.toString().trim(),
            examYear = binding.examYearInput.text.toString().trim()
        )

        if (isEditMode) {
            alExamManager.updateALExam(alExam) { success ->
                if (success) {
                    Snackbar.make(binding.root, "A/L result updated successfully", Snackbar.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Snackbar.make(binding.root, "Failed to update A/L result", Snackbar.LENGTH_LONG).show()
                }
            }
        } else {
            alExamManager.addALExam(alExam) { success ->
                if (success) {
                    Snackbar.make(binding.root, "A/L result added successfully", Snackbar.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Snackbar.make(binding.root, "Failed to add A/L result", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        alExamManager.cleanup()
    }
} 
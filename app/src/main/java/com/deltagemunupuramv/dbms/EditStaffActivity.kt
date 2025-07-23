package com.deltagemunupuramv.dbms

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffStatus
import com.deltagemunupuramv.dbms.model.StaffType
import com.google.firebase.firestore.FirebaseFirestore

class EditStaffActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var departmentEditText: EditText
    private lateinit var designationEditText: EditText
    private lateinit var qualificationsEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var emergencyContactEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var staffTypeRadioGroup: RadioGroup
    private lateinit var academicRadioButton: RadioButton
    private lateinit var nonAcademicRadioButton: RadioButton
    
    // Academic staff fields
    private lateinit var subjectsEditText: EditText
    private lateinit var teachingExperienceEditText: EditText
    private lateinit var researchPublicationsEditText: EditText
    
    // Non-academic staff fields
    private lateinit var roleEditText: EditText
    private lateinit var skillsEditText: EditText
    private lateinit var workExperienceEditText: EditText
    
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val staffCollection = db.collection("staff")
    private lateinit var currentStaff: Staff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_staff)

        currentStaff = intent.getParcelableExtra<Staff>("staff") ?: run {
            finish()
            return
        }

        setupViews()
        populateFields()
        setupListeners()
    }

    private fun setupViews() {
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        departmentEditText = findViewById(R.id.departmentEditText)
        designationEditText = findViewById(R.id.designationEditText)
        qualificationsEditText = findViewById(R.id.qualificationsEditText)
        addressEditText = findViewById(R.id.addressEditText)
        emergencyContactEditText = findViewById(R.id.emergencyContactEditText)
        statusSpinner = findViewById(R.id.statusSpinner)
        staffTypeRadioGroup = findViewById(R.id.staffTypeRadioGroup)
        academicRadioButton = findViewById(R.id.academicRadioButton)
        nonAcademicRadioButton = findViewById(R.id.nonAcademicRadioButton)
        
        // Academic staff fields
        subjectsEditText = findViewById(R.id.subjectsEditText)
        teachingExperienceEditText = findViewById(R.id.teachingExperienceEditText)
        researchPublicationsEditText = findViewById(R.id.researchPublicationsEditText)
        
        // Non-academic staff fields
        roleEditText = findViewById(R.id.roleEditText)
        skillsEditText = findViewById(R.id.skillsEditText)
        workExperienceEditText = findViewById(R.id.workExperienceEditText)
        
        saveButton = findViewById(R.id.saveButton)
    }

    private fun populateFields() {
        nameEditText.setText(currentStaff.name)
        emailEditText.setText(currentStaff.email)
        phoneEditText.setText(currentStaff.phone)
        departmentEditText.setText(currentStaff.department)
        designationEditText.setText(currentStaff.designation)
        qualificationsEditText.setText(currentStaff.qualifications)
        addressEditText.setText(currentStaff.address)
        emergencyContactEditText.setText(currentStaff.emergencyContact)

        // Set staff type
        when (currentStaff.staffType) {
            StaffType.ACADEMIC -> academicRadioButton.isChecked = true
            StaffType.NON_ACADEMIC -> nonAcademicRadioButton.isChecked = true
        }

        // Set status
        val statusArray = resources.getStringArray(R.array.staff_status_array)
        val statusIndex = StaffStatus.values().indexOf(currentStaff.status)
        if (statusIndex >= 0) {
            statusSpinner.setSelection(statusIndex)
        }

        // Populate type-specific fields
        if (currentStaff.staffType == StaffType.ACADEMIC) {
            subjectsEditText.setText(currentStaff.subjects.joinToString(", "))
            teachingExperienceEditText.setText(currentStaff.teachingExperience.toString())
            researchPublicationsEditText.setText(currentStaff.researchPublications.toString())
            showAcademicFields()
        } else {
            roleEditText.setText(currentStaff.role)
            skillsEditText.setText(currentStaff.skills.joinToString(", "))
            workExperienceEditText.setText(currentStaff.workExperience.toString())
            showNonAcademicFields()
        }
    }

    private fun setupListeners() {
        staffTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.academicRadioButton -> showAcademicFields()
                R.id.nonAcademicRadioButton -> showNonAcademicFields()
            }
        }

        saveButton.setOnClickListener {
            if (validateInputs()) {
                updateStaff()
            }
        }
    }

    private fun showAcademicFields() {
        findViewById<LinearLayout>(R.id.academicFieldsLayout).visibility = android.view.View.VISIBLE
        findViewById<LinearLayout>(R.id.nonAcademicFieldsLayout).visibility = android.view.View.GONE
    }

    private fun showNonAcademicFields() {
        findViewById<LinearLayout>(R.id.academicFieldsLayout).visibility = android.view.View.GONE
        findViewById<LinearLayout>(R.id.nonAcademicFieldsLayout).visibility = android.view.View.VISIBLE
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (nameEditText.text.isNullOrBlank()) {
            nameEditText.error = "Name is required"
            isValid = false
        }
        if (emailEditText.text.isNullOrBlank()) {
            emailEditText.error = "Email is required"
            isValid = false
        }
        if (phoneEditText.text.isNullOrBlank()) {
            phoneEditText.error = "Phone is required"
            isValid = false
        }
        if (departmentEditText.text.isNullOrBlank()) {
            departmentEditText.error = "Department is required"
            isValid = false
        }
        if (designationEditText.text.isNullOrBlank()) {
            designationEditText.error = "Designation is required"
            isValid = false
        }

        return isValid
    }

    private fun updateStaff() {
        val staffType = if (academicRadioButton.isChecked) StaffType.ACADEMIC else StaffType.NON_ACADEMIC
        val status = StaffStatus.values()[statusSpinner.selectedItemPosition]

        val updatedStaff = currentStaff.copy(
            name = nameEditText.text.toString(),
            email = emailEditText.text.toString(),
            phone = phoneEditText.text.toString(),
            department = departmentEditText.text.toString(),
            designation = designationEditText.text.toString(),
            staffType = staffType,
            qualifications = qualificationsEditText.text.toString(),
            address = addressEditText.text.toString(),
            emergencyContact = emergencyContactEditText.text.toString(),
            status = status,
            subjects = if (staffType == StaffType.ACADEMIC) {
                subjectsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
            } else {
                emptyList()
            },
            teachingExperience = if (staffType == StaffType.ACADEMIC) {
                teachingExperienceEditText.text.toString().toIntOrNull() ?: 0
            } else {
                0
            },
            researchPublications = if (staffType == StaffType.ACADEMIC) {
                researchPublicationsEditText.text.toString().toIntOrNull() ?: 0
            } else {
                0
            },
            role = if (staffType == StaffType.NON_ACADEMIC) {
                roleEditText.text.toString()
            } else {
                ""
            },
            skills = if (staffType == StaffType.NON_ACADEMIC) {
                skillsEditText.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
            } else {
                emptyList()
            },
            workExperience = if (staffType == StaffType.NON_ACADEMIC) {
                workExperienceEditText.text.toString().toIntOrNull() ?: 0
            } else {
                0
            }
        )

        staffCollection.document(updatedStaff.id)
            .set(updatedStaff)
            .addOnSuccessListener {
                Toast.makeText(this, "Staff updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating staff: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
} 
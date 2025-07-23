package com.deltagemunupuramv.dbms

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.model.Staff
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddStaffActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var departmentEditText: EditText
    private lateinit var designationEditText: EditText
    private lateinit var qualificationsEditText: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val staffCollection = db.collection("staff")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_staff)

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        departmentEditText = findViewById(R.id.departmentEditText)
        designationEditText = findViewById(R.id.designationEditText)
        qualificationsEditText = findViewById(R.id.qualificationsEditText)
        saveButton = findViewById(R.id.saveButton)
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveStaff()
            }
        }
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

    private fun saveStaff() {
        val staff = Staff(
            id = UUID.randomUUID().toString(),
            name = nameEditText.text.toString(),
            email = emailEditText.text.toString(),
            phone = phoneEditText.text.toString(),
            department = departmentEditText.text.toString(),
            designation = designationEditText.text.toString(),
            qualifications = qualificationsEditText.text.toString(),
            dateJoined = System.currentTimeMillis().toString()
        )

        staffCollection.document(staff.id)
            .set(staff)
            .addOnSuccessListener {
                Toast.makeText(this, "Staff added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding staff: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
} 
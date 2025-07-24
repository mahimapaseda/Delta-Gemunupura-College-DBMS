package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.ActivityViewStudentBinding
import com.deltagemunupuramv.dbms.model.Student
import com.deltagemunupuramv.dbms.manager.StudentManager
import com.google.android.material.snackbar.Snackbar
import android.widget.Toast
import com.bumptech.glide.Glide

class ViewStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewStudentBinding
    private lateinit var studentManager: StudentManager
    private lateinit var student: Student

    companion object {
        const val EXTRA_STUDENT = "extra_student"
        private const val EDIT_STUDENT_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = intent.getParcelableExtra(EXTRA_STUDENT)
            ?: throw IllegalArgumentException("Student data is required")

        studentManager = StudentManager()

        setupToolbar()
        populateStudentData()
        setupFabButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Student Details"
        }
    }

    private fun populateStudentData() {
        // Load student image
        if (student.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(student.imageUrl)
                .placeholder(R.color.maroon_light)
                .into(binding.studentAvatar)
        } else {
            // Set avatar background based on gender
            val avatarBg = if (student.gender.equals("Male", true)) {
                getColor(R.color.maroon_primary)
            } else {
                getColor(R.color.maroon_light)
            }
            binding.studentAvatar.setBackgroundColor(avatarBg)

            // Set initial letter in avatar
            val initial = student.fullName.firstOrNull()?.toString()?.uppercase() ?: "?"
            // You can set this as a text overlay if needed
        }

        // Basic Information
        binding.studentName.text = student.fullName
        binding.studentIndex.text = "Index: ${student.indexNumber}"
        binding.gradeChip.text = student.grade
        binding.genderChip.text = student.gender

        // Add class/stream chip
        val classOrStreamLabel = if (student.isStream) "Stream: " else "Class: "
        binding.classChip.text = classOrStreamLabel + student.class_
        binding.classChip.setChipBackgroundColorResource(
            if (student.isStream) R.color.gold_accent else R.color.maroon_light
        )

        // Personal Information
        binding.nameWithInitials.text = student.nameWithInitials.ifEmpty { "Not provided" }
        binding.dateOfBirth.text = student.dateOfBirth.ifEmpty { "Not provided" }
        binding.nicNumber.text = student.nicNumber.ifEmpty { "Not provided" }
        binding.religion.text = student.religion.ifEmpty { "Not provided" }

        // Contact Information
        binding.address.text = student.address.ifEmpty { "Not provided" }
        binding.phoneNumber.text = student.phone.ifEmpty { "Not provided" }
        binding.whatsappNumber.text = student.whatsapp.ifEmpty { "Not provided" }
        binding.emailAddress.text = student.email.ifEmpty { "Not provided" }

        // Academic Information
        binding.admissionDate.text = student.admissionDate.ifEmpty { "Not provided" }
        binding.previousSchools.text = student.previousSchools.ifEmpty { "Not provided" }
        binding.medium.text = student.medium.ifEmpty { "Not provided" }
        binding.subjects.text = student.subjects.ifEmpty { "Not provided" }

        // Guardian Information
        binding.guardianName.text = student.guardianName.ifEmpty { "Not provided" }
        binding.guardianContact.text = student.guardianContact.ifEmpty { "Not provided" }
        binding.guardianNic.text = student.guardianNic.ifEmpty { "Not provided" }
        binding.guardianOccupation.text = student.guardianOccupation.ifEmpty { "Not provided" }

        // Additional Information
        if (student.siblings.isNotEmpty()) {
            binding.siblings.text = student.siblings
            binding.siblingsSection.visibility = View.VISIBLE
        } else {
            binding.siblingsSection.visibility = View.GONE
        }

        if (student.disabilities.isNotEmpty()) {
            binding.disabilities.text = student.disabilities
            binding.disabilitiesSection.visibility = View.VISIBLE
        } else {
            binding.disabilities.text = "None"
            binding.disabilitiesSection.visibility = View.VISIBLE
        }

        // Make avatar clickable to view full image
        if (student.imageUrl.isNotEmpty()) {
            binding.studentAvatar.setOnClickListener {
                showFullImage()
            }
        }
    }

    private fun showFullImage() {
        // You can implement a full-screen image viewer here
        Toast.makeText(this, "Opening full image...", Toast.LENGTH_SHORT).show()
    }

    private fun setupFabButtons() {
        // Edit button
        binding.editFab.setOnClickListener {
            val intent = Intent(this, EditStudentActivity::class.java).apply {
                putExtra(EditStudentActivity.EXTRA_STUDENT, student)
            }
            startActivityForResult(intent, EDIT_STUDENT_REQUEST)
        }

        // Delete button
        binding.deleteFab.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.fullName}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteStudent()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStudent() {
        // Disable FABs during deletion
        binding.editFab.isEnabled = false
        binding.deleteFab.isEnabled = false

        studentManager.deleteStudent(student.id) { success ->
            if (!isFinishing) {
                binding.editFab.isEnabled = true
                binding.deleteFab.isEnabled = true

                if (success) {
                    Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Snackbar.make(binding.root, "Failed to delete student", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_STUDENT_REQUEST && resultCode == RESULT_OK) {
            // Refresh student data after edit
            setResult(RESULT_OK)
            finish() // Return to previous activity to refresh the list
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 
package com.deltagemunupuramv.dbms

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.deltagemunupuramv.dbms.R
import com.deltagemunupuramv.dbms.databinding.ActivityAddStudentBinding
import com.deltagemunupuramv.dbms.model.Student
import com.deltagemunupuramv.dbms.manager.StudentManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditStudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStudentBinding
    private lateinit var studentManager: StudentManager
    private lateinit var student: Student
    private var selectedImageUri: Uri? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private var currentPhotoPath: String? = null

    companion object {
        const val EXTRA_STUDENT = "extra_student"
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAKE_PHOTO_REQUEST = 2
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = intent.getParcelableExtra(EXTRA_STUDENT)
            ?: throw IllegalArgumentException("Student data is required")

        studentManager = StudentManager()

        setupToolbar()
        setupDropdowns()
        setupDatePickers()
        setupImagePicker()
        populateStudentData()
        setupSaveButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Edit Student"
        }
    }

    private fun setupDropdowns() {
        // Setup Grade dropdown
        val grades = arrayOf(
            "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10",
            "Grade 12", "Grade 13"
        )
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        binding.gradeDropdown.setAdapter(gradeAdapter)

        // Setup Class/Stream dropdown based on grade selection
        binding.gradeDropdown.setOnItemClickListener { _, _, _, _ ->
            setupClassOrStreamDropdown(binding.gradeDropdown.text.toString())
        }

        // Setup Medium dropdown
        val mediums = arrayOf("Sinhala", "English", "Tamil")
        val mediumAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mediums)
        binding.mediumDropdown.setAdapter(mediumAdapter)

        // Setup Gender dropdown
        val genders = arrayOf("Male", "Female")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        binding.genderDropdown.setAdapter(genderAdapter)

        // Initialize class/stream dropdown with current grade
        setupClassOrStreamDropdown(student.grade)
    }

    private fun setupClassOrStreamDropdown(selectedGrade: String) {
        val isStream = Student.isStreamGrade(selectedGrade)
        val options = Student.getAvailableClassesOrStreams(selectedGrade)

        // Update hint and helper text
        binding.classLayout.hint = "Select ${Student.getClassOrStreamLabel(selectedGrade)}"
        binding.classLayout.helperText = if (isStream) {
            "Select stream for A/L student"
        } else {
            "Select class A or B"
        }

        // Setup adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        binding.classDropdown.setAdapter(adapter)

        // Only set text if it's a valid option for the current grade
        if (options.contains(student.class_)) {
            binding.classDropdown.setText(student.class_, false)
        } else {
            binding.classDropdown.text = null
        }
    }

    private fun populateStudentData() {
        // Basic Information
        binding.fullNameInput.setText(student.fullName)
        binding.nameWithInitialsInput.setText(student.nameWithInitials)
        binding.indexNumberInput.setText(student.indexNumber)
        binding.dobInput.setText(student.dateOfBirth)
        binding.nicInput.setText(student.nicNumber)
        binding.gradeDropdown.setText(student.grade, false)
        binding.genderDropdown.setText(student.gender, false)
        binding.religionInput.setText(student.religion)

        // Contact Information
        binding.addressInput.setText(student.address)
        binding.phoneInput.setText(student.phone)
        binding.whatsappInput.setText(student.whatsapp)
        binding.emailInput.setText(student.email)

        // Academic Information
        binding.admissionDateInput.setText(student.admissionDate)
        binding.previousSchoolsInput.setText(student.previousSchools)
        binding.mediumDropdown.setText(student.medium, false)
        binding.subjectsInput.setText(student.subjects)

        // Guardian Information
        binding.guardianNameInput.setText(student.guardianName)
        binding.guardianContactInput.setText(student.guardianContact)
        binding.guardianNicInput.setText(student.guardianNic)
        binding.guardianOccupationInput.setText(student.guardianOccupation)

        // Additional Information
        binding.siblingsInput.setText(student.siblings)
        binding.disabilitiesInput.setText(student.disabilities)
    }

    private fun setupSaveButton() {
        binding.saveButton.apply {
            text = "Update Student"
            setOnClickListener {
                if (validateInputs()) {
                    showLoading(true)
                    updateStudent()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate Full Name
        if (binding.fullNameInput.text.isNullOrBlank()) {
            binding.fullNameLayout.error = "Full name is required"
            isValid = false
        } else {
            binding.fullNameLayout.error = null
        }

        // Validate Name with Initials
        if (binding.nameWithInitialsInput.text.isNullOrBlank()) {
            binding.nameWithInitialsLayout.error = "Name with initials is required"
            isValid = false
        } else {
            binding.nameWithInitialsLayout.error = null
        }

        // Validate Index Number
        if (binding.indexNumberInput.text.isNullOrBlank()) {
            binding.indexNumberLayout.error = "Index number is required"
            isValid = false
        } else {
            binding.indexNumberLayout.error = null
        }

        // Validate Grade
        if (binding.gradeDropdown.text.isNullOrBlank()) {
            binding.gradeLayout.error = "Grade is required"
            isValid = false
        } else {
            binding.gradeLayout.error = null
        }

        // Validate Class/Stream
        if (binding.classDropdown.text.isNullOrBlank()) {
            val selectedGrade = binding.gradeDropdown.text.toString()
            binding.classLayout.error = if (Student.isStreamGrade(selectedGrade)) {
                "Stream is required"
            } else {
                "Class is required"
            }
            isValid = false
        } else {
            // Validate class/stream value
            val selectedGrade = binding.gradeDropdown.text.toString()
            val selectedClassOrStream = binding.classDropdown.text.toString()
            val validOptions = Student.getAvailableClassesOrStreams(selectedGrade)

            if (!validOptions.contains(selectedClassOrStream)) {
                binding.classLayout.error = if (Student.isStreamGrade(selectedGrade)) {
                    "Invalid stream selected"
                } else {
                    "Invalid class selected"
                }
                isValid = false
            } else {
                binding.classLayout.error = null
            }
        }

        // Validate Medium
        if (binding.mediumDropdown.text.isNullOrBlank()) {
            binding.mediumLayout.error = "Medium is required"
            isValid = false
        } else {
            binding.mediumLayout.error = null
        }

        // Validate Gender
        if (binding.genderDropdown.text.isNullOrBlank()) {
            binding.genderLayout.error = "Gender is required"
            isValid = false
        } else {
            binding.genderLayout.error = null
        }

        return isValid
    }

    private fun setupImagePicker() {
        // Load existing image if available
        if (student.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(student.imageUrl)
                .placeholder(R.color.maroon_light)
                .into(binding.studentImage)
        }

        binding.pickImageButton.setOnClickListener {
            showImagePickerDialog()
        }

        binding.studentImage.setOnClickListener {
            binding.pickImageButton.performClick()
        }
    }

    private fun showImagePickerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_picker, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<MaterialButton>(R.id.cameraButton).setOnClickListener {
            dialog.dismiss()
            checkCameraPermissionAndOpen()
        }

        dialogView.findViewById<MaterialButton>(R.id.galleryButton).setOnClickListener {
            dialog.dismiss()
            openGallery()
        }

        dialog.show()
    }

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            
            // Check if camera app is available
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Error creating image file: ${ex.message}", Toast.LENGTH_LONG).show()
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${packageName}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    
                    // Grant URI permission to camera app
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    
                    startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST)
                }
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening camera: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        
        // Create the directory if it doesn't exist
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }
        
        return File.createTempFile(
            "STUDENT_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        binding.studentImage.setImageURI(uri)
                    }
                }
                TAKE_PHOTO_REQUEST -> {
                    File(currentPhotoPath).let { file ->
                        selectedImageUri = FileProvider.getUriForFile(
                            this,
                            "${packageName}.provider",
                            file
                        )
                        binding.studentImage.setImageURI(selectedImageUri)
                    }
                }
            }
        }
    }

    private fun uploadImage(callback: (String?) -> Unit) {
        if (selectedImageUri == null) {
            // If no new image selected, return the existing URL
            callback(student.imageUrl)
            return
        }

        val imageRef = storageRef.child("student_images/${student.id}.jpg")

        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                callback(student.imageUrl) // Return existing URL on failure
            }
    }

    private fun updateStudent() {
        if (!validateInputs()) {
            return
        }

        showLoading(true)

        // First upload the image if a new one was selected
        uploadImage { imageUrl ->
            val updatedStudent = student.copy(
                fullName = binding.fullNameInput.text.toString().trim(),
                nameWithInitials = binding.nameWithInitialsInput.text.toString().trim(),
                indexNumber = binding.indexNumberInput.text.toString().trim(),
                dateOfBirth = binding.dobInput.text.toString().trim(),
                nicNumber = binding.nicInput.text.toString().trim(),
                grade = binding.gradeDropdown.text.toString().trim(),
                class_ = binding.classDropdown.text.toString().trim(),
                isStream = Student.isStreamGrade(binding.gradeDropdown.text.toString().trim()),
                gender = binding.genderDropdown.text.toString().trim(),
                religion = binding.religionInput.text.toString().trim(),
                address = binding.addressInput.text.toString().trim(),
                phone = binding.phoneInput.text.toString().trim(),
                whatsapp = binding.whatsappInput.text.toString().trim(),
                email = binding.emailInput.text.toString().trim(),
                admissionDate = binding.admissionDateInput.text.toString().trim(),
                previousSchools = binding.previousSchoolsInput.text.toString().trim(),
                medium = binding.mediumDropdown.text.toString().trim(),
                subjects = binding.subjectsInput.text.toString().trim(),
                guardianName = binding.guardianNameInput.text.toString().trim(),
                guardianContact = binding.guardianContactInput.text.toString().trim(),
                guardianNic = binding.guardianNicInput.text.toString().trim(),
                guardianOccupation = binding.guardianOccupationInput.text.toString().trim(),
                siblings = binding.siblingsInput.text.toString().trim(),
                disabilities = binding.disabilitiesInput.text.toString().trim(),
                imageUrl = imageUrl ?: "",
                updatedAt = System.currentTimeMillis()
            )

            studentManager.updateStudent(updatedStudent) { success ->
                if (!isFinishing) {
                    showLoading(false)
                    if (success) {
                        Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to update student", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupDatePickers() {
        // Date of Birth picker
        binding.dobInput.setOnClickListener {
            showDatePicker(Calendar.getInstance()) { date ->
                binding.dobInput.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date.time))
            }
        }

        // Admission Date picker
        binding.admissionDateInput.setOnClickListener {
            showDatePicker(Calendar.getInstance()) { date ->
                binding.admissionDateInput.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date.time))
            }
        }
    }

    private fun showDatePicker(calendar: Calendar, onDateSelected: (Calendar) -> Unit) {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showLoading(show: Boolean) {
        binding.saveButton.isEnabled = !show
        binding.saveButton.text = if (show) "Updating..." else "Update Student"
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
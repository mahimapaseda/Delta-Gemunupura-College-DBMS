package com.deltagemunupuramv.dbms

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.deltagemunupuramv.dbms.manager.StaffManager
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffStatus
import com.deltagemunupuramv.dbms.model.StaffType
import com.deltagemunupuramv.dbms.util.StaffUserConverter
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditStaffActivity : AppCompatActivity() {
    
    private lateinit var staffManager: StaffManager
    private lateinit var currentStaff: Staff
    private var selectedImageUri: Uri? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private var currentPhotoPath: String? = null
    
    // UI Components
    private lateinit var profileImageView: ImageView
    private lateinit var cameraButton: MaterialButton
    private lateinit var galleryButton: MaterialButton
    private lateinit var saveButton: ExtendedFloatingActionButton
    
    // Basic Information
    private lateinit var fullNameInput: TextInputEditText
    private lateinit var nameWithInitialsInput: TextInputEditText
    private lateinit var nicInput: TextInputEditText
    private lateinit var registrationNumberInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var addressInput: TextInputEditText
    
    // Personal Details
    private lateinit var dobInput: TextInputEditText
    private lateinit var genderDropdown: AutoCompleteTextView
    private lateinit var maritalStatusDropdown: AutoCompleteTextView
    private lateinit var spouseNameInput: TextInputEditText
    private lateinit var emergencyContactNameInput: TextInputEditText
    private lateinit var emergencyContactPhoneInput: TextInputEditText
    
    // Employment Details
    private lateinit var staffClassificationDropdown: AutoCompleteTextView
    private lateinit var appointedSubjectInput: TextInputEditText
    private lateinit var firstAppointmentInput: TextInputEditText
    private lateinit var schoolAppointmentInput: TextInputEditText
    private lateinit var statusDropdown: AutoCompleteTextView
    
    // Additional Information
    private lateinit var previouslyServedSchoolsInput: TextInputEditText
    private lateinit var educationalQualificationsInput: TextInputEditText
    private lateinit var professionalQualificationsInput: TextInputEditText
    private lateinit var subjectsTaughtInput: TextInputEditText
    private lateinit var gradesTaughtInput: TextInputEditText

    companion object {
        const val EXTRA_STAFF = "staff"
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAKE_PHOTO_REQUEST = 2
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_staff)
        
        currentStaff = intent.getParcelableExtra<Staff>(EXTRA_STAFF) ?: run {
            Toast.makeText(this, "Error loading staff data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        staffManager = StaffManager()
        
        initializeViews()
        setupToolbar()
        setupDropdowns()
        setupDatePickers()
        setupImagePicker()
        populateStaffData()
        setupSaveButton()
    }
    
    private fun initializeViews() {
        // Profile section
        profileImageView = findViewById(R.id.profileImageView)
        cameraButton = findViewById(R.id.cameraButton)
        galleryButton = findViewById(R.id.galleryButton)
        saveButton = findViewById(R.id.saveButton)
        
        // Basic Information
        fullNameInput = findViewById(R.id.fullNameInput)
        nameWithInitialsInput = findViewById(R.id.nameWithInitialsInput)
        nicInput = findViewById(R.id.nicInput)
        registrationNumberInput = findViewById(R.id.registrationNumberInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        passwordInput = findViewById(R.id.passwordInput)
        addressInput = findViewById(R.id.addressInput)
        
        // Personal Details
        dobInput = findViewById(R.id.dobInput)
        genderDropdown = findViewById(R.id.genderDropdown)
        maritalStatusDropdown = findViewById(R.id.maritalStatusDropdown)
        spouseNameInput = findViewById(R.id.spouseNameInput)
        emergencyContactNameInput = findViewById(R.id.emergencyContactNameInput)
        emergencyContactPhoneInput = findViewById(R.id.emergencyContactPhoneInput)
        
        // Employment Details
        staffClassificationDropdown = findViewById(R.id.staffClassificationDropdown)
        appointedSubjectInput = findViewById(R.id.appointedSubjectInput)
        firstAppointmentInput = findViewById(R.id.firstAppointmentInput)
        schoolAppointmentInput = findViewById(R.id.schoolAppointmentInput)
        statusDropdown = findViewById(R.id.statusDropdown)
        
        // Additional Information
        previouslyServedSchoolsInput = findViewById(R.id.previouslyServedSchoolsInput)
        educationalQualificationsInput = findViewById(R.id.educationalQualificationsInput)
        professionalQualificationsInput = findViewById(R.id.professionalQualificationsInput)
        subjectsTaughtInput = findViewById(R.id.subjectsTaughtInput)
        gradesTaughtInput = findViewById(R.id.gradesTaughtInput)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Edit Staff Member"
        }
    }
    
    private fun setupDropdowns() {
        // Gender dropdown
        val genders = arrayOf("Male", "Female")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        genderDropdown.setAdapter(genderAdapter)
        
        // Marital Status dropdown
        val maritalStatuses = arrayOf("Single", "Married", "Divorced", "Widowed")
        val maritalStatusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, maritalStatuses)
        maritalStatusDropdown.setAdapter(maritalStatusAdapter)
        
        // Staff Classification dropdown
        val classifications = arrayOf(
            "Non-Academic Staff (Partial Access)",
            "Principal (Full Access)",
            "Data Officer (Full Access)",
            "Technical Officer (Full Access)",
            "Academic Staff (Partial Access)"
        )
        val classificationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classifications)
        staffClassificationDropdown.setAdapter(classificationAdapter)
        
        // Status dropdown
        val statuses = arrayOf("Active", "Inactive", "On Leave")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statuses)
        statusDropdown.setAdapter(statusAdapter)
    }
    
    private fun setupDatePickers() {
        dobInput.setOnClickListener { showDatePicker(dobInput) }
        firstAppointmentInput.setOnClickListener { showDatePicker(firstAppointmentInput) }
        schoolAppointmentInput.setOnClickListener { showDatePicker(schoolAppointmentInput) }
    }
    
    private fun showDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            editText.setText(date)
        }, year, month, day).show()
    }
    
    private fun setupImagePicker() {
        cameraButton.setOnClickListener {
            if (checkCameraPermission()) {
                takePhoto()
            } else {
                requestCameraPermission()
            }
        }
        
        galleryButton.setOnClickListener {
            openGallery()
        }
    }
    
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
    }
    
    private fun takePhoto() {
        val photoFile = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show()
            return
        }
        
        val photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        selectedImageUri = photoUri
        
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST)
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "STAFF_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    
    private fun populateStaffData() {
        // Load profile image
        if (currentStaff.photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(currentStaff.photoUrl)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .circleCrop()
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.ic_person_placeholder)
        }
        
        // Basic Information
        fullNameInput.setText(currentStaff.fullName)
        nameWithInitialsInput.setText(currentStaff.nameWithInitials)
        nicInput.setText(currentStaff.nicNumber)
        registrationNumberInput.setText(currentStaff.registrationNumber)
        emailInput.setText(currentStaff.email)
        phoneInput.setText(currentStaff.phoneNumber)
        passwordInput.setText(currentStaff.password)
        addressInput.setText(currentStaff.personalAddress)
        
        // Personal Details
        dobInput.setText(currentStaff.dateOfBirth)
        genderDropdown.setText(currentStaff.gender, false)
        maritalStatusDropdown.setText(currentStaff.maritalStatus, false)
        spouseNameInput.setText(currentStaff.spouseName)
        emergencyContactNameInput.setText(currentStaff.emergencyContactName)
        emergencyContactPhoneInput.setText(currentStaff.emergencyContactPhone)
        
        // Employment Details
        val classification = when {
            currentStaff.appointedSubject.contains("Principal", ignoreCase = true) -> "Principal (Full Access)"
            currentStaff.appointedSubject.contains("Data Officer", ignoreCase = true) -> "Data Officer (Full Access)"
            currentStaff.appointedSubject.contains("Technical Officer", ignoreCase = true) -> "Technical Officer (Full Access)"
            currentStaff.staffType == StaffType.ACADEMIC -> "Academic Staff (Partial Access)"
            else -> "Non-Academic Staff (Partial Access)"
        }
        staffClassificationDropdown.setText(classification, false)
        
        appointedSubjectInput.setText(currentStaff.appointedSubject)
        firstAppointmentInput.setText(currentStaff.dateOfFirstAppointment)
        schoolAppointmentInput.setText(currentStaff.dateOfAppointmentToSchool)
        
        val status = when (currentStaff.status) {
            StaffStatus.ACTIVE -> "Active"
            StaffStatus.INACTIVE -> "Inactive"
            StaffStatus.ON_LEAVE -> "On Leave"
        }
        statusDropdown.setText(status, false)
        
        // Additional Information
        previouslyServedSchoolsInput.setText(currentStaff.previouslyServedSchools.joinToString(", "))
        educationalQualificationsInput.setText(currentStaff.educationalQualifications.joinToString(", "))
        professionalQualificationsInput.setText(currentStaff.professionalQualifications.joinToString(", "))
        subjectsTaughtInput.setText(currentStaff.subjectsTaught.joinToString(", "))
        gradesTaughtInput.setText(currentStaff.gradesTaught.joinToString(", "))
    }
    
    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            if (validateInputs()) {
                showLoading(true)
                updateStaff()
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Validate required fields
        if (fullNameInput.text.isNullOrBlank()) {
            findViewById<TextInputLayout>(R.id.fullNameLayout).error = "Full name is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.fullNameLayout).error = null
        }
        
        if (nameWithInitialsInput.text.isNullOrBlank()) {
            findViewById<TextInputLayout>(R.id.nameWithInitialsLayout).error = "Name with initials is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.nameWithInitialsLayout).error = null
        }
        
        if (nicInput.text.isNullOrBlank()) {
            findViewById<TextInputLayout>(R.id.nicLayout).error = "NIC number is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.nicLayout).error = null
        }
        
        if (registrationNumberInput.text.isNullOrBlank()) {
            findViewById<TextInputLayout>(R.id.registrationNumberLayout).error = "Registration number is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.registrationNumberLayout).error = null
        }
        
        if (emailInput.text.isNullOrBlank()) {
            findViewById<TextInputLayout>(R.id.emailLayout).error = "Email is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.emailLayout).error = null
        }
        
        if (phoneInput.text.isNullOrBlank()) {
            findViewById<TextInputLayout>(R.id.phoneLayout).error = "Phone number is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.phoneLayout).error = null
        }
        
        if (passwordInput.text.isNullOrBlank()) {
            findViewById<TextInputLayout>(R.id.passwordLayout).error = "Password is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.passwordLayout).error = null
        }
        
        // Validate staff classification and appointed subject
        val classification = staffClassificationDropdown.text.toString()
        val appointedSubject = appointedSubjectInput.text.toString()
        
        if ((classification.contains("Academic Staff") || classification.contains("Non-Academic Staff")) && 
            appointedSubject.isBlank()) {
            findViewById<TextInputLayout>(R.id.appointedSubjectLayout).error = "Appointed subject is required for this classification"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.appointedSubjectLayout).error = null
        }
        
        return isValid
    }
    
    private fun updateStaff() {
        // Get selected staff classification and determine type and appointed subject
        val staffClassification = staffClassificationDropdown.text.toString()
        val (staffType, appointedSubject) = when {
            staffClassification.contains("Principal") -> {
                StaffType.ACADEMIC to "Principal"
            }
            staffClassification.contains("Data Officer") -> {
                StaffType.NON_ACADEMIC to "Data Officer"
            }
            staffClassification.contains("Technical Officer") -> {
                StaffType.NON_ACADEMIC to "Technical Officer"
            }
            staffClassification.contains("Academic Staff") -> {
                StaffType.ACADEMIC to appointedSubjectInput.text.toString()
            }
            staffClassification.contains("Non-Academic Staff") -> {
                StaffType.NON_ACADEMIC to appointedSubjectInput.text.toString()
            }
            else -> {
                StaffType.ACADEMIC to appointedSubjectInput.text.toString()
            }
        }
        
        val updatedStaff = currentStaff.copy(
            // Basic Information
            fullName = fullNameInput.text.toString(),
            nameWithInitials = nameWithInitialsInput.text.toString(),
            nicNumber = nicInput.text.toString(),
            registrationNumber = registrationNumberInput.text.toString(),
            email = emailInput.text.toString(),
            password = passwordInput.text.toString(),
            phoneNumber = phoneInput.text.toString(),
            personalAddress = addressInput.text.toString(),
            
            // Personal Details
            dateOfBirth = dobInput.text.toString(),
            gender = genderDropdown.text.toString(),
            maritalStatus = maritalStatusDropdown.text.toString(),
            spouseName = spouseNameInput.text.toString(),
            emergencyContactName = emergencyContactNameInput.text.toString(),
            emergencyContactPhone = emergencyContactPhoneInput.text.toString(),
            
            // Employment Details
            dateOfFirstAppointment = firstAppointmentInput.text.toString(),
            dateOfAppointmentToSchool = schoolAppointmentInput.text.toString(),
            previouslyServedSchools = previouslyServedSchoolsInput.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            educationalQualifications = educationalQualificationsInput.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            professionalQualifications = professionalQualificationsInput.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            
            // Teaching Details
            appointedSubject = appointedSubject,
            subjectsTaught = subjectsTaughtInput.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            gradesTaught = gradesTaughtInput.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() },
            
            // Staff Classification
            staffType = staffType,
            status = when (statusDropdown.text.toString()) {
                "Active" -> StaffStatus.ACTIVE
                "Inactive" -> StaffStatus.INACTIVE
                "On Leave" -> StaffStatus.ON_LEAVE
                else -> StaffStatus.ACTIVE
            },
            
            // Keep existing photo URL and update timestamp
            updatedAt = System.currentTimeMillis()
        )
        
        // Handle profile image upload if changed
        if (selectedImageUri != null) {
            uploadProfileImageAndUpdate(updatedStaff, selectedImageUri!!)
        } else {
            updateStaffInDatabase(updatedStaff)
        }
    }
    
    private fun uploadProfileImageAndUpdate(staff: Staff, imageUri: Uri) {
        val imageRef = storageRef.child("staff_photos/${staff.id}.jpg")
        
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val staffWithPhoto = staff.copy(photoUrl = downloadUri.toString())
                    updateStaffInDatabase(staffWithPhoto)
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
                // Update staff without image
                updateStaffInDatabase(staff)
            }
    }
    
    private fun updateStaffInDatabase(staff: Staff) {
        staffManager.updateStaff(staff) { success ->
            showLoading(false)
            if (success) {
                val user = StaffUserConverter.staffToUser(staff)
                Toast.makeText(this, "Staff member updated successfully! Role: ${user.role}", Toast.LENGTH_LONG).show()
                val resultIntent = Intent().putExtra(EXTRA_STAFF, staff)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update staff member", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        saveButton.isEnabled = !show
        saveButton.text = if (show) "Updating..." else "Update Staff"
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(profileImageView)
                    }
                }
                TAKE_PHOTO_REQUEST -> {
                    selectedImageUri?.let { uri ->
                        Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(profileImageView)
                    }
                }
            }
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    override fun onDestroy() {
        super.onDestroy()
        staffManager.cleanup()
    }
} 
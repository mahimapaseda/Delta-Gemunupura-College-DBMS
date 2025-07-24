package com.deltagemunupuramv.dbms

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.storage.FirebaseStorage
import com.deltagemunupuramv.dbms.adapter.StaffFormPagerAdapter
import com.deltagemunupuramv.dbms.fragment.BasicInfoFragment
import com.deltagemunupuramv.dbms.fragment.PersonalDetailsFragment
import com.deltagemunupuramv.dbms.fragment.EmploymentDetailsFragment
import com.deltagemunupuramv.dbms.fragment.StaffClassificationFragment
import com.deltagemunupuramv.dbms.manager.StaffManager
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffStatus
import com.deltagemunupuramv.dbms.model.StaffType
import com.deltagemunupuramv.dbms.util.StaffUserConverter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.UUID

class AddStaffActivity : AppCompatActivity() {
    
    private lateinit var staffManager: StaffManager
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var saveButton: ExtendedFloatingActionButton
    private lateinit var pagerAdapter: StaffFormPagerAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_staff)
        
        staffManager = StaffManager()
        
        initializeViews()
        setupViewPager()
        setupSaveButton()
        setupToolbar()
    }
    
    private fun initializeViews() {
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        saveButton = findViewById(R.id.saveButton)
    }
    
    private fun setupViewPager() {
        pagerAdapter = StaffFormPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        
        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Basic Info"
                1 -> "Personal"
                2 -> "Employment"
                3 -> "Classification"
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }
    
    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            if (validateAllFields()) {
                saveStaff()
            }
        }
    }
    
    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add New Staff Member"
    }
    
    private fun validateAllFields(): Boolean {
        val basicInfoFragment = pagerAdapter.getBasicInfoFragment()
        val classificationFragment = pagerAdapter.getClassificationFragment()
        
        var isValid = true
        
        // Validate basic info
        if (basicInfoFragment != null && !basicInfoFragment.validateFields()) {
            viewPager.currentItem = 0 // Switch to Basic Info tab
            isValid = false
        }
        
        // Validate classification
        if (classificationFragment != null && !classificationFragment.validateFields()) {
            if (isValid) viewPager.currentItem = 3 // Switch to Classification tab only if other validations passed
            isValid = false
        }
        
        // Additional validation for appointed subject based on classification
        val classification = classificationFragment?.getStaffClassification() ?: ""
        val employmentFragment = pagerAdapter.getEmploymentFragment()
        
        if ((classification.contains("Academic Staff") || classification.contains("Non-Academic Staff")) && 
            employmentFragment?.getAppointedSubject()?.isBlank() == true) {
            Toast.makeText(this, "Appointed subject is required for this classification", Toast.LENGTH_SHORT).show()
            viewPager.currentItem = 2 // Switch to Employment tab
            isValid = false
        }
        
        return isValid
    }
    
    private fun saveStaff() {
        val basicInfoFragment = pagerAdapter.getBasicInfoFragment()
        val personalFragment = pagerAdapter.getPersonalFragment()
        val employmentFragment = pagerAdapter.getEmploymentFragment()
        val classificationFragment = pagerAdapter.getClassificationFragment()
        
        if (basicInfoFragment == null || personalFragment == null || 
            employmentFragment == null || classificationFragment == null) {
            Toast.makeText(this, "Error accessing form data", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show loading state
        saveButton.isEnabled = false
        saveButton.text = "Saving..."
        
        // Get selected staff classification and determine type and appointed subject
        val staffClassification = classificationFragment.getStaffClassification()
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
                StaffType.ACADEMIC to employmentFragment.getAppointedSubject()
            }
            staffClassification.contains("Non-Academic Staff") -> {
                StaffType.NON_ACADEMIC to employmentFragment.getAppointedSubject()
            }
            else -> {
                StaffType.ACADEMIC to employmentFragment.getAppointedSubject()
            }
        }
        
        val staff = Staff(
            id = UUID.randomUUID().toString(),
            
            // Basic Information
            fullName = basicInfoFragment.getFullName(),
            nameWithInitials = basicInfoFragment.getNameWithInitials(),
            nicNumber = basicInfoFragment.getNicNumber(),
            registrationNumber = basicInfoFragment.getRegistrationNumber(),
            email = basicInfoFragment.getEmail(),
            password = basicInfoFragment.getPassword(),
            phoneNumber = basicInfoFragment.getPhoneNumber(),
            personalAddress = basicInfoFragment.getPersonalAddress(),
            
            // Personal Details
            dateOfBirth = personalFragment.getDateOfBirth(),
            gender = personalFragment.getGender(),
            maritalStatus = personalFragment.getMaritalStatus(),
            spouseName = personalFragment.getSpouseName(),
            spouseAddress = personalFragment.getSpouseAddress(),
            spouseTelephone = personalFragment.getSpouseTelephone(),
            
            // Employment Details
            dateOfFirstAppointment = employmentFragment.getDateOfFirstAppointment(),
            dateOfAppointmentToSchool = employmentFragment.getDateOfAppointmentToSchool(),
            previouslyServedSchools = employmentFragment.getPreviouslyServedSchools(),
            classAndGrade = employmentFragment.getClassAndGrade(),
            
            // Qualifications
            educationalQualifications = employmentFragment.getEducationalQualifications(),
            professionalQualifications = employmentFragment.getProfessionalQualifications(),
            
            // Teaching Details
            appointedSubject = appointedSubject,
            subjectsTaught = employmentFragment.getSubjectsTaught(),
            gradesTaught = employmentFragment.getGradesTaught(),
            
            // Emergency Contact
            emergencyContactName = personalFragment.getEmergencyContactName(),
            emergencyContactPhone = personalFragment.getEmergencyContactPhone(),
            
            // Staff Classification
            staffType = staffType,
            status = when (classificationFragment.getStatus()) {
                "Active" -> StaffStatus.ACTIVE
                "Inactive" -> StaffStatus.INACTIVE
                "On Leave" -> StaffStatus.ON_LEAVE
                else -> StaffStatus.ACTIVE
            },
            photoUrl = "",
            updatedAt = System.currentTimeMillis()
        )
        
        // Handle profile image upload if present
        val profileImageUri = basicInfoFragment.getProfileImageUri()
        if (profileImageUri != null) {
            uploadProfileImage(staff, profileImageUri)
        } else {
            saveStaffToDatabase(staff)
        }
    }
    
    private fun uploadProfileImage(staff: Staff, imageUri: android.net.Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("staff_photos/${staff.id}.jpg")
        
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val updatedStaff = staff.copy(photoUrl = downloadUri.toString())
                    saveStaffToDatabase(updatedStaff)
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                    resetSaveButton()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
                // Save staff without image
                saveStaffToDatabase(staff)
            }
    }
    
    private fun saveStaffToDatabase(staff: Staff) {
        // Save staff using StaffManager
        staffManager.addStaff(staff) { success ->
            resetSaveButton()
            if (success) {
                // Determine the role that will be assigned
                val user = StaffUserConverter.staffToUser(staff)
                Toast.makeText(this, "Staff member added successfully! Role assigned: ${user.role}", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Failed to add staff member", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun resetSaveButton() {
        saveButton.isEnabled = true
        saveButton.text = "Save Staff Member"
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
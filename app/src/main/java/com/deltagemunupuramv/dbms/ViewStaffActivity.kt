package com.deltagemunupuramv.dbms

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffType

class ViewStaffActivity : AppCompatActivity() {
    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var departmentTextView: TextView
    private lateinit var designationTextView: TextView
    private lateinit var staffTypeTextView: TextView
    private lateinit var dateJoinedTextView: TextView
    private lateinit var qualificationsTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var emergencyContactTextView: TextView
    private lateinit var statusTextView: TextView
    
    // Academic staff specific views
    private lateinit var subjectsTextView: TextView
    private lateinit var teachingExperienceTextView: TextView
    private lateinit var researchPublicationsTextView: TextView
    
    // Non-academic staff specific views
    private lateinit var roleTextView: TextView
    private lateinit var skillsTextView: TextView
    private lateinit var workExperienceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_staff)

        val staff = intent.getParcelableExtra<Staff>("staff")
        if (staff == null) {
            finish()
            return
        }

        setupViews()
        displayStaffDetails(staff)
    }

    private fun setupViews() {
        profileImageView = findViewById(R.id.profileImageView)
        nameTextView = findViewById(R.id.nameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        phoneTextView = findViewById(R.id.phoneTextView)
        departmentTextView = findViewById(R.id.departmentTextView)
        designationTextView = findViewById(R.id.designationTextView)
        staffTypeTextView = findViewById(R.id.staffTypeTextView)
        dateJoinedTextView = findViewById(R.id.dateJoinedTextView)
        qualificationsTextView = findViewById(R.id.qualificationsTextView)
        addressTextView = findViewById(R.id.addressTextView)
        emergencyContactTextView = findViewById(R.id.emergencyContactTextView)
        statusTextView = findViewById(R.id.statusTextView)
        
        // Academic staff specific views
        subjectsTextView = findViewById(R.id.subjectsTextView)
        teachingExperienceTextView = findViewById(R.id.teachingExperienceTextView)
        researchPublicationsTextView = findViewById(R.id.researchPublicationsTextView)
        
        // Non-academic staff specific views
        roleTextView = findViewById(R.id.roleTextView)
        skillsTextView = findViewById(R.id.skillsTextView)
        workExperienceTextView = findViewById(R.id.workExperienceTextView)
    }

    private fun displayStaffDetails(staff: Staff) {
        // Load profile image
        if (staff.photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(staff.photoUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.ic_person)
        }

        // Basic information
        nameTextView.text = staff.name
        emailTextView.text = staff.email
        phoneTextView.text = staff.phone
        departmentTextView.text = staff.department
        designationTextView.text = staff.designation
        staffTypeTextView.text = staff.staffType.name.replace("_", " ")
        dateJoinedTextView.text = staff.dateJoined
        qualificationsTextView.text = staff.qualifications.ifEmpty { "Not specified" }
        addressTextView.text = staff.address.ifEmpty { "Not specified" }
        emergencyContactTextView.text = staff.emergencyContact.ifEmpty { "Not specified" }
        statusTextView.text = staff.status.name.replace("_", " ")

        // Show/hide specific sections based on staff type
        if (staff.staffType == StaffType.ACADEMIC) {
            // Show academic staff fields
            findViewById<TextView>(R.id.academicSectionLabel).visibility = android.view.View.VISIBLE
            findViewById<androidx.cardview.widget.CardView>(R.id.academicSection).visibility = android.view.View.VISIBLE
            
            subjectsTextView.text = if (staff.subjects.isNotEmpty()) {
                staff.subjects.joinToString(", ")
            } else {
                "Not specified"
            }
            teachingExperienceTextView.text = "${staff.teachingExperience} years"
            researchPublicationsTextView.text = staff.researchPublications.toString()

            // Hide non-academic fields
            findViewById<TextView>(R.id.nonAcademicSectionLabel).visibility = android.view.View.GONE
            findViewById<androidx.cardview.widget.CardView>(R.id.nonAcademicSection).visibility = android.view.View.GONE
        } else {
            // Show non-academic staff fields
            findViewById<TextView>(R.id.nonAcademicSectionLabel).visibility = android.view.View.VISIBLE
            findViewById<androidx.cardview.widget.CardView>(R.id.nonAcademicSection).visibility = android.view.View.VISIBLE
            
            roleTextView.text = staff.role.ifEmpty { "Not specified" }
            skillsTextView.text = if (staff.skills.isNotEmpty()) {
                staff.skills.joinToString(", ")
            } else {
                "Not specified"
            }
            workExperienceTextView.text = "${staff.workExperience} years"

            // Hide academic fields
            findViewById<TextView>(R.id.academicSectionLabel).visibility = android.view.View.GONE
            findViewById<androidx.cardview.widget.CardView>(R.id.academicSection).visibility = android.view.View.GONE
        }
    }
} 
package com.deltagemunupuramv.dbms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.deltagemunupuramv.dbms.manager.StaffManager
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffStatus
import com.deltagemunupuramv.dbms.model.StaffType
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class ViewStaffActivity : AppCompatActivity() {
    private lateinit var staffManager: StaffManager
    private lateinit var currentStaff: Staff
    
    // UI Components
    private lateinit var profileImageView: ImageView
    private lateinit var staffNameTextView: TextView
    private lateinit var staffPositionTextView: TextView
    private lateinit var staffIdTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var fullNameTextView: TextView
    private lateinit var nameWithInitialsTextView: TextView
    private lateinit var nicNumberTextView: TextView
    private lateinit var registrationNumberTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var staffTypeTextView: TextView
    private lateinit var appointedSubjectTextView: TextView
    private lateinit var firstAppointmentTextView: TextView
    private lateinit var schoolAppointmentTextView: TextView
    private lateinit var editButton: MaterialButton
    private lateinit var contactButton: MaterialButton
    private lateinit var editFab: ExtendedFloatingActionButton

    companion object {
        const val EXTRA_STAFF = "staff"
        const val EDIT_STAFF_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_staff)
        
        currentStaff = intent.getParcelableExtra<Staff>(EXTRA_STAFF) ?: run {
            Toast.makeText(this, "Error loading staff data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        staffManager = StaffManager()
        initializeViews()
        setupToolbar()
        displayStaffData()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        profileImageView = findViewById(R.id.profileImageView)
        staffNameTextView = findViewById(R.id.staffNameTextView)
        staffPositionTextView = findViewById(R.id.staffPositionTextView)
        staffIdTextView = findViewById(R.id.staffIdTextView)
        statusTextView = findViewById(R.id.statusTextView)
        fullNameTextView = findViewById(R.id.fullNameTextView)
        nameWithInitialsTextView = findViewById(R.id.nameWithInitialsTextView)
        nicNumberTextView = findViewById(R.id.nicNumberTextView)
        registrationNumberTextView = findViewById(R.id.registrationNumberTextView)
        emailTextView = findViewById(R.id.emailTextView)
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView)
        staffTypeTextView = findViewById(R.id.staffTypeTextView)
        appointedSubjectTextView = findViewById(R.id.appointedSubjectTextView)
        firstAppointmentTextView = findViewById(R.id.firstAppointmentTextView)
        schoolAppointmentTextView = findViewById(R.id.schoolAppointmentTextView)
        editButton = findViewById(R.id.editButton)
        contactButton = findViewById(R.id.contactButton)
        editFab = findViewById(R.id.editFab)
    }
    
    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Staff Details"
    }
    
    private fun displayStaffData() {
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
        
        // Header information
        staffNameTextView.text = currentStaff.fullName
        staffPositionTextView.text = currentStaff.appointedSubject
        staffIdTextView.text = "Staff ID: ${currentStaff.registrationNumber}"
        
        // Status with color
        statusTextView.text = when (currentStaff.status) {
            StaffStatus.ACTIVE -> "Active"
            StaffStatus.INACTIVE -> "Inactive"
            StaffStatus.ON_LEAVE -> "On Leave"
        }
        
        // Set status background color
        val statusCard = statusTextView.parent as com.google.android.material.card.MaterialCardView
        when (currentStaff.status) {
            StaffStatus.ACTIVE -> statusCard.setCardBackgroundColor(getColor(R.color.green_success))
            StaffStatus.INACTIVE -> statusCard.setCardBackgroundColor(getColor(R.color.red_error))
            StaffStatus.ON_LEAVE -> statusCard.setCardBackgroundColor(getColor(R.color.orange_warning))
        }
        
        // Basic information
        fullNameTextView.text = currentStaff.fullName
        nameWithInitialsTextView.text = currentStaff.nameWithInitials
        nicNumberTextView.text = currentStaff.nicNumber
        registrationNumberTextView.text = currentStaff.registrationNumber
        emailTextView.text = currentStaff.email
        phoneNumberTextView.text = currentStaff.phoneNumber
        
        // Employment details
        staffTypeTextView.text = when (currentStaff.staffType) {
            StaffType.ACADEMIC -> "Academic Staff"
            StaffType.NON_ACADEMIC -> "Non-Academic Staff"
        }
        appointedSubjectTextView.text = currentStaff.appointedSubject
        firstAppointmentTextView.text = currentStaff.dateOfFirstAppointment.ifEmpty { "Not specified" }
        schoolAppointmentTextView.text = currentStaff.dateOfAppointmentToSchool.ifEmpty { "Not specified" }
    }
    
    private fun setupClickListeners() {
        editButton.setOnClickListener {
            openEditActivity()
        }
        
        editFab.setOnClickListener {
            openEditActivity()
        }
        
        contactButton.setOnClickListener {
            showContactOptions()
        }
    }
    
    private fun openEditActivity() {
        val intent = Intent(this, EditStaffActivity::class.java)
        intent.putExtra(EditStaffActivity.EXTRA_STAFF, currentStaff)
        startActivityForResult(intent, EDIT_STAFF_REQUEST)
    }
    
    private fun showContactOptions() {
        val options = arrayOf("Call", "Email", "SMS")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Contact ${currentStaff.fullName}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> makePhoneCall()
                    1 -> sendEmail()
                    2 -> sendSMS()
                }
            }
            .show()
    }
    
    private fun makePhoneCall() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${currentStaff.phoneNumber}")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No phone app available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${currentStaff.email}")
            putExtra(Intent.EXTRA_SUBJECT, "Message from School")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No email app available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun sendSMS() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${currentStaff.phoneNumber}")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No SMS app available", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_STAFF_REQUEST && resultCode == RESULT_OK) {
            // Refresh staff data if edited
            data?.getParcelableExtra<Staff>(EditStaffActivity.EXTRA_STAFF)?.let { updatedStaff ->
                currentStaff = updatedStaff
                displayStaffData()
                setResult(RESULT_OK, Intent().putExtra(EXTRA_STAFF, currentStaff))
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
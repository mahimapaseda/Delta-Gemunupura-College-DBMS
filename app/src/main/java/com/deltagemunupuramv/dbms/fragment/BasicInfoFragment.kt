package com.deltagemunupuramv.dbms.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.deltagemunupuramv.dbms.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException

class BasicInfoFragment : Fragment() {
    
    // UI Components
    lateinit var profileImageView: ImageView
    lateinit var cameraButton: MaterialButton
    lateinit var galleryButton: MaterialButton
    lateinit var fullNameEditText: TextInputEditText
    lateinit var nameWithInitialsEditText: TextInputEditText
    lateinit var nicNumberEditText: TextInputEditText
    lateinit var registrationNumberEditText: TextInputEditText
    lateinit var emailEditText: TextInputEditText
    lateinit var phoneNumberEditText: TextInputEditText
    lateinit var passwordEditText: TextInputEditText
    lateinit var personalAddressEditText: TextInputEditText
    
    // Image handling
    private var profileImageUri: Uri? = null
    private var tempImageFile: File? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_basic_info, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupImageHandling()
    }
    
    private fun initializeViews(view: View) {
        profileImageView = view.findViewById(R.id.profileImageView)
        cameraButton = view.findViewById(R.id.cameraButton)
        galleryButton = view.findViewById(R.id.galleryButton)
        fullNameEditText = view.findViewById(R.id.fullNameEditText)
        nameWithInitialsEditText = view.findViewById(R.id.nameWithInitialsEditText)
        nicNumberEditText = view.findViewById(R.id.nicNumberEditText)
        registrationNumberEditText = view.findViewById(R.id.registrationNumberEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        personalAddressEditText = view.findViewById(R.id.personalAddressEditText)
    }
    
    fun getFullName(): String = fullNameEditText.text?.toString()?.trim() ?: ""
    fun getNameWithInitials(): String = nameWithInitialsEditText.text?.toString()?.trim() ?: ""
    fun getNicNumber(): String = nicNumberEditText.text?.toString()?.trim() ?: ""
    fun getRegistrationNumber(): String = registrationNumberEditText.text?.toString()?.trim() ?: ""
    fun getEmail(): String = emailEditText.text?.toString()?.trim() ?: ""
    fun getPhoneNumber(): String = phoneNumberEditText.text?.toString()?.trim() ?: ""
    fun getPassword(): String = passwordEditText.text?.toString()?.trim() ?: ""
    fun getPersonalAddress(): String = personalAddressEditText.text?.toString()?.trim() ?: ""
    fun getProfileImageUri(): Uri? = profileImageUri
    
    // Activity result launchers
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempImageFile != null) {
            profileImageUri = Uri.fromFile(tempImageFile)
            profileImageView.setImageURI(profileImageUri)
            Toast.makeText(requireContext(), "Photo captured successfully!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            profileImageUri = it
            profileImageView.setImageURI(profileImageUri)
            Toast.makeText(requireContext(), "Photo selected successfully!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupImageHandling() {
        cameraButton.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
        
        galleryButton.setOnClickListener {
            openGallery()
        }
        
        profileImageView.setOnClickListener {
            if (profileImageUri != null) {
                showImageOptionsDialog()
            } else {
                showImagePickerDialog()
            }
        }
    }
    
    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun openCamera() {
        try {
            tempImageFile = File.createTempFile("staff_photo_", ".jpg", requireContext().cacheDir)
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                tempImageFile!!
            )
            cameraLauncher.launch(photoUri)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    
    private fun showImagePickerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Profile Photo")
            .setMessage("Choose how you'd like to add a profile photo")
            .setPositiveButton("Camera") { _, _ ->
                checkCameraPermissionAndOpen()
            }
            .setNegativeButton("Gallery") { _, _ ->
                openGallery()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }
    
    private fun showImageOptionsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Profile Photo Options")
            .setItems(arrayOf("View Photo", "Change Photo", "Remove Photo")) { _, which ->
                when (which) {
                    0 -> viewFullImage()
                    1 -> showImagePickerDialog()
                    2 -> removePhoto()
                }
            }
            .show()
    }
    
    private fun viewFullImage() {
        // For now, just show a toast. In a full implementation, you'd open a full-screen image viewer
        Toast.makeText(requireContext(), "Image viewer would open here", Toast.LENGTH_SHORT).show()
    }
    
    private fun removePhoto() {
        profileImageUri = null
        profileImageView.setImageResource(R.drawable.ic_person_placeholder)
        Toast.makeText(requireContext(), "Profile photo removed", Toast.LENGTH_SHORT).show()
    }
    
    fun validateFields(): Boolean {
        var isValid = true
        
        if (getFullName().isEmpty()) {
            fullNameEditText.error = "Full name is required"
            isValid = false
        }
        
        if (getNicNumber().isEmpty()) {
            nicNumberEditText.error = "NIC number is required"
            isValid = false
        }
        
        if (getEmail().isEmpty()) {
            emailEditText.error = "Email is required"
            isValid = false
        }
        
        if (getPassword().isEmpty()) {
            passwordEditText.error = "Password is required"
            isValid = false
        }
        
        return isValid
    }
} 
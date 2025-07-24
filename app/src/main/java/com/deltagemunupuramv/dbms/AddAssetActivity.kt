package com.deltagemunupuramv.dbms

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.deltagemunupuramv.dbms.databinding.ActivityAddAssetBinding
import com.deltagemunupuramv.dbms.manager.AssetManager
import com.deltagemunupuramv.dbms.model.Asset
import com.deltagemunupuramv.dbms.model.AssetCategory
import com.deltagemunupuramv.dbms.model.AssetStatus
import com.deltagemunupuramv.dbms.model.AssetType
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog

class AddAssetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAssetBinding
    private lateinit var assetManager: AssetManager
    private var selectedImageUri: Uri? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private var currentPhotoPath: String = ""

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAKE_PHOTO_REQUEST = 2
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAssetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        assetManager = AssetManager()
        
        setupToolbar()
        setupDropdowns()
        setupDatePickers()
        setupImagePicker()
        setupSaveButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Add New Asset"
        }
    }

    private fun setupDropdowns() {
        // Setup Asset Type dropdown
        val assetTypes = AssetType.values().map { it.name.replace("_", " ") }
        val assetTypeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, assetTypes)
        binding.assetTypeDropdown.setAdapter(assetTypeAdapter)

        // Setup Asset Category dropdown
        val assetCategories = AssetCategory.values().map { it.name.replace("_", " ") }
        val assetCategoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, assetCategories)
        binding.assetCategoryDropdown.setAdapter(assetCategoryAdapter)

        // Setup Asset Status dropdown
        val assetStatuses = AssetStatus.values().map { it.name.replace("_", " ") }
        val assetStatusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, assetStatuses)
        binding.assetStatusDropdown.setAdapter(assetStatusAdapter)
    }

    private fun setupDatePickers() {
        // Purchase Date picker
        binding.purchaseDateInput.setOnClickListener {
            showDatePicker(binding.purchaseDateInput, "Select Purchase Date")
        }

        // Warranty Expiry picker
        binding.warrantyExpiryInput.setOnClickListener {
            showDatePicker(binding.warrantyExpiryInput, "Select Warranty Expiry Date")
        }

        // Last Maintenance picker
        binding.lastMaintenanceInput.setOnClickListener {
            showDatePicker(binding.lastMaintenanceInput, "Select Last Maintenance Date")
        }

        // Next Maintenance picker
        binding.nextMaintenanceInput.setOnClickListener {
            showDatePicker(binding.nextMaintenanceInput, "Select Next Maintenance Date")
        }

        // Date Entered picker
        binding.dateEnteredInput.setOnClickListener {
            showDatePicker(binding.dateEnteredInput, "Select Date Entered")
        }

        // Date Removed picker
        binding.dateRemovedInput.setOnClickListener {
            showDatePicker(binding.dateRemovedInput, "Select Date Removed")
        }
    }

    private fun showDatePicker(editText: com.google.android.material.textfield.TextInputEditText, title: String) {
        val calendar = Calendar.getInstance()
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.setTitle(title)
        datePickerDialog.show()
    }

    private fun setupImagePicker() {
        binding.pickImageButton.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> pickImageFromGallery()
                    2 -> { /* Cancel */ }
                }
            }
            .show()
    }

    private fun takePhoto() {
        if (checkCameraPermission()) {
            val photoFile = createImageFile()
            photoFile?.let { file ->
                currentPhotoPath = file.absolutePath
                val photoUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    file
                )
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                }
                startActivityForResult(intent, TAKE_PHOTO_REQUEST)
            }
        } else {
            requestCameraPermission()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "ASSET_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            if (validateInputs()) {
                saveAsset()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate required fields
        if (binding.assetNameInput.text.isNullOrBlank()) {
            binding.assetNameLayout.error = "Asset name is required"
            isValid = false
        } else {
            binding.assetNameLayout.error = null
        }

        if (binding.assetTypeDropdown.text.isNullOrBlank()) {
            binding.assetTypeLayout.error = "Asset type is required"
            isValid = false
        } else {
            binding.assetTypeLayout.error = null
        }

        if (binding.assetCategoryDropdown.text.isNullOrBlank()) {
            binding.assetCategoryLayout.error = "Asset category is required"
            isValid = false
        } else {
            binding.assetCategoryLayout.error = null
        }

        if (binding.assetStatusDropdown.text.isNullOrBlank()) {
            binding.assetStatusLayout.error = "Asset status is required"
            isValid = false
        } else {
            binding.assetStatusLayout.error = null
        }

        if (binding.locationInput.text.isNullOrBlank()) {
            binding.locationLayout.error = "Location is required"
            isValid = false
        } else {
            binding.locationLayout.error = null
        }

        // Validate numeric fields
        val purchasePrice = binding.purchasePriceInput.text.toString()
        if (purchasePrice.isNotEmpty()) {
            try {
                purchasePrice.toDouble()
                binding.purchasePriceLayout.error = null
            } catch (e: NumberFormatException) {
                binding.purchasePriceLayout.error = "Invalid purchase price"
                isValid = false
            }
        }

        val currentValue = binding.currentValueInput.text.toString()
        if (currentValue.isNotEmpty()) {
            try {
                currentValue.toDouble()
                binding.currentValueLayout.error = null
            } catch (e: NumberFormatException) {
                binding.currentValueLayout.error = "Invalid current value"
                isValid = false
            }
        }

        return isValid
    }

    private fun saveAsset() {
        showLoading(true)

        // Create asset object
        val asset = Asset(
            id = generateAssetId(),
            name = binding.assetNameInput.text.toString(),
            type = binding.assetTypeDropdown.text.toString().replace(" ", "_"),
            category = binding.assetCategoryDropdown.text.toString().replace(" ", "_"),
            status = binding.assetStatusDropdown.text.toString().replace(" ", "_"),
            location = binding.locationInput.text.toString(),
            assignedTo = binding.assignedToInput.text.toString(),
            assignedDepartment = binding.assignedDepartmentInput.text.toString(),
            serialNumber = binding.serialNumberInput.text.toString(),
            manufacturer = binding.manufacturerInput.text.toString(),
            model = binding.modelInput.text.toString(),
            purchaseDate = binding.purchaseDateInput.text.toString(),
            purchasePrice = binding.purchasePriceInput.text.toString().toDoubleOrNull() ?: 0.0,
            currentValue = binding.currentValueInput.text.toString().toDoubleOrNull() ?: 0.0,
            warrantyExpiry = binding.warrantyExpiryInput.text.toString(),
            lastMaintenance = binding.lastMaintenanceInput.text.toString(),
            nextMaintenance = binding.nextMaintenanceInput.text.toString(),
            description = binding.descriptionInput.text.toString(),
            notes = binding.notesInput.text.toString(),
            // New fields
            bookName = binding.bookNameInput.text.toString(),
            itemNumber = binding.itemNumberInput.text.toString(),
            item = binding.itemInput.text.toString(),
            dateEntered = binding.dateEnteredInput.text.toString(),
            voucherNumber = binding.voucherNumberInput.text.toString(),
            fromWhomReceived = binding.fromWhomReceivedInput.text.toString(),
            dateRemoved = binding.dateRemovedInput.text.toString(),
            reasonForRemoval = binding.reasonForRemovalInput.text.toString(),
            other = binding.otherInput.text.toString(),
            imageUrl = "",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Upload image if selected
        if (selectedImageUri != null) {
            uploadImageAndSaveAsset(asset)
        } else {
            saveAssetToDatabase(asset)
        }
    }

    private fun generateAssetId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (Math.random() * 1000).toInt()
        return "asset_${timestamp}_$random"
    }

    private fun uploadImageAndSaveAsset(asset: Asset) {
        val imageFileName = "assets/${asset.id}_${System.currentTimeMillis()}.jpg"
        val imageRef = storageRef.child(imageFileName)

        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val assetWithImage = asset.copy(imageUrl = downloadUri.toString())
                    saveAssetToDatabase(assetWithImage)
                }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveAssetToDatabase(asset: Asset) {
        assetManager.addAsset(asset) { success ->
            runOnUiThread {
                showLoading(false)
                if (success) {
                    Toast.makeText(this, "Asset added successfully", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to add asset", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.saveButton.isEnabled = !show
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        binding.assetImage.setImageURI(uri)
                    }
                }
                TAKE_PHOTO_REQUEST -> {
                    val file = File(currentPhotoPath)
                    if (file.exists()) {
                        selectedImageUri = Uri.fromFile(file)
                        binding.assetImage.setImageURI(selectedImageUri)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Show confirmation dialog if user has entered data
        if (hasUserEnteredData()) {
            AlertDialog.Builder(this)
                .setTitle("Discard Changes")
                .setMessage("Are you sure you want to discard your changes?")
                .setPositiveButton("Discard") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun hasUserEnteredData(): Boolean {
        return binding.assetNameInput.text?.isNotEmpty() == true ||
                binding.locationInput.text?.isNotEmpty() == true ||
                binding.assignedToInput.text?.isNotEmpty() == true ||
                binding.serialNumberInput.text?.isNotEmpty() == true ||
                binding.bookNameInput.text?.isNotEmpty() == true ||
                binding.itemNumberInput.text?.isNotEmpty() == true ||
                binding.itemInput.text?.isNotEmpty() == true ||
                binding.voucherNumberInput.text?.isNotEmpty() == true ||
                binding.fromWhomReceivedInput.text?.isNotEmpty() == true ||
                binding.reasonForRemovalInput.text?.isNotEmpty() == true ||
                binding.otherInput.text?.isNotEmpty() == true ||
                selectedImageUri != null
    }
} 
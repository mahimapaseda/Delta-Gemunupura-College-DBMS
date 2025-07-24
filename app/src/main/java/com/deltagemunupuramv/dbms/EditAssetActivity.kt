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
import com.bumptech.glide.Glide
import com.deltagemunupuramv.dbms.databinding.ActivityEditAssetBinding
import com.deltagemunupuramv.dbms.manager.AssetManager
import com.deltagemunupuramv.dbms.model.Asset
import com.deltagemunupuramv.dbms.model.AssetCategory
import com.deltagemunupuramv.dbms.model.AssetStatus
import com.deltagemunupuramv.dbms.model.AssetType
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog

class EditAssetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAssetBinding
    private lateinit var assetManager: AssetManager
    private var selectedImageUri: Uri? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private var currentPhotoPath: String = ""
    private var originalAsset: Asset? = null

    companion object {
        const val EXTRA_ASSET = "extra_asset"
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAKE_PHOTO_REQUEST = 2
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAssetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        assetManager = AssetManager()
        
        setupToolbar()
        setupDropdowns()
        setupDatePickers()
        setupImagePicker()
        setupSaveButton()
        loadAssetData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Edit Asset"
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
                updateAsset()
            }
        }
    }

    private fun loadAssetData() {
        originalAsset = intent.getParcelableExtra(EXTRA_ASSET)
        originalAsset?.let { asset ->
            populateFormWithAsset(asset)
        } ?: run {
            Toast.makeText(this, "Asset not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun populateFormWithAsset(asset: Asset) {
        // Load image
        if (asset.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(asset.imageUrl)
                .placeholder(R.drawable.ic_assets)
                .error(R.drawable.ic_assets)
                .into(binding.assetImage)
        } else {
            binding.assetImage.setImageResource(R.drawable.ic_assets)
        }

        // Basic Information
        binding.assetNameInput.setText(asset.name)
        binding.assetTypeDropdown.setText(asset.type.replace("_", " "), false)
        binding.assetCategoryDropdown.setText(asset.category.replace("_", " "), false)
        binding.assetStatusDropdown.setText(asset.status.replace("_", " "), false)
        binding.locationInput.setText(asset.location)

        // Assignment Information
        binding.assignedToInput.setText(asset.assignedTo)
        binding.assignedDepartmentInput.setText(asset.assignedDepartment)

        // Technical Details
        binding.serialNumberInput.setText(asset.serialNumber)
        binding.manufacturerInput.setText(asset.manufacturer)
        binding.modelInput.setText(asset.model)

        // Financial Information
        binding.purchaseDateInput.setText(asset.purchaseDate)
        binding.purchasePriceInput.setText(if (asset.purchasePrice > 0) asset.purchasePrice.toString() else "")
        binding.currentValueInput.setText(if (asset.currentValue > 0) asset.currentValue.toString() else "")

        // Maintenance Information
        binding.warrantyExpiryInput.setText(asset.warrantyExpiry)
        binding.lastMaintenanceInput.setText(asset.lastMaintenance)
        binding.nextMaintenanceInput.setText(asset.nextMaintenance)

        // Additional Information
        binding.descriptionInput.setText(asset.description)
        binding.notesInput.setText(asset.notes)

        // Entry Information
        binding.bookNameInput.setText(asset.bookName)
        binding.itemNumberInput.setText(asset.itemNumber)
        binding.itemInput.setText(asset.item)
        binding.dateEnteredInput.setText(asset.dateEntered)
        binding.voucherNumberInput.setText(asset.voucherNumber)
        binding.fromWhomReceivedInput.setText(asset.fromWhomReceived)

        // Removal Information
        binding.dateRemovedInput.setText(asset.dateRemoved)
        binding.reasonForRemovalInput.setText(asset.reasonForRemoval)
        binding.otherInput.setText(asset.other)
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

    private fun updateAsset() {
        showLoading(true)

        originalAsset?.let { originalAsset ->
            // Create updated asset object
            val updatedAsset = originalAsset.copy(
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
                updatedAt = System.currentTimeMillis()
            )

            // Upload new image if selected
            if (selectedImageUri != null) {
                uploadImageAndUpdateAsset(updatedAsset)
            } else {
                updateAssetInDatabase(updatedAsset)
            }
        }
    }

    private fun uploadImageAndUpdateAsset(asset: Asset) {
        val imageFileName = "assets/${asset.id}_${System.currentTimeMillis()}.jpg"
        val imageRef = storageRef.child(imageFileName)

        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val assetWithImage = asset.copy(imageUrl = downloadUri.toString())
                    updateAssetInDatabase(assetWithImage)
                }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateAssetInDatabase(asset: Asset) {
        assetManager.updateAsset(asset) { success ->
            runOnUiThread {
                showLoading(false)
                if (success) {
                    Toast.makeText(this, "Asset updated successfully", Toast.LENGTH_LONG).show()
                    val resultIntent = Intent().apply {
                        putExtra(EXTRA_ASSET, asset)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update asset", Toast.LENGTH_LONG).show()
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
        // Show confirmation dialog if user has made changes
        if (hasUserMadeChanges()) {
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

    private fun hasUserMadeChanges(): Boolean {
        originalAsset?.let { original ->
            return binding.assetNameInput.text.toString() != original.name ||
                    binding.assetTypeDropdown.text.toString().replace(" ", "_") != original.type ||
                    binding.assetCategoryDropdown.text.toString().replace(" ", "_") != original.category ||
                    binding.assetStatusDropdown.text.toString().replace(" ", "_") != original.status ||
                    binding.locationInput.text.toString() != original.location ||
                    binding.assignedToInput.text.toString() != original.assignedTo ||
                    binding.assignedDepartmentInput.text.toString() != original.assignedDepartment ||
                    binding.serialNumberInput.text.toString() != original.serialNumber ||
                    binding.manufacturerInput.text.toString() != original.manufacturer ||
                    binding.modelInput.text.toString() != original.model ||
                    binding.purchaseDateInput.text.toString() != original.purchaseDate ||
                    binding.purchasePriceInput.text.toString().toDoubleOrNull() != original.purchasePrice ||
                    binding.currentValueInput.text.toString().toDoubleOrNull() != original.currentValue ||
                    binding.warrantyExpiryInput.text.toString() != original.warrantyExpiry ||
                    binding.lastMaintenanceInput.text.toString() != original.lastMaintenance ||
                    binding.nextMaintenanceInput.text.toString() != original.nextMaintenance ||
                    binding.descriptionInput.text.toString() != original.description ||
                    binding.notesInput.text.toString() != original.notes ||
                    binding.bookNameInput.text.toString() != original.bookName ||
                    binding.itemNumberInput.text.toString() != original.itemNumber ||
                    binding.itemInput.text.toString() != original.item ||
                    binding.dateEnteredInput.text.toString() != original.dateEntered ||
                    binding.voucherNumberInput.text.toString() != original.voucherNumber ||
                    binding.fromWhomReceivedInput.text.toString() != original.fromWhomReceived ||
                    binding.dateRemovedInput.text.toString() != original.dateRemoved ||
                    binding.reasonForRemovalInput.text.toString() != original.reasonForRemoval ||
                    binding.otherInput.text.toString() != original.other ||
                    selectedImageUri != null
        }
        return false
    }
} 
package com.deltagemunupuramv.dbms

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.deltagemunupuramv.dbms.databinding.ActivityViewAssetBinding
import com.deltagemunupuramv.dbms.manager.AssetManager
import com.deltagemunupuramv.dbms.model.Asset
import com.deltagemunupuramv.dbms.util.AccessLevel
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class ViewAssetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewAssetBinding
    private lateinit var assetManager: AssetManager
    private var asset: Asset? = null

    companion object {
        const val EXTRA_ASSET = "extra_asset"
        private const val EDIT_ASSET_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAssetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        assetManager = AssetManager()
        
        setupToolbar()
        setupButtons()
        loadAssetData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Asset Details"
        }
    }

    private fun setupButtons() {
        val currentUser = UserSession.getUser()
        val userRole = currentUser?.role ?: ""

        // Setup Edit Button
        if (AccessLevel.canModifyAssets(userRole)) {
            binding.editButton.setOnClickListener {
                asset?.let { asset ->
                    val intent = Intent(this, EditAssetActivity::class.java).apply {
                        putExtra(EditAssetActivity.EXTRA_ASSET, asset)
                    }
                    startActivityForResult(intent, EDIT_ASSET_REQUEST)
                }
            }
        } else {
            binding.editButton.visibility = View.GONE
        }

        // Setup Delete Button
        if (AccessLevel.canModifyAssets(userRole)) {
            binding.deleteButton.setOnClickListener {
                showDeleteConfirmation()
            }
        } else {
            binding.deleteButton.visibility = View.GONE
        }
    }

    private fun loadAssetData() {
        asset = intent.getParcelableExtra(EXTRA_ASSET)
        asset?.let { asset ->
            displayAssetData(asset)
        } ?: run {
            Toast.makeText(this, "Asset not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayAssetData(asset: Asset) {
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
        binding.assetNameText.text = asset.name
        binding.assetTypeText.text = asset.type.replace("_", " ")
        binding.assetCategoryText.text = asset.category.replace("_", " ")
        binding.assetStatusText.text = asset.status.replace("_", " ")
        binding.locationText.text = asset.location

        // Assignment Information
        binding.assignedToText.text = asset.assignedTo.ifEmpty { "Not assigned" }
        binding.assignedDepartmentText.text = asset.assignedDepartment.ifEmpty { "Not specified" }

        // Technical Details
        binding.serialNumberText.text = asset.serialNumber.ifEmpty { "Not specified" }
        binding.manufacturerText.text = asset.manufacturer.ifEmpty { "Not specified" }
        binding.modelText.text = asset.model.ifEmpty { "Not specified" }

        // Financial Information
        binding.purchaseDateText.text = asset.purchaseDate.ifEmpty { "Not specified" }
        
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        binding.purchasePriceText.text = if (asset.purchasePrice > 0) {
            formatter.format(asset.purchasePrice)
        } else {
            "Not specified"
        }
        
        binding.currentValueText.text = if (asset.currentValue > 0) {
            formatter.format(asset.currentValue)
        } else {
            "Not specified"
        }

        // Maintenance Information
        binding.warrantyExpiryText.text = asset.warrantyExpiry.ifEmpty { "Not specified" }
        binding.lastMaintenanceText.text = asset.lastMaintenance.ifEmpty { "Not specified" }
        binding.nextMaintenanceText.text = asset.nextMaintenance.ifEmpty { "Not specified" }

        // Entry Information
        binding.bookNameText.text = asset.bookName.ifEmpty { "Not specified" }
        binding.itemNumberText.text = asset.itemNumber.ifEmpty { "Not specified" }
        binding.itemText.text = asset.item.ifEmpty { "Not specified" }
        binding.dateEnteredText.text = asset.dateEntered.ifEmpty { "Not specified" }
        binding.voucherNumberText.text = asset.voucherNumber.ifEmpty { "Not specified" }
        binding.fromWhomReceivedText.text = asset.fromWhomReceived.ifEmpty { "Not specified" }

        // Removal Information
        binding.dateRemovedText.text = asset.dateRemoved.ifEmpty { "Not specified" }
        binding.reasonForRemovalText.text = asset.reasonForRemoval.ifEmpty { "No reason specified" }

        // Additional Information
        binding.otherText.text = asset.other.ifEmpty { "No additional information" }
        binding.descriptionText.text = asset.description.ifEmpty { "No description available" }
        binding.notesText.text = asset.notes.ifEmpty { "No notes available" }
    }

    private fun showDeleteConfirmation() {
        asset?.let { asset ->
            AlertDialog.Builder(this)
                .setTitle("Delete Asset")
                .setMessage("Are you sure you want to delete ${asset.name}?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteAsset(asset)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun deleteAsset(asset: Asset) {
        assetManager.deleteAsset(asset.id) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Asset deleted successfully", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to delete asset", Toast.LENGTH_LONG).show()
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
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_ASSET_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    // Refresh the asset data
                    data?.getParcelableExtra<Asset>(EditAssetActivity.EXTRA_ASSET)?.let { updatedAsset ->
                        asset = updatedAsset
                        displayAssetData(updatedAsset)
                    }
                }
            }
        }
    }
} 
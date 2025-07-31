package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deltagemunupuramv.dbms.adapter.AssetAdapter
import com.deltagemunupuramv.dbms.databinding.ActivityManageAssetsBinding
import com.deltagemunupuramv.dbms.manager.AssetManager
import com.deltagemunupuramv.dbms.model.Asset
import com.deltagemunupuramv.dbms.model.AssetCategory
import com.deltagemunupuramv.dbms.model.AssetStatus
import com.deltagemunupuramv.dbms.util.AccessLevel
import com.deltagemunupuramv.dbms.util.DataInitializer
import com.deltagemunupuramv.dbms.util.FirebaseManager
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageAssetsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageAssetsBinding
    private lateinit var assetAdapter: AssetAdapter
    private lateinit var assetManager: AssetManager
    
    private var currentSearchQuery = ""
    private var currentCategory = "All Categories"
    private var allAssets = listOf<Asset>()

    companion object {
        private const val EDIT_ASSET_REQUEST = 1001
        private const val ADD_ASSET_REQUEST = 1002
        private const val VIEW_ASSET_REQUEST = 1003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageAssetsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check access permission
        val currentUser = UserSession.getUser()
        if (currentUser == null || !AccessLevel.canAccessAssets(currentUser.role)) {
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        setupAddButton()

        assetManager = AssetManager()
        loadAssets()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupFilters() {
        // Setup Category filter
        val categories = arrayOf("All Categories") + AssetCategory.values().map { it.name.replace("_", " ") }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.categoryFilterDropdown.setAdapter(categoryAdapter)
        binding.categoryFilterDropdown.setText("All Categories", false)

        // Setup search functionality
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString() ?: ""
                filterAssets()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Setup category filter listener
        binding.categoryFilterDropdown.setOnItemClickListener { _, _, _, _ ->
            currentCategory = binding.categoryFilterDropdown.text.toString()
            filterAssets()
        }
    }

    private fun setupRecyclerView() {
        // Setup adapter
        assetAdapter = AssetAdapter(
            assets = emptyList(),
            onItemClick = { asset ->
                val intent = Intent(this, ViewAssetActivity::class.java).apply {
                    putExtra(ViewAssetActivity.EXTRA_ASSET, asset)
                }
                startActivityForResult(intent, VIEW_ASSET_REQUEST)
            },
            onEditClick = { asset ->
                val currentUser = UserSession.getUser()
                val userRole = currentUser?.role ?: ""
                if (AccessLevel.canModifyAssets(userRole)) {
                    val intent = Intent(this, EditAssetActivity::class.java).apply {
                        putExtra(EditAssetActivity.EXTRA_ASSET, asset)
                    }
                    startActivityForResult(intent, EDIT_ASSET_REQUEST)
                } else {
                    Snackbar.make(binding.root, "You don't have permission to edit assets", Snackbar.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { asset ->
                val currentUser = UserSession.getUser()
                val userRole = currentUser?.role ?: ""
                if (AccessLevel.canModifyAssets(userRole)) {
                    showDeleteConfirmation(asset)
                } else {
                    Snackbar.make(binding.root, "You don't have permission to delete assets", Snackbar.LENGTH_SHORT).show()
                }
            }
        )

        binding.assetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ManageAssetsActivity)
            adapter = assetAdapter
        }
    }

    private fun setupAddButton() {
        val currentUser = UserSession.getUser()
        val userRole = currentUser?.role ?: ""
        
        if (AccessLevel.canModifyAssets(userRole)) {
            binding.addAssetFab.setOnClickListener {
                val intent = Intent(this, AddAssetActivity::class.java)
                startActivityForResult(intent, ADD_ASSET_REQUEST)
            }
        } else {
            binding.addAssetFab.visibility = View.GONE
        }
    }

    private fun loadAssets() {
        showLoading(true)
        assetManager.getAllAssets { assets ->
            if (!isFinishing) {  // Check if activity is still active
                showLoading(false)
                allAssets = assets
                updateAssetsList(assets)
            }
        }
    }

    private fun filterAssets() {
        showLoading(true)
        assetManager.searchAssets(currentSearchQuery) { assets ->
            if (!isFinishing) {
                showLoading(false)
                var filteredAssets = assets

                // Apply category filter
                if (currentCategory != "All Categories") {
                    val categoryFilter = currentCategory.replace(" ", "_")
                    filteredAssets = filteredAssets.filter { asset ->
                        asset.category.equals(categoryFilter, ignoreCase = true)
                    }
                }

                updateAssetsList(filteredAssets)
            }
        }
    }

    private fun updateAssetsList(assets: List<Asset>) {
        assetAdapter.updateAssets(assets)
        updateResultsHeader(assets.size)
        
        // Show or hide empty state
        binding.emptyState.visibility = if (assets.isEmpty()) View.VISIBLE else View.GONE
        binding.assetsRecyclerView.visibility = if (assets.isEmpty()) View.GONE else View.VISIBLE

        // Update the results text with more detailed information
        val resultsText = buildFilterDescription(assets.size)
        binding.resultsText.text = resultsText
    }

    private fun buildFilterDescription(count: Int): String {
        val countText = if (count == 1) "1 Asset" else "$count Assets"
        
        return when {
            // No category filter
            currentCategory == "All Categories" -> countText
            
            // Category filter applied
            else -> "$countText in $currentCategory"
        }
    }

    private fun updateResultsHeader(count: Int) {
        binding.totalCountChip.text = count.toString()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.addAssetFab.isEnabled = !show
        
        // Only hide the RecyclerView if we're showing loading and there are no items
        if (show && assetAdapter.itemCount == 0) {
            binding.assetsRecyclerView.visibility = View.GONE
            binding.emptyState.visibility = View.GONE
        }
    }

    private fun showDeleteConfirmation(asset: Asset) {
        AlertDialog.Builder(this)
            .setTitle("Delete Asset")
            .setMessage("Are you sure you want to delete ${asset.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteAsset(asset)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAsset(asset: Asset) {
        showLoading(true)
        assetManager.deleteAsset(asset.id) { success ->
            if (!isFinishing) {
                showLoading(false)
                if (success) {
                    Snackbar.make(binding.root, "Asset deleted successfully", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            // Restore the deleted asset
                            restoreAsset(asset)
                        }
                        .show()
                } else {
                    Snackbar.make(binding.root, "Failed to delete asset", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun restoreAsset(asset: Asset) {
        assetManager.addAsset(asset) { success ->
            if (!isFinishing) {
                if (success) {
                    Snackbar.make(binding.root, "Asset restored successfully", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Failed to restore asset", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }







    override fun onResume() {
        super.onResume()
        // Refresh the asset list when returning to this screen
        loadAssets()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manage_assets_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                loadAssets()
                true
            }
            R.id.action_sync_data -> {
                syncFirebaseData()
                true
            }
            R.id.action_initialize_sample -> {
                val currentUser = UserSession.getUser()
                if (currentUser != null && AccessLevel.canInitializeSampleData(currentUser.role)) {
                    initializeSampleData()
                } else {
                    Snackbar.make(binding.root, "You don't have permission to initialize sample data", Snackbar.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun syncFirebaseData() {
        Snackbar.make(binding.root, "Syncing data with Firebase...", Snackbar.LENGTH_SHORT).show()
        
        CoroutineScope(Dispatchers.Main).launch {
            val success = FirebaseManager.synchronizeAllData()
            if (success) {
                Snackbar.make(binding.root, "Data synchronized successfully!", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(binding.root, "Failed to synchronize data. Check your internet connection.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun initializeSampleData() {
        Snackbar.make(binding.root, "Initializing sample data...", Snackbar.LENGTH_SHORT).show()
        
        CoroutineScope(Dispatchers.Main).launch {
            val success = DataInitializer.initializeSampleData()
            if (success) {
                Snackbar.make(binding.root, "Sample data initialized successfully!", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(binding.root, "Failed to initialize sample data.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any listeners or resources
        assetManager.cleanup()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_ASSET_REQUEST, ADD_ASSET_REQUEST, VIEW_ASSET_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    // Refresh the asset list when returning from edit, add, or view
                    filterAssets()
                }
            }
        }
    }
} 
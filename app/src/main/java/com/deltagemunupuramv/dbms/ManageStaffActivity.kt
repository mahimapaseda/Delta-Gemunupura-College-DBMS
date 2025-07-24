package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.deltagemunupuramv.dbms.adapter.StaffAdapter
import com.deltagemunupuramv.dbms.databinding.ActivityManageStaffBinding
import com.deltagemunupuramv.dbms.manager.StaffManager
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.util.AccessLevel
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.android.material.snackbar.Snackbar

class ManageStaffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageStaffBinding
    private lateinit var staffAdapter: StaffAdapter
    private lateinit var staffManager: StaffManager
    
    private var currentSearchQuery = ""
    private var currentStaffType = "All Staff"

    companion object {
        private const val EDIT_STAFF_REQUEST = 1001
        private const val ADD_STAFF_REQUEST = 1002
        private const val VIEW_STAFF_REQUEST = 1003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check access permission
        val currentUser = UserSession.getUser()
        if (currentUser == null || !AccessLevel.canAccessStaff(currentUser.role)) {
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupFilters()
        setupAddButton()

        staffManager = StaffManager()
        loadStaff()
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
        // Setup Staff Type filter
        val staffTypes = arrayOf("All Staff", "Academic Staff", "Non-Academic Staff")
        val staffTypeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, staffTypes)
        binding.staffTypeFilterDropdown.setAdapter(staffTypeAdapter)
        binding.staffTypeFilterDropdown.setText("All Staff", false)

        // Setup search functionality
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString() ?: ""
                filterStaff()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Setup staff type filter listener
        binding.staffTypeFilterDropdown.setOnItemClickListener { _, _, _, _ ->
            currentStaffType = binding.staffTypeFilterDropdown.text.toString()
            filterStaff()
        }
    }

    private fun setupRecyclerView() {
        // Setup adapter
        staffAdapter = StaffAdapter(
            staffList = emptyList(),
            onItemClick = { staff ->
                val intent = Intent(this, ViewStaffActivity::class.java)
                intent.putExtra(ViewStaffActivity.EXTRA_STAFF, staff)
                startActivityForResult(intent, VIEW_STAFF_REQUEST)
            },
            onEditClick = { staff ->
                val currentUser = UserSession.getUser()
                val userRole = currentUser?.role ?: ""
                if (AccessLevel.canModifyStaff(userRole)) {
                    val intent = Intent(this, EditStaffActivity::class.java)
                    intent.putExtra(EditStaffActivity.EXTRA_STAFF, staff)
                    startActivityForResult(intent, EDIT_STAFF_REQUEST)
                }
            },
            onDeleteClick = { staff ->
                val currentUser = UserSession.getUser()
                val userRole = currentUser?.role ?: ""
                if (AccessLevel.canModifyStaff(userRole)) {
                    showDeleteConfirmation(staff)
                }
            }
        )

        binding.staffRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ManageStaffActivity)
            adapter = staffAdapter
        }
    }

    private fun setupAddButton() {
        val currentUser = UserSession.getUser()
        val userRole = currentUser?.role ?: ""
        
        if (AccessLevel.canModifyStaff(userRole)) {
            binding.addStaffButton.setOnClickListener {
                val intent = Intent(this, AddStaffActivity::class.java)
                startActivityForResult(intent, ADD_STAFF_REQUEST)
            }
        } else {
            binding.addStaffButton.visibility = View.GONE
        }
    }

    private fun loadStaff() {
        showLoading(true)
        staffManager.getAllStaff { staff ->
            if (!isFinishing) {
                showLoading(false)
                updateStaffList(staff)
            }
        }
    }

    private fun filterStaff() {
        showLoading(true)
        staffManager.getFilteredStaff(
            searchQuery = currentSearchQuery,
            staffType = currentStaffType
        ) { staff ->
            if (!isFinishing) {
                showLoading(false)
                updateStaffList(staff)
            }
        }
    }

    private fun updateStaffList(staffList: List<Staff>) {
        staffAdapter.updateStaffList(staffList)
        updateResultsHeader(staffList.size)
        
        // Show or hide empty state
        binding.emptyState.visibility = if (staffList.isEmpty()) View.VISIBLE else View.GONE
        binding.staffRecyclerView.visibility = if (staffList.isEmpty()) View.GONE else View.VISIBLE

        // Update the results text with more detailed information
        val resultsText = buildFilterDescription(staffList.size)
        binding.resultsText.text = resultsText
    }

    private fun buildFilterDescription(count: Int): String {
        val countText = if (count == 1) "1 Staff Member" else "$count Staff Members"
        
        return when {
            currentStaffType == "All Staff" -> countText
            else -> "$countText in $currentStaffType"
        }
    }

    private fun updateResultsHeader(count: Int) {
        binding.totalCountChip.text = count.toString()
    }

    private fun showDeleteConfirmation(staff: Staff) {
        AlertDialog.Builder(this)
            .setTitle("Delete Staff")
            .setMessage("Are you sure you want to delete ${staff.fullName}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteStaff(staff)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStaff(staff: Staff) {
        showLoading(true)
        staffManager.deleteStaff(staff.id) { success ->
            if (!isFinishing) {
                showLoading(false)
                if (success) {
                    Snackbar.make(binding.root, "${staff.fullName} deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            // Restore staff
                            staffManager.addStaff(staff) { restored ->
                                if (restored) {
                                    Snackbar.make(binding.root, "${staff.fullName} restored", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .show()
                } else {
                    Snackbar.make(binding.root, "Error deleting ${staff.fullName}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.addStaffButton.isEnabled = !show
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_STAFF_REQUEST, ADD_STAFF_REQUEST, VIEW_STAFF_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    // Refresh the staff list when returning from edit, add, or view
                    filterStaff()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the staff list when returning to this screen
        loadStaff()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any listeners or resources
        staffManager.cleanup()
    }
} 
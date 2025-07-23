package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deltagemunupuramv.dbms.adapter.StaffAdapter
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffType
import com.deltagemunupuramv.dbms.util.AccessLevel
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ManageStaffActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var staffAdapter: StaffAdapter
    private lateinit var addStaffButton: ExtendedFloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    
    private val db = FirebaseFirestore.getInstance()
    private val staffCollection = db.collection("staff")
    private var currentFilter = StaffType.ACADEMIC
    private var currentQuery = ""
    private var allStaff = listOf<Staff>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_staff)

        // Check access permission using the user's role string
        val currentUser = UserSession.getUser()
        if (currentUser == null || !AccessLevel.canAccessStaff(currentUser.role)) {
            Toast.makeText(this, "Access denied. Insufficient permissions.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupViews()
        setupRecyclerView()
        loadStaffData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.staff_menu, menu)
        
        // Get current user role
        val currentUser = UserSession.getUser()
        val userRole = currentUser?.role ?: ""
        
        // Hide certain menu items based on user role
        menu.findItem(R.id.action_generate_report)?.isVisible = AccessLevel.canAccessStaff(userRole)
        
        // Setup SearchView
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        setupSearchView()
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.staffRecyclerView)
        addStaffButton = findViewById(R.id.addStaffButton)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)

        // Get current user role
        val currentUser = UserSession.getUser()
        val userRole = currentUser?.role ?: ""

        // Only show add button if user has permission
        addStaffButton.visibility = if (AccessLevel.canModifyStaff(userRole)) View.VISIBLE else View.GONE

        addStaffButton.setOnClickListener {
            startActivity(Intent(this, AddStaffActivity::class.java))
        }

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        val currentUser = UserSession.getUser()
        val userRole = currentUser?.role ?: ""

        staffAdapter = StaffAdapter(
            staffList = emptyList(),
            onItemClick = { staff -> viewStaffDetails(staff) },
            onEditClick = { staff -> 
                if (AccessLevel.canModifyStaff(userRole)) {
                    editStaff(staff)
                } else {
                    Toast.makeText(this, "You don't have permission to edit staff", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { staff ->
                if (AccessLevel.canModifyStaff(userRole)) {
                    confirmDelete(staff)
                } else {
                    Toast.makeText(this, "You don't have permission to delete staff", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ManageStaffActivity)
            adapter = staffAdapter
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query ?: ""
                filterStaff()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                filterStaff()
                return true
            }
        })
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.GONE
        }
    }

    private fun showEmptyState(show: Boolean) {
        emptyState.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun loadStaffData() {
        showLoading(true)
        staffCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (!isFinishing) {
                    allStaff = documents.mapNotNull { doc ->
                        doc.toObject(Staff::class.java)
                    }
                    filterStaff()
                    showLoading(false)
                }
            }
            .addOnFailureListener { e ->
                if (!isFinishing) {
                    showLoading(false)
                    Toast.makeText(this, "Error loading staff: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun filterStaff() {
        val filteredList = allStaff
            .filter { it.staffType == currentFilter }
            .filter {
                if (currentQuery.isEmpty()) true
                else it.name.contains(currentQuery, ignoreCase = true) ||
                     it.department.contains(currentQuery, ignoreCase = true) ||
                     it.designation.contains(currentQuery, ignoreCase = true)
            }
        staffAdapter.updateStaffList(filteredList)
        showEmptyState(filteredList.isEmpty())
    }

    private fun viewStaffDetails(staff: Staff) {
        val intent = Intent(this, ViewStaffActivity::class.java)
        intent.putExtra("staff", staff)
        startActivity(intent)
    }

    private fun editStaff(staff: Staff) {
        val intent = Intent(this, EditStaffActivity::class.java)
        intent.putExtra("staff", staff)
        startActivity(intent)
    }

    private fun confirmDelete(staff: Staff) {
        AlertDialog.Builder(this)
            .setTitle("Delete Staff")
            .setMessage("Are you sure you want to delete ${staff.name}?")
            .setPositiveButton("Delete") { _, _ -> deleteStaff(staff) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteStaff(staff: Staff) {
        showLoading(true)
        staffCollection.document(staff.id)
            .delete()
            .addOnSuccessListener {
                if (!isFinishing) {
                    Toast.makeText(this, "Staff deleted successfully", Toast.LENGTH_SHORT).show()
                    loadStaffData()
                }
            }
            .addOnFailureListener { e ->
                if (!isFinishing) {
                    showLoading(false)
                    Toast.makeText(this, "Error deleting staff: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun generateStaffReport() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "staff_report_$timestamp.csv"
        val reportFile = File(getExternalFilesDir(null), fileName)

        try {
            FileWriter(reportFile).use { writer ->
                // Write CSV header
                writer.append("Name,Email,Phone,Department,Designation,Staff Type,Date Joined," +
                            "Qualifications,Status")
                if (currentFilter == StaffType.ACADEMIC) {
                    writer.append(",Subjects,Teaching Experience,Research Publications")
                } else {
                    writer.append(",Role,Skills,Work Experience")
                }
                writer.append("\n")

                // Write staff data
                allStaff.filter { it.staffType == currentFilter }.forEach { staff ->
                    writer.append("${staff.name},${staff.email},${staff.phone}," +
                                "${staff.department},${staff.designation},${staff.staffType}," +
                                "${staff.dateJoined},${staff.qualifications},${staff.status}")
                    if (staff.staffType == StaffType.ACADEMIC) {
                        writer.append(",${staff.subjects.joinToString("|")}," +
                                    "${staff.teachingExperience},${staff.researchPublications}")
                    } else {
                        writer.append(",${staff.role},${staff.skills.joinToString("|")}," +
                                    "${staff.workExperience}")
                    }
                    writer.append("\n")
                }
            }

            // Share the generated report
            val fileUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                reportFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Share Staff Report"))

        } catch (e: Exception) {
            Toast.makeText(this, "Error generating report: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadStaffData()
    }
} 
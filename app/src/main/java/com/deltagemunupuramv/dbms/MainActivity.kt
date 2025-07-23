package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.deltagemunupuramv.dbms.adapter.FeatureAdapter
import com.deltagemunupuramv.dbms.databinding.ActivityMainBinding
import com.deltagemunupuramv.dbms.databinding.NavHeaderBinding
import com.deltagemunupuramv.dbms.model.Feature
import com.deltagemunupuramv.dbms.util.AccessLevel
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var featureAdapter: FeatureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is logged in
        if (UserSession.getUser() == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        setupToolbar()
        setupNavigationDrawer()
        setupFeaturesList()
        updateUserInfo()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(this)
        
        // Hide menu items based on user role
        val menu = binding.navigationView.menu
        val userRole = UserSession.getUser()?.role ?: ""
        
        // Hide staff management if user doesn't have access
        menu.findItem(R.id.nav_staff)?.isVisible = AccessLevel.canAccessStaff(userRole)
        
        // Hide assets management if user doesn't have access
        menu.findItem(R.id.nav_assets)?.isVisible = AccessLevel.canAccessAssets(userRole)
    }

    private fun setupFeaturesList() {
        val features = mutableListOf<Feature>()
        val userRole = UserSession.getUser()?.role ?: ""
        
        // Add features based on user access
        features.add(Feature(
            R.drawable.ic_students,
            getString(R.string.manage_students),
            getString(R.string.students_desc),
            true
        ))

        // Add staff management if user has access
        if (AccessLevel.canAccessStaff(userRole)) {
            features.add(Feature(
                R.drawable.ic_staff,
                getString(R.string.manage_staff),
                getString(R.string.staff_desc),
                true
            ))
        }

        // Add assets management if user has access
        if (AccessLevel.canAccessAssets(userRole)) {
            features.add(Feature(
                R.drawable.ic_assets,
                getString(R.string.manage_assets),
                getString(R.string.assets_desc),
                true
            ))
        }

        features.add(Feature(
            R.drawable.ic_exams,
            getString(R.string.manage_exams),
            getString(R.string.exams_desc),
            true
        ))

        features.add(Feature(
            R.drawable.ic_timetable,
            getString(R.string.manage_timetables),
            getString(R.string.timetables_desc),
            true,
            UserSession.canModifyTimetables()
        ))

        featureAdapter = FeatureAdapter(features) { feature ->
            // Handle feature click
            when (feature.title) {
                getString(R.string.manage_students) -> {
                    startActivity(Intent(this, ManageStudentsActivity::class.java))
                }
                getString(R.string.manage_staff) -> {
                    startActivity(Intent(this, ManageStaffActivity::class.java))
                }
                getString(R.string.manage_assets) -> {
                    // TODO: Navigate to Assets Activity
                }
                getString(R.string.manage_exams) -> {
                    // TODO: Navigate to Exams Activity
                }
                getString(R.string.manage_timetables) -> {
                    // TODO: Navigate to Timetables Activity
                }
            }
        }

        binding.featuresRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = featureAdapter
        }
    }

    private fun updateUserInfo() {
        UserSession.getUser()?.let { user ->
            // Update welcome text
            binding.welcomeText.text = getString(R.string.welcome_user, user.fullName)
            
            // Update navigation header
            val headerView = binding.navigationView.getHeaderView(0)
            val headerBinding = NavHeaderBinding.bind(headerView)
            headerBinding.userNameText.text = user.fullName
            headerBinding.userRoleText.text = user.role
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_students -> {
                startActivity(Intent(this, ManageStudentsActivity::class.java))
            }
            R.id.nav_staff -> {
                startActivity(Intent(this, ManageStaffActivity::class.java))
            }
            R.id.nav_assets -> {
                // TODO: Navigate to Assets Activity
            }
            R.id.nav_exams -> {
                // TODO: Navigate to Exams Activity
            }
            R.id.nav_timetables -> {
                // TODO: Navigate to Timetables Activity
            }
            R.id.nav_profile -> {
                // TODO: Navigate to Profile Activity
            }
            R.id.nav_logout -> {
                UserSession.clearSession()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
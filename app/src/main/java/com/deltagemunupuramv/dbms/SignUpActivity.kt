package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.SignupBinding
import com.deltagemunupuramv.dbms.model.User
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: SignupBinding
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleDropdown()
        setupSignUpButton()
        setupSignInLink()
    }

    private fun setupRoleDropdown() {
        val roles = arrayOf(
            getString(R.string.role_administrator),
            getString(R.string.role_principal),
            getString(R.string.role_data_officer),
            getString(R.string.role_technical_officer),
            getString(R.string.role_academic_staff)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.roleDropdown.setAdapter(adapter)
    }

    private fun setupSignUpButton() {
        binding.signUpButton.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }
    }

    private fun setupSignInLink() {
        binding.signInText.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate Full Name
        binding.fullNameInput.text.toString().trim().let { fullName ->
            if (fullName.isEmpty()) {
                binding.fullNameLayout.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                binding.fullNameLayout.error = null
            }
        }

        // Validate Username
        binding.usernameInput.text.toString().trim().let { username ->
            if (username.isEmpty() || username.length < 3) {
                binding.usernameLayout.error = getString(R.string.error_invalid_username)
                isValid = false
            } else {
                binding.usernameLayout.error = null
            }
        }

        // Validate Email
        binding.emailInput.text.toString().trim().let { email ->
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = getString(R.string.error_invalid_email)
                isValid = false
            } else {
                binding.emailLayout.error = null
            }
        }

        // Validate Role
        binding.roleDropdown.text.toString().let { role ->
            if (role.isEmpty()) {
                binding.roleLayout.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                binding.roleLayout.error = null
            }
        }

        // Validate Password
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.error_empty_fields)
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = getString(R.string.error_empty_fields)
            isValid = false
        } else if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = getString(R.string.error_passwords_dont_match)
            isValid = false
        } else {
            binding.confirmPasswordLayout.error = null
        }

        return isValid
    }

    private fun registerUser() {
        showLoading(true)

        val newUser = User(
            uid = UUID.randomUUID().toString(),
            username = binding.usernameInput.text.toString().trim(),
            email = binding.emailInput.text.toString().trim(),
            fullName = binding.fullNameInput.text.toString().trim(),
            role = binding.roleDropdown.text.toString()
        )

        // First check if username exists
        usersRef.orderByChild("username").equalTo(newUser.username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        showLoading(false)
                        binding.usernameLayout.error = "Username already taken"
                    } else {
                        // Then check if email exists
                        checkEmailAndRegister(newUser)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    showError(error.message)
                }
            })
    }

    private fun checkEmailAndRegister(newUser: User) {
        usersRef.orderByChild("email").equalTo(newUser.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        showLoading(false)
                        binding.emailLayout.error = "Email already registered"
                    } else {
                        // Save user to database
                        usersRef.child(newUser.uid).setValue(newUser)
                            .addOnSuccessListener {
                                // Save user session and navigate to MainActivity
                                UserSession.setUser(newUser)
                                showLoading(false)
                                startMainActivity()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                showLoading(false)
                                showError(e.message ?: "Registration failed")
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    showError(error.message)
                }
            })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.signUpButton.isEnabled = !show
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.maroon_primary))
            .setTextColor(getColor(R.color.white))
            .show()
    }
} 
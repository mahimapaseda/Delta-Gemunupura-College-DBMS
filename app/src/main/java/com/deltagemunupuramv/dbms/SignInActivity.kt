package com.deltagemunupuramv.dbms

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.deltagemunupuramv.dbms.databinding.SigninBinding
import com.deltagemunupuramv.dbms.model.User
import com.deltagemunupuramv.dbms.util.UserSession
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: SigninBinding
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if already logged in
        if (UserSession.getUser() != null) {
            startMainActivity()
            finish()
            return
        }

        setupSignInButton()
        setupSignUpLink()
    }

    private fun setupSignInButton() {
        binding.signInButton.setOnClickListener {
            if (validateInputs()) {
                signInUser()
            }
        }
    }

    private fun setupSignUpLink() {
        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate Username/Email
        binding.loginFieldInput.text.toString().trim().let { input ->
            if (input.isEmpty()) {
                binding.loginFieldLayout.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                binding.loginFieldLayout.error = null
            }
        }

        // Validate Password
        binding.passwordInput.text.toString().let { password ->
            if (password.isEmpty()) {
                binding.passwordLayout.error = getString(R.string.error_empty_fields)
                isValid = false
            } else {
                binding.passwordLayout.error = null
            }
        }

        return isValid
    }

    private fun signInUser() {
        showLoading(true)
        val loginField = binding.loginFieldInput.text.toString().trim()
        
        // Check if input is email or username
        val isEmail = Patterns.EMAIL_ADDRESS.matcher(loginField).matches()
        val queryField = if (isEmail) "email" else "username"

        // Query user by email or username
        usersRef.orderByChild(queryField).equalTo(loginField)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Get the first user (should be only one)
                        val user = snapshot.children.first().getValue(User::class.java)
                        user?.let {
                            // TODO: Implement proper password verification
                            handleSuccessfulSignIn(it)
                        } ?: run {
                            showLoading(false)
                            showError(getString(R.string.error_invalid_credentials))
                        }
                    } else {
                        showLoading(false)
                        showError(getString(R.string.error_invalid_credentials))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    showError(error.message)
                }
            })
    }

    private fun handleSuccessfulSignIn(user: User) {
        // Save user session
        UserSession.setUser(user)
        showLoading(false)
        
        // Start MainActivity and clear back stack
        startMainActivity()
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.signInButton.isEnabled = !show
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.maroon_primary))
            .setTextColor(getColor(R.color.white))
            .show()
    }
} 
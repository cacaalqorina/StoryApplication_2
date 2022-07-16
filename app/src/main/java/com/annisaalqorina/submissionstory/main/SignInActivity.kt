package com.annisaalqorina.submissionstory.main

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.annisaalqorina.submissionstory.DataPreferences
import com.annisaalqorina.submissionstory.R
import com.annisaalqorina.submissionstory.databinding.ActivitySignInBinding
import com.annisaalqorina.submissionstory.modeldata.SignInBody
import com.annisaalqorina.submissionstory.response.LoginResult
import com.annisaalqorina.submissionstory.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
public class SignInActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        login()
        setupAction()
        userViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        userViewModel.userStatus.observe(this) {
            if (!it) {
                Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun login() {
        userViewModel.getDataSession().observe(this) {
            if (it.token.trim() != "") {
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnSignIn.setOnClickListener {
                userLogin()
            }
            tvSignUp.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }

        binding.apply {
            btnSignIn.isEnabled = !isLoading
            tvSignUp.isEnabled = !isLoading
            emailAccount.isEnabled = !isLoading
            passwordAccount.isEnabled = !isLoading
        }
    }

    private fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun userLogin() {
        val email = binding.emailAccount.text.toString()
        val password = binding.passwordAccount.text.toString()

        when {
            email.isEmpty() -> {
                binding.emailAccount.error = getString(R.string.email_invalid)
            }
            password.isEmpty() -> {
                binding.passwordAccount.error = getString(R.string.password_min)
            }
            else -> {
                if (checkEmail(email) && password.length >= 6) {
                    userViewModel.userLogin(SignInBody(email, password))
                } else {
                    Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show()
                }

                userViewModel.loginResult.observe(this) {
                    userViewModel.saveDataSession(it)
                }
            }
        }

    }

}
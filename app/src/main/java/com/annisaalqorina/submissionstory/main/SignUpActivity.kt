package com.annisaalqorina.submissionstory.main

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.annisaalqorina.submissionstory.R
import com.annisaalqorina.submissionstory.databinding.ActivitySignUpBinding
import com.annisaalqorina.submissionstory.modeldata.SignUpBody

import com.annisaalqorina.submissionstory.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    companion object {
        private val DELLAY_MILLIS = 3000L
    }

    private lateinit var activitySignUpBinding: ActivitySignUpBinding

    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(activitySignUpBinding.root)

        userViewModel.signUpResult.observe(this) {
            Log.d(TAG, "onCreate : $it")
            if (it.error != false) {
                showSnackbar(getString(R.string.register))
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, DELLAY_MILLIS)
            }
        }

        userViewModel.snackbar.observe(this) {
            Snackbar.make(
                activitySignUpBinding.btnSignUp, it, Snackbar.LENGTH_SHORT
            ).show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.register)

        setClick()

        userViewModel.isLoading.observe(this){
            setProgressDialog(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setProgressDialog(isLoading: Boolean) {
        activitySignUpBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setClick() {
        activitySignUpBinding.apply {
            btnSignUp.setOnClickListener {
                if (nameAccount.text.toString().length >= 2) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAccount.text.toString())
                            .matches()
                    ) {
                        if (passwordAccount.length() >= 6) {
                            if (passwordAccount.text.toString().isNotEmpty()) doSignUp()
                            else showSnackbar(resources.getString(R.string.password_dont_match_alert))
                        }else showSnackbar(resources.getString(R.string.password_alert))
                    }else showSnackbar(resources.getString(R.string.email_invalid))
                }else showSnackbar(resources.getString(R.string.name_alert))
            }
        }
    }

    private fun showSnackbar(isiString: String) {
        Snackbar.make(
            activitySignUpBinding.btnSignUp, isiString, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun doSignUp() {
        activitySignUpBinding.apply {
            Log.d(
                TAG, "doSignUp:" + nameAccount.text.toString()
                        + emailAccount.text.toString()
            )
        }

        activitySignUpBinding.apply {
            userViewModel.userRegister(
                SignUpBody(
                    nameAccount.text.toString(),
                    emailAccount.text.toString(),
                    passwordAccount.text.toString()
                )
            )
        }
    }
}

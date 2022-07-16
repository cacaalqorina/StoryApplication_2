package com.annisaalqorina.submissionstory.repository

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.annisaalqorina.submissionstory.api.MyApi
import com.annisaalqorina.submissionstory.main.MainActivity
import com.annisaalqorina.submissionstory.modeldata.SignInBody
import com.annisaalqorina.submissionstory.modeldata.SignUpBody
import com.annisaalqorina.submissionstory.response.LoginResult
import com.annisaalqorina.submissionstory.response.SignInResponse
import com.annisaalqorina.submissionstory.response.SignUpResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: MyApi,
) {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _userStatus = MutableLiveData<Boolean>()
    val userStatus: LiveData<Boolean> = _userStatus

    private val _loginData = MutableLiveData<LoginResult>()
    val loginData: LiveData<LoginResult> = _loginData

    private val _registerResponse = MutableLiveData<SignUpResponse>()
    val registerResponse: LiveData<SignUpResponse> = _registerResponse

    private val _signinResponse = MutableLiveData<SignInResponse>()
    val loginResponse: LiveData<SignInResponse> = _signinResponse

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    fun userLogin(signInBody: SignInBody) {
        _loading.value = true
        val client = apiService.login(signInBody)
        client.enqueue(object : Callback<SignInResponse> {
            override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true

                    Log.d("ResponseApi", "onResponse: ${responseBody.signinResult}")
                    _loginData.value = responseBody.signinResult

                } else {
                    try {
                        val objErr = JSONObject(response.errorBody()!!.string())
                        _snackbarText.value = objErr.getString("message")
                    } catch (e: Exception) {
                        _snackbarText.value = e.message
                    }
                }
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                _loading.value = false
                _snackbarText.value = t.message
                Log.e("ResponseError", "onFailure: ${t.message}")
            }
        })
    }

    fun userRegister(signUpBody: SignUpBody) {
        _loading.value = true
        val client = apiService.register(signUpBody)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _userStatus.value = true
                    Log.d("ResponseAPI", "onResponse: ${responseBody.message}")
                    _snackbarText.value = "Daftar"

                } else {
                    try {
                        val objErr = JSONObject(response.errorBody()!!.string())
                        _snackbarText.value = objErr.getString("message")
                    } catch (e: Exception) {
                        _snackbarText.value = e.message
                    }
                }

            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                _loading.value = false
                _snackbarText.value = t.message
                Log.d("ResponseAPI", "onFailure: ${t.message}")
            }

        })
    }

}
package com.annisaalqorina.submissionstory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.annisaalqorina.submissionstory.DataPreferences
import com.annisaalqorina.submissionstory.modeldata.SignInBody
import com.annisaalqorina.submissionstory.modeldata.SignUpBody
import com.annisaalqorina.submissionstory.repository.UserRepository
import com.annisaalqorina.submissionstory.response.LoginResult
import com.annisaalqorina.submissionstory.response.SignInResponse
import com.annisaalqorina.submissionstory.response.SignUpResponse
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preference: DataPreferences
) : ViewModel() {

    val isLoading: LiveData<Boolean> = userRepository.loading
    val userStatus: LiveData<Boolean> = userRepository.userStatus
    val loginResult: LiveData<LoginResult> = userRepository.loginData
    val signUpResult: LiveData<SignUpResponse> = userRepository.registerResponse
    val snackbar : LiveData<String> = userRepository.snackbarText

    fun userLogin(loginUser: SignInBody) {
        userRepository.userLogin(loginUser)
    }

    fun userRegister(registerUser: SignUpBody) {
        userRepository.userRegister(registerUser)
    }

    fun getDataSession(): LiveData<LoginResult> {
        return preference.getDataSetting().asLiveData()
    }

    fun saveDataSession(dataSetting: LoginResult) {
        viewModelScope.launch {
            preference.saveDataSetting(dataSetting)
        }
    }

    fun clearDataSession() {
        viewModelScope.launch {
            preference.clearDataSetting()
        }
    }
}
package com.example.facebooksignin

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facebooksignin.model.SocialLoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: FacebookRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    fun login(activity: ComponentActivity) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            repository.login(activity)
                .onSuccess { result ->
                    Log.d("FB_LOGIN", "Backend Login Success")
                    // TODO Save JWT using DataStore/SessionManager
                    // result.backendResponse.access_token
                    _loginState.value =
                        LoginState.Success(result)
                }
                .onFailure {
                    _loginState.value =
                        LoginState.Error(it.message ?: "Unknown Error")
                }
        }
    }
    fun logout() {
        repository.logout()
        _loginState.value = LoginState.Idle
    }
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }
}
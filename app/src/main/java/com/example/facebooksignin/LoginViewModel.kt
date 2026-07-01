package com.example.facebooksignin

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = FacebookRepository()
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(activity: ComponentActivity) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            repository.login(activity)
                .onSuccess {
                    Log.d("FB_LOGIN", "ViewModel Success")
                    _loginState.value = LoginState.Success(it)
                }
                .onFailure {
                    _loginState.value = LoginState.Error(it.message ?: "Unknown Error")
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
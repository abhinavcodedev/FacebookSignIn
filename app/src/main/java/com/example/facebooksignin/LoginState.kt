package com.example.facebooksignin

import com.example.facebooksignin.model.SocialLoginResult

sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data class Success(val result: SocialLoginResult) : LoginState
    data class Error(val message: String) : LoginState
}
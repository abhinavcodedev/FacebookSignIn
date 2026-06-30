package com.example.facebooksignin

sealed interface LoginState {

    data object Idle : LoginState

    data object Loading : LoginState

    data class Success(
        val user: FacebookUser
    ) : LoginState

    data class Error(
        val message: String
    ) : LoginState
}
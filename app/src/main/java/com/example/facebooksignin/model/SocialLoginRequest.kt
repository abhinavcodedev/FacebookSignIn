package com.example.facebooksignin.model

data class SocialLoginRequest(
    // Social provider
    val provider: String,
    // Facebook Access Token
    val credential: String,
    // access_token for Facebook
    val credential_type: String,
    // login/signup
    val flow: String,
    // Optional (backend docs)
    val client_id: String? = null
)

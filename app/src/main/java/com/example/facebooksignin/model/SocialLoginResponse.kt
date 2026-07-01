package com.example.facebooksignin.model

data class SocialLoginResponse(
    val success: Boolean,
    val message: String,
    val provider: String,
    val user_id: Int,
    val workspace_id: Int,
    val client_id: String?,
    val access_token: String,
    val token_type: String,
    val redirect_url: String?,
    val is_new_user: Boolean
)

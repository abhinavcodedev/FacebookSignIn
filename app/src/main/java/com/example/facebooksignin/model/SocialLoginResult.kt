package com.example.facebooksignin.model

import com.example.facebooksignin.FacebookUser


data class SocialLoginResult(
    // Facebook Graph API user
    val facebookUser: FacebookUser,
    // Backend JWT Response
    val backendResponse: SocialLoginResponse
)
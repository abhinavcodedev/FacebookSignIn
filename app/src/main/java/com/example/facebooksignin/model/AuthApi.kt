package com.example.facebooksignin.model

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // Backend Social Login API
    @POST("auth/android/social-login")
    suspend fun socialLogin(
        @Body request: SocialLoginRequest
    ): SocialLoginResponse
}
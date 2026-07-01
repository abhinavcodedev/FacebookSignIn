package com.example.facebooksignin.model

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getAccessToken()
        val request = chain.request()
            .newBuilder()
        // Backend JWT available ho to Bearer header add karenge.
        if (!token.isNullOrEmpty()) {
            request.addHeader(
                "Authorization",
                "Bearer $token"
            )
        }
        return chain.proceed(request.build())
    }
}
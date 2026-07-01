package com.example.facebooksignin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.facebooksignin.model.AuthApi
import com.example.facebooksignin.model.SessionManager
import com.example.facebooksignin.model.SocialLoginRequest
import com.example.facebooksignin.model.SocialLoginResponse
import com.example.facebooksignin.model.SocialLoginResult
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.FacebookCallback
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

class FacebookRepository(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager)
{
    // Facebook callback register karne ke liye
    private val callbackManager = CallbackManager.Factory.create()
    // LoginManager ka instance
    private val loginManager = LoginManager.getInstance()
    // Facebook login success hone par AccessToken suspend function ko return karenge
    private var loginContinuation: CancellableContinuation<AccessToken>? = null
    init {
        // SDK callback sirf ek baar register hoga
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    Log.d("FB_LOGIN", "Facebook Login Success")
                    // Facebook AccessToken suspend function ko return karte hain
                    loginContinuation?.resume(result.accessToken)
                }

                override fun onCancel() {
                    loginContinuation?.cancel(CancellationException("Facebook login cancelled"))
                }
                override fun onError(error: FacebookException) {
                    loginContinuation?.cancel(error)
                }
            }
        )

    }
    suspend fun login(
        activity: ComponentActivity
    ): Result<SocialLoginResult> {
        return try {
            // Facebook Login start
            val accessToken = suspendCancellableCoroutine<AccessToken> { continuation ->
                loginContinuation = continuation
                loginManager.logInWithReadPermissions(
                    activity,
                    callbackManager,
                    listOf(
                        "public_profile",
                        "email"
                    )
                )
            }
            // Facebook Graph API
            val facebookUser = fetchUser(accessToken)
            // Backend Login
            val backendResponse = loginWithBackend(accessToken.token)
            // Backend JWT locally save karte hain
            sessionManager.saveAccessToken(backendResponse.access_token)
            Result.success(
                SocialLoginResult(
                    facebookUser = facebookUser,
                    backendResponse = backendResponse
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Graph API se user details fetch karta hai
    // Graph API se profile fetch karke suspend function return karega
    private suspend fun fetchUser(
        accessToken: AccessToken
    ): FacebookUser {
        return suspendCancellableCoroutine { continuation ->
            val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
                val user = parseUser(jsonObject)
                continuation.resume(user)
            }
            val bundle = Bundle().apply {
                putString("fields", "id,name,email,picture.width(400).height(400)")
            }
            request.parameters = bundle
            request.executeAsync()
        }
    }

    // Facebook Access Token backend ko send karega
    private suspend fun loginWithBackend(
        accessToken: String
    ): SocialLoginResponse {
        return authApi.socialLogin(
            SocialLoginRequest(
                provider = "facebook",
                credential = accessToken,
                credential_type = "access_token",
                flow = "login"
            )
        )
    }

     //JSON -> Kotlin Model
     private fun parseUser(json: JSONObject?): FacebookUser {
         val userJson = requireNotNull(json) {
             "Facebook Graph API returned null response." }
         return FacebookUser(
             id = userJson.getString("id"),
             name = userJson.getString("name"),
             email = userJson.optString("email"),
             imageUrl = userJson
                 .getJSONObject("picture")
                 .getJSONObject("data")
                 .getString("url")
         )
     }
    fun logout() {
        // Facebook Session
        loginManager.logOut()
        // Backend Session
        sessionManager.clearSession()
    }

    // Check karta hai user pehle se login hai ya nahi
    fun isLoggedIn(): Boolean {
        val facebookLoggedIn = AccessToken.getCurrentAccessToken()?.isExpired == false
        val backendLoggedIn = sessionManager.isLoggedIn()
        return facebookLoggedIn && backendLoggedIn
    }
}

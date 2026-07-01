package com.example.facebooksignin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import kotlin.coroutines.resume

class FacebookRepository {
    // Facebook callback register karne ke liye
    private val callbackManager = CallbackManager.Factory.create()
    // LoginManager ka instance
    private val loginManager = LoginManager.getInstance()
    private var loginContinuation: CancellableContinuation<Result<FacebookUser>>? = null

    init {
        // SDK callback sirf ek baar register hoga
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult) {
                    Log.d("FB_LOGIN", "Login Success")
                    fetchUser(result.accessToken) {user ->
                        Log.d("FB_LOGIN", "User : $user")

                        loginContinuation?.resume(
                            Result.success(user),
                            null
                        )
                    }
                }

                override fun onCancel() {
                    loginContinuation?.resume(
                        Result.failure(
                            Exception("Login Cancelled")
                        ),
                        null
                    )
                }
                override fun onError(error: FacebookException) {
                    loginContinuation?.resume(
                        Result.failure(error),
                        null
                    )
                }
            }
        )

    }
    suspend fun login(
        activity: ComponentActivity
    ): Result<FacebookUser> {
        return suspendCancellableCoroutine {
            loginContinuation = it
            // Facebook App available hogi to app open hogi,
            // warna browser automatically open ho jayega.
            // Result direct callbackManager ko milega.
            loginManager.logInWithReadPermissions(
                activity,
                callbackManager,
                listOf(
                    "public_profile",
                    "email"
                )
            )
        }
    }

    // Graph API se user details fetch karta hai
    private fun fetchUser(
        accessToken: AccessToken,
        onResult: (FacebookUser) -> Unit

    ) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
            val user = parseUser(jsonObject)
            onResult(user)
        }
        val bundle = Bundle()
        bundle.putString("fields", "id,name,email,picture.width(400).height(400)")
        request.parameters = bundle
        request.executeAsync()

    }

     //JSON -> Kotlin Model
    private fun parseUser(json: JSONObject?): FacebookUser {
        return FacebookUser(
            id = json!!.getString("id"),
            name = json.getString("name"),
            email = json.optString("email"),
            imageUrl = json
                .getJSONObject("picture")
                .getJSONObject("data")
                .getString("url"),)

    }
    fun logout() { loginManager.logOut() }

    // Check karta hai user pehle se login hai ya nahi
    fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }
}

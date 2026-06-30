package com.example.facebooksignin

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
    fun callbackManager() = callbackManager
    suspend fun login(
        activity: Activity
    ): Result<FacebookUser> {
        return suspendCancellableCoroutine {
            loginContinuation = it
/*             Facebook Login SDK start hota hai yahin se.
 SDK internally ye steps follow karta hai:
 1. Device me Facebook app installed hai?
      |-- YES -> Facebook app launch karne ki koshish karega.
      |-- NO  -> Browser / Custom Tab / WebView login open karega.
 2. Login successful hone ke baad callback
    onSuccess(), onCancel(), onError() me return aata hai.
 3. onSuccess() ke baad hum Graph API se
    name, email aur profile picture fetch karte hain.*/
            loginManager.logInWithReadPermissions(
                activity,
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
}

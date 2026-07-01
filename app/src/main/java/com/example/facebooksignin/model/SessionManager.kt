package com.example.facebooksignin.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences =
        context.getSharedPreferences(
            "facebook_session",
            Context.MODE_PRIVATE
        )
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
    // Backend JWT Save
    fun saveAccessToken(token: String) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
    }
    // Backend JWT Read
    fun getAccessToken(): String? {
        return preferences.getString(KEY_ACCESS_TOKEN, null)
    }
    // Backend JWT Clear
    fun clearSession() {
        preferences.edit().clear().apply()
    }
    fun isLoggedIn(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }
}
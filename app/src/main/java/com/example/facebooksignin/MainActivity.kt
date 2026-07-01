package com.example.facebooksignin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.facebooksignin.ui.theme.FacebookSignInTheme
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import com.example.facebooksignin.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        printFacebookKeyHash(this)
        setContent {
            FacebookSignInTheme {
                AppNavigation()
            }
        }
    }

    fun printFacebookKeyHash(context: Context) {
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
        }
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.signingInfo?.apkContentsSigners
        } else {
            @Suppress("DEPRECATION")
            info.signatures
        }
        signatures?.forEach {
            val md = MessageDigest.getInstance("SHA")
            md.update(it.toByteArray())
            Log.d("FB_KEY_HASH", Base64.encodeToString(md.digest(), Base64.NO_WRAP))
        }
    }
}


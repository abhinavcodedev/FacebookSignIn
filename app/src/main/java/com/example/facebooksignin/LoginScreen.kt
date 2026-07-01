package com.example.facebooksignin

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(
    navController: NavHostController
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel: LoginViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            // Facebook login flow start
                viewModel.login(activity)
            }
        ) {
            Text("Continue with Facebook")
        }

        Spacer(modifier = Modifier.height(20.dp))
        when (loginState) {
            is LoginState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginState.Success -> {
                LaunchedEffect(loginState) {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            }
            is LoginState.Error -> {
                Text((loginState as LoginState.Error).message)
            }
            else -> {}
        }
    }
}
package com.example.facebooksignin.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.facebooksignin.HomeScreen
import com.example.facebooksignin.LoginScreen
import com.example.facebooksignin.LoginViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = loginViewModel
            )

        }

    }

}


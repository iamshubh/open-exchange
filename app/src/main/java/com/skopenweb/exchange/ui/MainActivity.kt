package com.skopenweb.exchange.ui

import Routes
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skopenweb.exchange.conversion.ui.ExchangeScreenRoot
import com.skopenweb.exchange.conversion.ui.ExchangeViewModel
import com.skopenweb.exchange.design.theme.OpenExchangeTheme
import com.skopenweb.exchange.ui.splash.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ExchangeViewModel by viewModels<ExchangeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenExchangeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,
                    )
                }
            }
        }
        initialize()
    }

    private fun initialize() {
        viewModel.initialize()
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: ExchangeViewModel) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable<Routes.Splash> {
            SplashScreen(redirectToMainScreen = {
                navController.navigate(Routes.Main) {
                    popUpTo(Routes.Splash) {
                        inclusive = true
                    }
                }
            })
        }
        composable<Routes.Main> {
            ExchangeScreenRoot(viewModel = viewModel)
        }
    }
}
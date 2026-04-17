package com.personal.appstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.personal.appstore.ui.AppDetailScreen
import com.personal.appstore.ui.AppListScreen
import com.personal.appstore.ui.SettingsScreen
import com.personal.appstore.ui.StoreViewModel
import com.personal.appstore.ui.theme.AppStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppStoreTheme {
                AppStoreApp()
            }
        }
    }
}

@Composable
fun AppStoreApp() {
    val navController = rememberNavController()
    val viewModel: StoreViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            AppListScreen(
                viewModel = viewModel,
                onAppClick = { app ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("app", app)
                    navController.navigate("detail")
                },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("detail") {
            val app = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<com.personal.appstore.data.model.AppEntry>("app")
            if (app != null) {
                AppDetailScreen(
                    app = app,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

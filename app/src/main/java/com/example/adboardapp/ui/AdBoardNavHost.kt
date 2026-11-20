package com.example.adboardapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// Routes used in the app
private const val LIST_ROUTE = "list"
private const val DETAILS_ROUTE = "details"

// Single NavHost with the list and the details screens
@Composable
fun AdBoardNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LIST_ROUTE
    ) {
        composable(LIST_ROUTE) {
            AdListScreen(
                onAdClick = { id -> navController.navigate("$DETAILS_ROUTE/$id") }
            )
        }

        composable(
            route = "$DETAILS_ROUTE/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { entry ->
            val itemId = entry.arguments?.getInt("itemId") ?: 0
            DetailScreen(
                viewModel = koinViewModel(parameters = { parametersOf(itemId) }),
                onBack = { navController.popBackStack() }
            )
        }
    }
}

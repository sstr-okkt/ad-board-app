package com.example.adboardapp.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// Routes used in the app
private const val LIST_ROUTE = "list"
private const val MY_ROUTE = "my"
private const val FAVORITES_ROUTE = "favorites"
private const val SETTINGS_ROUTE = "settings"
private const val DETAILS_ROUTE = "details"
private const val EDIT_ROUTE = "edit"

// Bottom bar tabs
private val tabRoutes = setOf(LIST_ROUTE, MY_ROUTE, FAVORITES_ROUTE, SETTINGS_ROUTE)

// Single NavHost with the bottom bar and all screens
@Composable
fun AdBoardNavHost(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentRoute in tabRoutes) {
                BottomBar(
                    currentRoute = currentRoute,
                    onSelect = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LIST_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(LIST_ROUTE) {
                AdListScreen(
                    onAdClick = { id -> navController.navigate("$DETAILS_ROUTE/$id") },
                    onAddClick = { navController.navigate("$EDIT_ROUTE?adId=0") }
                )
            }

            composable(MY_ROUTE) {
                MyAdsScreen(
                    onAdClick = { id -> navController.navigate("$DETAILS_ROUTE/$id") },
                    onAddClick = { navController.navigate("$EDIT_ROUTE?adId=0") },
                    onEditClick = { id -> navController.navigate("$EDIT_ROUTE?adId=$id") }
                )
            }

            composable(FAVORITES_ROUTE) {
                FavoritesScreen(
                    onAdClick = { id -> navController.navigate("$DETAILS_ROUTE/$id") }
                )
            }

            composable(SETTINGS_ROUTE) {
                SettingsScreen()
            }

            composable(
                route = "$DETAILS_ROUTE/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { entry ->
                val itemId = entry.arguments?.getInt("itemId") ?: 0
                DetailScreen(
                    viewModel = koinViewModel(parameters = { parametersOf(itemId) }),
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("$EDIT_ROUTE?adId=$itemId") },
                    onDeleted = { navController.popBackStack() }
                )
            }

            composable(
                route = "$EDIT_ROUTE?adId={adId}",
                arguments = listOf(
                    navArgument("adId") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { entry ->
                val adId = entry.arguments?.getInt("adId") ?: 0
                AdEditScreen(
                    viewModel = koinViewModel(parameters = { parametersOf(adId) }),
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}

// Bottom navigation between search, my ads and favorites
@Composable
private fun BottomBar(
    currentRoute: String?,
    onSelect: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == LIST_ROUTE,
            onClick = { onSelect(LIST_ROUTE) },
            icon = { Icon(Icons.Filled.Search, contentDescription = null) },
            label = { Text("Пошук") }
        )
        NavigationBarItem(
            selected = currentRoute == MY_ROUTE,
            onClick = { onSelect(MY_ROUTE) },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Мої") }
        )
        NavigationBarItem(
            selected = currentRoute == FAVORITES_ROUTE,
            onClick = { onSelect(FAVORITES_ROUTE) },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
            label = { Text("Обране") }
        )
        NavigationBarItem(
            selected = currentRoute == SETTINGS_ROUTE,
            onClick = { onSelect(SETTINGS_ROUTE) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text("Налаштування") }
        )
    }
}

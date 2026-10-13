package fr.univsmb.fridgemate.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
//import fr.univsmb.fridgemate.ui.AddProductScreen
//import fr.univsmb.fridgemate.ui.EditProductScreen
import fr.univsmb.fridgemate.ui.FridgeScreen
import fr.univsmb.fridgemate.ui.FridgeViewModel
import fr.univsmb.fridgemate.ui.HomeScreen
import fr.univsmb.fridgemate.ui.features.AddProductScreen
import fr.univsmb.fridgemate.ui.features.EditProductScreen

@Composable
fun AppNavigation(
    // Un seul ViewModel partagé par tous les écrans (portée Activity)
    vm: FridgeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // La barre du bas n'apparaît que sur Accueil et Frigo, pas sur les formulaires
    val showBottomBar = bottomItems.any { it.link.route == currentRoute }

    fun goToTab(link: NavLink) {
        navController.navigate(link.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FridgeBottomBar(currentRoute = currentRoute, onSelect = ::goToTab)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavLink.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavLink.Home.route) {
                HomeScreen(
                    vm = vm,
                    onVoirFrigo = { goToTab(NavLink.Frigo) },
                    onAjouter = { navController.navigate(NavLink.AddProduct.route) }
                )
            }

            composable(NavLink.Frigo.route) {
                FridgeScreen(
                    vm = vm,
                    onAjouter = { navController.navigate(NavLink.AddProduct.route) },
                    onModifier = { code ->
                        navController.navigate(NavLink.EditProduct.createRoute(code))
                    }
                )
            }

            composable(NavLink.AddProduct.route) {
                AddProductScreen(
                    vm = vm,
                    onDone = { navController.popBackStack() }
                )
            }

            composable(
                route = NavLink.EditProduct.route,
                arguments = listOf(
                    navArgument(NavLink.EditProduct.ARG_CODE) { type = NavType.StringType }
                )
            ) { entry -> val code = entry.arguments?.getString(NavLink.EditProduct.ARG_CODE).orEmpty()
                EditProductScreen(
                    vm = vm,
                    code = code,
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun FridgeBottomBar(
    currentRoute: String?,
    onSelect: (NavLink) -> Unit
) {
    NavigationBar(containerColor = Color(0xFFE8F4E9), tonalElevation = 0.dp) {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.link.route,
                onClick = { onSelect(item.link) },
                icon = {
                    Box(
                        modifier = Modifier.size(34.dp).clip(CircleShape)
                            .background(if (currentRoute == item.link.route) Color(0xFF205544) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = item.label,
                            tint = if (currentRoute == item.link.route) Color.White else Color(0xFF205544))
                    }
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedTextColor = Color(0xFF205544),
                    unselectedTextColor = Color(0xFF45584D)
                )
            )
        }
    }
}
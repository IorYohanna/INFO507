package fr.univsmb.fridgemate.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavLink(val route: String) {
    data object Home : NavLink("home")
    data object Frigo : NavLink("frigo")
    data object AddProduct : NavLink("produit/ajouter")

    data object EditProduct : NavLink("produit/modifier/{code}") {
        const val ARG_CODE = "code"
        fun createRoute(code: String) = "produit/modifier/$code"
    }
}


data class BottomItem(
    val link: NavLink,
    val label: String,
    val icon: ImageVector
)

val bottomItems = listOf(
    BottomItem(NavLink.Home, "Accueil", Icons.Filled.Home),
    BottomItem(NavLink.Frigo, "Frigo", Icons.AutoMirrored.Filled.List)
)
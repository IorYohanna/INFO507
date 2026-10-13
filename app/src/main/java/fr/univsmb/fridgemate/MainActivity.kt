package fr.univsmb.fridgemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.univsmb.fridgemate.ui.navigation.AppNavigation
import fr.univsmb.fridgemate.ui.theme.FridgeMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridgeMateTheme {
                AppNavigation()
            }
        }
    }
}
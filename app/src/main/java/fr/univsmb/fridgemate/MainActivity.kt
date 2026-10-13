
package fr.univsmb.fridgemate

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import fr.univsmb.fridgemate.notifications.ExpirationWorker
import fr.univsmb.fridgemate.ui.navigation.AppNavigation
import fr.univsmb.fridgemate.ui.theme.FridgeMateTheme

class MainActivity : ComponentActivity() {

    private val requestNotifications =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                ExpirationWorker.checkNow(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= 33) {
            requestNotifications.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        setContent {
            FridgeMateTheme {
                AppNavigation()
            }
        }
    }
}

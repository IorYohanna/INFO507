package fr.univsmb.fridgemate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.univsmb.fridgemate.domain.usecase.FetchProductInfoUseCase
import fr.univsmb.fridgemate.repository.OpenFoodFactRepository
import fr.univsmb.fridgemate.ui.FridgeScreen
import fr.univsmb.fridgemate.ui.theme.FridgeMateTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridgeMateTheme {
                FridgeScreen(modifier = Modifier.padding(horizontal = 50.dp, vertical = 50.dp))
            }
        }
    }

    @Composable
    fun TestApiScreen(modifier: Modifier = Modifier) {
        val fetchInfo = remember { FetchProductInfoUseCase(OpenFoodFactRepository()) }
        val scope = rememberCoroutineScope()

        var code by remember { mutableStateOf("3017620422003") }   // Nutella
        var resultat by remember { mutableStateOf("Aucun appel pour l'instant") }
        var loading by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Code-barres") },
                singleLine = true
            )

            Button(
                enabled = !loading,
                onClick = {
                    scope.launch {
                        loading = true
                        val res = fetchInfo(code.trim())
                        Log.d("FridgeMate", "OFF : $res")
                        resultat = res.fold(
                            onSuccess = { info ->
                                info?.toString() ?: "Produit introuvable (null)"
                            },
                            onFailure = { "Erreur : ${it::class.simpleName} - ${it.message}" }
                        )
                        loading = false
                    }
                }
            ) {
                Text(if (loading) "Chargement..." else "Tester l'API")
            }

            Text(resultat)
        }
    }
}
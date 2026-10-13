package fr.univsmb.fridgemate.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.univsmb.fridgemate.model.ProduitModel
import fr.univsmb.fridgemate.model.calculerStatut
import fr.univsmb.fridgemate.ui.FridgeViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Composable
fun EditProductScreen(
    vm: FridgeViewModel,
    code: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val produits by vm.produits.collectAsState()
    val produit = produits.find { it.code == code }

    if (produit == null) {
        Column(modifier.padding(16.dp)) {
            Text("Produit introuvable.")
            Button(onClick = onDone) { Text("Retour") }
        }
    } else {
        EditForm(produit = produit, vm = vm, onDone = onDone, modifier = modifier)
    }
}

@Composable
private fun EditForm(
    produit: ProduitModel,
    vm: FridgeViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableIntStateOf(produit.nombre) }
    var date by remember { mutableStateOf(produit.date_expiration) }
    var erreur by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDone) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
            Text(
                "Modifier le produit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(produit.nom, fontWeight = FontWeight.Bold)
        Text("Contenance : ${produit.quantite} ${produit.unite.raw}")

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Nombre : ")
            OutlinedButton(onClick = { if (nombre > 1) nombre-- }) { Text("-") }
            Text("$nombre", modifier = Modifier.padding(horizontal = 16.dp))
            OutlinedButton(onClick = { nombre++ }) { Text("+") }
        }

        OutlinedTextField(
            value = date,
            onValueChange = { date = it; erreur = null },
            label = { Text("Expiration (yyyy-MM-dd)") },
            singleLine = true,
            isError = erreur != null,
            supportingText = erreur?.let { { Text(it) } }
        )

        Button(onClick = {
            val dateValide = try {
                LocalDate.parse(date.trim()).toString()
            } catch (e: DateTimeParseException) {
                erreur = "Date invalide (format attendu : yyyy-MM-dd)"
                return@Button
            }

            vm.modifier(
                produit.copy(
                    nombre = nombre,
                    date_expiration = dateValide,
                    statut = calculerStatut(dateValide),
                    // nouvelle date → la notification peut être renvoyée
                    notifie = if (dateValide != produit.date_expiration) false else produit.notifie
                )
            )
            onDone()
        }) { Text("Enregistrer") }
    }
}
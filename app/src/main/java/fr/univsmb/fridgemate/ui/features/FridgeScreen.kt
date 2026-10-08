package fr.univsmb.fridgemate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.univsmb.fridgemate.model.parseQuantite

@Composable
fun FridgeScreen(
    modifier: Modifier = Modifier,
    vm: FridgeViewModel = viewModel()
) {
    val produits by vm.produits.collectAsState()
    val add by vm.add.collectAsState()

    var nombre by remember { mutableIntStateOf(1) }
    var date by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ajouter un produit")

                OutlinedTextField(
                    value = add.code,
                    onValueChange = vm::onCodeChange,
                    label = { Text("Code-barres") },
                    singleLine = true
                )

                Button(onClick = vm::rechercher, enabled = !add.loading) {
                    Text(if (add.loading) "Recherche..." else "Rechercher")
                }

                add.message?.let { Text(it) }

                add.info?.let { info ->
                    val (q, u) = parseQuantite(info.quantity)

                    Card {
                        Column(Modifier.padding(12.dp)) {
                            Text("${info.nom} (${info.marque ?: "marque inconnue"})")
                            Text("Nutri-score : ${info.nutriscore}")
                            Text(
                                "Contenance : ${info.quantity ?: "non renseignée"} " +
                                        "→ retenu : $q ${u.raw}"
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nombre : ")
                        OutlinedButton(onClick = { if (nombre > 1) nombre-- }) { Text("-") }
                        Text("$nombre", modifier = Modifier.padding(horizontal = 16.dp))
                        OutlinedButton(onClick = { nombre++ }) { Text("+") }
                    }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Expiration (yyyy-MM-dd)") },
                        singleLine = true
                    )

                    Button(onClick = {
                        vm.ajouter(
                            nombre = nombre,
                            dateExpiration = date,
                            onSuccess = {
                                nombre = 1
                                date = ""
                            }
                        )
                    }) { Text("Ajouter au frigo") }
                }
            }
        }


        item { Text("Mon frigo (${produits.size})") }

        items(produits, key = { it.code }) { p ->
            Card {
                Column(Modifier.padding(12.dp)) {
                    Text("${p.nom} ×${p.nombre}")
                    Text("${p.quantite} ${p.unite.raw} · exp. ${p.date_expiration}")
                    Text("Statut : ${p.statut.raw}")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.consommerUn(p.code) }) { Text("-1") }
                        OutlinedButton(onClick = { vm.supprimer(p.code) }) { Text("Supprimer") }
                    }
                }
            }
        }

        if (produits.isNotEmpty()) {
            item {
                OutlinedButton(onClick = vm::toutSupprimer) { Text("Vider le frigo") }
            }
        }
    }
}
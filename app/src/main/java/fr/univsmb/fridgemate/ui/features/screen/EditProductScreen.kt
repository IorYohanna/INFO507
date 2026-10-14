package fr.univsmb.fridgemate.ui.features.screen

import fr.univsmb.fridgemate.R
import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.univsmb.fridgemate.model.ProduitModel
import fr.univsmb.fridgemate.model.calculerStatut
import fr.univsmb.fridgemate.ui.FridgeViewModel
import fr.univsmb.fridgemate.ui.StatutChip
import fr.univsmb.fridgemate.ui.VertFonce
import fr.univsmb.fridgemate.ui.theme.DeepGreen
import fr.univsmb.fridgemate.ui.theme.ForestGreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

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
            Text(stringResource(id=R.string.product_not_found))
            Button(onClick = onDone) { Text(stringResource(id = R.string.back)) }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDone) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                }
                Text(
                    stringResource(id = R.string.edit_product),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = produit.image_url,
                        contentDescription = produit.nom,
                        modifier = Modifier.fillMaxWidth().height(165.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        produit.nom,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = VertFonce
                    )
                    Text("${stringResource(R.string.contenance)} : ${produit.quantite} ${produit.unite.raw}")
                    Text(produit.marque.orEmpty().ifBlank { "Marque inconnue" }, color = Color.Gray)
                    StatutChip(produit.statut)
                }
            }
            EditForm(produit = produit, vm = vm, onDone = onDone, modifier = modifier)
        }
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
    val context = LocalContext.current
    val chooseDate: () -> Unit = {
        val expiration = LocalDate.parse(date.trim())
        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = LocalDate.of(
                    year,
                    month + 1,
                    day
                ).toString()
            },
            expiration.year,
            expiration.monthValue - 1,
            expiration.dayOfMonth
        ).show()
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${stringResource(id = R.string.number)} : ")
            OutlinedButton(onClick = { if (nombre > 1) nombre-- }) { Text("-") }
            Text("$nombre", modifier = Modifier.padding(horizontal = 16.dp))
            OutlinedButton(onClick = { nombre++ }) { Text("+") }
        }
        Surface(
            border = BorderStroke(1.dp, color = ForestGreen),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable(onClick = chooseDate)
            )
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FieldShape)
                    .padding(17.dp),
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = ForestGreen
                )

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        date.format(
                            DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy",
                                Locale.FRENCH
                            )
                        ) ?: "Choisir une date",
                        fontWeight = FontWeight.SemiBold,
                        color = DeepGreen
                    )
                }
            }

        }
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

                    notifie = if (dateValide != produit.date_expiration) false else produit.notifie
                )
            )
            onDone()
        }) { Text(stringResource(id = R.string.save)) }
    }
}
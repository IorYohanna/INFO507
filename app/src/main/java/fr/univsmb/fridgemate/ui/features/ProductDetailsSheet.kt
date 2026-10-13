package fr.univsmb.fridgemate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.univsmb.fridgemate.model.ProduitModel
import fr.univsmb.fridgemate.model.texteExpiration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun dateLisible(texte: String): String = runCatching {
    LocalDate.parse(texte).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH))
}.getOrDefault(texte)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsSheet(produit: ProduitModel, onDismiss: () -> Unit, onModifier: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFFAFCF9)) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Détails du produit", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = VertFonce)
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                Column(
                    Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(model = produit.image_url, contentDescription = produit.nom,
                        modifier = Modifier.fillMaxWidth().height(165.dp), contentScale = ContentScale.Fit)
                    Text(produit.nom, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = VertFonce)
                    Text(produit.marque.orEmpty().ifBlank { "Marque inconnue" }, color = Color.Gray)
                    StatutChip(produit.statut)
                }
            }
            Text("Informations générales", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VertFonce)
            DetailsCard {
                DetailRow("Code-barres", produit.code)
                DetailRow("Marque", produit.marque.orEmpty().ifBlank { "Non renseignée" })
                DetailRow("Catégorie principale", produit.categorie.orEmpty().split(',', ';').firstOrNull()?.trim().orEmpty().ifBlank { "Non renseignée" })
                DetailRow("Contenance", "${produit.quantite} ${produit.unite.raw}")
                DetailRow("Nombre en stock", produit.nombre.toString())
                DetailRow("Nutri-Score", if (produit.nutriscore.name == "UNKOWN") "Non renseigné" else produit.nutriscore.name)
            }
            Text("Conservation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = VertFonce)
            DetailsCard {
                DetailRow("Date d'ajout", dateLisible(produit.date_ajout))
                DetailRow("Date d'expiration", dateLisible(produit.date_expiration))
                DetailRow("Échéance", texteExpiration(produit.date_expiration))
                DetailRow("Statut", produit.statut.raw)
                DetailRow("Notification déjà envoyée", if (produit.notifie) "Oui" else "Non")
            }
            Button(
                onClick = onModifier, modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VertFonce),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Modifier le produit", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun DetailsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp), content = content)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(label, modifier = Modifier.weight(1f), color = Color.Gray, fontSize = 13.sp)
        Spacer(Modifier.width(10.dp))
        Text(value, modifier = Modifier.weight(1.15f), color = VertFonce, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

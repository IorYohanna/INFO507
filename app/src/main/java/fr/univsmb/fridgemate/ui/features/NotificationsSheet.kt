package fr.univsmb.fridgemate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.univsmb.fridgemate.model.ProduitModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    produits: List<ProduitModel>,
    onDismiss: () -> Unit,
    onProductClick: (String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color(0xFFFAFCF9)) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = VertFonce)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Mes alertes", fontSize = 24.sp, color = VertFonce, fontWeight = FontWeight.Bold)
                    Text("${produits.size} aliment(s) à surveiller", color = Color.Gray, fontSize = 13.sp)
                }
                TextButton(onClick = onDismiss) { Text("Fermer") }
            }
            if (produits.isEmpty()) {
                Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CheckCircle, null, tint = VertFonce, modifier = Modifier.size(44.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Tout va bien !", fontWeight = FontWeight.Bold, color = VertFonce)
                    Text("Aucun aliment à surveiller pour le moment.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(produits, key = { it.code }) { produit ->
                        val expiration = runCatching { LocalDate.parse(produit.date_expiration) }.getOrNull()
                        val jours = expiration?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }
                        val texte = when {
                            jours == null -> "Date inconnue"
                            jours < 0 -> "Expiré depuis ${-jours} jour(s)"
                            jours == 0L -> "Expire aujourd'hui"
                            jours == 1L -> "Expire demain"
                            else -> "Expire dans $jours jours"
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onProductClick(produit.code) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                ProduitImage(produit.image_url, produit.nom, Modifier.size(64.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(produit.nom, color = VertFonce, fontWeight = FontWeight.Bold)
                                    Text("${produit.nombre} en stock · ${produit.marque.orEmpty()}", color = Color.Gray, fontSize = 12.sp)
                                    Text(texte, color = if (jours != null && jours <= 0L) Rouge else Ambre, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

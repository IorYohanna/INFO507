package fr.univsmb.fridgemate.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.univsmb.fridgemate.domain.data.Statut
import fr.univsmb.fridgemate.model.ProduitModel
import fr.univsmb.fridgemate.model.texteExpiration


internal val VertFonce = Color(0xFF205544)
internal val VertClair = Color(0xFFE8F4E9)
internal val Creme = Color(0xFFFFF3DF)
internal val Ambre = Color(0xFFBE6D0D)
internal val RougeFond = Color(0xFFFDE8E8)
internal val Rouge = Color(0xFFB3261E)

@Composable
fun HomeScreen(
    vm: FridgeViewModel,
    onVoirFrigo: () -> Unit,
    onAjouter: () -> Unit,
    modifier: Modifier = Modifier,
    nomUtilisateur: String = "Chriskely"
) {
    val produits by vm.produits.collectAsState()

    // Le ViewModel a déjà recalculé le statut et trie par date d'expiration
    val frais = produits.count { it.statut == Statut.Frais }
    val aUtiliserVite = produits.count { it.statut == Statut.BPerime }
    val urgents = produits.filter { it.statut != Statut.Frais }
    val recents = produits.sortedByDescending { it.date_ajout }.take(2)

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { HomeHeader(hasAlert = urgents.isNotEmpty(), onBellClick = onVoirFrigo) }

        item {
            Column {
                Text(
                    "Bonjour, $nomUtilisateur",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 27.sp,
                    color = VertFonce
                )
                Text(
                    "On cuisine quoi aujourd’hui ?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    icon = Icons.Outlined.Kitchen,
                    valeur = frais,
                    libelle = "aliments au frais",
                    tint = VertFonce,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.Schedule,
                    valeur = aUtiliserVite,
                    libelle = "à utiliser vite",
                    tint = Ambre,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { UrgentCard(urgents = urgents, onVoirFrigo = onVoirFrigo) }

        item { AddCard(onClick = onVoirFrigo) }

        if (recents.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dans ton frigo", fontWeight = FontWeight.Bold, color = VertFonce, fontSize = 19.sp)
                    TextButton(onClick = onVoirFrigo) { Text("Tout voir  ›", color = Color.Gray) }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    recents.forEach { RecentCard(it, Modifier.weight(1f)) }
                    if (recents.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(hasAlert: Boolean, onBellClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(Icons.Outlined.Kitchen, contentDescription = null, tint = VertFonce, modifier = Modifier.size(29.dp))
            Text("FridgeMate", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = VertFonce)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                IconButton(onClick = onBellClick) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Alertes", tint = VertFonce)
                }
                if (hasAlert) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Ambre)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VertFonce),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White)
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    valeur: Int,
    libelle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(32.dp))
            Column {
                Text("$valeur", fontWeight = FontWeight.Bold, fontSize = 23.sp)
                Text(libelle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun UrgentCard(urgents: List<ProduitModel>, onVoirFrigo: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Creme)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("À consommer bientôt", fontWeight = FontWeight.Bold, color = Ambre)
            Spacer(Modifier.height(10.dp))

            val premier = urgents.firstOrNull()
            if (premier == null) {
                Text(
                    "Rien d'urgent : tout est encore frais.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onVoirFrigo),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProduitImage(
                        url = premier.image_url,
                        nom = premier.nom,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Column(Modifier.weight(1f)) {
                        Text(premier.nom, fontWeight = FontWeight.Bold, color = VertFonce)
                        Text(
                            "×${premier.nombre} · ${premier.quantite} ${premier.unite.raw} " +
                                    "(${texteExpiration(premier.date_expiration)})",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 10.dp),
                    color = Ambre.copy(alpha = 0.25f)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onVoirFrigo) {
                        Text("Voir les ${urgents.size} aliments", color = Ambre, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Ambre)
                }
            }
        }
    }
}

@Composable
private fun AddCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = VertFonce
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = Color(0xFFF5B46F), modifier = Modifier.size(38.dp))
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Que puis-je cuisiner ?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Des idées avec ce que tu as.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun RecentCard(p: ProduitModel, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column {
            ProduitImage(
                url = p.image_url,
                nom = p.nom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(p.nom, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(
                    "×${p.nombre} · ${p.quantite} ${p.unite.raw}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatutChip(p.statut)
            }
        }
    }
}


@Composable
internal fun ProduitImage(url: String?, nom: String, modifier: Modifier = Modifier) {
    if (url.isNullOrBlank()) {
        Box(modifier.background(VertClair), contentAlignment = Alignment.Center) {
            Text(nom.take(1).uppercase(), color = VertFonce, fontWeight = FontWeight.Bold)
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = nom,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Composable
internal fun StatutChip(statut: Statut) {
    val (fond, texte) = when (statut) {
        Statut.Frais -> VertClair to VertFonce
        Statut.BPerime -> Creme to Ambre
        Statut.Perime -> RougeFond to Rouge
    }
    Surface(color = fond, shape = RoundedCornerShape(50)) {
        Text(
            statut.raw,
            color = texte,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
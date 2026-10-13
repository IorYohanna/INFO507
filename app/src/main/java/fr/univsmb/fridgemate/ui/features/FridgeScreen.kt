package fr.univsmb.fridgemate.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.univsmb.fridgemate.domain.data.Statut
import fr.univsmb.fridgemate.model.ProduitModel
import fr.univsmb.fridgemate.model.texteExpiration

@Composable
fun FridgeScreen(
    vm: FridgeViewModel,
    onAjouter: () -> Unit,
    onModifier: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val produits by vm.produits.collectAsState()
    var selectedCode by remember { mutableStateOf<String?>(null) }
    val selectedProduct = produits.firstOrNull { it.code == selectedCode }
    var recherche by remember { mutableStateOf("") }
    var filtre by remember { mutableStateOf("Tous") }
    val filtres = listOf("Tous", "Légumes", "Viandes", "Laitiers")
    val visibles = produits.filter { p ->
        val texte = "${p.nom} ${p.categorie.orEmpty()} ${p.marque.orEmpty()}"
        val correspondRecherche = texte.contains(recherche, ignoreCase = true)
        val categorie = p.categorie.orEmpty().lowercase()
        val nom = p.nom.lowercase()
        val correspondFiltre = when (filtre) {
            "Légumes" -> listOf("légume", "legume", "fruit", "tomate").any { it in categorie || it in nom }
            "Viandes" -> listOf("viande", "poulet", "boeuf", "bœuf", "poisson").any { it in categorie || it in nom }
            "Laitiers" -> listOf("lait", "yaourt", "yogurt", "fromage").any { it in categorie || it in nom }
            else -> true
        }
        correspondRecherche && correspondFiltre
    }.sortedWith(compareBy<ProduitModel> { it.statut == Statut.Frais }.thenBy { it.date_expiration })
    val urgent = produits.count { it.statut != Statut.Frais }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFFAFCF9))) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 15.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Mon frigo", fontFamily = FontFamily.Serif, fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp, color = VertFonce)
                    Text("${produits.size} aliments · Tout est sous contrôle", fontSize = 12.sp, color = Color(0xFF60766B))
                }
            }
            item {
                OutlinedTextField(
                    value = recherche, onValueChange = { recherche = it },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    placeholder = { Text("Rechercher un aliment", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null, tint = VertFonce) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VertFonce, unfocusedBorderColor = Color(0xFFE2E9E2),
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                    )
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    filtres.forEach { choix ->
                        val selected = filtre == choix
                        Surface(
                            modifier = Modifier.weight(1f).clickable { filtre = choix },
                            shape = RoundedCornerShape(30.dp),
                            color = if (selected) VertFonce else Color.Transparent,
                            border = if (selected) null else BorderStroke(1.dp, Color(0xFFDDE7DF))
                        ) {
                            Text(choix, modifier = Modifier.padding(vertical = 9.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = if (selected) Color.White else VertFonce,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                        }
                    }
                }
            }
            if (urgent > 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(Creme).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Outlined.Schedule, null, tint = Ambre, modifier = Modifier.size(20.dp))
                        Text("$urgent aliments à utiliser bientôt", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFF92550F))
                    }
                }
            }
            item {
                Text("Triés par date d’expiration", fontSize = 12.sp, color = Color(0xFF849087),
                    modifier = Modifier.padding(top = 4.dp))
            }
            if (visibles.isEmpty()) {
                item { Text("Aucun aliment trouvé.", color = Color.Gray, modifier = Modifier.padding(20.dp)) }
            }
            items(visibles, key = { it.code }) { p ->
                var menuOuvert by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedCode = p.code }, shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(9.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                        ProduitImage(p.image_url, p.nom,
                            Modifier.size(66.dp).clip(RoundedCornerShape(10.dp)))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(p.nom, fontSize = 15.sp, color = VertFonce, fontWeight = FontWeight.Bold,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${p.quantite} ${p.unite.raw} · ×${p.nombre}", fontSize = 12.sp,
                                color = Color(0xFF77877E))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(if (p.statut == Statut.Frais) Color(0xFF75AA66) else Ambre))
                                Text(texteExpiration(p.date_expiration), fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (p.statut == Statut.Frais) VertFonce else Ambre)
                            }
                        }
                        Box {
                            IconButton(onClick = { menuOuvert = true }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Actions", tint = VertFonce)
                            }
                            DropdownMenu(expanded = menuOuvert, onDismissRequest = { menuOuvert = false }) {
                                DropdownMenuItem(text = { Text("Modifier") }, onClick = { menuOuvert = false; onModifier(p.code) })
                                DropdownMenuItem(text = { Text("Consommer une unité") }, onClick = { menuOuvert = false; vm.consommerUn(p.code) })
                                DropdownMenuItem(text = { Text("Supprimer") }, onClick = { menuOuvert = false; vm.supprimer(p.code) })
                            }
                        }
                    }
                }
            }
            if (produits.isNotEmpty()) {
                item {
                    TextButton(onClick = vm::toutSupprimer, modifier = Modifier.fillMaxWidth()) {
                        Text("Vider le frigo", color = Ambre)
                    }
                }
            }
        }
        selectedProduct?.let { produit ->
            ProductDetailsSheet(
                produit = produit,
                onDismiss = { selectedCode = null },
                onModifier = {
                    selectedCode = null
                    onModifier(produit.code)
                }
            )
        }
        ExtendedFloatingActionButton(
            onClick = onAjouter, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = VertFonce, contentColor = Color.White,
            shape = RoundedCornerShape(30.dp),
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("Ajouter", fontWeight = FontWeight.Bold) }
        )
    }
}

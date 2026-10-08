package fr.univsmb.fridgemate.model

import fr.univsmb.fridgemate.domain.data.Nutriscore

data class ProduitInfo(
    val code: String,
    val nom: String,
    val marque: String?,
    val categorie: String?,
    val imageUrl: String?,
    val nutriscore: Nutriscore,
    val quantity: String?
)
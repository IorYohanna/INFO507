package fr.univsmb.fridgemate.remote.dto

import fr.univsmb.fridgemate.domain.data.Nutriscore

data class OpenFoodFactsInfo(
    val code: String,
    val nom: String,
    val marque: String?,
    val categorie: String?,
    val imageUrl: String?,
    val nutriscore: Nutriscore,
    val quantity: String?
)
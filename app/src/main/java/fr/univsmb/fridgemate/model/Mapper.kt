package fr.univsmb.fridgemate.model

import fr.univsmb.fridgemate.domain.data.Statut
import fr.univsmb.fridgemate.domain.data.Unite
import fr.univsmb.fridgemate.remote.dto.OpenFoodFactsInfo
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

private val QUANTITE_REGEX =
    Regex("""(\d+(?:[.,]\d+)?)\s*(kg|mg|ml|cl|g|l)(?![a-z])""", RegexOption.IGNORE_CASE)
fun parseQuantite(texte: String?): Pair<Int, Unite> {
    val match = texte?.let { QUANTITE_REGEX.find(it) } ?: return 1 to Unite.unite
    val valeur = match.groupValues[1].replace(',', '.').toDoubleOrNull()
        ?: return 1 to Unite.unite

    return when (match.groupValues[2].lowercase()) {
        "kg" -> (valeur * 1000).roundToInt() to Unite.gramme
        "g"  -> valeur.roundToInt() to Unite.gramme
        "mg" -> maxOf(1, (valeur / 1000).roundToInt()) to Unite.gramme
        "l"  -> (valeur * 100).roundToInt() to Unite.centilitre
        "cl" -> valeur.roundToInt() to Unite.centilitre
        "ml" -> maxOf(1, (valeur / 10).roundToInt()) to Unite.centilitre
        else -> 1 to Unite.unite
    }
}

fun OpenFoodFactsInfo.toProduitModel(
    nombre: Int,
    dateExpiration: String
): ProduitModel {
    val (quantite, unite) = parseQuantite(quantity)
    return ProduitModel(
        code = code,
        nom = nom,
        marque = marque,
        categorie = categorie,
        quantite = quantite,
        unite = unite,
        nombre = nombre,
        nutriscore = nutriscore,
        image_url = imageUrl,
        date_ajout = LocalDate.now().toString(),
        date_expiration = dateExpiration,
        statut = calculerStatut(dateExpiration),
        notifie = false
    )
}

fun calculerStatut(dateExpiration: String): Statut {
    val jours = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(dateExpiration))
    return when {
        jours < 0 -> Statut.Perime
        jours <= 3 -> Statut.BPerime
        else -> Statut.Frais
    }
}
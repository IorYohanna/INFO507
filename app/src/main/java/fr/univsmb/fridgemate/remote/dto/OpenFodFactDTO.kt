package fr.univsmb.fridgemate.remote.dto

import fr.univsmb.fridgemate.domain.data.Nutriscore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponse(
    val status: Int,
    val product: OpenFoodFactsProductDto? = null
)

@Serializable
data class OpenFoodFactsProductDto(

    @SerialName("product_name")
    val productName: String? = null,

    val brands: String? = null,

    @SerialName("nutriscore_grade")
    val nutriscoreGrade: String? = null,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("categories_tags_fr")
    val categoriesTags: List<String>? = null,

    val quantity: String? = null
)

fun OpenFoodFactsProductDto.toProduitInfo(code: String) = OpenFoodFactsInfo(
    code = code,
    nom = productName?.takeIf { it.isNotBlank() } ?: "Produit inconnu",
    marque = brands?.substringBefore(",")?.trim(),
    categorie = categoriesTags?.firstOrNull { !it.contains(":") },
    imageUrl = imageUrl,
    quantity = quantity,
    nutriscore = when (nutriscoreGrade?.lowercase()) {
        "a" -> Nutriscore.A
        "b" -> Nutriscore.B
        "c" -> Nutriscore.C
        "d" -> Nutriscore.D
        "e" -> Nutriscore.E
        else -> Nutriscore.UNKOWN
    }
)
package fr.univsmb.fridgemate.remote.dto

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

    @SerialName("image_front_url")
    val imageUrl: String? = null,

    val categories: String? = null
)
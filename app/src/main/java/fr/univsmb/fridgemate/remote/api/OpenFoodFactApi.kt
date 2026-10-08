package fr.univsmb.fridgemate.remote.api

import fr.univsmb.fridgemate.remote.HttpClientFactory
import fr.univsmb.fridgemate.remote.dto.OpenFoodFactsResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

object OpenFoodFactApi {
    private const val BASE_URL = "https://world.openfoodfacts.org/api/v2/product"

    private val client = HttpClientFactory.create()

    suspend fun fetchProductDetail(code: String): OpenFoodFactsResponse =
        client.get("$BASE_URL/$code.json") {
            parameter(
                "fields",
                "code,product_name,brands,categories_tags_fr,quantity,nutriscore_grade,image_url"
            )
        }.body()
}
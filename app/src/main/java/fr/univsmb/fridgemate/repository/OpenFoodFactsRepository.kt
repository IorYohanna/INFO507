package fr.univsmb.fridgemate.repository

import fr.univsmb.fridgemate.remote.api.OpenFoodFactApi
import fr.univsmb.fridgemate.remote.dto.OpenFoodFactsInfo
import fr.univsmb.fridgemate.remote.dto.toOpenFoodFactsInfo
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import java.io.IOException
import kotlinx.serialization.SerializationException

class OpenFoodFactRepository(
    private val api: OpenFoodFactApi = OpenFoodFactApi
) {
    suspend fun fetchProduit(code: String): Result<OpenFoodFactsInfo?> = try {
        val response = api.fetchProductDetail(code)
        Result.success(
            if (response.status == 1) response.product?.toOpenFoodFactsInfo(code) else null
        )
    } catch (e: ResponseException) {
        if (e.response.status == HttpStatusCode.NotFound) Result.success(null)
        else Result.failure(e)
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: SerializationException) {
        Result.failure(e)
    }
}
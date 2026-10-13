package fr.univsmb.fridgemate

import fr.univsmb.fridgemate.domain.data.Nutriscore
import fr.univsmb.fridgemate.remote.dto.OpenFoodFactsProductDto
import fr.univsmb.fridgemate.remote.dto.toOpenFoodFactsInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OpenFoodFactsMappingTest {
    @Test
    fun conserveLesMarquesEtLesCategoriesAvecPrefixe() {
        val info = OpenFoodFactsProductDto(
            brands = "Marque A, Marque B",
            categoriesTags = listOf("fr:produits-laitiers", "yaourts", "fr:yaourts", ""),
            nutriscoreGrade = "b"
        ).toOpenFoodFactsInfo("123")

        assertEquals("Marque A, Marque B", info.marque)
        assertEquals("produits laitiers, yaourts", info.categorie)
        assertEquals(Nutriscore.B, info.nutriscore)
    }

    @Test
    fun lesInformationsAbsentesRestentNonRenseignees() {
        val info = OpenFoodFactsProductDto(
            brands = " ",
            categoriesTags = emptyList()
        ).toOpenFoodFactsInfo("123")

        assertNull(info.marque)
        assertNull(info.categorie)
        assertEquals(Nutriscore.UNKOWN, info.nutriscore)
    }
}

package fr.univsmb.fridgemate

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.univsmb.fridgemate.domain.data.Nutriscore
import fr.univsmb.fridgemate.domain.data.Statut
import fr.univsmb.fridgemate.domain.data.Unite
import fr.univsmb.fridgemate.local.ProduitDatabase
import fr.univsmb.fridgemate.local.entity.ProduitEntity
import fr.univsmb.fridgemate.repository.ProduitRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProduitRepositoryTest {
    private lateinit var database: ProduitDatabase
    private lateinit var repository: ProduitRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ProduitDatabase::class.java
        ).build()
        repository = ProduitRepository(database.produitDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun consommerDeuxExemplairesGardeLeDernierPuisLeSupprime() = runBlocking {
        val produit = produit(nombre = 2)
        val autre = produit(nombre = 3).copy(code = "autre")
        database.produitDao().upsertProduit(produit)
        database.produitDao().upsertProduit(autre)

        repository.consommerUn(produit.code)

        assertEquals(produit.copy(nombre = 1), database.produitDao().getProduitByCode(produit.code))
        assertEquals(autre, database.produitDao().getProduitByCode(autre.code))

        repository.consommerUn(produit.code)

        assertNull(database.produitDao().getProduitByCode(produit.code))
        repository.consommerUn(produit.code)
        assertNull(database.produitDao().getProduitByCode(produit.code))
    }

    @Test
    fun deuxRetraitsSimultanesSurTroisExemplairesEnLaissentUn() = runBlocking {
        val produit = produit(nombre = 3)
        database.produitDao().upsertProduit(produit)

        coroutineScope {
            List(2) { async { repository.consommerUn(produit.code) } }.awaitAll()
        }

        assertEquals(produit.copy(nombre = 1), database.produitDao().getProduitByCode(produit.code))
    }

    private fun produit(nombre: Int) = ProduitEntity(
        code = "test",
        nom = "Lait",
        nutriscore = Nutriscore.A,
        marque = null,
        categorie = null,
        image_url = null,
        quantite = 100,
        unite = Unite.centilitre,
        nombre = nombre,
        date_ajout = "2026-10-12",
        date_expiration = "2026-10-20",
        statut = Statut.Frais,
        notifie = false
    )
}

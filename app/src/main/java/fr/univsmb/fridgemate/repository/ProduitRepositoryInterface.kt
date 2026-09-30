package fr.univsmb.fridgemate.repository

import fr.univsmb.fridgemate.model.ProduitModel
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ProduitRepositoryInterface {
    fun getAllProduits(): Flow<List<ProduitModel>>

    suspend fun getProduitByCode(code: String) : ProduitModel?

    fun rechercherProduit(recherche: String): Flow<List<ProduitModel>>

    fun getProduitsExpires(date: Date): Flow<List<ProduitModel>>

    fun getProduitsBientotExpires(
        dateDebut: Date,
        DateExpiration: Date
    ): Flow<List<ProduitModel>>

    suspend fun ajouterProduit(produit: ProduitModel)

    suspend fun modifierProduit(produit: ProduitModel)

    suspend fun supprimerProduitByCode(code: String)

    suspend fun  supprimerProduit(produit: ProduitModel)

    suspend fun supprimerTousLesProduits()

}
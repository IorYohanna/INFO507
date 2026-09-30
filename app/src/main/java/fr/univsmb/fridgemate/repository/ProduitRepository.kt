package fr.univsmb.fridgemate.repository

import fr.univsmb.fridgemate.local.dao.ProduitDao
import fr.univsmb.fridgemate.local.entity.ProduitEntity
import fr.univsmb.fridgemate.model.ProduitModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProduitRepository (
    private val produitDao: ProduitDao
) {
    fun getAllProduits(): Flow<List<ProduitModel>> {
        return produitDao.getAllProduits()
            .map {
                produits -> produits.map{it.toProduitModel()}
            }
    }

    suspend fun  getProduitByCode(code: String) : ProduitModel? {
        return produitDao.getProduitByCode(code)
    }
}
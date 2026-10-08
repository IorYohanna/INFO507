package fr.univsmb.fridgemate.repository

import fr.univsmb.fridgemate.local.dao.ProduitDao
import fr.univsmb.fridgemate.local.entity.ProduitEntity
import fr.univsmb.fridgemate.model.ProduitModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProduitRepository(
    private val produitDao: ProduitDao
) {
    fun getAllProduits(): Flow<List<ProduitModel>> =
        produitDao.getAllProduits().map { list -> list.map { it.toProduitModel() } }

    suspend fun getProduitByCode(code: String): ProduitModel? =
        produitDao.getProduitByCode(code)?.toProduitModel()

    suspend fun upsertProduit(produit: ProduitModel) {
        produitDao.upsertProduit(ProduitEntity.fromProduitModel(produit))
    }

    suspend fun getProduitsANotifier(limite: String): List<ProduitModel> =
        produitDao.getProduitsANotifier(limite).map { it.toProduitModel() }

    suspend fun marquerNotifie(code: String) = produitDao.marquerNotifie(code)

    suspend fun ajouterOuIncrementer(produit: ProduitModel) {
        val existant = produitDao.getProduitByCode(produit.code)
        if (existant == null) {
            produitDao.upsertProduit(ProduitEntity.fromProduitModel(produit))
        } else {
            produitDao.upsertProduit(
                existant.copy(
                    nombre = existant.nombre + produit.nombre,
                    date_expiration = minOf(existant.date_expiration, produit.date_expiration)
                )
            )
        }
    }
    suspend fun consommerUn(code: String) {
        produitDao.retirerUn(code)
        produitDao.supprimerSiDernier(code)
    }

    suspend fun supprimerProduitByCode(code: String) = produitDao.deleteProduitByCode(code)

    suspend fun supprimerTousLesProduits() = produitDao.deleteAllProduits()
}
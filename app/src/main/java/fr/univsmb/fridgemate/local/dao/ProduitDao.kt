package fr.univsmb.fridgemate.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import fr.univsmb.fridgemate.domain.data.Statut
import fr.univsmb.fridgemate.local.entity.ProduitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduitDao {
    @Query("SELECT * FROM produit ORDER BY date_expiration")
    fun getAllProduits(): Flow<List<ProduitEntity>>

    @Query("SELECT * FROM produit WHERE code = :code LIMIT 1")
    suspend fun getProduitByCode(code: String): ProduitEntity?

    @Upsert
    suspend fun upsertProduit(produit: ProduitEntity)

    @Query("SELECT * FROM produit WHERE date_expiration <= :limite AND notifie = 0")
    suspend fun getProduitsANotifier(limite: String): List<ProduitEntity>

    @Query("UPDATE produit SET notifie = 1 WHERE code = :code")
    suspend fun marquerNotifie(code: String)

    @Query("UPDATE produit SET statut = :statut WHERE code = :code")
    suspend fun updateStatut(code: String, statut: Statut)

    @Query("UPDATE produit SET nombre = nombre - 1 WHERE code = :code AND nombre > 1")
    suspend fun retirerUn(code: String)

    @Query("DELETE FROM produit WHERE code = :code AND nombre <= 1")
    suspend fun supprimerSiDernier(code: String)

    @Query("DELETE FROM produit WHERE code = :code")
    suspend fun deleteProduitByCode(code: String)

    @Query("DELETE FROM produit")
    suspend fun deleteAllProduits()
}
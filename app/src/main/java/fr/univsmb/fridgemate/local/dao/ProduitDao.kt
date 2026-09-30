package fr.univsmb.fridgemate.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import fr.univsmb.fridgemate.local.entity.ProduitEntity
import fr.univsmb.fridgemate.model.ProduitModel

import kotlinx.coroutines.flow.Flow

@Dao
interface ProduitDao {
    @Query("SELECT * FROM produit ORDER BY date_expiration")
    fun getAllProduits() : Flow<List<ProduitEntity>>

    @Query("SELECT * FROM  produit WHERE code = :code LIMIT 1")
    suspend fun getProduitByCode(code:String): ProduitEntity?

    @Upsert
    suspend fun upsertProduit(produit: ProduitEntity)

    @Query("DELETE FROM produit WHERE code = :code")
    suspend fun deleteProduitByCode(code: String)

    @Query("DELETE FROM produit")
    suspend fun deleteAllProduits()

}
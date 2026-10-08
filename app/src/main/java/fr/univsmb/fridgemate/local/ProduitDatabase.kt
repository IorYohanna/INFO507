package fr.univsmb.fridgemate.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.univsmb.fridgemate.local.dao.ProduitDao
import fr.univsmb.fridgemate.local.entity.ProduitEntity


@Database(entities = [ProduitEntity::class], version = 1, exportSchema = false)
abstract class ProduitDatabase : RoomDatabase() {
    abstract fun produitDao(): ProduitDao

    companion object {
        private const val DB_NAME = "produit.db"

        @Volatile
        private var INSTANCE: ProduitDatabase? = null

        fun get(context: Context): ProduitDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, ProduitDatabase::class.java, DB_NAME
                ).build().also { INSTANCE = it }
            }
    }
}

object ProduitDatabaseHolder {
    var database: ProduitDatabase? = null

    fun initialize(context: Context) {
        database = ProduitDatabase.get(context)
        Log.d("Room", database.toString())
    }
}
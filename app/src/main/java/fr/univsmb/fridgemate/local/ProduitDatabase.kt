package fr.univsmb.fridgemate.local

import android.content.Context
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.univsmb.fridgemate.local.dao.ProduitDao
import fr.univsmb.fridgemate.local.entity.ProduitEntity


@Database(entities = [ProduitEntity::class], version = 1, exportSchema = false )
abstract class ProduitDatabase : RoomDatabase() {
    abstract fun hourlyForecastDao() : ProduitDao
    companion object {
        private const val DB_NAME = "produit.db"

        @Volatile
        private var INSTANCE: ProduitDatabase? = null

        fun get(context: Context): ProduitDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    Room.databaseBuilder(context, ProduitDatabase::class.java, DB_NAME).build()
                        .also { INSTANCE = it }
                }
            }
            return INSTANCE!!
        }
    }
}

data object ProduitDatabaseHolder {
    var database: ProduitDatabase? = null

    fun initialize(context: Context) {
        database = ProduitDatabase.get(context)
    }
}

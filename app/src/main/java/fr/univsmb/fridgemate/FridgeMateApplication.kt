package fr.univsmb.fridgemate

import android.app.Application
import fr.univsmb.fridgemate.local.ProduitDatabaseHolder

class FridgeMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ProduitDatabaseHolder.initialize(this)
    }
}

package fr.univsmb.fridgemate

import android.app.Application
import fr.univsmb.fridgemate.local.ProduitDatabaseHolder
import fr.univsmb.fridgemate.notifications.ExpirationWorker

class FridgeMateApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ProduitDatabaseHolder.initialize(this)
        ExpirationWorker.schedule(this)
        ExpirationWorker.checkNow(this)
    }
}

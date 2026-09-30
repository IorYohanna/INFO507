package fr.univsmb.fridgemate.model

import fr.univsmb.fridgemate.domain.data.Nutriscore
import fr.univsmb.fridgemate.domain.data.Statut
import fr.univsmb.fridgemate.domain.data.Unite
import java.util.Date
data class ProduitModel (
    val code : String,
    val nom : String,
    val marque : String?,
    val categorie : String?,
    val quantite : Int,
    val unite : Unite,
    val nutriscore : Nutriscore,
    val image_url : String?,
    val date_ajout : String,
    val date_expiration : String,
    val statut : Statut,
    val notifie : Boolean
)

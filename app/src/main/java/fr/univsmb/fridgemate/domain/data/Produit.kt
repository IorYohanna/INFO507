package fr.univsmb.fridgemate.domain.data

import java.util.Date

data class Produit(
    val code : Int,
    val product_name : String,
    val brands : String,
    val category : String,
    val quantity : Int,
    val unite : Unite,
    val nutriscore : Nutriscore,
    val image_url : String,
    val date_ajout : Date,
    val date_expiration : Date,
    val statut : Statut,
    val notifie : Boolean
)
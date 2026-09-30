package fr.univsmb.fridgemate.local.model


data class ProduitModel (
    val barcode: String,
    val nom: String,
    val marque: String?,
    val categorie: String?,
    val imageUrl: String?,
    val quantiteStock: Double,
    val unite: String,
    val dateAjout: Long,
    val dateExpiration: Long,
    val notifie: Boolean
)
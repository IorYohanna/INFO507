package fr.univsmb.fridgemate.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.univsmb.fridgemate.domain.data.Nutriscore
import fr.univsmb.fridgemate.domain.data.Statut
import fr.univsmb.fridgemate.domain.data.Unite
import fr.univsmb.fridgemate.model.ProduitModel

@Entity(tableName = "produit")
data class ProduitEntity(
    @PrimaryKey @ColumnInfo("code") val code: String,
    @ColumnInfo("nom") val nom: String,
    @ColumnInfo("nutriscore") val nutriscore: Nutriscore,
    @ColumnInfo("marque") val marque: String?,
    @ColumnInfo("categorie") val categorie: String?,
    @ColumnInfo("image_url") val image_url: String?,
    @ColumnInfo("quantite") val quantite: Int,
    @ColumnInfo("unite") val unite: Unite,
    @ColumnInfo("nombre", defaultValue = "1") val nombre: Int = 1,
    @ColumnInfo("date_ajout") val date_ajout: String,
    @ColumnInfo("date_expiration") val date_expiration: String,
    @ColumnInfo("statut") val statut: Statut,
    @ColumnInfo("notifie") val notifie: Boolean
) {
    fun toProduitModel() = ProduitModel(
        code = code,
        nom = nom,
        marque = marque,
        categorie = categorie,
        quantite = quantite,
        unite = unite,
        nombre = nombre,
        nutriscore = nutriscore,
        image_url = image_url,
        date_ajout = date_ajout,
        date_expiration = date_expiration,
        statut = statut,
        notifie = notifie
    )

    companion object {
        fun fromProduitModel(produit: ProduitModel) = ProduitEntity(
            code = produit.code,
            nom = produit.nom,
            marque = produit.marque,
            categorie = produit.categorie,
            quantite = produit.quantite,
            unite = produit.unite,
            nombre = produit.nombre,
            nutriscore = produit.nutriscore,
            image_url = produit.image_url,
            date_ajout = produit.date_ajout,
            date_expiration = produit.date_expiration,
            statut = produit.statut,
            notifie = produit.notifie
        )
    }
}
package fr.univsmb.fridgemate.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.univsmb.fridgemate.local.model.ProduitModel


@Entity(tableName = "aliment")
data class ProduitEntity(
    @PrimaryKey @ColumnInfo("barcode") val barcode: String,
    @ColumnInfo("nom") val nom: String,
    @ColumnInfo("marque") val marque: String?,
    @ColumnInfo("categorie") val categorie: String?,
    @ColumnInfo("image_url") val imageUrl: String?,
    @ColumnInfo("quantite_stock") val quantiteStock: Double,
    @ColumnInfo("unite") val unite: String,
    @ColumnInfo("date_ajout") val dateAjout: Long,
    @ColumnInfo("date_expiration") val dateExpiration: Long,
    @ColumnInfo("notifie") val notifie: Boolean
) {
    fun toAliment(): ProduitModel {
        return ProduitModel(
            barcode = barcode,
            nom = nom,
            marque = marque,
            categorie = categorie,
            imageUrl = imageUrl,
            quantiteStock = quantiteStock,
            unite = unite,
            dateAjout = dateAjout,
            dateExpiration = dateExpiration,
            notifie = notifie
        )
    }

    companion object {
        fun FromProduitModel (aliment: ProduitModel): ProduitEntity {
            return ProduitEntity(
                barcode = aliment.barcode,
                nom = aliment.nom,
                marque = aliment.marque,
                categorie = aliment.categorie,
                imageUrl = aliment.imageUrl,
                quantiteStock = aliment.quantiteStock,
                unite = aliment.unite,
                dateAjout = aliment.dateAjout,
                dateExpiration = aliment.dateExpiration,
                notifie = aliment.notifie
            )
        }
    }
}
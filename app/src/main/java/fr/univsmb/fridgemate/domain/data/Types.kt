package fr.univsmb.fridgemate.domain.data

enum class Nutriscore {
    A,B,C,D,E,UNKOWN
}

enum class Statut(val raw : String) {
    Frais("Frais"),
    BPerime("Bientôt Perimé"),
    Perime("Perimé")
}

enum class  Unite(val raw: String) {
    unite("Unité"),
    gramme("Grammes"),
    centilitre("Centilitre")
}
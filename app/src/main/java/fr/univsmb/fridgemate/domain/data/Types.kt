package fr.univsmb.fridgemate.domain.data

enum class Nutriscore(val raw : String) {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E") //955
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
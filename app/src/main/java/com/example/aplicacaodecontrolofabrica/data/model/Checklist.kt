package com.example.aplicacaodecontrolofabrica.data.model

enum class TipoChecklistUi {
    MONTAGEM,
    EMBALAGEM,
    CONTROLO,
    DESCONHECIDO
}

data class Checklist(
    val id: Int,
    val nome: String,
    val descricao: String = "",
    val tipo: TipoChecklistUi = TipoChecklistUi.DESCONHECIDO
)

fun TipoChecklistUi.label(): String = when (this) {
    TipoChecklistUi.MONTAGEM -> "Montagem"
    TipoChecklistUi.EMBALAGEM -> "Embalagem"
    TipoChecklistUi.CONTROLO -> "Controlo"
    TipoChecklistUi.DESCONHECIDO -> "Desconhecido"
}

fun tipoChecklistFromApi(value: Int?): TipoChecklistUi = when (value) {
    1 -> TipoChecklistUi.MONTAGEM
    2 -> TipoChecklistUi.EMBALAGEM
    3 -> TipoChecklistUi.CONTROLO
    else -> TipoChecklistUi.DESCONHECIDO
}
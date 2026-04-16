package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class ChecklistDto(
    @SerializedName(
        value = "IDChecklist",
        alternate = ["idChecklist", "IdChecklist", "id", "Id", "ID"]
    )
    val idChecklist: Int? = null,

    @SerializedName(
        value = "Nome",
        alternate = ["nome"]
    )
    val nome: String? = null,

    @SerializedName(
        value = "Tipo",
        alternate = ["tipo"]
    )
    val tipo: Int? = null
)

/**
 * Mantido apenas para compatibilidade com UI antiga.
 */
data class OrdemChecklistDto(
    val idChecklist: Int,
    val nome: String,
    val tipo: String,
    val estado: String
)

data class ChecklistsStatusDto(
    @SerializedName(
        value = "ordemId",
        alternate = ["OrdemId", "IDOrdemProducao", "idOrdemProducao", "IdOrdemProducao"]
    )
    val ordemId: Int? = null,

    @SerializedName(
        value = "montagem",
        alternate = ["Montagem"]
    )
    val montagem: List<ChecklistItemDto> = emptyList(),

    @SerializedName(
        value = "embalagem",
        alternate = ["Embalagem"]
    )
    val embalagem: List<ChecklistItemDto> = emptyList(),

    @SerializedName(
        value = "controlo",
        alternate = ["Controlo"]
    )
    val controlo: List<ChecklistItemDto> = emptyList()
)

data class ChecklistItemDto(
    @SerializedName(
        value = "IDChecklist",
        alternate = ["idChecklist", "IdChecklist", "id", "Id", "ID"]
    )
    val idChecklist: Int? = null,

    @SerializedName(
        value = "Nome",
        alternate = ["nome"]
    )
    val nome: String? = null,

    @SerializedName(
        value = "Tipo",
        alternate = ["tipo"]
    )
    val tipo: Int? = null,

    @SerializedName(
        value = "Verificado",
        alternate = [
            "verificado",
            "Incluido",
            "incluido",
            "ControloFinal",
            "controloFinal",
            "value",
            "Value"
        ]
    )
    val value: Int? = null
)
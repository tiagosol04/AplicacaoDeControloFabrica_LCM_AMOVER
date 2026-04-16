package com.example.aplicacaodecontrolofabrica.data.model

enum class CoberturaTecnicaUi {
    POR_INTEGRAR,
    GARANTIA,
    FORA_GARANTIA
}

data class HistoricoTecnicoUi(
    val ordemId: Int,
    val numeroOrdem: String,

    val motaId: Int? = null,
    val vin: String? = null,

    val modeloId: Int? = null,
    val clienteId: Int? = null,
    val paisDestino: String? = null,

    val dataConclusaoIso: String? = null,

    val totalServicos: Int = 0,
    val checklistsOk: Boolean = false,
    val unidadeConcluida: Boolean = false,

    val cobertura: CoberturaTecnicaUi = CoberturaTecnicaUi.POR_INTEGRAR,
    val resumoTecnico: String = "",

    val totalGarantias: Int = 0,
    val totalOcorrencias: Int = 0,
    val problemaRecorrente: Boolean = false
)

fun CoberturaTecnicaUi.label(): String = when (this) {
    CoberturaTecnicaUi.POR_INTEGRAR -> "Cobertura por integrar"
    CoberturaTecnicaUi.GARANTIA -> "Em garantia"
    CoberturaTecnicaUi.FORA_GARANTIA -> "Fora de garantia"
}
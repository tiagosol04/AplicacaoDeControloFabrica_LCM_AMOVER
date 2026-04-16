package com.example.aplicacaodecontrolofabrica.data.model

data class Mota(
    val id: Int,
    val numeroIdentificacao: String? = null,
    val cor: String? = null,
    val km: Double = 0.0,
    val estado: String = "EmProdução",
    val idModelo: Int? = null,
    val idOrdemProducao: Int? = null,
    val dataRegistoIso: String? = null,
    val dataCriacaoIso: String? = null,
    val dataModificacaoIso: String? = null
) {
    val vin: String
        get() = numeroIdentificacao.orEmpty().trim()

    val hasVin: Boolean
        get() = vin.isNotBlank()

    val vinDisplay: String
        get() = vin.ifBlank { "Por definir" }

    val corDisplay: String
        get() = cor?.takeIf { it.isNotBlank() } ?: "Por definir"
}
package com.example.aplicacaodecontrolofabrica.data.model

data class ModeloMota(
    val id: Int,
    val nomeModelo: String,
    val codigoProduto: String? = null,
    val cilindrada: Int? = null,
    val autonomia: Int? = null,
    val tipoMotorizacao: String? = null,
    val estado: String = "Ativo",
    val descricao: String? = null,
    val observacoes: String? = null,
    val dataInicioProducaoIso: String? = null,
    val dataLancamentoIso: String? = null,
    val dataDescontinuacaoIso: String? = null
) {
    val nomeDisplay: String
        get() = nomeModelo.ifBlank { "Modelo sem nome" }
}
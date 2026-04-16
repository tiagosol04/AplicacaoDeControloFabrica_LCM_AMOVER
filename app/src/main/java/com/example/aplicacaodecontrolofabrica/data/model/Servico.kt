package com.example.aplicacaodecontrolofabrica.data.model

enum class TipoServicoUi {
    MANUTENCAO,
    AVARIA,
    GARANTIA,
    INSPECAO,
    DIAGNOSTICO,
    PREPARACAO_ENTREGA,
    CAMPANHA_TECNICA,
    OUTRO
}

enum class EstadoServicoUi {
    ABERTO,
    EM_TRATAMENTO,
    CONCLUIDO,
    CANCELADO
}

enum class CoberturaServicoUi {
    POR_INTEGRAR,
    GARANTIA,
    FORA_GARANTIA
}

data class Servico(
    val id: Int,
    val idMota: Int,
    val idModelo: Int? = null,
    val tipo: TipoServicoUi = TipoServicoUi.OUTRO,
    val estado: EstadoServicoUi = EstadoServicoUi.ABERTO,
    val cobertura: CoberturaServicoUi = CoberturaServicoUi.POR_INTEGRAR,
    val descricao: String? = null,
    val dataCriacaoIso: String? = null,
    val dataConclusaoIso: String? = null,
    val tecnicoResponsavel: String? = null
)

fun TipoServicoUi.label(): String = when (this) {
    TipoServicoUi.MANUTENCAO -> "Manutenção"
    TipoServicoUi.AVARIA -> "Avaria"
    TipoServicoUi.GARANTIA -> "Garantia"
    TipoServicoUi.INSPECAO -> "Inspeção"
    TipoServicoUi.DIAGNOSTICO -> "Diagnóstico"
    TipoServicoUi.PREPARACAO_ENTREGA -> "Preparação / Entrega"
    TipoServicoUi.CAMPANHA_TECNICA -> "Campanha técnica"
    TipoServicoUi.OUTRO -> "Outro"
}

fun EstadoServicoUi.label(): String = when (this) {
    EstadoServicoUi.ABERTO -> "Aberto"
    EstadoServicoUi.EM_TRATAMENTO -> "Em tratamento"
    EstadoServicoUi.CONCLUIDO -> "Concluído"
    EstadoServicoUi.CANCELADO -> "Cancelado"
}

fun CoberturaServicoUi.label(): String = when (this) {
    CoberturaServicoUi.POR_INTEGRAR -> "Cobertura por integrar"
    CoberturaServicoUi.GARANTIA -> "Em garantia"
    CoberturaServicoUi.FORA_GARANTIA -> "Fora de garantia"
}
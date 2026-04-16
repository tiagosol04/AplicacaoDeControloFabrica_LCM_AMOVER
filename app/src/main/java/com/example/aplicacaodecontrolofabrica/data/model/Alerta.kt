package com.example.aplicacaodecontrolofabrica.data.model

enum class TipoAlertaUi {
    BLOQUEIO,
    MATERIAL_CRITICO,
    QUALIDADE,
    ATRASO,
    PRIORIDADE,
    TECNICO,
    GARANTIA,
    OPERACIONAL,
    OUTRO
}

enum class SeveridadeAlertaUi {
    BAIXA,
    MEDIA,
    ALTA,
    CRITICA
}

enum class EstadoAlertaUi {
    ABERTO,
    EM_ANALISE,
    EM_TRATAMENTO,
    RESOLVIDO,
    FECHADO
}

enum class OrigemAlertaUi {
    FABRICA,
    QUALIDADE,
    POS_VENDA,
    SISTEMA,
    API,
    MANUAL
}

data class Alerta(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val tipo: TipoAlertaUi = TipoAlertaUi.OPERACIONAL,
    val severidade: SeveridadeAlertaUi = SeveridadeAlertaUi.MEDIA,
    val estado: EstadoAlertaUi = EstadoAlertaUi.ABERTO,
    val origem: OrigemAlertaUi = OrigemAlertaUi.SISTEMA,

    val ordemId: Int? = null,
    val motaId: Int? = null,
    val modeloId: Int? = null,
    val clienteId: Int? = null,
    val vin: String? = null,

    val responsavelId: Int? = null,
    val responsavelNome: String? = null,

    val dataCriacaoIso: String? = null,
    val dataAtualizacaoIso: String? = null,
    val dataResolucaoIso: String? = null,

    val observacoes: String? = null
)

fun TipoAlertaUi.label(): String = when (this) {
    TipoAlertaUi.BLOQUEIO -> "Bloqueio"
    TipoAlertaUi.MATERIAL_CRITICO -> "Material crítico"
    TipoAlertaUi.QUALIDADE -> "Qualidade"
    TipoAlertaUi.ATRASO -> "Atraso"
    TipoAlertaUi.PRIORIDADE -> "Prioridade"
    TipoAlertaUi.TECNICO -> "Técnico"
    TipoAlertaUi.GARANTIA -> "Garantia"
    TipoAlertaUi.OPERACIONAL -> "Operacional"
    TipoAlertaUi.OUTRO -> "Outro"
}

fun SeveridadeAlertaUi.label(): String = when (this) {
    SeveridadeAlertaUi.BAIXA -> "Baixa"
    SeveridadeAlertaUi.MEDIA -> "Média"
    SeveridadeAlertaUi.ALTA -> "Alta"
    SeveridadeAlertaUi.CRITICA -> "Crítica"
}

fun EstadoAlertaUi.label(): String = when (this) {
    EstadoAlertaUi.ABERTO -> "Aberto"
    EstadoAlertaUi.EM_ANALISE -> "Em análise"
    EstadoAlertaUi.EM_TRATAMENTO -> "Em tratamento"
    EstadoAlertaUi.RESOLVIDO -> "Resolvido"
    EstadoAlertaUi.FECHADO -> "Fechado"
}

fun OrigemAlertaUi.label(): String = when (this) {
    OrigemAlertaUi.FABRICA -> "Fábrica"
    OrigemAlertaUi.QUALIDADE -> "Qualidade"
    OrigemAlertaUi.POS_VENDA -> "Pós-venda"
    OrigemAlertaUi.SISTEMA -> "Sistema"
    OrigemAlertaUi.API -> "API"
    OrigemAlertaUi.MANUAL -> "Manual"
}
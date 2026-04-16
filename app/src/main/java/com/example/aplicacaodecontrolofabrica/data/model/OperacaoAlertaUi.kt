package com.example.aplicacaodecontrolofabrica.data.model

enum class TipoAlertaOperacionalUi {
    BLOQUEIO,
    UNIDADE_PENDENTE,
    CONTROLO_PENDENTE,
    PRONTA_A_CONCLUIR,
    ATRASO,
    MATERIAL_CRITICO,
    PRIORIDADE,
    ATENCAO
}

data class OperacaoAlertaUi(
    val id: String,
    val tipo: TipoAlertaOperacionalUi,
    val titulo: String,
    val descricao: String,
    val ordemId: Int? = null,
    val vin: String? = null,
    val modeloId: Int? = null
)

data class CockpitResumoUi(
    val totalOrdens: Int = 0,
    val emProducao: Int = 0,
    val bloqueadas: Int = 0,
    val porArrancar: Int = 0,
    val concluidas: Int = 0,
    val emRisco: Int = 0,
    val prioritarias: Int = 0,
    val acoesImediatas: List<OperacaoAlertaUi> = emptyList(),
    val ordensPrioritarias: List<OrdemOperacionalUi> = emptyList()
)

fun TipoAlertaOperacionalUi.label(): String = when (this) {
    TipoAlertaOperacionalUi.BLOQUEIO -> "Bloqueio"
    TipoAlertaOperacionalUi.UNIDADE_PENDENTE -> "Unidade pendente"
    TipoAlertaOperacionalUi.CONTROLO_PENDENTE -> "Controlo pendente"
    TipoAlertaOperacionalUi.PRONTA_A_CONCLUIR -> "Pronta a concluir"
    TipoAlertaOperacionalUi.ATRASO -> "Atraso"
    TipoAlertaOperacionalUi.MATERIAL_CRITICO -> "Material crítico"
    TipoAlertaOperacionalUi.PRIORIDADE -> "Prioridade"
    TipoAlertaOperacionalUi.ATENCAO -> "Atenção"
}
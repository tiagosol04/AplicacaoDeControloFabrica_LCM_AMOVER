package com.example.aplicacaodecontrolofabrica.data.model

enum class EstadoChecklistExecucaoUi {
    POR_FAZER,
    EM_CURSO,
    CONCLUIDO
}

data class ChecklistExecucao(
    val id: Int,
    val idChecklist: Int,
    val idOrdemProducao: Int,
    val idResponsavel: Int? = null,
    val grupo: TipoChecklistUi = TipoChecklistUi.DESCONHECIDO,
    val estado: EstadoChecklistExecucaoUi = EstadoChecklistExecucaoUi.POR_FAZER,
    val concluido: Boolean = false,
    val dataCriacaoIso: String? = null,
    val dataModificacaoIso: String? = null
)

fun EstadoChecklistExecucaoUi.label(): String = when (this) {
    EstadoChecklistExecucaoUi.POR_FAZER -> "Por fazer"
    EstadoChecklistExecucaoUi.EM_CURSO -> "Em curso"
    EstadoChecklistExecucaoUi.CONCLUIDO -> "Concluído"
}
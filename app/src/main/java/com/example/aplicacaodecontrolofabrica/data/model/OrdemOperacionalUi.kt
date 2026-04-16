package com.example.aplicacaodecontrolofabrica.data.model

enum class EtapaOrdemUi {
    POR_ARRANCAR,
    MONTAGEM,
    EMBALAGEM,
    CONTROLO,
    PRONTA_EXPEDICAO,
    CONCLUIDA,
    BLOQUEADA
}

enum class StatusExecucaoUi {
    NORMAL,
    ATENCAO,
    CRITICO,
    BLOQUEADO,
    CONCLUIDO
}

enum class PrioridadeUi {
    NORMAL,
    ALTA,
    CRITICA
}

enum class AcaoPrincipalUi {
    INICIAR,
    REGISTAR_UNIDADE,
    FECHAR_CHECKLISTS,
    VALIDAR_CONTROLO,
    CONCLUIR,
    ANALISAR_BLOQUEIO,
    CONSULTAR
}

enum class FiltroOperacaoUi {
    TODAS,
    BLOQUEADAS,
    POR_ARRANCAR,
    EM_EXECUCAO,
    CONTROLO_PENDENTE,
    PRONTAS_A_CONCLUIR,
    CONCLUIDAS,
    PRIORITARIAS,
    EM_RISCO
}

typealias FiltroProducaoUi = FiltroOperacaoUi

data class OrdemOperacionalUi(
    val id: Int,
    val numeroOrdem: String,
    val estadoBase: Int,

    val modeloId: Int? = null,
    val clienteId: Int? = null,
    val encomendaId: Int? = null,
    val paisDestino: String? = null,

    val etapaAtual: EtapaOrdemUi,
    val statusExecucao: StatusExecucaoUi,

    val prioridade: PrioridadeUi,
    val urgente: Boolean,
    val bloqueada: Boolean,
    val motivoBloqueio: String? = null,

    val prontidao: ProntidaoOrdemUi,
    val motivoProntidao: String,
    val prontaParaArrancar: Boolean,

    val materialCritico: MaterialCriticoUi,
    val faltasCriticas: Int = 0,

    val temUnidadeRegistada: Boolean,
    val vinPendente: Boolean,

    val checklistsOk: Boolean,
    val montagemOk: Boolean,
    val embalagemOk: Boolean,
    val controloOk: Boolean,

    val totalMotas: Int,
    val totalServicos: Int,

    val dataCriacaoIso: String? = null,
    val dataConclusaoIso: String? = null,

    val acaoPrincipal: AcaoPrincipalUi
)

fun EtapaOrdemUi.label(): String = when (this) {
    EtapaOrdemUi.POR_ARRANCAR -> "Por arrancar"
    EtapaOrdemUi.MONTAGEM -> "Montagem"
    EtapaOrdemUi.EMBALAGEM -> "Embalagem"
    EtapaOrdemUi.CONTROLO -> "Controlo"
    EtapaOrdemUi.PRONTA_EXPEDICAO -> "Pronta"
    EtapaOrdemUi.CONCLUIDA -> "Concluída"
    EtapaOrdemUi.BLOQUEADA -> "Bloqueada"
}

fun StatusExecucaoUi.label(): String = when (this) {
    StatusExecucaoUi.NORMAL -> "Normal"
    StatusExecucaoUi.ATENCAO -> "Atenção"
    StatusExecucaoUi.CRITICO -> "Crítico"
    StatusExecucaoUi.BLOQUEADO -> "Bloqueado"
    StatusExecucaoUi.CONCLUIDO -> "Concluído"
}

fun PrioridadeUi.label(): String = when (this) {
    PrioridadeUi.NORMAL -> "Normal"
    PrioridadeUi.ALTA -> "Alta"
    PrioridadeUi.CRITICA -> "Crítica"
}

fun AcaoPrincipalUi.label(): String = when (this) {
    AcaoPrincipalUi.INICIAR -> "Iniciar"
    AcaoPrincipalUi.REGISTAR_UNIDADE -> "Registar unidade"
    AcaoPrincipalUi.FECHAR_CHECKLISTS -> "Fechar checklists"
    AcaoPrincipalUi.VALIDAR_CONTROLO -> "Validar controlo"
    AcaoPrincipalUi.CONCLUIR -> "Concluir"
    AcaoPrincipalUi.ANALISAR_BLOQUEIO -> "Analisar bloqueio"
    AcaoPrincipalUi.CONSULTAR -> "Consultar"
}

fun FiltroOperacaoUi.label(): String = when (this) {
    FiltroOperacaoUi.TODAS -> "Todas"
    FiltroOperacaoUi.BLOQUEADAS -> "Bloqueadas"
    FiltroOperacaoUi.POR_ARRANCAR -> "Por arrancar"
    FiltroOperacaoUi.EM_EXECUCAO -> "Em execução"
    FiltroOperacaoUi.CONTROLO_PENDENTE -> "Controlo pendente"
    FiltroOperacaoUi.PRONTAS_A_CONCLUIR -> "Prontas a concluir"
    FiltroOperacaoUi.CONCLUIDAS -> "Concluídas"
    FiltroOperacaoUi.PRIORITARIAS -> "Prioritárias"
    FiltroOperacaoUi.EM_RISCO -> "Em risco"
}
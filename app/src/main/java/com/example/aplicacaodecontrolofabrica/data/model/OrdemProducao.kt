package com.example.aplicacaodecontrolofabrica.data.model

enum class EstadoOrdemBaseUi {
    POR_ARRANCAR,
    EM_PRODUCAO,
    CONCLUIDA,
    BLOQUEADA,
    DESCONHECIDO
}

data class OrdemProducao(
    val id: Int,
    val numeroOrdem: String,
    val estado: Int,
    val idModelo: Int? = null,
    val idCliente: Int? = null,
    val idEncomenda: Int? = null,
    val paisDestino: String? = null,
    val dataCriacaoIso: String? = null,
    val dataConclusaoIso: String? = null,
    val prioridadeManual: Boolean = false,
    val motivoPrioridade: String? = null,
    val motivoBloqueio: String? = null
) {
    val isPorArrancar: Boolean get() = estado == 0
    val isEmProducao: Boolean get() = estado == 1
    val isConcluida: Boolean get() = estado == 2
    val isBloqueada: Boolean get() = estado == 3

    val estadoBaseUi: EstadoOrdemBaseUi
        get() = when (estado) {
            0 -> EstadoOrdemBaseUi.POR_ARRANCAR
            1 -> EstadoOrdemBaseUi.EM_PRODUCAO
            2 -> EstadoOrdemBaseUi.CONCLUIDA
            3 -> EstadoOrdemBaseUi.BLOQUEADA
            else -> EstadoOrdemBaseUi.DESCONHECIDO
        }
}

fun EstadoOrdemBaseUi.label(): String = when (this) {
    EstadoOrdemBaseUi.POR_ARRANCAR -> "Por arrancar"
    EstadoOrdemBaseUi.EM_PRODUCAO -> "Em produção"
    EstadoOrdemBaseUi.CONCLUIDA -> "Concluída"
    EstadoOrdemBaseUi.BLOQUEADA -> "Bloqueada"
    EstadoOrdemBaseUi.DESCONHECIDO -> "Desconhecido"
}
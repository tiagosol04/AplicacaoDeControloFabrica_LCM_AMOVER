package com.example.aplicacaodecontrolofabrica.data.model

enum class EstadoMaterialCriticoUi {
    POR_INTEGRAR,
    A_CONFIRMAR,
    DISPONIVEL,
    FALTA_CRITICA
}

enum class FonteMaterialCriticoUi {
    SEM_INTEGRACAO,
    PHC_FUTURO,
    COMPRAS_FUTURO
}

enum class ProntidaoOrdemUi {
    PRONTA,
    A_CONFIRMAR,
    EM_EXECUCAO,
    BLOQUEADA,
    CONCLUIDA
}

data class MaterialCriticoUi(
    val estado: EstadoMaterialCriticoUi,
    val fonte: FonteMaterialCriticoUi = FonteMaterialCriticoUi.SEM_INTEGRACAO,
    val totalCriticosEsperados: Int = 0,
    val confirmados: Int = 0,
    val emFalta: Int = 0,
    val nota: String = "Integração futura com compras/PHC."
)

fun EstadoMaterialCriticoUi.label(): String = when (this) {
    EstadoMaterialCriticoUi.POR_INTEGRAR -> "Por integrar"
    EstadoMaterialCriticoUi.A_CONFIRMAR -> "A confirmar"
    EstadoMaterialCriticoUi.DISPONIVEL -> "Disponível"
    EstadoMaterialCriticoUi.FALTA_CRITICA -> "Falta crítica"
}

fun FonteMaterialCriticoUi.label(): String = when (this) {
    FonteMaterialCriticoUi.SEM_INTEGRACAO -> "Sem integração"
    FonteMaterialCriticoUi.PHC_FUTURO -> "PHC futuro"
    FonteMaterialCriticoUi.COMPRAS_FUTURO -> "Compras futuras"
}

fun ProntidaoOrdemUi.label(): String = when (this) {
    ProntidaoOrdemUi.PRONTA -> "Pronta"
    ProntidaoOrdemUi.A_CONFIRMAR -> "A confirmar"
    ProntidaoOrdemUi.EM_EXECUCAO -> "Em execução"
    ProntidaoOrdemUi.BLOQUEADA -> "Bloqueada"
    ProntidaoOrdemUi.CONCLUIDA -> "Concluída"
}
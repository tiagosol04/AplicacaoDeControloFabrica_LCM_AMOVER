package com.example.aplicacaodecontrolofabrica.data.model

enum class EstadoColaboradorUi {
    DISPONIVEL,
    AFETO,
    SOBRECARGA,
    INDISPONIVEL
}

enum class FiltroEquipaUi {
    TODOS,
    DISPONIVEIS,
    AFETOS,
    SOBRECARGA,
    INDISPONIVEIS
}

data class UnidadeAssociadaUi(
    val idAssociacao: Int,
    val motaId: Int,
    val vin: String,
    val modeloNome: String,
    val ordemId: Int? = null
)

data class ColaboradorFabricaUi(
    val id: Int,
    val nome: String,
    val funcao: String,
    val email: String?,
    val telefone: String?,
    val estado: EstadoColaboradorUi,
    val disponibilidadeLabel: String,
    val ativoNaBaseDados: Boolean,
    val totalAssociacoesAtivas: Int,
    val unidadesAtuais: List<UnidadeAssociadaUi> = emptyList(),
    val notaOperacional: String,
    val podeAlterarEstado: Boolean = true
)

data class EquipaResumoUi(
    val total: Int = 0,
    val disponiveis: Int = 0,
    val afetos: Int = 0,
    val sobrecarga: Int = 0,
    val indisponiveis: Int = 0
)
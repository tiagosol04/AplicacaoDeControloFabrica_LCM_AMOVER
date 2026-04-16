package com.example.aplicacaodecontrolofabrica.data.dto

data class AlertaUiDto(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val prioridade: String, // BAIXA | MEDIA | ALTA | CRITICA
    val estado: String,     // ABERTO | EM_TRATAMENTO | RESOLVIDO
    val dataIso: String? = null,

    val tipo: String = "OPERACIONAL",
    val origem: String = "PRODUCAO",

    val ordemId: Int? = null,
    val vin: String? = null,
    val modeloId: Int? = null,
    val clienteId: Int? = null
)
package com.example.aplicacaodecontrolofabrica.data.model

data class Cliente(
    val id: Int,
    val nome: String,
    val nif: String? = null,
    val localidade: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val morada: String? = null,
    val codigoPostal: String? = null,
    val pais: String? = null,
    val estado: String = "Ativo",
    val dataCriacaoIso: String? = null,
    val dataModificacaoIso: String? = null,
    val ultimaEncomendaIso: String? = null
)
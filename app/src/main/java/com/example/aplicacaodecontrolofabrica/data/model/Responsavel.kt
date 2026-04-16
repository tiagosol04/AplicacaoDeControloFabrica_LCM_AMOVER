package com.example.aplicacaodecontrolofabrica.data.model

data class Responsavel(
    val id: Int,
    val nome: String,
    val funcao: String? = null,
    val contacto: String? = null,
    val estado: String = "Ativo",
    val email: String? = null,
    val aspNetUserId: String? = null,
    val roles: List<String> = emptyList()
) {
    val nomeDisplay: String
        get() = nome.ifBlank { "Responsável sem nome" }
}
package com.example.aplicacaodecontrolofabrica.data.model

data class Peca(
    val id: Int,
    val nome: String? = null,
    val tipo: String? = null,
    val quantidade: Int? = null,
    val estado: String? = null,
    val partNumber: String? = null,
    val descricao: String? = null,
    val serializavel: Boolean = false,
    val critica: Boolean = false
) {
    val nomeDisplay: String
        get() = nome?.ifBlank { "Peça sem nome" } ?: "Peça sem nome"
}
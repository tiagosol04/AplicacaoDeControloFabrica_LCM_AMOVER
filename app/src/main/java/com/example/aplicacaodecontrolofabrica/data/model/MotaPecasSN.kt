package com.example.aplicacaodecontrolofabrica.data.model

data class MotaPecasSN(
    val id: Int,
    val idMota: Int,
    val idPeca: Int,
    val serialNumber: String,
    val nomePeca: String? = null,
    val dataCriacaoIso: String? = null,
    val dataModificacaoIso: String? = null
)
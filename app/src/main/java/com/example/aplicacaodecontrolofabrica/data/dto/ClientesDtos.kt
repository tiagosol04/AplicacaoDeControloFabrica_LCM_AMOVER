package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class ClienteDto(
    @SerializedName(
        value = "IDCliente",
        alternate = ["idCliente", "IdCliente", "id", "Id", "ID"]
    )
    val idCliente: Int? = null,

    @SerializedName(
        value = "Nome",
        alternate = ["nome"]
    )
    val nome: String? = null,

    @SerializedName(
        value = "NIF",
        alternate = ["nif"]
    )
    val nif: String? = null,

    @SerializedName(
        value = "Localidade",
        alternate = ["localidade"]
    )
    val localidade: String? = null,

    @SerializedName(
        value = "Email",
        alternate = ["email"]
    )
    val email: String? = null,

    @SerializedName(
        value = "Telefone",
        alternate = ["telefone"]
    )
    val telefone: String? = null,

    @SerializedName(
        value = "Estado",
        alternate = ["estado"]
    )
    val estado: Int? = null,

    @SerializedName(
        value = "Ativo",
        alternate = ["ativo"]
    )
    val ativo: Boolean? = null,

    @SerializedName(
        value = "DataCriacao",
        alternate = ["dataCriacao"]
    )
    val dataCriacao: String? = null,

    @SerializedName(
        value = "DataModificacao",
        alternate = ["dataModificacao"]
    )
    val dataModificacao: String? = null,

    @SerializedName(
        value = "UltimaEncomenda",
        alternate = ["ultimaEncomenda"]
    )
    val ultimaEncomenda: String? = null
)
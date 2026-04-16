package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class EncomendaDto(
    @SerializedName(
        value = "IDEncomenda",
        alternate = ["idEncomenda", "IdEncomenda", "EncomendaIDEncomenda", "id", "Id"]
    )
    val idEncomenda: Int? = null,

    @SerializedName(
        value = "IDCliente",
        alternate = ["idCliente", "IdCliente", "ClienteIDCliente"]
    )
    val idCliente: Int? = null,

    @SerializedName(
        value = "IDModelo",
        alternate = ["idModelo", "IdModelo", "ModeloMotaIDModelo"]
    )
    val idModelo: Int? = null,

    @SerializedName(
        value = "Quantidade",
        alternate = ["quantidade"]
    )
    val quantidade: Int? = null,

    @SerializedName(
        value = "DataEncomenda",
        alternate = ["dataEncomenda"]
    )
    val dataEncomenda: String? = null,

    @SerializedName(
        value = "DataEntrega",
        alternate = ["dataEntrega"]
    )
    val dataEntrega: String? = null,

    @SerializedName(
        value = "Estado",
        alternate = ["estado"]
    )
    val estado: Int? = null,

    @SerializedName(
        value = "DataCriacao",
        alternate = ["dataCriacao"]
    )
    val dataCriacao: String? = null,

    @SerializedName(
        value = "DataModificacao",
        alternate = ["dataModificacao"]
    )
    val dataModificacao: String? = null
)

data class UpdateEncomendaRequest(
    @SerializedName("estado")
    val estado: Int
)

data class EncomendasAlertasDto(
    val encomendasPendentes: Int = 0,
    val alertasAbertos: Int = 0
)
data class CreateEncomendaRequest(
    @SerializedName(value = "IDCliente", alternate = ["idCliente"])
    val idCliente: Int,

    @SerializedName(value = "IDModelo", alternate = ["idModelo"])
    val idModelo: Int,

    @SerializedName(value = "Quantidade", alternate = ["quantidade"])
    val quantidade: Int,

    @SerializedName(value = "Estado", alternate = ["estado"])
    val estado: Int = 0,

    @SerializedName(value = "DataEntrega", alternate = ["dataEntrega"])
    val dataEntrega: String? = null
)

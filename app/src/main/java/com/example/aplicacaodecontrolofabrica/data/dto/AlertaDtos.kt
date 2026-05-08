package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class AlertasApiResponse(
    @SerializedName(value = "calculado", alternate = ["Calculado"])
    val calculado: Boolean = false,

    @SerializedName(value = "alertas", alternate = ["Alertas", "items", "data"])
    val alertas: List<AlertaApiDto> = emptyList()
)

data class AlertaApiDto(
    @SerializedName(value = "id", alternate = ["Id", "ID"])
    val id: Int? = null,

    @SerializedName(value = "titulo", alternate = ["Titulo", "title"])
    val titulo: String? = null,

    @SerializedName(value = "descricao", alternate = ["Descricao", "description"])
    val descricao: String? = null,

    @SerializedName(value = "tipo", alternate = ["Tipo", "type"])
    val tipo: String? = null,

    @SerializedName(value = "severidade", alternate = ["Severidade", "severity"])
    val severidade: String? = null,

    @SerializedName(value = "estado", alternate = ["Estado", "status"])
    val estado: String? = null,

    @SerializedName(value = "origem", alternate = ["Origem", "source"])
    val origem: String? = null,

    @SerializedName(value = "ordemId", alternate = ["OrdemId", "IDOrdemProducao"])
    val ordemId: Int? = null,

    @SerializedName(value = "modeloId", alternate = ["ModeloId", "IDModelo"])
    val modeloId: Int? = null,

    @SerializedName(value = "clienteId", alternate = ["ClienteId", "IDCliente"])
    val clienteId: Int? = null,

    @SerializedName(value = "dataCriacaoIso", alternate = ["DataCriacao", "dataCriacao"])
    val dataCriacaoIso: String? = null
)

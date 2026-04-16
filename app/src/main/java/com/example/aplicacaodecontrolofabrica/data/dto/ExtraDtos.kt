package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class IdResponse(
    @SerializedName(value = "id", alternate = ["Id", "ID"])
    val id: Int? = null
)

data class UpdateEstadoRequest(
    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int
)

data class UpdateChecklistRequest(
    @SerializedName(value = "value", alternate = ["Value"])
    val value: Int
)

data class AddPecaSnRequest(
    @SerializedName(value = "idPeca", alternate = ["IDPeca", "IdPeca"])
    val idPeca: Int,

    @SerializedName(value = "numeroSerie", alternate = ["NumeroSerie", "serialNumber", "SerialNumber"])
    val numeroSerie: String
)

data class CriarMotaRequest(
    @SerializedName(value = "numeroIdentificacao", alternate = ["NumeroIdentificacao", "vin", "VIN"])
    val numeroIdentificacao: String,

    @SerializedName(value = "cor", alternate = ["Cor"])
    val cor: String,

    @SerializedName(value = "quilometragem", alternate = ["Quilometragem", "km", "KM"])
    val quilometragem: Double = 0.0,

    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int = 1
)
package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class ModeloDto(
    @SerializedName(
        value = "IDModelo",
        alternate = ["idModelo", "IdModelo", "id", "Id", "ID"]
    )
    val idModelo: Int? = null,

    @SerializedName(
        value = "Nome",
        alternate = ["nome", "NomeModelo", "nomeModelo"]
    )
    val nomeModelo: String? = null,

    @SerializedName(
        value = "CodigoProduto",
        alternate = ["codigoProduto", "Codigo", "codigo", "CodigoModelo", "codigoModelo"]
    )
    val codigoProduto: String? = null,

    @SerializedName(
        value = "Cilindrada",
        alternate = ["cilindrada"]
    )
    val cilindrada: Int? = null,

    @SerializedName(
        value = "Autonomia",
        alternate = ["autonomia"]
    )
    val autonomia: Int? = null,

    @SerializedName(
        value = "Estado",
        alternate = ["estado"]
    )
    val estado: String? = null,

    @SerializedName(
        value = "Descricao",
        alternate = ["descricao"]
    )
    val descricao: String? = null,

    @SerializedName(
        value = "DataInicioProducao",
        alternate = ["dataInicioProducao", "DataProducao", "dataProducao"]
    )
    val dataInicioProducao: String? = null,

    @SerializedName(
        value = "DataLancamento",
        alternate = ["dataLancamento"]
    )
    val dataLancamento: String? = null,

    @SerializedName(
        value = "DataDescontinuacao",
        alternate = ["dataDescontinuacao"]
    )
    val dataDescontinuacao: String? = null
)
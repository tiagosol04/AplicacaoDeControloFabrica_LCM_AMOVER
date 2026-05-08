package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class MotaDto(
    @SerializedName(
        value = "IDMota",
        alternate = ["idMota", "IdMota", "id", "Id", "ID"]
    )
    val idMota: Int? = null,

    @SerializedName(
        value = "NumeroIdentificacao",
        alternate = ["numeroIdentificacao", "vin", "VIN"]
    )
    val numeroIdentificacao: String? = null,

    @SerializedName(
        value = "Cor",
        alternate = ["cor"]
    )
    val cor: String? = null,

    @SerializedName(
        value = "Quilometragem",
        alternate = ["quilometragem", "km", "KM"]
    )
    val quilometragem: Double? = null,

    @SerializedName(
        value = "Estado",
        alternate = ["estado"]
    )
    val estado: Int? = null,

    @SerializedName(
        value = "IDModelo",
        alternate = ["idModelo", "IdModelo", "ModeloMotaIDModelo"]
    )
    val idModelo: Int? = null,

    @SerializedName(
        value = "IDOrdemProducao",
        alternate = ["idOrdemProducao", "IdOrdemProducao", "OrdemProducaoIDOrdemProducao"]
    )
    val idOrdemProducao: Int? = null,

    @SerializedName(
        value = "DataRegisto",
        alternate = ["dataRegisto"]
    )
    val dataRegisto: String? = null
)

data class MotaPecaSnDto(
    @SerializedName(
        value = "IDMotasPecasSN",
        alternate = ["id", "ID", "Id", "idMotasPecasSN", "IdMotasPecasSN"]
    )
    val id: Int? = null,

    @SerializedName(
        value = "IDMota",
        alternate = ["idMota", "IdMota"]
    )
    val idMota: Int? = null,

    @SerializedName(
        value = "IDPeca",
        alternate = ["idPeca", "IdPeca"]
    )
    val idPeca: Int? = null,

    @SerializedName(
        value = "NumeroSerie",
        alternate = ["numeroSerie", "serialNumber", "SerialNumber"]
    )
    val serialNumber: String? = null
)

data class PecaFixaDto(
    @SerializedName(value = "idPeca", alternate = ["IDPeca", "IdPeca", "id", "Id"])
    val idPeca: Int? = null,

    @SerializedName(value = "nome", alternate = ["Nome"])
    val nome: String? = null,

    @SerializedName(value = "partNumber", alternate = ["PartNumber", "partnumber"])
    val partNumber: String? = null,

    @SerializedName(value = "quantidade", alternate = ["Quantidade"])
    val quantidade: Int? = null,

    @SerializedName(value = "descricao", alternate = ["Descricao"])
    val descricao: String? = null
)

data class PecaFixaResponse(
    @SerializedName(value = "motaId", alternate = ["MotaId", "IDMota", "idMota"])
    val motaId: Int? = null,

    @SerializedName(value = "idModelo", alternate = ["IdModelo", "IDModelo"])
    val idModelo: Int? = null,

    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int? = null,

    @SerializedName(value = "pecas", alternate = ["Pecas", "items"])
    val pecas: List<PecaFixaDto> = emptyList()
)

data class PecaDto(
    @SerializedName(
        value = "IDPeca",
        alternate = ["idPeca", "IdPeca", "id", "Id", "ID"]
    )
    val idPeca: Int? = null,

    @SerializedName(
        value = "PartNumber",
        alternate = ["partNumber", "partnumber"]
    )
    val partNumber: String? = null,

    @SerializedName(
        value = "Descricao",
        alternate = ["descricao"]
    )
    val descricao: String? = null,

    @SerializedName(
        value = "IDFornecedor",
        alternate = ["idFornecedor", "IdFornecedor"]
    )
    val idFornecedor: Int? = null,

    @SerializedName(
        value = "Nome",
        alternate = ["nome"]
    )
    val nome: String? = null,

    @SerializedName(
        value = "Tipo",
        alternate = ["tipo"]
    )
    val tipo: String? = null,

    @SerializedName(
        value = "Quantidade",
        alternate = ["quantidade"]
    )
    val quantidade: Int? = null,

    @SerializedName(
        value = "Estado",
        alternate = ["estado"]
    )
    val estado: Int? = null
)
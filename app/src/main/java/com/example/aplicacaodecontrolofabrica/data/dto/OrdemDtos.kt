package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class OrdemProducaoDto(
    @SerializedName(
        value = "IDOrdemProducao",
        alternate = ["idOrdemProducao", "IdOrdemProducao", "id", "Id", "ID"]
    )
    val idOrdemProducao: Int? = null,

    @SerializedName(
        value = "NumeroOrdem",
        alternate = ["numeroOrdem", "Numero", "numero"]
    )
    val numeroOrdem: String? = null,

    @SerializedName(
        value = "Estado",
        alternate = ["estado"]
    )
    val estado: Int? = null,

    @SerializedName(
        value = "idModelo",
        alternate = ["IDModelo", "IdModelo", "ModeloMotaIDModelo"]
    )
    val idModelo: Int? = null,

    @SerializedName(
        value = "idCliente",
        alternate = ["IDCliente", "IdCliente", "ClienteIDCliente"]
    )
    val idCliente: Int? = null,

    @SerializedName(
        value = "IDEncomenda",
        alternate = ["idEncomenda", "IdEncomenda", "EncomendaIDEncomenda"]
    )
    val idEncomenda: Int? = null,

    @SerializedName(
        value = "PaisDestino",
        alternate = ["paisDestino"]
    )
    val paisDestino: String? = null,

    @SerializedName(
        value = "DataCriacao",
        alternate = ["dataCriacao", "dataCriacaoIso", "DataCriacaoIso"]
    )
    val dataCriacao: String? = null,

    @SerializedName(
        value = "DataConclusao",
        alternate = ["dataConclusao", "dataConclusaoIso", "DataConclusaoIso"]
    )
    val dataConclusao: String? = null
)

data class OrdemResumoDto(
    @SerializedName(
        value = "ordemId",
        alternate = ["OrdemId", "IDOrdemProducao", "idOrdemProducao", "IdOrdemProducao", "id", "Id"]
    )
    val ordemId: Int = 0,

    @SerializedName(
        value = "motas",
        alternate = ["Motas"]
    )
    val motas: Int = 0,

    @SerializedName(
        value = "servicos",
        alternate = ["Servicos"]
    )
    val servicos: Int = 0,

    @SerializedName(
        value = "checklists",
        alternate = ["Checklists"]
    )
    val checklists: OrdemResumoChecklistsDto = OrdemResumoChecklistsDto()
)

data class OrdemResumoChecklistsDto(
    @SerializedName(
        value = "montagemOk",
        alternate = ["MontagemOk"]
    )
    val montagemOk: Boolean = false,

    @SerializedName(
        value = "embalagemOk",
        alternate = ["EmbalagemOk"]
    )
    val embalagemOk: Boolean = false,

    @SerializedName(
        value = "controloOk",
        alternate = ["ControloOk"]
    )
    val controloOk: Boolean = false
)
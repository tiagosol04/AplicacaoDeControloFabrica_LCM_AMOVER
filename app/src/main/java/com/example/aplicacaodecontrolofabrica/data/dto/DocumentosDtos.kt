package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class DocumentoDto(
    @SerializedName(
        value = "IDDocumento",
        alternate = ["idDocumento", "IdDocumento", "id", "Id", "ID"]
    )
    val idDocumento: Int? = null,

    @SerializedName(
        value = "Nome",
        alternate = ["nome"]
    )
    val nome: String? = null,

    @SerializedName(
        value = "Descricao",
        alternate = ["descricao"]
    )
    val descricao: String? = null,

    @SerializedName(
        value = "Caminho",
        alternate = ["caminho", "url", "Url"]
    )
    val caminho: String? = null,

    @SerializedName(
        value = "Tipo",
        alternate = ["tipo"]
    )
    val tipo: String? = null
)

data class DocumentoModeloDto(
    @SerializedName(
        value = "IDDocumentosModelo",
        alternate = ["idDocumentosModelo", "IdDocumentosModelo", "id", "Id", "ID"]
    )
    val idDocumentosModelo: Int? = null,

    @SerializedName(
        value = "IDDocumento",
        alternate = ["idDocumento", "IdDocumento"]
    )
    val idDocumento: Int? = null,

    @SerializedName(
        value = "IDModelo",
        alternate = ["idModelo", "IdModelo"]
    )
    val idModelo: Int? = null
)
package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class UtilizadorDto(
    @SerializedName(
        value = "IDUtilizador",
        alternate = ["idUtilizador", "IdUtilizador", "id", "Id", "ID"]
    )
    val idUtilizador: Int? = null,

    @SerializedName(
        value = "Username",
        alternate = ["username", "Nome", "nome"]
    )
    val username: String? = null,

    @SerializedName(
        value = "Tipo",
        alternate = ["tipo", "Funcao", "funcao"]
    )
    val tipo: String? = null,

    @SerializedName(
        value = "Telefone",
        alternate = ["telefone"]
    )
    val telefone: String? = null,

    @SerializedName(
        value = "Ativo",
        alternate = ["ativo"]
    )
    val ativo: Boolean? = null,

    @SerializedName(
        value = "Estado",
        alternate = ["estado"]
    )
    val estado: Int? = null,

    @SerializedName(
        value = "Email",
        alternate = ["email"]
    )
    val email: String? = null,

    @SerializedName(
        value = "KeycloakId",
        alternate = ["keycloakId", "AuthUserId", "authUserId", "AspNetUserId", "aspNetUserId"]
    )
    val keycloakId: String? = null
)

data class UtilizadorDetailResponseDto(
    @SerializedName(value = "utilizador", alternate = ["Utilizador"])
    val utilizador: UtilizadorDto? = null,

    @SerializedName(value = "totalAssociacoesAtivas", alternate = ["TotalAssociacoesAtivas"])
    val totalAssociacoesAtivas: Int? = null
)

data class UtilizadorMotaResumoDto(
    @SerializedName(value = "IDUtilizadorMota", alternate = ["idUtilizadorMota", "IdUtilizadorMota"])
    val idUtilizadorMota: Int? = null,

    @SerializedName(value = "MotaId", alternate = ["motaId", "IDMota", "idMota"])
    val motaId: Int? = null,

    @SerializedName(value = "NumeroIdentificacao", alternate = ["numeroIdentificacao", "vin"])
    val numeroIdentificacao: String? = null,

    @SerializedName(value = "modeloNome", alternate = ["ModeloNome", "nomeModelo"])
    val modeloNome: String? = null,

    @SerializedName(value = "modeloCodigo", alternate = ["ModeloCodigo", "codigoProduto"])
    val modeloCodigo: String? = null,

    @SerializedName(value = "estadoAssociacaoNome", alternate = ["EstadoAssociacaoNome", "estadoNome"])
    val estadoAssociacaoNome: String? = null,

    @SerializedName(value = "DataCriacao", alternate = ["dataCriacao"])
    val dataCriacao: String? = null,

    @SerializedName(value = "DataInativacao", alternate = ["dataInativacao"])
    val dataInativacao: String? = null,

    @SerializedName(value = "MotivoInativacao", alternate = ["motivoInativacao"])
    val motivoInativacao: String? = null,

    @SerializedName(value = "IDOrdemProducao", alternate = ["idOrdemProducao"])
    val idOrdemProducao: Int? = null
)

data class UtilizadorMotasResponseDto(
    @SerializedName(value = "utilizadorId", alternate = ["UtilizadorId"])
    val utilizadorId: Int? = null,

    @SerializedName(value = "ativasOnly", alternate = ["AtivasOnly"])
    val ativasOnly: Boolean? = null,

    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int? = null,

    @SerializedName(value = "motas", alternate = ["Motas"])
    val motas: List<UtilizadorMotaResumoDto> = emptyList()
)

data class UpdateUserStatusRequest(
    @SerializedName(value = "ativo", alternate = ["Ativo"])
    val ativo: Boolean
)

data class InativarAssociacaoRequest(
    @SerializedName(value = "motivoInativacao", alternate = ["MotivoInativacao"])
    val motivoInativacao: String? = null
)
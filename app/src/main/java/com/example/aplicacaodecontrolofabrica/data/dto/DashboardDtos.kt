package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class DashboardResumoDto(
    @SerializedName(value = "totalOrdens", alternate = ["TotalOrdens"])
    val totalOrdens: Int = 0,

    @SerializedName(value = "emProducao", alternate = ["EmProducao"])
    val emProducao: Int = 0,

    @SerializedName(value = "bloqueadas", alternate = ["Bloqueadas"])
    val bloqueadas: Int = 0,

    @SerializedName(value = "semUnidade", alternate = ["SemUnidade"])
    val semUnidade: Int = 0,

    @SerializedName(value = "controloPendente", alternate = ["ControloPendente"])
    val controloPendente: Int = 0,

    @SerializedName(value = "vinPendente", alternate = ["VinPendente"])
    val vinPendente: Int = 0,

    @SerializedName(value = "equipaAtiva", alternate = ["EquipaAtiva"])
    val equipaAtiva: Int = 0,

    @SerializedName(value = "servicosEmAberto", alternate = ["ServicosEmAberto"])
    val servicosEmAberto: Int = 0,

    @SerializedName(value = "ordens", alternate = ["Ordens"])
    val ordens: List<DashboardOrdemDto> = emptyList()
)

data class DashboardOrdemDto(
    @SerializedName(value = "ordemId", alternate = ["IDOrdemProducao", "idOrdemProducao", "id", "Id"])
    val ordemId: Int? = null,

    @SerializedName(value = "numeroOrdem", alternate = ["NumeroOrdem"])
    val numeroOrdem: String? = null,

    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int? = null,

    @SerializedName(value = "modeloNome", alternate = ["ModeloNome", "nomeModelo"])
    val modeloNome: String? = null,

    @SerializedName(value = "clienteNome", alternate = ["ClienteNome"])
    val clienteNome: String? = null,

    @SerializedName(value = "unidadeRegistada", alternate = ["UnidadeRegistada"])
    val unidadeRegistada: Boolean = false,

    @SerializedName(value = "vinPendente", alternate = ["VinPendente"])
    val vinPendente: Boolean = false,

    @SerializedName(value = "montagemOk", alternate = ["MontagemOk"])
    val montagemOk: Boolean = false,

    @SerializedName(value = "embalagemOk", alternate = ["EmbalagemOk"])
    val embalagemOk: Boolean = false,

    @SerializedName(value = "controloOk", alternate = ["ControloOk"])
    val controloOk: Boolean = false,

    @SerializedName(value = "totalMotas", alternate = ["TotalMotas"])
    val totalMotas: Int = 0
)

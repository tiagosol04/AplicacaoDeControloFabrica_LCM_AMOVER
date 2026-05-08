package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

// ── Ficha operacional consolidada ──

data class OrdemFichaDto(
    @SerializedName(value = "ordemId", alternate = ["IDOrdemProducao", "idOrdemProducao", "id", "Id"])
    val ordemId: Int? = null,

    @SerializedName(value = "numeroOrdem", alternate = ["NumeroOrdem", "numero"])
    val numeroOrdem: String? = null,

    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int? = null,

    @SerializedName(value = "paisDestino", alternate = ["PaisDestino"])
    val paisDestino: String? = null,

    @SerializedName(value = "dataCriacao", alternate = ["DataCriacao", "dataCriacaoIso"])
    val dataCriacao: String? = null,

    @SerializedName(value = "dataConclusao", alternate = ["DataConclusao", "dataConclusaoIso"])
    val dataConclusao: String? = null,

    @SerializedName(value = "modeloNome", alternate = ["ModeloNome", "nomeModelo"])
    val modeloNome: String? = null,

    @SerializedName(value = "clienteNome", alternate = ["ClienteNome", "nomeCliente"])
    val clienteNome: String? = null,

    @SerializedName(value = "montagemOk", alternate = ["MontagemOk"])
    val montagemOk: Boolean? = null,

    @SerializedName(value = "embalagemOk", alternate = ["EmbalagemOk"])
    val embalagemOk: Boolean? = null,

    @SerializedName(value = "controloOk", alternate = ["ControloOk"])
    val controloOk: Boolean? = null,

    @SerializedName(value = "totalServicos", alternate = ["TotalServicos", "servicos"])
    val totalServicos: Int? = null,

    @SerializedName(value = "motas", alternate = ["Motas"])
    val motas: List<MotaDto>? = null,

    @SerializedName(value = "embalada", alternate = ["Embalada"])
    val embalada: Boolean? = null,

    @SerializedName(value = "enviada", alternate = ["Enviada"])
    val enviada: Boolean? = null
)

// ── Histórico de eventos da ordem ──

data class HistoricoItemDto(
    @SerializedName(value = "id", alternate = ["Id", "ID"])
    val id: Int? = null,

    @SerializedName(value = "tipo", alternate = ["Tipo"])
    val tipo: String? = null,

    @SerializedName(value = "descricao", alternate = ["Descricao"])
    val descricao: String? = null,

    @SerializedName(value = "utilizadorId", alternate = ["UtilizadorId", "IDUtilizador", "idUtilizador"])
    val utilizadorId: Int? = null,

    @SerializedName(value = "utilizadorNome", alternate = ["UtilizadorNome"])
    val utilizadorNome: String? = null,

    @SerializedName(value = "dataOcorrencia", alternate = ["DataOcorrencia"])
    val dataOcorrencia: String? = null,

    @SerializedName(value = "valorAnterior", alternate = ["ValorAnterior"])
    val valorAnterior: String? = null,

    @SerializedName(value = "valorNovo", alternate = ["ValorNovo"])
    val valorNovo: String? = null,

    @SerializedName(value = "calculado", alternate = ["Calculado"])
    val calculado: Boolean? = null
)

data class HistoricoOrdemResponse(
    @SerializedName(value = "ordemId", alternate = ["OrdemId"])
    val ordemId: Int? = null,

    @SerializedName(value = "numeroOrdem", alternate = ["NumeroOrdem"])
    val numeroOrdem: String? = null,

    @SerializedName(value = "aviso", alternate = ["Aviso", "warning"])
    val aviso: String? = null,

    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int? = null,

    @SerializedName(value = "historico", alternate = ["Historico", "items"])
    val historico: List<HistoricoItemDto> = emptyList()
)

// ── Bloquear ordem ──

data class BloquearOrdemRequest(
    @SerializedName(value = "motivo", alternate = ["Motivo"])
    val motivo: String
)

data class BloquearOrdemResponse(
    @SerializedName(value = "message", alternate = ["Message", "mensagem"])
    val message: String? = null,

    @SerializedName(value = "ordemId", alternate = ["OrdemId", "IDOrdemProducao"])
    val ordemId: Int? = null,

    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int? = null,

    @SerializedName(value = "motivoBloqueio", alternate = ["MotivoBloqueio"])
    val motivoBloqueio: String? = null,

    @SerializedName(value = "dataOcorrencia", alternate = ["DataOcorrencia"])
    val dataOcorrencia: String? = null
)

// ── Desbloquear ordem ──

data class DesbloquearOrdemRequest(
    @SerializedName(value = "resolucao", alternate = ["Resolucao"])
    val resolucao: String? = null
)

data class DesbloquearOrdemResponse(
    @SerializedName(value = "message", alternate = ["Message", "mensagem"])
    val message: String? = null,

    @SerializedName(value = "ordemId", alternate = ["OrdemId", "IDOrdemProducao"])
    val ordemId: Int? = null,

    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int? = null
)

// ── Expedição ──

data class MarcarEmbalagemResponse(
    @SerializedName(value = "message", alternate = ["Message", "mensagem"])
    val message: String? = null,

    @SerializedName(value = "aviso", alternate = ["Aviso", "warning"])
    val aviso: String? = null,

    @SerializedName(value = "ordemId", alternate = ["OrdemId"])
    val ordemId: Int? = null
)

data class MarcarEnviadaResponse(
    @SerializedName(value = "message", alternate = ["Message", "mensagem"])
    val message: String? = null,

    @SerializedName(value = "aviso", alternate = ["Aviso", "warning"])
    val aviso: String? = null,

    @SerializedName(value = "ordemId", alternate = ["OrdemId"])
    val ordemId: Int? = null
)

data class ProntosExpedicaoDto(
    @SerializedName(value = "ordemId", alternate = ["IDOrdemProducao", "idOrdemProducao"])
    val ordemId: Int? = null,

    @SerializedName(value = "numeroOrdem", alternate = ["NumeroOrdem"])
    val numeroOrdem: String? = null,

    @SerializedName(value = "modeloNome", alternate = ["ModeloNome"])
    val modeloNome: String? = null,

    @SerializedName(value = "clienteNome", alternate = ["ClienteNome"])
    val clienteNome: String? = null,

    @SerializedName(value = "totalUnidades", alternate = ["TotalUnidades"])
    val totalUnidades: Int? = null
)

data class ProntosExpedicaoResponse(
    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int = 0,

    @SerializedName(value = "ordens", alternate = ["Ordens"])
    val ordens: List<ProntosExpedicaoDto> = emptyList()
)

// ── Utilizadores atribuídos à ordem ──

data class OrdemUtilizadoresResponse(
    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int = 0,

    @SerializedName(value = "utilizadores", alternate = ["Utilizadores"])
    val utilizadores: List<UtilizadorDto> = emptyList()
)

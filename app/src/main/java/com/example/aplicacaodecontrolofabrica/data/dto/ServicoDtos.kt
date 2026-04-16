package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

// ---------- Serviço base (lista / detalhe) ----------

data class ServicoDto(
    @SerializedName(value = "IDServico", alternate = ["idServico", "IdServico", "id", "Id"])
    val idServico: Int? = null,

    @SerializedName(value = "IDMota", alternate = ["idMota", "IdMota"])
    val idMota: Int? = null,

    @SerializedName(value = "Tipo", alternate = ["tipo"])
    val tipo: Int? = null,

    @SerializedName(value = "tipoNome", alternate = ["TipoNome"])
    val tipoNome: String? = null,

    @SerializedName(value = "Descricao", alternate = ["descricao"])
    val descricao: String? = null,

    @SerializedName(value = "Estado", alternate = ["estado"])
    val estado: Int? = null,

    @SerializedName(value = "estadoNome", alternate = ["EstadoNome"])
    val estadoNome: String? = null,

    @SerializedName(value = "DataServico", alternate = ["dataServico"])
    val dataServico: String? = null,

    @SerializedName(value = "DataConclusao", alternate = ["dataConclusao"])
    val dataConclusao: String? = null,

    @SerializedName(value = "NotasServico", alternate = ["notasServico"])
    val notasServico: String? = null,

    @SerializedName(value = "IDModelo", alternate = ["idModelo", "IdModelo"])
    val idModelo: Int? = null,

    @SerializedName(value = "IDOrdemProducao", alternate = ["idOrdemProducao"])
    val idOrdemProducao: Int? = null,

    @SerializedName(value = "NumeroIdentificacao", alternate = ["numeroIdentificacao", "vin"])
    val numeroIdentificacao: String? = null,

    @SerializedName(value = "Cor", alternate = ["cor"])
    val cor: String? = null,

    @SerializedName(value = "modeloNome", alternate = ["ModeloNome"])
    val modeloNome: String? = null,

    @SerializedName(value = "modeloCodigo", alternate = ["ModeloCodigo"])
    val modeloCodigo: String? = null,

    // Detalhe expandido (GET /api/servicos/{id})
    @SerializedName(value = "mota", alternate = ["Mota"])
    val mota: ServicoMotaDto? = null,

    @SerializedName(value = "pecasAlteradas", alternate = ["PecasAlteradas"])
    val pecasAlteradas: List<PecaAlteradaDto>? = null
)

data class ServicoMotaDto(
    @SerializedName(value = "IDMota", alternate = ["idMota"])
    val idMota: Int? = null,

    @SerializedName(value = "IDModelo", alternate = ["idModelo"])
    val idModelo: Int? = null,

    @SerializedName(value = "IDOrdemProducao", alternate = ["idOrdemProducao"])
    val idOrdemProducao: Int? = null,

    @SerializedName(value = "NumeroIdentificacao", alternate = ["numeroIdentificacao"])
    val numeroIdentificacao: String? = null,

    @SerializedName(value = "Cor", alternate = ["cor"])
    val cor: String? = null,

    @SerializedName(value = "Quilometragem", alternate = ["quilometragem"])
    val quilometragem: Double? = null,

    @SerializedName(value = "Estado", alternate = ["estado"])
    val estado: Int? = null,

    @SerializedName(value = "modelo", alternate = ["Modelo"])
    val modelo: ServicoModeloDto? = null
)

data class ServicoModeloDto(
    @SerializedName(value = "IDModelo", alternate = ["idModelo"])
    val idModelo: Int? = null,

    @SerializedName(value = "Nome", alternate = ["nome"])
    val nome: String? = null,

    @SerializedName(value = "CodigoProduto", alternate = ["codigoProduto"])
    val codigoProduto: String? = null
)

data class PecaAlteradaDto(
    @SerializedName(value = "ID", alternate = ["id", "Id"])
    val id: Int? = null,

    @SerializedName(value = "IDServico", alternate = ["idServico"])
    val idServico: Int? = null,

    @SerializedName(value = "IDMotasPecasSN", alternate = ["idMotasPecasSN"])
    val idMotasPecasSN: Int? = null,

    @SerializedName(value = "Observacoes", alternate = ["observacoes"])
    val observacoes: String? = null,

    @SerializedName(value = "IDMota", alternate = ["idMota"])
    val idMota: Int? = null,

    @SerializedName(value = "IDPeca", alternate = ["idPeca"])
    val idPeca: Int? = null,

    @SerializedName(value = "PartNumber", alternate = ["partNumber"])
    val partNumber: String? = null,

    @SerializedName(value = "Descricao", alternate = ["descricao"])
    val descricao: String? = null,

    @SerializedName(value = "NumeroSerie", alternate = ["numeroSerie"])
    val numeroSerie: String? = null,

    @SerializedName(value = "numeroSerieAtual", alternate = ["NumeroSerieAtual"])
    val numeroSerieAtual: String? = null
)

// ---------- Serviços em aberto ----------

data class ServicosEmAbertoResponse(
    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int? = null,

    @SerializedName(value = "servicos", alternate = ["Servicos"])
    val servicos: List<ServicoDto> = emptyList()
)

// ---------- Histórico por mota / modelo ----------

data class HistoricoMotaResponse(
    @SerializedName(value = "mota", alternate = ["Mota"])
    val mota: HistoricoMotaInfoDto? = null,

    @SerializedName(value = "totalServicos", alternate = ["TotalServicos"])
    val totalServicos: Int? = null,

    @SerializedName(value = "concluidos", alternate = ["Concluidos"])
    val concluidos: Int? = null,

    @SerializedName(value = "emCurso", alternate = ["EmCurso"])
    val emCurso: Int? = null,

    @SerializedName(value = "agendados", alternate = ["Agendados"])
    val agendados: Int? = null,

    @SerializedName(value = "servicos", alternate = ["Servicos"])
    val servicos: List<ServicoDto> = emptyList()
)

data class HistoricoMotaInfoDto(
    @SerializedName(value = "IDMota", alternate = ["idMota"])
    val idMota: Int? = null,

    @SerializedName(value = "IDModelo", alternate = ["idModelo"])
    val idModelo: Int? = null,

    @SerializedName(value = "NumeroIdentificacao", alternate = ["numeroIdentificacao"])
    val numeroIdentificacao: String? = null,

    @SerializedName(value = "Cor", alternate = ["cor"])
    val cor: String? = null,

    @SerializedName(value = "modeloNome", alternate = ["ModeloNome"])
    val modeloNome: String? = null,

    @SerializedName(value = "modeloCodigo", alternate = ["ModeloCodigo"])
    val modeloCodigo: String? = null
)

data class HistoricoModeloResponse(
    @SerializedName(value = "modelo", alternate = ["Modelo"])
    val modelo: ServicoModeloDto? = null,

    @SerializedName(value = "totalServicos", alternate = ["TotalServicos"])
    val totalServicos: Int? = null,

    @SerializedName(value = "totalMotasComHistorico", alternate = ["TotalMotasComHistorico"])
    val totalMotasComHistorico: Int? = null,

    @SerializedName(value = "concluidos", alternate = ["Concluidos"])
    val concluidos: Int? = null,

    @SerializedName(value = "emCurso", alternate = ["EmCurso"])
    val emCurso: Int? = null,

    @SerializedName(value = "agendados", alternate = ["Agendados"])
    val agendados: Int? = null,

    @SerializedName(value = "porTipo", alternate = ["PorTipo"])
    val porTipo: List<ServicoTipoContagem>? = null,

    @SerializedName(value = "problemasMaisRegistados", alternate = ["ProblemasMaisRegistados"])
    val problemasMaisRegistados: List<ProblemaFrequenteDto>? = null,

    @SerializedName(value = "ultimosServicos", alternate = ["UltimosServicos"])
    val ultimosServicos: List<ServicoDto> = emptyList()
)

data class ServicoTipoContagem(
    @SerializedName(value = "tipo", alternate = ["Tipo"])
    val tipo: Int? = null,

    @SerializedName(value = "nome", alternate = ["Nome"])
    val nome: String? = null,

    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int? = null
)

// ---------- Problemas frequentes ----------

data class ProblemasFrequentesResponse(
    @SerializedName(value = "modelo", alternate = ["Modelo"])
    val modelo: ServicoModeloDto? = null,

    @SerializedName(value = "totalProblemasAgrupados", alternate = ["TotalProblemasAgrupados"])
    val totalProblemasAgrupados: Int? = null,

    @SerializedName(value = "problemas", alternate = ["Problemas"])
    val problemas: List<ProblemaFrequenteDto> = emptyList()
)

data class ProblemaFrequenteDto(
    @SerializedName(value = "descricao", alternate = ["Descricao"])
    val descricao: String? = null,

    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int? = null,

    @SerializedName(value = "totalMotas", alternate = ["TotalMotas"])
    val totalMotas: Int? = null,

    @SerializedName(value = "ultimosCasos", alternate = ["UltimosCasos"])
    val ultimosCasos: List<ServicoDto>? = null
)

// ---------- Garantias por modelo ----------

data class GarantiasModeloResponse(
    @SerializedName(value = "modelo", alternate = ["Modelo"])
    val modelo: ServicoModeloDto? = null,

    @SerializedName(value = "total", alternate = ["Total"])
    val total: Int? = null,

    @SerializedName(value = "servicos", alternate = ["Servicos"])
    val servicos: List<ServicoDto> = emptyList()
)

// ---------- Meta (tipos e estados) ----------

data class ServicosMetaResponse(
    @SerializedName(value = "estados", alternate = ["Estados"])
    val estados: List<ServicosMetaItem> = emptyList(),

    @SerializedName(value = "tipos", alternate = ["Tipos"])
    val tipos: List<ServicosMetaItem> = emptyList()
)

data class ServicosMetaItem(
    @SerializedName(value = "id", alternate = ["Id", "ID"])
    val id: Int? = null,

    @SerializedName(value = "nome", alternate = ["Nome"])
    val nome: String? = null
)

// ---------- Requests ----------

data class CreateServicoRequest(
    @SerializedName(value = "IDMota", alternate = ["idMota"])
    val idMota: Int,

    @SerializedName(value = "Tipo", alternate = ["tipo"])
    val tipo: Int,

    @SerializedName(value = "Descricao", alternate = ["descricao"])
    val descricao: String? = null,

    @SerializedName(value = "Estado", alternate = ["estado"])
    val estado: Int = 0,

    @SerializedName(value = "DataServico", alternate = ["dataServico"])
    val dataServico: String? = null,

    @SerializedName(value = "NotasServico", alternate = ["notasServico"])
    val notasServico: String? = null
)

data class UpdateServicoEstadoRequest(
    @SerializedName(value = "Estado", alternate = ["estado"])
    val estado: Int,

    @SerializedName(value = "DataConclusao", alternate = ["dataConclusao"])
    val dataConclusao: String? = null
)

data class AddPecaAlteradaRequest(
    @SerializedName(value = "IDMotasPecasSN", alternate = ["idMotasPecasSN"])
    val idMotasPecasSN: Int,

    @SerializedName(value = "Observacoes", alternate = ["observacoes"])
    val observacoes: String? = null,

    @SerializedName(value = "NovoNumeroSerie", alternate = ["novoNumeroSerie"])
    val novoNumeroSerie: String? = null
)

// ---------- Iniciar / Finalizar Ordem ----------

data class IniciarOrdemResponse(
    @SerializedName(value = "message", alternate = ["Message"])
    val message: String? = null,

    @SerializedName(value = "ordemId", alternate = ["OrdemId"])
    val ordemId: Int? = null,

    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int? = null
)

data class FinalizarOrdemResponse(
    @SerializedName(value = "message", alternate = ["Message"])
    val message: String? = null,

    @SerializedName(value = "ordemId", alternate = ["OrdemId"])
    val ordemId: Int? = null,

    @SerializedName(value = "estado", alternate = ["Estado"])
    val estado: Int? = null,

    @SerializedName(value = "dataConclusao", alternate = ["DataConclusao"])
    val dataConclusao: String? = null
)

// ---------- VIN update ----------

data class UpdateVinRequest(
    @SerializedName(value = "numeroIdentificacao", alternate = ["NumeroIdentificacao"])
    val numeroIdentificacao: String
)

data class UpdateVinResponse(
    @SerializedName(value = "message", alternate = ["Message"])
    val message: String? = null,

    @SerializedName(value = "IDMota", alternate = ["idMota"])
    val idMota: Int? = null,

    @SerializedName(value = "NumeroIdentificacao", alternate = ["numeroIdentificacao"])
    val numeroIdentificacao: String? = null
)

// ---------- Peças SN Resumo ----------

data class PecasSnResumoResponse(
    @SerializedName(value = "motaId", alternate = ["MotaId"])
    val motaId: Int? = null,

    @SerializedName(value = "idModelo", alternate = ["IdModelo"])
    val idModelo: Int? = null,

    @SerializedName(value = "totalObrigatorias", alternate = ["TotalObrigatorias"])
    val totalObrigatorias: Int? = null,

    @SerializedName(value = "preenchidas", alternate = ["Preenchidas"])
    val preenchidas: Int? = null,

    @SerializedName(value = "ok", alternate = ["Ok"])
    val ok: Boolean? = null,

    @SerializedName(value = "pecas", alternate = ["Pecas"])
    val pecas: List<PecaSnResumoItemDto> = emptyList()
)

data class PecaSnResumoItemDto(
    @SerializedName(value = "IDPeca", alternate = ["idPeca"])
    val idPeca: Int? = null,

    @SerializedName(value = "PartNumber", alternate = ["partNumber"])
    val partNumber: String? = null,

    @SerializedName(value = "Descricao", alternate = ["descricao"])
    val descricao: String? = null,

    @SerializedName(value = "Preenchida", alternate = ["preenchida"])
    val preenchida: Boolean? = null,

    @SerializedName(value = "NumeroSerie", alternate = ["numeroSerie"])
    val numeroSerie: String? = null
)

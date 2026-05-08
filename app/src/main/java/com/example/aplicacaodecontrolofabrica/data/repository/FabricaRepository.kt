package com.example.aplicacaodecontrolofabrica.data.repository

import com.example.aplicacaodecontrolofabrica.data.dto.*

interface FabricaRepository {

    // ── Ordens ──
    suspend fun getOrdens(estado: Int? = null): List<OrdemProducaoDto>
    suspend fun getOrdem(id: Int): OrdemProducaoDto
    suspend fun getOrdemResumo(id: Int): OrdemResumoDto
    suspend fun criarOrdensFromEncomenda(encomendaId: Int): List<Int>
    suspend fun updateOrdemEstado(id: Int, estado: Int)
    suspend fun iniciarOrdem(id: Int): IniciarOrdemResponse
    suspend fun finalizarOrdem(id: Int): FinalizarOrdemResponse
    suspend fun getMotasDaOrdem(ordemId: Int): List<MotaDto>
    suspend fun criarMotaNaOrdem(ordemId: Int, body: CriarMotaRequest): Int
    suspend fun getOrdemFicha(id: Int): OrdemFichaDto
    suspend fun bloquearOrdem(id: Int, motivo: String): BloquearOrdemResponse
    suspend fun desbloquearOrdem(id: Int, resolucao: String?): DesbloquearOrdemResponse
    suspend fun getOrdemHistorico(id: Int): HistoricoOrdemResponse
    suspend fun marcarEmbalada(id: Int): MarcarEmbalagemResponse
    suspend fun marcarEnviada(id: Int): MarcarEnviadaResponse
    suspend fun getProntosExpedicao(): ProntosExpedicaoResponse
    suspend fun getOrdemUtilizadores(id: Int): OrdemUtilizadoresResponse
    suspend fun getDashboardResumo(): DashboardResumoDto
    suspend fun getAlertas(): AlertasApiResponse

    // ── Motas / VIN / peças SN ──
    suspend fun getMotas(): List<MotaDto>
    suspend fun getMota(motaId: Int): MotaDto
    suspend fun getMotaByVin(vin: String): MotaDto
    suspend fun criarMotaDireto(body: CriarMotaRequest): Int
    suspend fun updateMotaEstado(id: Int, estado: Int)
    suspend fun updateVin(id: Int, vin: String): UpdateVinResponse
    suspend fun getMotaPecasSn(motaId: Int): List<MotaPecaSnDto>
    suspend fun getMotaPecasSnResumo(motaId: Int): PecasSnResumoResponse
    suspend fun addMotaPecaSn(motaId: Int, body: AddPecaSnRequest): Int
    suspend fun deleteMotaPecaSn(idMotaPecaSn: Int)
    suspend fun getMotaPecasFixas(motaId: Int): PecaFixaResponse
    suspend fun updateMota(motaId: Int, body: CriarMotaRequest): MotaDto

    // ── Peças ──
    suspend fun getPecas(): List<PecaDto>

    // ── Checklists ──
    suspend fun getChecklists(): List<ChecklistDto>
    suspend fun getChecklistsDaOrdem(ordemId: Int): ChecklistsStatusDto
    suspend fun updateChecklistValue(ordemId: Int, tipo: String, checklistId: Int, value: Int)

    // ── Serviços / Manutenção / Garantias ──
    suspend fun getServicosMeta(): ServicosMetaResponse
    suspend fun getServicos(estado: Int? = null, motaId: Int? = null, modeloId: Int? = null, tipo: Int? = null, vin: String? = null, emAberto: Boolean? = null, q: String? = null): List<ServicoDto>
    suspend fun getServicosEmAberto(): ServicosEmAbertoResponse
    suspend fun getServico(id: Int): ServicoDto
    suspend fun criarServico(body: CreateServicoRequest): ServicoDto
    suspend fun updateServicoEstado(id: Int, estado: Int, dataConclusao: String? = null): ServicoDto
    suspend fun getHistoricoByMota(motaId: Int): HistoricoMotaResponse
    suspend fun getHistoricoByVin(vin: String): HistoricoMotaResponse
    suspend fun getHistoricoByModelo(idModelo: Int): HistoricoModeloResponse
    suspend fun getProblemasFrequentes(idModelo: Int): ProblemasFrequentesResponse
    suspend fun getGarantiasByModelo(idModelo: Int): GarantiasModeloResponse

    // ── Encomendas ──
    suspend fun getEncomendas(clienteId: Int? = null, estado: Int? = null): List<EncomendaDto>
    suspend fun getEncomenda(id: Int): EncomendaDto
    suspend fun criarEncomenda(body: CreateEncomendaRequest): Int

    // ── Contexto operacional ──
    suspend fun getUtilizadores(): List<UtilizadorDto>
    suspend fun getUtilizadorDetalhe(id: Int): UtilizadorDetailResponseDto
    suspend fun updateDisponibilidadeUtilizador(id: Int, ativo: Boolean)
    suspend fun getMotasDoUtilizador(id: Int, ativasOnly: Boolean = true): UtilizadorMotasResponseDto
    suspend fun getModelos(): List<ModeloDto>
    suspend fun getModelo(id: Int): ModeloDto
    suspend fun getClientes(): List<ClienteDto>
    suspend fun getCliente(id: Int): ClienteDto
}

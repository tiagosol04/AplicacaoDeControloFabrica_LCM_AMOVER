package com.example.aplicacaodecontrolofabrica.data.repository

import com.example.aplicacaodecontrolofabrica.core.network.ApiService
import com.example.aplicacaodecontrolofabrica.data.dto.*

class FabricaRepositoryImpl(
    private val api: ApiService
) : FabricaRepository {

    // ── Ordens ──
    override suspend fun getOrdens(estado: Int?) = api.getOrdens(estado)
    override suspend fun getOrdem(id: Int) = api.getOrdem(id)
    override suspend fun getOrdemResumo(id: Int) = api.getResumo(id)
    override suspend fun criarOrdensFromEncomenda(encomendaId: Int) = api.criarOrdensFromEncomenda(encomendaId).mapNotNull { it.id }
    override suspend fun updateOrdemEstado(id: Int, estado: Int) { api.updateEstadoOrdem(id, UpdateEstadoRequest(estado)) }
    override suspend fun iniciarOrdem(id: Int) = api.iniciarOrdem(id)
    override suspend fun finalizarOrdem(id: Int) = api.finalizarOrdem(id)
    override suspend fun getMotasDaOrdem(ordemId: Int) = api.getMotasDaOrdem(ordemId)
    override suspend fun criarMotaNaOrdem(ordemId: Int, body: CriarMotaRequest) = api.criarMota(ordemId, body).id ?: 0

    // ── Motas ──
    override suspend fun getMotas() = api.getMotas()
    override suspend fun getMota(motaId: Int) = api.getMota(motaId)
    override suspend fun getMotaByVin(vin: String) = api.getMotaByVin(vin)
    override suspend fun criarMotaDireto(body: CriarMotaRequest) = api.criarMotaDireto(body).id ?: 0
    override suspend fun updateMotaEstado(id: Int, estado: Int) { api.updateEstadoMota(id, UpdateEstadoRequest(estado)) }
    override suspend fun updateVin(id: Int, vin: String) = api.updateVin(id, UpdateVinRequest(vin))
    override suspend fun getMotaPecasSn(motaId: Int) = api.getMotaPecasSn(motaId)
    override suspend fun getMotaPecasSnResumo(motaId: Int) = api.getMotaPecasSnResumo(motaId)
    override suspend fun addMotaPecaSn(motaId: Int, body: AddPecaSnRequest) = api.addPecaSn(motaId, body).id ?: 0
    override suspend fun deleteMotaPecaSn(idMotaPecaSn: Int) { api.deleteMotaPecaSn(idMotaPecaSn) }

    // ── Peças ──
    override suspend fun getPecas() = api.getPecas()

    // ── Checklists ──
    override suspend fun getChecklists() = api.getChecklists()
    override suspend fun getChecklistsDaOrdem(ordemId: Int) = api.getChecklistsDaOrdem(ordemId)
    override suspend fun updateChecklistValue(ordemId: Int, tipo: String, checklistId: Int, value: Int) {
        val body = UpdateChecklistRequest(value = value)
        when (tipo.trim().lowercase()) {
            "montagem" -> api.updateChecklistMontagem(ordemId, checklistId, body)
            "embalagem" -> api.updateChecklistEmbalagem(ordemId, checklistId, body)
            "controlo", "controle" -> api.updateChecklistControlo(ordemId, checklistId, body)
            else -> error("Tipo de checklist inválido: $tipo")
        }
    }

    // ── Serviços ──
    override suspend fun getServicosMeta() = api.getServicosMeta()
    override suspend fun getServicos(estado: Int?, motaId: Int?, modeloId: Int?, tipo: Int?, vin: String?, emAberto: Boolean?, q: String?) = api.getServicos(estado, motaId, modeloId, tipo, vin, emAberto, q)
    override suspend fun getServicosEmAberto() = api.getServicosEmAberto()
    override suspend fun getServico(id: Int) = api.getServico(id)
    override suspend fun criarServico(body: CreateServicoRequest) = api.criarServico(body)
    override suspend fun updateServicoEstado(id: Int, estado: Int, dataConclusao: String?) = api.updateServicoEstado(id, UpdateServicoEstadoRequest(estado, dataConclusao))
    override suspend fun getHistoricoByMota(motaId: Int) = api.getHistoricoByMota(motaId)
    override suspend fun getHistoricoByVin(vin: String) = api.getHistoricoByVin(vin)
    override suspend fun getHistoricoByModelo(idModelo: Int) = api.getHistoricoByModelo(idModelo)
    override suspend fun getProblemasFrequentes(idModelo: Int) = api.getProblemasFrequentes(idModelo)
    override suspend fun getGarantiasByModelo(idModelo: Int) = api.getGarantiasByModelo(idModelo)

    // ── Encomendas ──
    override suspend fun getEncomendas(clienteId: Int?, estado: Int?) = api.getEncomendas(clienteId, estado)
    override suspend fun getEncomenda(id: Int) = api.getEncomenda(id)
    override suspend fun criarEncomenda(body: CreateEncomendaRequest) = api.criarEncomenda(body).id ?: 0

    // ── Contexto ──
    override suspend fun getUtilizadores() = api.getUtilizadores()
    override suspend fun getUtilizadorDetalhe(id: Int) = api.getUtilizadorDetalhe(id)
    override suspend fun updateDisponibilidadeUtilizador(id: Int, ativo: Boolean) { api.updateUtilizadorStatus(id, UpdateUserStatusRequest(ativo)) }
    override suspend fun getMotasDoUtilizador(id: Int, ativasOnly: Boolean) = api.getMotasDoUtilizador(id, ativasOnly)
    override suspend fun getModelos() = api.getModelos()
    override suspend fun getModelo(id: Int) = api.getModelo(id)
    override suspend fun getClientes() = api.getClientes()
    override suspend fun getCliente(id: Int) = api.getCliente(id)
}

package com.example.aplicacaodecontrolofabrica.features.operacao

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.dto.AddPecaSnRequest
import com.example.aplicacaodecontrolofabrica.data.dto.CriarMotaRequest
import com.example.aplicacaodecontrolofabrica.data.dto.MotaPecaSnDto
import com.example.aplicacaodecontrolofabrica.data.dto.PecaDto
import com.example.aplicacaodecontrolofabrica.data.mapper.DtoHelpers
import com.example.aplicacaodecontrolofabrica.data.mapper.toChecklistExecucoesPorGrupo
import com.example.aplicacaodecontrolofabrica.data.model.ChecklistExecucao
import com.example.aplicacaodecontrolofabrica.data.model.TipoChecklistUi
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoricoItemUi(
    val id: Int,
    val tipo: String,
    val descricao: String,
    val utilizadorNome: String,
    val dataOcorrencia: String?
)

data class PecaFixaUi(
    val idPeca: Int,
    val nome: String,
    val partNumber: String?,
    val quantidade: Int
)

data class MotaFichaUi(
    val motaId: Int,
    val vin: String?,
    val cor: String?,
    val estado: String,
    val pecasSn: List<MotaPecaSnDto> = emptyList(),
    val pecasFixas: List<PecaFixaUi> = emptyList()
)

data class FichaOperacionalUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,

    val ordemId: Int = 0,
    val numeroOrdem: String = "",
    val modeloNome: String = "",
    val clienteNome: String = "",
    val paisDestino: String = "",
    val estadoLabel: String = "",
    val estadoBase: Int = 0,
    val dataCriacaoIso: String? = null,
    val dataConclusaoIso: String? = null,

    val bloqueada: Boolean = false,
    val concluida: Boolean = false,
    val unidadeRegistada: Boolean = false,
    val vinList: List<String> = emptyList(),
    val vinPendente: Boolean = false,

    val montagemOk: Boolean = false,
    val embalagemOk: Boolean = false,
    val controloOk: Boolean = false,
    val totalServicos: Int = 0,
    val totalMotas: Int = 0,

    val prontidaoTexto: String = "",
    val observacaoEstado: String = "",
    val proximaAcao: String = "",
    val resumoRisco: String = "",

    val motasFicha: List<MotaFichaUi> = emptyList(),

    val checklistsMontagem: List<ChecklistExecucao> = emptyList(),
    val checklistsEmbalagem: List<ChecklistExecucao> = emptyList(),
    val checklistsControlo: List<ChecklistExecucao> = emptyList(),

    val pecasDisponiveis: List<PecaDto> = emptyList(),

    val historicoItems: List<HistoricoItemUi> = emptyList(),
    val utilizadoresAtribuidos: Int = 0,

    val actionLoading: Boolean = false,
    val actionSuccess: String? = null,
    val avisoExpedicao: String? = null,
    val avisoHistorico: String? = null,

    val tabAtual: Int = 0
)

private const val TAG = "OrdemDetalheVM"

class OrdemDetalheRealViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FichaOperacionalUiState(isLoading = true))
    val uiState: StateFlow<FichaOperacionalUiState> = _uiState.asStateFlow()

    fun setTab(index: Int) {
        _uiState.update { it.copy(tabAtual = index) }
    }

    fun clearActionSuccess() {
        _uiState.update { it.copy(actionSuccess = null) }
    }

    fun load(ordemId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, ordemId = ordemId) }
            runCatching {
                buildState(ordemId)
            }.onSuccess { state ->
                _uiState.value = state.copy(isLoading = false, errorMessage = null)
            }.onFailure { ex ->
                Log.e(TAG, "Falha ao carregar ordem $ordemId", ex)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Não foi possível carregar a ordem."
                    )
                }
            }
        }
    }

    private suspend fun buildState(ordemId: Int): FichaOperacionalUiState {
        // Lançar em paralelo o que é sempre necessário
        val fichaDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getOrdemFicha(ordemId) }.getOrNull()
        }
        val checklistsDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getChecklistsDaOrdem(ordemId) }.getOrNull()
        }
        val pecasDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getPecas() }.getOrDefault(emptyList())
        }
        val historicoDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getOrdemHistorico(ordemId) }.getOrNull()
        }
        val utilizadoresDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getOrdemUtilizadores(ordemId) }.getOrNull()
        }

        val ficha = fichaDeferred.await()

        // Resolver dados base: ficha se disponível, senão chamadas individuais
        val numeroOrdem: String
        val modeloNome: String
        val clienteNome: String
        val paisDestino: String
        val estadoBase: Int
        val dataCriacaoIso: String?
        val dataConclusaoIso: String?
        val montagemOk: Boolean
        val embalagemOk: Boolean
        val controloOk: Boolean
        val totalServicos: Int
        var motasDtos = emptyList<com.example.aplicacaodecontrolofabrica.data.dto.MotaDto>()

        if (ficha != null) {
            numeroOrdem = ficha.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº"
            modeloNome = ficha.modeloNome?.ifBlank { "Modelo por identificar" } ?: "Modelo por identificar"
            clienteNome = ficha.clienteNome?.ifBlank { "Cliente por identificar" } ?: "Cliente por identificar"
            paisDestino = ficha.paisDestino ?: "—"
            estadoBase = ficha.estado ?: 0
            dataCriacaoIso = ficha.dataCriacao
            dataConclusaoIso = ficha.dataConclusao
            montagemOk = ficha.montagemOk ?: false
            embalagemOk = ficha.embalagemOk ?: false
            controloOk = ficha.controloOk ?: false
            totalServicos = ficha.totalServicos ?: 0
            motasDtos = ficha.motas ?: emptyList()
        } else {
            // Fallback: chamadas individuais em paralelo
            val ordemDeferred = viewModelScope.async { fabricaRepository.getOrdem(ordemId) }
            val resumoDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getOrdemResumo(ordemId) }.getOrNull()
            }
            val motasDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }.getOrDefault(emptyList())
            }

            val ordem = ordemDeferred.await()
            val resumo = resumoDeferred.await()
            motasDtos = motasDeferred.await()

            val modeloDeferred = viewModelScope.async {
                val idModelo = ordem.idModelo
                if (idModelo != null) runCatching { fabricaRepository.getModelo(idModelo) }.getOrNull() else null
            }
            val clienteDeferred = viewModelScope.async {
                val idCliente = ordem.idCliente
                if (idCliente != null) runCatching { fabricaRepository.getCliente(idCliente) }.getOrNull() else null
            }

            numeroOrdem = ordem.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº"
            modeloNome = modeloDeferred.await()?.nomeModelo?.ifBlank { "Modelo por identificar" } ?: "Modelo por identificar"
            clienteNome = clienteDeferred.await()?.nome?.ifBlank { "Cliente por identificar" } ?: "Cliente por identificar"
            paisDestino = ordem.paisDestino ?: "—"
            estadoBase = ordem.estado ?: 0
            dataCriacaoIso = ordem.dataCriacao
            dataConclusaoIso = ordem.dataConclusao
            montagemOk = resumo?.checklists?.montagemOk ?: false
            embalagemOk = resumo?.checklists?.embalagemOk ?: false
            controloOk = resumo?.checklists?.controloOk ?: false
            totalServicos = resumo?.servicos ?: 0
        }

        // Se a ficha não incluiu motas, buscar individualmente
        if (motasDtos.isEmpty() && ficha?.motas == null) {
            motasDtos = runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }.getOrDefault(emptyList())
        }

        // Carregar peças SN e peças fixas por mota
        val motasFicha = motasDtos.mapNotNull { mota ->
            val motaId = mota.idMota ?: return@mapNotNull null
            val pecasSn = runCatching { fabricaRepository.getMotaPecasSn(motaId) }
                .onFailure { Log.e(TAG, "Falha peças SN mota $motaId", it) }
                .getOrDefault(emptyList())
            val pecasFixasResponse = runCatching { fabricaRepository.getMotaPecasFixas(motaId) }
                .onFailure { Log.d(TAG, "Peças fixas não disponíveis para mota $motaId") }
                .getOrNull()
            val pecasFixas = pecasFixasResponse?.pecas?.mapNotNull { pf ->
                val id = pf.idPeca ?: return@mapNotNull null
                PecaFixaUi(
                    idPeca = id,
                    nome = pf.nome ?: "Peça #$id",
                    partNumber = pf.partNumber,
                    quantidade = pf.quantidade ?: 1
                )
            } ?: emptyList()
            MotaFichaUi(
                motaId = motaId,
                vin = DtoHelpers.textOrNull(mota.numeroIdentificacao),
                cor = DtoHelpers.textOrNull(mota.cor),
                estado = DtoHelpers.mapEstadoMota(mota.estado),
                pecasSn = pecasSn,
                pecasFixas = pecasFixas
            )
        }

        val checklistsStatus = checklistsDeferred.await()
        val pecasDisponiveis = pecasDeferred.await()
        val historicoResponse = historicoDeferred.await()
        val utilizadoresData = utilizadoresDeferred.await()

        val checklistsPorGrupo = checklistsStatus?.toChecklistExecucoesPorGrupo() ?: emptyMap()

        val historicoItems = (historicoResponse?.historico ?: emptyList()).mapNotNull { item ->
            val id = item.id ?: return@mapNotNull null
            HistoricoItemUi(
                id = id,
                tipo = item.tipo ?: "EVENTO",
                descricao = item.descricao ?: "—",
                utilizadorNome = item.utilizadorNome ?: "Utilizador",
                dataOcorrencia = item.dataOcorrencia
            )
        }
        val avisoHistorico = historicoResponse?.aviso

        val utilizadoresAtribuidos = utilizadoresData?.total ?: 0

        val bloqueada = estadoBase == 3
        val concluida = estadoBase == 2
        val emProducao = estadoBase == 1

        val vinList = motasFicha.mapNotNull { it.vin }.filter { it.isNotBlank() }
        val unidadeRegistada = motasFicha.isNotEmpty()
        val vinPendente = unidadeRegistada && vinList.size < motasFicha.size

        val estadoLabel = when (estadoBase) {
            0 -> "Por arrancar"
            1 -> "Em produção"
            2 -> "Concluída"
            3 -> "Bloqueada"
            else -> "Desconhecido"
        }

        val prontidaoTexto = when {
            concluida -> "A ordem foi concluída e está fora da fila de produção."
            bloqueada -> "Existe um bloqueio. Esta ordem precisa de decisão de supervisão antes de avançar."
            !unidadeRegistada -> "A ordem existe mas ainda não tem unidade/mota associada."
            vinPendente -> "A unidade está registada mas a rastreabilidade ainda não está fechada — falta VIN."
            montagemOk && embalagemOk && !controloOk -> "Montagem e embalagem concluídas. Falta fechar o controlo final."
            emProducao -> "A ordem está em acompanhamento operacional normal."
            else -> "A ordem está pronta para ser acompanhada no terreno."
        }

        val observacaoEstado = buildString {
            append(if (unidadeRegistada) "Unidade registada" else "Sem unidade")
            if (vinPendente) append(" • VIN pendente")
            if (montagemOk) append(" • Montagem OK")
            if (embalagemOk) append(" • Embalagem OK")
            if (controloOk) append(" • Controlo OK")
            if (bloqueada) append(" • Bloqueio ativo")
        }

        val proximaAcao = when {
            bloqueada -> "Analisar bloqueio e decidir próximo passo"
            !unidadeRegistada -> "Registar unidade antes de avançar no fluxo"
            vinPendente -> "Fechar VIN/quadro para garantir rastreabilidade"
            montagemOk && embalagemOk && !controloOk -> "Validar controlo final"
            !emProducao && !concluida -> "Colocar ordem em produção"
            montagemOk && embalagemOk && controloOk && unidadeRegistada -> "Concluir ordem"
            else -> "Acompanhar ordem em curso"
        }

        val resumoRisco = when {
            bloqueada -> "Crítico"
            !unidadeRegistada || vinPendente -> "Rastreabilidade em risco"
            montagemOk && embalagemOk && !controloOk -> "Fecho pendente"
            concluida -> "Fechada"
            else -> "Estável"
        }

        return FichaOperacionalUiState(
            isLoading = false,
            isUpdating = false,
            errorMessage = null,
            ordemId = ordemId,
            numeroOrdem = numeroOrdem,
            modeloNome = modeloNome,
            clienteNome = clienteNome,
            paisDestino = paisDestino,
            estadoLabel = estadoLabel,
            estadoBase = estadoBase,
            dataCriacaoIso = dataCriacaoIso,
            dataConclusaoIso = dataConclusaoIso,
            bloqueada = bloqueada,
            concluida = concluida,
            unidadeRegistada = unidadeRegistada,
            vinList = vinList,
            vinPendente = vinPendente,
            montagemOk = montagemOk,
            embalagemOk = embalagemOk,
            controloOk = controloOk,
            totalServicos = totalServicos,
            totalMotas = motasFicha.size,
            prontidaoTexto = prontidaoTexto,
            observacaoEstado = observacaoEstado,
            proximaAcao = proximaAcao,
            resumoRisco = resumoRisco,
            motasFicha = motasFicha,
            checklistsMontagem = checklistsPorGrupo[TipoChecklistUi.MONTAGEM] ?: emptyList(),
            checklistsEmbalagem = checklistsPorGrupo[TipoChecklistUi.EMBALAGEM] ?: emptyList(),
            checklistsControlo = checklistsPorGrupo[TipoChecklistUi.CONTROLO] ?: emptyList(),
            pecasDisponiveis = pecasDisponiveis,
            historicoItems = historicoItems,
            avisoHistorico = avisoHistorico,
            utilizadoresAtribuidos = utilizadoresAtribuidos,
            tabAtual = _uiState.value.tabAtual
        )
    }

    // ── Ações de estado da ordem ──

    fun iniciarOrdem() {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, actionSuccess = null) }
            runCatching { fabricaRepository.iniciarOrdem(ordemId) }
                .onSuccess {
                    _uiState.update { it.copy(actionSuccess = "Ordem iniciada com sucesso.") }
                    load(ordemId)
                }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao iniciar ordem $ordemId", ex)
                    _uiState.update {
                        it.copy(isUpdating = false, errorMessage = ex.message ?: "Não foi possível iniciar a ordem.")
                    }
                }
        }
    }

    fun finalizarOrdem() {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, actionSuccess = null) }
            runCatching { fabricaRepository.finalizarOrdem(ordemId) }
                .onSuccess {
                    _uiState.update { it.copy(actionSuccess = "Ordem finalizada com sucesso.") }
                    load(ordemId)
                }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao finalizar ordem $ordemId", ex)
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            errorMessage = ex.message ?: "Não foi possível finalizar a ordem. Verifique se todos os requisitos estão cumpridos."
                        )
                    }
                }
        }
    }

    fun bloquearOrdem(motivo: String) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0 || motivo.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null) }
            runCatching { fabricaRepository.bloquearOrdem(ordemId, motivo.trim()) }
                .onSuccess { response ->
                    _uiState.update { it.copy(actionSuccess = response.message ?: "Ordem bloqueada.") }
                    load(ordemId)
                }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao bloquear ordem $ordemId", ex)
                    _uiState.update {
                        it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível bloquear a ordem.")
                    }
                }
        }
    }

    fun desbloquearOrdem(resolucao: String = "") {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null) }
            runCatching { fabricaRepository.desbloquearOrdem(ordemId, resolucao.trim().ifBlank { null }) }
                .onSuccess { response ->
                    _uiState.update { it.copy(actionSuccess = response.message ?: "Ordem desbloqueada.") }
                    load(ordemId)
                }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao desbloquear ordem $ordemId", ex)
                    _uiState.update {
                        it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível desbloquear a ordem.")
                    }
                }
        }
    }

    fun marcarEmbalada() {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null, avisoExpedicao = null) }
            runCatching { fabricaRepository.marcarEmbalada(ordemId) }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            actionSuccess = response.message ?: "Marcada como embalada.",
                            avisoExpedicao = response.aviso
                        )
                    }
                    load(ordemId)
                }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao marcar ordem $ordemId como embalada", ex)
                    _uiState.update {
                        it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível marcar como embalada.")
                    }
                }
        }
    }

    fun marcarEnviada() {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null, avisoExpedicao = null) }
            runCatching { fabricaRepository.marcarEnviada(ordemId) }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            actionSuccess = response.message ?: "Marcada como enviada.",
                            avisoExpedicao = response.aviso
                                ?: "Expedição ainda sem tabela própria na BD. A mota poderá transitar para Ativa. Proxy ativo."
                        )
                    }
                    load(ordemId)
                }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao marcar ordem $ordemId como enviada", ex)
                    _uiState.update {
                        it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível marcar como enviada.")
                    }
                }
        }
    }

    // ── Unidades (motas) ──

    fun registarMota(vin: String, cor: String) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        val corFinal = cor.trim().ifBlank { "Indefinida" }
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null) }
            runCatching {
                fabricaRepository.criarMotaNaOrdem(
                    ordemId,
                    CriarMotaRequest(
                        numeroIdentificacao = vin.trim(),
                        cor = corFinal,
                        quilometragem = 0.0,
                        estado = 0  // 0 = Em Produção (API força Estado=0, mas a app fica coerente)
                    )
                )
            }.onSuccess {
                _uiState.update { it.copy(actionSuccess = "Unidade registada com sucesso.") }
                load(ordemId)
            }.onFailure { ex ->
                Log.e(TAG, "Falha ao registar mota na ordem $ordemId", ex)
                _uiState.update {
                    it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível registar a unidade.")
                }
            }
        }
    }

    fun registarVin(motaId: Int, vin: String) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0 || motaId <= 0 || vin.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null) }
            runCatching { fabricaRepository.updateVin(motaId, vin.trim().uppercase()) }
                .onSuccess {
                    _uiState.update { it.copy(actionSuccess = "VIN registado: ${vin.trim().uppercase()}") }
                    load(ordemId)
                }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao registar VIN $vin na mota $motaId", ex)
                    _uiState.update {
                        it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível registar o VIN.")
                    }
                }
        }
    }

    // ── Checklists ──

    fun toggleChecklist(tipo: String, checklistId: Int, concluido: Boolean) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0 || checklistId <= 0) return
        val value = if (concluido) 1 else 0
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null) }
            runCatching { fabricaRepository.updateChecklistValue(ordemId, tipo, checklistId, value) }
                .onSuccess { load(ordemId) }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao atualizar checklist $checklistId (tipo=$tipo)", ex)
                    _uiState.update {
                        it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível atualizar o checklist.")
                    }
                }
        }
    }

    // ── Peças com número de série ──

    fun addPecaSn(motaId: Int, pecaId: Int, serialNumber: String) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0 || motaId <= 0 || pecaId <= 0 || serialNumber.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null) }
            runCatching {
                fabricaRepository.addMotaPecaSn(
                    motaId,
                    AddPecaSnRequest(idPeca = pecaId, numeroSerie = serialNumber.trim())
                )
            }.onSuccess {
                _uiState.update { it.copy(actionSuccess = "Peça com SN registada.") }
                load(ordemId)
            }.onFailure { ex ->
                Log.e(TAG, "Falha ao adicionar peça SN $serialNumber à mota $motaId", ex)
                _uiState.update {
                    it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível registar a peça. Verifique se o número de série já existe.")
                }
            }
        }
    }

    fun deletePecaSn(idMotaPecaSn: Int) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0 || idMotaPecaSn <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null) }
            runCatching { fabricaRepository.deleteMotaPecaSn(idMotaPecaSn) }
                .onSuccess { load(ordemId) }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao remover peça SN $idMotaPecaSn", ex)
                    _uiState.update {
                        it.copy(actionLoading = false, errorMessage = ex.message ?: "Não foi possível remover a peça.")
                    }
                }
        }
    }
}

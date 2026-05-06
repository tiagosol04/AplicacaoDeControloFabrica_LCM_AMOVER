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

/** Representa uma mota dentro da ficha operacional, com VIN e peças SN carregadas. */
data class MotaFichaUi(
    val motaId: Int,
    val vin: String?,
    val cor: String?,
    val estado: String,
    val pecasSn: List<MotaPecaSnDto> = emptyList()
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

    // ── Dados completos das motas (unidades + VIN + peças SN) ──
    val motasFicha: List<MotaFichaUi> = emptyList(),

    // ── Checklists por grupo ──
    val checklistsMontagem: List<ChecklistExecucao> = emptyList(),
    val checklistsEmbalagem: List<ChecklistExecucao> = emptyList(),
    val checklistsControlo: List<ChecklistExecucao> = emptyList(),

    // ── Peças disponíveis para registo de número de série ──
    val pecasDisponiveis: List<PecaDto> = emptyList(),

    // ── Estado de ação em curso e feedback ──
    val actionLoading: Boolean = false,
    val actionSuccess: String? = null,

    // ── Tab atual (0=Geral, 1=Unidades, 2=Checklists, 3=Peças, 4=Ações) ──
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
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, ordemId = ordemId)
            }
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
        val ordem = fabricaRepository.getOrdem(ordemId)

        val resumoDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getOrdemResumo(ordemId) }
                .onFailure { Log.e(TAG, "Falha ao obter resumo da ordem $ordemId", it) }
                .getOrNull()
        }
        val motasDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }
                .onFailure { Log.e(TAG, "Falha ao obter motas da ordem $ordemId", it) }
                .getOrDefault(emptyList())
        }
        val modeloDeferred = viewModelScope.async {
            val idModelo = ordem.idModelo
            if (idModelo != null) {
                runCatching { fabricaRepository.getModelo(idModelo) }
                    .onFailure { Log.e(TAG, "Falha ao obter modelo $idModelo", it) }
                    .getOrNull()
            } else null
        }
        val clienteDeferred = viewModelScope.async {
            val idCliente = ordem.idCliente
            if (idCliente != null) {
                runCatching { fabricaRepository.getCliente(idCliente) }
                    .onFailure { Log.e(TAG, "Falha ao obter cliente $idCliente", it) }
                    .getOrNull()
            } else null
        }
        val checklistsDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getChecklistsDaOrdem(ordemId) }
                .onFailure { Log.e(TAG, "Falha ao obter checklists da ordem $ordemId", it) }
                .getOrNull()
        }
        val pecasDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getPecas() }
                .onFailure { Log.e(TAG, "Falha ao obter peças disponíveis", it) }
                .getOrDefault(emptyList())
        }

        val resumo = resumoDeferred.await()
        val motasDtos = motasDeferred.await()
        val modelo = modeloDeferred.await()
        val cliente = clienteDeferred.await()
        val checklistsStatus = checklistsDeferred.await()
        val pecasDisponiveis = pecasDeferred.await()

        // Carregar peças SN por mota em paralelo
        val motasFicha = motasDtos.mapNotNull { mota ->
            val motaId = mota.idMota ?: return@mapNotNull null
            val pecasSn = runCatching { fabricaRepository.getMotaPecasSn(motaId) }
                .onFailure { Log.e(TAG, "Falha ao obter peças SN da mota $motaId", it) }
                .getOrDefault(emptyList())
            MotaFichaUi(
                motaId = motaId,
                vin = DtoHelpers.textOrNull(mota.numeroIdentificacao),
                cor = DtoHelpers.textOrNull(mota.cor),
                estado = DtoHelpers.mapEstadoMota(mota.estado),
                pecasSn = pecasSn
            )
        }

        // Checklists por grupo
        val checklistsPorGrupo = checklistsStatus?.toChecklistExecucoesPorGrupo() ?: emptyMap()

        val estadoBase = ordem.estado ?: 0
        val bloqueada = estadoBase == 3
        val concluida = estadoBase == 2
        val emProducao = estadoBase == 1

        val montagemOk = resumo?.checklists?.montagemOk ?: false
        val embalagemOk = resumo?.checklists?.embalagemOk ?: false
        val controloOk = resumo?.checklists?.controloOk ?: false

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
            numeroOrdem = ordem.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº",
            modeloNome = modelo?.nomeModelo?.ifBlank { "Modelo por identificar" } ?: "Modelo por identificar",
            clienteNome = cliente?.nome?.ifBlank { "Cliente por identificar" } ?: "Cliente por identificar",
            paisDestino = ordem.paisDestino ?: "—",
            estadoLabel = estadoLabel,
            estadoBase = estadoBase,
            dataCriacaoIso = ordem.dataCriacao,
            dataConclusaoIso = ordem.dataConclusao,
            bloqueada = bloqueada,
            concluida = concluida,
            unidadeRegistada = unidadeRegistada,
            vinList = vinList,
            vinPendente = vinPendente,
            montagemOk = montagemOk,
            embalagemOk = embalagemOk,
            controloOk = controloOk,
            totalServicos = resumo?.servicos ?: 0,
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
        if (ordemId <= 0) return
        // Nota: o backend não tem endpoint dedicado POST /api/ordens/{id}/bloquear com motivo.
        // Usamos updateEstadoOrdem(3). O motivo fica apenas no log local até o backend suportar.
        // Ver BACKEND_REQUIREMENTS.md para o endpoint necessário.
        Log.i(TAG, "Bloquear ordem $ordemId — motivo: $motivo")
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null) }
            runCatching { fabricaRepository.updateOrdemEstado(ordemId, 3) }
                .onSuccess {
                    _uiState.update { it.copy(actionSuccess = "Ordem bloqueada.") }
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

    fun desbloquearOrdem() {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        // Nota: o backend não tem endpoint dedicado POST /api/ordens/{id}/desbloquear.
        // Usamos updateEstadoOrdem(1) para colocar em produção.
        // Ver BACKEND_REQUIREMENTS.md para o endpoint necessário.
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, errorMessage = null, actionSuccess = null) }
            runCatching { fabricaRepository.updateOrdemEstado(ordemId, 1) }
                .onSuccess {
                    _uiState.update { it.copy(actionSuccess = "Ordem desbloqueada — colocada em produção.") }
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

    fun atualizarEstado(estado: Int) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }
            runCatching { fabricaRepository.updateOrdemEstado(ordemId, estado) }
                .onSuccess { load(ordemId) }
                .onFailure { ex ->
                    Log.e(TAG, "Falha ao atualizar estado da ordem $ordemId para $estado", ex)
                    _uiState.update {
                        it.copy(isUpdating = false, errorMessage = ex.message ?: "Não foi possível atualizar o estado.")
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
                        estado = 3 // Em produção
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

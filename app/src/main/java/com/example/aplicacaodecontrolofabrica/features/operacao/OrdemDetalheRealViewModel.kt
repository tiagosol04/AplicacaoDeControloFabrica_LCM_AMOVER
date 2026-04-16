package com.example.aplicacaodecontrolofabrica.features.operacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val resumoRisco: String = ""
)

class OrdemDetalheRealViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FichaOperacionalUiState(isLoading = true))
    val uiState: StateFlow<FichaOperacionalUiState> = _uiState.asStateFlow()

    fun load(ordemId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    ordemId = ordemId
                )
            }

            runCatching {
                buildState(ordemId)
            }.onSuccess { state ->
                _uiState.value = state.copy(isLoading = false, errorMessage = null)
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Não foi possível carregar o centro da ordem."
                    )
                }
            }
        }
    }

    fun atualizarEstado(estado: Int) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }

            runCatching {
                fabricaRepository.updateOrdemEstado(ordemId, estado)
            }.onSuccess {
                load(ordemId)
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        errorMessage = ex.message ?: "Não foi possível atualizar o estado da ordem."
                    )
                }
            }
        }
    }

    private suspend fun buildState(ordemId: Int): FichaOperacionalUiState {
        val ordem = fabricaRepository.getOrdem(ordemId)

        val resumoDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getOrdemResumo(ordemId) }.getOrNull()
        }
        val motasDeferred = viewModelScope.async {
            runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }.getOrDefault(emptyList())
        }
        val modeloDeferred = viewModelScope.async {
            val idModelo = ordem.idModelo
            if (idModelo != null) runCatching { fabricaRepository.getModelo(idModelo) }.getOrNull() else null
        }
        val clienteDeferred = viewModelScope.async {
            val idCliente = ordem.idCliente
            if (idCliente != null) runCatching { fabricaRepository.getCliente(idCliente) }.getOrNull() else null
        }

        val resumo = resumoDeferred.await()
        val motas = motasDeferred.await()
        val modelo = modeloDeferred.await()
        val cliente = clienteDeferred.await()

        val estadoBase = ordem.estado ?: 0
        val bloqueada = estadoBase == 3
        val concluida = estadoBase == 2
        val emProducao = estadoBase == 1

        val montagemOk = resumo?.checklists?.montagemOk ?: false
        val embalagemOk = resumo?.checklists?.embalagemOk ?: false
        val controloOk = resumo?.checklists?.controloOk ?: false

        val vinList = motas
            .mapNotNull { it.numeroIdentificacao?.trim() }
            .filter { it.isNotBlank() }

        val unidadeRegistada = motas.isNotEmpty()
        val vinPendente = unidadeRegistada && vinList.size < motas.size

        val estadoLabel = when (estadoBase) {
            0 -> "Por arrancar"
            1 -> "Em produção"
            2 -> "Concluída"
            3 -> "Bloqueada"
            else -> "Desconhecido"
        }

        val prontidaoTexto = when {
            concluida -> "A unidade já foi concluída e está fora da fila de decisão do dia."
            bloqueada -> "Existe um bloqueio aberto. Esta ordem precisa de decisão de supervisão."
            !unidadeRegistada -> "A ordem existe, mas ainda não tem unidade associada."
            vinPendente -> "A unidade está registada, mas a rastreabilidade ainda não ficou fechada."
            montagemOk && embalagemOk && !controloOk -> "A ordem está quase pronta. Falta apenas fechar a validação final."
            emProducao -> "A ordem está em acompanhamento operacional normal."
            else -> "A ordem está pronta para ser acompanhada e validada no terreno."
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
            totalMotas = motas.size,
            prontidaoTexto = prontidaoTexto,
            observacaoEstado = observacaoEstado,
            proximaAcao = proximaAcao,
            resumoRisco = resumoRisco
        )
    }

    fun iniciarOrdem() {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }
            runCatching { fabricaRepository.iniciarOrdem(ordemId) }
                .onSuccess { load(ordemId) }
                .onFailure { ex -> _uiState.update { it.copy(isUpdating = false, errorMessage = ex.message ?: "Não foi possível iniciar a ordem.") } }
        }
    }

    fun finalizarOrdem() {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }
            runCatching { fabricaRepository.finalizarOrdem(ordemId) }
                .onSuccess { load(ordemId) }
                .onFailure { ex -> _uiState.update { it.copy(isUpdating = false, errorMessage = ex.message ?: "Não foi possível finalizar a ordem. Verifique se todos os requisitos estão cumpridos.") } }
        }
    }

    fun atualizarVin(motaId: Int, vin: String) {
        val ordemId = _uiState.value.ordemId
        if (ordemId <= 0 || motaId <= 0 || vin.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null) }
            runCatching { fabricaRepository.updateVin(motaId, vin) }
                .onSuccess { load(ordemId) }
                .onFailure { ex -> _uiState.update { it.copy(isUpdating = false, errorMessage = ex.message ?: "Não foi possível atualizar o VIN.") } }
        }
    }

}

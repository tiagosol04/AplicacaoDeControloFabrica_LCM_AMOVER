package com.example.aplicacaodecontrolofabrica.features.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.model.CoberturaTecnicaUi
import com.example.aplicacaodecontrolofabrica.data.model.HistoricoTecnicoUi
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoricoUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val historico: List<HistoricoTecnicoUi> = emptyList()
)

class HistoricoViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoricoUiState(isLoading = true))
    val uiState: StateFlow<HistoricoUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                buildHistorico()
            }.onSuccess { historico ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        historico = historico
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Não foi possível carregar a rastreabilidade."
                    )
                }
            }
        }
    }

    private suspend fun buildHistorico(): List<HistoricoTecnicoUi> {
        val ordens = fabricaRepository.getOrdens()
        if (ordens.isEmpty()) return emptyList()

        val modelosMap = runCatching {
            fabricaRepository.getModelos().associateBy { it.idModelo ?: 0 }
        }.getOrDefault(emptyMap())

        return ordens.mapNotNull { ordem ->
            val ordemId = ordem.idOrdemProducao ?: return@mapNotNull null

            val resumoDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getOrdemResumo(ordemId) }.getOrNull()
            }
            val motasDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }.getOrDefault(emptyList())
            }

            val resumo = resumoDeferred.await()
            val motas = motasDeferred.await()
            val motaPrincipal = motas.firstOrNull()

            val modeloNome = modelosMap[ordem.idModelo ?: -1]
                ?.nomeModelo
                ?.takeIf { !it.isNullOrBlank() }
                ?: "Modelo por identificar"

            val checklistsOk = resumo?.checklists?.montagemOk == true &&
                    resumo.checklists.embalagemOk &&
                    resumo.checklists.controloOk

            val cobertura = CoberturaTecnicaUi.POR_INTEGRAR

            val resumoTecnico = buildString {
                append(modeloNome)

                motaPrincipal?.numeroIdentificacao
                    ?.takeIf { it.isNotBlank() }
                    ?.let {
                        append(" • VIN ")
                        append(it)
                    }

                ordem.paisDestino
                    ?.takeIf { it.isNotBlank() }
                    ?.let {
                        append(" • ")
                        append(it)
                    }

                if ((resumo?.servicos ?: 0) > 0) {
                    append(" • ")
                    append(resumo?.servicos ?: 0)
                    append(" serviço(s)")
                } else {
                    append(" • sem serviços registados")
                }

                append(
                    if (ordem.estado == 2) {
                        " • unidade concluída"
                    } else {
                        " • unidade em acompanhamento"
                    }
                )
            }

            HistoricoTecnicoUi(
                ordemId = ordemId,
                numeroOrdem = ordem.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº",
                motaId = motaPrincipal?.idMota,
                vin = motaPrincipal?.numeroIdentificacao,
                modeloId = ordem.idModelo,
                clienteId = ordem.idCliente,
                paisDestino = ordem.paisDestino,
                dataConclusaoIso = ordem.dataConclusao ?: ordem.dataCriacao,
                totalServicos = resumo?.servicos ?: 0,
                checklistsOk = checklistsOk,
                unidadeConcluida = ordem.estado == 2,
                cobertura = cobertura,
                resumoTecnico = resumoTecnico,
                totalGarantias = 0,
                totalOcorrencias = if ((resumo?.servicos ?: 0) > 0) 1 else 0,
                problemaRecorrente = false
            )
        }.sortedWith(
            compareByDescending<HistoricoTecnicoUi> { it.unidadeConcluida }
                .thenByDescending { !it.vin.isNullOrBlank() }
                .thenByDescending { it.dataConclusaoIso ?: "" }
        )
    }
}
package com.example.aplicacaodecontrolofabrica.features.servicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.dto.PecaAlteradaDto
import com.example.aplicacaodecontrolofabrica.data.dto.ProblemaFrequenteDto
import com.example.aplicacaodecontrolofabrica.data.dto.ServicoDto
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServicoDetalheUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val servico: ServicoDto? = null,
    val pecasAlteradas: List<PecaAlteradaDto> = emptyList(),
    val problemasModelo: List<ProblemaFrequenteDto> = emptyList(),
    val totalGarantiasModelo: Int = 0
)

class ServicoDetalheViewModel(
    private val repo: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicoDetalheUiState(isLoading = true))
    val uiState: StateFlow<ServicoDetalheUiState> = _uiState.asStateFlow()

    fun load(servicoId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val servico = repo.getServico(servicoId)
                val pecas = runCatching { servico.pecasAlteradas ?: emptyList() }.getOrDefault(emptyList())

                // Carregar problemas frequentes e garantias do modelo, se disponível
                val idModelo = servico.mota?.idModelo
                val problemas = if (idModelo != null) {
                    runCatching { repo.getProblemasFrequentes(idModelo).problemas }.getOrDefault(emptyList())
                } else emptyList()
                val totalGarantias = if (idModelo != null) {
                    runCatching { repo.getGarantiasByModelo(idModelo).total ?: 0 }.getOrDefault(0)
                } else 0

                Triple(servico, problemas, totalGarantias) to pecas
            }.onSuccess { (data, pecas) ->
                val (servico, problemas, totalGarantias) = data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        servico = servico,
                        pecasAlteradas = pecas,
                        problemasModelo = problemas.take(5),
                        totalGarantiasModelo = totalGarantias
                    )
                }
            }.onFailure { ex ->
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message ?: "Erro ao carregar serviço.") }
            }
        }
    }

    fun iniciarServico(servicoId: Int) = atualizarEstado(servicoId, 1)
    fun concluirServico(servicoId: Int) = atualizarEstado(servicoId, 2)

    private fun atualizarEstado(servicoId: Int, estado: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, errorMessage = null, successMessage = null) }
            runCatching {
                repo.updateServicoEstado(servicoId, estado)
            }.onSuccess {
                _uiState.update { it.copy(isUpdating = false, successMessage = "Estado atualizado.") }
                load(servicoId)
            }.onFailure { ex ->
                _uiState.update { it.copy(isUpdating = false, errorMessage = ex.message ?: "Erro ao atualizar estado.") }
            }
        }
    }
}

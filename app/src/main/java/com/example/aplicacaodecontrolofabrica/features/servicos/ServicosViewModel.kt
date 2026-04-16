package com.example.aplicacaodecontrolofabrica.features.servicos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.dto.ServicoDto
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FiltroServicoUi { TODOS, EM_ABERTO, GARANTIA, AVARIA, CONCLUIDOS }

data class ServicosUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val servicos: List<ServicoDto> = emptyList(),
    val totalEmAberto: Int = 0,
    val totalGarantias: Int = 0,
    val totalAvarias: Int = 0,
    val totalConcluidos: Int = 0,
    val filtroAtivo: FiltroServicoUi = FiltroServicoUi.TODOS,
    val pesquisa: String = ""
)

class ServicosViewModel(
    private val repo: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicosUiState(isLoading = true))
    val uiState: StateFlow<ServicosUiState> = _uiState.asStateFlow()

    private var todosServicos: List<ServicoDto> = emptyList()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repo.getServicos()
            }.onSuccess { lista ->
                todosServicos = lista
                val emAberto = lista.count { (it.estado ?: 0) != 2 }
                val garantias = lista.count { (it.tipo ?: 0) == 3 }
                val avarias = lista.count { (it.tipo ?: 0) == 2 }
                val concluidos = lista.count { (it.estado ?: 0) == 2 }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        servicos = lista,
                        totalEmAberto = emAberto,
                        totalGarantias = garantias,
                        totalAvarias = avarias,
                        totalConcluidos = concluidos
                    )
                }
                aplicarFiltro()
            }.onFailure { ex ->
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message ?: "Erro ao carregar serviços.") }
            }
        }
    }

    fun setFiltro(f: FiltroServicoUi) {
        _uiState.update { it.copy(filtroAtivo = f) }
        aplicarFiltro()
    }

    fun setPesquisa(q: String) {
        _uiState.update { it.copy(pesquisa = q) }
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        val filtro = _uiState.value.filtroAtivo
        val q = _uiState.value.pesquisa.trim()
        val filtrados = todosServicos
            .filter { s ->
                when (filtro) {
                    FiltroServicoUi.TODOS -> true
                    FiltroServicoUi.EM_ABERTO -> (s.estado ?: 0) != 2
                    FiltroServicoUi.GARANTIA -> (s.tipo ?: 0) == 3
                    FiltroServicoUi.AVARIA -> (s.tipo ?: 0) == 2
                    FiltroServicoUi.CONCLUIDOS -> (s.estado ?: 0) == 2
                }
            }
            .filter { s ->
                q.isBlank() || listOfNotNull(
                    s.descricao, s.notasServico, s.numeroIdentificacao,
                    s.modeloNome, s.tipoNome, s.estadoNome
                ).any { it.contains(q, ignoreCase = true) }
            }
        _uiState.update { it.copy(servicos = filtrados) }
    }
}

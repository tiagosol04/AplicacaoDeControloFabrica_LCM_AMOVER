package com.example.aplicacaodecontrolofabrica.features.encomendas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.dto.ClienteDto
import com.example.aplicacaodecontrolofabrica.data.dto.EncomendaDto
import com.example.aplicacaodecontrolofabrica.data.dto.ModeloDto
import com.example.aplicacaodecontrolofabrica.data.mapper.DtoHelpers
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EncomendaUi(
    val id: Int,
    val clienteNome: String,
    val modeloNome: String,
    val quantidade: Int,
    val estadoLabel: String,
    val dataCriacao: String,
    val dataEntrega: String?,
    val estado: Int
)

data class EncomendasUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val encomendas: List<EncomendaUi> = emptyList(),
    val totalPendentes: Int = 0,
    val totalEmProducao: Int = 0,
    val totalConcluidas: Int = 0
)

class EncomendasViewModel(
    private val repo: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EncomendasUiState(isLoading = true))
    val uiState: StateFlow<EncomendasUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val encomendas = repo.getEncomendas()
                val clientes = runCatching { repo.getClientes().associateBy { it.idCliente ?: 0 } }.getOrDefault(emptyMap())
                val modelos = runCatching { repo.getModelos().associateBy { it.idModelo ?: 0 } }.getOrDefault(emptyMap())

                encomendas.map { e ->
                    val estado = e.estado ?: 0
                    EncomendaUi(
                        id = e.idEncomenda ?: 0,
                        clienteNome = clientes[e.idCliente ?: 0]?.nome ?: "Cliente #${e.idCliente}",
                        modeloNome = modelos[e.idModelo ?: 0]?.nomeModelo ?: "Modelo #${e.idModelo}",
                        quantidade = e.quantidade ?: 0,
                        estadoLabel = DtoHelpers.mapEstadoEncomenda(estado),
                        dataCriacao = e.dataCriacao?.take(10) ?: "—",
                        dataEntrega = e.dataEntrega?.take(10),
                        estado = estado
                    )
                }
            }.onSuccess { lista ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        encomendas = lista,
                        totalPendentes = lista.count { e -> e.estado == 0 },
                        totalEmProducao = lista.count { e -> e.estado == 1 },
                        totalConcluidas = lista.count { e -> e.estado >= 2 }
                    )
                }
            }.onFailure { ex ->
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message ?: "Erro ao carregar encomendas.") }
            }
        }
    }
}

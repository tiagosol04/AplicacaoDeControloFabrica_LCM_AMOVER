package com.example.aplicacaodecontrolofabrica.features.encomendas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.dto.OrdemProducaoDto
import com.example.aplicacaodecontrolofabrica.data.mapper.DtoHelpers
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EncomendaOrdemUi(
    val ordemId: Int,
    val numeroOrdem: String,
    val estadoLabel: String,
    val bloqueada: Boolean
)

data class EncomendaDetalheUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val encomendaId: Int = 0,
    val clienteNome: String = "",
    val modeloNome: String = "",
    val quantidade: Int = 0,
    val estadoLabel: String = "",
    val estado: Int = 0,
    val dataCriacao: String = "",
    val dataEntrega: String? = null,
    val ordens: List<EncomendaOrdemUi> = emptyList(),
    val actionLoading: Boolean = false,
    val actionSuccess: String? = null,
    val actionError: String? = null
)

class EncomendaDetalheViewModel(
    private val repo: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EncomendaDetalheUiState(isLoading = true))
    val uiState: StateFlow<EncomendaDetalheUiState> = _uiState.asStateFlow()

    fun load(encomendaId: Int) {
        if (_uiState.value.encomendaId == encomendaId && !_uiState.value.isLoading && _uiState.value.errorMessage == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val encDeferred = async { repo.getEncomenda(encomendaId) }
                val clientesDeferred = async {
                    runCatching { repo.getClientes().associateBy { it.idCliente ?: 0 } }
                        .onFailure { Log.e("EncomendaDetalheVM", "Falha ao carregar clientes", it) }
                        .getOrDefault(emptyMap())
                }
                val modelosDeferred = async {
                    runCatching { repo.getModelos().associateBy { it.idModelo ?: 0 } }
                        .onFailure { Log.e("EncomendaDetalheVM", "Falha ao carregar modelos", it) }
                        .getOrDefault(emptyMap())
                }
                val ordensDeferred = async {
                    runCatching { repo.getOrdens() }
                        .onFailure { Log.e("EncomendaDetalheVM", "Falha ao carregar ordens", it) }
                        .getOrDefault(emptyList())
                }

                val enc = encDeferred.await()
                val clientes = clientesDeferred.await()
                val modelos = modelosDeferred.await()
                val todasOrdens = ordensDeferred.await()

                val ordensAssociadas = todasOrdens
                    .filter { it.idEncomenda == encomendaId }
                    .map { o ->
                        val estado = o.estado ?: 0
                        EncomendaOrdemUi(
                            ordemId = o.idOrdemProducao ?: 0,
                            numeroOrdem = o.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº",
                            estadoLabel = when (estado) {
                                0 -> "Por arrancar"
                                1 -> "Em produção"
                                2 -> "Concluída"
                                3 -> "Bloqueada"
                                else -> "Desconhecido"
                            },
                            bloqueada = estado == 3
                        )
                    }

                Triple(enc, clientes to modelos, ordensAssociadas)
            }.onSuccess { (enc, lookup, ordens) ->
                val (clientes, modelos) = lookup
                val estado = enc.estado ?: 0
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        encomendaId = encomendaId,
                        clienteNome = clientes[enc.idCliente ?: 0]?.nome ?: "Cliente #${enc.idCliente}",
                        modeloNome = modelos[enc.idModelo ?: 0]?.nomeModelo ?: "Modelo #${enc.idModelo}",
                        quantidade = enc.quantidade ?: 0,
                        estadoLabel = DtoHelpers.mapEstadoEncomenda(estado),
                        estado = estado,
                        dataCriacao = enc.dataCriacao?.take(10) ?: enc.dataEncomenda?.take(10) ?: "—",
                        dataEntrega = enc.dataEntrega?.take(10),
                        ordens = ordens
                    )
                }
            }.onFailure { ex ->
                Log.e("EncomendaDetalheVM", "Falha ao carregar encomenda $encomendaId", ex)
                _uiState.update { it.copy(isLoading = false, errorMessage = ex.message ?: "Erro ao carregar encomenda.") }
            }
        }
    }

    fun criarOrdensFromEncomenda(encomendaId: Int) {
        if (_uiState.value.actionLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionLoading = true, actionError = null, actionSuccess = null) }
            runCatching { repo.criarOrdensFromEncomenda(encomendaId) }
                .onSuccess { idsCreated ->
                    _uiState.update {
                        it.copy(
                            actionLoading = false,
                            actionSuccess = "${idsCreated.size} ordem(ns) criada(s) com sucesso."
                        )
                    }
                    load(encomendaId)
                }
                .onFailure { ex ->
                    Log.e("EncomendaDetalheVM", "Falha ao criar ordens da encomenda $encomendaId", ex)
                    _uiState.update {
                        it.copy(
                            actionLoading = false,
                            actionError = ex.message ?: "Erro ao criar ordens."
                        )
                    }
                }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(actionSuccess = null, actionError = null) }
    }
}

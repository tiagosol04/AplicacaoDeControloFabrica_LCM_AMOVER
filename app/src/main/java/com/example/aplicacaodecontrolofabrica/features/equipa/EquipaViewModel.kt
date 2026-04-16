package com.example.aplicacaodecontrolofabrica.features.equipa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.mapper.DtoHelpers
import com.example.aplicacaodecontrolofabrica.data.model.ColaboradorFabricaUi
import com.example.aplicacaodecontrolofabrica.data.model.EquipaResumoUi
import com.example.aplicacaodecontrolofabrica.data.model.EstadoColaboradorUi
import com.example.aplicacaodecontrolofabrica.data.model.FiltroEquipaUi
import com.example.aplicacaodecontrolofabrica.data.model.UnidadeAssociadaUi
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipaUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,
    val pesquisa: String = "",
    val filtro: FiltroEquipaUi = FiltroEquipaUi.TODOS,
    val resumo: EquipaResumoUi = EquipaResumoUi(),
    val colaboradores: List<ColaboradorFabricaUi> = emptyList()
)

class EquipaViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EquipaUiState(isLoading = true))
    val uiState: StateFlow<EquipaUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    feedbackMessage = null
                )
            }

            runCatching { buildEquipa() }
                .onSuccess { colaboradores ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            colaboradores = colaboradores,
                            resumo = colaboradores.toResumo(),
                            errorMessage = null
                        )
                    }
                }
                .onFailure { ex ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ex.message ?: "Não foi possível carregar o turno."
                        )
                    }
                }
        }
    }

    fun atualizarPesquisa(valor: String) {
        _uiState.update { it.copy(pesquisa = valor) }
    }

    fun atualizarFiltro(filtro: FiltroEquipaUi) {
        _uiState.update { it.copy(filtro = filtro) }
    }

    fun limparFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    fun alternarDisponibilidade(colaborador: ColaboradorFabricaUi) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    feedbackMessage = null,
                    errorMessage = null
                )
            }

            val novoEstado = !colaborador.ativoNaBaseDados

            runCatching {
                fabricaRepository.updateDisponibilidadeUtilizador(colaborador.id, novoEstado)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        feedbackMessage = if (novoEstado) {
                            "${colaborador.nome} voltou a ficar disponível na base operacional."
                        } else {
                            "${colaborador.nome} foi marcado como indisponível na base operacional."
                        }
                    )
                }
                refresh()
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = ex.message ?: "Não foi possível atualizar a disponibilidade."
                    )
                }
            }
        }
    }

    fun listaFiltrada(): List<ColaboradorFabricaUi> {
        val state = _uiState.value
        val termo = state.pesquisa.trim().lowercase()

        return state.colaboradores
            .filter { colaborador ->
                when (state.filtro) {
                    FiltroEquipaUi.TODOS -> true
                    FiltroEquipaUi.DISPONIVEIS -> colaborador.estado == EstadoColaboradorUi.DISPONIVEL
                    FiltroEquipaUi.AFETOS -> colaborador.estado == EstadoColaboradorUi.AFETO
                    FiltroEquipaUi.SOBRECARGA -> colaborador.estado == EstadoColaboradorUi.SOBRECARGA
                    FiltroEquipaUi.INDISPONIVEIS -> colaborador.estado == EstadoColaboradorUi.INDISPONIVEL
                }
            }
            .filter { colaborador ->
                termo.isBlank() ||
                        colaborador.nome.lowercase().contains(termo) ||
                        colaborador.funcao.lowercase().contains(termo) ||
                        (colaborador.email?.lowercase()?.contains(termo) == true)
            }
    }

    private suspend fun buildEquipa(): List<ColaboradorFabricaUi> {
        val utilizadores = fabricaRepository.getUtilizadores()
        if (utilizadores.isEmpty()) return emptyList()

        return utilizadores.map { utilizador ->
            viewModelScope.async {
                val id = utilizador.idUtilizador ?: 0
                val detalhe = runCatching { fabricaRepository.getUtilizadorDetalhe(id) }.getOrNull()
                val motas = runCatching { fabricaRepository.getMotasDoUtilizador(id, ativasOnly = true) }.getOrNull()

                val nome = DtoHelpers.firstNotBlank(utilizador.username, detalhe?.utilizador?.username) ?: "Sem nome"
                val funcao = DtoHelpers.firstNotBlank(utilizador.tipo, detalhe?.utilizador?.tipo) ?: "Operação"
                val ativo = DtoHelpers.isUserAtivo(utilizador.ativo, utilizador.estado)

                val unidades = motas?.motas.orEmpty().mapNotNull { mota ->
                    val motaId = mota.motaId ?: return@mapNotNull null
                    UnidadeAssociadaUi(
                        idAssociacao = mota.idUtilizadorMota ?: 0,
                        motaId = motaId,
                        vin = DtoHelpers.text(mota.numeroIdentificacao, "VIN pendente"),
                        modeloNome = DtoHelpers.text(mota.modeloNome, "Modelo por identificar"),
                        ordemId = mota.idOrdemProducao
                    )
                }

                val totalAssociacoesAtivas = detalhe?.totalAssociacoesAtivas ?: motas?.total ?: unidades.size

                val estado = when {
                    !ativo -> EstadoColaboradorUi.INDISPONIVEL
                    totalAssociacoesAtivas >= 2 -> EstadoColaboradorUi.SOBRECARGA
                    totalAssociacoesAtivas == 1 -> EstadoColaboradorUi.AFETO
                    else -> EstadoColaboradorUi.DISPONIVEL
                }

                val disponibilidadeLabel = when (estado) {
                    EstadoColaboradorUi.DISPONIVEL -> "Disponível"
                    EstadoColaboradorUi.AFETO -> "Afeto ao turno"
                    EstadoColaboradorUi.SOBRECARGA -> "Sobrecarga"
                    EstadoColaboradorUi.INDISPONIVEL -> "Indisponível"
                }

                val nota = when {
                    !ativo -> "Pessoa indisponível na base operacional. Pode exigir cobertura ou reafetação."
                    totalAssociacoesAtivas == 0 -> "Sem unidades ativas associadas neste momento."
                    totalAssociacoesAtivas == 1 -> "A acompanhar 1 unidade ativa."
                    else -> "A acompanhar $totalAssociacoesAtivas unidades ativas."
                }

                ColaboradorFabricaUi(
                    id = id,
                    nome = nome,
                    funcao = funcao,
                    email = DtoHelpers.firstNotBlank(utilizador.email, detalhe?.utilizador?.email),
                    telefone = DtoHelpers.firstNotBlank(utilizador.telefone, detalhe?.utilizador?.telefone),
                    estado = estado,
                    disponibilidadeLabel = disponibilidadeLabel,
                    ativoNaBaseDados = ativo,
                    totalAssociacoesAtivas = totalAssociacoesAtivas,
                    unidadesAtuais = unidades,
                    notaOperacional = nota,
                    podeAlterarEstado = id > 0
                )
            }
        }.map { it.await() }
            .sortedWith(
                compareBy<ColaboradorFabricaUi> {
                    when (it.estado) {
                        EstadoColaboradorUi.SOBRECARGA -> 0
                        EstadoColaboradorUi.INDISPONIVEL -> 1
                        EstadoColaboradorUi.AFETO -> 2
                        EstadoColaboradorUi.DISPONIVEL -> 3
                    }
                }.thenByDescending { it.totalAssociacoesAtivas }
                    .thenBy { it.nome }
            )
    }
}

private fun List<ColaboradorFabricaUi>.toResumo(): EquipaResumoUi {
    return EquipaResumoUi(
        total = size,
        disponiveis = count { it.estado == EstadoColaboradorUi.DISPONIVEL },
        afetos = count { it.estado == EstadoColaboradorUi.AFETO },
        sobrecarga = count { it.estado == EstadoColaboradorUi.SOBRECARGA },
        indisponiveis = count { it.estado == EstadoColaboradorUi.INDISPONIVEL }
    )
}
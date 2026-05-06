package com.example.aplicacaodecontrolofabrica.features.operacao

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.model.AcaoPrincipalUi
import com.example.aplicacaodecontrolofabrica.data.model.EstadoMaterialCriticoUi
import com.example.aplicacaodecontrolofabrica.data.model.EtapaOrdemUi
import com.example.aplicacaodecontrolofabrica.data.model.FiltroOperacaoUi
import com.example.aplicacaodecontrolofabrica.data.model.FonteMaterialCriticoUi
import com.example.aplicacaodecontrolofabrica.data.model.MaterialCriticoUi
import com.example.aplicacaodecontrolofabrica.data.model.OrdemOperacionalUi
import com.example.aplicacaodecontrolofabrica.data.model.PrioridadeUi
import com.example.aplicacaodecontrolofabrica.data.model.ProntidaoOrdemUi
import com.example.aplicacaodecontrolofabrica.data.model.StatusExecucaoUi
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OperacaoResumoUi(
    val total: Int = 0,
    val prioritarias: Int = 0,
    val bloqueadas: Int = 0,
    val emRisco: Int = 0,
    val semUnidade: Int = 0,
    val controloPendente: Int = 0
)

data class OperacaoUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pesquisa: String = "",
    val filtro: FiltroOperacaoUi = FiltroOperacaoUi.TODAS,
    val ordens: List<OrdemOperacionalUi> = emptyList(),
    val resumo: OperacaoResumoUi = OperacaoResumoUi()
)

class OrdensRealViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperacaoUiState(isLoading = true))
    val uiState: StateFlow<OperacaoUiState> = _uiState.asStateFlow()

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

            runCatching { buildOperacao() }
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            ordens = lista,
                            resumo = lista.toResumo()
                        )
                    }
                }
                .onFailure { ex ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ex.message ?: "Não foi possível carregar a operação."
                        )
                    }
                }
        }
    }

    fun atualizarPesquisa(valor: String) {
        _uiState.update { it.copy(pesquisa = valor) }
    }

    fun atualizarFiltro(filtro: FiltroOperacaoUi) {
        _uiState.update { it.copy(filtro = filtro) }
    }

    fun listaFiltrada(): List<OrdemOperacionalUi> {
        val state = _uiState.value
        val base = filtrar(state.ordens, state.filtro)
        val termo = state.pesquisa.trim().lowercase()

        return base.filter { ordem ->
            termo.isBlank() ||
                    ordem.numeroOrdem.lowercase().contains(termo) ||
                    (ordem.paisDestino?.lowercase()?.contains(termo) == true) ||
                    acaoPrincipalLabel(ordem.acaoPrincipal).lowercase().contains(termo) ||
                    etapaLabel(ordem.etapaAtual).lowercase().contains(termo) ||
                    prioridadeLabel(ordem.prioridade).lowercase().contains(termo)
        }
    }

    private suspend fun buildOperacao(): List<OrdemOperacionalUi> {
        val ordens = fabricaRepository.getOrdens()
        if (ordens.isEmpty()) return emptyList()

        return ordens.mapNotNull { ordem ->
            val ordemId = ordem.idOrdemProducao ?: return@mapNotNull null

            val resumoDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getOrdemResumo(ordemId) }
                    .onFailure { Log.e("OrdensVM", "Falha ao obter resumo da ordem $ordemId", it) }
                    .getOrNull()
            }
            val motasDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }
                    .onFailure { Log.e("OrdensVM", "Falha ao obter motas da ordem $ordemId", it) }
                    .getOrDefault(emptyList())
            }

            val resumo = resumoDeferred.await()
            val motas = motasDeferred.await()

            val estadoBase = ordem.estado ?: 0
            val bloqueada = estadoBase == 3
            val concluida = estadoBase == 2
            val emProducao = estadoBase == 1

            val montagemOk = resumo?.checklists?.montagemOk ?: false
            val embalagemOk = resumo?.checklists?.embalagemOk ?: false
            val controloOk = resumo?.checklists?.controloOk ?: false
            val checklistsOk = montagemOk && embalagemOk && controloOk

            val temUnidadeRegistada = motas.isNotEmpty()
            val vinPendente = motas.any { it.numeroIdentificacao.isNullOrBlank() }
            val faltasCriticas = 0

            val etapaAtual = when {
                bloqueada -> EtapaOrdemUi.BLOQUEADA
                concluida -> EtapaOrdemUi.CONCLUIDA
                montagemOk && embalagemOk && !controloOk -> EtapaOrdemUi.CONTROLO
                montagemOk && !embalagemOk -> EtapaOrdemUi.EMBALAGEM
                emProducao -> EtapaOrdemUi.MONTAGEM
                else -> EtapaOrdemUi.POR_ARRANCAR
            }

            val prontidao = when {
                concluida -> ProntidaoOrdemUi.CONCLUIDA
                bloqueada -> ProntidaoOrdemUi.BLOQUEADA
                emProducao -> ProntidaoOrdemUi.EM_EXECUCAO
                !temUnidadeRegistada -> ProntidaoOrdemUi.A_CONFIRMAR
                else -> ProntidaoOrdemUi.PRONTA
            }

            val prioridade = when {
                bloqueada -> PrioridadeUi.CRITICA
                montagemOk && embalagemOk && !controloOk -> PrioridadeUi.CRITICA
                !temUnidadeRegistada && emProducao -> PrioridadeUi.ALTA
                vinPendente -> PrioridadeUi.ALTA
                else -> PrioridadeUi.NORMAL
            }

            val statusExecucao = when {
                concluida -> StatusExecucaoUi.CONCLUIDO
                bloqueada -> StatusExecucaoUi.BLOQUEADO
                prioridade == PrioridadeUi.CRITICA -> StatusExecucaoUi.CRITICO
                prioridade == PrioridadeUi.ALTA -> StatusExecucaoUi.ATENCAO
                else -> StatusExecucaoUi.NORMAL
            }

            val acaoPrincipal = when {
                bloqueada -> AcaoPrincipalUi.ANALISAR_BLOQUEIO
                !temUnidadeRegistada -> AcaoPrincipalUi.REGISTAR_UNIDADE
                estadoBase == 0 -> AcaoPrincipalUi.INICIAR
                montagemOk && embalagemOk && !controloOk -> AcaoPrincipalUi.VALIDAR_CONTROLO
                checklistsOk && temUnidadeRegistada && !concluida -> AcaoPrincipalUi.CONCLUIR
                emProducao && !checklistsOk -> AcaoPrincipalUi.FECHAR_CHECKLISTS
                else -> AcaoPrincipalUi.CONSULTAR
            }

            val motivoProntidao = when {
                concluida -> "Ordem concluída."
                bloqueada -> "Ordem bloqueada e a aguardar decisão."
                !temUnidadeRegistada -> "Falta registar a unidade/mota."
                vinPendente -> "Existe pelo menos uma unidade sem VIN/quadro."
                montagemOk && embalagemOk && !controloOk -> "Falta controlo final."
                else -> "Sem bloqueios críticos visíveis."
            }

            val materialCritico = MaterialCriticoUi(
                estado = EstadoMaterialCriticoUi.POR_INTEGRAR,
                fonte = FonteMaterialCriticoUi.SEM_INTEGRACAO,
                totalCriticosEsperados = 0,
                confirmados = 0,
                emFalta = 0,
                nota = "Integração futura com PHC/compras."
            )

            OrdemOperacionalUi(
                id = ordemId,
                numeroOrdem = ordem.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº",
                estadoBase = estadoBase,
                modeloId = ordem.idModelo,
                clienteId = ordem.idCliente,
                encomendaId = ordem.idEncomenda,
                paisDestino = ordem.paisDestino,
                etapaAtual = etapaAtual,
                statusExecucao = statusExecucao,
                prioridade = prioridade,
                urgente = prioridade == PrioridadeUi.CRITICA,
                bloqueada = bloqueada,
                motivoBloqueio = if (bloqueada) "Bloqueio operacional em aberto." else null,
                prontidao = prontidao,
                motivoProntidao = motivoProntidao,
                prontaParaArrancar = !bloqueada && !concluida,
                materialCritico = materialCritico,
                faltasCriticas = faltasCriticas,
                temUnidadeRegistada = temUnidadeRegistada,
                vinPendente = vinPendente,
                checklistsOk = checklistsOk,
                montagemOk = montagemOk,
                embalagemOk = embalagemOk,
                controloOk = controloOk,
                totalMotas = motas.size,
                totalServicos = resumo?.servicos ?: 0,
                dataCriacaoIso = ordem.dataCriacao,
                dataConclusaoIso = ordem.dataConclusao,
                acaoPrincipal = acaoPrincipal
            )
        }.sortedWith(
            compareByDescending<OrdemOperacionalUi> { it.bloqueada }
                .thenByDescending { it.prioridade == PrioridadeUi.CRITICA }
                .thenByDescending { it.prioridade == PrioridadeUi.ALTA }
                .thenBy { it.numeroOrdem }
        )
    }

    fun filtrar(lista: List<OrdemOperacionalUi>, filtro: FiltroOperacaoUi): List<OrdemOperacionalUi> {
        return when (filtro) {
            FiltroOperacaoUi.TODAS -> lista
            FiltroOperacaoUi.BLOQUEADAS -> lista.filter { it.bloqueada }
            FiltroOperacaoUi.POR_ARRANCAR -> lista.filter { it.etapaAtual == EtapaOrdemUi.POR_ARRANCAR }
            FiltroOperacaoUi.EM_EXECUCAO -> lista.filter {
                it.etapaAtual == EtapaOrdemUi.MONTAGEM ||
                        it.etapaAtual == EtapaOrdemUi.EMBALAGEM ||
                        it.etapaAtual == EtapaOrdemUi.CONTROLO
            }
            FiltroOperacaoUi.CONTROLO_PENDENTE -> lista.filter {
                it.montagemOk && it.embalagemOk && !it.controloOk
            }
            FiltroOperacaoUi.PRONTAS_A_CONCLUIR -> lista.filter {
                it.checklistsOk && it.temUnidadeRegistada && !it.bloqueada
            }
            FiltroOperacaoUi.CONCLUIDAS -> lista.filter { it.etapaAtual == EtapaOrdemUi.CONCLUIDA }
            FiltroOperacaoUi.PRIORITARIAS -> lista.filter { it.prioridade != PrioridadeUi.NORMAL }
            FiltroOperacaoUi.EM_RISCO -> lista.filter {
                it.bloqueada || it.vinPendente || !it.temUnidadeRegistada || !it.controloOk
            }
        }
    }

    private fun etapaLabel(value: EtapaOrdemUi): String = when (value) {
        EtapaOrdemUi.POR_ARRANCAR -> "Por arrancar"
        EtapaOrdemUi.MONTAGEM -> "Montagem"
        EtapaOrdemUi.EMBALAGEM -> "Embalagem"
        EtapaOrdemUi.CONTROLO -> "Controlo"
        EtapaOrdemUi.PRONTA_EXPEDICAO -> "Pronta"
        EtapaOrdemUi.CONCLUIDA -> "Concluída"
        EtapaOrdemUi.BLOQUEADA -> "Bloqueada"
    }

    private fun prioridadeLabel(value: PrioridadeUi): String = when (value) {
        PrioridadeUi.NORMAL -> "Normal"
        PrioridadeUi.ALTA -> "Alta"
        PrioridadeUi.CRITICA -> "Crítica"
    }

    private fun acaoPrincipalLabel(value: AcaoPrincipalUi): String = when (value) {
        AcaoPrincipalUi.INICIAR -> "Iniciar"
        AcaoPrincipalUi.REGISTAR_UNIDADE -> "Registar unidade"
        AcaoPrincipalUi.FECHAR_CHECKLISTS -> "Fechar checklists"
        AcaoPrincipalUi.VALIDAR_CONTROLO -> "Validar controlo"
        AcaoPrincipalUi.CONCLUIR -> "Concluir"
        AcaoPrincipalUi.ANALISAR_BLOQUEIO -> "Analisar bloqueio"
        AcaoPrincipalUi.CONSULTAR -> "Consultar"
    }
}

private fun List<OrdemOperacionalUi>.toResumo(): OperacaoResumoUi {
    return OperacaoResumoUi(
        total = size,
        prioritarias = count { it.prioridade != PrioridadeUi.NORMAL },
        bloqueadas = count { it.bloqueada },
        emRisco = count { it.bloqueada || it.vinPendente || !it.temUnidadeRegistada || !it.controloOk },
        semUnidade = count { !it.temUnidadeRegistada },
        controloPendente = count { it.montagemOk && it.embalagemOk && !it.controloOk }
    )
}
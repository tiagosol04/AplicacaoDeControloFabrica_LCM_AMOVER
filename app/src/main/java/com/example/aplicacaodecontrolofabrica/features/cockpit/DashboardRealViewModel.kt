package com.example.aplicacaodecontrolofabrica.features.cockpit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.dto.UtilizadorDto
import com.example.aplicacaodecontrolofabrica.data.mapper.DtoHelpers
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DashboardZonaEstadoUi {
    NORMAL,
    ATENCAO,
    CRITICA
}

data class DashboardZonaUi(
    val nome: String,
    val totalOrdens: Int,
    val totalUnidades: Int,
    val responsavelLabel: String,
    val riscoLabel: String,
    val resumo: String,
    val estado: DashboardZonaEstadoUi
)

data class DashboardOrderUi(
    val ordemId: Int,
    val numeroOrdem: String,
    val modeloNome: String,
    val estadoLabel: String,
    val prioridadeLabel: String,
    val bloqueada: Boolean,
    val unidadeRegistada: Boolean,
    val vinPendente: Boolean,
    val controloPendente: Boolean
)

data class DashboardActionUi(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val ordemId: Int? = null
)

data class DashboardPessoaUi(
    val id: Int,
    val nome: String,
    val funcao: String,
    val disponibilidade: String,
    val ativo: Boolean
)

data class DashboardUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val totalOrdens: Int = 0,
    val emProducao: Int = 0,
    val bloqueadas: Int = 0,
    val semUnidade: Int = 0,
    val controloPendente: Int = 0,
    val vinPendente: Int = 0,
    val equipaAtiva: Int = 0,
    val equipaIndisponivel: Int = 0,

    val zonasHoje: List<DashboardZonaUi> = emptyList(),
    val acoesImediatas: List<DashboardActionUi> = emptyList(),
    val ordensPrioritarias: List<DashboardOrderUi> = emptyList(),
    val equipaHoje: List<DashboardPessoaUi> = emptyList(),

    // Serviços
    val servicosEmAberto: Int = 0,
    val servicosGarantia: Int = 0,
    val servicosAvaria: Int = 0
)

class DashboardRealViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

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

            runCatching { buildDashboard() }
                .onSuccess { result ->
                    _uiState.value = result.copy(isLoading = false)
                }
                .onFailure { ex ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ex.message ?: "Não foi possível carregar a visão geral da fábrica."
                        )
                    }
                }
        }
    }

    private suspend fun buildDashboard(): DashboardUiState {
        val ordens = fabricaRepository.getOrdens()

        val utilizadores: List<UtilizadorDto> = runCatching {
            fabricaRepository.getUtilizadores()
        }.getOrDefault(emptyList())

        val modelosMap = runCatching {
            fabricaRepository.getModelos().associateBy { it.idModelo ?: 0 }
        }.getOrDefault(emptyMap())

        val cards = mutableListOf<DashboardOrderUi>()
        val acoes = mutableListOf<DashboardActionUi>()

        var emProducao = 0
        var bloqueadas = 0
        var semUnidade = 0
        var controloPendente = 0
        var vinPendente = 0

        var zonaMontagemOrdens = 0
        var zonaMontagemUnidades = 0

        var zonaEmbalagemOrdens = 0
        var zonaEmbalagemUnidades = 0

        var zonaControloOrdens = 0
        var zonaControloUnidades = 0

        var zonaBloqueiosOrdens = 0
        var zonaBloqueiosUnidades = 0

        for (ordem in ordens) {
            val ordemId = ordem.idOrdemProducao ?: continue
            val estado = ordem.estado ?: 0
            val numeroOrdem = ordem.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº"
            val modeloNome = modelosMap[ordem.idModelo ?: -1]
                ?.nomeModelo
                ?.takeIf { !it.isNullOrBlank() }
                ?: "Modelo por identificar"

            val resumoDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getOrdemResumo(ordemId) }.getOrNull()
            }
            val motasDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }.getOrDefault(emptyList())
            }

            val resumo = resumoDeferred.await()
            val motas = motasDeferred.await()

            val unidadeRegistada = motas.isNotEmpty()
            val temVinPendente = motas.any { it.numeroIdentificacao.isNullOrBlank() }

            val montagemOk = resumo?.checklists?.montagemOk ?: false
            val embalagemOk = resumo?.checklists?.embalagemOk ?: false
            val controloOk = resumo?.checklists?.controloOk ?: false

            val temControloPendente = montagemOk && embalagemOk && !controloOk && estado != 2
            val estaBloqueada = estado == 3

            if (estado == 1) emProducao++
            if (estaBloqueada) bloqueadas++
            if (!unidadeRegistada && estado != 2) semUnidade++
            if (temControloPendente) controloPendente++
            if (temVinPendente && estado != 2) vinPendente++

            when {
                estaBloqueada -> {
                    zonaBloqueiosOrdens++
                    zonaBloqueiosUnidades += motas.size
                }

                temControloPendente -> {
                    zonaControloOrdens++
                    zonaControloUnidades += motas.size
                }

                montagemOk && !embalagemOk -> {
                    zonaEmbalagemOrdens++
                    zonaEmbalagemUnidades += motas.size
                }

                estado == 1 || !montagemOk -> {
                    zonaMontagemOrdens++
                    zonaMontagemUnidades += motas.size
                }
            }

            val prioridadeLabel = when {
                estaBloqueada -> "Crítica"
                temControloPendente -> "Alta"
                !unidadeRegistada -> "Alta"
                temVinPendente -> "Alta"
                else -> "Normal"
            }

            val estadoLabel = when (estado) {
                0 -> "Por arrancar"
                1 -> "Em produção"
                2 -> "Concluída"
                3 -> "Bloqueada"
                else -> "Operacional"
            }

            when {
                estaBloqueada -> {
                    acoes += DashboardActionUi(
                        id = acoes.size + 1,
                        titulo = "Desbloquear $numeroOrdem",
                        descricao = "A ordem está parada e precisa de decisão de supervisão.",
                        ordemId = ordemId
                    )
                }

                !unidadeRegistada && estado != 2 -> {
                    acoes += DashboardActionUi(
                        id = acoes.size + 1,
                        titulo = "Registar unidade em $numeroOrdem",
                        descricao = "A ordem avançou sem unidade associada. Isto é crítico para a rastreabilidade.",
                        ordemId = ordemId
                    )
                }

                temControloPendente -> {
                    acoes += DashboardActionUi(
                        id = acoes.size + 1,
                        titulo = "Fechar controlo final $numeroOrdem",
                        descricao = "Montagem e embalagem prontas, falta apenas a validação final.",
                        ordemId = ordemId
                    )
                }

                temVinPendente -> {
                    acoes += DashboardActionUi(
                        id = acoes.size + 1,
                        titulo = "Fechar quadro/VIN de $numeroOrdem",
                        descricao = "Existe pelo menos uma unidade com identificação por concluir.",
                        ordemId = ordemId
                    )
                }
            }

            cards += DashboardOrderUi(
                ordemId = ordemId,
                numeroOrdem = numeroOrdem,
                modeloNome = modeloNome,
                estadoLabel = estadoLabel,
                prioridadeLabel = prioridadeLabel,
                bloqueada = estaBloqueada,
                unidadeRegistada = unidadeRegistada,
                vinPendente = temVinPendente,
                controloPendente = temControloPendente
            )
        }

        val equipaAtiva = utilizadores.count { utilizador ->
            DtoHelpers.isUserAtivo(utilizador.ativo, utilizador.estado)
        }

        val equipaIndisponivel = utilizadores.size - equipaAtiva

        val equipaHoje = utilizadores
            .sortedWith(
                compareByDescending<UtilizadorDto> { utilizador ->
                    DtoHelpers.isUserAtivo(utilizador.ativo, utilizador.estado)
                }.thenBy { utilizador ->
                    DtoHelpers.text(utilizador.username, "Utilizador")
                }
            )
            .take(5)
            .map { utilizador ->
                val ativo = DtoHelpers.isUserAtivo(utilizador.ativo, utilizador.estado)

                val totalAssociacoes = runCatching {
                    fabricaRepository.getMotasDoUtilizador(
                        utilizador.idUtilizador ?: 0,
                        true
                    ).total ?: 0
                }.getOrDefault(0)

                val disponibilidade = when {
                    !ativo -> "Indisponível"
                    totalAssociacoes >= 2 -> "Sobrecarga"
                    totalAssociacoes == 1 -> "Afeto à operação"
                    else -> "Disponível"
                }

                DashboardPessoaUi(
                    id = utilizador.idUtilizador ?: 0,
                    nome = DtoHelpers.text(utilizador.username, "Utilizador"),
                    funcao = DtoHelpers.text(utilizador.tipo, "Operação"),
                    disponibilidade = disponibilidade,
                    ativo = ativo
                )
            }

        if (equipaIndisponivel > 0) {
            acoes += DashboardActionUi(
                id = acoes.size + 1,
                titulo = "Rever cobertura da equipa",
                descricao = "Existem $equipaIndisponivel colaborador(es) indisponível(eis) hoje."
            )
        }

        val zonas = listOf(
            DashboardZonaUi(
                nome = "Zona de Montagem",
                totalOrdens = zonaMontagemOrdens,
                totalUnidades = zonaMontagemUnidades,
                responsavelLabel = if (equipaAtiva > 0) "Supervisão ativa" else "Sem cobertura",
                riscoLabel = when {
                    zonaMontagemOrdens >= 4 -> "Fluxo intenso"
                    zonaMontagemOrdens > 0 -> "Estável"
                    else -> "Sem carga"
                },
                resumo = "Arranque e avanço físico das ordens em curso.",
                estado = when {
                    zonaMontagemOrdens >= 5 -> DashboardZonaEstadoUi.ATENCAO
                    else -> DashboardZonaEstadoUi.NORMAL
                }
            ),
            DashboardZonaUi(
                nome = "Zona de Embalagem",
                totalOrdens = zonaEmbalagemOrdens,
                totalUnidades = zonaEmbalagemUnidades,
                responsavelLabel = "Acompanhamento intermédio",
                riscoLabel = when {
                    zonaEmbalagemOrdens >= 3 -> "Acumulação"
                    zonaEmbalagemOrdens > 0 -> "Com carga"
                    else -> "Sem carga"
                },
                resumo = "Ordens que passaram montagem e aguardam fecho intermédio.",
                estado = when {
                    zonaEmbalagemOrdens >= 3 -> DashboardZonaEstadoUi.ATENCAO
                    else -> DashboardZonaEstadoUi.NORMAL
                }
            ),
            DashboardZonaUi(
                nome = "Zona de Controlo Final",
                totalOrdens = zonaControloOrdens,
                totalUnidades = zonaControloUnidades,
                responsavelLabel = "Qualidade / validação",
                riscoLabel = when {
                    zonaControloOrdens >= 2 -> "Fecho pendente"
                    zonaControloOrdens > 0 -> "A acompanhar"
                    else -> "Livre"
                },
                resumo = "Último fecho antes de considerar a unidade pronta.",
                estado = when {
                    zonaControloOrdens >= 2 -> DashboardZonaEstadoUi.ATENCAO
                    else -> DashboardZonaEstadoUi.NORMAL
                }
            ),
            DashboardZonaUi(
                nome = "Zona de Exceções",
                totalOrdens = zonaBloqueiosOrdens,
                totalUnidades = zonaBloqueiosUnidades,
                responsavelLabel = "Supervisão / decisão",
                riscoLabel = when {
                    zonaBloqueiosOrdens >= 2 -> "Crítico"
                    zonaBloqueiosOrdens == 1 -> "Aberto"
                    else -> "Controlado"
                },
                resumo = "Bloqueios, paragens e ordens fora do fluxo normal.",
                estado = when {
                    zonaBloqueiosOrdens >= 2 -> DashboardZonaEstadoUi.CRITICA
                    zonaBloqueiosOrdens == 1 -> DashboardZonaEstadoUi.ATENCAO
                    else -> DashboardZonaEstadoUi.NORMAL
                }
            )
        )

        // Serviços em aberto
        val servicosData = runCatching { fabricaRepository.getServicosEmAberto() }.getOrNull()
        val servicosEmAberto = servicosData?.total ?: 0
        val servicosGarantia = servicosData?.servicos?.count { (it.tipo ?: 0) == 3 } ?: 0
        val servicosAvaria = servicosData?.servicos?.count { (it.tipo ?: 0) == 2 } ?: 0

        if (servicosEmAberto > 0) {
            acoes += DashboardActionUi(
                id = acoes.size + 1,
                titulo = "Serviços em aberto: $servicosEmAberto",
                descricao = "$servicosGarantia garantia(s), $servicosAvaria avaria(s) por tratar."
            )
        }

        return DashboardUiState(
            totalOrdens = ordens.size,
            emProducao = emProducao,
            bloqueadas = bloqueadas,
            semUnidade = semUnidade,
            controloPendente = controloPendente,
            vinPendente = vinPendente,
            equipaAtiva = equipaAtiva,
            equipaIndisponivel = equipaIndisponivel,
            zonasHoje = zonas,
            acoesImediatas = acoes.take(6),
            ordensPrioritarias = cards
                .sortedWith(
                    compareByDescending<DashboardOrderUi> { it.bloqueada }
                        .thenByDescending { it.prioridadeLabel == "Crítica" }
                        .thenByDescending { it.prioridadeLabel == "Alta" }
                        .thenByDescending { it.vinPendente }
                )
                .take(5),
            equipaHoje = equipaHoje,
            servicosEmAberto = servicosEmAberto,
            servicosGarantia = servicosGarantia,
            servicosAvaria = servicosAvaria
        )
    }
}
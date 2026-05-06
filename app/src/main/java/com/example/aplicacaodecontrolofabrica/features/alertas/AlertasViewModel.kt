package com.example.aplicacaodecontrolofabrica.features.alertas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacaodecontrolofabrica.data.model.Alerta
import com.example.aplicacaodecontrolofabrica.data.model.EstadoAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.OrigemAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.SeveridadeAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.TipoAlertaUi
import com.example.aplicacaodecontrolofabrica.data.repository.FabricaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlertasUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val alertas: List<Alerta> = emptyList(),
    val alertaSelecionado: Alerta? = null,
    // Indica que os alertas são calculados localmente a partir das ordens —
    // não existem no backend como entidades persistentes.
    // Quando existir endpoint GET /api/alertas, remover este flag e consumir a API.
    val alertasCalculados: Boolean = true
)

class AlertasViewModel(
    private val fabricaRepository: FabricaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertasUiState(isLoading = true))
    val uiState: StateFlow<AlertasUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh(alertaIdParaSelecionar: Int? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                buildOperationalAlerts()
            }.onSuccess { alertas ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        alertas = alertas,
                        alertaSelecionado = alertaIdParaSelecionar?.let { id ->
                            alertas.firstOrNull { a -> a.id == id }
                        }
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Não foi possível carregar os alertas."
                    )
                }
            }
        }
    }

    fun selecionarAlerta(alertaId: Int) {
        val atual = _uiState.value.alertas.firstOrNull { it.id == alertaId }
        if (atual != null) {
            _uiState.update { it.copy(alertaSelecionado = atual) }
        } else {
            refresh(alertaId)
        }
    }

    private suspend fun buildOperationalAlerts(): List<Alerta> {
        val ordens = fabricaRepository.getOrdens()

        if (ordens.isEmpty()) return emptyList()

        val modelosMap = runCatching {
            fabricaRepository.getModelos().associateBy { it.idModelo ?: 0 }
        }.onFailure { Log.e("AlertasVM", "Falha ao carregar modelos para alertas", it) }
            .getOrDefault(emptyMap())

        val alertas = mutableListOf<Alerta>()

        for (ordem in ordens) {
            val ordemId = ordem.idOrdemProducao ?: continue
            val numeroOrdem = ordem.numeroOrdem?.ifBlank { "Sem nº" } ?: "Sem nº"
            val estado = ordem.estado ?: 0
            val modeloNome = modelosMap[ordem.idModelo ?: -1]?.nomeModelo?.takeIf { !it.isNullOrBlank() }

            val resumoDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getOrdemResumo(ordemId) }
                    .onFailure { Log.e("AlertasVM", "Falha resumo ordem $ordemId", it) }
                    .getOrNull()
            }
            val motasDeferred = viewModelScope.async {
                runCatching { fabricaRepository.getMotasDaOrdem(ordemId) }
                    .onFailure { Log.e("AlertasVM", "Falha motas ordem $ordemId", it) }
                    .getOrDefault(emptyList())
            }

            val resumo = resumoDeferred.await()
            val motas = motasDeferred.await()

            val contextoModelo = modeloNome?.let { " • $it" }.orEmpty()
            val dataRef = ordem.dataCriacao ?: ordem.dataConclusao

            // 1) Ordem bloqueada
            if (estado == 3) {
                alertas += Alerta(
                    id = stableAlertId(ordemId, 1),
                    titulo = "Ordem bloqueada",
                    descricao = "A ordem $numeroOrdem$contextoModelo está bloqueada e requer decisão operacional.",
                    tipo = TipoAlertaUi.BLOQUEIO,
                    severidade = SeveridadeAlertaUi.CRITICA,
                    estado = EstadoAlertaUi.ABERTO,
                    origem = OrigemAlertaUi.FABRICA,
                    ordemId = ordemId,
                    modeloId = ordem.idModelo,
                    clienteId = ordem.idCliente,
                    dataCriacaoIso = dataRef
                )
            }

            // 2) Ordem sem unidade registada
            if (estado != 2 && motas.isEmpty()) {
                alertas += Alerta(
                    id = stableAlertId(ordemId, 2),
                    titulo = "Unidade não registada",
                    descricao = "A ordem $numeroOrdem$contextoModelo ainda não tem mota/unidade associada.",
                    tipo = TipoAlertaUi.OPERACIONAL,
                    severidade = if (estado == 1) SeveridadeAlertaUi.ALTA else SeveridadeAlertaUi.MEDIA,
                    estado = EstadoAlertaUi.ABERTO,
                    origem = OrigemAlertaUi.FABRICA,
                    ordemId = ordemId,
                    modeloId = ordem.idModelo,
                    clienteId = ordem.idCliente,
                    dataCriacaoIso = dataRef
                )
            }

            // 3) VIN pendente
            val motasSemVin = motas.filter { it.numeroIdentificacao.isNullOrBlank() }
            if (estado != 2 && motasSemVin.isNotEmpty()) {
                alertas += Alerta(
                    id = stableAlertId(ordemId, 3),
                    titulo = "VIN / quadro pendente",
                    descricao = "A ordem $numeroOrdem$contextoModelo tem ${motasSemVin.size} unidade(s) sem VIN/quadro registado.",
                    tipo = TipoAlertaUi.OPERACIONAL,
                    severidade = SeveridadeAlertaUi.MEDIA,
                    estado = EstadoAlertaUi.ABERTO,
                    origem = OrigemAlertaUi.FABRICA,
                    ordemId = ordemId,
                    modeloId = ordem.idModelo,
                    clienteId = ordem.idCliente,
                    dataCriacaoIso = dataRef
                )
            }

            if (resumo != null) {
                val montagemOk = resumo.checklists.montagemOk
                val embalagemOk = resumo.checklists.embalagemOk
                val controloOk = resumo.checklists.controloOk

                // 4) Controlo final pendente
                if (montagemOk && embalagemOk && !controloOk && estado != 2) {
                    alertas += Alerta(
                        id = stableAlertId(ordemId, 4),
                        titulo = "Controlo final pendente",
                        descricao = "A ordem $numeroOrdem$contextoModelo já passou montagem e embalagem, mas falta controlo final.",
                        tipo = TipoAlertaUi.QUALIDADE,
                        severidade = SeveridadeAlertaUi.ALTA,
                        estado = EstadoAlertaUi.ABERTO,
                        origem = OrigemAlertaUi.QUALIDADE,
                        ordemId = ordemId,
                        modeloId = ordem.idModelo,
                        clienteId = ordem.idCliente,
                        dataCriacaoIso = dataRef
                    )
                }

                // 5) Montagem por validar em ordem em produção
                if (!montagemOk && estado == 1) {
                    alertas += Alerta(
                        id = stableAlertId(ordemId, 5),
                        titulo = "Montagem por validar",
                        descricao = "A ordem $numeroOrdem$contextoModelo está em produção com checklist de montagem ainda por fechar.",
                        tipo = TipoAlertaUi.OPERACIONAL,
                        severidade = SeveridadeAlertaUi.MEDIA,
                        estado = EstadoAlertaUi.ABERTO,
                        origem = OrigemAlertaUi.FABRICA,
                        ordemId = ordemId,
                        modeloId = ordem.idModelo,
                        clienteId = ordem.idCliente,
                        dataCriacaoIso = dataRef
                    )
                }

                // 6) Embalagem pendente depois da montagem
                if (montagemOk && !embalagemOk && estado == 1) {
                    alertas += Alerta(
                        id = stableAlertId(ordemId, 6),
                        titulo = "Embalagem pendente",
                        descricao = "A ordem $numeroOrdem$contextoModelo já concluiu montagem, mas ainda não fechou embalagem.",
                        tipo = TipoAlertaUi.OPERACIONAL,
                        severidade = SeveridadeAlertaUi.MEDIA,
                        estado = EstadoAlertaUi.ABERTO,
                        origem = OrigemAlertaUi.FABRICA,
                        ordemId = ordemId,
                        modeloId = ordem.idModelo,
                        clienteId = ordem.idCliente,
                        dataCriacaoIso = dataRef
                    )
                }
            }
        }

        return alertas
            .distinctBy { it.id }
            .sortedWith(
                compareByDescending<Alerta> { severityWeight(it.severidade) }
                    .thenByDescending { it.dataCriacaoIso ?: "" }
                    .thenBy { it.ordemId ?: Int.MAX_VALUE }
            )
    }

    private fun stableAlertId(ordemId: Int, slot: Int): Int {
        return ordemId * 100 + slot
    }

    private fun severityWeight(value: SeveridadeAlertaUi): Int = when (value) {
        SeveridadeAlertaUi.CRITICA -> 4
        SeveridadeAlertaUi.ALTA -> 3
        SeveridadeAlertaUi.MEDIA -> 2
        SeveridadeAlertaUi.BAIXA -> 1
    }
}
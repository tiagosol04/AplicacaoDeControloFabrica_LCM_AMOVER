package com.example.aplicacaodecontrolofabrica.features.operacao

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.data.model.AcaoPrincipalUi
import com.example.aplicacaodecontrolofabrica.data.model.FiltroOperacaoUi
import com.example.aplicacaodecontrolofabrica.data.model.OrdemOperacionalUi
import com.example.aplicacaodecontrolofabrica.data.model.PrioridadeUi
import com.example.aplicacaodecontrolofabrica.data.model.StatusExecucaoUi
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.EmptyState
import com.example.aplicacaodecontrolofabrica.ui.components.ErrorState
import com.example.aplicacaodecontrolofabrica.ui.components.LoadingView
import com.example.aplicacaodecontrolofabrica.ui.components.PriorityBadge
import com.example.aplicacaodecontrolofabrica.ui.components.PriorityBadgeType
import com.example.aplicacaodecontrolofabrica.ui.components.SearchBar
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChip
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChipType
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryAlert
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryInfo
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPrimary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OperacaoScreen(
    contentPadding: PaddingValues,
    onOpenOrdem: (Int) -> Unit,
    viewModel: OrdensRealViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val listaFiltrada = viewModel.listaFiltrada()

    when {
        uiState.isLoading -> LoadingView(message = "A carregar prioridades...")
        uiState.errorMessage != null -> ErrorState(
            message = uiState.errorMessage ?: "Erro ao carregar.",
            onRetry = { viewModel.refresh() },
            modifier = Modifier.padding(contentPadding).padding(16.dp)
        )
        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Resumo compacto ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniKpi(Modifier.weight(1f), uiState.resumo.bloqueadas.toString(), "Bloqueadas", FactoryAlert)
                    MiniKpi(Modifier.weight(1f), uiState.resumo.emRisco.toString(), "Em risco", FactoryWarning)
                    MiniKpi(Modifier.weight(1f), uiState.resumo.controloPendente.toString(), "Validação", FactoryInfo)
                    MiniKpi(Modifier.weight(1f), uiState.resumo.total.toString(), "Total", FactoryPrimary)
                }
            }

            // ── Pesquisa + filtros ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SearchBar(
                        value = uiState.pesquisa,
                        onValueChange = viewModel::atualizarPesquisa,
                        label = "Pesquisar ordem, país ou prioridade"
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filtrosPrincipais().forEach { filtro ->
                            FilterChip(
                                selected = uiState.filtro == filtro,
                                onClick = { viewModel.atualizarFiltro(filtro) },
                                label = {
                                    Text(
                                        text = filtroLabel(filtro),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                    Text(
                        text = "${listaFiltrada.size} ordem(ns)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (listaFiltrada.isEmpty()) {
                item {
                    EmptyState(
                        title = "Sem ordens para este filtro",
                        message = "Ajusta a pesquisa ou muda o filtro."
                    )
                }
            } else {
                items(listaFiltrada, key = { it.id }) { ordem ->
                    OrdemCard(ordem = ordem, onOpen = { onOpenOrdem(ordem.id) })
                }
            }
        }
    }
}

@Composable
private fun MiniKpi(
    modifier: Modifier,
    valor: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OrdemCard(
    ordem: OrdemOperacionalUi,
    onOpen: () -> Unit
) {
    val accentColor = when (ordem.prioridade) {
        PrioridadeUi.CRITICA -> FactoryAlert
        PrioridadeUi.ALTA -> FactoryWarning
        PrioridadeUi.NORMAL -> FactoryPrimary
    }

    Card(
        modifier = Modifier.clickable(onClick = onOpen),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de prioridade
            Box(
                modifier = Modifier
                    .size(4.dp, 40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ordem.numeroOrdem,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    PriorityBadge(
                        priority = when (ordem.prioridade) {
                            PrioridadeUi.CRITICA -> PriorityBadgeType.CRITICA
                            PrioridadeUi.ALTA -> PriorityBadgeType.ALTA
                            PrioridadeUi.NORMAL -> PriorityBadgeType.NORMAL
                        }
                    )
                }
                Text(
                    text = "${ordem.paisDestino ?: "—"} · ${ordem.totalMotas} un. · ${acaoLabel(ordem.acaoPrincipal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatusChip(
                        status = when (ordem.statusExecucao) {
                            StatusExecucaoUi.NORMAL -> StatusChipType.NORMAL
                            StatusExecucaoUi.ATENCAO -> StatusChipType.ATENCAO
                            StatusExecucaoUi.CRITICO, StatusExecucaoUi.BLOQUEADO -> StatusChipType.BLOQUEADO
                            StatusExecucaoUi.CONCLUIDO -> StatusChipType.CONCLUIDO
                        },
                        labelOverride = statusLabel(ordem)
                    )
                    if (!ordem.temUnidadeRegistada) {
                        StatusChip(status = StatusChipType.ATENCAO, labelOverride = "Sem unidade")
                    }
                    if (ordem.vinPendente) {
                        StatusChip(status = StatusChipType.ATENCAO, labelOverride = "VIN pend.")
                    }
                }
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun filtrosPrincipais(): List<FiltroOperacaoUi> = listOf(
    FiltroOperacaoUi.PRIORITARIAS, FiltroOperacaoUi.BLOQUEADAS,
    FiltroOperacaoUi.EM_RISCO, FiltroOperacaoUi.CONTROLO_PENDENTE,
    FiltroOperacaoUi.TODAS, FiltroOperacaoUi.CONCLUIDAS
)

private fun filtroLabel(value: FiltroOperacaoUi): String = when (value) {
    FiltroOperacaoUi.TODAS -> "Todas"
    FiltroOperacaoUi.BLOQUEADAS -> "Bloqueadas"
    FiltroOperacaoUi.POR_ARRANCAR -> "Por arrancar"
    FiltroOperacaoUi.EM_EXECUCAO -> "Em execução"
    FiltroOperacaoUi.CONTROLO_PENDENTE -> "Validação"
    FiltroOperacaoUi.PRONTAS_A_CONCLUIR -> "Prontas"
    FiltroOperacaoUi.CONCLUIDAS -> "Concluídas"
    FiltroOperacaoUi.PRIORITARIAS -> "Em foco"
    FiltroOperacaoUi.EM_RISCO -> "Em risco"
}

private fun acaoLabel(value: AcaoPrincipalUi): String = when (value) {
    AcaoPrincipalUi.INICIAR -> "Arrancar"
    AcaoPrincipalUi.REGISTAR_UNIDADE -> "Registar un."
    AcaoPrincipalUi.FECHAR_CHECKLISTS -> "Checklists"
    AcaoPrincipalUi.VALIDAR_CONTROLO -> "Validar"
    AcaoPrincipalUi.CONCLUIR -> "Concluir"
    AcaoPrincipalUi.ANALISAR_BLOQUEIO -> "Bloqueio"
    AcaoPrincipalUi.CONSULTAR -> "Consultar"
}

private fun statusLabel(ordem: OrdemOperacionalUi): String = when {
    ordem.bloqueada -> "Bloqueada"
    ordem.vinPendente -> "Atenção VIN"
    !ordem.temUnidadeRegistada -> "Sem unidade"
    ordem.checklistsOk -> "Pronta"
    else -> "A acompanhar"
}

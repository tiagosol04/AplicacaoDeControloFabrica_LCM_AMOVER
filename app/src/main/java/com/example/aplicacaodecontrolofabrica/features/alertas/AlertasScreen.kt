package com.example.aplicacaodecontrolofabrica.features.alertas

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.data.model.Alerta
import com.example.aplicacaodecontrolofabrica.data.model.EstadoAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.SeveridadeAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.TipoAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.label
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

private enum class OcorrenciasFiltroUi { TODAS, CRITICAS, BLOQUEIOS, QUALIDADE, OPERACIONAIS, PRIORIDADE }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlertasScreen(
    onOpenAlerta: (Int) -> Unit,
    viewModel: AlertasViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    var pesquisa by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf(OcorrenciasFiltroUi.TODAS) }

    val lista = uiState.alertas
        .filter { a -> when (filtro) {
            OcorrenciasFiltroUi.TODAS -> true
            OcorrenciasFiltroUi.CRITICAS -> a.severidade == SeveridadeAlertaUi.CRITICA || a.severidade == SeveridadeAlertaUi.ALTA
            OcorrenciasFiltroUi.BLOQUEIOS -> a.tipo == TipoAlertaUi.BLOQUEIO
            OcorrenciasFiltroUi.QUALIDADE -> a.tipo == TipoAlertaUi.QUALIDADE
            OcorrenciasFiltroUi.OPERACIONAIS -> a.tipo == TipoAlertaUi.OPERACIONAL
            OcorrenciasFiltroUi.PRIORIDADE -> a.tipo == TipoAlertaUi.PRIORIDADE
        }}
        .filter { a -> pesquisa.isBlank() || listOfNotNull(a.titulo, a.descricao, a.vin, a.ordemId?.toString())
            .any { it.contains(pesquisa.trim(), ignoreCase = true) }
        }

    val totalCriticas = uiState.alertas.count { it.severidade == SeveridadeAlertaUi.CRITICA || it.severidade == SeveridadeAlertaUi.ALTA }
    val totalAbertas = uiState.alertas.count { it.estado == EstadoAlertaUi.ABERTO || it.estado == EstadoAlertaUi.EM_ANALISE || it.estado == EstadoAlertaUi.EM_TRATAMENTO }

    when {
        uiState.isLoading -> LoadingView(message = "A carregar ocorrências...")
        uiState.errorMessage != null -> ErrorState(
            message = uiState.errorMessage ?: "Erro.",
            onRetry = { viewModel.refresh() },
            modifier = Modifier.padding(16.dp)
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── KPIs ──
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AlertKpi(Modifier.weight(1f), totalCriticas.toString(), "Críticas", FactoryAlert)
                    AlertKpi(Modifier.weight(1f), totalAbertas.toString(), "Abertas", FactoryWarning)
                    AlertKpi(Modifier.weight(1f), uiState.alertas.size.toString(), "Total", FactoryPrimary)
                }
            }

            // ── Filtros ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SearchBar(value = pesquisa, onValueChange = { pesquisa = it }, label = "Pesquisar ocorrência")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Todas" to OcorrenciasFiltroUi.TODAS, "Críticas" to OcorrenciasFiltroUi.CRITICAS,
                            "Bloqueios" to OcorrenciasFiltroUi.BLOQUEIOS, "Qualidade" to OcorrenciasFiltroUi.QUALIDADE,
                            "Operacionais" to OcorrenciasFiltroUi.OPERACIONAIS).forEach { (lbl, f) ->
                            FilterChip(
                                selected = filtro == f,
                                onClick = { filtro = f },
                                label = { Text(lbl, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    Text("${lista.size} ocorrência(s)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (lista.isEmpty()) {
                item { EmptyState(title = "Sem ocorrências", message = "Ajusta o filtro.") }
            } else {
                items(lista, key = { it.id }) { alerta ->
                    AlertaRow(alerta = alerta, onClick = { onOpenAlerta(alerta.id) })
                }
            }
        }
    }
}

@Composable
private fun AlertKpi(modifier: Modifier, valor: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AlertaRow(alerta: Alerta, onClick: () -> Unit) {
    val accentColor = when (alerta.severidade) {
        SeveridadeAlertaUi.CRITICA -> FactoryAlert
        SeveridadeAlertaUi.ALTA -> FactoryWarning
        SeveridadeAlertaUi.MEDIA -> FactoryInfo
        SeveridadeAlertaUi.BAIXA -> FactoryPrimary
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(4.dp, 40.dp).clip(RoundedCornerShape(2.dp)).background(accentColor))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = alerta.titulo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    PriorityBadge(priority = when (alerta.severidade) {
                        SeveridadeAlertaUi.CRITICA -> PriorityBadgeType.CRITICA
                        SeveridadeAlertaUi.ALTA -> PriorityBadgeType.ALTA
                        else -> PriorityBadgeType.NORMAL
                    }, labelOverride = alerta.severidade.label())
                }
                Text(text = alerta.descricao, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusChip(status = when { alerta.estado == EstadoAlertaUi.RESOLVIDO || alerta.estado == EstadoAlertaUi.FECHADO -> StatusChipType.CONCLUIDO
                        alerta.tipo == TipoAlertaUi.BLOQUEIO -> StatusChipType.BLOQUEADO
                        else -> StatusChipType.NORMAL
                    }, labelOverride = alerta.tipo.label())
                    StatusChip(status = if (alerta.estado == EstadoAlertaUi.RESOLVIDO || alerta.estado == EstadoAlertaUi.FECHADO) StatusChipType.CONCLUIDO else StatusChipType.NORMAL, labelOverride = alerta.estado.label())
                }
            }
            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

package com.example.aplicacaodecontrolofabrica.features.historico

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.data.model.HistoricoTecnicoUi
import com.example.aplicacaodecontrolofabrica.data.model.label
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.EmptyState
import com.example.aplicacaodecontrolofabrica.ui.components.ErrorState
import com.example.aplicacaodecontrolofabrica.ui.components.LoadingView
import com.example.aplicacaodecontrolofabrica.ui.components.SearchBar
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChip
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChipType
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryInfo
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPrimary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactorySecondary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

private enum class RastreioFiltroUi { TODOS, COM_VIN, CONCLUIDOS, COM_SERVICOS }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoricoScreen(
    onOpenOrdem: (Int) -> Unit,
    viewModel: HistoricoViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    var pesquisa by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf(RastreioFiltroUi.TODOS) }

    val lista = uiState.historico
        .filter { when (filtro) {
            RastreioFiltroUi.TODOS -> true
            RastreioFiltroUi.COM_VIN -> !it.vin.isNullOrBlank()
            RastreioFiltroUi.CONCLUIDOS -> it.unidadeConcluida
            RastreioFiltroUi.COM_SERVICOS -> it.totalServicos > 0
        }}
        .filter { pesquisa.isBlank() || listOfNotNull(it.numeroOrdem, it.vin, it.paisDestino, it.resumoTecnico)
            .any { s -> s.contains(pesquisa.trim(), ignoreCase = true) }
        }

    when {
        uiState.isLoading -> LoadingView(message = "A carregar rastreio...")
        uiState.errorMessage != null -> ErrorState(message = uiState.errorMessage ?: "Erro.", onRetry = { viewModel.refresh() }, modifier = Modifier.padding(16.dp))
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HistKpi(Modifier.weight(1f), uiState.historico.count { !it.vin.isNullOrBlank() }.toString(), "Com VIN", FactoryPrimary)
                    HistKpi(Modifier.weight(1f), uiState.historico.count { it.unidadeConcluida }.toString(), "Concluídas", FactorySecondary)
                    HistKpi(Modifier.weight(1f), uiState.historico.size.toString(), "Total", FactoryInfo)
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SearchBar(value = pesquisa, onValueChange = { pesquisa = it }, label = "Pesquisar ordem, VIN ou destino")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Todos" to RastreioFiltroUi.TODOS, "Com VIN" to RastreioFiltroUi.COM_VIN,
                            "Concluídas" to RastreioFiltroUi.CONCLUIDOS, "Com serviços" to RastreioFiltroUi.COM_SERVICOS).forEach { (lbl, f) ->
                            FilterChip(selected = filtro == f, onClick = { filtro = f },
                                label = { Text(lbl, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                    Text("${lista.size} registo(s)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (lista.isEmpty()) {
                item { EmptyState(title = "Sem resultados", message = "Ajusta o filtro.") }
            } else {
                items(lista, key = { it.ordemId }) { item ->
                    HistoricoRow(item = item, onClick = { onOpenOrdem(item.ordemId) })
                }
            }
        }
    }
}

@Composable
private fun HistKpi(modifier: Modifier, valor: String, label: String, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HistoricoRow(item: HistoricoTecnicoUi, onClick: () -> Unit) {
    val accentColor = when { item.unidadeConcluida -> FactorySecondary; item.totalServicos > 0 -> FactoryWarning; else -> FactoryInfo }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(4.dp, 40.dp).clip(RoundedCornerShape(2.dp)).background(accentColor))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(item.numeroOrdem, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    StatusChip(
                        status = if (item.unidadeConcluida) StatusChipType.CONCLUIDO else StatusChipType.ATENCAO,
                        labelOverride = if (item.unidadeConcluida) "Concluída" else "Em acomp."
                    )
                }
                Text(
                    text = listOfNotNull(item.vin, item.paisDestino, "${item.totalServicos} serv.").joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

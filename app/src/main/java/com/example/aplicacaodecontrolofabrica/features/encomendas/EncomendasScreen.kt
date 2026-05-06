package com.example.aplicacaodecontrolofabrica.features.encomendas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.*
import com.example.aplicacaodecontrolofabrica.ui.theme.*

@Composable
fun EncomendasScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onOpenEncomenda: (Int) -> Unit = {},
    viewModel: EncomendasViewModel = viewModel(factory = ViewModelFactory())
) {
    val state by viewModel.uiState.collectAsState()

    when {
        state.isLoading -> LoadingView(message = "A carregar encomendas...")
        state.errorMessage != null -> ErrorState(
            message = state.errorMessage ?: "Erro.",
            onRetry = { viewModel.refresh() },
            modifier = Modifier.padding(contentPadding).padding(16.dp)
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EncKpi(Modifier.weight(1f), state.totalPendentes.toString(), "Pendentes", FactoryWarning)
                    EncKpi(Modifier.weight(1f), state.totalEmProducao.toString(), "Em produção", FactoryPrimary)
                    EncKpi(Modifier.weight(1f), state.totalConcluidas.toString(), "Concluídas", FactorySecondary)
                }
            }

            if (state.encomendas.isEmpty()) {
                item { EmptyState(title = "Sem encomendas", message = "Não há encomendas registadas.") }
            } else {
                items(state.encomendas, key = { it.id }) { enc ->
                    EncomendaRow(enc, onClick = { onOpenEncomenda(enc.id) })
                }
            }
        }
    }
}

@Composable
private fun EncKpi(modifier: Modifier, valor: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EncomendaRow(enc: EncomendaUi, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(enc.clienteNome, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    StatusChip(
                        status = when (enc.estado) { 0 -> StatusChipType.ATENCAO; 1 -> StatusChipType.NORMAL; else -> StatusChipType.CONCLUIDO },
                        labelOverride = enc.estadoLabel
                    )
                }
                Text("${enc.modeloNome} · ${enc.quantidade} un.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Criada: ${enc.dataCriacao}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    enc.dataEntrega?.let { Text("Entrega: $it", style = MaterialTheme.typography.labelSmall, color = FactoryWarning) }
                }
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

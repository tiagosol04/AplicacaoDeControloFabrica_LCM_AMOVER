package com.example.aplicacaodecontrolofabrica.features.servicos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.data.dto.ServicoDto
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.*
import com.example.aplicacaodecontrolofabrica.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServicosScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onOpenServico: (Int) -> Unit,
    viewModel: ServicosViewModel = viewModel(factory = ViewModelFactory())
) {
    val state by viewModel.uiState.collectAsState()
    var pesquisa by rememberSaveable { mutableStateOf("") }

    when {
        state.isLoading -> LoadingView(message = "A carregar serviços...")
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
            // KPIs
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ServicoKpi(Modifier.weight(1f), state.totalEmAberto.toString(), "Em aberto", FactoryWarning)
                    ServicoKpi(Modifier.weight(1f), state.totalGarantias.toString(), "Garantias", FactoryInfo)
                    ServicoKpi(Modifier.weight(1f), state.totalAvarias.toString(), "Avarias", FactoryAlert)
                    ServicoKpi(Modifier.weight(1f), state.totalConcluidos.toString(), "Concluídos", FactorySecondary)
                }
            }

            // Pesquisa + Filtros
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SearchBar(
                        value = pesquisa,
                        onValueChange = { pesquisa = it; viewModel.setPesquisa(it) },
                        label = "Pesquisar VIN, modelo, descrição..."
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "Todos" to FiltroServicoUi.TODOS,
                            "Em aberto" to FiltroServicoUi.EM_ABERTO,
                            "Garantia" to FiltroServicoUi.GARANTIA,
                            "Avaria" to FiltroServicoUi.AVARIA,
                            "Concluídos" to FiltroServicoUi.CONCLUIDOS
                        ).forEach { (label, filtro) ->
                            FilterChip(
                                selected = state.filtroAtivo == filtro,
                                onClick = { viewModel.setFiltro(filtro) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    Text(
                        "${state.servicos.size} serviço(s)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Lista
            if (state.servicos.isEmpty()) {
                item { EmptyState(title = "Sem serviços", message = "Ajusta o filtro ou pesquisa.") }
            } else {
                items(state.servicos, key = { it.idServico ?: 0 }) { servico ->
                    ServicoRow(servico = servico, onClick = { servico.idServico?.let(onOpenServico) })
                }
            }
        }
    }
}

@Composable
private fun ServicoKpi(modifier: Modifier, valor: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ServicoRow(servico: ServicoDto, onClick: () -> Unit) {
    val estadoColor = when (servico.estado) {
        0 -> FactoryInfo      // Agendado
        1 -> FactoryWarning   // Em Curso
        2 -> FactorySecondary // Concluído
        else -> FactoryInfo
    }
    val tipoColor = when (servico.tipo) {
        2 -> FactoryAlert  // Avaria
        3 -> FactoryWarning // Garantia
        else -> FactoryInfo
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    servico.tipoNome ?: "Serviço",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(
                    status = when (servico.estado) {
                        0 -> StatusChipType.NORMAL
                        1 -> StatusChipType.ATENCAO
                        2 -> StatusChipType.CONCLUIDO
                        else -> StatusChipType.NORMAL
                    },
                    labelOverride = servico.estadoNome ?: "—"
                )
            }
            Text(
                text = listOfNotNull(
                    servico.numeroIdentificacao?.takeIf { it.isNotBlank() },
                    servico.modeloNome?.takeIf { it.isNotBlank() },
                    servico.descricao?.takeIf { it.isNotBlank() }?.take(60)
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            servico.dataServico?.take(10)?.let { data ->
                Text(data, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

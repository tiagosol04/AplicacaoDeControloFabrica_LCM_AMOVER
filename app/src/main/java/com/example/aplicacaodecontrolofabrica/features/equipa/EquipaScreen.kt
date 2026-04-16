package com.example.aplicacaodecontrolofabrica.features.equipa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Button
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
import com.example.aplicacaodecontrolofabrica.data.model.ColaboradorFabricaUi
import com.example.aplicacaodecontrolofabrica.data.model.EstadoColaboradorUi
import com.example.aplicacaodecontrolofabrica.data.model.FiltroEquipaUi
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.EmptyState
import com.example.aplicacaodecontrolofabrica.ui.components.ErrorState
import com.example.aplicacaodecontrolofabrica.ui.components.LoadingView
import com.example.aplicacaodecontrolofabrica.ui.components.SearchBar
import com.example.aplicacaodecontrolofabrica.ui.components.SectionCard
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChip
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChipType
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryAlert
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryInfo
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPrimary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactorySecondary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EquipaScreen(
    contentPadding: PaddingValues,
    viewModel: EquipaViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val lista = viewModel.listaFiltrada()

    when {
        uiState.isLoading -> LoadingView(message = "A carregar turno...")
        uiState.errorMessage != null && uiState.colaboradores.isEmpty() -> ErrorState(
            message = uiState.errorMessage ?: "Erro.", onRetry = { viewModel.refresh() },
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
                    EquipaKpi(Modifier.weight(1f), uiState.resumo.disponiveis.toString(), "Disponíveis", FactorySecondary)
                    EquipaKpi(Modifier.weight(1f), uiState.resumo.afetos.toString(), "Afetos", FactoryPrimary)
                    EquipaKpi(Modifier.weight(1f), uiState.resumo.sobrecarga.toString(), "Sobrecarga", FactoryWarning)
                    EquipaKpi(Modifier.weight(1f), uiState.resumo.indisponiveis.toString(), "Ausentes", FactoryAlert)
                }
            }

            // Filtros
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SearchBar(value = uiState.pesquisa, onValueChange = viewModel::atualizarPesquisa, label = "Pesquisar colaborador")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Todos" to FiltroEquipaUi.TODOS, "Disponíveis" to FiltroEquipaUi.DISPONIVEIS,
                            "Afetos" to FiltroEquipaUi.AFETOS, "Sobrecarga" to FiltroEquipaUi.SOBRECARGA,
                            "Ausentes" to FiltroEquipaUi.INDISPONIVEIS).forEach { (lbl, f) ->
                            FilterChip(selected = uiState.filtro == f, onClick = { viewModel.atualizarFiltro(f) },
                                label = { Text(lbl, style = MaterialTheme.typography.labelSmall) })
                        }
                    }
                    Text("${lista.size} pessoa(s)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Feedback
            uiState.feedbackMessage?.let { fb ->
                item {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = FactorySecondary.copy(alpha = 0.08f))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = fb, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Button(onClick = { viewModel.limparFeedback() }) { Text("OK") }
                        }
                    }
                }
            }

            if (lista.isEmpty()) {
                item { EmptyState(title = "Sem pessoas", message = "Ajusta o filtro.") }
            } else {
                items(lista, key = { it.id }) { c ->
                    ColaboradorCard(colaborador = c, isSaving = uiState.isSaving, onToggle = { viewModel.alternarDisponibilidade(c) })
                }
            }
        }
    }
}

@Composable
private fun EquipaKpi(modifier: Modifier, valor: String, label: String, color: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColaboradorCard(
    colaborador: ColaboradorFabricaUi,
    isSaving: Boolean,
    onToggle: () -> Unit
) {
    val cor = when (colaborador.estado) {
        EstadoColaboradorUi.DISPONIVEL -> FactorySecondary
        EstadoColaboradorUi.AFETO -> FactoryPrimary
        EstadoColaboradorUi.SOBRECARGA -> FactoryWarning
        EstadoColaboradorUi.INDISPONIVEL -> FactoryAlert
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(cor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = colaborador.nome.take(1).uppercase(), color = cor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = colaborador.nome, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = colaborador.funcao, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusChip(
                    status = when (colaborador.estado) {
                        EstadoColaboradorUi.DISPONIVEL -> StatusChipType.CONCLUIDO
                        EstadoColaboradorUi.AFETO -> StatusChipType.NORMAL
                        EstadoColaboradorUi.SOBRECARGA -> StatusChipType.ATENCAO
                        EstadoColaboradorUi.INDISPONIVEL -> StatusChipType.BLOQUEADO
                    },
                    labelOverride = colaborador.disponibilidadeLabel
                )
            }

            if (colaborador.notaOperacional.isNotBlank()) {
                Text(text = colaborador.notaOperacional, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Unidades compactas
            if (colaborador.unidadesAtuais.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    colaborador.unidadesAtuais.take(2).forEach { u ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Filled.DirectionsBike, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${u.modeloNome} · ${u.vin}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            if (colaborador.podeAlterarEstado) {
                Button(onClick = onToggle, enabled = !isSaving, modifier = Modifier.fillMaxWidth()) {
                    Text(if (colaborador.ativoNaBaseDados) "Marcar ausente" else "Reativar", maxLines = 1)
                }
            }
        }
    }
}

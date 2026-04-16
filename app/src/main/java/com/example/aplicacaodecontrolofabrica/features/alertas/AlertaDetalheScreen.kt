package com.example.aplicacaodecontrolofabrica.features.alertas

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.data.model.Alerta
import com.example.aplicacaodecontrolofabrica.data.model.SeveridadeAlertaUi
import com.example.aplicacaodecontrolofabrica.data.model.label
import com.example.aplicacaodecontrolofabrica.data.model.EstadoAlertaUi
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.ErrorState
import com.example.aplicacaodecontrolofabrica.ui.components.LoadingView
import com.example.aplicacaodecontrolofabrica.ui.components.PriorityBadge
import com.example.aplicacaodecontrolofabrica.ui.components.PriorityBadgeType
import com.example.aplicacaodecontrolofabrica.ui.components.SectionCard
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChip
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChipType
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryAlert
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryInfo
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlertaDetalheScreen(
    alertaId: Int,
    onBack: () -> Unit,
    viewModel: AlertasViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(alertaId) { viewModel.selecionarAlerta(alertaId) }
    val alerta = uiState.alertaSelecionado

    when {
        uiState.isLoading && alerta == null -> LoadingView(message = "A carregar ocorrência...")
        uiState.errorMessage != null && alerta == null -> ErrorState(
            message = uiState.errorMessage ?: "Erro.",
            onRetry = { viewModel.refresh(alertaId) },
            modifier = Modifier.padding(16.dp)
        )
        alerta == null -> {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
                Text("Ocorrência não encontrada.", style = MaterialTheme.typography.titleSmall)
                OutlinedButton(onClick = onBack, modifier = Modifier.padding(top = 12.dp)) { Text("Voltar") }
            }
        }
        else -> {
            val heroColor = when (alerta.severidade) {
                SeveridadeAlertaUi.CRITICA -> FactoryAlert
                SeveridadeAlertaUi.ALTA -> FactoryWarning
                else -> FactoryInfo
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(PaddingValues(16.dp)),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Card(shape = RoundedCornerShape(16.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(heroColor, heroColor.copy(alpha = 0.7f))))
                        .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = alerta.titulo, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(text = alerta.descricao, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                PriorityBadge(
                                    priority = when (alerta.severidade) { SeveridadeAlertaUi.CRITICA -> PriorityBadgeType.CRITICA; SeveridadeAlertaUi.ALTA -> PriorityBadgeType.ALTA; else -> PriorityBadgeType.NORMAL },
                                    labelOverride = alerta.severidade.label()
                                )
                                StatusChip(
                                    status = when { alerta.estado == EstadoAlertaUi.RESOLVIDO || alerta.estado == EstadoAlertaUi.FECHADO -> StatusChipType.CONCLUIDO; alerta.severidade == SeveridadeAlertaUi.CRITICA -> StatusChipType.BLOQUEADO; else -> StatusChipType.NORMAL },
                                    labelOverride = alerta.estado.label()
                                )
                                StatusChip(status = StatusChipType.NORMAL, labelOverride = alerta.tipo.label())
                            }
                        }
                    }
                }

                SectionCard(title = "Contexto") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DetailRow("Ordem", alerta.ordemId?.toString() ?: "—")
                        DetailRow("VIN", alerta.vin ?: "—")
                        DetailRow("Origem", alerta.origem.label())
                        DetailRow("Responsável", alerta.responsavelNome ?: "—")
                    }
                }

                SectionCard(title = "Datas") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DetailRow("Criação", alerta.dataCriacaoIso ?: "—")
                        DetailRow("Atualização", alerta.dataAtualizacaoIso ?: "—")
                        DetailRow("Resolução", alerta.dataResolucaoIso ?: "—")
                    }
                }

                alerta.observacoes?.takeIf { it.isNotBlank() }?.let { obs ->
                    SectionCard(title = "Observações") {
                        Text(text = obs, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

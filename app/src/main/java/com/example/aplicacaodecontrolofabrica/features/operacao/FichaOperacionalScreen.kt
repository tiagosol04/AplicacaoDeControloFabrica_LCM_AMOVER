package com.example.aplicacaodecontrolofabrica.features.operacao

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPrimary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactorySecondary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FichaOperacionalScreen(
    ordemId: Int,
    onBack: () -> Unit,
    viewModel: OrdemDetalheRealViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(ordemId) { viewModel.load(ordemId) }

    // Diálogo de confirmação
    showConfirmDialog?.let { estado ->
        val titulo = when (estado) {
            1 -> "Colocar em produção"
            2 -> "Concluir ordem"
            3 -> "Bloquear ordem"
            10 -> "Iniciar produção"
            20 -> "Finalizar ordem"
            else -> "Alterar estado"
        }
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            title = { Text(titulo) },
            text = { Text("Confirmar esta ação para ${uiState.numeroOrdem}?") },
            confirmButton = {
                Button(onClick = {
                    when (estado) {
                        10 -> viewModel.iniciarOrdem()
                        20 -> viewModel.finalizarOrdem()
                        else -> viewModel.atualizarEstado(estado)
                    }
                    showConfirmDialog = null
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = null }) { Text("Cancelar") }
            }
        )
    }

    when {
        uiState.isLoading -> LoadingView(message = "A carregar ordem...")
        uiState.errorMessage != null -> ErrorState(
            message = uiState.errorMessage ?: "Erro.",
            onRetry = { viewModel.load(ordemId) },
            modifier = Modifier.padding(16.dp)
        )
        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Header compacto ──
            Card(shape = RoundedCornerShape(16.dp)) {
                val accentColor = when {
                    uiState.bloqueada -> FactoryAlert
                    uiState.concluida -> FactorySecondary
                    uiState.vinPendente || !uiState.unidadeRegistada -> FactoryWarning
                    else -> FactoryPrimary
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(accentColor, accentColor.copy(alpha = 0.7f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = uiState.numeroOrdem,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.modeloNome,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            StatusChip(
                                status = when {
                                    uiState.bloqueada -> StatusChipType.BLOQUEADO
                                    uiState.concluida -> StatusChipType.CONCLUIDO
                                    else -> StatusChipType.NORMAL
                                },
                                labelOverride = uiState.estadoLabel
                            )
                            PriorityBadge(
                                priority = when {
                                    uiState.bloqueada -> PriorityBadgeType.CRITICA
                                    uiState.vinPendente -> PriorityBadgeType.ALTA
                                    else -> PriorityBadgeType.NORMAL
                                },
                                labelOverride = uiState.proximaAcao
                            )
                        }
                    }
                }
            }

            // ── Gates de rastreabilidade ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GateIndicator(Modifier.weight(1f), "Unidade", uiState.unidadeRegistada)
                GateIndicator(
                    Modifier.weight(1f), "VIN",
                    uiState.unidadeRegistada && !uiState.vinPendente
                )
                GateIndicator(
                    Modifier.weight(1f), "Qualidade",
                    uiState.montagemOk && uiState.embalagemOk && uiState.controloOk
                )
            }

            // ── Contexto ──
            SectionCard(title = "Contexto") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Modelo", uiState.modeloNome)
                    InfoRow("Cliente", uiState.clienteNome)
                    InfoRow("Destino", uiState.paisDestino)
                    InfoRow("Criação", uiState.dataCriacaoIso ?: "—")
                    InfoRow("Conclusão", uiState.dataConclusaoIso ?: "—")
                }
            }

            // ── Risco ──
            SectionCard(title = "Estado operacional") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = uiState.prontidaoTexto,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (uiState.vinList.isEmpty()) {
                        Text(
                            text = "Sem VINs registados",
                            style = MaterialTheme.typography.bodySmall,
                            color = FactoryWarning
                        )
                    } else {
                        uiState.vinList.forEachIndexed { i, vin ->
                            InfoRow("VIN ${i + 1}", vin)
                        }
                    }
                }
            }

            // ── Indicadores rápidos ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStat(Modifier.weight(1f), Icons.Filled.DirectionsBike, "Unidades", uiState.totalMotas.toString())
                MiniStat(Modifier.weight(1f), Icons.Filled.AssignmentTurnedIn, "Serviços", uiState.totalServicos.toString())
                MiniStat(Modifier.weight(1f), Icons.Filled.PlayArrow, "Fecho", if (uiState.controloOk) "OK" else "Pend.")
            }

            // ── Ações com confirmação ──
            if (!uiState.concluida) {
                SectionCard(title = "Ações") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Iniciar ordem (via API endpoint dedicado - cria checklists)
                        if (uiState.estadoLabel == "Por arrancar") {
                            Button(
                                onClick = { showConfirmDialog = 10 },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uiState.isUpdating,
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Iniciar produção") }
                        }

                        if (uiState.estadoLabel == "Em produção") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showConfirmDialog = 3 },
                                    modifier = Modifier.weight(1f),
                                    enabled = !uiState.isUpdating
                                ) { Text("Bloquear", maxLines = 1) }
                                Button(
                                    onClick = { showConfirmDialog = 20 },
                                    modifier = Modifier.weight(1f),
                                    enabled = !uiState.isUpdating,
                                    colors = ButtonDefaults.buttonColors(containerColor = FactorySecondary)
                                ) { Text("Finalizar", maxLines = 1) }
                            }
                        }

                        if (uiState.isUpdating) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GateIndicator(modifier: Modifier, label: String, ok: Boolean) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ok) FactorySecondary.copy(alpha = 0.08f)
                else FactoryWarning.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (ok) FactorySecondary else FactoryWarning)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (ok) "OK" else "Pendente",
                style = MaterialTheme.typography.labelSmall,
                color = if (ok) FactorySecondary else FactoryWarning
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MiniStat(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

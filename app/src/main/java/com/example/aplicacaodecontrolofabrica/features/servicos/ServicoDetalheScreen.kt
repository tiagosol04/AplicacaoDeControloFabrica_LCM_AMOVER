package com.example.aplicacaodecontrolofabrica.features.servicos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Warning
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
fun ServicoDetalheScreen(
    servicoId: Int,
    onBack: () -> Unit,
    viewModel: ServicoDetalheViewModel = viewModel(factory = ViewModelFactory())
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(servicoId) { viewModel.load(servicoId) }

    when {
        state.isLoading -> LoadingView(message = "A carregar serviço...")
        state.errorMessage != null -> ErrorState(
            message = state.errorMessage ?: "Erro.",
            onRetry = { viewModel.load(servicoId) },
            modifier = Modifier.padding(16.dp)
        )
        state.servico != null -> {
            val s = state.servico!!
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cabeçalho
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(s.tipoNome ?: "Serviço", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                StatusChip(
                                    status = when (s.estado) { 0 -> StatusChipType.NORMAL; 1 -> StatusChipType.ATENCAO; 2 -> StatusChipType.CONCLUIDO; else -> StatusChipType.NORMAL },
                                    labelOverride = s.estadoNome ?: "—"
                                )
                            }
                            s.descricao?.takeIf { it.isNotBlank() }?.let {
                                Text(it, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Info da mota
                item {
                    SectionCard(title = "Unidade") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            InfoRow("VIN / Quadro", s.mota?.numeroIdentificacao ?: s.numeroIdentificacao ?: "—")
                            InfoRow("Modelo", s.mota?.modelo?.nome ?: s.modeloNome ?: "—")
                            InfoRow("Cor", s.mota?.cor ?: s.cor ?: "—")
                            InfoRow("Quilometragem", s.mota?.quilometragem?.let { "${it.toInt()} km" } ?: "—")
                        }
                    }
                }

                // Datas
                item {
                    SectionCard(title = "Datas") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            InfoRow("Data do serviço", s.dataServico?.take(10) ?: "—")
                            InfoRow("Data de conclusão", s.dataConclusao?.take(10) ?: "—")
                        }
                    }
                }

                // Notas
                s.notasServico?.takeIf { it.isNotBlank() }?.let { notas ->
                    item {
                        SectionCard(title = "Notas do serviço") {
                            Text(notas, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // Peças alteradas
                if (state.pecasAlteradas.isNotEmpty()) {
                    item {
                        SectionCard(title = "Peças alteradas (${state.pecasAlteradas.size})") {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.pecasAlteradas.forEach { p ->
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = FactoryWarning.copy(alpha = 0.06f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("${p.partNumber ?: ""} — ${p.descricao ?: ""}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                            Text("S/N: ${p.numeroSerieAtual ?: p.numeroSerie ?: "—"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            p.observacoes?.takeIf { it.isNotBlank() }?.let {
                                                Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Problemas frequentes do modelo
                if (state.problemasModelo.isNotEmpty()) {
                    item {
                        SectionCard(title = "Problemas frequentes do modelo") {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.problemasModelo.forEach { prob ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Warning, contentDescription = null, tint = FactoryWarning, modifier = Modifier.size(16.dp))
                                        Text("${prob.descricao ?: "—"} (${prob.total ?: 0}x em ${prob.totalMotas ?: 0} motas)", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (state.totalGarantiasModelo > 0) {
                                    Text(
                                        "${state.totalGarantiasModelo} serviço(s) de garantia registados neste modelo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = FactoryInfo
                                    )
                                }
                            }
                        }
                    }
                }

                // Ações
                if ((s.estado ?: 0) != 2) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            if ((s.estado ?: 0) == 0) {
                                Button(
                                    onClick = { viewModel.iniciarServico(servicoId) },
                                    enabled = !state.isUpdating,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Iniciar")
                                }
                            }
                            Button(
                                onClick = { viewModel.concluirServico(servicoId) },
                                enabled = !state.isUpdating,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FactorySecondary)
                            ) {
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Concluir")
                            }
                        }
                    }
                }

                // Mensagem de sucesso
                state.successMessage?.let { msg ->
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = FactorySecondary.copy(alpha = 0.1f)), shape = RoundedCornerShape(10.dp)) {
                            Text(msg, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = FactorySecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

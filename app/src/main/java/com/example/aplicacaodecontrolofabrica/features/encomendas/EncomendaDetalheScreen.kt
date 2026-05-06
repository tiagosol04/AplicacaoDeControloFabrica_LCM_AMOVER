package com.example.aplicacaodecontrolofabrica.features.encomendas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.data.model.RoleAccessUi
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.EmptyState
import com.example.aplicacaodecontrolofabrica.ui.components.ErrorState
import com.example.aplicacaodecontrolofabrica.ui.components.LoadingView
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChip
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChipType
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryAlert
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPrimary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

@Composable
fun EncomendaDetalheScreen(
    encomendaId: Int,
    access: RoleAccessUi? = null,
    onBack: () -> Unit,
    onOpenOrdem: (Int) -> Unit,
    viewModel: EncomendaDetalheViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCriarDialog by remember { mutableStateOf(false) }

    LaunchedEffect(encomendaId) { viewModel.load(encomendaId) }

    LaunchedEffect(uiState.actionSuccess, uiState.actionError) {
        val msg = uiState.actionSuccess ?: uiState.actionError
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    if (showCriarDialog) {
        AlertDialog(
            onDismissRequest = { showCriarDialog = false },
            title = { Text("Criar ordens de produção") },
            text = { Text("Serão criadas ordens de produção para esta encomenda (${uiState.quantidade} unidade(s)). Confirmar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showCriarDialog = false
                        viewModel.criarOrdensFromEncomenda(encomendaId)
                    },
                    enabled = !uiState.actionLoading
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showCriarDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LoadingView(message = "A carregar encomenda...")
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage ?: "Erro.",
                onRetry = { viewModel.load(encomendaId) },
                modifier = Modifier.padding(16.dp)
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    EncomendaInfoCard(uiState)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ordens de produção (${uiState.ordens.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (access?.canCriarOrdemDeEncomenda == true && uiState.estado != 2) {
                            if (uiState.actionLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(4.dp), strokeWidth = 2.dp)
                            } else {
                                TextButton(onClick = { showCriarDialog = true }) {
                                    Icon(Icons.Filled.Add, contentDescription = null)
                                    Text("Criar ordens")
                                }
                            }
                        }
                    }
                }

                if (uiState.ordens.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Sem ordens de produção",
                            message = "Ainda não foram criadas ordens para esta encomenda."
                        )
                    }
                } else {
                    items(uiState.ordens, key = { it.ordemId }) { ordem ->
                        OrdemEncomendaRow(ordem = ordem, onClick = { onOpenOrdem(ordem.ordemId) })
                    }
                }

                item { Spacer(Modifier.height(72.dp)) }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}

@Composable
private fun EncomendaInfoCard(state: EncomendaDetalheUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.clienteNome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(
                    status = when (state.estado) {
                        0 -> StatusChipType.ATENCAO
                        1 -> StatusChipType.NORMAL
                        else -> StatusChipType.CONCLUIDO
                    },
                    labelOverride = state.estadoLabel
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            InfoRow("Modelo", state.modeloNome)
            InfoRow("Quantidade", "${state.quantidade} unidade(s)")
            InfoRow("Criada em", state.dataCriacao)
            state.dataEntrega?.let {
                InfoRow("Data de entrega", it, valueColor = FactoryWarning)
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

@Composable
private fun OrdemEncomendaRow(ordem: EncomendaOrdemUi, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ordem.bloqueada)
                FactoryAlert.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Ordem ${ordem.numeroOrdem}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                StatusChip(
                    status = when {
                        ordem.bloqueada -> StatusChipType.BLOQUEADO
                        ordem.estadoLabel == "Concluída" -> StatusChipType.CONCLUIDO
                        else -> StatusChipType.NORMAL
                    },
                    labelOverride = ordem.estadoLabel
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.example.aplicacaodecontrolofabrica.features.cockpit

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.EmptyState
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
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    onOpenOrdens: () -> Unit,
    onOpenEquipa: () -> Unit,
    onOpenAlertas: () -> Unit,
    onOpenOrdemDetalhe: (Int) -> Unit,
    viewModel: DashboardRealViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingView(message = "A carregar cockpit...")
        uiState.errorMessage != null -> ErrorState(
            message = uiState.errorMessage ?: "Não foi possível carregar.",
            onRetry = { viewModel.refresh() },
            modifier = Modifier.padding(16.dp)
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Hero compacto ──
            item {
                Card(
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        FactoryPrimary,
                                        FactoryPrimary.copy(alpha = 0.75f)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hoje na fábrica",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${uiState.totalOrdens} ordens · ${uiState.equipaAtiva} pessoas · ${uiState.bloqueadas + uiState.vinPendente + uiState.controloPendente} decisões",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // ── KPIs 2x2 — clicáveis ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Bloqueadas",
                            valor = uiState.bloqueadas.toString(),
                            color = FactoryAlert,
                            icon = Icons.Filled.WarningAmber,
                            onClick = onOpenOrdens
                        )
                        KpiCard(
                            modifier = Modifier.weight(1f),
                            titulo = "VIN pendente",
                            valor = uiState.vinPendente.toString(),
                            color = FactoryWarning,
                            icon = Icons.Filled.PriorityHigh,
                            onClick = onOpenOrdens
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Sem unidade",
                            valor = uiState.semUnidade.toString(),
                            color = FactoryInfo,
                            icon = Icons.Filled.Bolt,
                            onClick = onOpenOrdens
                        )
                        KpiCard(
                            modifier = Modifier.weight(1f),
                            titulo = "Controlo pend.",
                            valor = uiState.controloPendente.toString(),
                            color = FactoryPrimary,
                            icon = Icons.Filled.PlayArrow,
                            onClick = onOpenOrdens
                        )
                    }
                }
            }

            // ── Ações imediatas ──
            if (uiState.acoesImediatas.isNotEmpty()) {
                item {
                    SectionCard(title = "Decisões imediatas") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.acoesImediatas.take(4).forEach { acao ->
                                ActionRow(
                                    titulo = acao.titulo,
                                    descricao = acao.descricao,
                                    onOpen = acao.ordemId?.let { id -> { onOpenOrdemDetalhe(id) } }
                                )
                            }
                        }
                    }
                }
            }

            // ── Ordens prioritárias ──
            item {
                SectionCard(title = "Ordens em foco") {
                    if (uiState.ordensPrioritarias.isEmpty()) {
                        EmptyState(
                            title = "Sem ordens críticas",
                            message = "Não existem ordens com prioridade forte neste momento."
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.ordensPrioritarias.forEach { ordem ->
                                FocusOrderRow(
                                    ordem = ordem,
                                    onOpen = { onOpenOrdemDetalhe(ordem.ordemId) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Equipa ──
            if (uiState.equipaHoje.isNotEmpty()) {
                item {
                    SectionCard(title = "Turno ativo") {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            uiState.equipaHoje.forEach { pessoa ->
                                TurnoRow(
                                    nome = pessoa.nome,
                                    funcao = pessoa.funcao,
                                    disponibilidade = pessoa.disponibilidade,
                                    ativo = pessoa.ativo
                                )
                            }
                        }
                    }
                }
            }

            // ── Atalhos rápidos ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickLink(
                        label = "Prioridades",
                        modifier = Modifier.weight(1f),
                        onClick = onOpenOrdens
                    )
                    QuickLink(
                        label = "Ocorrências",
                        modifier = Modifier.weight(1f),
                        onClick = onOpenAlertas
                    )
                    QuickLink(
                        label = "Turno",
                        modifier = Modifier.weight(1f),
                        onClick = onOpenEquipa
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    titulo: String,
    valor: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun ActionRow(
    titulo: String,
    descricao: String,
    onOpen: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onOpen != null) Modifier.clickable(onClick = onOpen) else Modifier)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(FactoryAlert)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = descricao,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onOpen != null) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FocusOrderRow(
    ordem: DashboardOrderUi,
    onOpen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
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
                    priority = when (ordem.prioridadeLabel) {
                        "Crítica" -> PriorityBadgeType.CRITICA
                        "Alta" -> PriorityBadgeType.ALTA
                        else -> PriorityBadgeType.NORMAL
                    }
                )
            }
            Text(
                text = ordem.modeloNome,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        StatusChip(
            status = when {
                ordem.bloqueada -> StatusChipType.BLOQUEADO
                ordem.vinPendente || ordem.controloPendente -> StatusChipType.ATENCAO
                else -> StatusChipType.NORMAL
            },
            labelOverride = ordem.estadoLabel
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun TurnoRow(
    nome: String,
    funcao: String,
    disponibilidade: String,
    ativo: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (ativo) FactoryPrimary.copy(alpha = 0.12f)
                    else FactoryAlert.copy(alpha = 0.08f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nome.take(1).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (ativo) FactoryPrimary else FactoryAlert
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nome,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = funcao,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        StatusChip(
            status = when {
                !ativo -> StatusChipType.BLOQUEADO
                disponibilidade.contains("Sobrecarga", ignoreCase = true) -> StatusChipType.ATENCAO
                disponibilidade.contains("Dispon", ignoreCase = true) -> StatusChipType.CONCLUIDO
                else -> StatusChipType.NORMAL
            },
            labelOverride = disponibilidade
        )
    }
}

@Composable
private fun QuickLink(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

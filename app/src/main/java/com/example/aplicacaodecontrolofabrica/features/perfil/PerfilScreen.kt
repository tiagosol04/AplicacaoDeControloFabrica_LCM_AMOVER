package com.example.aplicacaodecontrolofabrica.features.perfil

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacaodecontrolofabrica.di.ViewModelFactory
import com.example.aplicacaodecontrolofabrica.ui.components.SectionCard
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChip
import com.example.aplicacaodecontrolofabrica.ui.components.StatusChipType
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryAlert
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPrimary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPurple
import com.example.aplicacaodecontrolofabrica.ui.theme.FactorySecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerfilScreen(
    onLogout: () -> Unit,
    viewModel: PerfilRealViewModel = viewModel(factory = ViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(14.dp))
            Text("A carregar perfil...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(16.dp)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Avatar ──
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(FactoryPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = uiState.nome.take(2).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FactoryPrimary
            )
        }

        // ── Nome e email ──
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = uiState.nome,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = uiState.email.ifBlank { "Sem email" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            StatusChip(
                status = StatusChipType.NORMAL,
                labelOverride = uiState.perfilLabel
            )
        }

        // ── Roles ──
        if (uiState.roles.isNotEmpty()) {
            SectionCard(title = "Roles atribuídas") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    uiState.roles.forEach { role ->
                        Box(
                            modifier = Modifier
                                .background(FactoryPurple.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = role,
                                style = MaterialTheme.typography.labelMedium,
                                color = FactoryPurple
                            )
                        }
                    }
                }
            }
        }

        // ── Acessos ──
        uiState.access?.let { access ->
            SectionCard(title = "Acessos operacionais") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccessRow("Cockpit", access.canOpenCockpit)
                    AccessRow("Operação", access.canOpenOperacao || access.canOpenProducao)
                    AccessRow("Alertas", access.canOpenAlertas)
                    AccessRow("Histórico", access.canOpenHistorico)
                    AccessRow("Marcar prioridade", access.canMarcarPrioridade)
                    AccessRow("Gerir bloqueios", access.canGerirBloqueios)
                    AccessRow("Registar ocorrências", access.canRegistarOcorrencias)
                    AccessRow("Consultar garantias", access.canConsultarGarantias)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Logout ──
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FactoryAlert.copy(alpha = 0.10f),
                contentColor = FactoryAlert
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Terminar sessão", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AccessRow(label: String, ativo: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Icon(
            imageVector = if (ativo) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            tint = if (ativo) FactorySecondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp)
        )
    }
}

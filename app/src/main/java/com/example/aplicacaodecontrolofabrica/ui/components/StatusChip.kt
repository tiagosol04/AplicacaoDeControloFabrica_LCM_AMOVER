package com.example.aplicacaodecontrolofabrica.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryAlert
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryPrimary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactorySecondary
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

enum class StatusChipType {
    NORMAL,
    ATENCAO,
    BLOQUEADO,
    CONCLUIDO
}

@Composable
fun StatusChip(
    status: StatusChipType,
    modifier: Modifier = Modifier,
    labelOverride: String? = null
) {
    val (label, background, textColor) = when (status) {
        StatusChipType.NORMAL -> Triple(
            labelOverride ?: "Normal",
            FactoryPrimary.copy(alpha = 0.12f),
            FactoryPrimary
        )
        StatusChipType.ATENCAO -> Triple(
            labelOverride ?: "Atenção",
            FactoryWarning.copy(alpha = 0.14f),
            FactoryWarning
        )
        StatusChipType.BLOQUEADO -> Triple(
            labelOverride ?: "Bloqueado",
            FactoryAlert.copy(alpha = 0.12f),
            FactoryAlert
        )
        StatusChipType.CONCLUIDO -> Triple(
            labelOverride ?: "Concluído",
            FactorySecondary.copy(alpha = 0.12f),
            FactorySecondary
        )
    }

    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

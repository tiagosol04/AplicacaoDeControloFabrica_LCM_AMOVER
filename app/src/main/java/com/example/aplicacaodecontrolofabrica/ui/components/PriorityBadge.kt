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
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryInfo
import com.example.aplicacaodecontrolofabrica.ui.theme.FactoryWarning

enum class PriorityBadgeType {
    NORMAL,
    ALTA,
    CRITICA
}

@Composable
fun PriorityBadge(
    priority: PriorityBadgeType,
    modifier: Modifier = Modifier,
    labelOverride: String? = null
) {
    val (label, background, textColor) = when (priority) {
        PriorityBadgeType.NORMAL -> Triple(
            labelOverride ?: "Normal",
            FactoryInfo.copy(alpha = 0.12f),
            FactoryInfo
        )
        PriorityBadgeType.ALTA -> Triple(
            labelOverride ?: "Alta",
            FactoryWarning.copy(alpha = 0.14f),
            FactoryWarning
        )
        PriorityBadgeType.CRITICA -> Triple(
            labelOverride ?: "Crítica",
            FactoryAlert.copy(alpha = 0.12f),
            FactoryAlert
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

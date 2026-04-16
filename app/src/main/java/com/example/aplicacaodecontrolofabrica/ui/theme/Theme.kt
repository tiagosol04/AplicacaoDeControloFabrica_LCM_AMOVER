package com.example.aplicacaodecontrolofabrica.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FactoryPrimary,
    onPrimary = FactoryOnPrimary,
    primaryContainer = Color(0xFF1A3A6B),
    onPrimaryContainer = Color(0xFFD4E3FF),

    secondary = FactorySecondary,
    onSecondary = FactoryOnSecondary,
    secondaryContainer = Color(0xFF0D3B2E),
    onSecondaryContainer = Color(0xFFC8F7E8),

    tertiary = FactoryInfo,
    onTertiary = FactoryOnPrimary,
    tertiaryContainer = Color(0xFF1A3050),
    onTertiaryContainer = Color(0xFFD0E4FF),

    background = FactoryDark,
    onBackground = FactoryTextPrimary,

    surface = FactorySurface,
    onSurface = FactoryTextPrimary,

    surfaceVariant = FactorySurfaceAlt,
    onSurfaceVariant = FactoryTextSecondary,

    outline = FactoryTextDisabled,
    outlineVariant = Color(0xFF2A2F3A),

    error = FactoryAlert,
    onError = FactoryOnPrimary,
    errorContainer = Color(0xFF3B1111),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = FactoryPrimary,
    onPrimary = FactoryOnPrimary,
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF0A1D3A),

    secondary = Color(0xFF1DAF8A),
    onSecondary = FactoryOnPrimary,
    secondaryContainer = Color(0xFFC8F7E8),
    onSecondaryContainer = Color(0xFF062E22),

    tertiary = FactoryInfo,
    onTertiary = FactoryOnPrimary,
    tertiaryContainer = Color(0xFFD0E4FF),
    onTertiaryContainer = Color(0xFF0A1D3A),

    background = FactoryLightBg,
    onBackground = Color(0xFF111318),

    surface = FactoryLightSurface,
    onSurface = Color(0xFF111318),

    surfaceVariant = FactoryLightSurfaceAlt,
    onSurfaceVariant = Color(0xFF44474E),

    outline = Color(0xFFB0B4BE),
    outlineVariant = Color(0xFFDFE1E8),

    error = FactoryAlert,
    onError = FactoryOnPrimary,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun AplicacaoDeControloFabricaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

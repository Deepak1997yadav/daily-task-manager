package com.dailytaskmanager.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppDarkColorScheme = darkColorScheme(
    primary = Teal,
    onPrimary = DarkBackground,
    primaryContainer = TealDark.copy(alpha = 0.2f),
    onPrimaryContainer = Teal,
    secondary = Amber,
    onSecondary = DarkBackground,
    secondaryContainer = AmberDark.copy(alpha = 0.2f),
    onSecondaryContainer = Amber,
    tertiary = Rose,
    onTertiary = DarkBackground,
    tertiaryContainer = RoseDark.copy(alpha = 0.2f),
    onTertiaryContainer = Rose,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = DarkSurfaceHigh,
    error = PriorityUrgent,
    onError = DarkBackground
)

@Composable
fun DailytaskmanagerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppDarkColorScheme,
        typography = Typography,
        content = content
    )
}
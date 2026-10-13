package fr.univsmb.fridgemate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FridgeColors = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = SoftGreen,
    onPrimaryContainer = DeepGreen,
    secondary = Amber,
    background = OffWhite,
    onBackground = DeepGreen,
    surface = Color.White,
    onSurface = DeepGreen,
    surfaceVariant = SoftGreen,
    onSurfaceVariant = Color(0xFF75877C)
)

@Composable
fun FridgeMateTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(colorScheme = FridgeColors, typography = Typography, content = content)
}

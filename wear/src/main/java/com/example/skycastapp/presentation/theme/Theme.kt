package com.example.skycastapp.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun SkyCastAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = wearColorPalette,
        typography = Typography,
        // For shapes, we generally recommend using the default Material
        // shapes which are -> Round -> Small, Medium, Large
        shapes = Shapes,
        content = content
    )
}

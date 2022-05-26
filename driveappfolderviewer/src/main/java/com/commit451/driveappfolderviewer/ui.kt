package com.commit451.driveappfolderviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xff03dac6)

private val DarkColors by lazy {
    darkColors(
        primary = PrimaryColor,
        secondary = PrimaryColor,
    )
}
private val LightColors by lazy {
    lightColors(
        primary = PrimaryColor,
        secondary = PrimaryColor,
        background = Color(0xffeeeeee),
    )
}

@Composable
fun Theme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colors = colors,
        content = content
    )
}
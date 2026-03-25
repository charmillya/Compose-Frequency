package com.charmillya.frequency.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val SkyPurple = Color(0xffe6b0f5)
val LightPurple = Color(0xffe78fff)
val DarkPurple = Color(0xff4f1e5c)
val LightBlue = Color(0xff9c8fff)
val SoftLightBlue = LightBlue.copy(alpha = 0.7f)
val SoftLightPurple = LightPurple.copy(alpha = 0.7f)
val DarkGray = Color(0xff212121)
val LightWhite = Color(0xfff2f2f2)



val gradientBrush = Brush.horizontalGradient(
    listOf(SoftLightBlue, SoftLightPurple)
)
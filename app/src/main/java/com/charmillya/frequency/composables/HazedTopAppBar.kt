package com.charmillya.frequency.composables

import androidx.compose.animation.core.EaseIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmillya.frequency.ui.theme.gradientBrush
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazedTopAppBar(
    text: String,
    hazeState: HazeState,
    isSubScreen: Boolean = false,
    onBackClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    TopAppBar(
        navigationIcon = {
            if (isSubScreen) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour"
                    )
                }
            }
        },
        title = {
            Box(
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = text,
                    letterSpacing = 1.6.sp,
                    style = TextStyle(
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        brush = gradientBrush, 
                        drawStyle = Stroke(
                            width = 4f, 
                            miter = 1f
                        )
                    )
                )

                Text(
                    text = text,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                )
            }

        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
            titleContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            actionIconContentColor = Color.White
        ),
        modifier = androidx.compose.ui.Modifier
            .height(120.dp)
            .hazeEffect(state = hazeState) {
                progressive = HazeProgressive.verticalGradient(
                    easing = EaseIn,
                    startIntensity = 0.8f,
                    endIntensity = 0f,
                    preferPerformance = true
                )
            }
    )
}

@Preview
@Composable
private fun HazedTopAppBarPreview() {
    HazedTopAppBar("Mes liens", rememberHazeState())
}

package com.charmillya.frequency.composables

import android.R.attr.fontWeight
import android.R.attr.text
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.EaseOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmillya.frequency.ui.theme.gradientBrush
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazedTopAppBar(
    text: String,
    hazeState: HazeState,
    modifier: Modifier = Modifier,

    ) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    TopAppBar(
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
        colors = TopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
            titleContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            actionIconContentColor = Color.White
        ),
        modifier = Modifier
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

package com.charmillya.frequency.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmillya.frequency.R
import com.charmillya.frequency.composables.SimpleLoginCard
import com.charmillya.frequency.ui.theme.gradientBrush
import kotlinx.coroutines.isActive
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewLogin(
    onLoginSuccess: (Boolean) -> Unit,
) {
    val currentTime = remember { LocalTime.now() }
    val greetingRes = getGreetingRes(currentTime)

    Box(modifier = Modifier.fillMaxSize()) {
        
        CosmosBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box {
                    
                    Text(
                        text = stringResource(greetingRes),
                        letterSpacing = 1.4.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            brush = gradientBrush,
                            drawStyle = Stroke(width = 4f, miter = 1f)
                        )
                    )

                    
                    Text(
                        text = stringResource(greetingRes),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            letterSpacing = 1.sp,
                        )
                    )
                }

                Spacer(modifier = Modifier.size(5.dp))

                Text(
                    color = Color.White.copy(alpha = 0.85f),
                    text = stringResource(R.string.login_subtitle)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(
                    topStart = 32.dp,
                    topEnd = 32.dp
                ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp, bottom = 50.dp)
                ) {
                    SimpleLoginCard(onLoginSuccess)
                }
            }
        }
    }
}

@Composable
fun CosmosBackground() {
    
    val stars = remember {
        List(150) {
            
            val tintColor = when {
                Random.nextFloat() < 0.15f -> Color(0xFF81D4FA) 
                Random.nextFloat() < 0.15f -> Color(0xFFF48FB1) 
                else -> Color.White
            }

            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1f,
                baseAlpha = Random.nextFloat() * 0.7f + 0.3f,
                color = tintColor
            )
        }
    }

    
    val meteors = remember { mutableStateListOf<Meteor>() }

    
    var time by remember { mutableFloatStateOf(0f) }

    
    LaunchedEffect(Unit) {
        while (isActive) {
            
            time += 0.05f

            
            if (Random.nextFloat() < 0.02f) { 
                meteors.add(Meteor())
            }

            
            val iterator = meteors.listIterator()
            while (iterator.hasNext()) {
                val meteor = iterator.next()
                
                meteor.progress += 0.005f
                if (meteor.progress >= 1f) {
                    iterator.remove()
                }
            }

            withFrameNanos { } 
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        
        drawRect(color = Color(0xFF02040A))

        
        drawNebulaBackground(center, isDark = true)

        
        stars.forEach { star ->
            
            val twinkle = sin(time * star.twinkleSpeed + star.twinklePhase) * 0.2f
            val currentAlpha = (star.baseAlpha + twinkle).coerceIn(0.1f, 1f)

            drawCircle(
                color = star.color.copy(alpha = currentAlpha),
                radius = star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }

        
        rotate(degrees = 45f, pivot = center) {
            meteors.forEach { meteor ->
                val startX = (meteor.startX * size.width * 2) - size.width
                val startY = -size.height * 0.5f
                val currentY = startY + (size.height * 2 * meteor.progress)
                val tailLength = 300f * meteor.speedScale

                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White),
                        startY = currentY - tailLength,
                        endY = currentY
                    ),
                    start = Offset(startX, currentY - tailLength),
                    end = Offset(startX, currentY),
                    strokeWidth = 4f * meteor.thickness,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}


fun DrawScope.drawNebulaBackground(centre: Offset, isDark: Boolean) {
    val maxDimension = size.width.coerceAtLeast(size.height) * 1.6f
    val currentRadius = maxDimension

    
    val (baseCol, cloud1, cloud2, accent) = if (isDark) {
        listOf(
            Color(0xFF02040A),
            Color(0xFF0A1128),
            Color(0xFF1C2541),
            Color(0xFF4A148C).copy(alpha = 0.3f)
        )
    } else {
        listOf(
            Color(0xFFF9FAFF),
            Color(0xFFE3F2FD),
            Color(0xFFF3E5F5),
            Color(0xFFF8BBD0).copy(alpha = 0.3f)
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            0.0f to baseCol.copy(alpha = 0.95f),
            0.6f to cloud1.copy(alpha = 0.8f),
            1.0f to baseCol,
            center = centre, radius = currentRadius
        ),
        radius = currentRadius, center = centre
    )

    drawCircle(
        brush = Brush.radialGradient(
            0.0f to baseCol.copy(alpha = 0.8f),
            0.4f to cloud2.copy(alpha = 0.3f),
            1.0f to Color.Transparent,
            center = centre, radius = currentRadius * 0.9f
        ),
        radius = currentRadius * 0.9f, center = centre
    )

    drawCircle(
        brush = Brush.radialGradient(
            0.4f to accent.copy(alpha = 0.15f),
            0.8f to Color.Transparent,
            center = centre, radius = currentRadius * 0.7f
        ),
        radius = currentRadius * 0.7f, center = centre
    )
}


private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val baseAlpha: Float,
    val color: Color,
    val twinklePhase: Float = Random.nextFloat() * 2 * PI.toFloat(),
    val twinkleSpeed: Float = Random.nextFloat() * 3f + 1f
)

private class Meteor {
    val startX: Float = Random.nextFloat()
    var progress: Float = 0f
    val speedScale: Float = Random.nextFloat() * 0.5f + 0.8f
    val thickness: Float = Random.nextFloat() * 0.5f + 0.5f
}

@RequiresApi(Build.VERSION_CODES.O)
fun getGreetingRes(currentTime: LocalTime): Int {
    val eveningStartHour = 18
    val morningStartHour = 6

    val isDayTime = currentTime.isAfter(LocalTime.of(morningStartHour - 1, 59)) &&
            currentTime.isBefore(LocalTime.of(eveningStartHour, 0))

    return if (isDayTime) R.string.login_greeting_day else R.string.login_greeting_night
}
package com.charmillya.frequency.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmillya.frequency.ui.theme.gradientBrush
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import sv.lib.squircleshape.SquircleShape

@Composable
fun ActionSquircleButton(
    text: String,
    hazeState: HazeState,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(80.dp)
            .width(160.dp)
            .border(3.dp, gradientBrush, SquircleShape(40.dp))
            .clip(SquircleShape(40.dp))
            .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(4.dp)
        )
    }
}
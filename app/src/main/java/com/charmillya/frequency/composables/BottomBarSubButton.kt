package com.charmillya.frequency.composables

import android.R.attr.onClick
import android.R.attr.text
import android.R.attr.type
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.charmillya.frequency.ui.theme.gradientBrush
import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import sv.lib.squircleshape.SquircleShape

@Composable
fun BottomBarSubButton(
    width: Dp,
    height: Dp,
    onClick: () -> Unit,
    hazeState: HazeState,
    type: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
                .width(width)
                .height(height)
                .border(
                    2.dp,
                    gradientBrush,
                    SquircleShape(20.dp)
                )
                .clip(SquircleShape(20.dp))
        ) {
            Button(
                shape = SquircleShape(2.dp),
                onClick = { onClick() },
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.ultraThin()
                    )
            ) {
                if(type == "icon") {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                        )
                    }
                } else if (type == "text") {
                    if (text != null) {
                        Text(
                            text = text
                        )
                    }
                }
            }
        }
    }
}
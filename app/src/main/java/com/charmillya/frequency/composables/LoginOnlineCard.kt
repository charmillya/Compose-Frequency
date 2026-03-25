package com.charmillya.frequency.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmillya.frequency.R
import com.charmillya.frequency.ui.theme.SoftLightBlue
import com.charmillya.frequency.ui.theme.SoftLightPurple
import sv.lib.squircleshape.SquircleShape

@Composable
fun LoginOnlineCard(
    onLoginSuccess: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
        shape = SquircleShape(20.dp),
        border = BorderStroke(2.dp, Color.White),
        shadowElevation = 4.dp,
        modifier = modifier
            .height(85.dp)
            .width(320.dp)
            .clickable { onLoginSuccess(false) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(SoftLightBlue, SoftLightPurple)))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.login_with),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Icon(
                    painter = painterResource(R.drawable.google_favicon),
                    contentDescription = stringResource(R.string.google_logo_desc),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}
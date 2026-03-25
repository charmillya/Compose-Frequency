package com.charmillya.frequency.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmillya.frequency.R
import com.charmillya.frequency.ui.theme.LightWhite
import sv.lib.squircleshape.SquircleShape

@Composable
fun LoginOfflineCard(
    onLoginSuccess: (isGuest: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSystemInDarkTheme()) Color.DarkGray else LightWhite,
        shape = SquircleShape(20.dp),
        border = BorderStroke(1.dp, Color.White),
        shadowElevation = 4.dp,
        modifier = modifier
            .width(320.dp)
            .clickable { onLoginSuccess(true) }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.login_as_guest),
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
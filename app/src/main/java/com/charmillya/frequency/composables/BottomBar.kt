package com.charmillya.frequency.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.charmillya.frequency.R
import com.charmillya.frequency.ui.theme.DarkPreview
import com.charmillya.frequency.ui.theme.FrequencyTheme
import compose.icons.FeatherIcons
import compose.icons.TablerIcons
import compose.icons.feathericons.Aperture
import compose.icons.tablericons.Aperture
import compose.icons.tablericons.Planet
import compose.icons.tablericons.Settings
import compose.icons.tablericons.User
import compose.icons.tablericons.Users

@Composable
fun BottomBar(
    galaxieIconFocus: Boolean,
    liensIconFocus: Boolean,
    settingsIconFocus: Boolean,
    onGalaxieClick: () -> Unit,
    onLiensClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier
) {
    val containerColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = modifier
            .height(90.dp)
    ) {
        val animatedFontSizeGalaxie by animateFloatAsState(
            targetValue = if (galaxieIconFocus) 15f else 11f,
            label = "fontSizeAnimationGalaxie"
        )
        val animatedIconSizeGalaxie by animateFloatAsState(
            targetValue = if (galaxieIconFocus) 30f else 20f,
            label = "fontSizeAnimationGalaxie"
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = TablerIcons.Planet,
                    contentDescription = null,
                    modifier = Modifier.size(animatedIconSizeGalaxie.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_bar_1),
                    fontWeight = if (galaxieIconFocus) FontWeight.Bold else FontWeight.Normal,
                    style = TextStyle(fontSize = animatedFontSizeGalaxie.sp)
                )
            },
            selected = galaxieIconFocus,
            onClick = { onGalaxieClick() }
        )

        val animatedFontSizeLiens by animateFloatAsState(
            targetValue = if (liensIconFocus) 15f else 11f,
            label = "fontSizeAnimationLiens"
        )
        val animatedIconSizeLiens by animateFloatAsState(
            targetValue = if (liensIconFocus) 30f else 20f,
            label = "fontSizeAnimationLiens"
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = TablerIcons.Users,
                    contentDescription = null,
                    modifier = Modifier.size(animatedIconSizeLiens.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_bar_2),
                    fontWeight = if (liensIconFocus) FontWeight.Bold else FontWeight.Normal,
                    style = TextStyle(fontSize = animatedFontSizeLiens.sp)
                )
            },
            selected = liensIconFocus,
            onClick = { onLiensClick() }
        )

        val animatedFontSizeProfil by animateFloatAsState(
            targetValue = if (settingsIconFocus) 15f else 11f,
            label = "fontSizeAnimationProfil"
        )
        val animatedIconSizeProfil by animateFloatAsState(
            targetValue = if (settingsIconFocus) 30f else 20f,
            label = "fontSizeAnimationProfil"
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = TablerIcons.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(animatedIconSizeProfil.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_bar_3),
                    fontWeight = if (settingsIconFocus) FontWeight.Bold else FontWeight.Normal,
                    style = TextStyle(fontSize = animatedFontSizeProfil.sp)
                )
            },
            selected = settingsIconFocus,
            onClick = { onSettingsClick() }
        )
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    FrequencyTheme() {
        BottomBar(true, false, false, {}, {}, {}, Modifier)
    }
}

@DarkPreview
@Composable
private fun BottomBarDarkPreview() {
    FrequencyTheme() {
        BottomBar(true, false, false, {}, {}, {}, Modifier)
    }
}
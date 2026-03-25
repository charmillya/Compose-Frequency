package com.charmillya.frequency.views

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charmillya.frequency.LocalHazeState
import com.charmillya.frequency.R
import com.charmillya.frequency.composables.HazedScaffoldLazyColumn
import com.charmillya.frequency.composables.LoginOnlineCard
import com.charmillya.frequency.ui.theme.gradientBrush
import com.charmillya.frequency.viewmodels.ViewModelSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSettings(
    modifier: Modifier = Modifier,
    viewModel: ViewModelSettings = viewModel(),
    onGoogleLogin: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val hazeState = LocalHazeState.current
    val uriHandler = LocalUriHandler.current

    var langExpanded by remember { mutableStateOf(false) }
    var orbitExpanded by remember { mutableStateOf(false) }
    var speedExpanded by remember { mutableStateOf(false) }

    
    var showLogoutDialog by remember { mutableStateOf(false) }

    val currentOrbitStyle by viewModel.orbitStyle.collectAsState()
    val currentSpeed by viewModel.rotationSpeed.collectAsState()

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_title)) }, 
            text = { Text(stringResource(R.string.logout_confirmation_message)) }, 
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.viewModelScope.launch { viewModel.logout() }
                        onLogout()
                    }
                ) {
                    Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    
    val currentLangCode = remember {
        AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: Locale.getDefault().language
    }

    val transparentBorderColors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        errorBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent
    )

    HazedScaffoldLazyColumn(
        title = stringResource(R.string.settings_title),
        hazeState = hazeState
    ) {
        item {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {

                StaggeredSlideItem(index = 0) {
                    Text(stringResource(R.string.language_section), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
                    ExposedDropdownMenuBox(expanded = langExpanded, onExpandedChange = { langExpanded = !langExpanded }) {
                        OutlinedTextField(
                            value = when(currentLangCode) {
                                "en" -> stringResource(R.string.lang_en)
                                "es" -> stringResource(R.string.lang_es)
                                else -> stringResource(R.string.lang_fr)
                            },
                            onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(langExpanded) },
                            colors = transparentBorderColors,
                            modifier = Modifier.menuAnchor().fillMaxWidth().border(width = if (langExpanded) 2.5.dp else 1.dp, brush = if (langExpanded) gradientBrush else SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), shape = RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = langExpanded, onDismissRequest = { langExpanded = false }) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.lang_fr)) }, onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fr")); langExpanded = false })
                            DropdownMenuItem(text = { Text(stringResource(R.string.lang_en)) }, onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en")); langExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                StaggeredSlideItem(index = 2) {
                    Text(stringResource(R.string.appearance_section), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
                    ExposedDropdownMenuBox(expanded = orbitExpanded, onExpandedChange = { orbitExpanded = !orbitExpanded }) {
                        OutlinedTextField(
                            value = when(currentOrbitStyle) {
                                "visible" -> stringResource(R.string.orbit_style_visible)
                                "hidden" -> stringResource(R.string.orbit_style_hidden)
                                else -> stringResource(R.string.orbit_style_dotted)
                            },
                            onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(orbitExpanded) },
                            colors = transparentBorderColors,
                            modifier = Modifier.menuAnchor().fillMaxWidth().border(width = if (orbitExpanded) 2.5.dp else 1.dp, brush = if (orbitExpanded) gradientBrush else SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), shape = RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = orbitExpanded, onDismissRequest = { orbitExpanded = false }) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.orbit_style_dotted)) }, onClick = { viewModel.saveOrbitStyle("dotted"); orbitExpanded = false })
                            DropdownMenuItem(text = { Text(stringResource(R.string.orbit_style_visible)) }, onClick = { viewModel.saveOrbitStyle("visible"); orbitExpanded = false })
                            DropdownMenuItem(text = { Text(stringResource(R.string.orbit_style_hidden)) }, onClick = { viewModel.saveOrbitStyle("hidden"); orbitExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                StaggeredSlideItem(index = 3) {
                    Text(stringResource(R.string.rotation_speed_label), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
                    ExposedDropdownMenuBox(expanded = speedExpanded, onExpandedChange = { speedExpanded = !speedExpanded }) {
                        OutlinedTextField(
                            value = when(currentSpeed) {
                                0f -> stringResource(R.string.speed_static)
                                0.5f -> stringResource(R.string.speed_slow)
                                2f -> stringResource(R.string.speed_fast)
                                else -> stringResource(R.string.speed_normal)
                            },
                            onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(speedExpanded) },
                            colors = transparentBorderColors,
                            modifier = Modifier.menuAnchor().fillMaxWidth().border(width = if (speedExpanded) 2.5.dp else 1.dp, brush = if (speedExpanded) gradientBrush else SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), shape = RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = speedExpanded, onDismissRequest = { speedExpanded = false }) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.speed_static)) }, onClick = { viewModel.saveRotationSpeed(0f); speedExpanded = false })
                            DropdownMenuItem(text = { Text(stringResource(R.string.speed_slow)) }, onClick = { viewModel.saveRotationSpeed(0.5f); speedExpanded = false })
                            DropdownMenuItem(text = { Text(stringResource(R.string.speed_normal)) }, onClick = { viewModel.saveRotationSpeed(1f); speedExpanded = false })
                            DropdownMenuItem(text = { Text(stringResource(R.string.speed_fast)) }, onClick = { viewModel.saveRotationSpeed(2f); speedExpanded = false })
                        }
                    }
                }

                StaggeredSlideItem(index = 4) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 32.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                    if (viewModel.isGuest == true) {
                        LoginOnlineCard({ onGoogleLogin(false) })
                    } else {
                        Button(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.logout_button), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                StaggeredSlideItem(index = 5) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=com.charmillya.frequency") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.leave_review), fontWeight = FontWeight.Bold)
                    }
                }

                StaggeredSlideItem(index = 6) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 50.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(stringResource(R.string.made_with), fontWeight = FontWeight.Light, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}




@Composable
private fun StaggeredSlideItem(
    index: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * 60L)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = animatable.value
                translationX = (1f - animatable.value) * -100f
            },
        content = content
    )
}

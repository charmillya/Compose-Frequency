package com.charmillya.frequency.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charmillya.frequency.R
import com.charmillya.frequency.composables.*
import com.charmillya.frequency.helpers.ContactSelectionContract
import com.charmillya.frequency.ui.theme.FrequencyTheme
import com.charmillya.frequency.ui.theme.gradientBrush
import com.charmillya.frequency.viewmodels.ViewModelLiens
import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Users
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewLiens(
    onAjouterManuellementClick: () -> Unit = {},
    onImporterContactsClick: (List<Uri>) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ViewModelLiens = viewModel()
) {
    val hazeState = rememberHazeState()
    val listeLiens by viewModel.listeLiens.collectAsState()
    val selectedLienIds by viewModel.selectedLienIds.collectAsState()
    val isSelectionMode = selectedLienIds.isNotEmpty()
    val selectionPulseTokens = remember { mutableStateMapOf<String, Int>() }

    val toggleSelectionWithPulse: (String) -> Unit = { lienId ->
        viewModel.toggleSelection(lienId)
        selectionPulseTokens[lienId] = (selectionPulseTokens[lienId] ?: 0) + 1
    }

    var isInitialComposition by remember { mutableStateOf(true) }
    var menuEtendu by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ContactSelectionContract()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            onImporterContactsClick(uris)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        contactPickerLauncher.launch(null)
    }

    LaunchedEffect(Unit) {
        delay(800)
        isInitialComposition = false
    }

    Box(modifier = modifier.fillMaxSize()) {

        HazedScaffoldLazyColumn(
            title = stringResource(R.string.bonds_list_title),
            hazeState = hazeState,
            isSubScreen = false
        ) {
            if (listeLiens.isEmpty()) {
                item { EmptyStateContent() }
            } else {
                itemsIndexed(
                    items = listeLiens,
                    key = { _, lien -> lien.idLien }
                ) { index, lien ->
                    val delayMillis = if (isInitialComposition) (index * 70L).coerceAtMost(500L) else 0L
                    val animatedScale = remember(lien.idLien) { Animatable(0.85f) }
                    val animatedAlpha = remember(lien.idLien) { Animatable(0f) }

                    LaunchedEffect(lien.idLien) {
                        if (delayMillis > 0) delay(delayMillis)
                        launch { animatedAlpha.animateTo(1f, animationSpec = tween(300)) }
                        animatedScale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }

                    LienCard(
                        lien = lien,
                        onSupprimerLien = { viewModel.supprimerLien(lien) },
                        isSelectionMode = isSelectionMode,
                        isSelected = lien.idLien in selectedLienIds,
                        selectionPulseToken = selectionPulseTokens[lien.idLien] ?: 0,
                        onClick = { toggleSelectionWithPulse(lien.idLien) },
                        onLongPress = {
                            if (!isSelectionMode) {
                                toggleSelectionWithPulse(lien.idLien)
                            }
                        },
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = null,
                                placementSpec = tween(400),
                                fadeOutSpec = tween(400)
                            )
                            .graphicsLayer {
                                scaleX = animatedScale.value
                                scaleY = animatedScale.value
                                alpha = animatedAlpha.value
                            }
                    )
                }
            }
        }

        if (menuEtendu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { menuEtendu = false }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = isSelectionMode,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                label = "delete_button_visibility"
            ) {
                BottomBarSubButton(
                    width = 210.dp,
                    height = 60.dp,
                    onClick = { showBulkDeleteDialog = true },
                    hazeState = hazeState,
                    type = "text",
                    text = stringResource(
                        R.string.delete_selected_count,
                        selectedLienIds.size
                    )
                )
            }

            AnimatedVisibility(
                visible = isSelectionMode,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 22.dp, bottom = 126.dp),
                label = "clear_button_visibility"
            ) {
                FilledIconButton(
                    onClick = { viewModel.clearSelection() },
                    modifier = Modifier.size(46.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ClearAll,
                        contentDescription = stringResource(R.string.clear_selection)
                    )
                }
            }

            AnimatedVisibility(
                visible = !isSelectionMode,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                label = "menu_button_visibility"
            ) {
                AnimatedContent(
                    targetState = menuEtendu,
                    transitionSpec = {
                        val isOpening = targetState

                        val openSlideSpec = spring<IntOffset>(
                            dampingRatio = 0.82f,
                            stiffness = Spring.StiffnessLow,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                        val closeSlideSpec = spring<IntOffset>(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )

                        val slideInSpec = if (isOpening) openSlideSpec else closeSlideSpec
                        val slideOutSpec = if (isOpening) closeSlideSpec else openSlideSpec
                        val fadeInSpec = tween<Float>(durationMillis = if (isOpening) 260 else 140)
                        val fadeOutSpec = tween<Float>(durationMillis = if (isOpening) 180 else 110)
                        val sizeSpec = tween<IntSize>(
                            durationMillis = if (isOpening) 320 else 180,
                            easing = FastOutSlowInEasing
                        )

                        if (isOpening) {
                            (slideInHorizontally(animationSpec = slideInSpec) { -it } + fadeIn(animationSpec = fadeInSpec)) togetherWith
                                    (slideOutHorizontally(animationSpec = slideOutSpec) { it } + fadeOut(animationSpec = fadeOutSpec))
                        } else {
                            (slideInHorizontally(animationSpec = slideInSpec) { it } + fadeIn(animationSpec = fadeInSpec)) togetherWith
                                    (slideOutHorizontally(animationSpec = slideOutSpec) { -it } + fadeOut(animationSpec = fadeOutSpec))
                        } using SizeTransform(clip = false) { _, _ -> sizeSpec }

                    },
                    contentAlignment = Alignment.BottomCenter,
                    label = "menu_animation"
                ) { estOuvert ->
                    if (!estOuvert) {
                        BottomBarSubButton(
                            width = 80.dp,
                            height = 80.dp,
                            onClick = { menuEtendu = true },
                            hazeState = hazeState,
                            type = "icon",
                            icon = TablerIcons.Plus
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActionSquircleButton(
                                text = stringResource(R.string.import_header_text),
                                hazeState = hazeState,
                                onClick = {
                                    menuEtendu = false
                                    permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                                }
                            )

                            ActionSquircleButton(
                                text = stringResource(R.string.add_manually),
                                hazeState = hazeState,
                                onClick = {
                                    menuEtendu = false
                                    onAjouterManuellementClick()
                                }
                            )
                        }
                }
            }
        }
        }

        if (showBulkDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showBulkDeleteDialog = false },
                title = { Text(stringResource(R.string.delete_selected_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.delete_selected_message,
                            selectedLienIds.size
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.supprimerLiensSelectionnes()
                            showBulkDeleteDialog = false
                            menuEtendu = false
                        }
                    ) {
                        Text(stringResource(R.string.delete_selected_action), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBulkDeleteDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun LazyItemScope.EmptyStateContent() {
    Column(
        modifier = Modifier
            .fillParentMaxHeight(0.8f)
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = TablerIcons.Users,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_bonds_primary),
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = stringResource(R.string.empty_bonds_secondary),
            fontSize = 14.sp,
            color = Color.Gray.copy(alpha = 0.7f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 24.dp)
                .size(32.dp),
            tint = Color.Gray.copy(alpha = 0.4f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun ViewLiensPreview() {
    FrequencyTheme {
        ViewLiens()
    }
}
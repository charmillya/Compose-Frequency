package com.charmillya.frequency.views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.charmillya.frequency.R
import com.charmillya.frequency.composables.BottomBarSubButton
import com.charmillya.frequency.composables.DatePickerModal
import com.charmillya.frequency.composables.HazedScaffoldLazyColumn
import com.charmillya.frequency.helpers.ContactSelectionContract
import com.charmillya.frequency.ui.theme.gradientBrush
import com.charmillya.frequency.viewmodels.ContactImport
import com.charmillya.frequency.viewmodels.ContactValide
import com.charmillya.frequency.viewmodels.ViewModelImporterLiens
import compose.icons.TablerIcons
import compose.icons.tablericons.Trash
import compose.icons.tablericons.User
import compose.icons.tablericons.Users
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

@Composable
fun ViewImporterLiens(
    urisContacts: List<Uri>,
    onBack: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ViewModelImporterLiens = viewModel()
) {
    val hazeState = rememberHazeState()
    val context = LocalContext.current

    var currentIndex by remember { mutableStateOf(0) }
    var editingContact by remember { mutableStateOf<ContactValide?>(null) }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ContactSelectionContract()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            currentIndex = 0
            viewModel.reinitialiser()
            viewModel.resoudreContacts(context, uris)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        contactPickerLauncher.launch(null)
    }

    LaunchedEffect(urisContacts) {
        viewModel.resoudreContacts(context, urisContacts)
    }

    Box(modifier = modifier.fillMaxSize()) {

        HazedScaffoldLazyColumn(
            title = stringResource(R.string.import_header_text),
            hazeState = hazeState,
            isSubScreen = true,
            navController = navController
        ) {
            if (viewModel.isLoading) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (viewModel.contactsValides.isEmpty()) {
                item {
                    EmptyStateImport()
                }
            } else {
                itemsIndexed(
                    items = viewModel.contactsValides,
                    key = { _, valide -> valide.contact.uri.toString() }
                ) { _, valide ->
                    val animatedScale = remember { Animatable(0.5f) }
                    val animatedAlpha = remember { Animatable(0f) }

                    LaunchedEffect(Unit) {
                        launch { animatedAlpha.animateTo(1f, animationSpec = tween(300)) }
                        animatedScale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
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
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { editingContact = valide }
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (valide.contact.photoBitmap != null) {
                                Image(bitmap = valide.contact.photoBitmap, contentDescription = null, contentScale = ContentScale.Crop)
                            } else {
                                Icon(TablerIcons.User, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = valide.contact.nom, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = stringResource(
                                    R.string.last_interaction_at,
                                    convertMillisToDate(valide.derniereInteraction)
                                ),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(180.dp)) }
            }
        }

        if (!viewModel.isLoading && currentIndex < viewModel.contactsEnAttente.size) {
            val currentContact = viewModel.contactsEnAttente[currentIndex]

            key(currentContact.uri) {
                PopupConfigurationContactAnimated(
                    contact = currentContact,
                    isVisible = true,
                    onValider = { derniereInterac, dateRencontre ->
                        viewModel.ajouterContactValide(ContactValide(currentContact, derniereInterac, dateRencontre))
                        currentIndex++
                    },
                    onIgnorer = {
                        currentIndex++
                    },
                    onDelete = null, 
                    showDismiss = false
                )
            }
        }

        if (editingContact != null) {
            PopupConfigurationContactAnimated(
                contact = editingContact!!.contact,
                isVisible = true,
                initialDateInteraction = editingContact!!.derniereInteraction,
                initialDateRencontre = editingContact!!.dateRencontre,
                onValider = { derniereInterac, dateRencontre ->
                    viewModel.mettreAJourContactValide(ContactValide(editingContact!!.contact, derniereInterac, dateRencontre))
                    editingContact = null
                },
                onIgnorer = { editingContact = null },
                onDelete = {
                    viewModel.supprimerContactValide(editingContact!!.contact.uri)
                    editingContact = null
                },
                showDismiss = true
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = !viewModel.isLoading && viewModel.contactsValides.isEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                BottomBarSubButton(
                    width = 195.dp,
                    height = 60.dp,
                    onClick = {
                        permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                    },
                    hazeState = hazeState,
                    type = "text",
                    text = stringResource(R.string.import_header_text)
                )
            }

            AnimatedVisibility(
                visible = !viewModel.isLoading && currentIndex >= viewModel.contactsEnAttente.size && viewModel.contactsValides.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                BottomBarSubButton(
                    width = 180.dp,
                    height = 60.dp,
                    onClick = {
                        viewModel.sauvegarderImports(context) {
                            onBack()
                        }
                    },
                    hazeState = hazeState,
                    type = "text",
                    text = stringResource(R.string.import_validation)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupConfigurationContactAnimated(
    contact: ContactImport,
    isVisible: Boolean,
    initialDateInteraction: Long? = null,
    initialDateRencontre: Long? = null,
    onValider: (Long, Long?) -> Unit,
    onIgnorer: () -> Unit,
    onDelete: (() -> Unit)? = null,
    showDismiss: Boolean = false
) {
    var dateInteraction by remember(contact.uri) { mutableStateOf<Long?>(initialDateInteraction) }
    var dateRencontre by remember(contact.uri) { mutableStateOf<Long?>(initialDateRencontre) }

    var showInteractionModal by remember { mutableStateOf(false) }
    var showMeetModal by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    if (showInteractionModal) {
        DatePickerModal(
            onDateSelected = {
                dateInteraction = it
                showInteractionModal = false
            },
            onDismiss = { showInteractionModal = false }
        )
    }

    if (showMeetModal) {
        DatePickerModal(
            onDateSelected = {
                dateRencontre = it
                showMeetModal = false
            },
            onDismiss = { showMeetModal = false }
        )
    }

    Dialog(
        onDismissRequest = { if (showDismiss) onIgnorer() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(spring(dampingRatio = 0.7f, stiffness = 400f)) + fadeIn(tween(200)),
                exit = scaleOut(tween(200)) + fadeOut(tween(200))
            ) {
                Card(
                    modifier = Modifier
                        .padding(32.dp)
                        .widthIn(max = 320.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(32.dp))
                        .border(2.dp, gradientBrush, RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {

                        DessinerEntetePhoto(nom = contact.nom, photoBitmap = contact.photoBitmap, height = 140.dp)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 140.dp)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            OutlinedTextField(
                                value = dateInteraction?.let { convertMillisToDate(it) } ?: "",
                                singleLine = true,
                                readOnly = true,
                                onValueChange = {},
                                label = { Text(stringResource(R.string.label_last_interaction), fontSize = 12.sp) },
                                trailingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                awaitFirstDown(pass = PointerEventPass.Initial)
                                                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                                if (upEvent != null) {
                                                    focusManager.clearFocus()
                                                    showInteractionModal = true
                                                }
                                            }
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = dateRencontre?.let { convertMillisToDate(it) } ?: "",
                                singleLine = true,
                                readOnly = true,
                                onValueChange = {},
                                label = { Text(stringResource(R.string.label_meet_date), fontSize = 12.sp) },
                                trailingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                awaitFirstDown(pass = PointerEventPass.Initial)
                                                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                                if (upEvent != null) {
                                                    focusManager.clearFocus()
                                                    showMeetModal = true
                                                }
                                            }
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (onDelete != null) {
                                        IconButton(onClick = onDelete) {
                                            Icon(
                                                imageVector = TablerIcons.Trash,
                                                contentDescription = "Supprimer",
                                                tint = MaterialTheme.colorScheme.error 
                                            )
                                        }
                                    }
                                    TextButton(onClick = onIgnorer) {
                                        Text(if (showDismiss) stringResource(R.string.close_button) else stringResource(R.string.ignore), color = Color.Gray)
                                    }
                                }

                                
                                Button(
                                    onClick = { dateInteraction?.let { onValider(it, dateRencontre) } },
                                    enabled = dateInteraction != null,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (dateInteraction != null) gradientBrush else androidx.compose.ui.graphics.SolidColor(Color.Gray)),
                                    colors = ButtonDefaults.buttonColors(Color.Transparent, disabledContainerColor = Color.Transparent)
                                ) {
                                    Text(if (showDismiss) stringResource(R.string.save) else stringResource(R.string.next), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.EmptyStateImport() {
    Column(
        modifier = Modifier
            .fillParentMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 120.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = TablerIcons.Users, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.no_contact), fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(text = stringResource(R.string.no_contact_subtext), fontSize = 14.sp, color = Color.Gray.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}
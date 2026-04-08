package com.charmillya.frequency.composables

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.charmillya.frequency.R
import com.charmillya.frequency.data.Utilities
import com.charmillya.frequency.models.Lien
import com.charmillya.frequency.ui.theme.gradientBrush
import com.charmillya.frequency.viewmodels.ViewModelLienCard
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LienCard(
    lien: Lien,
    onSupprimerLien: (Lien) -> Unit,
    onClick: () -> Unit = {},
    onLongPress: () -> Unit = {},
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    selectionPulseToken: Int = 0,
    modifier: Modifier = Modifier,
    viewModel: ViewModelLienCard = viewModel()
) {
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = if (isPressed) {
            // Quick shrink with no bounce while finger stays down.
            tween(durationMillis = 85)
        } else {
            // Single smooth return when the finger is released.
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "press_scale"
    )

    var nameText by rememberSaveable { mutableStateOf(lien.name) }
    var lastInteractionDay by rememberSaveable { mutableStateOf(lien.lastInteractionDay) }
    var meetDate by rememberSaveable { mutableStateOf(lien.meetDate) }
    var imageUri by rememberSaveable { mutableStateOf<String?>(lien.imagePath) }

    var showLastInteractionModal by remember { mutableStateOf(false) }
    var showMeetDateModal by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) imageUri = uri.toString() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = SquircleShape(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .clip(SquircleShape(20.dp))
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    brush = if (isSelected) gradientBrush else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent)),
                    shape = SquircleShape(20.dp)
                )
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (isSelectionMode) {
                            onClick()
                        } else {
                            showBottomSheet = true
                        }
                    },
                    onLongClick = {
                        if (!isSelectionMode) {
                            onLongPress()
                        }
                    }
                )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
            if (lien.imagePath != null) {
                AsyncImage(model = lien.imagePath, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 300f)))
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                }
            }

            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(lien.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                lastInteractionDay?.let {
                    Text(stringResource(R.string.last_interaction_at, convertToDate(it)), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected) Color.Black.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.1f))
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }
            }
        }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                Text(stringResource(R.string.edit_bond_title), fontWeight = FontWeight.Bold, fontSize = 20.sp)

                OutlinedTextField(value = nameText, onValueChange = { nameText = it }, label = { Text(stringResource(R.string.label_name)) }, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = lastInteractionDay?.let { convertToDate(it) } ?: "",
                    onValueChange = {}, readOnly = true,
                    label = { Text(stringResource(R.string.label_last_interaction)) },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    modifier = Modifier.fillMaxWidth().pointerInput(lastInteractionDay) {
                        awaitEachGesture {
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) showLastInteractionModal = true
                        }
                    }
                )

                OutlinedTextField(
                    value = meetDate?.let { convertToDate(it) } ?: "",
                    onValueChange = {}, readOnly = true,
                    label = { Text(stringResource(R.string.label_meet_date)) },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    modifier = Modifier.fillMaxWidth().pointerInput(meetDate) {
                        awaitEachGesture {
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) showMeetDateModal = true
                        }
                    }
                )

                PreviewLienImage(nameText, imageUri, onClick = { launcher.launch("image/*") }, modifier = Modifier.align(Alignment.CenterHorizontally))

                val errorName = stringResource(R.string.error_name_required)
                val errorDate = stringResource(R.string.error_date_required)

                Button(
                    onClick = {
                        if (nameText.isBlank()) { Toast.makeText(context, errorName, Toast.LENGTH_SHORT).show(); return@Button }
                        if (lastInteractionDay == null) { Toast.makeText(context, errorDate, Toast.LENGTH_SHORT).show(); return@Button }

                        val utils = Utilities()
                        val finalImagePath = if (imageUri != null && imageUri != lien.imagePath) utils.saveImageToInternalStorage(context, imageUri) else lien.imagePath
                        viewModel.updateLien(Lien(lien.idLien, nameText, finalImagePath, lastInteractionDay, meetDate, lien.interactionCount))
                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                    },
                    shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.background(gradientBrush, RoundedCornerShape(14.dp)).fillMaxWidth().padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.save_changes), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Button(onClick = { showDeleteDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red), modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.delete_bond), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showLastInteractionModal) DatePickerModal(onDateSelected = { lastInteractionDay = it; showLastInteractionModal = false }, onDismiss = { showLastInteractionModal = false })
    if (showMeetDateModal) DatePickerModal(onDateSelected = { meetDate = it; showMeetDateModal = false }, onDismiss = { showMeetDateModal = false })

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(stringResource(R.string.delete_confirm_msg, lien.name)) },
            confirmButton = { TextButton(onClick = { onSupprimerLien(lien); showDeleteDialog = false; showBottomSheet = false }) { Text(stringResource(R.string.delete_bond), color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertToDate(date: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    return Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)
}
package com.charmillya.frequency.views

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.charmillya.frequency.R
import com.charmillya.frequency.composables.BottomBarSubButton
import com.charmillya.frequency.composables.DatePickerModal
import com.charmillya.frequency.composables.HazedScaffoldLazyColumn
import com.charmillya.frequency.ui.theme.FrequencyTheme
import com.charmillya.frequency.viewmodels.ViewModelAjouterLien
import dev.chrisbanes.haze.rememberHazeState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ViewAjouterLien(
    navController: NavHostController,
    onAjouterLienClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ViewModelAjouterLien = viewModel()
) {
    val hazeState = rememberHazeState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var showLastInteractionModal by remember { mutableStateOf(false) }
    var showMeetDateModal by remember { mutableStateOf(false) }

    val errorName = stringResource(R.string.error_name_required)
    val errorDate = stringResource(R.string.error_date_required)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.imageUri = uri.toString()
        }
    }

    HazedScaffoldLazyColumn(
        stringResource(R.string.add_bond_title),
        hazeState,
        true,
        isSubScreen = true,
        navController = navController,
        focusManager = focusManager
    ) {
        item {
            OutlinedTextField(
                value = viewModel.name,
                singleLine = true,
                onValueChange = { viewModel.name = it },
                label = { Text(stringResource(R.string.label_name)) },
                keyboardOptions = KeyboardOptions(showKeyboardOnFocus = true),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = viewModel.lastInteractionDay?.let { convertMillisToDate(it) } ?: "",
                singleLine = true,
                readOnly = true,
                onValueChange = {},
                label = { Text(stringResource(R.string.label_last_interaction)) },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    focusManager.clearFocus()
                                    showLastInteractionModal = true
                                }
                            }
                        }
                    }
            )

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = viewModel.meetDate?.let { convertMillisToDate(it) } ?: "",
                singleLine = true,
                readOnly = true,
                onValueChange = {},
                label = { Text(stringResource(R.string.label_meet_date)) },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent =
                                    waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    focusManager.clearFocus()
                                    showMeetDateModal = true
                                }
                            }
                        }
                    }
            )

            Spacer(Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(stringResource(R.string.button_select_image))
                }

                if (viewModel.imageUri != null) {
                    Spacer(Modifier.height(10.dp))
                    AsyncImage(
                        model = viewModel.imageUri,
                        contentDescription = stringResource(R.string.image_preview_desc),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
            }
        }
    }

    if (showLastInteractionModal) {
        DatePickerModal(
            onDateSelected = {
                viewModel.lastInteractionDay = it
                showLastInteractionModal = false
            },
            onDismiss = { showLastInteractionModal = false }
        )
    }

    if (showMeetDateModal) {
        DatePickerModal(
            onDateSelected = {
                viewModel.meetDate = it
                showMeetDateModal = false
            },
            onDismiss = { showMeetDateModal = false }
        )
    }
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        BottomBarSubButton(
            width = 120.dp,
            height = 60.dp,
            onClick = {
                when {
                    viewModel.name.isBlank() -> {
                        Toast.makeText(context, errorName, Toast.LENGTH_SHORT).show()
                    }

                    viewModel.lastInteractionDay == null -> {
                        Toast.makeText(context, errorDate, Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        viewModel.ajouterLien(context) {
                            onAjouterLienClick()
                        }
                    }
                }
            },
            hazeState = hazeState,
            type = "text",
            text = stringResource(R.string.button_add)
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
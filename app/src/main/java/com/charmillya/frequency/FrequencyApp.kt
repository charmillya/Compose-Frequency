package com.charmillya.frequency

import android.net.Uri 
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.charmillya.frequency.composables.BottomBar
import com.charmillya.frequency.ui.theme.gradientBrush
import com.charmillya.frequency.viewmodels.ViewModelFrequencyApp
import com.charmillya.frequency.views.ViewAjouterLien
import com.charmillya.frequency.views.ViewGalaxie
import com.charmillya.frequency.views.ViewImporterLiens
import com.charmillya.frequency.views.ViewLiens
import com.charmillya.frequency.views.ViewLogin
import com.charmillya.frequency.views.ViewSettings
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import sv.lib.squircleshape.SquircleShape

val LocalHazeState = staticCompositionLocalOf { HazeState() }

enum class FrequencyNavigation() {
    Login,
    Galaxie,
    Liens,
    AjouterLien,
    ImporterLiens,
    Settings
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FrequencyApp(
    modifier: Modifier = Modifier,
    viewModel: ViewModelFrequencyApp = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hazeState = rememberHazeState()
    val isLoggedIn: Boolean? by viewModel.isLoggedIn.collectAsState()

    var selectedContactUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    if (isLoggedIn == null) return

    val startDestination = if (isLoggedIn == true) {
        FrequencyNavigation.Galaxie.name
    } else {
        FrequencyNavigation.Login.name
    }

    val shouldShowNavBar = currentRoute != FrequencyNavigation.Login.name

    var galaxieIconFocus by rememberSaveable { mutableStateOf(true) }
    var liensIconFocus by rememberSaveable { mutableStateOf(false) }
    var settingsIconFocus by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        if (currentRoute == FrequencyNavigation.Galaxie.name) {
            galaxieIconFocus = true; liensIconFocus = false; settingsIconFocus = false
        } else if (currentRoute == FrequencyNavigation.Liens.name || currentRoute == FrequencyNavigation.AjouterLien.name || currentRoute == FrequencyNavigation.ImporterLiens.name) {
            galaxieIconFocus = false; liensIconFocus = true; settingsIconFocus = false
        } else if (currentRoute == FrequencyNavigation.Settings.name) {
            galaxieIconFocus = false; liensIconFocus = false; settingsIconFocus = true
        }
    }

    val enterAnim: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if (initialState.destination.route == targetState.destination.route) {
            scaleIn(initialScale = 0.9f, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
        } else {
            fadeIn() + slideInHorizontally()
        }
    }

    val exitAnim: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if (initialState.destination.route == targetState.destination.route) {
            fadeOut(animationSpec = tween(150))
        } else {
            fadeOut() + slideOutHorizontally()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier
            ) {
                composable(
                    route = FrequencyNavigation.Login.name,
                    enterTransition = { scaleIn() + fadeIn() },
                    exitTransition = { scaleOut() + fadeOut() },
                ) {
                    ViewLogin(
                        onLoginSuccess = { isGuest ->
                            navController.navigate(FrequencyNavigation.Galaxie.name) {
                                popUpTo(FrequencyNavigation.Login.name) { inclusive = true }
                                launchSingleTop = true
                            }
                            viewModel.viewModelScope.launch { viewModel.rememberLogin(isGuest) }
                        }
                    )
                }

                composable(
                    route = FrequencyNavigation.Galaxie.name,
                    enterTransition = enterAnim,
                    exitTransition = exitAnim
                ) {
                    ViewGalaxie(
                        onAjouterLienClick = {
                            navController.navigate(FrequencyNavigation.AjouterLien.name)
                        }
                    )
                }

                composable(
                    route = FrequencyNavigation.Liens.name,
                    enterTransition = enterAnim,
                    exitTransition = exitAnim
                ) {
                    ViewLiens(
                        onAjouterManuellementClick = {
                            navController.navigate(FrequencyNavigation.AjouterLien.name)
                        },
                        onImporterContactsClick = { uris ->
                            selectedContactUris = uris
                            navController.navigate(FrequencyNavigation.ImporterLiens.name)
                        }
                    )
                }

                composable(
                    route = FrequencyNavigation.AjouterLien.name,
                    enterTransition = { fadeIn() + slideInHorizontally { it } },
                    exitTransition = { fadeOut() + slideOutHorizontally { -it } }
                ) {
                    ViewAjouterLien(
                        navController = navController,
                        onAjouterLienClick = {
                            navController.navigate(FrequencyNavigation.Liens.name) {
                                popUpTo(FrequencyNavigation.AjouterLien.name) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(
                    route = FrequencyNavigation.ImporterLiens.name,
                    enterTransition = { fadeIn() + slideInHorizontally { it } },
                    exitTransition = { fadeOut() + slideOutHorizontally { -it } }
                ) {
                    ViewImporterLiens(
                        navController = navController,
                        urisContacts = selectedContactUris,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = FrequencyNavigation.Settings.name,
                    enterTransition = enterAnim,
                    exitTransition = exitAnim
                ) {
                    ViewSettings(
                        onGoogleLogin = {
                            navController.navigate(FrequencyNavigation.Galaxie.name) {
                                popUpTo(FrequencyNavigation.Settings.name) { inclusive = true }
                                launchSingleTop = true
                            }
                            viewModel.viewModelScope.launch { viewModel.rememberLogin(false) }
                        },
                        onLogout = {
                            navController.navigate(FrequencyNavigation.Login.name) {
                                popUpTo(FrequencyNavigation.Settings.name) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = shouldShowNavBar,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
                    .padding(horizontal = 30.dp)
                    .border(3.dp, gradientBrush, SquircleShape(40.dp))
                    .clip(SquircleShape(40.dp))
                    .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin())
            ) {
                BottomBar(
                    galaxieIconFocus = galaxieIconFocus,
                    liensIconFocus = liensIconFocus,
                    settingsIconFocus = settingsIconFocus,
                    onGalaxieClick = {
                        navController.navigate(FrequencyNavigation.Galaxie.name) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    },
                    onLiensClick = {
                        navController.navigate(FrequencyNavigation.Liens.name) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    },
                    onSettingsClick = {
                        navController.navigate(FrequencyNavigation.Settings.name) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                )
            }
        }
    }
}
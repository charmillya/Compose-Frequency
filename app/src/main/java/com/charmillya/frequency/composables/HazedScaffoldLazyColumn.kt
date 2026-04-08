package com.charmillya.frequency.composables

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun HazedScaffoldLazyColumn(
    title: String,
    hazeState: HazeState,
    centerItems: Boolean = false,
    focusManager: FocusManager? = null,
    isSubScreen: Boolean,
    navController: NavHostController? = null,
    content: LazyListScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            HazedTopAppBar(
                text = title,
                hazeState = hazeState,
                isSubScreen = isSubScreen,
                onBackClick = { navController?.popBackStack() }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager?.clearFocus()
                    })
                }
        ) {
            LazyColumn(
                horizontalAlignment = if (centerItems) Alignment.CenterHorizontally else Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 0.dp, 
                    start = 18.dp,
                    end = 18.dp
                )
            ) {
                
                content()

                
                item {
                    Spacer(Modifier.height(230.dp))
                }
            }
        }
    }
}
package com.charmillya.frequency.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.charmillya.frequency.data.AppDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ViewModelSettings(application: Application) : AndroidViewModel(application) {

    private val dataStore = AppDataStore(getApplication())
    var isGuest by mutableStateOf<Boolean?>(null)

    
    val orbitStyle: StateFlow<String> = dataStore.orbitStyleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "visible")

    val rotationSpeed: StateFlow<Float> = dataStore.rotationSpeedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1f)

    init {
        viewModelScope.launch {
            isGuest = dataStore.getFromDataStore("isGuest").toBoolean()
        }
    }

    fun saveOrbitStyle(style: String) {
        viewModelScope.launch { dataStore.saveToDataStore("orbitStyle", style) }
    }

    fun saveRotationSpeed(speed: Float) {
        viewModelScope.launch { dataStore.saveToDataStore("rotationSpeed", speed.toString()) }
    }

    fun logout() {
        viewModelScope.launch { dataStore.resetDataStore() }
    }
}
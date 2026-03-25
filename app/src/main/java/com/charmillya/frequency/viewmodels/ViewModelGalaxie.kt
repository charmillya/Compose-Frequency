package com.charmillya.frequency.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.charmillya.frequency.data.AppDataStore
import com.charmillya.frequency.models.Lien
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class ViewModelGalaxie(application: Application) : AndroidViewModel(application) {
    private val dataStore = AppDataStore(getApplication())
    private val gson = Gson()

    private val _listeLiens = MutableStateFlow<List<Lien>>(emptyList())
    val listeLiens: StateFlow<List<Lien>> = _listeLiens.asStateFlow()

    val orbitStyle: StateFlow<String> = dataStore.orbitStyleFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "visible"
        )

    val rotationSpeed: StateFlow<Float> = dataStore.rotationSpeedFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1f
        )

    val layoutStyle = dataStore.layoutStyleFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "random")

    init {
        getLiens()
    }

    private fun getLiens() {
        viewModelScope.launch {
            dataStore.liensFlow.collect { jsonString ->
                val itemType: Type = object : TypeToken<List<Lien>>() {}.type
                val list: List<Lien> = gson.fromJson(jsonString, itemType) ?: emptyList()
                _listeLiens.value = list
            }
        }
    }

    fun enregistrerInteraction(idLien: String) {
        viewModelScope.launch {
            val currentList = _listeLiens.value.toMutableList()
            val index = currentList.indexOfFirst { it.idLien == idLien }
            if (index != -1) {
                val ancienLien = currentList[index]

                val nouveauCompteur = (ancienLien.interactionCount) + 1

                currentList[index] = ancienLien.copy(
                    lastInteractionDay = System.currentTimeMillis(),
                    interactionCount = nouveauCompteur
                )

                val json = gson.toJson(currentList)
                dataStore.saveToDataStore("lesLiens", json)
            }
        }
    }
}
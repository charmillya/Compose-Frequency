package com.charmillya.frequency.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.charmillya.frequency.data.AppDataStore
import com.charmillya.frequency.models.Lien
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class ViewModelLiens(application: Application) : AndroidViewModel(application) {
    private val dataStore = AppDataStore(getApplication())
    private val gson = Gson()

    
    
    private val _listeLiens = MutableStateFlow<List<Lien>>(emptyList())
    val listeLiens: StateFlow<List<Lien>> = _listeLiens.asStateFlow()

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

    fun supprimerLien(lienASupprimer: Lien) {
        viewModelScope.launch {
            val currentList = _listeLiens.value.toMutableList()
            
            currentList.removeAll { it.idLien == lienASupprimer.idLien }

            val updatedJson = gson.toJson(currentList)
            dataStore.saveToDataStore("lesLiens", updatedJson)
        }
    }
}
package com.charmillya.frequency.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.charmillya.frequency.composables.convertToDate
import com.charmillya.frequency.data.AppDataStore
import com.charmillya.frequency.models.Lien
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class ViewModelLienCard(application: Application) : AndroidViewModel(application) {
    val dataStore = AppDataStore(getApplication())
    private val gson = Gson()

    public fun updateLien(lien: Lien) {
        viewModelScope.launch {
            val jsonString = dataStore.getFromDataStore("lesLiens", "[]")
            val itemType: Type = object : TypeToken<MutableList<Lien>>() {}.type
            val listeLiens: MutableList<Lien> = gson.fromJson(jsonString, itemType) ?: mutableListOf()

            
            val index = listeLiens.indexOfFirst { it.idLien == lien.idLien }

            listeLiens[index] = lien

            
            val updatedJson = gson.toJson(listeLiens)
            dataStore.saveToDataStore("lesLiens", updatedJson)
        }
    }
}
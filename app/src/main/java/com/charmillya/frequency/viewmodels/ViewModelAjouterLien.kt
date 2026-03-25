package com.charmillya.frequency.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.charmillya.frequency.data.AppDataStore
import com.charmillya.frequency.data.Utilities
import com.charmillya.frequency.models.Lien
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import java.util.UUID

class ViewModelAjouterLien(application: Application) : AndroidViewModel(application) {
    val dataStore = AppDataStore(getApplication())
    private val gson = Gson()

    var name by mutableStateOf("")
    var meetDate by mutableStateOf<Long?>(null)
    var lastInteractionDay by mutableStateOf<Long?>(null)

    var imageUri by mutableStateOf<String?>(null)

    fun ajouterLien(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val utils = Utilities()
                val pathStockageInterne = utils.saveImageToInternalStorage(context, imageUri)

                val jsonString = dataStore.getFromDataStore("lesLiens", "[]")
                val itemType: Type = object : TypeToken<MutableList<Lien>>() {}.type
                val listeLiens: MutableList<Lien> = gson.fromJson(jsonString, itemType) ?: mutableListOf()

                val nouveauLien = Lien(
                    idLien = UUID.randomUUID().toString(),
                    name = name,
                    meetDate = meetDate,
                    lastInteractionDay = lastInteractionDay,
                    imagePath = pathStockageInterne,
                    interactionCount = 0
                )

                listeLiens.add(nouveauLien)

                val updatedJson = gson.toJson(listeLiens)
                dataStore.saveToDataStore("lesLiens", updatedJson)

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
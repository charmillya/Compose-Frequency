package com.charmillya.frequency.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.charmillya.frequency.data.AppDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelFrequencyApp(application: Application) : AndroidViewModel(application) {
    val dataStore = AppDataStore(getApplication())

    
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    init {
        viewModelScope.launch {
            val storedValue = getLogin()
            _isLoggedIn.value = storedValue
        }
    }

    suspend fun rememberLogin(isGuest: Boolean) {
        dataStore.saveToDataStore("isLoggedIn", "true")
        dataStore.saveToDataStore("isGuest", isGuest.toString())
    }

    suspend fun getLogin(): Boolean {
        val loginString = try {
            dataStore.getFromDataStore("isLoggedIn")
        } catch(e: Exception) {
            return false
        }
        
         return loginString == "true"
    }
}
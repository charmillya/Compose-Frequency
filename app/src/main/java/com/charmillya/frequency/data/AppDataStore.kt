package com.charmillya.frequency.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.ds: DataStore<Preferences> by preferencesDataStore(name = "frequencyDS")

class AppDataStore(var context: Context) {
    val liensFlow: Flow<String> = context.ds.data
        .map { it[stringPreferencesKey("lesLiens")] ?: "[]" }

    val orbitStyleFlow: Flow<String> = context.ds.data
        .map { it[stringPreferencesKey("orbitStyle")] ?: "dotted" }

    val rotationSpeedFlow: Flow<Float> = context.ds.data
        .map { it[stringPreferencesKey("rotationSpeed")]?.toFloat() ?: 1f }

    
    val layoutStyleFlow: Flow<String> = context.ds.data
        .map { it[stringPreferencesKey("layoutStyle")] ?: "random" }

    suspend fun saveToDataStore(keyString: String, value: String) {
        val key = stringPreferencesKey(keyString)
        context.ds.edit { it[key] = value }
    }

    suspend fun getFromDataStore(keyString: String, defaultValue: String = ""): String {
        val key = stringPreferencesKey(keyString)
        val prefs = context.ds.data.first()
        return prefs[key] ?: defaultValue
    }

    suspend fun resetDataStore() {
        context.ds.edit { it.clear() }
    }
}
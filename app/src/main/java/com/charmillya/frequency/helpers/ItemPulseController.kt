package com.charmillya.frequency.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember

@Stable
class ItemPulseController<K> internal constructor(
    private val tokens: MutableMap<K, Int>
) {
    fun trigger(key: K) {
        tokens[key] = (tokens[key] ?: 0) + 1
    }

    fun tokenFor(key: K): Int = tokens[key] ?: 0

    fun retainKeys(validKeys: Set<K>) {
        tokens.keys.retainAll(validKeys)
    }

    fun clear() {
        tokens.clear()
    }
}

@Composable
fun <K> rememberItemPulseController(): ItemPulseController<K> {
    val tokens = remember { mutableStateMapOf<K, Int>() }
    return remember(tokens) { ItemPulseController(tokens) }
}


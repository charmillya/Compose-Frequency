package com.charmillya.frequency.data

import androidx.compose.ui.graphics.Color
import com.charmillya.frequency.models.Lien

data class Planete(
    val lien: Lien,
    val couleur: Color,
    val rayon: Float, 
    val distanceOrbitale: Float 
)
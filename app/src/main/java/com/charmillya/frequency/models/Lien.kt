package com.charmillya.frequency.models



data class Lien(
    val idLien: String,
    val name: String,
    var imagePath: String?, 
    val lastInteractionDay: Long?,
    val meetDate: Long?,
    val interactionCount: Int
)
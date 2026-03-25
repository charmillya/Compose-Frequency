package com.charmillya.frequency.data

import android.content.Context
import android.net.Uri
import java.io.File

class Utilities {
    fun saveImageToInternalStorage(context: Context, imagePath: String?): String? {
        if (imagePath == null) return null

        return try {
            
            val sourceUri = Uri.parse(imagePath)

            val inputStream = context.contentResolver.openInputStream(sourceUri)
            val fileName = "lien_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
package com.charmillya.frequency.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.charmillya.frequency.data.AppDataStore
import com.charmillya.frequency.data.Utilities
import com.charmillya.frequency.models.Lien
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.lang.reflect.Type
import java.util.UUID


data class ContactImport(val uri: Uri, val nom: String, val photoBitmap: ImageBitmap?)
data class ContactValide(val contact: ContactImport, val derniereInteraction: Long, val dateRencontre: Long?)

class ViewModelImporterLiens(application: Application) : AndroidViewModel(application) {
    private val dataStore = AppDataStore(getApplication())
    private val gson = Gson()
    private val utils = Utilities()

    var contactsEnAttente by mutableStateOf<List<ContactImport>>(emptyList())
        private set
    var contactsValides by mutableStateOf<List<ContactValide>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set

    fun resoudreContacts(context: Context, urisContacts: List<Uri>) {
        if (contactsEnAttente.isNotEmpty() || contactsValides.isNotEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val resolvedContacts = urisContacts.mapNotNull { uri ->
                var nom = "Sans nom"
                var photoBitmap: ImageBitmap? = null
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_URI)

                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
                        if (nameIndex != -1) nom = cursor.getString(nameIndex) ?: "Sans nom"

                        if (photoIndex != -1 && cursor.getString(photoIndex) != null) {
                            try {
                                val photoUri = Uri.parse(cursor.getString(photoIndex))
                                val stream = context.contentResolver.openInputStream(photoUri)
                                if (stream != null) {
                                    photoBitmap = BitmapFactory.decodeStream(stream)?.asImageBitmap()
                                    stream.close()
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    }
                }

                if (photoBitmap == null) {
                    try {
                        val stream: InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(context.contentResolver, uri, true)
                        if (stream != null) {
                            photoBitmap = BitmapFactory.decodeStream(stream)?.asImageBitmap()
                            stream.close()
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }

                ContactImport(uri, nom, photoBitmap)
            }

            withContext(Dispatchers.Main) {
                contactsEnAttente = resolvedContacts
                isLoading = false
            }
        }
    }

    fun ajouterContactValide(contactValide: ContactValide) {
        contactsValides = contactsValides + contactValide
    }

    fun sauvegarderImports(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = dataStore.getFromDataStore("lesLiens", "[]")
                val itemType: Type = object : TypeToken<MutableList<Lien>>() {}.type
                val listeLiens: MutableList<Lien> = gson.fromJson(jsonString, itemType) ?: mutableListOf()

                contactsValides.forEach { valide ->
                    var pathStockageInterne: String? = null

                    
                    valide.contact.photoBitmap?.let { imageBitmap ->
                        val bitmap = imageBitmap.asAndroidBitmap()
                        val filename = "contact_${UUID.randomUUID()}.png"

                        
                        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        }
                        pathStockageInterne = context.getFileStreamPath(filename).absolutePath
                    }

                    val nouveauLien = Lien(
                        idLien = UUID.randomUUID().toString(),
                        name = valide.contact.nom,
                        meetDate = valide.dateRencontre,
                        lastInteractionDay = valide.derniereInteraction,
                        imagePath = pathStockageInterne,
                        interactionCount = 0
                    )
                    listeLiens.add(nouveauLien)
                }

                val updatedJson = gson.toJson(listeLiens)
                dataStore.saveToDataStore("lesLiens", updatedJson)

                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun mettreAJourContactValide(updatedContact: ContactValide) {
        contactsValides = contactsValides.map {
            if (it.contact.uri == updatedContact.contact.uri) updatedContact else it
        }
    }

    fun supprimerContactValide(contactUri: Uri) {
        contactsValides = contactsValides.filter { it.contact.uri != contactUri }
    }

    fun reinitialiser() {
        contactsEnAttente = emptyList()
        contactsValides = emptyList()
    }
}
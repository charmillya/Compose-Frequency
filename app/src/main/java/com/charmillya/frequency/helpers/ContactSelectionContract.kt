package com.charmillya.frequency.helpers 

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContract


class ContactSelectionContract : ActivityResultContract<Void?, List<Uri>>() {

    
    override fun createIntent(context: Context, input: Void?): Intent {
        return Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
    }

    
    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val uris = mutableListOf<Uri>()
        if (resultCode == Activity.RESULT_OK && intent != null) {
            if (intent.clipData != null) {
                val count = intent.clipData!!.itemCount
                for (i in 0 until count) {
                    intent.clipData!!.getItemAt(i).uri?.let { uris.add(it) }
                }
            } else if (intent.data != null) {
                uris.add(intent.data!!)
            }
        }
        return uris
    }
}
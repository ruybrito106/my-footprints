package br.com.ufpe.cin.myfootprints

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract

class ContactPicker {
    var contactNumber: String? = null
        private set

    fun setContactMobileNumber(ctx: Context, data: Intent?) {
        val contactUri = data?.data
        val cursor = ctx.contentResolver.query(contactUri!!, PROJECTION, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            contactNumber = cursor.getString(numberIndex)
        }

        cursor!!.close()
    }

    companion object {

        private val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
    }

}

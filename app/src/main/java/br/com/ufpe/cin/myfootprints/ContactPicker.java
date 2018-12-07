package br.com.ufpe.cin.myfootprints;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactPicker {

    private static String[] PROJECTION = { ContactsContract.CommonDataKinds.Phone.NUMBER };
    private String contactNumber;

    public void setContactMobileNumber(Context ctx, Intent data) {
        Uri contactUri = data.getData();
        Cursor cursor = ctx.getContentResolver().query(contactUri, PROJECTION, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            contactNumber = cursor.getString(numberIndex);
        }

        cursor.close();
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

}

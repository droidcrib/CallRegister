package com.blogspot.droidcrib.callregister.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.blogspot.droidcrib.callregister.model.ContactCard;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ContactsProvider {

    private static ContactCard mContactCard = new ContactCard();

    public static ContactCard getNameByPhoneNumber(Context context, String phoneNumber) {

        mContactCard.setName(null);
        mContactCard.setAavatar(null);
        mContactCard.setAvatarUri(null);

        ContentResolver contentResolver = context.getContentResolver();
        String lastTenDigitsNumber = parseLastTenDigits(phoneNumber);

        // Check if number exists in Contacts
        Cursor cur = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{lastTenDigitsNumber},
                null);

        // If query result is not empty get name for given number
        if (cur.getCount() > 0) {

            cur.moveToFirst();
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            mContactCard.setName(name);
            // Getting image
            String image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if (image_uri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image_uri));
                    mContactCard.setAavatar(bitmap);
                    mContactCard.setAvatarUri(image_uri);
                } catch (FileNotFoundException e) {
                    //  Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    //  Auto-generated catch block
                    e.printStackTrace();
                }
            }
            cur.close();

            return mContactCard;
        } else {
            return parseAllContacts(context, phoneNumber);
        }
    }

    private static ContactCard parseAllContacts(Context context, String phoneNumber) {

        mContactCard.setName(phoneNumber);
        String sampleNumber = parseLastTenDigits(phoneNumber);
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String queriedNumber = parseLastTenDigits(phoneNo);

                        if (queriedNumber.equals(sampleNumber)) {
                            mContactCard.setName(name);
                            // Getting image
                            String image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                            if (image_uri != null) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image_uri));
                                    mContactCard.setAavatar(bitmap);
                                    mContactCard.setAvatarUri(image_uri);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return mContactCard;
                        }
                    }
                    pCur.close();
                }
            }
        }
        return mContactCard;
    }

    private static String parseLastTenDigits(String phoneNumber) {
        //Remove all non numeric symbols in number
        String nospaceNumber = phoneNumber.replaceAll("\\D+", "");
        // Get last 10 digits of phone number
        return nospaceNumber.length() > 10
                ? nospaceNumber.substring(nospaceNumber.length() - 10)
                : nospaceNumber;
    }


}

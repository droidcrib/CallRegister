package com.blogspot.droidcrib.callregister.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 */
public class ContactsProvider {

    private static final String TAG = "ContactsProvider";

    public static String getNameByPhoneNumber(Context context, String phoneNumber) {

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
            // Getting image
            String image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if (image_uri != null) {
                System.out.println(Uri.parse(image_uri));
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image_uri));
                    Log.d(TAG, "\n getNameByPhoneNumber -- Image in Bitmap:" + bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            cur.close();

            Log.d(TAG, "ContactsProvider -- return name " + name);
            return name;
        } else {
            Log.d(TAG, "ContactsProvider -- return parseAllContacts;");
            return parseAllContacts(context, phoneNumber);
        }


    }

    private static String parseAllContacts(Context context, String phoneNumber){

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
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String queriedNumber = parseLastTenDigits(phoneNo);

                        if(queriedNumber.equals(sampleNumber)) {
                            // Getting image
                            String image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                            Log.d(TAG, "ContactsProvider -- image uri " + image_uri);
                            if (image_uri != null) {
                                System.out.println(Uri.parse(image_uri));
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image_uri));
                                    Log.d(TAG, "\n parseAllContacts -- Image in Bitmap:" + bitmap);
                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            Log.d(TAG, "ContactsProvider -- phoneNo " + phoneNo);
                            Log.d(TAG, "ContactsProvider -- queriedNumber " + queriedNumber);
                            return name;
                        }
                    }
                    pCur.close();
                }
            }
        }
        return phoneNumber;
    }

    private static String parseLastTenDigits(String phoneNumber){
        //Remove all non numeric symbols in number
        String nospaceNumber = phoneNumber.replaceAll("\\D+","");
        // Get last 10 digits of phone number
        return nospaceNumber.length() > 10
                ? nospaceNumber.substring(nospaceNumber.length() - 10)
                : nospaceNumber;
    }




}

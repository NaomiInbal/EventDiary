package com.nomyin.dairyapp;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Date;

public class MyEvent {
    String eventName;
    ContactsContract.Contacts contactName;
    Date eventDate;
    String eventNote;
    Bitmap eventImageBitmap;
//    Contact contact;

    public MyEvent(String eventName, ContactsContract.Contacts contactName, Date eventDate, String eventNote, Bitmap eventImageBitmap) {
        this.eventName = eventName;
        this.eventDate =eventDate;
        this.contactName = contactName;
        this.eventImageBitmap = eventImageBitmap;
        this.eventNote = eventNote;
        Log.d("mylog", "MyEvent: "+ eventName+ " "+ eventNote+" " + eventDate +" "+ contactName);
    }
}

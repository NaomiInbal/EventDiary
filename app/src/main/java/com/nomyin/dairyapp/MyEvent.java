package com.nomyin.dairyapp;

import android.util.Log;

public class MyEvent {
    String eventName;
    String contactName;
    String eventDate;
    String eventNote;
    String eventImageURL;
//    Contact contact;

    public MyEvent(String eventName, String contactName, String eventDate, String eventNote, String eventImageURL) {
        this.eventName = eventName;
        this.eventDate =eventDate;
        this.contactName = contactName;
        this.eventImageURL = eventImageURL;
        this.eventNote = eventNote;
        Log.d("mylog", "MyEvent: "+ eventName+ " "+ eventNote+" " + eventDate +" "+ contactName);
    }
}

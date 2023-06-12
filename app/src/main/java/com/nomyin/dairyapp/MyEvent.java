package com.nomyin.dairyapp;

import android.util.Log;

import com.google.firebase.storage.StorageReference;

public class MyEvent {
    String eventName;
    String contactName;
    String eventDate;
    String eventNote;
    StorageReference eventImageUrl;





    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventNote(String eventNote) {
        this.eventNote = eventNote;
    }

    public void setEventImageUrl(StorageReference eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventNote() {
        return eventNote;
    }

    public StorageReference getEventImageUrl() {
        return eventImageUrl;
    }

    public MyEvent(String eventName, String contactName, String eventDate, String eventNote, StorageReference eventImageURL) {
        this.eventName = eventName;
        this.eventDate =eventDate;
        this.contactName = contactName;
        this.eventImageUrl = eventImageURL;
        this.eventNote = eventNote;
        Log.d("mylog", "MyEvent: "+ eventName+ " "+ eventNote+" " + eventDate +" "+ contactName);
    }

        @Override
        public String toString() {
            return "MyEvent{" +
                    "eventName='" + eventName + '\'' +
                    ", eventContactName='" + contactName + '\'' +
                    ", eventDateStr='" + eventDate + '\'' +
                    ", eventNote='" + eventNote + '\'' +
                    ", eventImageUrl='" + eventImageUrl + '\'' +
                    '}';
        }
    }



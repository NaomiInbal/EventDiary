package com.nomyin.dairyapp;

import android.util.Log;

public class MyEvent {
    String eventName;
    String contactName;
    String eventDate;
    String eventNote;
    String eventImageURL;





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

    public void setEventImageURL(String eventImageURL) {
        this.eventImageURL = eventImageURL;
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

    public String getEventImageURL() {
        return eventImageURL;
    }

    public MyEvent(String eventName, String contactName, String eventDate, String eventNote, String eventImageURL) {
        this.eventName = eventName;
        this.eventDate =eventDate;
        this.contactName = contactName;
        this.eventImageURL = eventImageURL;
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
                    ", eventImageUrl='" + eventImageURL + '\'' +
                    '}';
        }
    }



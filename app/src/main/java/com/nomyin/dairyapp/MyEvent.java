package com.nomyin.dairyapp;


import androidx.annotation.NonNull;

public class MyEvent {
    String eventName;
    String contactName;
    String eventDate;
    String eventNote;
    String eventImageUrl;
    String eventID;
    String eventDateDayBefore;
    String eventDateWeekBefore;


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

    public void setEventImageUrl(String eventImageUrl) {
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

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    //constructor without eventID parameter
    public MyEvent(String eventName, String contactName, String eventDate,String eventDateDayBefore, String eventDateWeekBefore, String eventNote, String eventImageUrl) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDateDayBefore = eventDateDayBefore;
        this.eventDateWeekBefore = eventDateWeekBefore;
        this.contactName = contactName;
        this.eventImageUrl = eventImageUrl;
        this.eventNote = eventNote;
    }

    // Constructor without eventID and eventImageUrl parameters
    public MyEvent(String eventName, String contactName, String eventDate, String eventDateDayBefore, String eventDateWeekBefore, String eventNote) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDateDayBefore = eventDateDayBefore;
        this.eventDateWeekBefore = eventDateWeekBefore;
        this.contactName = contactName;
        this.eventNote = eventNote;
    }


    @NonNull
    @Override
    public String toString() {
        return "MyEvent{" +
                "eventName='" + eventName + '\'' +
                ", contactName='" + contactName + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventNote='" + eventNote + '\'' +
                ", eventImageUrl='" + eventImageUrl + '\'' +
                ", eventID='" + eventID + '\'' +
                '}';
    }
}



package com.nomyin.dairyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//
//public class ReceiverDateChanged extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO: This method is called when the BroadcastReceiver is receiving
//        String action = intent.getAction();
//            Log.d("", "onReceivejlkjljlkjlk: " );
//        if (Intent.ACTION_DATE_CHANGED.equals(action)) {// Get the current date
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String currentDate = dateFormat.format(new Date());
//            // Display the current date
//            Toast.makeText(context, "Current Date: " + currentDate, Toast.LENGTH_SHORT).show();
//        }
//        }
//    }
public class ReceiverDateChanged extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_DATE_CHANGED.equals(action)) {
            // Get the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            String currentDate = dateFormat.format(new Date());

            // Display the current date
            Toast.makeText(context, "Current Date: " + currentDate, Toast.LENGTH_SHORT).show();

            // Check if it's a change of year
            if (isChangeOfYear(currentDate)) {
                // Update all documents in the Events collection in Firebase to the new year
                updateEventsCollectionToNewYear();
            }
        }
    }

    private boolean isChangeOfYear(String currentDate) {
        // Check if the current date is the first day of a new year
        return currentDate.endsWith("-01-01");
    }

    private void updateEventsCollectionToNewYear() {
        // Update the Events collection in Firebase to the new year
        // Add your implementation to update the documents accordingly
        Log.d("mylog", "Updating Events collection to a new year...");
    }
}

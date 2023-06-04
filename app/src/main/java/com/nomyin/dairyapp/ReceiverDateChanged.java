package com.nomyin.dairyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiverDateChanged extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String action = intent.getAction();
        if (Intent.ACTION_DATE_CHANGED.equals(action)) {// Get the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());
            // Display the current date
            Toast.makeText(context, "Current Date: " + currentDate, Toast.LENGTH_SHORT).show();
        }
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

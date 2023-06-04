package com.nomyin.dairyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final long DELAY_MS = 5000; // 5 seconds delay
    private BroadcastReceiver myReceiver;
    private NotificationManager notificationManager;
    private int nID = 0;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delayed transition to MainActivity2
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
          //TODO IF THE USER GETS BACK TO THIS PAGE HE CANT DO ANYTHING AND IT IS NOT MOVES
           //TODO// finish(); // Optional: Finish MainActivity to prevent going back to it
        }, DELAY_MS);

        //notification and Broadcast
        //TODO is this is the right place for notification and broadcast receiver
        //setupUI();
        setupNotification();
        showNotification();
        setupBroadcast();
    }
    private void setupBroadcast()
    {
        // Create BroadcastReceiver object
        myReceiver = new ReceiverDateChanged();

        // Create IntentFilter for battery change broadcast
        filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Register the receiver to start listening for battery change messages
        registerReceiver(myReceiver, filter); // place it in onStart()
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // Un-Register the receiver to stop listening for battery change messages
        unregisterReceiver(myReceiver); // place it in onStop()
    }

//    private void setupUI()
//    {
//
//        btn = findViewById(R.id.btnShowID);
//        btn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                showNotification();
//            }
//        });
//    }

    private void showNotification()
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, 0);
        String s = "houhjik";
        // 3. Create & show the Notification. (Every time you want to show notification)
        Notification notification = new NotificationCompat.Builder(this, "CHANNEL1_ID")
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(s)
                .setContentIntent(pendingIntent)
                .build();

        nID++;
        notificationManager.notify(nID, notification);
    }

    private void setupNotification()
    {
        // Get reference Notification Manager system Service
        notificationManager = getSystemService(NotificationManager.class);
        // Create Notification-Channel. (JUST ONCE!)
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    "CHANNEL1_ID", // Constant for Channel ID
                    "CHANNEL1_NAME", // Constant for Channel NAME
                    NotificationManager.IMPORTANCE_HIGH);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

}


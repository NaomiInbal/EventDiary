package com.nomyin.dairyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    //TODO 5000
    private static final long DELAY_MS = 1000; // 5 seconds delay
    private BroadcastReceiver myReceiver;

    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delayed transition to MainActivity2
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            //TODO IF THE USER GETS BACK TO THIS PAGE HE CANT DO ANYTHING AND IT IS NOT MOVES
            finish(); // Optional: Finish MainActivity to prevent going back to it
        }, DELAY_MS);
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
//setupBroadcast();
//    private void setupBroadcast()
//    {
//        // Create BroadcastReceiver object
//        myReceiver = new ReceiverDateChanged();
//
//        // Create IntentFilter for battery change broadcast
//        filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
//    }
//
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//
//        // Register the receiver to start listening for battery change messages
//        registerReceiver(myReceiver, filter); // place it in onStart()
//    }
//
//    @Override
//    protected void onStop()
//    {
//        super.onStop();
//        // Un-Register the receiver to stop listening for battery change messages
//        unregisterReceiver(myReceiver); // place it in onStop()
//    }
//broadcast
//  Create BroadcastReceiver object
//BroadcastReceiver myReceiver = new ReceiverDateChanged();
    // Create IntentFilter for battery change broadcast
   // IntentFilter filter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
    // Register the receiver to start listening for battery change messages

}


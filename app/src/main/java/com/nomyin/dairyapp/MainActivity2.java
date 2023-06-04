package com.nomyin.dairyapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import android.content.DialogInterface;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
//import android.os.Build;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private Button btn_addEvent;
    private ArrayList<MyEvent> events;
    private EventAdapter eventAdapter;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = findViewById(R.id.listView);
         eventAdapter = new EventAdapter(this);
        listView.setAdapter(eventAdapter);

        events = new ArrayList();
        listData = new ArrayList<>();
        eventAdapter.events = events;
        eventAdapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);

        addNewEvent();
    }
    //create the menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //there are 3 item in the menu
        MenuItem aboutMenu = menu.add("About");
        MenuItem settingsMenu = menu.add("Settings");
        MenuItem exitMenu = menu.add("Exit");

        aboutMenu.setOnMenuItemClickListener(item -> {
            aboutAlertDialog();
            return false;
        });

        settingsMenu.setOnMenuItemClickListener(item -> {
            IntentSettingActivity();
            return false;
        });

        exitMenu.setOnMenuItemClickListener(item -> {
            exitAlertDialog();
            return false;
        });

        return true;
    }
    //About dialog
    private void aboutAlertDialog() {
        String strDeviceOS = "Android OS " + Build.VERSION.RELEASE + " API " + Build.VERSION.SDK_INT;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("About App");
        dialog.setMessage("\nEvent Diary Reminder \n\n By Naomi Inbal-Weinstein \n\n 19.06.2023 \n\n " + strDeviceOS + "\n\n" + "Android 11(R) API Level 30 ");
        dialog.setPositiveButton("OK", (dialog1, which) -> {
            dialog1.dismiss();   // close this dialog
        });
        dialog.show();
    }
    //Moving to settings activity
    private void IntentSettingActivity() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
    //Exit dialog
    private void exitAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_exit);
        dialog.setTitle("Exit App");
        dialog.setMessage("Are you sure you want to exit?");
        dialog.setCancelable(false);//not able to be canceled

        //TODO exit the app, not the page!
        dialog.setPositiveButton("YES", (dialog1, which) -> {
            finish();   // destroy this activity
        });
        dialog.setNegativeButton("NO", (dialog12, which) -> {
            dialog12.dismiss();   // close this dialog
        });
        dialog.show();
    }
    //add new event
    private void addNewEvent(){
        btn_addEvent = findViewById(R.id.btnAddEventID);
        btn_addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEventDialog();
            }
        });
    }
    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Event");
        // Set up the input field
        final EditText eventNameInput = new EditText(this);
        eventNameInput.setHint("Enter event name");
        builder.setView(eventNameInput);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String eventName = eventNameInput.getText().toString();
                TextView textView = findViewById(R.id.tex);
                textView.setText(eventName);

                events.add(new MyEvent(eventName));
                eventAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("Cancel", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//load contacts

}

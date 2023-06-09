package com.nomyin.dairyapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

//import android.content.DialogInterface;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
//import android.os.Build;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PICK_CONTACT = 1;
    private Button btn_addEvent;
    private Button  btnPrevMonth;
    private Button btnNextMonth;
    private TextView MonthTitle;

    private ArrayList<MyEvent> events;
    private EventAdapter eventAdapter;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> listData;
    private static final int REQUEST_CONTACT_PICKER = 1;
    private EditText eventNameInput;
    private DatePicker eventDatePicker;
    private Button chooseContactButton;
    private Button eventDateButton;
    private String selectedContactName;
    private static final int REQUEST_IMAGE_PICKER = 2;
    private EditText eventNoteInput;
    private ImageView eventImageView;
    private Bitmap eventImageBitmap;
    private String eventImageUrl;
    private String eventName;
    private String eventContactName;
    private String eventDateStr;
    private String eventNote;
    private String selectedDate;
    private NotificationManager notificationManager;
    private int nID = 0;
    private static final int REQUEST_CAMERA_PHOTO = 1;
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 1;
    private ActivityResultLauncher<Intent> contactPickerLauncher;
    // Calendar instance to get the current month
    private Calendar calendar;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        eventAdapter = new EventAdapter(this);
        listView.setAdapter(eventAdapter);
        btnPrevMonth = findViewById(R.id.btnPrevMonthID);
        btnNextMonth = findViewById(R.id.btnNextMonthID);
        MonthTitle = findViewById(R.id.MonthTitleID);
        events = new ArrayList();
        listData = new ArrayList<>();
        eventAdapter.events = events;
        eventAdapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        showEvents();
        addNewEvent();
//        setupNotification();
//        showNotification();

    }

    private void showEvents() {
         //Initialize the calendar instance
         calendar = Calendar.getInstance();

         //navigete between month
        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the previous month
                calendar.add(Calendar.MONTH, -1);
                Log.d("mylog", "prev: "+ Calendar.MONTH);
                //filterEventsByMonth();
            }
        });
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the next month
                calendar.add(Calendar.MONTH, 1);
                Log.d("mylog", "next: "+ Calendar.MONTH);
               // filterEventsByMonth();
            }
        });

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
        dialog.setMessage("\nEvent Dairy Reminder \n\n By Naomi Inbal-Weinstein \n\n 19.06.2023 \n\n " + strDeviceOS + "\n\n" + "Android 11(R) API Level 30 ");
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
    private void addNewEvent() {
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
        // Set up the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
//clear the variables
        eventName = "";
        eventContactName = "";
        eventNote = "";
        eventImageUrl = "";
        selectedDate = "";

        eventNameInput = new EditText(this);
        eventNameInput.setHint("Enter event name");

        chooseContactButton = new Button(this);
        chooseContactButton.setText("Choose a contact");
        chooseContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionContacts();
            }
        });

        eventDateButton = new Button(this);
        eventDateButton.setText("Choose a date");
        eventDateButton.setInputType(InputType.TYPE_NULL);
        eventDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalander();
            }
        });

        eventNoteInput = new EditText(this);
        eventNoteInput.setHint("Notes:");

        eventImageView = new ImageView(this);
        eventImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(); // Start the camera activity

            }
        });

        layout.addView(eventNameInput);
        layout.addView(chooseContactButton);
        layout.addView(eventDateButton);
        layout.addView(eventNoteInput);
        //TODO add an image
//        layout.addView(eventImageView);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveNewEvent();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setView(layout);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContactPicker(); // Permission granted, open the contact picker
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                //dialog.setCanceledOnTouchOutside(false);
            }
        }
    }

    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO check if permission denied
        if (requestCode == REQUEST_PICK_CONTACT && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        eventContactName = cursor.getString(nameIndex);
                        chooseContactButton.setText(eventContactName);

                    }
                }
            }
        }
    }

    private void permissionContacts() {
        // Request permission to read contacts
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS_PERMISSION);
        } else {
            openContactPicker(); // If permission is already granted, open the contact picker
        }
    }

    private void createCalander() {
        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();

        // Get the current date values
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Update the event date input field with the selected date
                    selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    eventDateButton.setText(selectedDate);
                },
                year, month, day);

        // Show the date picker dialog
        datePickerDialog.show();
    }

    private void saveNewEvent() {
        //TODO required fields
        //All variables have values, proceed with saving
        eventName = eventNameInput.getText().toString();
        eventNote = eventNoteInput.getText().toString();
        eventDateStr = selectedDate;

        saveEventOnFirestore();
        readEventsOnFireStore();
        //convert string to date
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                Date eventDate = null;
//                try {
//                    eventDate = sdf.parse(eventDateStr);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
    }

    private void takePicture() {

    }

//                        private void showNotification ()
//                        {
//        Intent intent = new Intent(this, SplashActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, 0);
//        String s = "houhjik";
//        // 3. Create & show the Notification. (Every time you want to show notification)
//        Notification notification = new NotificationCompat.Builder(this, "CHANNEL1_ID")
//                .setSmallIcon(R.drawable.ic_notify)
//                .setContentTitle(s)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        nID++;
//        notificationManager.notify(nID, notification);
//                        }
//Notification
//                        private void setupNotification ()
//                        {
//        // Get reference Notification Manager system Service
//        notificationManager = getSystemService(NotificationManager.class);
//        // Create Notification-Channel. (JUST ONCE!)
//        NotificationChannel notificationChannel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationChannel = new NotificationChannel(
//                    "CHANNEL1_ID", // Constant for Channel ID
//                    "CHANNEL1_NAME", // Constant for Channel NAME
//                    NotificationManager.IMPORTANCE_HIGH);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.createNotificationChannel(notificationChannel);
//        }

    ////TODO put in a thread
    private void saveEventOnFirestore() {
        //                                       //eventImageBitmap
////                        /url =  upload image to Strorage
////built new event with the url     }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MyEvent myEvent = new MyEvent(eventName, eventContactName, eventDateStr, eventNote, eventImageUrl);
        events.add(myEvent);

        eventAdapter.notifyDataSetChanged();
        Log.d("mylog", "my: " + myEvent);


            db.collection("Events").add(myEvent)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("mylog", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("mylog", "Error adding document", e);
                        }
                    });
        }
    private void readEventsOnFireStore() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("Events")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("mylog", document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Log.d("mylog", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        thread.start();
    }


}




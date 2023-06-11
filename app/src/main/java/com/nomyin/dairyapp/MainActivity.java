package com.nomyin.dairyapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
import android.provider.MediaStore;
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
import java.util.UUID;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PICK_CONTACT = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE =6;
    private static final int CAMERA_REQUEST_CODE = 7;
    private Button btn_addEvent;
    private Button  btnPrevMonth;
    private Button btnNextMonth;
    private TextView monthTitle;

    private ArrayList<MyEvent> events;
    private EventAdapter eventAdapter;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> listData;
    private static final int REQUEST_CONTACT_PICKER =5;
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
    private static final int REQUEST_CAMERA_PHOTO = 3;
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 4;
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
        monthTitle = findViewById(R.id.MonthTitleID);
        events = new ArrayList();
        listData = new ArrayList<>();
        eventAdapter.events = events;
        eventAdapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        readEventsOnFireStore();
        showEvents();
        addNewEvent();
//        setupNotification();
//        showNotification();

    }
    private void readEventsOnFireStore() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "run: ");
                db.collection("Events")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "run2 ");
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("TAG", document.getId() + " => " + document.getData());
//                                       TODO convert document to MyEvent object
//                                        MyEvent event = document.toObject(MyEvent.class);
//                                        events.add(event);
//                                        Log.d("TAG", "onComplete: "+event);

                                    }

                                    Log.d("TAG", "onComplete2: ");
//                                    filterEventsByMonth();


                                } else {
                                    Log.d("mylog", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        thread.start();
    }
    private void callReadEventsOnFireStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("mylog", "Error getting documents.", e);
                            return;
                        }

                        if (querySnapshot != null) {
                            readEventsOnFireStore();
                        }
                    }
                });
    }



    private void showEvents() {
        Log.d("TAG", "showEvents: ");
        filterEventsByMonth();
        // Initialize the calendar instance
        calendar = Calendar.getInstance();
        // Set the name of the current month
        updateMonthTitle();
        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the previous month
                calendar.add(Calendar.MONTH, -1);
                updateMonthTitle();
                filterEventsByMonth();
            }
        });

        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to the next month
                calendar.add(Calendar.MONTH, 1);
                updateMonthTitle();
                Log.d("TAG", "onClick: ");
                filterEventsByMonth();
            }
        });
    }


        private void filterEventsByMonth() {
            Log.d("TAG", "filterEventsByMonth: ");
            // Create a Calendar instance
            Calendar calendar = Calendar.getInstance();

            // Get the current month and year
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);
            Log.d("filter", "filterEventsByMonth: "+ events);
            ArrayList<MyEvent> currentMonthEvents = new ArrayList<>();
                Date eventDate;
            // Filter events by the current month and year
            for (MyEvent event : events) {
                Calendar eventCalendar = Calendar.getInstance();
                Log.d("mylog", "filterEventsByMonth: "+event);
                //convert string date to object Date
                eventDate = convertToDate(event);
                if(eventDate==null){//Handle error
                    continue;
                }
                eventCalendar.setTime(eventDate);
                int eventMonth = eventCalendar.get(Calendar.MONTH);
                if (eventMonth == currentMonth) {
                    currentMonthEvents.add(event);
                }
            }
            // Display the current month events in the ListView
            events.clear();
            events.addAll(currentMonthEvents);
            eventAdapter.notifyDataSetChanged();
        }

    private Date convertToDate(MyEvent event) {
        Log.d("TAG", "convertToDate: ");
        Date date=null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            date = dateFormat.parse(event.eventDate);
            // Do something with the converted date
            // ...
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
        return date;
    }


    private void updateMonthTitle() {
        // Get the name of the current month
        String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());
        Log.d("logii", "updateMonthTitle: "+ monthName);
        // Set the month name in the MonthTitle TextView
        monthTitle.setText(monthName);
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
                createCalender();
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
////        layout.addView(eventImageView);
//        // Add button to capture a picture
//        Button captureImageButton = new Button(this);
//        captureImageButton.setText("Capture Image");
//        captureImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Check camera permission
//                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                    takePicture();
//                } else {
//                    // Request camera permission
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
//                }
//            }
//        });

//        layout.addView(captureImageButton);
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
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
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
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("TAG", "onActivityResult: camera");
            // Handle the captured image
            if (data != null) {
                // Get the image from the intent data
                eventImageBitmap = (Bitmap) data.getExtras().get("data");
                Log.d("TAG", "camera: ");
                // store the image in firestorege
               // uploadImageToFirebaseStorage(eventImageBitmap);
//                TODO load the picture, save the url , store on firebase the complete event
                // ...
            }
        }
    }
//    private void uploadImageToFirebaseStorage(Bitmap imageBitmap) {
//        // Create a unique filename for the image
//        String filename = UUID.randomUUID().toString() + ".jpg";
//
//        // Get a reference to the Firebase Storage root
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//
//        // Create a reference to the file location in Firebase Storage
//        StorageReference imageRef = storageRef.child("images/" + filename);
//
//        // Convert the Bitmap to a byte array
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageData = baos.toByteArray();
//
//        // Upload the byte array to Firebase Storage
//        UploadTask uploadTask = imageRef.putBytes(imageData);
//
//        // Register an upload listener to track the upload progress and handle the result
//        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()) {
//                    // Image upload successful
//                    // Get the download URL of the uploaded image
//                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            // Handle the image URL
//                            eventImageUrl = uri.toString();
//                            Log.d("im", "Image URL: eventImageUrl " + eventImageUrl);
//
//                            // TODO: Save the image URL to your database or perform any other required actions
//                        }
//                    });
//                } else {
//                    // Image upload failed
//                    Log.e("im", "Image upload failed: " + task.getException());
//                    // TODO: Handle the failure scenario
//                }
//            }
//        });
//    }


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

    private void createCalender() {
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
        Log.d("TAG", "takePicture: ");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                Log.d("TAG", "if ");
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);

                // onActivityResult( CAMERA_REQUEST_CODE, RESULT_OK,takePictureIntent);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
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




}




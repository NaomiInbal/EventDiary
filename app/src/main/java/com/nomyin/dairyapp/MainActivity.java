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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements EventAdapter.EventClickListener ,EventAdapter.EventEditClickListener{
    private static final int REQUEST_PICK_CONTACT = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE =6;
    private static final int CAMERA_REQUEST_CODE = 7;
    private Button btn_addEvent;
    private Button  btnPrevMonth;
    private Button btnNextMonth;
    private TextView monthTitle;

    private ArrayList<MyEvent> events;
    private ArrayList<MyEvent> allEvents;
    private EventAdapter eventAdapter;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private ArrayList<String> listData;
    private static final int REQUEST_CONTACT_PICKER =5;
    private EditText eventNameInput;
    private DatePicker eventDatePicker;
    private Button chooseContactButton;
    private Button eventDateButton;
    private  Button captureImageButton;
    private String selectedContactName;
    private static final int REQUEST_IMAGE_PICKER = 2;
    private EditText eventNoteInput;
    private ImageView eventImageView;
    private Bitmap eventImageBitmap;
    //private String eventImageUrl;
    private String eventImageUrl;
private int currentMonth;
    private String eventName;

    private String eventContactName;
    private String eventDateStr;
    String eventDateDayBefore;
    String eventDateWeekBefore;

    private String eventNote;
    private String selectedDate;
    private NotificationManager notificationManager;
    private int nID = 0;
    private static final int REQUEST_CAMERA_PHOTO = 3;
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 4;
    private ActivityResultLauncher<Intent> contactPickerLauncher;
    // Calendar instance to get the current month
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        initialize();
        //TODO read from firebase and display event on the screen - with thread
        //readEventsOnFirestore();
        showEvents();
        addNewEvent();
//        setupNotification();
//        showNotification();
    }
    private void setupUI() {
        listView = findViewById(R.id.listView);
        eventAdapter = new EventAdapter(this);
        listView.setAdapter(eventAdapter);
        btnPrevMonth = findViewById(R.id.btnPrevMonthID);
        btnNextMonth = findViewById(R.id.btnNextMonthID);
        monthTitle = findViewById(R.id.MonthTitleID);
    }
    private void initialize(){
        events = new ArrayList();
        allEvents = new ArrayList();
        listData = new ArrayList<>();
        eventAdapter.events = events;
        eventAdapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        readEventsOnFireStore();
        eventAdapter.setEventDetailsClickListener(MainActivity.this);
        eventAdapter.setEventEditClickListener(MainActivity.this);

    }
    //---------------------------------------------------------------------------------------------
    //create the menu
    //---------------------------------------------------------------------------------------------
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
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    //Exit dialog
    private void exitAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_exit);
        dialog.setTitle("Exit App");
        dialog.setMessage("Are you sure you want to exit?");
        dialog.setCancelable(false);//not able to be canceled
        dialog.setPositiveButton("YES", (dialog1, which) -> {
            finish();   // destroy this activity
        });
        dialog.setNegativeButton("NO", (dialog12, which) -> {
            dialog12.dismiss();   // close this dialog
        });
        dialog.show();
    }
    //----------------------------------------------------------------------------------------
    //add new event % display dialog
    //----------------------------------------------------------------------------------------
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
        builder.setMessage("All The Fields Are Required Except Note ");
        //TODO if edit needed - alloud not to take picture and note

        // Set up the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        //clear the variables
        eventName = "";
        eventContactName = "";
        eventNote = "";
        //TODO try save event with not take a picture and see what happened
        eventImageUrl = "";
        selectedDate = "";
        eventDateDayBefore="";
        eventDateWeekBefore="";
                //set the name
        eventNameInput = new EditText(this);
        eventNameInput.setHint("Enter event name");
        //set the contact
        chooseContactButton = new Button(this);
        chooseContactButton.setText("Choose a contact");
        chooseContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionContacts();//get permission to use contact
            }
        });
        //set the date
        eventDateButton = new Button(this);
        eventDateButton.setText("Choose a date");
        eventDateButton.setInputType(InputType.TYPE_NULL);
        eventDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalender();//open the calendar
            }
        });
        //set the note
        eventNoteInput = new EditText(this);
        eventNoteInput.setHint("Notes:");
        //set the picture
        captureImageButton = new Button(this);
        captureImageButton.setText("Take a picture");
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check camera permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    // Request camera permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });

        layout.addView(eventNameInput);
        layout.addView(chooseContactButton);
        layout.addView(eventDateButton);
        layout.addView(eventNoteInput);
        layout.addView(captureImageButton);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isNewEvent = true;
                //TODO now picture is  required so if there is not it can be problem to save the event
                // it should be checked separately and then save with url ="" and what to do whith out thread in this case?
                boolean isRequiredFields = requiredFields();
          if(isRequiredFields) {
                uploadImageToFirebaseStorage(eventImageBitmap, new UploadCallback() {
                    @Override
                    public void onUploadComplete(boolean success) {
                        if (success) {
                            Log.d("Avi", "onUploadComplete: saved!!!!!! ");
                            // Image upload was successful
                            // Continue with the rest of your code here
                        }
                        //TODO success is always false because asynchronous saveEventOnFirestore
//                        else {
//                            // Image upload failed
//                            Toast.makeText(MainActivity.this, "The event did not save! Maybe you are not connect to wifi, please try again", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
            } else { //there are empty required fields
                Toast.makeText(MainActivity.this, "The event did not save because the required fields, please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setView(layout);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //-------------------------------------------------------------------------------------------
    //Helper functions
    //-------------------------------------------------------------------------------------------
    //permission to get the  Contacts from the phone
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
    //move to the contacts if permission
    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_CONTACT);
    }
    //------------------------------------------------------------------------------------------
    //open the calendar to choose date
    private void createCalender() {
        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance(); //TODO to declare in mainactivity? use in 2 difference function

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
    //-----------------------------------------------------------------------------------------
    //open camera
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
    //--------------------------------------------------------------------------------------------
    //The result of the choose contact and open camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO check if permission denied
        if (requestCode == REQUEST_PICK_CONTACT && resultCode == RESULT_OK) { //contact
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
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) { //camera
            Log.d("TAG", "onActivityResult: camera");
            // Handle the captured image
            if (data != null) {
                // Get the image from the intent data
                eventImageBitmap = (Bitmap) data.getExtras().get("data");
                Log.d("TAG", "camera: ");
                // store the image in firestorege - on "save" button clicked
                // Set the button text to "Picture taken"
                captureImageButton.setText("Picture taken");
            }
        }
    }
    //-----------------------------------------------------------------------------------
    //check if the required Fields are not empty
    private  boolean requiredFields(){
        //TODO required fields according to if edit or not
        //All variables have values, proceed with saving
        eventName = eventNameInput.getText().toString();
        eventNote = eventNoteInput.getText().toString();
        eventDateStr = selectedDate;
        boolean isImage = eventImageBitmap != null ;
        Log.e("Avi", "requiredFields: "+ isImage);
        //checks required fields
        if (!eventName.isEmpty() && !selectedDate.isEmpty()  && isImage &&  !eventContactName.isEmpty()) {
            return true;//If all the required lines are not empty or null
        }
        else return false;
    }

    public static String getPreviousDay(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPreviousWeek(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            return dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //---------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //FireBase & FireStore
    //-------------------------------------------------------------------------------------
    //callback returns if success or failed after the upload complete
    interface UploadCallback {
        void onUploadComplete(boolean success);
    }

private void uploadImageToFirebaseStorage(Bitmap eventImageBitmap, UploadCallback callback) {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                // Create a unique filename for the image (e.g., using a timestamp)
                String filename = "event_image_" + System.currentTimeMillis() + ".jpg";
                // Get a reference to the Firebase Storage instance and the desired storage location
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("event_images").child(filename);
                // Convert the Bitmap to a byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                eventImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();
                // Create an upload task to upload the image to Firestore storage
                UploadTask uploadTask = storageRef.putBytes(imageData);

                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Image upload successful
                            // Retrieve the download URL for the image
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // The download URL is available here
                                    eventImageUrl = downloadUri.toString();
                                    // Save the event
                                    boolean saveSuccess = saveEventOnFirestore();
                                    Log.d("Avi", "onSuccess: saveSuccess:"+ saveSuccess +"eventImageUrl "+ eventImageUrl);

                                    if (saveSuccess) {
                                        callback.onUploadComplete(true);
                                    } else {
                                        callback.onUploadComplete(false);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error occurred while getting the download URL
                                    // Handle the error as needed
                                    Log.d("Avi", "failedUploadImage: " + eventImageUrl);
                                    callback.onUploadComplete(false);
                                }
                            });
                        } else {
                            // Image upload failed
                            // Handle the error as needed
                            callback.onUploadComplete(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.onUploadComplete(false);

            }
        }
    });

    thread.start();
}


    //--------------------------------------------------------------------------------------------------
private boolean saveEventOnFirestore() {
    eventDateDayBefore = getPreviousDay(eventDateStr);//
    eventDateWeekBefore = getPreviousWeek(eventDateStr);
    AtomicBoolean isSaveEventOnFirestore = new AtomicBoolean(false);
        //TODO if this function called from thread onSuccess it is in thread too? because it is not done in separate thread
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Log.d("Avi", "saveEventOnFirestore: " + "eventName='" + eventName + '\'' +
                        ", eventContactName='" + eventContactName + '\'' +
                        ", eventDateStr='" + eventDateStr + '\n' + "eventDateDayBefore" + eventDateDayBefore + '\'' +
                         "eventDateWeekBefore"+ eventDateWeekBefore + "eventNote=" + eventNote + '\'' +
                        ", eventImageUrl='" + eventImageUrl + '\'');
                MyEvent myEvent = new MyEvent(eventName, eventContactName, eventDateStr,eventDateDayBefore, eventDateWeekBefore , eventNote, eventImageUrl);
                Log.d("mylog", "my: " + myEvent);
                //Store on firebase
                db.collection("Events").add(myEvent)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Avi", "DocumentSnapshot added with ID: " + documentReference.getId());
                                //TODO edd the id to the event to use it to edit the event
                                myEvent.setEventID( documentReference.getId());
                                Log.d("edit", "myevent: "+ myEvent);
                                //TODO copy to allEvents and show the list sorted
                                events.add(myEvent);
                                eventAdapter.notifyDataSetChanged();
                                allEvents.add(myEvent);
                                filterEventsByMonth();
                                isSaveEventOnFirestore.set(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Avi", "Error adding document", e);

                            }
                        });
    return isSaveEventOnFirestore.get();
}
//---------------------------------------------------------------------------------------------------------------------
// read events from FB
private void readEventsOnFireStore() {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();//TODO to declare in mainactivity? use in 2 difference function

            // Query the "Events" collection in Firestore
            db.collection("Events")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            // Clear the existing events list
                            events.clear();

                            // Iterate through the documents in the query snapshot
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Extract the event details from the document
                                 eventName = document.getString("eventName");
                                eventContactName = document.getString("contactName");
                                eventDateStr = document.getString("eventDate");
                                eventDateDayBefore =document.getString("eventDateDayBefore");
                                eventDateWeekBefore = document.getString("eventDateWeekBefore");
                                eventNote = document.getString("eventNote");
                                 eventImageUrl = document.getString("eventImageUrl");

                                // Create a new MyEvent object and add it to the events list
                                MyEvent event = new MyEvent(eventName, eventContactName, eventDateStr, eventDateDayBefore, eventDateWeekBefore, eventNote, eventImageUrl);
                                events.add(event);
                            }
                            // Notify the adapter that the data has changed
                            eventAdapter.notifyDataSetChanged();
                            allEvents.addAll(events);//copy the events

                            filterEventsByMonth();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occur during the Firestore read operation
                            Log.e("FirebaseFirestore", "Error reading events from Firestore", e);
                        }
                    });
        }
    });

    thread.start();
}

//------------------------------------------------------------------------------------------------------------
    //Handle month and date
    //----------------------------------------------------------------------------------------------------------
private void showEvents() {
    Log.d("TAG", "showEvents: ");
    calendar = Calendar.getInstance();
    currentMonth = calendar.get(Calendar.MONTH);
    updateMonth();
    filterEventsByMonth();
    btnPrevMonth.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Move to the previous month
            calendar.add(Calendar.MONTH, -1);
            updateMonth();
            filterEventsByMonth();
        }
    });

    btnNextMonth.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Move to the next month
            calendar.add(Calendar.MONTH, 1);
            updateMonth();
            Log.d("TAG", "onClick: ");
            filterEventsByMonth();
        }
    });
}
//---------------------------------------------------------------------------------------------
    //Sort events by month
    private void filterEventsByMonth() {
        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();
        // Get the current month and year
      //  int curretMonth = calendar.get(Calendar.MONTH); gives the real date
        ArrayList<MyEvent> currentMonthEvents = new ArrayList<>();
        Date eventDate;
        // Filter events by the current month and year
        for (MyEvent event : allEvents) {
            //convert string date to object Date
            eventDate = convertToDate(event);
            if(eventDate==null){//Handle error
                continue;
            }
            calendar.setTime(eventDate);
            int eventMonth = calendar.get(Calendar.MONTH)+1;
            if (eventMonth == currentMonth) {
                currentMonthEvents.add(event);
            }
        }
        // Display the current month events in the ListView
        events.clear();
        events.addAll(currentMonthEvents);
        eventAdapter.notifyDataSetChanged();
    }
//---------------------------------------------------------------------------------------------------------
    //convert string date to  object Date
    private Date convertToDate(MyEvent event) {
        Date date=null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if(event.eventDate ==null) {
            return date;
        }
        try {
            date = dateFormat.parse(event.eventDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
        return date;
    }

//-----------------------------------------------------------------------------------------------------------
    // change the current month and display the name of the month
    private void updateMonth() {
        // Get the name of the current month
        String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());
        currentMonth = calendar.get(Calendar.MONTH)+1;
        // Set the month name in the MonthTitle TextView
        monthTitle.setText(monthName);
    }
    //------------------------------------------------------------------------------------------------
    //show details of exists event
        public void onEventClick(MyEvent event) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Event Details");
            builder.setMessage("Event Name: " + event.eventName + "\n Contact: " + event.contactName + "\n Date: " + event.eventDate + "\n Notes: " + event.eventNote);

            // Create a ImageView to display the image
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);

            // Load the image using Glide or Picasso
            Glide.with(this).load(event.eventImageUrl).into(imageView);

            // Add the ImageView to the dialog's layout
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(imageView);
            builder.setView(layout);
//            builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    showEditDialog(event);
//                }
//            });
            builder.setView(layout);
            builder.setPositiveButton("OK", null);
            builder.show();
        }
        //---------------------------------------------------------------------------------
       //edit exists event
        public void onEventEditClick(MyEvent event) {
            Log.d("edit", "onEventEditClick: ");
            showEditDialog(event);
        }
//----------------------------------------------------------------------------------
private void showEditDialog(MyEvent event) {
//    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    builder.setTitle("Edit Event");
//    builder.setMessage("All The Fields Are Required Except Note ");
//   //  Set up the input fields
//    LinearLayout layout = new LinearLayout(this);
//    layout.setOrientation(LinearLayout.VERTICAL);
//
//
//   //  Set the initial values in the EditText fields
//    EditText nameInput = new EditText(this);
//    nameInput.setText("Name: "+event.getEventName());
//  // set the contact
//    Button contactButton = new Button(this);
//
//   contactButton.setText(event.contactName);
//   contactButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            permissionContacts();//get permission to use contact
//        }
//    });
//    //set the date
//    Button dateButton = new Button(this);
//
//    dateButton.setText(event.eventDate);
//    dateButton.setInputType(InputType.TYPE_NULL);
//    dateButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            createCalender();//open the calendar
//        }
//    });
//   //set the note
//    EditText noteInput = new EditText(this);
//    noteInput.setHint("Note: "+event.eventNote);
//    //TODO set the picture
//    // Create a ImageView to display the image
//    ImageView imageView = new ImageView(this);
//    imageView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
//    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//    imageView.setAdjustViewBounds(true);
//
//    // Load the image using Glide or Picasso
//    Glide.with(this).load(event.eventImageUrl).into(imageView);
//
//    // Add the ImageView to the dialog's layout
//    Button imageButton = new Button(this);
//
//    imageButton.setText("Take a picture");
//    final boolean[] flagPictureTooked = {false};
//   imageButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // Check camera permission
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                takePicture();
//                flagPictureTooked[0] = true;
//            } else {
//                // Request camera permission
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
//            }
//        }
//    });
//
//   layout.addView(nameInput);
//    layout.addView(contactButton);
//    layout.addView(dateButton);
//    layout.addView(noteInput);
//    if(!flagPictureTooked[0]) {
//        layout.addView(imageView);
//        builder.setView(layout);
//    }
//    layout.addView(imageButton);
//
//    // Set up the buttons
//    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            boolean isNewEvent = true;
//            //TODO now picture is  required so if there is not it can be problem to save the event
//            // it should be checked separately and then save with url ="" and what to do whith out thread in this case?
//            String updatedEventName = nameInput.getText().toString();
//            String updatedEventContact = contactButton.getText().toString();
//        String updatedEventNote = noteInput.getText().toString();
//            // Update the event object with the new values
//            if(!updatedEventName.equals(event.eventName))//name changed
//            {event.eventName = updatedEventName;}
//            if(!updatedEventContact.equals(event.contactName))//contact changed
//            {event.contactName = updatedEventContact;}
//            if(!updatedEventNote.equals(event.eventNote))//name changed
//                //TODO arese the event from fire base and then create new one
//                uploadImageToFirebaseStorage(eventImageBitmap, new UploadCallback() {
//                    @Override
//                    public void onUploadComplete(boolean success) {
//                        if (success) {
//                            Log.d("Avi", "onUploadComplete: saved!!!!!! ");
//                            // Image upload was successful
//                            // Continue with the rest of your code here
//                        }
//                        //TODO success is always false because asynchronous saveEventOnFirestore
////                        else {
////                            // Image upload failed
////                            Toast.makeText(MainActivity.this, "The event did not save! Maybe you are not connect to wifi, please try again", Toast.LENGTH_SHORT).show();
////                        }
//                    }
//                });
//          //  } else { //there are empty required fields
//                Toast.makeText(MainActivity.this, "The event did not save because the required fields, please try again", Toast.LENGTH_SHORT).show();
//                return;
//            }
//       // }
//    });
//
//    builder.setNegativeButton("Cancel", null);
//   builder.setView(layout);
//
//    // Create and show the dialog
//    AlertDialog dialog = builder.create();
//    dialog.setCanceledOnTouchOutside(false);
//    dialog.show();
////    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
////        @Override
////        public void onClick(DialogInterface dialog, int which) {
////            // Get the updated values from the EditText fields
////            String updatedEventName = eventNameEditText.getText().toString();
////            String updatedEventContact = eventContactEditText.getText().toString();
////            // Get the updated values from other EditText fields as needed
////
////            // Update the event object with the new values
////            event.eventName = updatedEventName;
////            event.contactName = updatedEventContact;
////            // Update other event details as needed
////
////            // Call a method to save the updated event data or update the UI
////            // saveUpdatedEvent(event);
////            // updateEventUI(event);
////        }
////    });
////    builder.setNegativeButton("Cancel", null);
////    builder.show();
}


}








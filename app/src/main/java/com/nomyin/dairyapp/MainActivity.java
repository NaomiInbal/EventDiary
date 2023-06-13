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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

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
    private  Button captureImageButton;
    private String selectedContactName;
    private static final int REQUEST_IMAGE_PICKER = 2;
    private EditText eventNoteInput;
    private ImageView eventImageView;
    private Bitmap eventImageBitmap;
    //private String eventImageUrl;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        setupEvent();
        //TODO read from firebase and display event on the screen - with thread
        //r
        //showEvents();
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
    private void setupEvent(){
        events = new ArrayList();
        listData = new ArrayList<>();
        eventAdapter.events = events;
        eventAdapter.notifyDataSetChanged();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
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
        builder.setMessage("All The Fields Are Required Except Note And Picture");
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
                //TODO now picture is not required so if there is not it can be problem to upload
                boolean isRequiredFields = requiredFields();
          if(isRequiredFields) {
                uploadImageToFirebaseStorage(eventImageBitmap, new UploadCallback() {
                    @Override
                    public void onUploadComplete(boolean success) {
                        if (success) {
                            // Image upload was successful
                            // Continue with the rest of your code here
                        } else {
                            // Image upload failed
                            Toast.makeText(MainActivity.this, "The event did not save! Maybe you are not connect to wifi, please try again", Toast.LENGTH_SHORT).show();
                        }
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
        //checks required fields
        if (!eventName.isEmpty() && !selectedDate.isEmpty()  && !eventContactName.isEmpty()) {
            return true;//If all the required lines are not empty or null
        }
        else return false;
    }
    //---------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------
    //FireBase & FireStore
    //-------------------------------------------------------------------------------------
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
                    // Register a success listener to get the download URL after the upload is complete
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
                                        Log.d("save", "onSuccess: "+ eventImageUrl);
                                        // Save the image URL to Firestore or perform any other desired actions
                                        // saveImageUrlToFirestore(imageUrl);
                                        callback.onUploadComplete(true);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error occurred while getting the download URL
                                        // Handle the error as needed
                                        Log.d("save", "fai: "+ eventImageUrl);

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
                }
            }
        });

        thread.start();
    }
    }
//--------------------------------------------------------------------------------------------------


//        private void saveEventWithImage(String imageUrl) {
//            // Create a new Event object with the necessary data, including the image URL
//            MyEvent event = new MyEvent(eventName, eventContactName, eventNote, imageUrl);
//
//            // Add the event to the Firestore "Event" collection
//            eventsCollection.add(event)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            // Event added successfully to Firestore
//                            // Handle the success scenario
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Error occurred while adding the event to Firestore
//                            // Handle the failure scenario
//                        }
//                    });
//        }
//    }





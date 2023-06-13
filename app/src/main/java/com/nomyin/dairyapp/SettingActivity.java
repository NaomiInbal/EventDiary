package com.nomyin.dairyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class SettingActivity extends AppCompatActivity {
private int selectedInx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SharedPreferences sp = getSharedPreferences("SettingsPref", Context.MODE_PRIVATE);
        //save the settings in SP
//TODO replace the 3 with int outside the on creat that initialise first time and check if it  work well with sp when we close and then open the app
        int selectIdx = sp.getInt("set1", 3);
        String time = sp.getString("set2", "0:00");
        TextView timePickerTxt = findViewById(R.id.txtTimepickerID);
        timePickerTxt.setText(time);
        RadioGroup radioGroup = findViewById(R.id.groupDividerID);

        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(selectIdx);
        if (radioButton != null) {
            radioButton.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton r = findViewById(i);
                selectedInx = radioGroup.indexOfChild(r);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("set1", selectedInx);
                editor.apply();

            }
        });
        //setting 2 - time picker
        findViewById(R.id.btnTimepickerID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    //save the time in SP
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = "" + selectedHour + ":" + selectedMinute;
                        timePickerTxt.setText(time);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("set2", time);
                        editor.apply();
                        AlarmByTime.setAlarm(time, SettingActivity.this);

                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


    }


}
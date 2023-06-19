package com.nomyin.dairyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;


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

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            RadioButton r = findViewById(i);
            selectedInx = radioGroup1.indexOfChild(r);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("set1", selectedInx);
            editor.apply();

        });
        //setting 2 - time picker
        findViewById(R.id.btnTimepickerID).setOnClickListener(view -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            //save the time in SP
            mTimePicker = new TimePickerDialog(SettingActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                String time1 = "" + selectedHour + ":" + selectedMinute;
                timePickerTxt.setText(time1);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("set2", time1);
                editor.apply();
              //  AlarmByTime.setAlarm(time1, SettingActivity.this);

            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });


    }


}
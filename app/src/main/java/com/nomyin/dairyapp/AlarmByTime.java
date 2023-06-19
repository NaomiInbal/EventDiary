package com.nomyin.dairyapp;

//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//
//public class AlarmByTime {
//   public static void setAlarm(String time, Context context){
//        AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent i=new Intent(context, ReceiverDateChanged.class);
//        PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);
//
//        Calendar time1 = Calendar.getInstance();
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
////        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
//        try {
////            time1.setTime(sdf.parse(time));// all done
//            time1.setTime(sdf.parse("2023-06-04 19:43"));// all done
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//
////                        time1.setTimeInMillis(System.currentTimeMillis());
//        //time1.add(Calendar.SECOND, 3);
////                        time1.add(Calendar.SECOND);
//        mgr.set(AlarmManager.RTC_WAKEUP, time1.getTimeInMillis(),
//                pi);
////                        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), 500, pi);
//
//
//    }
//}
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmByTime {
    public static void setAlarm(MyEvent event, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReceiverDateChanged.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        try {
            Date alarmTime = sdf.parse(event.eventDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alarmTime);

            // Set the alarm using the specified date and time
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

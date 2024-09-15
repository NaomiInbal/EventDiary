package com.nomyin.dairyapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import java.util.ArrayList;


public class EventAdapter extends BaseAdapter {

    private final Activity activity;
    private EventClickListener eventClickListener;
    private EventEditClickListener editClickListener;


    EventAdapter(Activity activity) {
        this.activity = activity;
    }

    ArrayList<MyEvent> events = new ArrayList<>();

    // override other abstract methods here

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup container) {

            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            MyEvent event = events.get(position);
            ((TextView) convertView.findViewById(R.id.eventName)).setText(event.eventName);
        ((TextView) convertView.findViewById(R.id.eventDate)).setText(event.eventDate);
//            convertView.findViewById(R.id.btnDetails).setOnClickListener(v -> {
//                events.remove(position);
//                notifyDataSetChanged();
////
        //handle the detail button for specific event
        convertView.findViewById(R.id.btnDetails).setOnClickListener(v -> {
            if (eventClickListener != null) {
                eventClickListener.onEventClick(event);
            }
        });
        //handle the edit button for specific event
       convertView.findViewById(R.id.btnEdit).setOnClickListener(v -> {
           if (editClickListener != null) {
               editClickListener.onEventEditClick(event);
           }
       });

        return convertView;
    }
//--------------------------------------------------------------------------------------------------------
//    private void showDialog(MyEvent event) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setTitle("Event Details");
//        builder.setMessage("Event Name: " + event.eventName + "\n Contact: "+ event.contactName + "\n Date: " + event.eventDate +"\n Notes: "+ event.eventNote + "\n Picture:" + event.eventImageUrl);
//        builder.setPositiveButton("OK", null);
//        builder.show();
//    }
    //--------------------------------------------------------------------
//handle the detail button clicked
public interface EventClickListener {
    void onEventClick(MyEvent event);
}
    public void setEventDetailsClickListener(EventClickListener eventClickListener) {
        this.eventClickListener = eventClickListener;
    }
//-----------------------------------------------------------------------------------
//handle the edit button clicked
public interface EventEditClickListener {
    void onEventEditClick(MyEvent event);
}
    public void setEventEditClickListener(EventEditClickListener listener) {
        this.editClickListener = listener;
    }
//-------------------------------------------------------------------------------------
}


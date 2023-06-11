package com.nomyin.dairyapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {

    private final Activity activity;

    EventAdapter(Activity activity) {
        this.activity = activity;
    }

    ArrayList<MyEvent> events = new ArrayList();

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

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_item, null);

            MyEvent event = events.get(position);
            ((TextView) convertView.findViewById(R.id.eventName)).setText(event.eventName);
            convertView.findViewById(R.id.btnDetails).setOnClickListener(v -> {
                events.remove(position);
                notifyDataSetChanged();
            });
        return convertView;
    }
}


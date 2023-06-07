package com.nomyin.dairyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> listData;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        listView = findViewById(R.id.listID);
        listData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Contacts.this, "onItemClick on pos: " + position, Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listData.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

//            Button btn = findViewById(R.id.btnLoadID);
//            btn.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    loadContacts();
//                }
//            });
//            loadContacts();
//        }
//
//        private void loadContacts()
//        {
//            listData.clear();
//
//            ContentResolver resolver = getContentResolver();
//
//            Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;  // contacts table uri
//            //String[] fields = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
//            //String sortOreder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"; // DESC(DOWN order) / ASC(UP order)
//            Cursor cursor = resolver.query(contactsUri,null,null,null, null); // SELECT * FROM Contacts
//
//            if(cursor != null && cursor.moveToFirst())
//            {
//                do
//                {
//                    int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//                    String name = cursor.getString(nameIndex);
//
//                    int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                    String num = cursor.getString(phoneIndex);
//
//                    Log.d("mylog", name + " " + num);
//                    listData.add(name + " " + num);
//                    adapter.notifyDataSetChanged();
//
//                } while (cursor.moveToNext());
//
//            }
//            else
//            {
//                Toast.makeText(this, "EMPTY CONTACTS LIST", Toast.LENGTH_LONG).show();
//            }
//        }
    }
}

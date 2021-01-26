package com.bytes18.example.salesview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Fragment2 extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "Fragment2";
    public static ArrayList<Frag2ItemList> itemList;
    public ListView listView;

    public void updateItemList(String date){

        SQLiteDatabase DataBase = getContext().openOrCreateDatabase("DataBase", Context.MODE_PRIVATE, null);

        Cursor cursor = DataBase.rawQuery("SELECT * FROM History WHERE date = '"+date+"'", null);
        int customerNameIndex = cursor.getColumnIndex("customerName");
        int customerContactIndex = cursor.getColumnIndex("customerContact");
        int timestampIndex = cursor.getColumnIndex("time");
        int billNumberIndex = cursor.getColumnIndex("billNumber");

        cursor.moveToFirst();

        String customerName;
        String customerContact;
        String timestamp;
        String billNumber;
        String currentBillNumber = "-1";

        itemList = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            customerName = cursor.getString(customerNameIndex).replaceAll("\"", "");
            customerContact = cursor.getString(customerContactIndex).replaceAll("\"", "");
            timestamp = cursor.getString(timestampIndex).replaceAll("\"", "");
            billNumber = cursor.getString(billNumberIndex).replaceAll("\"", "");

            if (!currentBillNumber.equals(billNumber)) {
                Frag2ItemList item = new Frag2ItemList(customerName, customerContact, timestamp, billNumber);
                itemList.add(item);
                currentBillNumber = billNumber;
            }
            cursor.moveToNext();
        }

        cursor.close();

        Collections.reverse(itemList);

        listView = getView().findViewById(R.id.listView);

        Frag2ItemListAdapter adapter = new Frag2ItemListAdapter(getActivity().getApplicationContext(), R.layout.frag2_item_list_layout, itemList);

        listView.setAdapter(adapter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "on create: Started." );

        Date dateToday = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String stringDateToday = dateFormat.format(dateToday);

        updateItemList(stringDateToday);
        
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

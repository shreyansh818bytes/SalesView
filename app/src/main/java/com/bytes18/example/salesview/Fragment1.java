package com.bytes18.example.salesview;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

public class Fragment1 extends Fragment {

    private static final String TAG = "Fragment1";
    public static ArrayList<Frag1ItemList> itemList;
    public ListView listView;

    public void updateItemList(String date){

        try {

            SQLiteDatabase DataBase = getContext().openOrCreateDatabase("DataBase", Context.MODE_PRIVATE, null);

            // DataBase order: History (billNumber VARCHAR, barcode VARCHAR, quantity VARCHAR, customerName VARCHAR, customerContact VARCHAR, customerEmail VARCHAR, timestamp VARCHAR)

            Cursor cursor = DataBase.rawQuery("SELECT * FROM History WHERE date = '" + date + "'", null);
            int billNumberIndex = cursor.getColumnIndex("billNumber");
            int timestampIndex = cursor.getColumnIndex("time");

            cursor.moveToFirst();

            String billNumber;
            String timestamp;

            itemList = new ArrayList<>();

            String prevBillNumber = "-1";

            int dateTotal = 0;

            while (!cursor.isAfterLast()) {

                billNumber = cursor.getString(billNumberIndex);
                if (!prevBillNumber.equals(billNumber)) {
                    timestamp = cursor.getString(timestampIndex).replaceAll("\"", "");

                    String sql = "SELECT * FROM History WHERE billNumber = '" + billNumber + "'";
                    Cursor cursor2 = DataBase.rawQuery(sql, null);
                    int barcodeIndex = cursor2.getColumnIndex("barcode");
                    int quantityIndex = cursor2.getColumnIndex("quantity");

                    cursor2.moveToFirst();

                    String barcode;

                    ArrayList<String> itemName = new ArrayList<>();
                    ArrayList<String> itemCost = new ArrayList<>();
                    ArrayList<String> itemQuantity = new ArrayList<>();

                    while (!cursor2.isAfterLast()) {
                        barcode = cursor2.getString(barcodeIndex);

                        Cursor cursor1 = DataBase.rawQuery("SELECT * FROM Stocklist WHERE barcode=" + barcode + "", null);
                        int nameIndex = cursor1.getColumnIndex("name");
                        int priceIndex = cursor1.getColumnIndex("price");

                        cursor1.moveToFirst();
                        int price = Integer.parseInt(cursor1.getString(priceIndex).replace("\"", ""));
                        int cost = price * Integer.parseInt(cursor2.getString(quantityIndex).replace("\"", ""));
                        itemName.add(cursor1.getString(nameIndex).replace("\"", "").replace("\\", ""));
                        itemCost.add(Integer.toString(cost));
                        itemQuantity.add(cursor2.getString(quantityIndex).replace("\"", ""));

                        dateTotal += cost;

                        cursor1.close();
                        cursor2.moveToNext();
                    }

                    cursor2.close();

                    // order: billNumber-0, itemNameList-1, itemQuantityList-2, itemCostList-3, timestamp-4
                    Frag1ItemList itemDetails = new Frag1ItemList(billNumber.replaceAll("\"", ""), itemName, itemQuantity, itemCost, timestamp);
                    itemList.add(itemDetails);

                }
                prevBillNumber = billNumber;
                cursor.moveToNext();
            }

            cursor.close();

            ArrayList<String> totalNameList = new ArrayList<>();
            totalNameList.add("TOTAL");
            ArrayList<String> totalCostList = new ArrayList<>();
            totalCostList.add(Integer.toString(dateTotal));
            ArrayList<String> totalQuantityList = new ArrayList<>();
            totalQuantityList.add("");
            Frag1ItemList lastTotalItem = new Frag1ItemList("", totalNameList, totalQuantityList, totalCostList, "");

            Collections.reverse(itemList);

            itemList.add(lastTotalItem);

        } catch (IndexOutOfBoundsException exception){
            Toast.makeText(getContext(), "No DATA for this Date", Toast.LENGTH_LONG).show();
        }

        listView = getView().findViewById(R.id.listView);

        Frag1ItemListAdapter adapter = new Frag1ItemListAdapter(getActivity().getApplicationContext(), R.layout.frag1_item_list_layout, itemList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (itemList.get(position).getBillNumber().equals("")){
                    // Start TotalLogsActivity
                    Log.i("Info", itemList.get(position).getItemNameList().toString());

                } else {
                    // Start BillDetailsActivity
                    Intent intent = new Intent(getContext(), BillDetailsActivity.class);
                    intent.putExtra("billNumber", itemList.get(position).getBillNumber().replace("\"", ""));
                    startActivity(intent);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1_layout, container, false);
    }
}

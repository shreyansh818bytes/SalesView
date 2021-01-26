package com.bytes18.example.salesview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class BillDetailsActivity extends AppCompatActivity {

    SQLiteDatabase DataBase;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.refreshMenu) {
            Toast.makeText(this, "History Refreshed", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        Intent intent = getIntent();
        String billNumber = intent.getStringExtra("billNumber");

        Log.i("Info", billNumber);

        DataBase = openOrCreateDatabase("DataBase", Context.MODE_PRIVATE, null);
        Cursor cursor = DataBase.rawQuery("SELECT * FROM History WHERE billNumber= '"+billNumber+"'", null);

        // DataBase order: History (billNumber VARCHAR, barcode VARCHAR, quantity VARCHAR, customerName VARCHAR, customerContact VARCHAR, customerEmail VARCHAR, timestamp VARCHAR)
        int itemBarcodeIndex = cursor.getColumnIndex("barcode");
        int itemQuantityIndex = cursor.getColumnIndex("quantity");
        int itemTimestampIndex = cursor.getColumnIndex("timestamp");
        int customerNameIndex = cursor.getColumnIndex("customerName");
        int customerContactIndex = cursor.getColumnIndex("customerContact");
        int customerEmailIndex = cursor.getColumnIndex("customerEmail");

        cursor.moveToFirst();

        int i = 0;
        while(!cursor.isAfterLast()){
            if (i==0) {
                // Same for all items of same bill
                String timestamp = cursor.getString(itemTimestampIndex).replace("\"", "");
                String customerName = cursor.getString(customerNameIndex).replace("\"", "");
                String customerContact = cursor.getString(customerContactIndex).replace("\"", "");
                String customerEmail = cursor.getString(customerEmailIndex).replace("\"", "");
            }
            i++;

            String barcode = cursor.getString(itemBarcodeIndex).replace("\"", "");
            String quantity = cursor.getString(itemQuantityIndex).replace("\"", "");

            Cursor cursor1 = DataBase.rawQuery("SELECT * FROM Stocklist WHERE barcode= '"+barcode+"'", null);

            // DataBase order: Stocklist (barcode VARCHAR, name VARCHAR, codename VARCHAR, price VARCHAR, stock VARCHAR)
            int itemNameIndex = cursor1.getColumnIndex("name");
            int itemPriceIndex = cursor1.getColumnIndex("price");

            String itemName = cursor1.getString(itemNameIndex);
            String itemPrice = cursor1.getString(itemPriceIndex);

            cursor1.close();

            cursor.moveToNext();
        }

        cursor.close();
    }
}
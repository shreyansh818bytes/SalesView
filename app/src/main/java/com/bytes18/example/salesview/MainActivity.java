package com.bytes18.example.salesview;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bytes18.example.salesview.ui.main.SectionsPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    int year;
    int month;
    int day;
    String date;
    TextView dateTextView;
    String url = "https://sheets.googleapis.com/v4/spreadsheets/1SUx5uyVlO07xmZY88WJGECV3Jb2g40eJ_Qt3XcK0EeI/values/Sheet1!A2:E10000?key=AIzaSyBaFMAkHIWZmYZjECPnKRN9l9IaYPHASw8";
    String url2 = "https://sheets.googleapis.com/v4/spreadsheets/1SUx5uyVlO07xmZY88WJGECV3Jb2g40eJ_Qt3XcK0EeI/values/Sheet2!A3:I10000?key=AIzaSyBaFMAkHIWZmYZjECPnKRN9l9IaYPHASw8";
    public int SHEET_NUMBER = 0;
    SQLiteDatabase DataBase;
    View parentLayout;
    AlertDialog.Builder dialogBuilder;
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    ProgressDialog pd;


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void updateStocklist(String dataJSONString) throws JSONException {

        JSONObject object = new JSONObject(dataJSONString);
        String valuesStringFormatted = object.getString("values").replaceAll("[\\[]+", "");
        valuesStringFormatted = valuesStringFormatted.replaceAll("]]", "");

        String[] rowStringArray = valuesStringFormatted.split("],");
        String[] cellValues;

        DataBase.execSQL("DELETE FROM Stocklist");

        for (String row : rowStringArray) {
            cellValues = row.split(",");
            // Cell Values in order: Barcode, Name, Codename, Price, Stock;

            // FOR UPDATING DATABASE
            // DataBase order: Stocklist (barcode VARCHAR, name VARCHAR, codename VARCHAR, price VARCHAR, stock VARCHAR)
            DataBase.execSQL("INSERT INTO Stocklist (barcode, name, codename, price, stock ) VALUES (" + cellValues[0] + ", " + cellValues[1] + ", " + cellValues[2] + ", " + cellValues[3] + ", " + cellValues[4] + ")");
        }

        SHEET_NUMBER = 2;
        new JsonTask().execute(url2);
    }

    public void updateHistoryList(String dataJSONString) throws JSONException {

        JSONObject object = new JSONObject(dataJSONString);
        String valuesStringFormatted = object.getString("values").replaceAll("[\\[]+", "");
        valuesStringFormatted = valuesStringFormatted.replaceAll("]]", "");

        String[] rowStringArray = valuesStringFormatted.split("],");
        String[] cellValues;

        DataBase.execSQL("DELETE FROM History");

        for (String row : rowStringArray) {
            cellValues = row.split(",");
            // Cell Values in order: Bill No.-0, Date-1, Time-2, Barcode-3, Name-4, Quantity-5, Customer Name-6, Customer Contact-7, Customer EmailID-8;
            String date = cellValues[1].replaceAll("\"", "");
            String time = cellValues[2].replaceAll("\"", "");

                    String customerName;
            String customerContact;
            String customerEmail;
            try {
                customerName = cellValues[6];
                customerContact = cellValues[7];
                customerEmail = cellValues[8];
            } catch (ArrayIndexOutOfBoundsException exception){
                customerName = "NA";
                customerContact = "NA";
                customerEmail = "NA";
            }
            // FOR UPDATING DATABASE
            // DataBase order: History (billNumber VARCHAR, barcode VARCHAR, quantity VARCHAR, customerName VARCHAR, customerContact VARCHAR, customerEmail VARCHAR, timestamp VARCHAR)
            DataBase.execSQL("INSERT INTO History (billNumber, barcode, quantity, customerName, customerContact, customerEmail, date, time) VALUES ('" + cellValues[0] + "', '" + cellValues[3] + "', '" + cellValues[5] + "', '" + customerName + "', '"+ customerContact +"', '" + customerEmail + "', '"+ date +"', '"+ time +"')");
        }


        Date dateToday = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        String stringDateToday = dateFormat.format(dateToday);

        Fragment1 fragment1 = (Fragment1) sectionsPagerAdapter.instantiateItem(viewPager, 0);
        Fragment2 fragment2 = (Fragment2) sectionsPagerAdapter.instantiateItem(viewPager, 1);

        fragment1.updateItemList(stringDateToday);
        fragment2.updateItemList(stringDateToday);

        dateTextView.setText("Today's Sale");

        Toast.makeText(this, "History Refreshed", Toast.LENGTH_SHORT).show();
    }

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

            if (isNetworkAvailable(this)) {
                SHEET_NUMBER = 1;
                new JsonTask().execute(url);
            } else {
                dialogBuilder.setMessage("Stock List and History List not updated.\nPlease connect to Internet to update Stock List and History List.")
                        .setTitle("Offline")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                Snackbar snackbar = Snackbar.make(parentLayout, "No Internet Connection!", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);

        dateTextView = findViewById(R.id.historyDate2);

        final Calendar calendar = Calendar.getInstance();
        final Date currentDate = Calendar.getInstance().getTime();


        // Creating or Opening a sqlite database file
        DataBase = openOrCreateDatabase("DataBase", Context.MODE_PRIVATE, null);

        parentLayout = findViewById(android.R.id.content);

        dialogBuilder = new AlertDialog.Builder(this);

        try {
            // Created History Table
            DataBase.execSQL("CREATE TABLE IF NOT EXISTS History (billNumber VARCHAR, barcode VARCHAR, quantity VARCHAR, customerName VARCHAR, customerContact VARCHAR, customerEmail VARCHAR, date VARCHAR, time VARCHAR)");

            // Created Stocklist Table
            DataBase.execSQL("CREATE TABLE IF NOT EXISTS Stocklist (barcode VARCHAR, name VARCHAR, codename VARCHAR, price VARCHAR, stock VARCHAR)");

        } catch(Exception e) {
            e.printStackTrace();
        }

        if (isNetworkAvailable(this)){
            SHEET_NUMBER = 1;
            new JsonTask().execute(url);
        } else {
            dialogBuilder.setMessage("Stock List and History List not updated.\nPlease connect to Internet to update Stock List and History List.")
                    .setTitle("Offline")
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            Snackbar snackbar = Snackbar.make(parentLayout, "No Internet Connection!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        date = SimpleDateFormat.getDateInstance().format(calendar.getTime());

                        month++;
                        String dateFormated = String.format("%02d-%02d-%02d", dayOfMonth, month, year%2000);

                        if (calendar.getTime().toString().equals(currentDate.toString())){
                            dateTextView.setText("Today's Sale");
                        } else {
                            dateTextView.setText(date + "'s Sale");
                        }

                        Fragment1 fragment1 = (Fragment1) sectionsPagerAdapter.instantiateItem(viewPager, 0);
                        Fragment2 fragment2 = (Fragment2) sectionsPagerAdapter.instantiateItem(viewPager, 1);

                        fragment1.updateItemList(dateFormated);
                        fragment2.updateItemList(dateFormated);

                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pd.isShowing()) {
                pd.show();
            }
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    if (SHEET_NUMBER == 1) {
                        updateStocklist(result);
                    } else if (SHEET_NUMBER == 2){
                        updateHistoryList(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar snackbar = Snackbar.make(parentLayout, "No Data returned from Google Spreadsheet!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            if (pd.isShowing() && SHEET_NUMBER == 2) {
                pd.dismiss();
            }
        }
    }

}
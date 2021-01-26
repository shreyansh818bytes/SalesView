package com.bytes18.example.salesview;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Frag1ItemListAdapter extends ArrayAdapter<Frag1ItemList> {

    private static final String TAG = "ItemListAdapter";

    private Context mContext;
    int mResource;
    ArrayList<Frag1ItemList> objects;


    public Frag1ItemListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Frag1ItemList> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // get the item's variables
        // order: billNumber-0, itemName-1, itemQuantity-2, itemCost-3, timestamp-4
        String billNumber = getItem(position).getBillNumber();
        ArrayList<String> itemNameList = getItem(position).getItemNameList();
        ArrayList<String> itemQuantityList = getItem(position).getItemQuantityList();
        ArrayList<String> itemCostList = getItem(position).getItemCostList();
        String timestamp = getItem(position).getTimestamp();

        //Create the item object with the variables
        Frag1ItemList item = new Frag1ItemList(billNumber, itemNameList, itemQuantityList, itemCostList, timestamp);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        LinearLayout billLinearLayout = convertView.findViewById(R.id.billLinearLayout);
        billLinearLayout.removeAllViews();

        TextView billNumberTextView = convertView.findViewById(R.id.billNumberTextView);
        TextView timestampTextView = convertView.findViewById(R.id.timestampTextView);
        TextView totalTextView = convertView.findViewById(R.id.totalTextView);
        int total = 0;

        for (int i = 0; i < itemNameList.size(); i++){
            View row = inflater.inflate(R.layout.frag1_item_list_row_layout, null);

            TextView nameTV = row.findViewById(R.id.itemNameTextView);
            TextView quantityTV = row.findViewById(R.id.quantityTextView);
            TextView costTV = row.findViewById(R.id.costTextView);

            nameTV.setText(itemNameList.get(i));
            quantityTV.setText(itemQuantityList.get(i));
            costTV.setText(itemCostList.get(i));
            total += Integer.parseInt(itemCostList.get(i));

            if (itemNameList.get(i).equals("TOTAL")){
                billLinearLayout.setBackgroundColor(Color.rgb(255, 213, 200));
                totalTextView.setBackgroundColor(Color.rgb(255, 213, 200));
                totalTextView.setTextColor(Color.rgb(255, 213, 200));
                timestampTextView.setBackgroundColor(Color.rgb(255, 213, 200));
                billNumberTextView.setBackgroundColor(Color.rgb(255, 213, 200));
                convertView.setBackgroundColor(Color.rgb(255, 213, 200));
            }

            billLinearLayout.addView(row);
        }

        billNumberTextView.setText(billNumber);

        timestampTextView.setText(timestamp);

        totalTextView.setText(Integer.toString(total));

        return convertView;
    }
}

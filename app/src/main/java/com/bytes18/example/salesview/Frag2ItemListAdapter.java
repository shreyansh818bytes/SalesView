package com.bytes18.example.salesview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Frag2ItemListAdapter extends ArrayAdapter<Frag2ItemList> {

    private static final String TAG = "ItemListAdapter";

    private Context mContext;
    int mResource;

    public Frag2ItemListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Frag2ItemList> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // get the item's variables
        String name = getItem(position).getName();
        String contact = getItem(position).getContact();
        String timestamp = getItem(position).getTimestamp();
        String billNumber = getItem(position).getBillNumber();

        //Create the item object with the variables
        Frag2ItemList item = new Frag2ItemList(name, contact, timestamp, billNumber);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.name);
        TextView tvContact = convertView.findViewById(R.id.contact);
        TextView tvTimestamp = convertView.findViewById(R.id.frag2Timestamp);
        TextView tvBillNumber = convertView.findViewById(R.id.frag2BillNumber);

        tvName.setText(name);
        tvContact.setText(contact);
        tvTimestamp.setText(timestamp);
        tvBillNumber.setText(billNumber);

        return convertView;
    }
}

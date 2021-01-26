package com.bytes18.example.salesview;

// order: billNumber-0, itemCodename-1, itemQuantity-2, itemCost-3, timestamp-4

import java.util.ArrayList;

public class Frag1ItemList {

    private String billNumber;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemQuantityList;
    private ArrayList<String> itemCostList;
    private String timestamp;

    public Frag1ItemList(String billNumber, ArrayList<String> itemNameList, ArrayList<String> itemQuantityList, ArrayList<String> itemCostList, String timestamp) {
        this.billNumber = billNumber;
        this.itemNameList = itemNameList;
        this.itemQuantityList = itemQuantityList;
        this.itemCostList = itemCostList;
        this.timestamp = timestamp;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public ArrayList<String> getItemNameList() {
        return itemNameList;
    }

    public void setItemNameList(ArrayList<String> itemNameList) {
        this.itemNameList = itemNameList;
    }

    public ArrayList<String> getItemQuantityList() {
        return itemQuantityList;
    }

    public void setItemQuantityList(ArrayList<String> itemQuantityList) {
        this.itemQuantityList = itemQuantityList;
    }

    public ArrayList<String> getItemCostList() {
        return itemCostList;
    }

    public void setItemCostList(ArrayList<String> itemCostList) {
        this.itemCostList = itemCostList;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

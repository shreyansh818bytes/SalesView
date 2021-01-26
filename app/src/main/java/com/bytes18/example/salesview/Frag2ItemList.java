package com.bytes18.example.salesview;

public class Frag2ItemList {

    private String name;
    private String contact;
    private String timestamp;
    private String billNumber;

    public Frag2ItemList(String name, String contact, String timestamp, String billNumber) {
        this.name = name;
        this.contact = contact;
        this.timestamp = timestamp;
        this.billNumber = billNumber;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

package com.example.spendsmartly;

import android.widget.TextView;

public class Model {
    int id;
    double amount;
    String type;
    String category;
    String desc;
    String date;

    String time;
    TextView msg;

    public Model(int id,double amount, String type, String category, String desc, String date,String time,TextView msg) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.desc = desc;
        this.date = date;
        this.msg = msg;
        this.time = time;
    }
    public int getId(){return id;}
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TextView getMsg(){return msg;}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
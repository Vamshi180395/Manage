package com.example.manage;

/**
 * Created by Rama Vamshi Krishna on 09/24/2017.
 */
public class Record {
    String name, age, phone, picture, date;

    public Record(String age, String date, String name, String phone, String picture) {
        this.age = age;
        this.date = date;
        this.name = name;
        this.phone = phone;
        this.picture = picture;
    }

    public Record() {
    }

    public String getAge() {

        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}

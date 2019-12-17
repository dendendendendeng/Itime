package com.example.itime;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.sql.Date;

public class MyTime implements Serializable {
    String title;
    String tips;
    Date date;
    long repeat;
    byte[] picture;

    public MyTime (){ }

    public MyTime(String title, String tips, Date date,long repeat,byte[] picture) {
        this.title = title;
        this.tips = tips;
        this.date = date;
        this.repeat = repeat;
        this.picture = picture;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getRepeat() {
        return repeat;
    }

    public void setRepeat(long repeat) {
        this.repeat = repeat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

}

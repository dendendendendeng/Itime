package com.example.itime;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MyTime implements Serializable {
    String title;
    String tips;
    long choosedTime;
    Bitmap bitmap;

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

    public long getChoosedTime() {
        return choosedTime;
    }

    public void setChoosedTime(long choosedTime) {
        this.choosedTime = choosedTime;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

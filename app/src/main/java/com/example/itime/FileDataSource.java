package com.example.itime;

import android.content.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileDataSource {
    private Context context;
    private int color = 0;
    private ArrayList<String> Labels = new ArrayList<String>();
    private ArrayList<MyTime> myTimes = new ArrayList<MyTime>();

    public ArrayList<String> getLabels() {
        return Labels;
    }

    public void setLabels(ArrayList<String> labels) {
        Labels = labels;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public FileDataSource(Context context) {
        this.context = context;
    }

    public ArrayList<MyTime> getTimes(){
        return myTimes;
    }

    public void  saveMyTimes(){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Serializable.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(myTimes);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MyTime> loadMyTimes(){
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable.txt")
            );
            myTimes = (ArrayList<MyTime>) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myTimes;
    }

    public void saveColor(){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Color.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(color);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int loadColor() {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Color.txt")
            );
            color = (int) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }


    public ArrayList<String> loadLabels() {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Labels.txt")
            );
            Labels = (ArrayList<String>) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Labels;
    }

    public void saveLabels(){
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Labels.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(Labels);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

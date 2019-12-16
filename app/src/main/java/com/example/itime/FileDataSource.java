package com.example.itime;

import android.content.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileDataSource {
    private Context context;

    public FileDataSource(Context context) {
        this.context = context;
    }

    private ArrayList<MyTime> myTimes = new ArrayList<MyTime>();

    public ArrayList<MyTime> getTimes(){
        return myTimes;
    }

    public void  save(){
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

    public ArrayList<MyTime> load(){

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

}

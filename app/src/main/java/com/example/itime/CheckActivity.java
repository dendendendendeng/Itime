package com.example.itime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CheckActivity extends AppCompatActivity {
    ImageButton button_return;
    ImageButton button_delete;
    ImageButton button_edit;
    ImageView imageView_check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        button_return = (ImageButton) findViewById(R.id.imageButton_return);
        button_delete = (ImageButton) findViewById(R.id.button_delete_check);
        button_edit = (ImageButton) findViewById(R.id.button_edit_check);

        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_return = new Intent(CheckActivity.this,MainActivity.class);
                startActivity(intent_return);
                finish();
            }
        });
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除操作还没完成
            }
        });
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_edit = new Intent(CheckActivity.this,AddActivity.class);
                startActivity(intent_edit);
                finish();
            }
        });

        imageView_check = (ImageView) findViewById(R.id.imageview_check);

    }

    protected class CheckAdapter extends ArrayAdapter {

        public CheckAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
        }
    }
}

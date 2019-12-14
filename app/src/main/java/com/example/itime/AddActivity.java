package com.example.itime;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    private List<Add_item> addItemList = new ArrayList<Add_item>();//保存每一个条项里的内容
    ImageButton button_return;
    ImageButton button_confirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent myItennt = getIntent();//从主页面跳转过来

        button_return = (ImageButton) findViewById(R.id.imageButton_return);
        button_return.setOnClickListener(new View.OnClickListener() {//点击返回主页面，这时不进行数据传递
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddActivity.this,MainActivity.class);
                AddActivity.this.startActivity(intent);
            }
        });

        button_confirm = (ImageButton) findViewById(R.id.imageButton_confirm);
        button_confirm.setOnClickListener(new View.OnClickListener() {//点击返回主页面，这时传递用户填入的数据
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddActivity.this,MainActivity.class);
                AddActivity.this.startActivity(intent);



            }
        });

        initItem();
        ArrayAdapter adapter = new AddAdapter(this,R.layout.list_item_main,addItemList);
        ListView listView = (ListView) findViewById(R.id.add_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Toast.makeText(AddActivity.this,year+" 年"+month+" 月"+dayOfMonth+" 日",Toast.LENGTH_SHORT).show();

                                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Toast.makeText(AddActivity.this,hourOfDay+":"+minute,Toast.LENGTH_SHORT).show();
                                    }
                                },9,4,true);
                                timePickerDialog.show();
                            }
                        },2019,11,25);
                        datePickerDialog.show();
                        break;
                    case 1:
                        Toast.makeText(AddActivity.this,"你点击了第 "+(position+1)+" 个",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Intent intent = new Intent();
                        /* 开启Pictures画面Type设定为image */
                        intent.setType("image/*");
                        /* 使用Intent.ACTION_GET_CONTENT这个Action */
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        /* 取得相片后返回本画面 */
                        startActivityForResult(intent, 1);
                        break;
                    case 3:
                        Toast.makeText(AddActivity.this,"你点击了第 "+(position+1)+" 个",Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//在内存里选择图片用来动态选择背景图片

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //在layout下面放置了一张背景图片imageview，用来动态改变背景
                ImageView imageView = (ImageView) findViewById(R.id.layout_back_image);
                /* 将Bitmap设定到ImageView */
               imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initItem() {
        Add_item date = new Add_item(R.drawable.clock,"日期","长按使用日期计算器","wuuuu");
        addItemList.add(date);
        Add_item setting = new Add_item(R.drawable.cir,"重复设置","无","");
        addItemList.add(setting);
        Add_item pict = new Add_item(R.drawable.picture,"图片","","");
        addItemList.add(pict);
        Add_item tip = new Add_item(R.drawable.tips,"标签","","");
        addItemList.add(tip);
    }
}

package com.example.itime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    private List<Add_item> addItemList = new ArrayList<Add_item>();//保存每一个条项里的内容
    MyTime myTime = new MyTime();
    EditText title;
    EditText tips;
    Bitmap bitmap;
    Bitmap bitmap2;
    Date chooseDate;
    ImageButton button_return;
    ImageButton button_confirm;
    ImageView imageView;
    int Position;
    int temp = 0 ;//用于遍历
    ArrayAdapter adapter;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //在layout下面放置了一张背景图片imageview，用来动态改变背景
        imageView = (ImageView) findViewById(R.id.layout_back_image);
        int color = getIntent().getIntExtra("color",0);
        Log.d("测试传输过来的颜色 ",color+"号");
        if (color!=0){
            imageView.setBackgroundColor(color);
        }

        title =(EditText) findViewById(R.id.editText_add_title);
        tips =(EditText) findViewById(R.id.editText_add_comment);

        button_return = (ImageButton) findViewById(R.id.imageButton_return);
        button_return.setOnClickListener(new View.OnClickListener() {//点击返回主页面，这时不进行数据传递
            @Override
            public void onClick(View v) {
                AddActivity.this.finish();
            }
        });

        button_confirm = (ImageButton) findViewById(R.id.imageButton_confirm);
        button_confirm.setOnClickListener(new View.OnClickListener() {//点击返回主页面，这时传递用户填入的数据
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                myTime.setTitle(title.getText().toString());
                myTime.setTips(tips.getText().toString());
                myTime.setDate(chooseDate);
                bitmap2 = Bitmap.createScaledBitmap(bitmap, 800, 600, true);
                Log.i("wechat", "压缩后图片的大小" + (bitmap2.getByteCount() / 1024) + "KB宽度为"
                        + bitmap2.getWidth() + "高度为" + bitmap2.getHeight());
                myTime.setPicture(bitmapToBytes(bitmap2));
                Bundle bundle = new Bundle();
                bundle.putSerializable("myTime",myTime);
                intent.putExtras(bundle);
                if (Position!=-1){
                    intent.putExtra("check_position",Position);
                    setResult(RESULT_FIRST_USER,intent);
                }else {
                    setResult(RESULT_OK,intent);
                }
                finish();
            }
        });

        initItem();
        adapter = new AddAdapter(this,R.layout.list_item_main,addItemList);
        listView = (ListView) findViewById(R.id.add_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                                Toast.makeText(AddActivity.this,"你选择了"+year+" 年"+month+" 月"+dayOfMonth+" 日",Toast.LENGTH_SHORT).show();

                                TimePickerDialog timePickerDialog = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Toast.makeText(AddActivity.this,"你选择的时间是"+hourOfDay+":"+minute,Toast.LENGTH_SHORT).show();
                                        chooseDate = new Date(year-1900,month,dayOfMonth,hourOfDay,minute,0);
                                    }
                                },9,0,true);
                                timePickerDialog.show();
                            }
                        },2019,11,25);
                        datePickerDialog.show();
                        break;
                    case 1:
                        final String[] how_often = {"无", "每天", "每周", "每月", "每年"};
                        new AlertDialog.Builder(AddActivity.this)
                                .setTitle("周期")
                                .setItems(how_often, null)
                                .show();
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
                        final Dialog dialog;
                        final LayoutInflater inflater=LayoutInflater.from(AddActivity.this);
                        final View myview=inflater.inflate(R.layout.layout_label_add,null);//引用自定义布局
                        AlertDialog.Builder builder=new AlertDialog.Builder(AddActivity.this);
                        builder.setView( myview );
                        dialog=builder.create();//创建对话框
                        dialog.show();//显示对话框
                        final EditText input = myview.findViewById(R.id.editText_label_add);
                        myview.findViewById(R.id.button_label_add_return).setOnClickListener(new View.OnClickListener() {//获取布局里面按钮
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();//点击按钮对话框消失
                            }
                        } );
                        LinearLayout linearLayout = myview.findViewById(R.id.layout_label_check_add);

                        //加载已经存在的标签
                        temp = 0;
                        while (temp<MainActivity.Labels.size()){
                            final Button button_label = new Button(AddActivity.this);
                            button_label.setText(MainActivity.Labels.get(temp));
                            button_label.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            button_label.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myTime.setLabel(button_label.getText().toString());
                                    addItemList.get(3).setSubtext(button_label.getText().toString());
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                            linearLayout.addView(button_label);
                            TextView textView = new TextView(AddActivity.this);
                            textView.setText(" ");
                            textView.setTextSize(20);
                            linearLayout.addView(textView);
                            temp++;
                        }

                        //添加新标签
                        myview.findViewById(R.id.button_label_add_comfirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(input.getText().toString().trim().isEmpty())
                                    Toast.makeText(AddActivity.this, "不能输入空标签", Toast.LENGTH_SHORT).show();
                                else{
                                    boolean alive = true;
                                    for (int t = 0;t<MainActivity.Labels.size();t++){
                                        if (input.getText().toString().equals(MainActivity.Labels.get(t))){
                                            alive = false;
                                        }
                                    }
                                    if (alive == true){
                                        MainActivity.Labels.add(input.getText().toString());
                                    }else {
                                        Toast.makeText(AddActivity.this, "该标签已存在", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            }
                        });
                        break;

                }
            }
        });

        Position = getIntent().getIntExtra("check_position",-1);
        if (Position != -1){
            Bundle bundle = new Bundle();
            bundle = getIntent().getExtras();
            myTime =(MyTime) bundle.getSerializable("myTime");
            title.setText(myTime.getTitle());
            if(myTime.getTips()!=null){
                tips.setText(myTime.getTips());
            }
            bitmap = byteToBitmap(myTime.getPicture());
            chooseDate = myTime.getDate();
            imageView.setImageBitmap(byteToBitmap(myTime.getPicture()));
        }else {
            chooseDate = new Date(System.currentTimeMillis());
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat_man);
        }
    }

    //在内存里选择图片用来动态选择背景图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                Log.i("wechat", "压缩前图片的大小" + (bitmap.getByteCount() / 1024 / 1024)
                        + "M宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());
                /* 将Bitmap设定到ImageView */
               imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //初始化listview内容
    private void initItem() {
        Add_item date = new Add_item(R.drawable.clock,"日期","","");
        addItemList.add(date);
        Add_item setting = new Add_item(R.drawable.cir,"重复设置","无","");
        addItemList.add(setting);
        Add_item pict = new Add_item(R.drawable.picture,"图片","","");
        addItemList.add(pict);
        Add_item tip = new Add_item(R.drawable.tips,"标签","","");
        addItemList.add(tip);
    }

    //将bitmap图片转化为byte数组，便于序列化
    public byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //用来将byte数组转化为bitmap图像
    public Bitmap byteToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public class Add_item {
        private int add_image;
        private String maintext;
        private String subtext;
        private String lefttext;

        public Add_item(int add_image, String maintext, String subtext, String lefttext) {
            this.add_image = add_image;
            this.maintext = maintext;
            this.subtext = subtext;
            this.lefttext = lefttext;
        }

        public int getAdd_image() {
            return add_image;
        }

        public void setAdd_image(int add_image) {
            this.add_image = add_image;
        }

        public String getMaintext() {
            return maintext;
        }

        public void setMaintext(String maintext) {
            this.maintext = maintext;
        }

        public String getSubtext() {
            return subtext;
        }

        public void setSubtext(String subtext) {
            this.subtext = subtext;
        }

        public String getLefttext() {
            return lefttext;
        }

        public void setLefttext(String lefttext) {
            this.lefttext = lefttext;
        }
    }

    public class AddAdapter extends ArrayAdapter {

        private final int resouseId;

        public AddAdapter(@NonNull Context context, int resource, @NonNull List<Add_item> objects) {
            super(context, resource, objects);
            this.resouseId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Add_item add_item = (Add_item) getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resouseId,null);
            ImageView addImage = (ImageView) view.findViewById(R.id.imageView_list_main);
            TextView mainText = (TextView) view.findViewById(R.id.textView_head_list_main);
            TextView subText = (TextView) view.findViewById(R.id.textView_sub_list_main);
            TextView leftText = (TextView) view.findViewById(R.id.textView_left_list_main);
            addImage.setImageResource(add_item.getAdd_image());
            mainText.setText(add_item.getMaintext());
            subText.setText(add_item.getSubtext());
            leftText.setText(add_item.getLefttext());
            return view;
        }
    }
}

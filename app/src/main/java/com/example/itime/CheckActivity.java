package com.example.itime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import static com.example.itime.MainActivity.REQUST_CODE_EDIT_TIME;

public class CheckActivity extends AppCompatActivity {
    List<Check_Item> check_items = new ArrayList<Check_Item>();
    ListView listView;
    ImageButton button_return;
    ImageButton button_delete;
    ImageButton button_edit;
    TextView title;
    TextView date;
    TextView time;
    ImageView imageView_check;
    private int position;
    private MyTime myTime;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        position= getIntent().getIntExtra("position",0);
        myTime = (MyTime) getIntent().getSerializableExtra("myTime");

        button_return = (ImageButton) findViewById(R.id.button_return_check);
        button_delete = (ImageButton) findViewById(R.id.button_delete_check);
        button_edit = (ImageButton) findViewById(R.id.button_edit_check);
        imageView_check = (ImageView) findViewById(R.id.imageview_check);
        title = (TextView) findViewById(R.id.textView_title_check);
        date = (TextView) findViewById(R.id.textView_date_check);
        time = (TextView) findViewById(R.id.textView_time_check);
        Bundle bundle = getIntent().getExtras();
        position = getIntent().getIntExtra("check_position",0);
        myTime = (MyTime) bundle.getSerializable("myTime");
        imageView_check.setImageBitmap(byteToBitmap(myTime.getPicture()));
        title.setText(myTime.getTitle());
        date.setText(SplitDateString(simpleDateFormat.format(myTime.getDate()),"日"));
        CountDownTimer countDownTimer = new CountDownTimer(transformTime(myTime.getDate()),1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time.setText(formatTime(millisUntilFinished));
            }
            //倒计时结束后的操作
            @Override
            public void onFinish() {
                time.setText("");
            }
            //返回格式化的日期和时间
            public String formatTime(long millisecond) {
                long day = (long) ((millisecond)/1000/60/60/24);
                long hour = (long) ((millisecond-(day*24*60*60*1000))/1000/60/60);
                long minute = (long) ((millisecond-(day*24*60*60*1000)-(hour*60*60*1000))/1000/60);
                long second = (long) (((millisecond-(day*24*60*60*1000)-(hour*60*60*1000)-(minute*1000*60))/1000)%60);

                if(day==0){
                    if(hour==0){
                        if (minute==0){
                            return second + "秒";
                        }else {
                            return minute + "分" + second + "秒";
                        }
                    }else {
                        return hour+"时" + minute + "分" + second + "秒";
                    }
                }else {
                    return day + "天" + hour + "时" + minute + "分" + second + "秒";
                }
            }
        };
        countDownTimer.start();

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CheckActivity.this)
                        .setIcon(R.drawable.alarm)
                        .setTitle("询问")
                        .setMessage("你确定要删除这个事项吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //这里应该传递一个intent回去，在主页面进行操作，对myTimes进行删除更新
                                Intent intent = new Intent(CheckActivity.this,MainActivity.class);
                                intent.putExtra("delete",position);
                                setResult(RESULT_CANCELED,intent);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create().show();
            }
        });

        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_return = new Intent(CheckActivity.this,MainActivity.class);
                Bundle bundle = new Bundle();
                intent_return.putExtra("check_position",position);
                bundle.putSerializable("myTime",myTime);
                Log.d("测试修改后传出去主页面的数据",myTime.getTitle());
                intent_return.putExtras(bundle);
                setResult(RESULT_FIRST_USER,intent_return);
                finish();
            }
        });

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CheckActivity.this,AddActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("check_position",position);
                bundle.putSerializable("myTime",myTime);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUST_CODE_EDIT_TIME);
            }
        });

        initItem();
        listView = (ListView) findViewById(R.id.listView_item_check);
        ArrayAdapter arrayAdapter = new CheckAdapter(this,R.layout.list_item_check,check_items);
        listView.setAdapter(arrayAdapter);

        //倒计时
        countDownTimer = new CountDownTimer(transformTime(myTime.getDate()),1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                time.setText("00:00");
            }
            //返回格式化的日期和时间
            public String formatTime(long millisecond) {
                long day = (long) ((millisecond)/1000/60/60/24);
                long hour = (long) ((millisecond-(day*24*60*60*1000))/1000/60/60);
                long minute = (long) ((millisecond-(day*24*60*60*1000)-(hour*60*60*1000))/1000/60);
                long second = (long) (((millisecond-(day*24*60*60*1000)-(hour*60*60*1000)-(minute*1000*60))/1000)%60);

                if(day==0){
                    if(hour==0){
                        if (minute==0){
                            return second + "秒";
                        }else {
                            return minute + "分" + second + "秒";
                        }
                    }else {
                        return hour+"时" + minute + "分" + second + "秒";
                    }
                }else {
                    return day + "天" + hour + "时" + minute + "分" + second + "秒";
                }
            }
        };
    }

    private void initItem() {
        Check_Item inform = new Check_Item(R.drawable.inform,"通知栏");
        check_items.add(inform);
        Check_Item date = new Check_Item(R.drawable.date,"在日历中显示");
        check_items.add(date);
        Check_Item quick = new Check_Item(R.drawable.list,"快捷图标");
        check_items.add(quick);
        Check_Item window = new Check_Item(R.drawable.window,"悬浮窗口");
        check_items.add(window);
    }

    //规定了日期的格式
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");

    //截取字符串
    public String SplitDateString (String time,String split){
        StringTokenizer tokenizer = new StringTokenizer(time,split);
        while (tokenizer!=null){
            return tokenizer.nextToken()+"日";
        }
        return "";
    }

    //用来将byte数组转化为bitmap图像
    public Bitmap byteToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    //将时间字符串转化为long型数据
    public long transformTime(java.util.Date chooseTime) {
        /* 当前系统时间*/
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        long result = chooseTime.getTime() - date.getTime();
        return result;
    }

    protected class CheckAdapter extends ArrayAdapter {
        int resouseId;

        public CheckAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            this.resouseId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Check_Item check_item = (Check_Item) getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resouseId,null);
            ImageView checkImage = (ImageView) view.findViewById(R.id.imageView_list_check);
            TextView checkText = (TextView) view.findViewById(R.id.textView_list_chek);
            checkImage.setImageResource(check_item.getPicture());
            checkText.setText(check_item.getTitle());
            return view;
        }
    }

    public class Check_Item{
        int picture;
        String title;

        public Check_Item() { }

        public Check_Item(int picture, String title) {
            this.picture = picture;
            this.title = title;
        }

        public int getPicture() {
            return picture;
        }

        public void setPicture(int picture) {
            this.picture = picture;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_FIRST_USER){
            Bundle bundle = data.getExtras();
            position = data.getIntExtra("check_position",0);
            myTime = (MyTime) bundle.getSerializable("myTime");
            imageView_check.setImageBitmap(byteToBitmap(myTime.getPicture()));
            title.setText(myTime.getTitle());
            date.setText(SplitDateString(simpleDateFormat.format(myTime.getDate()),"日"));
            CountDownTimer countDownTimer = new CountDownTimer(transformTime(myTime.getDate()),1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    time.setText(formatTime(millisUntilFinished));
                }
                //倒计时结束后的操作
                @Override
                public void onFinish() {
                    time.setText("");
                }
                //返回格式化的日期和时间
                public String formatTime(long millisecond) {
                    long day = (long) ((millisecond)/1000/60/60/24);
                    long hour = (long) ((millisecond-(day*24*60*60*1000))/1000/60/60);
                    long minute = (long) ((millisecond-(day*24*60*60*1000)-(hour*60*60*1000))/1000/60);
                    long second = (long) (((millisecond-(day*24*60*60*1000)-(hour*60*60*1000)-(minute*1000*60))/1000)%60);

                    if(day==0){
                        if(hour==0){
                            if (minute==0){
                                return second + "秒";
                            }else {
                                return minute + "分" + second + "秒";
                            }
                        }else {
                            return hour+"时" + minute + "分" + second + "秒";
                        }
                    }else {
                        return day + "天" + hour + "时" + minute + "分" + second + "秒";
                    }
                }
            };
            countDownTimer.start();
        }
    }
}

package com.example.itime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CheckActivity extends AppCompatActivity {
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
        position= getIntent().getIntExtra("position",0);
        myTime = (MyTime) getIntent().getSerializableExtra("myTime");

        button_return = (ImageButton) findViewById(R.id.imageButton_return);
        button_delete = (ImageButton) findViewById(R.id.button_delete_check);
        button_edit = (ImageButton) findViewById(R.id.button_edit_check);
        imageView_check = (ImageView) findViewById(R.id.imageview_check);
        title = (TextView) findViewById(R.id.textView_title_check);
        date = (TextView) findViewById(R.id.textView_date_check);
        time = (TextView) findViewById(R.id.textView_time_check);
        title.setText(myTime.getTitle());
        date.setText(simpleDateFormat.format(myTime.getBitmap()));
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

    }

    //规定了日期的格式
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    public long transformTime(Date chooseTime) {
        /* 当前系统时间*/
        Date date = new Date(System.currentTimeMillis());
        String time1 = simpleDateFormat.format(date);
        String time2 = simpleDateFormat.format(chooseTime);

        /*计算时间差*/
        Date begin = null;
        try {
            begin = (Date) simpleDateFormat.parse(time1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date end = null;
        try {
            end = (Date) simpleDateFormat.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = end.getTime() - begin.getTime();
        /*计算天数*/
        long days = diff / (1000 * 60 * 60 * 24);
        /*计算小时*/
        long hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        /*计算分钟*/
        long minutes = (diff % (1000 * 60 * 60)) / (1000 * 60);
        /*计算秒*/
        long seconds = (diff % (1000 * 60)) / 1000;
        return diff;
    }

    protected class CheckAdapter extends ArrayAdapter {

        public CheckAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);

        }
    }

}

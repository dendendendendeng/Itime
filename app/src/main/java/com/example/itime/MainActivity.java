package com.example.itime;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    TextView textView_time;
    CountDownTimer downTimer;
    ViewPager viewPager;
    private ArrayList<MyTime> myTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //右下角按钮点击事件
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,AddActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawer,toolbar,R.string.nav_app_bar_open_drawer_description,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        /*
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
         */

        textView_time = (TextView) findViewById(R.id.textView_time2);

        //显示倒计时
        downTimer = new CountDownTimer(14*30*24*60*60*1000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                textView_time.setText(formatTime(millisUntilFinished));
            }

            //倒计时结束后的操作
            @Override
            public void onFinish() {
                textView_time.setText("");
            }

            //返回格式化的日期和时间
            public String formatTime(long millisecond) {
                long day;
                long hour;
                long minute;
                long second;

                //获取系统当前时间
                long time=System.currentTimeMillis();

                /*
                int year;
                int month;
                year = (int) ((millisecond/1000/60/60/24)/365);
                month =  (int) (((millisecond-(365*year*24*60*60*1000))/1000/60/60/24)/30);
                day = (int) ((millisecond-(365*year*24*60*60*1000)-(30*month*24*60*60*1000))/1000/60/60/24);
                hour = (int) ((millisecond-(365*year*24*60*60*1000)-(30*month*24*60*60*1000)-(day*24*60*60*1000))/1000/60/60);
                minute = (int) ((millisecond-(365*year*24*60*60*1000)-(30*month*24*60*60*1000)-(day*24*60*60*1000)-(hour*60*60*1000))/1000/60);
                second = (int) (((millisecond-(365*year*24*60*60*1000)-(30*month*24*60*60*1000)-(day*24*60*60*1000)-(hour*60*60*1000)-(minute*1000*60))/1000)%60);

                  if (year==0){
                    if (month==0){
                        if (day==0){
                            if (hour==0){
                                if (minute==0){
                                    return second + "秒";
                                }else {
                                    return minute + "分" +second + "秒";
                                }
                            }else {
                                return hour + "时" + minute + "分" +second + "秒";
                            }
                        }else {
                            return day + "天" + hour + "时" + minute + "分" +second + "秒";
                        }
                    }else {
                        return month + "月" + day + "天" + hour + "时" + minute + "分" +second + "秒";
                    }
                }else {
                    return year + "年" + month + "月" + day + "天" + hour + "时" + minute + "分" +second + "秒";
                }
                 */

                day = (long) ((millisecond)/1000/60/60/24);
                hour = (long) ((millisecond-(day*24*60*60*1000))/1000/60/60);
                minute = (long) ((millisecond-(day*24*60*60*1000)-(hour*60*60*1000))/1000/60);
                second = (long) (((millisecond-(day*24*60*60*1000)-(hour*60*60*1000)-(minute*1000*60))/1000)%60);

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

        downTimer.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //String title = data.getIntent().getSerializableExtra("test");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem menuItem){
        return false;
    }


}

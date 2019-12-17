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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUST_CODE_NEW_TIME = 901;
    public static final int REQUST_CODE_EDIT_TIME = 902;
    private AppBarConfiguration mAppBarConfiguration;
    TextView textView_time;//用于测试，后期需要删除
    TextView textView_test;
    ImageView imageView_test;
    CountDownTimer downTimer;
    ViewPager viewPager;
    ListView listView;
    private ArrayList<MyTime> myTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView_test = (TextView) findViewById(R.id.textView_test_intent);
        imageView_test = (ImageView) findViewById(R.id.image_test_intent);

        //右下角按钮点击事件,这时的添加默认是添加到最低行，所以没有传输位置信息过去
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,AddActivity.class);
                MainActivity.this.startActivityForResult(intent,REQUST_CODE_NEW_TIME);
            }
        });

        listView = findViewById(R.id.list_view_main);
        //listview点击事件，这时是对特定行进行修改，必须传行的序号过去进行标识
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTime myTime = myTimes.get(position);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,CheckActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("myTime",myTime);
                MainActivity.this.startActivityForResult(intent,REQUST_CODE_EDIT_TIME);
            }
        });

        //这部分不知道干啥的。。。
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

        downTimer.start();
    }

    //获取传回来的数据对页面进行更新
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUST_CODE_NEW_TIME:
                if (resultCode==RESULT_OK){
                    //int position=data.getIntExtra("edit_position",0);

                    Toast.makeText(this,"回到主页面",Toast.LENGTH_SHORT).show();
                    Bundle bundle =  data.getExtras();
                    MyTime myTime = (MyTime)bundle.getSerializable("myTime");
                    myTimes.add(myTime);
                    textView_test.setText(myTime.getTitle());
                    imageView_test.setImageBitmap(myTime.getBitmap());
                    //theAdaper.notifyDataSetChanged();//适配器实时更新
                }
                break;
            case REQUST_CODE_EDIT_TIME:
                if (resultCode==RESULT_OK){
                    MyTime backTime = (MyTime) data.getSerializableExtra("test");

                    //int position=data.getIntExtra("edit_position",0);
                    //MyTime myTime = myTimes.get(position);
                    //myTime.setTitle(backTime.getTitle());
                    //myTime.setTips(backTime.getTips());
                    //myTime.setBitmap(backTime.getBitmap());
                    //theAdaper.notifyDataSetChanged();//适配器实时更新
                }
                break;
        }


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

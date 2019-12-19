package com.example.itime;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUST_CODE_NEW_TIME = 901;
    public static final int REQUST_CODE_EDIT_TIME = 902;
    private AppBarConfiguration mAppBarConfiguration;
    ViewPager viewPager;
    ArrayList<View> arrayList_view = new ArrayList<View>();
    ListView listView;
    FileDataSource fileDataSource;
    private ArrayList<MyTime> myTimes;
    MyViewPagerAdapter myViewPageradapter;
    MainListAdapter mainListAdapter;
    private int item_init = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitData();

        listView = (ListView) findViewById(R.id.list_view_main);
        mainListAdapter = new MainListAdapter(this,R.layout.list_item_main,myTimes);
        listView.setAdapter(mainListAdapter);

        Init();
        viewPager=findViewById(R.id.view_pager_main);
        myViewPageradapter = new MyViewPagerAdapter(arrayList_view);
        viewPager.setAdapter(myViewPageradapter);
        myViewPageradapter.notifyDataSetChanged();

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

        //listview点击事件，这时是对特定行进行修改，必须传行的序号过去进行标识
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyTime myTime = myTimes.get(position);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,CheckActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("check_position",position);
                bundle.putSerializable("myTime",myTime);
                intent.putExtras(bundle);
                MainActivity.this.startActivityForResult(intent,REQUST_CODE_EDIT_TIME);//注意这里
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
                    Bundle bundle =  data.getExtras();
                    MyTime myTime = (MyTime)bundle.getSerializable("myTime");
                    myTimes.add(myTime);
                    myViewPageradapter.notifyDataSetChanged();
                    mainListAdapter.notifyDataSetChanged();
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

    //不知道干嘛的
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //不知道干嘛的
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //也不知道干嘛的
    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem menuItem){
        return false;
    }

    //将时间字符串转化为long型数据
    public long transformTime(java.util.Date chooseTime) {
        /* 当前系统时间*/
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        long result = chooseTime.getTime() - date.getTime();
        return result;
    }

    //用来将byte数组转化为bitmap图像
    public Bitmap byteToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    //用来将bitmap图像转化为byte数组
    public byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    //重写viewpager的适配器
    private class MyViewPagerAdapter extends PagerAdapter{
        private ArrayList<View> list;
        public MyViewPagerAdapter(){}
        public MyViewPagerAdapter(ArrayList<View> list){
                this.list = list;
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view ==(View)object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(list.get(position));
                return list.get(position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                if(list.size()!=0){
                    View imageView=list.get(position%list.size());
                    container.removeView(imageView);
                }
                else{
                    container.removeViewAt(0);
                }
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }

            @Override
            public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
                ((ViewPager)container).removeView(list.get(position));
            }
        }

    //初始化viewpager
    private void Init(){
            if(myTimes.size()!=0)
                while(item_init<myTimes.size())
                {
                    LayoutInflater mInflater = LayoutInflater.from(this);
                    View item = mInflater.inflate(R.layout.viewpager_layout, null);
                    ImageView background = item.findViewById(R.id.imageView_back_main);
                    TextView title_pager = item.findViewById(R.id.textView_title_main);
                    TextView date_pager = item.findViewById(R.id.textView_date_main);
                    final TextView time_pager = item.findViewById(R.id.textView_time_main);
                    background.setImageBitmap(byteToBitmap(myTimes.get(item_init).getPicture()));
                    title_pager.setText(myTimes.get(item_init).getTitle());
                    date_pager.setText(SplitDateString(simpleDateFormat.format(myTimes.get(item_init).getDate()),"日"));
                    CountDownTimer countDownTimer = new CountDownTimer(transformTime(myTimes.get(item_init).getDate()),1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            time_pager.setText(formatTime(millisUntilFinished));
                        }
                        //倒计时结束后的操作
                        @Override
                        public void onFinish() {
                            time_pager.setText("");
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
                    arrayList_view.add(item);
                    item_init++;
                }
        }

    public class MainListAdapter extends ArrayAdapter {

        private final int resouseId;

        public MainListAdapter(@NonNull Context context, int resource, @NonNull List<MyTime> objects) {
            super(context, resource, objects);
            this.resouseId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            MyTime myTime = (MyTime) getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resouseId,null);
            ImageView addImage = (ImageView) view.findViewById(R.id.imageView_list_main);
            TextView mainText = (TextView) view.findViewById(R.id.textView_head_list_main);
            TextView subText = (TextView) view.findViewById(R.id.textView_sub_list_main);
            final TextView leftText = (TextView) view.findViewById(R.id.textView_left_list_main);
            addImage.setImageBitmap(byteToBitmap(myTime.getPicture()));
            mainText.setText(myTime.getTitle());
            subText.setText(SplitDateString(simpleDateFormat.format(myTime.getDate()),"日"));//对选择的日期时间进行截取，只要年月日
            CountDownTimer countDownTimerlist = new CountDownTimer(transformTime(myTime.getDate()),1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    leftText.setText("还剩"+SplitDateString(formatTime(millisUntilFinished),"天"));
                }
                //倒计时结束后的操作
                @Override
                public void onFinish() {
                    leftText.setText("");
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
            countDownTimerlist.start();
            return view;
        }
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");

    //截取字符串
    public String SplitDateString (String time,String split){
        StringTokenizer tokenizer = new StringTokenizer(time,split);
        while (tokenizer!=null){
            return tokenizer.nextToken()+"日";
        }
        return "";
    }

        //保存数据
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        fileDataSource.save();
    }

    //保存数据
    @Override
    protected void onPause() {
        super.onPause();
        fileDataSource.save();
    }

    //打开app后获取之前的数据
    private void InitData() {
        fileDataSource=new FileDataSource(this);
        myTimes=fileDataSource.load();
    }
}

package com.example.itime;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private ArrayList<MyTime> myTimes = new ArrayList<MyTime>();
    public static ArrayList<String> Labels = new ArrayList<String>();
    MyViewPagerAdapter myViewPageradapter;
    MainListAdapter mainListAdapter;
    ColorInt color = new ColorInt(0);
    DrawerLayout drawer;
    FloatingActionButton fab;
    Toolbar toolbar;
    NavigationView navigationView;
    int ItemID = 1;
    int temp = 0;//用于遍历

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitData();

        listView = (ListView) findViewById(R.id.list_view_main);
        mainListAdapter = new MainListAdapter(this,R.layout.list_item_main,myTimes);
        listView.setAdapter(mainListAdapter);

        InitViewPager();
        viewPager=findViewById(R.id.view_pager_main);
        myViewPageradapter = new MyViewPagerAdapter(arrayList_view);
        viewPager.setAdapter(myViewPageradapter);

        //右下角按钮点击事件,这时的添加默认是添加到最低行，所以没有传输位置信息过去
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("color",color.getColor());
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
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawer,toolbar,R.string.nav_app_bar_open_drawer_description,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        navigationView.getMenu().getItem(1).setOnMenuItemClickListener(new AddLableListenr());
        navigationView.getMenu().getItem(2).setOnMenuItemClickListener(new DeleteLabelListenter());
        navigationView.getMenu().getItem(3).setOnMenuItemClickListener(new ShowColorBarListener());


        if (color.getColor()!= 0){
            toolbar.setBackgroundColor(color.getColor());
            fab.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));
        }
    }

    //获取传回来的数据对页面进行更新
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode)
            {
                case REQUST_CODE_NEW_TIME:
                        //int position=data.getIntExtra("edit_position",0);
                        Bundle bundle =  data.getExtras();
                        MyTime myTime = (MyTime)bundle.getSerializable("myTime");
                        myTimes.add(myTime);
                        InitViewPager();
                        myViewPageradapter.notifyDataSetChanged();
                        mainListAdapter.notifyDataSetChanged();
                    break;
            }
        }else  if (resultCode == RESULT_CANCELED){
            switch (requestCode){
                case REQUST_CODE_EDIT_TIME:
                    int Position = data.getIntExtra("delete",-1);
                    if (Position != -1){
                        myTimes.remove(Position);
                        InitViewPager();
                        mainListAdapter.notifyDataSetChanged();
                        myViewPageradapter.notifyDataSetChanged();
                    }
            }
        }else if (resultCode == RESULT_FIRST_USER){
            switch (requestCode){
                case REQUST_CODE_EDIT_TIME:
                    //传回修改的数据对页面信息更新
                    int Position = data.getIntExtra("check_position",-1);
                    Bundle bundle = data.getExtras();
                    MyTime myTime = (MyTime)bundle.getSerializable("myTime");
                    Log.d("测试修改后传回来主页面的数据",myTime.getTitle());
                    myTimes.get(Position).setDate(myTime.getDate());
                    myTimes.get(Position).setTitle(myTime.getTitle());
                    myTimes.get(Position).setTips(myTime.getTips());
                    myTimes.get(Position).setPicture(myTime.getPicture());
                    myTimes.get(Position).setRepeat(myTime.getRepeat());
                    InitViewPager();
                    myViewPageradapter.notifyDataSetChanged();
                    mainListAdapter.notifyDataSetChanged();
                    break;
            }
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
    private void InitViewPager(){
        arrayList_view.clear();
        int item_init = 0;
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
        fileDataSource.setColor(color.getColor());
        super.onSaveInstanceState(outState);
        fileDataSource.saveMyTimes();
        fileDataSource.saveColor();
        fileDataSource.saveLabels();
    }

    //保存数据
    @Override
    protected void onPause() {
        fileDataSource.setColor(color.getColor());
        super.onPause();
        fileDataSource.saveMyTimes();
        fileDataSource.saveColor();
        fileDataSource.saveLabels();
    }

    //打开app后获取之前的数据
    private void InitData() {
        fileDataSource=new FileDataSource(this);
        myTimes=fileDataSource.loadMyTimes();
        color.setColor(fileDataSource.loadColor());
        Labels= fileDataSource.loadLabels();
    }

    public class ShowColorBarListener implements MenuItem.OnMenuItemClickListener{
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final int colorOld = color.getColor();
            final Dialog dialog;
            final LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
            final View myview=inflater.inflate(R.layout.color_choose_layout,null);//引用自定义布局
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setView( myview );
            dialog=builder.create();//创建对话框
            dialog.show();//显示对话框
            final ColorBar colorBar = myview.findViewById(R.id.ColorBar_show);
            colorBar.setOnView(toolbar,fab,color);
            Button buttonOk = myview.findViewById(R.id.button_color_ok);
            Button buttonQuit = myview.findViewById(R.id.button_color_cancel);

            buttonQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color.setColor(colorOld);
                    toolbar.setBackgroundColor(color.getColor());
                    fab.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));
                    dialog.dismiss();
                }
            });
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            drawer.closeDrawers();
            return false;
        }
    }

    //删除标签监听器
    public class DeleteLabelListenter implements MenuItem.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final Dialog dialog;
            final LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
            final View myview=inflater.inflate(R.layout.layout_label_delete,null);//引用自定义布局
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setView( myview );
            dialog=builder.create();//创建对话框
            dialog.show();//显示对话框
            final EditText input = myview.findViewById(R.id.editText_label_add);
            myview.findViewById(R.id.button_label_delete).setOnClickListener(new View.OnClickListener() {//获取布局里面按钮
                @Override
                public void onClick(View v) {
                    dialog.dismiss();//点击按钮对话框消失
                }
            } );
            LinearLayout linearLayout = myview.findViewById(R.id.LinearLayout_label_delete);
            temp = 0;
            while (temp<Labels.size()){
                final Button button_label = new Button(MainActivity.this);
                button_label.setText(Labels.get(temp));
                button_label.setBackgroundColor(Color.parseColor("#FFFFFF"));
                linearLayout.addView(button_label);
                TextView textView = new TextView(MainActivity.this);
                textView.setText(" ");
                textView.setTextSize(20);
                linearLayout.addView(textView);
                button_label.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Labels.remove(temp-1);
                        deleteLabelInTime(button_label.getText().toString());
                        dialog.dismiss();
                    }
                });
                temp++;
            }
            return false;
        }
    }
    private void deleteLabelInTime(String Label){//删除标签后把相关的myTime的标签值对应为空
        int i = 0;
        while(i<myTimes.size()){
            if (Label.equals(myTimes.get(i).getLabel())){
                myTimes.get(i).setLabel("");
            }
            i++;
        }
    }

    //查看以及添加页面的监听器
    public class AddLableListenr implements MenuItem.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final Dialog dialog;
            final LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
            final View myview=inflater.inflate(R.layout.layout_label_add,null);//引用自定义布局
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
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
            while (temp<Labels.size()){
                final Button button_label = new Button(MainActivity.this);
                button_label.setText(Labels.get(temp));
                button_label.setBackgroundColor(Color.parseColor("#FFFFFF"));
                linearLayout.addView(button_label);
                TextView textView = new TextView(MainActivity.this);
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
                        Toast.makeText(MainActivity.this, "不能输入空标签", Toast.LENGTH_SHORT).show();
                    else{
                        boolean alive = true;
                        for (int t = 0;t<Labels.size();t++){//遍历找出要新建的标签是否已经存在
                            if (input.getText().toString().equals(Labels.get(t))){
                                alive = false;
                            }
                        }
                        if (alive == true){
                            Labels.add(input.getText().toString());
                        }else {
                            Toast.makeText(MainActivity.this, "该标签已存在", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }
            });
            return false;
        }
    }
}

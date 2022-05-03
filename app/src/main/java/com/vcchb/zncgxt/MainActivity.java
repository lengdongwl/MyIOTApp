package com.vcchb.zncgxt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static long SendDatalen=0;//发送数据统计
    public static long ReceiveDatalen=0;//接收统计
    public static String serversip="110.40.223.237";//服务器ip
    public static int portnumber=8081;//服务器端口
    public static String password="Q1830011741";//连接服务器Android的key
    public static boolean root=false;//管理模式
    public static String message_sent = "cmd tempA";//连接状态下自动向服务器发送的指令
    public static int MS_serial_number = 1;//1.卧室 2.厨房 3.厕所当前页面索引
    public static int TCP_connection_status = 0; //0.未连接  1.普通连接 3.root连接
    public static boolean TCP_reconnect = true; //是否开启重新连接模式
    public static String title="主页";//标题
    CircleStatisticalView csv;
    List<StatisticalItem> list;
    float total = 1F,total2=1F,total3=1F,total4=1F,total5=1F;
    //测试数据
    int[] color = {0x9fFD695D, 0x9fFDB57B, 0x9f12B166, 0x9f1DA0FB,0x9f00FF00};
    static String[] markTop = {"00.0", "00.0", "00.0", "00.0", "00.0"};
    static String receivedataTime = "null";
    static String[] markBottom = {"温度", "湿度", "烟雾", "空气", "人体"};
    static String[] markBottomB = {"温度", "湿度", "烟雾", "气体", "火焰"};
    static String[] markBottomC = {"温度", "湿度", "空气" };
    static int[] Warning = {0,0,0};
    static int[] lamp = {0,0,0};
    static int[] lamp_of_instructions = {0,0,0};
    static boolean IOT = false;//IOT设备状态
    static int curtain = 0;//窗帘百分比

    //public static int LinkFlag=0;
    //public static String TCPReadData= null;
    public static int Activitystate = 0;//0.关闭 1.启动报警
    public View vie;
    public static int vieNum=0;
    public int alarm=0;//警报状态

    public static int background_animation_switch = 1;//0.关闭动画 1.开启动画
    public static Server myServer = null;//TCP服务器操作
    public static String TAG = "SocketService";//Debug标志

    static Intent homepage_intent = new Intent("android.intent.action.HOMEPAGE");//Activity intent 用于界面切换
    static Intent instrument_intent = new Intent("android.intent.action.INSTRUMENTLINESPLAN");
    static Intent instrument_intent2 = new Intent("android.intent.action.INSTRUMENTMETERDIAGRAM");
    static Intent control_intent = new Intent("android.intent.action.CONTROL");
    static Intent warn_intent = new Intent("android.intent.action.WARN");
    public static Button button1;//按钮
    public static Button button2;
    public static Button button3;
    public static Button button4;
    public static Button button5;
    public static Button buttonselecthome;
    public static Button buttonselectinstrument;
    public static Button buttonselectcontrol;
    private CheckBox rememberbox = null;//多选框 开关动画
    private CheckBox rememberbox2 = null;//警报提醒
    public TextView mTextMessage;
    public TextView mTextMessage2;
    //暂未处理
    public static String alarm_contrast_information = "";//指令
    public static int ACI_serial_number = 0;//指令
    public static DataOutputStream dos;
    public static String Threedata;
    public static int Threedata_flag=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initialize();
//        instrument_intent = new Intent(MainActivity.this, SecondMainActivityMeterDiagram.class);
//        control_intent = new Intent(MainActivity.this, ThreeMainActivity.class);
        if (!serversip.equals("0")) {
            SocketThread();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("kk","continue");
//        Initialize();
//        if (!serversip.equals("0")) {
//            SocketThread();
//        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bu1:
                onClickButton1(v);
                break;
            case R.id.bu2:
                onClickButton2(v);
                break;
            case R.id.bu3:
                onClickButton3(v);
                break;
            case R.id.message:
                onClickButton4(v);
                break;
            case R.id.butlamp:
                onClickButton5(v);
                break;
            case R.id.navigation_home:
                onClickButton_home(v);
                break;
            case R.id.navigation_dashboard:
                onClickButton_instrument(v);
                break;
            case R.id.navigation_notifications:
                onClickButton_control(v);
                break;
            default:
                break;
        }
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    try {
                        TCP_reconnect = false;//跳出循环 取消重连
                        myServer.close();//关闭服务器资源

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    public void Initialize(){
        setContentView(R.layout.activity_main);
        vie = findViewById(R.id.container);
        mTextMessage2 = (TextView) findViewById(R.id.readdata);
        mTextMessage = (Button) findViewById(R.id.message);
        button1 = findViewById(R.id.bu1); //卧室
        button2 = findViewById(R.id.bu2); //厨房
        button3 = findViewById(R.id.bu3); //厕所
        button4 = findViewById(R.id.message);  //服务器连接已断开
        button5 = findViewById(R.id.butlamp);  //灯的按钮
        buttonselecthome = findViewById(R.id.navigation_home);  //隐藏按钮1
        buttonselectinstrument = findViewById(R.id.navigation_dashboard);  //隐藏按钮2
        buttonselectcontrol = findViewById(R.id.navigation_notifications); //隐藏按钮3
        //注册本地监听器
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        buttonselecthome.setOnClickListener(this);
        buttonselectinstrument.setOnClickListener(this);
        buttonselectcontrol.setOnClickListener(this);

        rememberbox = (CheckBox) findViewById(R.id.dhremember); //创建一个监听对象1
        rememberbox.setChecked(false); //监听对象1的初始状态
        rememberbox2 = (CheckBox) findViewById(R.id.dhremember2);//创建一个监听对象1
        rememberbox2.setChecked(true);//监听对象2的初始状态

        //如果第一个活动里"关闭动画"被按下时执行这里的代码
        rememberbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//checkbox的监听事件语句是setOnCheckChangeListener
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    background_animation_switch = 0;
                } else {
                    background_animation_switch = 1;
                }
            }
        });
        //"启动报警"
        rememberbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//checkbox的监听事件语句是setOnCheckChangeListener
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Activitystate = 0;
                } else {
                    Activitystate = 1;
                }
            }
        });

        csv = findViewById(R.id.csv);
        csv.setUseAnimation(true);
        list = new ArrayList<>();
        float[] percent = {total, total2, total3, total4,total5};
        for (int i = 0; i < percent.length; i++) {
            StatisticalItem item = new StatisticalItem();
            item.setPercent(percent[i]);
            item.setColor(color[i]);
            item.setTopMarkText(markTop[i]);
            item.setBottomMarkText(markBottom[i]);
            list.add(item);
        }
        //设置数据方法
        csv.setStatisticalItems(list);
    }
    String thismessage_sent = null;
    int thisMS_serial_number = 0;

    public void SocketThread(){
        new Thread(new Runnable(){
            @Override
            public void run(){

                while (TCP_reconnect) {

                    if (myServer.isConnected()) { //连接成功
                        Log.d(TAG, "服务器读取数据线程开启...");

                        try {

                            while (myServer.isConnected() && TCP_reconnect) { //循环读取数据并处理
                                String msg=myServer.getServerMsg();
                                Log.d(TAG, msg);
                                CheckData1(msg);//解析处理数据函数
                                ReceiveDatalen++;//统计接收包数

                                Thread.sleep(1000);
                            }

                        } catch (IOException | InterruptedException e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    try {
                        //MainActivity.LinkFlag = 0;
                        if (TCP_reconnect) {
                            mTextMessage.setText("服务器重新连接中...");
                            myServer.connect();//重新连接Socket
                            if (myServer.send(password)) { //发送安卓端连接密钥

                                Log.e("SocketService", "TCP服务器连接成功");
                                Log.d("SocketService", "send message to cilent ok");
                                TCP_connection_status = 1;
                                //LinkFlag = 2;
                            }
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "服务器重新连接异常"+e.toString());
                        mTextMessage.setText("服务器重新连接异常");
                        TCP_connection_status = 0;
                    }

                    //3s防止断开socket刷新太快 单独捕获异常
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

            }
        }).start();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            new AlertDialog.Builder(this)
                    .setTitle("系统提示")
                    .setMessage("确定要退出吗")
                    .setPositiveButton("确定", listener)
                    .setNegativeButton("取消", listener)
                    .show();
        }
        return false;
    }
    private void onClickButton1(View view) {
        message_sent = "cmd tempA";
        MS_serial_number = 1;
        //处理逻辑
    }

    private void onClickButton2(View view) {
        message_sent = "cmd tempB";
        MS_serial_number = 2;
        //处理逻辑
    }

    private void onClickButton3(View view) {
        message_sent = "cmd tempC";
        MS_serial_number = 3;
        //处理逻辑
    }
    private void onClickButton4(View view) {
        click1();
        //处理逻辑
    }
    private void onClickButton5(View view) {
        switch (MS_serial_number){
            case 1:
                if(lamp[0]==0){
//                    button5.setBackgroundColor(0xff00FFFF);
                    Toast.makeText(this, "开灯指令十秒内生效，未生效请重试", Toast.LENGTH_SHORT).show();
                    lamp[0] = 1;
                }else{
//                    button5.setBackgroundColor(0xffFD695D);
                    Toast.makeText(this, "关灯指令十秒内生效，未生效请重试", Toast.LENGTH_SHORT).show();
                    lamp[0] = 0;
                }
                lamp_of_instructions[0] = 1;
                break;
            case 2:
                if (lamp[1]==0){
//                    button5.setBackgroundColor(0xff00FFFF);
                    Toast.makeText(this, "开灯指令十秒内生效，未生效请重试", Toast.LENGTH_SHORT).show();
                    lamp[1] = 1;
                }else{
//                    button5.setBackgroundColor(0xffFD695D);
                    Toast.makeText(this, "关灯指令十秒内生效，未生效请重试", Toast.LENGTH_SHORT).show();
                    lamp[1] = 0;
                }
                lamp_of_instructions[1] = 1;
                break;
            case 3:
                if (lamp[2]==0){
//                    button5.setBackgroundColor(0xff00FFFF);
                    Toast.makeText(this, "开灯指令十秒内生效，未生效请重试", Toast.LENGTH_SHORT).show();
                    lamp[2] = 1;
                }else{
//                    button5.setBackgroundColor(0xffFD695D);
                    Toast.makeText(this, "关灯指令十秒内生效，未生效请重试", Toast.LENGTH_SHORT).show();
                    lamp[2] = 0;
                }
                lamp_of_instructions[2] = 1;
                break;
        }
        //处理逻辑
    }
    private void onClickButton_home(View view) {
        title = "主页";
        //处理逻辑
    }
    private void onClickButton_instrument(View view) {
        title = "仪表页";
        startActivity(instrument_intent);
        //处理逻辑
    }
    private void onClickButton_control(View view) {
        title = "控制页";
        startActivity(control_intent);
        //处理逻辑
    }

    public void click1() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(root){
            builder.setTitle(" 当前连接服务器 : "+serversip+":"+portnumber+"  管理员模式");
        }else{
            builder.setTitle(" 当前连接服务器 : "+serversip+":"+portnumber+"  普通模式");
        }

        builder.setMessage("   总发送 : "+SendDatalen+"\n"+"   总接收 : "+ReceiveDatalen);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("点了确定");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("点了取消");
            }
        });
        //一样要show
        builder.show();
    }

    public String Extractstr(String str){
        int strlen = str.length();
        switch(strlen){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                str = str.substring(1,2);
                break;
            case 4:
                str = str.substring(2,3);
                break;
        }
        return str;
    }
    public String SafeState(String str){

        switch(Integer.parseInt(str)){
            case -1:
                str = "安全";
                break;
            case 0:
                str = "安全";
                break;
            case 1:
                str = "危险";
                Warning[MS_serial_number-1]=Warning[MS_serial_number-1]+1;
                break;
        }
        return str;
    }


    public void CheckData1(String TCPReadData) throws IOException {
        //csv.setBackgroundResource(R.drawable.acback18);设置背景图
        thismessage_sent = message_sent;//获取指令码
        thisMS_serial_number  = MS_serial_number;//1.卧室 2.厨房 3.厕所当前页面索引
        if(myServer.send(thismessage_sent)){ //向服务器发送指令
            SendDatalen++;//统计发包数
        }
        UI_light_func();//开关关灯发送指令
        UI_data_func(TCPReadData);//UI界面的数据解析
        UI_home_func();//若主页面UI处理

    }



    public void CheckData(String TCPReadData) throws IOException {



        thismessage_sent = message_sent;
        thisMS_serial_number  = MS_serial_number;
        myServer.send(thismessage_sent);
        if (!TCPReadData.equals("OK12OK12")) {
            System.out.println(TCPReadData);
            StringTokenizer st = new StringTokenizer(TCPReadData, "#");
            String[] str = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                str[i] = st.nextToken();
                i++;
            }

            switch (alarm) {
                case 0x01://AA
                    alarm_contrast_information = "cmd tempA";
                    message_sent = alarm_contrast_information;
                    ACI_serial_number = 1;
                    MS_serial_number = ACI_serial_number;
                    break;
                case 0x02://BB
                    alarm_contrast_information = "cmd tempB";
                    message_sent = alarm_contrast_information;
                    ACI_serial_number = 2;
                    MS_serial_number = ACI_serial_number;
                    break;
                case 0x04://CC
                    alarm_contrast_information = "cmd tempC";
                    message_sent = alarm_contrast_information;
                    ACI_serial_number = 3;
                    MS_serial_number = ACI_serial_number;
                    break;
                case 0x03://AABB
                    alarm_contrast_information = "cmd tempAB";
                    ACI_serial_number = 4;
                    break;
                case 0x05://AACC
                    alarm_contrast_information = "cmd tempAB";
                    ACI_serial_number = 5;
                    break;
                case 0x06://BBCC
                    alarm_contrast_information = "cmd tempBC";
                    ACI_serial_number = 6;
                    break;
                case 0x07://AABBCC
                    alarm_contrast_information = "cmd tempABC";
                    ACI_serial_number = 7;
                    break;
            }
        }

        SendDatalen = SendDatalen + thismessage_sent.length();
        ReceiveDatalen = ReceiveDatalen + TCPReadData.length();
        try {
            StringTokenizer st = new StringTokenizer(TCPReadData, "#");
            String[] str = new String[st.countTokens()];
            if (str[0].equals("Time:")&&str[2].equals("Begin")) {
                //Threedata = str[1];
               // Threedata_flag = 1;
            }
        }catch (Exception e){
        }
        for(int j=0;j<3;j++){
            if (Warning[j] > 0) {
                if (Activitystate == 0) {
                    Activitystate = 2;
                    Log.i("open","edjd");
                    startActivity(warn_intent);
                    Warning[j] = 0;
                    rememberbox2.setChecked(false);

                }
            } else {
            }
        }




        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (background_animation_switch==1) {
                    vieNum += 3;
                    Others.BackgroundAnimation(vie, vieNum);
                }
                if(vieNum>600){
                    vieNum = 0;
                }
            }
        });
        //Thread.sleep(50);
        TCP_connection_status = 2;
        TCP_connection_status = 3;

    }



    public void UI_data_func(String data) {
        //data 格式"Begin[ Add:0xBB Wd:23 SD: Hy:10 Qt:10 Yw:10 Kq:10 Rt:3 Cl:2 D:1 ]Over";

        /*
        static String[] markTop = {"00.0", "00.0", "00.0", "00.0", "00.0"};
        static String[] markBottom = {"温度", "湿度", "烟雾", "空气", "人体"};
        static String[] markBottomB = {"温度", "湿度", "烟雾", "气体", "火焰"};
        static String[] markBottomC = {"温度", "湿度", "空气" };
        */
        if (data.indexOf("]Over")-data.indexOf("Begin[")>0) { //判断包类型
            HashMap<String, String> cmdData = cmdGetValue(data);//使用cmdData.get()未重写equals，尽量用==不要用.equals，防止空指针闪退
            if (cmdData.get("Add")!=null)//判断包
            {
                try {
                    switch (thisMS_serial_number) {

                        case 1:
                            if (cmdData.get("Add").equals("0xAA")) {
                                markTop[0] = cmdData.get("Wd");//传感器指标数据
                                markTop[1] = cmdData.get("Sd");
                                markTop[2] = cmdData.get("Yw");
                                markTop[3] = cmdData.get("Kq");
                                markTop[4] = cmdData.get("Rt").equals("0") ? "无人" : "有人";
                                lamp[0] = Integer.parseInt(Extractstr(cmdData.get("D")));//lamp[0] [1] [2] 卧室，厨房，厕所的灯亮灭状态
                                alarm = Integer.parseInt(cmdData.get("alarm"));//警报
                                curtain = Integer.parseInt(cmdData.get("Cl"));//窗帘
                                receivedataTime = cmdData.get("Time");
                                IOT = cmdData.get("IOT").equals("1");//MCU设置在线状态
                            }
                            break;
                        case 2:
                            if (cmdData.get("Add").equals("0xBB")) {
                                markTop[0] = cmdData.get("Wd");
                                markTop[1] = cmdData.get("Sd");
                                markTop[2] = cmdData.get("Yw");
                                markTop[3] = cmdData.get("Qt");
                                markTop[4] = cmdData.get("Hy");
                                lamp[1] = Integer.parseInt(Extractstr(cmdData.get("D")));
                                alarm = Integer.parseInt(cmdData.get("alarm"));//警报
                                curtain = Integer.parseInt(cmdData.get("Cl"));//窗帘
                                receivedataTime = cmdData.get("Time");
                                IOT = cmdData.get("IOT").equals("1");//MCU设置在线状态
                            }
                            break;
                        case 3:
                            if (cmdData.get("Add").equals("0xCC")) {
                                markTop[0] = cmdData.get("Wd");
                                markTop[1] = cmdData.get("Sd");
                                markTop[2] = cmdData.get("Kq");
                                lamp[2] = Integer.parseInt(Extractstr(cmdData.get("D")));
                                alarm = Integer.parseInt(cmdData.get("alarm"));//警报
                                curtain = Integer.parseInt(cmdData.get("Cl"));//窗帘
                                receivedataTime = cmdData.get("Time");
                                IOT = cmdData.get("IOT").equals("1");//MCU设置在线状态
                            }
                            break;
                    }


                } catch (Exception e) {
                    Log.e(TAG, "UI_data_func: " + e);
                }
            }
        }

    }

    //设置界面数据显示
    public void UI_home_func(){
        if(title.equals("主页"))
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //设置更新时间
                    switch (thisMS_serial_number){
                        case 1:
                            mTextMessage2.setText("  更新时间 : " + receivedataTime + "   卧室 ");
                            break;
                        case 2:
                            mTextMessage2.setText("  更新时间 : " + receivedataTime + "   厨房 ");
                            break;
                        case 3:
                            mTextMessage2.setText("  更新时间 : " + receivedataTime + "   厕所 ");
                            break;
                    }

                    //显示IOT设备状态
                    if (IOT)
                    {
                        mTextMessage.setTextColor(0xff00FFFF);//设置背景色
                        mTextMessage.setText(" IOT设备在线 ");
                    }else
                    {
                        mTextMessage.setText(" IOT设备离线 ");
                    }

                    csv = findViewById(R.id.csv);
                    csv.setUseAnimation(false);
                    list = new ArrayList<>();

                    //设置1.卧室 2.厨房 3.厕所界面
                    switch (thisMS_serial_number) {
                        case 1:
                            float[] percent = {total, total2, total3, total4, total5};

                            //Warning[0] >0设置危险背景色 <0安全
                            if (Warning[0] > 0) {
                                csv.setMarkTextColor(0x9FFF4081);//设置字体颜色
                            } else {
                                csv.setMarkTextColor(0x9F92e6ed);
                            }

                            //设置按钮显示指标项 颜色 位置
                            for (int i = 0; i < percent.length; i++) {
                                StatisticalItem item = new StatisticalItem();
                                item.setPercent(percent[i]);//百分比
                                item.setColor(color[i]);
                                item.setTopMarkText(markTop[i]);//传感器指标
                                item.setBottomMarkText(markBottom[i]);//传感器名称
                                list.add(item);
                            }

                            if (lamp[0] == 1) { //灯开关按钮设置
                                button5.setText("灯亮着呢");
                                button5.setTextColor(0xffFFFF00);
                                button5.setBackgroundResource(R.drawable.button_cornerli);
                                button5.setFocusable(true);
                                button5.setFocusableInTouchMode(true);
                                button5.requestFocus();
                                button5.requestFocusFromTouch();
                                button5.postInvalidate();
                            } else {
                                button5.setText("灯灭着呢");
                                button5.setTextColor(0x9F92e6ed);
                                button5.setBackgroundResource(R.drawable.button_cornernoli);
                                button5.setFocusable(true);
                                button5.setFocusableInTouchMode(true);
                                button5.requestFocus();
                                button5.requestFocusFromTouch();
                                button5.postInvalidate();
                            }

                            break;
                        case 2:
                            float[] percent2 = {total, total2, total3, total4, total5};
                            if (Warning[1] > 0) {
                                csv.setMarkTextColor(0x9FFF4081);
                                if (Activitystate == 0) {
                                    Activitystate = 1;
                                }
                            } else {
                                csv.setMarkTextColor(0x9F92e6ed);
                            }
                            for (int i = 0; i < percent2.length; i++) {
                                StatisticalItem item = new StatisticalItem();
                                item.setPercent(percent2[i]);
                                item.setColor(color[i]);
                                item.setTopMarkText(markTop[i]);
                                item.setBottomMarkText(markBottomB[i]);
                                list.add(item);
                            }
                            if (lamp[1] == 1) {
                                button5.setText("灯亮着呢");
                                button5.setTextColor(0xffFFFF00);
                                button5.setBackgroundResource(R.drawable.button_cornerli);
                                button5.setFocusable(true);
                                button5.setFocusableInTouchMode(true);
                                button5.requestFocus();
                                button5.requestFocusFromTouch();
                                button5.postInvalidate();
                            } else {
                                button5.setText("灯灭着呢");
                                button5.setTextColor(0x9F92e6ed);
                                button5.setBackgroundResource(R.drawable.button_cornernoli);
                                button5.setFocusable(true);
                                button5.setFocusableInTouchMode(true);
                                button5.requestFocus();
                                button5.requestFocusFromTouch();
                                button5.postInvalidate();
                            }
                            break;
                        case 3:
                            float[] percent3 = {total, total2, total3};
                            if (Warning[2] > 0) {
                                csv.setMarkTextColor(0x9FFF4081);
                                if (Activitystate == 0) {
                                    Activitystate = 1;
                                }
                            } else {
                                csv.setMarkTextColor(0x9F92e6ed);
                            }
                            for (int i = 0; i < percent3.length; i++) {
                                StatisticalItem item = new StatisticalItem();
                                item.setPercent(percent3[i]);
                                item.setColor(color[i]);
                                item.setTopMarkText(markTop[i]);
                                item.setBottomMarkText(markBottomC[i]);
                                list.add(item);
                            }
                            if (lamp[2] == 1) {
                                button5.setText("灯亮着呢");
                                button5.setTextColor(0xffFFFF00);
                                button5.setBackgroundResource(R.drawable.button_cornerli);
                                button5.setFocusable(true);
                                button5.setFocusableInTouchMode(true);
                                button5.requestFocus();
                                button5.requestFocusFromTouch();
                                button5.postInvalidate();
                            } else {
                                button5.setText("灯灭着呢");
                                button5.setTextColor(0x9F92e6ed);
                                button5.setBackgroundResource(R.drawable.button_cornernoli);
                                button5.setFocusable(true);
                                button5.setFocusableInTouchMode(true);
                                button5.requestFocus();
                                button5.requestFocusFromTouch();
                                button5.postInvalidate();
                            }
                            break;
                    }
                    //设置数据方法
                    csv.setStatisticalItems(list);
                }
            });
        }

        //开启背景动画
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (background_animation_switch==1) {
                    vieNum += 3;
                    Others.BackgroundAnimation(vie, vieNum);
                }
                if(vieNum>600){
                    vieNum = 0;
                }
            }
        });


    }

    //类似以下的字符传解析成hashmap
    // "Begin[ Add:0xAA Wd:0 Sd:0 Hy:0 Qt:0 Yw:0 Kq:0 Rt:0 Cl:0 D:0 alarm:0 IOT:0 Time:2022-04-28 15:23:29 ]Over";
    public HashMap<String,String> cmdGetValue(String data) {
        HashMap<String, String> databuf = new HashMap<String, String>();
        try {
            String Time = data.substring(data.indexOf("Time:")+5, data.indexOf(" ]Over")); //由于时间有空格会被分割所以这里单独截取时间
            String tempbuf[] = data.split(" ");//分割字符串
            if (tempbuf[0].equals("Begin[") && tempbuf[tempbuf.length - 1].equals("]Over")) //判断包头包尾是否匹配
            {
                boolean after = false;//是否取尾部
                for (int i = 1; i < tempbuf.length - 1; i++) {
                    after = false;//是否开始读取:后的数据
                    StringBuilder key = new StringBuilder();
                    StringBuilder value = new StringBuilder();
                    for (int j = 0; j < tempbuf[i].length(); j++) {
                        if (tempbuf[i].charAt(j) != ':') {
                            if (after) {
                                value.append(tempbuf[i].charAt(j));
                            } else {
                                key.append(tempbuf[i].charAt(j));
                            }
                        } else {
                            after = true;
                        }
                    }
                    if (value.length() != 0) {
                        databuf.put(key.toString(), value.toString());//将解析结果加入hashmap
                    } else //若value为空 默认给0
                    {
                        databuf.put(key.toString(), "0");
                    }
                }
            }
            databuf.put("Time",Time);
        }catch (Exception e)
        {
            Log.e(TAG, "cmdGetValue: ",e );
        }
        return databuf;
    }

    //开关灯指令
    public void UI_light_func(){
        try {
            if ((lamp_of_instructions[0] == 1) || (lamp_of_instructions[1] == 1) || (lamp_of_instructions[2] == 1)) {
                if (lamp_of_instructions[0] == 1) {
                    if (lamp[0] == 1) {
                        myServer.send("cmd AopenDeng");//开0XAA灯
                    } else {
                        myServer.send("cmd AcloseDeng");//关灯
                    }
                    lamp_of_instructions[0] = 0;
                }
                if (lamp_of_instructions[1] == 1) {
                    if (lamp[1] == 1) {
                        myServer.send("cmd BopenDeng");
                    } else {
                        myServer.send("cmd BcloseDeng");
                    }
                    lamp_of_instructions[1] = 0;
                }
                if (lamp_of_instructions[2] == 1) {
                    if (lamp[2] == 1) {
                        myServer.send("cmd CopenDeng");
                    } else {
                        myServer.send("cmd CcloseDeng");
                    }
                    lamp_of_instructions[2] = 0;
                }
            }
        }catch (Exception e)
        {
            Log.e(TAG, "UI_light_func: ",e );
        }
    }

}



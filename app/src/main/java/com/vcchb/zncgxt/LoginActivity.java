package com.vcchb.zncgxt;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

import static com.vcchb.zncgxt.MainActivity.markTop;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText device_edit = null;
    private EditText login_edit = null;
    private EditText passwd_edit = null;

    private Button bt_connect = null;
    private CheckBox rememberbox = null;

    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置界面的参数
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 判断是否是平板
        if (isPad(this))
            setContentView(R.layout.activity_login);
        else
            setContentView(R.layout.activity_login_mobilephone);
        //改变屏幕显示的方向
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        findViews();  //控件初始化
    }


    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    private void findViews() {
        if (isPad(this)) {
            device_edit = (EditText) findViewById(R.id.deviceid);
            login_edit = (EditText) findViewById(R.id.loginname);
            passwd_edit = (EditText) findViewById(R.id.loginpasswd);
        }else
        {
            device_edit = (EditText) findViewById(R.id.deviceid1);
            login_edit = (EditText) findViewById(R.id.loginname1);
            passwd_edit = (EditText) findViewById(R.id.loginpasswd1);
        }
        Button bt_reset = (Button) findViewById(R.id.reset);
        // 连接按钮
        bt_connect = (Button) findViewById(R.id.connect);
        rememberbox = (CheckBox) findViewById(R.id.remember);
        rememberbox.setChecked(false);
        Switch transmitswitch = (Switch) findViewById(R.id.transmit_way);

        bt_reset.setOnClickListener(this);
        bt_connect.setOnClickListener(this);
        transmitswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton Button, boolean isChecked) {
                if (isChecked) {
                    Button.setText("管理模式");
                    Toast.makeText(LoginActivity.this, "请务必确保使用管理员密码接入大赛服务器", Toast.LENGTH_SHORT).show();
                    MainActivity.root=true;
                } else {
                    Button.setText("普通模式");
                    Toast.makeText(LoginActivity.this, "请务必确保使用普通密码接入大赛服务器", Toast.LENGTH_SHORT).show();
                    MainActivity.root=false;
                }
            }
        });
        rememberbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//checkbox的监听事件语句是setOnCheckChangeListener
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("tag",b+"");//android输出语句是Log.i("tag",  )
                if(b)
                {
                    String text=rememberbox.getText().toString();//得到checkbox控件的显示名字
                    Log.i("tag",text);//android输出语句
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reset) {//重置按钮
            device_edit.setText("");
            login_edit.setText("");
            passwd_edit.setText("");
            rememberbox.setChecked(false);
        } else if(view.equals(bt_connect)){//连接按钮
            dialog = new ProgressDialog(this);
            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }).start();
*/

            try{
                Log.d("SocketService","开始读取数据...");
                dialogShow("开始读取数据...");
                //MainActivity.homepage_intent = ;

                if (device_edit.getText().toString().isEmpty())
                {
                    MainActivity.serversip = "110.40.223.237";
                }else
                {
                    MainActivity.serversip = device_edit.getText().toString();
                }
                if (login_edit.getText().toString().isEmpty())
                {
                    MainActivity.portnumber = 8081;
                }else
                {
                    MainActivity.portnumber = Integer.valueOf(login_edit.getText().toString());//Integer.parseInt(device_edit.getText().toString());
                }
                if (passwd_edit.getText().toString().isEmpty())
                {
                    MainActivity.password="Q1830011741";
                }else
                {
                    MainActivity.password = passwd_edit.getText().toString();
                }
                //Log.d("SocketService", "onClick:"+MainActivity.serversip+":"+MainActivity.portnumber);

            }catch (Exception e){

                dialogShow("读取数据异常...");
            }

            Log.d("SocketService","开始连接服务器...");
            dialogShow("开始连接服务器...");

            new Thread() //必须单独开个线程连接服务器
            {
                @Override
                public void run() {
                    connect();
                }
            }.start();

        }
    }


    public void connect()
    {
        try {
            //Log.d("SocketService","ip:"+MainActivity.serversip +"port:"+MainActivity.portnumber);
            Server myServer= new Server(MainActivity.serversip,MainActivity.portnumber);//这里IP固定了
            //Socket TCP = myServer.getSocket();//连接服务器
            //MainActivity.myServer = new MyServer(MainActivity.serversip, MainActivity.portnumber);
            if (myServer.isConnected()) { // TCP连接成功
                //MainActivity.LinkFlag = 0;
                try {
                    if (myServer.send(MainActivity.password)) { //发送安卓端连接密钥
                        if (myServer.getServerMsg().equals(MainActivity.loginFlag)) {
                            Log.e("SocketService", "TCP服务器连接成功");
                            Log.d("SocketService", "send message to cilent ok");
                            MainActivity.TCP_connection_status = 1;
                            MainActivity.TCP_reconnect = true;
                            MainActivity.myServer = myServer;
                            Thread.sleep(1000);//延迟效果才看得见显示内容
                            dialogShow("服务器连接成功...");
                            myServer = null;

                            Thread.sleep(1000);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));//切换活动窗口
                        }else
                        {
                            Log.e("SocketService", "TCP服务器密钥不正确");
                            dialogShow("TCP服务器密钥不正确...");
                        }
                    }
                } catch (Exception E) {
                    MainActivity.TCP_connection_status = 0;
                    Log.e("SocketService", "TCP服务器密钥不正确");
                    dialogShow("TCP服务器密钥不正确...");

                }
            }else
            {
                dialogShow("连接服务器失败...");

                Log.e("SocketService", "MainActivity.TCP  == null");

            }
        } catch (Exception e) {
            Log.e("SocketService", "TCP连接异常");
            dialogShow("连接服务器异常...");


        }

        try {
            Thread.sleep(1000);//连接失败或者异常显示 延迟关闭显示
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dialog.cancel();//关闭连接显示
    }
    public void dialogShow(String data) {
        try {
            //dialog.cancel();
            dialog.setMessage(data);
            dialog.show();
        }catch (Exception e)
        {
            Log.i("Exception","dialog Exception");
        }
    }
}

package com.vcchb.zncgxt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class ThreeMainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button[] button = new Button[15];
    public static Button buttonselecthome;
    public static Button buttonselectinstrument;
    public static Button buttonselectcontrol;
    public View vie;
    int vieNum;
    private SeekBar SeekBar1;
    private boolean SeekBar1_flag=true;
    public static boolean closeallthread=false;
    public int Progress = 0;
    private String[] button_send = {"","","","","","","","","","","","",""};
    private TextView[] textView= new TextView[4];
    private int[] lamp = {0,0,0};
    private int curtain = -10,curtainchange=0,curtainpara;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curtainchange = 0;
        Initialize();
        ControlThread();
    }
    public void Initialize(){
        closeallthread = false;
        setContentView(R.layout.activity_three);
        button[0] = findViewById(R.id.Sbu1);
        button[1] = findViewById(R.id.Sbu2);
        button[2] = findViewById(R.id.Sbu3);
        button[3] = findViewById(R.id.bedroom_Key);
        button[4] = findViewById(R.id.kitchen_Key);
        button[5] = findViewById(R.id.toilet_Key);
        button[6] = findViewById(R.id.transmit_way3);
        button[7] = findViewById(R.id.transmit_way4);
        button[8] = findViewById(R.id.transmit_way5);
        button[9] = findViewById(R.id.transmit_way6);
        button[10] = findViewById(R.id.transmit_way7);
        button[11] = findViewById(R.id.transmit_way8);
        button[12] = findViewById(R.id.transmit_way9);
        button[13] = findViewById(R.id.transmit_way1);
        button[14] = findViewById(R.id.transmit_way2);
        //注册监听器
        button[0].setOnClickListener(this);
        button[1].setOnClickListener(this);
        button[2].setOnClickListener(this);
        button[3].setOnClickListener(this);
        button[4].setOnClickListener(this);
        button[5].setOnClickListener(this);
        button[6].setOnClickListener(this);
        button[7].setOnClickListener(this);
        button[8].setOnClickListener(this);
        button[9].setOnClickListener(this);
        button[10].setOnClickListener(this);
        button[11].setOnClickListener(this);
        button[12].setOnClickListener(this);
        button[13].setOnClickListener(this);
        button[14].setOnClickListener(this);
        SeekBar1 = findViewById(R.id.Sbu1_seekbar);
        buttonselecthome = findViewById(R.id.navigation_home);
        buttonselectinstrument = findViewById(R.id.navigation_dashboard);
        buttonselectcontrol = findViewById(R.id.navigation_notifications);
        buttonselecthome.setOnClickListener(this);
        buttonselectinstrument.setOnClickListener(this);
        buttonselectcontrol.setOnClickListener(this);
        textView[0] = findViewById(R.id.bedroom_Key_text);
        textView[1] = findViewById(R.id.kitchen_Key_text);
        textView[2] = findViewById(R.id.toilet_Key_text);
        textView[3] = findViewById(R.id.Sbu1_seekbar_text);

        vie = findViewById(R.id.container);

        curtainpara = MainActivity.curtain;
        SeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                txt_cur.setText("当前进度值:" + progress + "  / 100 ");
                Progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(mContext, "触碰SeekBar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(mContext, "放开SeekBar", Toast.LENGTH_SHORT).show();
                SeekBar1_flag = false;
                if(Progress<13){
                    seekBar.setProgress(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ThreeMainActivity.this, "设置窗帘为0%", Toast.LENGTH_SHORT).show();
                        }
                    });
                    curtainpara = 0;
                }else if(Progress>=13 && Progress<38){
                    seekBar.setProgress(25);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ThreeMainActivity.this, "设置窗帘为25%", Toast.LENGTH_SHORT).show();
                        }
                    });
                    curtainpara = 25;
                }else if(Progress>=38 && Progress<63){
                    seekBar.setProgress(50);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ThreeMainActivity.this, "设置窗帘为50%", Toast.LENGTH_SHORT).show();
                        }
                    });
                    curtainpara = 50;
                }else if(Progress>=63 && Progress<88){
                    seekBar.setProgress(75);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ThreeMainActivity.this, "设置窗帘为75%", Toast.LENGTH_SHORT).show();
                        }
                    });
                    curtainpara = 75;
                }else if(Progress>=88){
                    seekBar.setProgress(100);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ThreeMainActivity.this, "设置窗帘为100%", Toast.LENGTH_SHORT).show();
                        }
                    });
                    curtainpara = 100;
                }
                curtainchange = 1;
                SeekBar1_flag = true;
            }
        });

    }

    public void ControlThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                vieNum=0;
                while (!closeallthread) {
                    if ((vieNum != MainActivity.vieNum)) {//&& (MainActivity.title.equals("控制页"))
                        vieNum = MainActivity.vieNum;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Others.BackgroundAnimation(vie, vieNum);
                            }
                        });
                    }
                    Chage();
                }
            }
        }).start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        closeallthread = false;
        Log.i("kk","continue2");
        curtainchange = 0;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Sbu1:
                onClickButton1(v);
                break;
            case R.id.Sbu2:
                onClickButton2(v);
                break;
            case R.id.Sbu3:
                onClickButton3(v);
                break;
            case R.id.bedroom_Key:
                try {
                    for (int i =0;i<200;i++) {
                        if (MainActivity.TCP_connection_status==3) {
                            if (MainActivity.lamp[0] == 0) {
                                MainActivity.dos.writeBytes("cmd AopenDeng");
                                MainActivity.dos.flush();
                            } else {
                                MainActivity.dos.writeBytes("cmd AcloseDeng");
                                MainActivity.dos.flush();
                            }
                            break;
                        }
                        Thread.sleep(1);
                    }
                }catch (Exception e){
                }
                break;
            case R.id.kitchen_Key:
                try {
                    for (int i =0;i<200;i++) {
                        if (MainActivity.TCP_connection_status==3) {
                            if (MainActivity.lamp[1] == 0) {
                                MainActivity.dos.writeBytes("cmd BopenDeng");
                                MainActivity.dos.flush();
                            } else {
                                MainActivity.dos.writeBytes("cmd BcloseDeng");
                                MainActivity.dos.flush();
                            }
                            break;
                        }
                        Thread.sleep(1);
                    }
                }catch (Exception e){
                }
                break;
            case R.id.toilet_Key:
                try {
                    for (int i =0;i<200;i++) {
                        if (MainActivity.TCP_connection_status==3) {
                            if (MainActivity.lamp[2] == 0) {
                                MainActivity.dos.writeBytes("cmd CopenDeng");
                                MainActivity.dos.flush();
                            } else {
                                MainActivity.dos.writeBytes("cmd CcloseDeng");
                                MainActivity.dos.flush();
                            }
                            break;
                        }
                        Thread.sleep(1);
                    }
                }catch (Exception e){
                }
                break;
            case R.id.transmit_way1:
                try {
                    for (int i =0;i<200;i++) {
                        if (MainActivity.TCP_connection_status == 3) {
                            MainActivity.dos.writeBytes("cmd CServerClear");
                            MainActivity.dos.flush();
                            break;
                        }
                        Thread.sleep(1);
                    }
                }catch (Exception e){
                }
                break;
            case R.id.transmit_way2:
//                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("请输入自定义指令");
                    // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View view = LayoutInflater.from(this).inflate(
                            R.layout.three_main_zdy, null);
                    // 设置我们自己定义的布局文件作为弹出框的Content
                    builder.setView(view);

                    final EditText username = (EditText) view
                            .findViewById(R.id.edUsername);

                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //确定操作的内容
                                    try {
                                    for (int i =0;i<200;i++) {
                                        if (MainActivity.TCP_connection_status == 3) {
                                            MainActivity.dos.writeBytes(String.valueOf(username.getText()));
                                            MainActivity.dos.flush();
                                            break;
                                        }
                                        Thread.sleep(1);
                                    }
                                    } catch (Exception e) {
                                    }
                                }
                            });

                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    //Toast.makeText(ThreeMainActivity.this, "！",Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.show();
                    //MainActivity.dos.writeBytes("cmd CServerClear");
                    //MainActivity.dos.flush();
//                }catch (Exception e){
//                }
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
    private void onClickButton1(View view) {
        //处理逻辑
    }

    private void onClickButton2(View view) {
        //处理逻辑
    }

    private void onClickButton3(View view) {
        //处理逻辑
    }

    private void onClickButton_home(View view) {
//        startActivity(LoginActivity.homepage_intent);
        MainActivity.title = "主页";
        finish();
        //处理逻辑
    }

    private void onClickButton_instrument(View view) {
        MainActivity.title = "仪表页";
        startActivity(MainActivity.instrument_intent);
        finish();
        //处理逻辑
    }

    private void onClickButton_control(View view) {
        MainActivity.title = "控制页";
        //处理逻辑
    }
    public void Chage(){
            if (MainActivity.lamp[0] == 0 && lamp[0] !=0) {
                lamp[0] = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView[0].setText("灯关着呢");
                    }
                });
            }
            if (MainActivity.lamp[0] == 1 && lamp[0] !=1) {
                lamp[0] = 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView[0].setText("灯开着呢");
                    }
                });
            }
            if (MainActivity.lamp[1] == 0 && lamp[1] !=0) {
                lamp[1] = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView[1].setText("灯关着呢");
                    }
                });
            }
            if (MainActivity.lamp[1] == 1 && lamp[1] !=1) {
                lamp[1] = 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView[1].setText("灯开着呢");
                    }
                });
            }
            if (MainActivity.lamp[2] == 0 && lamp[2] !=0) {
                lamp[2] = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView[2].setText("灯关着呢");
                    }
                });
            }
            if (MainActivity.lamp[2] == 1 && lamp[2] !=1) {
                lamp[2] = 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView[2].setText("灯开着呢");
                    }
                });
            }
            if (SeekBar1_flag && curtain != MainActivity.curtain) {
                curtain = MainActivity.curtain;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SeekBar1.setProgress(curtain);
                        textView[3].setText("窗帘"+curtain+"%");
                    }
                });
            }
            if (curtainchange==1){
                curtainchange = 0;
                switch (curtainpara){
                    case 0:
                        if (MainActivity.curtain==0){
                        }else {
                            try {
                                for (int i = 0; i < 200; i++) {
                                    if (MainActivity.TCP_connection_status == 3) {
                                        MainActivity.dos.writeBytes("cmd curtain000");
                                        MainActivity.dos.flush();
                                        break;
                                    }
                                }
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 25:
                        if (MainActivity.curtain==25){
                        }else {
                            try {
                                for (int i = 0; i < 200; i++) {
                                    if (MainActivity.TCP_connection_status == 3) {
                                        MainActivity.dos.writeBytes("cmd curtain025");
                                        MainActivity.dos.flush();
                                        break;
                                    }
                                }
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 50:
                        if (MainActivity.curtain==50){
                        }else {
                            try {
                                for (int i = 0; i < 200; i++) {
                                    if (MainActivity.TCP_connection_status == 3) {
                                        MainActivity.dos.writeBytes("cmd curtain050");
                                        MainActivity.dos.flush();
                                        break;
                                    }
                                }
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 75:
                        if (MainActivity.curtain==100){
                        }else {
                            try {
                                for (int i = 0; i < 200; i++) {
                                    if (MainActivity.TCP_connection_status == 3) {
                                        MainActivity.dos.writeBytes("cmd curtain075");
                                        MainActivity.dos.flush();
                                        break;
                                    }
                                }
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 100:
                        if (MainActivity.curtain==100){
                        }else {
                            try {
                                for (int i = 0; i < 200; i++) {
                                    if (MainActivity.TCP_connection_status == 3) {
                                        MainActivity.dos.writeBytes("cmd curtain100");
                                        MainActivity.dos.flush();
                                        break;
                                    }
                                }
                                Thread.sleep(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
            try {
                char[] data = MainActivity.Threedata.toCharArray();
                if (MainActivity.Threedata_flag==1) {
                    MainActivity.Threedata_flag=0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ThreeMainActivity.this,MainActivity.Threedata+"",Toast.LENGTH_SHORT).show();
                        }
                    });
                    MainActivity.Threedata.length();
                }
            }catch (Exception e){
            }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeallthread = true;
    }
}

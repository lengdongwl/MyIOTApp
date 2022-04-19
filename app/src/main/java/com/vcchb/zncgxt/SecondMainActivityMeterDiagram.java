package com.vcchb.zncgxt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class SecondMainActivityMeterDiagram extends AppCompatActivity implements View.OnClickListener{
    DashboardView MDtemp,MDhumi;
    Thread th;
    public int SeekBar1progress;
    private TextView nameandSeekBarNumber;
    private  Button button1;
    private  Button button2;
    private  Button button3;
    private SeekBar SeekBar1;
    public static Button buttonselecthome;
    public static Button buttonselectinstrument;
    public static Button buttonselectcontrol;
    public static char point_location = 'A';
    public static int PL_serial_number = 1;
    char para = 0;
    int timer = 5;
    public View vie;
    public static int vieNum=0;
    public static boolean closeallthread=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeallthread = false;
        Initialize();
        DrawThread();


    }
    @Override
    protected void onResume() {
        super.onResume();
        closeallthread = false;
        Log.i("kk","continue2");
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
            case R.id.navigation_home:
                onClickButton_home(v);
                break;
            case R.id.navigation_dashboard:
                onClickButton_instrument(v);
                break;
            case R.id.navigation_notifications:
                onClickButton_control(v);
                break;
            case R.id.transmit_way2:
                onClickButton_LinesPlan(v);
                break;
            default:
                break;
        }
    }
    private void onClickButton1(View view) {
        point_location = 'A';
        para = 0;
        PL_serial_number = 1;

        //处理逻辑
    }

    private void onClickButton2(View view) {
        point_location = 'B';
        para = 0;
        PL_serial_number = 2;
        //处理逻辑
    }

    private void onClickButton3(View view) {
        point_location = 'C';
        para = 0;
        PL_serial_number = 3;

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
        //处理逻辑
    }
    private void onClickButton_control(View view) {
        startActivity(MainActivity.control_intent);
        MainActivity.title = "控制页";
        finish();
        //处理逻辑
    }
    private void onClickButton_LinesPlan(View view) {
        SecondMainActivityLinesPlan.choice_mode=0;
        startActivity(MainActivity.instrument_intent);
        finish();
        //处理逻辑
    }

    public String download(String strUrl) {
        StringBuffer dataread = new StringBuffer();
        BufferedReader breader = null;
        String line = null;
        try {
            URL url = new URL(strUrl);

            // 建立连接
            HttpURLConnection conn = ( HttpURLConnection ) url.openConnection();

            breader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            // BufferedReader中的信息转入String
            while ( (line = breader.readLine()) != null ) {
                dataread.append(line);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                breader.close();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return dataread.toString();
    }

    public void Initialize(){
        SeekBar1progress = 0;
        setContentView(R.layout.activity_secondyibiao);
        vie = findViewById(R.id.container);
        Switch transmitswitch = (Switch) findViewById(R.id.transmit_way);
        Button transmitswitch2 = findViewById(R.id.transmit_way2);
        button1 = findViewById(R.id.Sbu1);
        button2 = findViewById(R.id.Sbu2);
        button3 = findViewById(R.id.Sbu3);
        //注册监听器
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        transmitswitch2.setOnClickListener(this);
        buttonselecthome = findViewById(R.id.navigation_home);
        buttonselectinstrument = findViewById(R.id.navigation_dashboard);
        buttonselectcontrol = findViewById(R.id.navigation_notifications);
        buttonselecthome.setOnClickListener(this);
        buttonselectinstrument.setOnClickListener(this);
        buttonselectcontrol.setOnClickListener(this);
        SeekBar1 = findViewById(R.id.Canseekbar);
        MDtemp = findViewById(R.id.SCanvas2);
        MDhumi = findViewById(R.id.SCanvas3);
        MDtemp.setClockValueArea(0,50,"℃");
        MDhumi.setClockValueArea(0,95,"%RH");
        nameandSeekBarNumber = findViewById(R.id.chufang);

        SeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                txt_cur.setText("当前进度值:" + progress + "  / 100 ");

                switch (point_location){
                    case 'A':
                        nameandSeekBarNumber.setText("卧室\n"+progress);
                        break;
                    case 'B':
                        nameandSeekBarNumber.setText("厨房\n"+progress);
                        break;
                    case 'C':
                        nameandSeekBarNumber.setText("厕所\n"+progress);
                        break;
                }
                SeekBar1progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(mContext, "触碰SeekBar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(mContext, "放开SeekBar", Toast.LENGTH_SHORT).show();
            }
        });

        transmitswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton Button, boolean isChecked) {
                if (isChecked) {
                    Button.setText("三十分钟");
                    Toast.makeText(SecondMainActivityMeterDiagram.this, "三十分钟", Toast.LENGTH_SHORT).show();
                    timer=30;
                } else {
                    Button.setText("五分钟");
                    Toast.makeText(SecondMainActivityMeterDiagram.this, "五分钟", Toast.LENGTH_SHORT).show();
                    timer=5;
                }
            }
        });
    }


    public void DrawThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                vieNum=0;
                int humiditycount = 0;
                int tempcount = 0;
                int progress = 0;
                String[] str = new String[0];
                String[] shr = new String[0];
                MDtemp.Change = 0;
                MDhumi.Change = 0;
                SecondMainActivityLinesPlan.choice_mode=1;
                while (!closeallthread) {
                    if((vieNum!=MainActivity.vieNum) && (MainActivity.title.equals("仪表页")) && SecondMainActivityLinesPlan.choice_mode==1){
                        vieNum = MainActivity.vieNum;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Others.BackgroundAnimation(vie,vieNum);
                            }
                        });
//                        Log.i("kk",vieNum+"");
                    }
                    if (point_location != para) {
                        para = point_location;
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");// HH:mm:ss
                            //获取当前时间
                            Date date = new Date(System.currentTimeMillis());
                            StringTokenizer st = null;
                            StringTokenizer sh = null;
                            if(timer==5) {
                                switch (point_location) {
                                    case 'A':
                                        if ((Integer.valueOf(simpleDateFormat.format(date)) % 10 - 5) < 0) {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/HkyW0zC10bQr"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/e1x6jeOPbPFQ"), "#");
                                            Log.i("Dow", "0");
                                        } else {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/ftD1CPnGSTiF"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/X1dwCGZaT0Oy"), "#");
                                            Log.i("Dow", "5");
                                        }
                                        break;
                                    case 'B':
                                        if ((Integer.valueOf(simpleDateFormat.format(date)) % 10 - 5) < 0) {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/MpgF5iNXI6pD"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/Zzzq3EEcDgzU"), "#");
                                            Log.i("Dow", "0");
                                        } else {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/wbzvqdYFfimg"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/OP99yUj5OQnP"), "#");
                                            Log.i("Dow", "5");
                                        }
                                        break;
                                    case 'C':
                                        if ((Integer.valueOf(simpleDateFormat.format(date)) % 10 - 5) < 0) {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/rhmdhoXeYDSb"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/t91v9dZTmL3U"), "#");
                                            Log.i("Dow", "0");
                                        } else {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/iWYV9rytTORa"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/ZqUV9VZYvoUi"), "#");
                                            Log.i("Dow", "5");
                                        }
                                        break;
                                }
                            }else{
                                switch (point_location) {
                                    case 'A':
                                        if ((Integer.valueOf(simpleDateFormat.format(date)) / 30)==1) {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/8K3UFO7AGIHp"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/RnJNVOXxrs1m"), "#");
                                            Log.i("Dow", "00");
                                        } else {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/RaTepfEAxOYr"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/T1OtHM6JUjrO"), "#");
                                            Log.i("Dow", "30");
                                        }
                                        break;
                                    case 'B':
                                        if ((Integer.valueOf(simpleDateFormat.format(date)) / 30) == 1) {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/uO2QjarCIheT"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/nu6h97PXSDNK"), "#");
                                            Log.i("Dow", "00");
                                        } else {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/ajDEqpvpIg8W"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/V7wzdr3whZM2"), "#");
                                            Log.i("Dow", "30");
                                        }
                                        break;
                                    case 'C':
                                        if ((Integer.valueOf(simpleDateFormat.format(date)) / 30) == 1) {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/L77BR8ggBFdo"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/6d8hWGSw4NSP"), "#");
                                            Log.i("Dow", "00");
                                        } else {
                                            st = new StringTokenizer(download("http://101.201.239.81:9009/down/emw956dZed92"), "#");
                                            sh = new StringTokenizer(download("http://101.201.239.81:9009/down/lvOBK25dXH1n"), "#");
                                            Log.i("Dow", "30");
                                        }
                                        break;
                                }
                            }
                            str = new String[st.countTokens()];
                            tempcount = 0;
                            while (st.hasMoreTokens()) {
                                str[tempcount] = st.nextToken();
                                tempcount++;
                            }
                            shr = new String[sh.countTokens()];
                            humiditycount = 0;
                            while (sh.hasMoreTokens()) {
                                shr[humiditycount] = sh.nextToken();
                                humiditycount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        if (progress != SeekBar1progress && MDtemp.Change == 0 && MDhumi.Change == 0) {
                            progress = SeekBar1progress;
                            MDtemp.currentValuechange = Float.parseFloat(str[(tempcount * progress) / 100]);
                            MDhumi.currentValuechange = Float.parseFloat(shr[(humiditycount * progress) / 100]);
                            MDtemp.Change = 1;
                            MDhumi.Change = 1;
                            Log.i("data",tempcount+"    "+progress+"    "+(tempcount * progress) / 100);
                        }
                    }catch (Exception e){
                    }
                }
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeallthread = true;

    }
}

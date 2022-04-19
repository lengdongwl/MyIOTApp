package com.vcchb.zncgxt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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

public class SecondMainActivityLinesPlan extends AppCompatActivity implements View.OnClickListener{
    MyView line_chart;
    static int k = 0;
    List<Integer> dataTemp=new ArrayList<Integer>();
    List<Integer> dataHumi=new ArrayList<Integer>();
    Thread th;
    private  Button button1;
    private  Button button2;
    private  Button button3;
    public static Button buttonselecthome;
    public static Button buttonselectinstrument;
    public static Button buttonselectcontrol;
    public static char point_location = 'A';
    public static int PL_serial_number = 1;
    char para = 0;
    int timer = 5;
    static int choice_mode = 0;
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
                onClickButton_MeterDiagram(v);
                break;
            default:
                break;
        }
    }
    private void onClickButton1(View view) {
        point_location = 'A';
        para = 0;
        PL_serial_number = 1;
        line_chart.Change=0;
        //处理逻辑
    }

    private void onClickButton2(View view) {
        point_location = 'B';
        para = 0;
        PL_serial_number = 2;
        line_chart.Change=0;
        //处理逻辑
    }

    private void onClickButton3(View view) {
        point_location = 'C';
        para = 0;
        PL_serial_number = 3;
        line_chart.Change=0;
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
        MainActivity.title = "控制页";
        startActivity(MainActivity.control_intent);
        finish();
        //处理逻辑
    }


    private void onClickButton_MeterDiagram(View view) {
        choice_mode=1;
        startActivity(MainActivity.instrument_intent2);
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
        setContentView(R.layout.activity_second);
        line_chart.Change = 0;
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

        line_chart = findViewById(R.id.line_chart);


        transmitswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton Button, boolean isChecked) {
                if (isChecked) {
                    Button.setText("三十分钟");
                    Toast.makeText(SecondMainActivityLinesPlan.this, "三十分钟", Toast.LENGTH_SHORT).show();
                    timer=30;
                } else {
                    Button.setText("一小时");
                    Toast.makeText(SecondMainActivityLinesPlan.this, "一小时", Toast.LENGTH_SHORT).show();
                    timer=60;
                }
            }
        });
    }


    public void DrawThread(){
//        new Threa
        new Thread(new Runnable() {
            @Override
            public void run() {
                vieNum=0;
                choice_mode  = 0;
                while (!closeallthread) {
                    if((vieNum!=MainActivity.vieNum)  && choice_mode==0){ //&& (MainActivity.title.equals("仪表页"))
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
                        line_chart.dataTemp = dataTemp;
                        line_chart.dataHumi = dataHumi;
                        line_chart.Change=0;
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");// HH:mm:ss
                            //获取当前时间
                            Date date = new Date(System.currentTimeMillis());
                            StringTokenizer st = null;
                            StringTokenizer sh = null;
                            if(timer==60) {
                                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH");// HH:mm:ss
                                //获取当前时间
                                Date date2 = new Date(System.currentTimeMillis());
                                switch (point_location) {
                                    case 'A':
                                        if ((Integer.valueOf(simpleDateFormat.format(date2)) / 2) == 0) {
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
                                        if ((Integer.valueOf(simpleDateFormat.format(date2)) / 2) == 0) {
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
                                        if ((Integer.valueOf(simpleDateFormat.format(date2)) / 2) == 0) {
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
                            String[] str = new String[st.countTokens()];
                            int j = 0;
                            while (st.hasMoreTokens()) {
                                str[j] = st.nextToken();
                                j++;
                            }
                            String[] shr = new String[sh.countTokens()];
                            int k = 0;
                            while (sh.hasMoreTokens()) {
                                shr[k] = sh.nextToken();
                                k++;
                            }
                            for (int i = 1; i < 51; i++) {
                                while (true) {
                                    if (line_chart.Change == 0) {
                                        line_chart.dataTemp.add(Integer.valueOf(str[((j*i)/50)]));
                                        line_chart.dataHumi.add(Integer.valueOf(shr[((k*i)/50)]));
                                        line_chart.Change = 1;
//                                        Log.i("data",j+"  "+i+"  "+((j*i)/50));
                                        Thread.sleep(1);
                                        break;
                                    }
                                }
                            }
                            line_chart.Change=2;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

package com.vcchb.zncgxt;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class WarnActivity extends AppCompatActivity implements View.OnClickListener{
    public static Button button1;
    public static Button button2;
    public static Button button3;
    public static Button button4;
    public static Button button5;
    int vieNum=0;
    public View vie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn);
        button1 = findViewById(R.id.Wbu1);
        button2 = findViewById(R.id.Wbu2);
        button3 = findViewById(R.id.Wbu3);
        button4 = findViewById(R.id.Wmessage);
        button5 = findViewById(R.id.Wbutdeng);
        //注册监听器
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        vie = findViewById(R.id.Wcontainer);
        //设置为最大音量
        AudioManager audioMa = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioMa.setStreamVolume(AudioManager.STREAM_MUSIC,audioMa.getStreamMaxVolume
                (AudioManager.STREAM_MUSIC),AudioManager.FLAG_SHOW_UI);
        final MediaPlayer mediaPlayer= MediaPlayer.create(WarnActivity.this, R.raw.warn);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                vieNum=0;
                while (MainActivity.Activitystate==2){
                    if ((vieNum != MainActivity.vieNum)) {//&& (MainActivity.title.equals("控制页"))
                        vieNum = MainActivity.vieNum;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Others.BackgroundAnimation(vie, vieNum);
                            }
                        });
                    }
                }
                mediaPlayer.stop();
            }
        }).start();
    }
//    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
//    {
//        public void onClick(DialogInterface dialog, int which)
//        {
//            switch (which)
//            {
//                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
//                    finish();
//                    break;
//                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Wbu1:
                onClickButton1(v);
                break;
            case R.id.Wbu2:
                onClickButton2(v);
                break;
            case R.id.Wbu3:
                onClickButton3(v);
                break;
            case R.id.Wmessage:
                onClickButton4(v);
                break;
            case R.id.Wbutdeng:
                onClickButton5(v);
                break;
            default:
                break;
        }
    }
    private void onClickButton1(View view) {
        //mess = "cmd tempA";
        //messNum = 1;
        //处理逻辑
    }

    private void onClickButton2(View view) {
        //mess = "cmd tempB";
        //messNum = 2;
        //处理逻辑
    }

    private void onClickButton3(View view) {
        //mess = "cmd tempC";
        //messNum = 3;
        //处理逻辑
    }
    private void onClickButton4(View view) {
        //click1();
        //处理逻辑
    }
    private void onClickButton5(View view) {
        //click1();
        //处理逻辑
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.Activitystate = 1;
    }
}

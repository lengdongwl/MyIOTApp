package com.vcchb.zncgxt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyView extends View{
    //坐标轴原点的位置
    private int xPointTemp;
    private int yPointTemp;
    private int xPointHumi;
    private int yPointHumi;
    //刻度长度
    private int xScaleTemp=10;  //8个单位构成一个刻度
    private int yScaleTemp=18;
    private int xScaleHumi=10;  //8个单位构成一个刻度
    private int yScaleHumi=9;
    //x与y坐标轴的长度
    private int xLengthTemp=500;
    private int yLengthTemp=900;
    private int xLengthHumi=500;


    private int yLengthHumi=900;

    private int MaxDataSizeTemp=xLengthTemp/xScaleTemp;   //横坐标  最多可绘制的点
    private int MaxDataSizeHumi=xLengthHumi/xScaleHumi;   //横坐标  最多可绘制的点

    public static List<Integer> dataTemp=new ArrayList<Integer>();   //存放 纵坐标 所描绘的点
    public static List<Integer> dataHumi=new ArrayList<Integer>();   //存放 纵坐标 所描绘的点

    private String[] yLabelTemp=new String[yLengthTemp/yScaleTemp];  //Y轴的刻度上显示字的集合
    private String[] yLabelHumi=new String[yLengthHumi/yScaleHumi];  //Y轴的刻度上显示字的集合
    public static int Change = 0;

    public Handler mh=new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0){                //判断接受消息类型
                MyView.this.invalidate();  //刷新View
            }
        };
    };
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i = 0; i <yLabelTemp.length; i++) {
            if (i!=0){
                if ((i)%2==0){
                    yLabelTemp[i]=(i)+"℃";
                }else{
                    yLabelTemp[i]="";
                }
            }else{
                yLabelTemp[i]="0"+"℃";
            }

        }
        for (int i = 0; i <yLabelHumi.length; i++) {
            if (i!=0){
                if((i)%5==0) {
                    yLabelHumi[i] = i + "%RH";
                }else{
                    yLabelHumi[i]="";
                }
            }else{
                yLabelHumi[i] = "0" + "%RH";
            }

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!SecondMainActivityLinesPlan.closeallthread){     //在线程中不断往集合中增加数据
                    if (Change==2){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                    }
                    if(Change==1) {
                        if (dataTemp.size() > MaxDataSizeTemp) {  //判断集合的长度是否大于最大绘制长度
                            dataTemp.remove(0);  //删除头数据
                        }
                        if (dataHumi.size() > MaxDataSizeHumi) {  //判断集合的长度是否大于最大绘制长度
                            dataHumi.remove(0);  //删除头数据
                        }
                        mh.sendEmptyMessage(0);   //发送空消息通知刷新
                        Change = 0;
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//X=1000 Y=1480
        xLengthTemp = 500;
        yLengthTemp = 900;
        xPointTemp = 60;
        yPointTemp = 920;
//        Log.i("kk",xLengthTemp+"xLengthTemp"+yLengthTemp+"yLengthTemp");

        xLengthHumi = 500;
        yLengthHumi = 900;
        xPointHumi = 650;
        yPointHumi = 920;
//        Log.i("kk",xLengthHumi+"xLengthHumi"+yLengthHumi+"yLengthHumi");
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(15);
        paint.setColor(0x9F92e6ed);
        //绘制Y轴
        try {
            canvas.drawLine(xPointTemp, yPointTemp - yLengthTemp, xPointTemp, yPointTemp, paint);
            //绘制Y轴左右两边的箭头
            canvas.drawLine(xPointTemp, yPointTemp - yLengthTemp, xPointTemp - 3, yPointTemp - yLengthTemp + 6, paint);
            canvas.drawLine(xPointTemp, yPointTemp - yLengthTemp, xPointTemp + 3, yPointTemp - yLengthTemp + 6, paint);
            //Y轴上的刻度与文字
            for (int i = 0; i * yScaleTemp < yLengthTemp; i++) {
                canvas.drawLine(xPointTemp, yPointTemp - i * yScaleTemp, xPointTemp + 5, yPointTemp - i * yScaleTemp, paint);  //刻度
                canvas.drawText(yLabelTemp[i], xPointTemp - 50, yPointTemp - i * yScaleTemp, paint);//文字
            }
            //X轴
            canvas.drawLine(xPointTemp, yPointTemp, xPointTemp + xLengthTemp, yPointTemp, paint);
            paint.setTextSize(30);
            switch (SecondMainActivityMeterDiagram.PL_serial_number){
                case 1:
                    canvas.drawText("前5分钟卧室单位温湿度折线图", xPointTemp + 370, yPointTemp + 40, paint);//文字
                    break;
                case 2:
                    canvas.drawText("前5分钟厨房单位温湿度折线图", xPointTemp + 370, yPointTemp + 40, paint);//文字
                    break;
                case 3:
                    canvas.drawText("前5分钟厕所单位温湿度折线图", xPointTemp + 370, yPointTemp + 40, paint);//文字
                    break;
            }
            paint.setTextSize(15);
            //绘制Y轴
            canvas.drawLine(xPointHumi, yPointHumi - yLengthHumi, xPointHumi, yPointHumi, paint);
            //绘制Y轴左右两边的箭头
            canvas.drawLine(xPointHumi, yPointHumi - yLengthHumi, xPointHumi - 3, yPointHumi - yLengthHumi + 6, paint);
            canvas.drawLine(xPointHumi, yPointHumi - yLengthHumi, xPointHumi + 3, yPointHumi - yLengthHumi + 6, paint);
            //Y轴上的刻度与文字
            for (int i = 0; i * yScaleHumi < yLengthHumi; i++) {
                canvas.drawLine(xPointHumi, yPointHumi - i * yScaleHumi, xPointHumi + 5, yPointHumi - i * yScaleHumi, paint);  //刻度
                canvas.drawText(yLabelHumi[i], xPointHumi - 50, yPointHumi - i * yScaleHumi, paint);//文字
            }
            //X轴
            canvas.drawLine(xPointHumi, yPointHumi, xPointHumi + xLengthHumi, yPointHumi, paint);

            paint.setStrokeWidth(5);
            Paint paint2 = new Paint();
            paint2.setColor(0x9F92e6ed);
            paint2.setStyle(Paint.Style.FILL);
            if (dataTemp.size() > 1) {
                Path path = new Path();
                Path path2 = new Path();
                path.moveTo(xPointTemp, yPointTemp - dataTemp.get(0) * yScaleTemp);
                path2.moveTo(xPointTemp, yPointTemp);
                for (int i = 0; i < dataTemp.size(); i++) {
                    path.lineTo(xPointTemp + i * xScaleTemp, yPointTemp - dataTemp.get(i) * yScaleTemp);
                    path2.lineTo(xPointTemp + i * xScaleTemp, yPointTemp - dataTemp.get(i) * yScaleTemp);
                }
                path2.lineTo(xPointTemp + (dataTemp.size() - 1) * xScaleTemp, yPointTemp);
                canvas.drawPath(path, paint);
                //canvas.drawPath(path2, paint2);
            }
            if (dataHumi.size() > 1) {
                Path path = new Path();
                Path path2 = new Path();
                path.moveTo(xPointHumi, yPointHumi - dataHumi.get(0) * yScaleHumi);
                path2.moveTo(xPointHumi, yPointHumi);
                for (int i = 0; i < dataHumi.size(); i++) {
                    path.lineTo(xPointHumi + i * xScaleHumi, yPointHumi - dataHumi.get(i) * yScaleHumi);
                    path2.lineTo(xPointHumi + i * xScaleHumi, yPointHumi - dataHumi.get(i) * yScaleHumi);
                }
                path2.lineTo(xPointHumi + (dataHumi.size() - 1) * xScaleHumi, yPointHumi);
                canvas.drawPath(path, paint);
                //canvas.drawPath(path2, paint2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

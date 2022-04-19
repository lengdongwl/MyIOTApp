package com.vcchb.zncgxt;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.text.DecimalFormat;
import java.util.Random;

import static android.view.View.LAYER_TYPE_SOFTWARE;

public class DashboardView extends View {

    private static final int DEFAULT_COLOR_MIDDLE = Color.parseColor("#228fbd");
    private static final int DEFAULT_COLOR_TITLE = Color.WHITE;
    private static final int DEFAULT_TEXT_SIZE_DIAL = 11;
    private static final int DEFAULT_STROKE_WIDTH = 2;
    private static final int DEFAULT_RADIUS_DIAL = 128;
    private static final int DEFAULT_TITLE_SIZE = 22;
    private static final int DEFAULT_ANIM_PLAY_TIME = 2000;
    private static final int DEFAULT_border = 5;

    private int colorDialMiddle;
    private int textSizeDial;
    private int strokeWidthDial;
    private int titleDialSize;
    private int titleDialColor;
    private int animPlayTime;
    private float openAngle = 120;// 底部开口的角度
    private int radiusDial;
    private int mRealRadius;
    private float currentValue=0;
    private int clockPointNum = 100;
    private int clockMinValue = 0;
    private String dataUnit = "℃";

    private Paint arcPaint;
    private RectF mRect;
    private Paint pointerPaint;
    private Paint.FontMetrics fontMetrics;
    private Paint titlePaint;
    private Path pointerPath;
    public static int Change = 0;
    public float currentValuechange=0;

    public Handler mh=new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0){                //判断接受消息类型
                DashboardView.this.invalidate();  //刷新View
            }
        };
    };

    public DashboardView(Context context) {
        this(context, null);
    }

    public DashboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!SecondMainActivityMeterDiagram.closeallthread){     //在线程中不断往集合中增加数据
                    if (Change==2){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                    }
                    if(Change==1) {
                        currentValue = currentValuechange;
//                        currentValue = (new Random().nextInt(90)+5);  //生成1-6的随机数
                        mh.sendEmptyMessage(0);   //发送空消息通知刷新
                        Change = 0;
                    }
                }
            }
        }).start();
//        关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
//        初始化属性
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.DashboardView);
        colorDialMiddle = attributes.getColor(R.styleable.DashboardView_color_dial_middle, DEFAULT_COLOR_MIDDLE);
        textSizeDial = (int) attributes.getDimension(R.styleable.DashboardView_text_size_dial, sp2px(DEFAULT_TEXT_SIZE_DIAL));
        strokeWidthDial = (int) attributes.getDimension(R.styleable.DashboardView_stroke_width_dial, dp2px(DEFAULT_STROKE_WIDTH));
        radiusDial = (int) attributes.getDimension(R.styleable.DashboardView_radius_circle_dial, dp2px(DEFAULT_RADIUS_DIAL));
        titleDialSize = (int) attributes.getDimension(R.styleable.DashboardView_text_title_size, dp2px(DEFAULT_TITLE_SIZE));
        titleDialColor = attributes.getColor(R.styleable.DashboardView_text_title_color, DEFAULT_COLOR_TITLE);
        animPlayTime = attributes.getInt(R.styleable.DashboardView_animator_play_time, DEFAULT_ANIM_PLAY_TIME);
        attributes.recycle();
    }

    private void initPaint() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(strokeWidthDial);
        arcPaint.setShadowLayer(10, 0, 0, Color.parseColor("#35FCFB"));

        pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointerPaint.setTextSize(textSizeDial);
        pointerPaint.setTextAlign(Paint.Align.CENTER);
        fontMetrics = pointerPaint.getFontMetrics();

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);

        pointerPath = new Path();
    }

    public void setClockPointNum(int clockPointNum) {
        this.clockPointNum = clockPointNum;
        postInvalidate();
    }
    //设置仪表最大最小值以及中间单位文字
    public void setClockValueArea(int clockMinValue, int clockMaxValue, String dataUnit) {
        this.clockMinValue = clockMinValue;
        this.dataUnit = dataUnit;
        setClockPointNum(clockMaxValue - clockMinValue);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int mWidth, mHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = getPaddingLeft() + radiusDial * 2 + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                mWidth = Math.min(mWidth, widthSize);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = getPaddingTop() + radiusDial * 2 + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                mHeight = Math.min(mHeight, heightSize);
            }
        }

        setMeasuredDimension(mWidth, mHeight);

        radiusDial = Math.min((getMeasuredWidth() - getPaddingLeft() - getPaddingRight()),
                (getMeasuredHeight() - getPaddingTop() - getPaddingBottom())) / 2;
        mRealRadius = radiusDial - strokeWidthDial / 2 - DEFAULT_border * 2;
        mRect = new RectF(-mRealRadius - DEFAULT_border, -mRealRadius - DEFAULT_border,
                mRealRadius + DEFAULT_border, mRealRadius + DEFAULT_border);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        step1 画圆弧
        drawArc(canvas);
//        step2 绘制刻度和数字
        drawPointerLine(canvas);
//        step3 画指针阴影
        drawPointShadow(canvas);
//        step4 绘制中间黑色圆形背景
        drawBlackCircle(canvas);
//        step5 绘制表针
        drawPointer(canvas);
//        step6 绘制深蓝色发光圆形
        drawBlueCircle(canvas);
//        step7 绘制表盘中的数字
        drawCircleText(canvas);
    }

    private void drawPointShadow(Canvas canvas) {
        int currentDegree = (int) ((currentValue - clockMinValue) * ((360 - openAngle) / clockPointNum) + 150);
        canvas.rotate(currentDegree);

        int[] colorSweep = {0xAAFFE9EC, 0x0028E9EC, 0xAA28E9EC};
        float[] position = {0f, 0.9f, 1f};
        SweepGradient mShader = new SweepGradient(0, 0, colorSweep, position);

        arcPaint.setShader(mShader);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth((float) (radiusDial * 0.4));
        arcPaint.clearShadowLayer();
        RectF mRect = new RectF((float) (-mRealRadius - DEFAULT_border + radiusDial * 0.2), (float) (-mRealRadius - DEFAULT_border + radiusDial * 0.2),
                (float) (mRealRadius + DEFAULT_border - radiusDial * 0.2), (float) (mRealRadius + DEFAULT_border - radiusDial * 0.2));
        canvas.drawArc(mRect, 360 - (currentDegree - 150), (currentDegree - 150), false, arcPaint);
    }

    private void drawPointer(Canvas canvas) {
        canvas.save();
        int currentDegree = (int) ((currentValue - clockMinValue) * ((360 - openAngle) / clockPointNum) + 150);

        canvas.rotate(currentDegree);
        titlePaint.setColor(Color.WHITE);
        titlePaint.setAntiAlias(true);
        pointerPath.moveTo(radiusDial - dp2px(12), 0);
        pointerPath.lineTo(0, -dp2px(5));
        pointerPath.lineTo(-12, 0);
        pointerPath.lineTo(0, dp2px(5));
        pointerPath.close();
        canvas.drawPath(pointerPath, titlePaint);

        canvas.save();
        canvas.restore();
    }

    private void drawCircleText(Canvas canvas) {
        titlePaint.setColor(Color.WHITE);
        titlePaint.setColor(titleDialColor);
        titlePaint.setTextSize(titleDialSize);
        canvas.drawText(formatData(currentValue), 0, 0, titlePaint);
        titlePaint.setColor(Color.parseColor("#38F9FD"));
        titlePaint.setTextSize(sp2px(14));
        canvas.drawText("(" + dataUnit + ")", 0, dp2px(18), titlePaint);
    }

    private void drawBlueCircle(Canvas canvas) {
        canvas.rotate(0);
        canvas.restore();
        Paint pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setColor(Color.parseColor("#050D3D"));
        pointerPaint.setShadowLayer(15, 0, 0, Color.parseColor("#006EC6"));
        canvas.drawCircle(0, 0, (float) (radiusDial * 0.4), pointerPaint);
    }

    private void drawBlackCircle(Canvas canvas) {
        canvas.restore();
        canvas.translate(getPaddingLeft() + radiusDial, getPaddingTop() + radiusDial);
        Paint pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setColor(Color.parseColor("#05002D"));
        canvas.drawCircle(0, 0, (float) (radiusDial * 0.6), pointerPaint);
    }

    private void drawArc(Canvas canvas) {
        canvas.translate(getPaddingLeft() + radiusDial, getPaddingTop() + radiusDial);
        arcPaint.setShader(null);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setAntiAlias(true);
        arcPaint.setAlpha(70);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(strokeWidthDial);
        arcPaint.setShadowLayer(10, 0, 0, Color.parseColor("#FFFFFF"));
        arcPaint.setColor(Color.parseColor("#38F9FD"));
        canvas.drawArc(mRect, 150, (360 - openAngle), false, arcPaint);
    }

    private void drawPointerLine(Canvas canvas) {
//        旋转画布 （坐标系）
        canvas.rotate(150);

        for (int i = 0; i < clockPointNum + 1; i++) {
            pointerPaint.setColor(colorDialMiddle);

            if (i % 10 == 0) {     //长表针
                pointerPaint.setStrokeWidth(3);
                canvas.drawLine(radiusDial - DEFAULT_border - strokeWidthDial, 0, radiusDial - strokeWidthDial - dp2px(15), 0, pointerPaint);
                drawPointerText(canvas, i);
            } else if (i % 5 == 0f) {    //短表针
                pointerPaint.setStrokeWidth(2);
                canvas.drawLine(radiusDial - DEFAULT_border - strokeWidthDial, 0, radiusDial - strokeWidthDial - dp2px(9), 0, pointerPaint);
            }
            canvas.rotate((360 - openAngle) / clockPointNum);
        }
        canvas.rotate(-((180 - openAngle) / 2 + ((360 - openAngle) / clockPointNum)));
    }

    private void drawPointerText(Canvas canvas, int i) {
        canvas.save();
        pointerPaint.setColor(getResources().getColor(R.color.white));
        int currentCenterX = (int) (radiusDial - strokeWidthDial - dp2px(21) - pointerPaint.measureText(String.valueOf(i)) / 2);
        canvas.translate(currentCenterX, 0);

        canvas.rotate(360 - 150 - ((360 - openAngle) / clockPointNum) * i);        //坐标系总旋转角度为360度

        int textBaseLine = (int) (0 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
        canvas.drawText(String.valueOf(i + clockMinValue), 0, textBaseLine, pointerPaint);
        canvas.restore();
    }

    public void setCompleteDegree(float degree) {
        ValueAnimator animator = ValueAnimator.ofFloat(currentValue, degree);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) (Math.round((float) animation.getAnimatedValue() * 10)) / 10;
                invalidate();
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(animPlayTime);
        animator.start();
    }

    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }

    protected String formatData(float num) {
        DecimalFormat decimalFormat = new DecimalFormat("###.#");
        return decimalFormat.format(num);
    }

}



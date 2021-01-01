package com.giftedcat.wavelib.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * @Description: 波形图绘制控件
 * @Author: GiftedCat
 * @Date: 2021-01-01
 */
public class WaveView extends View {

    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

    /** 宽高*/
    private float mWidth = 0, mHeight = 0;
    /** 网格画笔*/
    private Paint mLinePaint;
    /** 数据线画笔*/
    private Paint mWavePaint;
    /** 线条的路径*/
    private Path mPath;

    /** 保存已绘制的数据坐标*/
    private ArrayList dataList = new ArrayList();

    /** 数据最大值，默认-20~20之间 */
    private float MAX_VALUE = 20;
    /** 线条粗细*/
    private float WAVE_LINE_STROKE_WIDTH = 3;
    /** 波形颜色*/
    private int waveLineColor = Color.parseColor("#EE4000");
    /** 当前的x，y坐标*/
    private float nowX, nowY;
    /** 线条的长度，可用于控制横坐标*/
    private int WAVE_LINE_WIDTH = 10;
    /** 数据点的数量*/
    private int row;


    /** 网格是否可见*/
    private boolean gridVisible;
    /** 网格的宽高*/
    private final int GRID_WIDTH = 50;
    /** 网格的横线和竖线的数量*/
    private int gridHorizontalNum, gridVerticalNum;
    /** 网格线条的粗细*/
    private final int GRID_LINE_WIDTH = 2;
    /** 网格颜色*/
    private int gridLineColor = Color.parseColor("#1b4200");

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(AttributeSet attrs) {
        MAX_VALUE = attrs.getAttributeIntValue(NAMESPACE, "max_value", 20);
        WAVE_LINE_WIDTH = attrs.getAttributeIntValue(NAMESPACE, "wave_line_width", 10);
        WAVE_LINE_STROKE_WIDTH = attrs.getAttributeIntValue(NAMESPACE, "wave_line_stroke_width", 3);
        gridVisible = attrs.getAttributeBooleanValue(NAMESPACE, "grid_visible", true);


        String wave_line_color = attrs.getAttributeValue(NAMESPACE, "wave_line_color");
        if (wave_line_color != null && !wave_line_color.isEmpty()){
            waveLineColor = Color.parseColor(wave_line_color);
        }

        String grid_line_color = attrs.getAttributeValue(NAMESPACE, "grid_line_color");
        if (grid_line_color != null && !grid_line_color.isEmpty()){
            gridLineColor = Color.parseColor(grid_line_color);
        }

        String wave_background = attrs.getAttributeValue(NAMESPACE, "wave_background");
        if (wave_background != null && !wave_background.isEmpty()){
            setBackgroundColor(Color.parseColor(wave_background));
        }


        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(GRID_LINE_WIDTH);
        /** 抗锯齿效果*/
        mLinePaint.setAntiAlias(true);

        mWavePaint = new Paint();
        mWavePaint.setStyle(Paint.Style.STROKE);
        mWavePaint.setColor(waveLineColor);
        mWavePaint.setStrokeWidth(WAVE_LINE_STROKE_WIDTH);
        /** 抗锯齿效果*/
        mWavePaint.setAntiAlias(true);

        mPath = new Path();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        /** 获取控件的宽高*/
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        /** 根据线条长度，最多能绘制多少个数据点*/
        row = (int) (mWidth / WAVE_LINE_WIDTH);

        /** 根据网格的单位长宽，获取能绘制网格横线和竖线的数量*/
        gridHorizontalNum = (int) (mHeight / GRID_WIDTH);
        gridVerticalNum = (int) (mWidth / GRID_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /** 绘制网格*/
        if (gridVisible){
            drawGrid(canvas);
        }
        /** 绘制折线*/
        drawWaveLine(canvas);
    }

    /**
     * 绘制折线
     *
     * @param canvas
     */
    private void drawWaveLine(Canvas canvas) {
        if (null == dataList || dataList.size() <= 0) {
            return;
        }
        mPath.reset();
        mPath.moveTo(0f, mHeight / 2);
        for (int i = 0; i < dataList.size(); i++) {
            nowX = i * WAVE_LINE_WIDTH;
            float dataValue = (float) dataList.get(i);
            /** 判断数据为正数还是负数  超过最大值的数据按最大值来绘制*/
            if (dataValue > 0) {
                if (dataValue > MAX_VALUE) {
                    dataValue = MAX_VALUE;
                }
            } else {
                if (dataValue < -MAX_VALUE) {
                    dataValue = -MAX_VALUE;
                }
            }
            nowY = mHeight / 2 - dataValue * (mHeight / (MAX_VALUE * 2));
            mPath.lineTo(nowX, nowY);
        }
        canvas.drawPath(mPath, mWavePaint);
        if (dataList.size() > row) {
            dataList.remove(0);
        }
    }

    /**
     * 绘制网格
     *
     * @param canvas
     * */
    private void drawGrid(Canvas canvas) {
        /** 设置颜色*/
        mLinePaint.setColor(gridLineColor);
        /** 绘制横线*/
        for (int i = 0; i < gridHorizontalNum + 1; i++) {
            canvas.drawLine(0, i * GRID_WIDTH,
                    mWidth, i * GRID_WIDTH, mLinePaint);
        }
        /** 绘制竖线*/
        for (int i = 0; i < gridVerticalNum + 1; i++) {
            canvas.drawLine(i * GRID_WIDTH, 0,
                    i * GRID_WIDTH, mHeight, mLinePaint);
        }
    }

    /**
     * 添加新的数据
     * */
    public void showLine(float line) {
        dataList.add(line);
        postInvalidate();
    }

    /** 清空已有数据*/
    public void clearAllLine() {
        dataList.clear();
    }


    public WaveView setMaxValue(int max_value){
        this.MAX_VALUE = max_value;
        return this;
    }

    public WaveView setWaveLineWidth(int width){
        this.WAVE_LINE_WIDTH = width;
        return this;
    }

    public WaveView setWaveLineStrokeWidth(int width){
        this.WAVE_LINE_WIDTH = width;
        return this;
    }

    public WaveView setWaveLineColor(String colorString){
        this.waveLineColor = Color.parseColor(colorString);
        return this;
    }

    public WaveView setGridVisible(boolean visible){
        this.gridVisible = visible;
        return this;
    }

    public WaveView setGridLineColor(String colorString){
        this.gridLineColor = Color.parseColor(colorString);
        return this;
    }

    public WaveView setWaveBackground(String colorString){
        setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

}

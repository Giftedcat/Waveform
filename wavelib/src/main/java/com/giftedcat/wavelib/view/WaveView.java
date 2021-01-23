package com.giftedcat.wavelib.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 波形图绘制控件
 * @Author: GiftedCat
 * @Date: 2021-01-01
 */
public class WaveView extends View {

    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

    /** 常规绘制模式 不断往后推的方式*/
    public static int NORMAL_MODE = 0;

    /** 循环绘制模式*/
    public static int LOOP_MODE = 1;

    /** 绘制模式*/
    private int drawMode;

    /**
     * 宽高
     */
    private float mWidth = 0, mHeight = 0;
    /**
     * 网格画笔
     */
    private Paint mLinePaint;
    /**
     * 数据线画笔
     */
    private Paint mWavePaint;
    /**
     * 线条的路径
     */
    private Path mPath;

    /**
     * 保存已绘制的数据坐标
     */
    private float[] dataArray;

    /**
     * 数据最大值，默认-20~20之间
     */
    private float MAX_VALUE = 20;
    /**
     * 线条粗细
     */
    private float WAVE_LINE_STROKE_WIDTH = 3;
    /**
     * 波形颜色
     */
    private int waveLineColor = Color.parseColor("#EE4000");
    /**
     * 当前的x，y坐标
     */
    private float nowX, nowY;

    private float startY;

    /**
     * 线条的长度，可用于控制横坐标
     */
    private int WAVE_LINE_WIDTH = 10;
    /**
     * 数据点的数量
     */
    private int row;

    private int draw_index;


    /**
     * 网格是否可见
     */
    private boolean gridVisible;
    /**
     * 网格的宽高
     */
    private final int GRID_WIDTH = 50;
    /**
     * 网格的横线和竖线的数量
     */
    private int gridHorizontalNum, gridVerticalNum;
    /**
     * 网格线条的粗细
     */
    private final int GRID_LINE_WIDTH = 2;
    /**
     * 网格颜色
     */
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
        drawMode = attrs.getAttributeIntValue(NAMESPACE, "draw_mode", NORMAL_MODE);


        String wave_line_color = attrs.getAttributeValue(NAMESPACE, "wave_line_color");
        if (wave_line_color != null && !wave_line_color.isEmpty()) {
            waveLineColor = Color.parseColor(wave_line_color);
        }

        String grid_line_color = attrs.getAttributeValue(NAMESPACE, "grid_line_color");
        if (grid_line_color != null && !grid_line_color.isEmpty()) {
            gridLineColor = Color.parseColor(grid_line_color);
        }

        String wave_background = attrs.getAttributeValue(NAMESPACE, "wave_background");
        if (wave_background != null && !wave_background.isEmpty()) {
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

        /** 根据网格的单位长宽，获取能绘制网格横线和竖线的数量*/
        gridHorizontalNum = (int) (mHeight / GRID_WIDTH);
        gridVerticalNum = (int) (mWidth / GRID_WIDTH);

        /** 根据线条长度，最多能绘制多少个数据点*/
        row = (int) (mWidth / WAVE_LINE_WIDTH);
        dataArray = new float[row + 10];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /** 绘制网格*/
        if (gridVisible) {
            drawGrid(canvas);
        }
        /** 绘制折线*/
        switch (drawMode){
            case 0:
                drawWaveLineNormal(canvas);
                break;
            case 1:
                drawWaveLineLoop(canvas);
                break;
        }
    }

    /**
     * 常规模式绘制折线
     *
     * @param canvas
     * */
    private void drawWaveLineNormal(Canvas canvas){
        drawPathFromDatas(canvas, 0, row);
        if (drawMode == NORMAL_MODE){
            for (int i=0;i<row;i++){
                dataArray[i] = dataArray[i + 1];
            }
        }
    }

    /**
     * 循环模式绘制折线
     *
     * @param canvas
     */
    private void drawWaveLineLoop(Canvas canvas) {
        if (draw_index < row - 7){
            /** 绘制两条中间有断开的线*/
            drawPathFromDatas(canvas, 0, draw_index - 3);
            drawPathFromDatas(canvas, draw_index + 5, row);
        }else {
            /** 数据绘制到末尾，则只绘制一条线*/
            drawPathFromDatas(canvas, 0, draw_index - 1);
        }
    }

    /**
     * 取数组中的指定一段数据来绘制折线
     * @param start 起始数据位
     * @param end 结束数据位
     * */
    private void drawPathFromDatas(Canvas canvas, int start, int end){
        mPath.reset();
        startY = mHeight / 2 - dataArray[start] * (mHeight / (MAX_VALUE * 2));
        mPath.moveTo(start * WAVE_LINE_WIDTH, startY);
        for (int i = start; i < end; i++) {
            nowX = i * WAVE_LINE_WIDTH;
            float dataValue = dataArray[i];
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
    }

    /**
     * 绘制网格
     *
     * @param canvas
     */
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
     */
    public void showLine(float line) {
        if (draw_index >= row) {
            draw_index = 0;
        }
        switch (drawMode){
            case 0:
                /** 常规模式数据添加至最后一位*/
                dataArray[row - 1] = line;
                break;
            case 1:
                /** 循环模式数据添加至当前绘制的位*/
                dataArray[draw_index] = line;
                Log.i("now draw is----------", draw_index + "");
                break;
        }
        draw_index += 1;
        postInvalidate();
    }


    public WaveView setMaxValue(int max_value) {
        this.MAX_VALUE = max_value;
        return this;
    }

    public WaveView setWaveLineWidth(int width) {
        this.WAVE_LINE_WIDTH = width;
        return this;
    }

    public WaveView setWaveLineStrokeWidth(int width) {
        this.WAVE_LINE_WIDTH = width;
        return this;
    }

    public WaveView setWaveLineColor(String colorString) {
        this.waveLineColor = Color.parseColor(colorString);
        return this;
    }

    public WaveView setGridVisible(boolean visible) {
        this.gridVisible = visible;
        return this;
    }

    public WaveView setGridLineColor(String colorString) {
        this.gridLineColor = Color.parseColor(colorString);
        return this;
    }

    public WaveView setWaveBackground(String colorString) {
        setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

    public WaveView setWaveDrawMode(int draw_mode){
        this.drawMode = draw_mode;
        return this;
    }

}

package com.giftedcat.wavelib.utils;

import android.util.Log;

import com.giftedcat.wavelib.view.WaveView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description: 曲线绘制工具类
 * @Author: GiftedCat
 * @Date: 2021-01-01
 */
public class WaveUtil {

    private Timer timer;
    private TimerTask timerTask;

    float data = 0f;
    float[] datas;

    /**
     * 模拟数据
     */
    public void showWaveData(final WaveView waveShowView){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                data = new Random().nextFloat()*(20f)-10f;
                waveShowView.showLine(data);//取得是-10到10间的浮点数
            }
        };
        //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        timer.schedule(timerTask,500,50);
    }

    /**
     * 模拟一次注入多条数据
     * @param length 需要一次性注入数据的数量
     * @param waveShowView 控件
     * */
    public void showWaveDatas(int length, final WaveView waveShowView){
        datas = new float[length];
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                /** 随机生成5条数据*/
                for (int i=0;i<datas.length;i++){
                    datas[i] = new Random().nextFloat()*(20f)-10f;
                }
                waveShowView.showLines(datas);
            }
        };
        //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        timer.schedule(timerTask,500,50);
    }

    /**
     * 停止绘制
     */
    public void stop(){
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if(null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}

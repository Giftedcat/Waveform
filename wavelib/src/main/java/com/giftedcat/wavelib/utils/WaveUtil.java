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

    /**
     * 模拟数据
     */
    public void showWaveData(final WaveView waveShowView){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                data = new Random().nextFloat()*(20f)-10f;
                Log.i("data is", data + "");
                waveShowView.showLine(data);//取得是-20到20间的浮点数
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

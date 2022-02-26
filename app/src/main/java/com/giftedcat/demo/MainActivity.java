package com.giftedcat.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.giftedcat.wavelib.utils.WaveUtil;
import com.giftedcat.wavelib.view.WaveView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private WaveUtil waveUtil1, waveUtil2;

    private WaveView wave_view1, wave_view2;

    private SeekBar seekBar;

//    float data = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waveUtil1 = new WaveUtil();
        waveUtil2 = new WaveUtil();

        wave_view1 = findViewById(R.id.wave_view1);
        wave_view2 = findViewById(R.id.wave_view2);
        seekBar = findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("seek_bar progress is", i + "");
                if (i == 0)
                    return;
                wave_view1.setWaveLineWidth(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        waveUtil1.showWaveDatas(2, wave_view1);
        waveUtil2.showWaveDatas(1, wave_view2);
//        findViewById(R.id.tv_wave1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                data = new Random().nextFloat()*(20f)-10f;
//                Log.i("data is --------------", data + "");
//                wave_view1.showLine(data);//取得是-10到10间的浮点数
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        waveUtil1.stop();
        waveUtil2.stop();
    }
}

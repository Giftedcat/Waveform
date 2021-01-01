package com.giftedcat.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.giftedcat.wavelib.utils.WaveUtil;
import com.giftedcat.wavelib.view.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveUtil waveUtil1, waveUtil2;

    private WaveView wave_view1, wave_view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waveUtil1 = new WaveUtil();
        waveUtil2 = new WaveUtil();

        wave_view1 = findViewById(R.id.wave_view1);
        wave_view2 = findViewById(R.id.wave_view2);

        waveUtil1.showWaveData(wave_view1);
        waveUtil2.showWaveData(wave_view2);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        waveUtil1.stop();
        waveUtil2.stop();
    }
}

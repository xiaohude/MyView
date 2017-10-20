package com.smarttiger.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class WaveBatteryLayout extends FrameLayout {

    private WaveBattery mWaveBattery;
    private View mBatteryPole;

    public WaveBatteryLayout(Context context) {
        super(context);
        initView();
    }

    public WaveBatteryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.wave_battery_layout, this);
        mWaveBattery = (WaveBattery)findViewById(R.id.wave_battery);
        mBatteryPole = findViewById(R.id.battery_pole);

    }

    public void updateLevel(int level) {
    	mWaveBattery.setProgress(level);
        if(level  == 100) {
        	mWaveBattery.setProgress(120);
        	mBatteryPole.setBackgroundResource(R.drawable.battery_pole_full_bg);
        } else {
        	mBatteryPole.setBackgroundResource(R.drawable.battery_pole_normal_bg);
        }
    }
}

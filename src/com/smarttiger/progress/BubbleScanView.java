package com.smarttiger.progress;

import com.smarttiger.myview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

public class BubbleScanView extends BubbleBaseView {

    public BubbleScanView(Context context) {
        super(context);
        init(null, 0);
    }

    public BubbleScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BubbleScanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleScanView, defStyle, 0);
        mScanSpriteSlowDownSpeed = mScanSpriteRotateSpeed = a.getFloat(R.styleable.BubbleScanView_spriteRotateSpeed, mScanSpriteRotateSpeed);
        mScanText = a.hasValue(R.styleable.BubbleScanView_text) ? a.getString(R.styleable.BubbleScanView_text) : "scan";
        mScanTextSize = a.getDimensionPixelSize(R.styleable.BubbleScanView_scanTextSize, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics()));
        a.recycle();

        setType(SPRITE_TYPE_SCAN);
    }

    public void startScanAnim() {
        initScanState();
        shouldShowScanAnim = true;
        shouldRotateSprite = true;
        invalidate();
    }

    public void finishScanAnim() {
        shouldFinishScanAnim = true;
    }

    public void setSpriteRotateSpeed(float speed) {
        mScanSpriteRotateSpeed = speed;
        if (!shouldFinishScanAnim)
            mScanSpriteSlowDownSpeed = mScanSpriteRotateSpeed;
    }
}

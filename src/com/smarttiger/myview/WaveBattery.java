
package com.smarttiger.myview;


import com.smarttiger.utils.UiUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

public class WaveBattery extends View {

    // 波纹颜色
    private static final int WAVE_PAINT_COLOR = 0xaa00cc00;
    // y = Asin(wx+b)+h
    // 振幅
    private static final float STRETCH_FACTOR_A = 10;
    private static final int OFFSET_Y = 0;
    // 第一条水波移动速度
    private static final int TRANSLATE_X_SPEED_ONE = 3;
    // 第二条水波移动速度
    private static final int TRANSLATE_X_SPEED_TWO = 2;
    // 圆角半径
    private int CORNER_RADIUS = 10;
    private float mCycleFactorW;

    private int mTotalWidth, mTotalHeight;
    // 可以加padding来调整背景位置
    private int mPaddingTop = 0;
    private int mPaddingBottom = 0;
    private int mCenterY = 200; //水波纹的高度-电池电量高度，和canvas的坐标相反
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;

    private Context mContext;
    private Paint mWavePaint;
    private DrawFilter mDrawFilter;

    public WaveBattery(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_TWO);
        CORNER_RADIUS = UiUtils.dipToPx(context, CORNER_RADIUS);

        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Style.FILL);
        // 设置画笔颜色
        mWavePaint.setColor(WAVE_PAINT_COLOR);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        
        setBackgroundResource(R.drawable.battery_capsule_bg);
    }
    
    public void setProgress(int level) {  
        mCenterY = (int) (mTotalHeight * ((double) level / 100));  
    }  

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(mDrawFilter);
        
        resetPositonY();
        for (int x = 0; x < CORNER_RADIUS; x++) {
        	float conerY = getRadiusY(CORNER_RADIUS - x);
        	float startY1 = mPaddingTop + mTotalHeight - mResetOneYPositions[x] - mCenterY;
        	float startY2 = mPaddingTop + mTotalHeight - mResetTwoYPositions[x] - mCenterY;
        	float stopY = mPaddingTop + mTotalHeight - conerY;

            if(mCenterY < mTotalHeight - CORNER_RADIUS - STRETCH_FACTOR_A) {
            	startY1 = startY1 < stopY ? startY1 : stopY;
            	startY2 = startY2 < stopY ? startY2 : stopY;
            } else {
            	startY1 = startY1 > conerY ? startY1 : conerY;
            	startY2 = startY2 > conerY ? startY2 : conerY;
            }
            // 绘制第一条水波纹
            canvas.drawLine(x, startY1, x, stopY, mWavePaint);
            // 绘制第二条水波纹
            canvas.drawLine(x, startY2, x, stopY, mWavePaint);
        }
        for (int x = CORNER_RADIUS; x < mTotalWidth - CORNER_RADIUS; x++) {
        	// 减mCenterY只是为了控制波纹绘制的y的在屏幕的位置，大家可以改成一个变量，然后动态改变这个变量，从而形成波纹上升下降效果
        	// 绘制第一条水波纹
        	canvas.drawLine(
        			x, mPaddingTop + mTotalHeight - mResetOneYPositions[x] - mCenterY, 
        			x, mPaddingTop + mTotalHeight,
        			mWavePaint);
        	// 绘制第二条水波纹
        	canvas.drawLine(
        			x, mPaddingTop + mTotalHeight - mResetTwoYPositions[x] - mCenterY, 
        			x, mPaddingTop + mTotalHeight,
        			mWavePaint);
        }
        for (int x = mTotalWidth - CORNER_RADIUS; x < mTotalWidth; x++) {

        	float conerY = getRadiusY(CORNER_RADIUS - mTotalWidth + x);
        	float startY1 = mPaddingTop + mTotalHeight - mResetOneYPositions[x] - mCenterY;
        	float startY2 = mPaddingTop + mTotalHeight - mResetTwoYPositions[x] - mCenterY;
        	float stopY = mPaddingTop + mTotalHeight - conerY;

            if(mCenterY < mTotalHeight - CORNER_RADIUS - STRETCH_FACTOR_A) {
            	startY1 = startY1 < stopY ? startY1 : stopY;
            	startY2 = startY2 < stopY ? startY2 : stopY;
            } else {
            	startY1 = startY1 > conerY ? startY1 : conerY;
            	startY2 = startY2 > conerY ? startY2 : conerY;
            }
        	
        	// 绘制第一条水波纹
            canvas.drawLine(x, startY1, x, stopY, mWavePaint);
            // 绘制第二条水波纹
            canvas.drawLine(x, startY2, x, stopY, mWavePaint);
        }
        

        // 改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }

        // 引发view重绘，一般可以考虑延迟20-30ms重绘，空出时间片
        postInvalidate();
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }
    
    // 获取圆角矩形，圆角对应的y值。
    private float getRadiusY(float x) {
    	float y;
    	y = (float) Math.sqrt((CORNER_RADIUS*CORNER_RADIUS) - (x*x));
    	return CORNER_RADIUS - y;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        mTotalWidth = w;
        mTotalHeight = h - mPaddingBottom - mPaddingTop;
        // 用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mTotalWidth];
        // 用于保存波纹二的y值
        mResetTwoYPositions = new float[mTotalWidth];

        // 将周期定为view总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
    }

}

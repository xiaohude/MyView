package com.smarttiger.progress;

import com.smarttiger.myview.R;
import com.smarttiger.myview.R.styleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;


public class CircleProgressImageView extends ImageView {

    private static final int RIPPLE_ALPHA_MAX = 255;
    private static final int RIPPLE_TIME = 4;

    private Paint mRipplePaint, mProgressPaint;

    private boolean shouldShowProgress;
    private boolean shouldRippling;
    private int mRippleRadius, mRippleAlpha, mRippleTime;
    private int mProgress = 0;
    private int mContentSize, mProgressWidth;

    public CircleProgressImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleProgressImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressImageView, defStyle, 0);
        shouldShowProgress = a.getBoolean(R.styleable.CircleProgressImageView_showProgress, false);
        a.recycle();

        setScaleType(ScaleType.CENTER_INSIDE);
        initPaint();
        initState();
    }

    private void initPaint() {

        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setColor(Color.RED);
        mRipplePaint.setStyle(Paint.Style.FILL);
        mRipplePaint.setAlpha(RIPPLE_ALPHA_MAX);

        mProgressWidth = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()) + 0.5f);
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(Color.RED);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    protected void initState() {
        shouldRippling = false;
        mRippleAlpha = RIPPLE_ALPHA_MAX;
    }

    public void setShouldShowProgress(boolean show) {
        shouldShowProgress = show;
    }

    public boolean isShouldShowProgress() {
        return shouldShowProgress;
    }

    public void setShouldRippling(boolean show) {
        shouldRippling = show;
        invalidate();
    }

    public boolean isShouldRippling() {
        return shouldRippling;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
    }

    public void setRippleColor(int color) {
        mRipplePaint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth() + mProgressWidth * 2, getMeasuredHeight() + mProgressWidth * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!shouldShowProgress) {
            super.onDraw(canvas);
            return;
        }

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int centerX = paddingLeft + contentWidth / 2;
        int centerY = paddingTop + contentHeight / 2;

        if (getDrawable() != null)
            mContentSize = getDrawable().getIntrinsicWidth();

        drawRipple(canvas, centerX, centerY);
        super.onDraw(canvas);
        drawProgressArc(canvas, centerX, centerY);
    }

    private void drawRipple(Canvas canvas, int centerX, int centerY) {
        if (!shouldRippling)
            return;

        float max = mContentSize * 0.8f;
        int min = mContentSize / 2;

        if (mRippleTime != 2 && mRippleRadius < min)
            mRippleRadius = min;

        if (mRippleRadius > max) {
            mRippleAlpha = RIPPLE_ALPHA_MAX;
            mRippleTime++;

            if (mRippleTime == 2)
                mRippleRadius = (int)(min / 4f * 3);
            else
                mRippleRadius = min;
        }

        if (mRippleTime >= RIPPLE_TIME) {
            shouldRippling = false;
            mRippleTime = 0;
            return;
        }

        mRippleRadius++;

        if (mRippleRadius >= min)
            mRippleAlpha -= RIPPLE_ALPHA_MAX / (max - min);

        if (mRippleAlpha < 0)
            mRippleAlpha = 0;

        mRipplePaint.setAlpha(mRippleAlpha);
        canvas.drawCircle(centerX, centerY, mRippleRadius, mRipplePaint);
        invalidate();
    }

    private void drawProgressArc(Canvas canvas, int centerX, int centerY) {

        if (mContentSize <= 0)
            return;

        int left = centerX - mContentSize / 2;
        int top = centerY - mContentSize / 2;
        int right = centerX + mContentSize / 2;
        int bottom = centerY + mContentSize / 2;
        int degree = 360 * mProgress / 100;

        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawArc(rectF, 90, degree, false, mProgressPaint);
    }
}

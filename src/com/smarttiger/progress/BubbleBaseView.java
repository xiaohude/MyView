package com.smarttiger.progress;

import com.smarttiger.myview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


public class BubbleBaseView extends View {

    protected static final int SPRITE_TYPE_SCAN = 0;
    protected static final int SPRITE_TYPE_PROGRESS = 1;

    private static final float SPRITE_ROTATE_SPEED_MIN = 2;
    private static final float BUBBLE_BREATH_SCALE_MIN = 1.0f;
    private static final float BUBBLE_BREATH_SCALE_MAX = 1.06f;
    private static final int BUBBLE_BREATH_TIME = 2;

    private static final int RES_BUBBLE = R.drawable.privacy_bubble;
    private static final int RES_SPRITE_SCAN = R.drawable.privacy_bubble_scan_sprite;
    private static final int RES_SPRITE_PROGRESS = R.drawable.privacy_bubble_progress_sprite;

    private Bitmap mBmpBubble, mBmpSprite, mBmpScanText;
    private Paint mBubblePaint, mSpritePaint, mRipplePaint, mScanTextPaint, mProgressPaint, mProgressNumPaint, mProgressUnitPaint;
    private Matrix mSpriteRotateMatrix, mBubbleBreathMatrix, mBmpScanTextMatrix;

    protected int mSpriteType = SPRITE_TYPE_SCAN;
    private int mOriginBubbleSize, mDrawBubbleSize;
    private int mOriginSpriteSize, mDrawSpriteSize;
    private float mBubbleSizeScale = 1;

    // --- SCAN
    protected String mScanText = "SCAN";
    protected float mScanTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics());
    protected float mScanSpriteRotateSpeed = 8, mScanSpriteSlowDownSpeed = 8;

    private int mScanTextAlpha;
    private int mBubbleBreathDirection = 1, mBubbleBreathTime = 0;
    private float mBubbleBreathScale;
    private float mRotateDegree = 0, mLeftDegree = 0;
    private int mRippleRadius;

    protected boolean shouldShowScanAnim, shouldFinishScanAnim;
    protected boolean shouldRotateSprite, shouldBubbleBreath, shouldRippling;

    // --- PROGRESS
    protected int mRealProgress, mCurrentProgress;
    protected float mProgressNumSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics());
    protected OnShowProgressChangedListener mOnShowProgressChangedListener;
    public interface OnShowProgressChangedListener {
        void onProgress(int progress);
    }

    public BubbleBaseView(Context context) {
        super(context);
        init(null, 0);
    }

    public BubbleBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BubbleBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleBaseView, defStyle, 0);
        mDrawBubbleSize = mOriginBubbleSize = a.getDimensionPixelSize(R.styleable.BubbleBaseView_bubbleSize, 0);
        mRippleRadius = mDrawBubbleSize / 2;
        a.recycle();
    }

    private void initMatrix() {
        mSpriteRotateMatrix = new Matrix();
        mBubbleBreathMatrix = new Matrix();
        mBmpScanTextMatrix = new Matrix();
    }

    private void initPaint() {
        mBubblePaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setFilterBitmap(true);

        mSpritePaint = new Paint();
        mSpritePaint.setAntiAlias(true);
        mSpritePaint.setFilterBitmap(true);

        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setColor(Color.WHITE);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setStrokeWidth(1);

        mScanTextPaint = new Paint();
        mScanTextPaint.setAntiAlias(true);
        mScanTextPaint.setFilterBitmap(true);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(Color.WHITE);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(4);

        mProgressNumPaint = new TextPaint();
        mProgressNumPaint.setAntiAlias(true);
        mProgressNumPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mProgressNumPaint.setTextAlign(Paint.Align.CENTER);
        mProgressNumPaint.setTextSize(mProgressNumSize);
        mProgressNumPaint.setColor(Color.WHITE);

        mProgressUnitPaint = new TextPaint();
        mProgressUnitPaint.setAntiAlias(true);
        mProgressUnitPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mProgressUnitPaint.setTextAlign(Paint.Align.LEFT);
        mProgressUnitPaint.setTextSize(mProgressNumSize / 3);
        mProgressUnitPaint.setColor(Color.WHITE);
    }

    private void initRes() {
        try {
            mBmpBubble = BitmapFactory.decodeResource(getResources(), RES_BUBBLE);
            mBmpSprite = BitmapFactory.decodeResource(getResources(), mSpriteType == SPRITE_TYPE_SCAN ? RES_SPRITE_SCAN : RES_SPRITE_PROGRESS);

            mDrawSpriteSize = mOriginSpriteSize = mBmpSprite.getWidth();
            mBmpScanText = createScanTestBitmap();
            if (mOriginBubbleSize == 0) {
                mDrawBubbleSize = mOriginBubbleSize = mBmpBubble.getWidth();
                mRippleRadius = mDrawBubbleSize / 2;
            } else {
                mBubbleSizeScale = (float)mOriginBubbleSize / mBmpBubble.getWidth();
                mDrawSpriteSize = (int)(mBubbleSizeScale * mBmpSprite.getWidth());

                mBmpBubble = Bitmap.createScaledBitmap(mBmpBubble, mDrawBubbleSize, mDrawBubbleSize, true);
                mBmpSprite = Bitmap.createScaledBitmap(mBmpSprite, mDrawSpriteSize, mDrawSpriteSize, true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void initScanState() {
        shouldShowScanAnim = false;
        shouldFinishScanAnim = false;
        shouldBubbleBreath = false;
        shouldRippling = false;
        shouldRotateSprite = false;

        mBubbleBreathScale = BUBBLE_BREATH_SCALE_MIN;
        mBubbleBreathDirection = 1;
        mBubbleBreathTime = 0;

        mRotateDegree = 0;
        mLeftDegree = 0;
        mSpritePaint.setAlpha(255);
        mScanSpriteSlowDownSpeed = mScanSpriteRotateSpeed;

        mScanTextAlpha = 255 / 2;
        mScanTextPaint.setAlpha(mScanTextAlpha);
    }

    protected void setType(int type) {
        mSpriteType = type;

        initMatrix();
        initPaint();
        initRes();
        initScanState();
    }

    private void resizeRes(int width, int height) {
        Log.d("bubble", "resizeRes");
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = width - paddingLeft - paddingRight;
        int contentHeight = height - paddingTop - paddingBottom;

        int tempSize = contentWidth > contentHeight ? contentHeight : contentWidth;
        mDrawBubbleSize = tempSize > mDrawBubbleSize ? mDrawBubbleSize : tempSize;
        mRippleRadius = mDrawBubbleSize / 2;

        if (mDrawBubbleSize == mOriginBubbleSize)
            return;

        mBubbleSizeScale = (float)mDrawBubbleSize / mOriginBubbleSize;
        mDrawSpriteSize = (int)(mBubbleSizeScale * mOriginSpriteSize);
        mBmpBubble = Bitmap.createScaledBitmap(mBmpBubble, mDrawBubbleSize, mDrawBubbleSize, true);
        mBmpSprite = Bitmap.createScaledBitmap(mBmpSprite, mDrawSpriteSize, mDrawSpriteSize, true);
    }

    private Bitmap createScanTestBitmap() {
        TextPaint paint = new TextPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mScanTextSize);
        paint.setColor(Color.WHITE);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        float width = paint.measureText(mScanText) + 0.5f;
        float height = Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent) + 0.5f;

        Bitmap bmp = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawText(mScanText, width / 2, height - Math.abs(fontMetrics.descent), paint);
        return bmp;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMeasureSize = measureSize(widthMeasureSpec);
        int heightMeasureSize = measureSize(heightMeasureSpec);
        setMeasuredDimension(widthMeasureSize, heightMeasureSize);
        resizeRes(widthMeasureSize, heightMeasureSize);
    }

    private int measureSize(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int size;
        if (specMode == MeasureSpec.EXACTLY) {
            size = specSize;
        } else {
            size = mOriginBubbleSize;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int left = paddingLeft + (contentWidth - mDrawBubbleSize) / 2;
        int top = paddingTop + (contentHeight - mDrawBubbleSize) / 2;
        int centerX = paddingLeft + contentWidth / 2;
        int centerY = paddingTop + contentHeight / 2;

        switch (mSpriteType) {
            case SPRITE_TYPE_SCAN:
                drawScanView(canvas, left, top, centerX, centerY);
                break;
            case SPRITE_TYPE_PROGRESS:
                drawProgressView(canvas, left, top, centerX, centerY);
                break;
        }
    }

    // --- SCAN

    private void drawScanView(Canvas canvas, int left, int top, int centerX, int centerY) {
        if (shouldShowScanAnim) {
            if (shouldRippling)
                drawRipple(canvas, centerX, centerY);

            if (!shouldBubbleBreath)
                drawBubble(canvas, left, top, centerX, centerY);
            else
                drawBreathingBubble(canvas, left, top, centerX, centerY);

            if (shouldRotateSprite)
                drawSprite(canvas, left, top);

            invalidate();
        } else {
            drawBubble(canvas, left, top, centerX, centerY);
        }
    }

    private void drawBubble(Canvas canvas, int left, int top, int centerX, int centerY) {
        if (mBmpBubble == null)
            return;
        canvas.drawBitmap(mBmpBubble, left, top, mBubblePaint);
        if (mSpriteType == SPRITE_TYPE_SCAN)
            drawScanText(canvas, centerX, centerY);
    }

    private void drawBreathingBubble(Canvas canvas, int left, int top, int centerX, int centerY) {
        if (mBmpBubble == null)
            return;
        mBubbleBreathScale += 0.002f * mBubbleBreathDirection;
        if (mBubbleBreathScale > BUBBLE_BREATH_SCALE_MAX || mBubbleBreathScale < BUBBLE_BREATH_SCALE_MIN) {
            mBubbleBreathDirection *= -1;
            if (mBubbleBreathDirection > 0)
                mBubbleBreathTime++;
            if (mBubbleBreathTime == BUBBLE_BREATH_TIME) {
                shouldBubbleBreath = false;
            }
        }
        mBubbleBreathMatrix.reset();
        mBubbleBreathMatrix.postTranslate(left, top);
        mBubbleBreathMatrix.postScale(mBubbleBreathScale, mBubbleBreathScale, (float)centerX, (float)centerY);
        canvas.drawBitmap(mBmpBubble, mBubbleBreathMatrix, mBubblePaint);

        if (mScanTextAlpha < 255)
            mScanTextAlpha += 4;
        if (mScanTextAlpha > 255)
            mScanTextAlpha = 255;

        mScanTextPaint.setAlpha(mScanTextAlpha);
        drawScanText(canvas, centerX, centerY);
    }

    private void drawSprite(Canvas canvas, int left, int top) {
        if (mBmpSprite == null)
            return;
        if (!shouldFinishScanAnim) {
            mRotateDegree += mScanSpriteRotateSpeed;
        } else {
            mRotateDegree += mScanSpriteSlowDownSpeed;

            float leftDegree = 180 - (mRotateDegree % 180);
            if (mLeftDegree == 0)
                mLeftDegree = leftDegree;

            if (mLeftDegree > 120) {
                if (mScanSpriteSlowDownSpeed > SPRITE_ROTATE_SPEED_MIN)
                    mScanSpriteSlowDownSpeed -= 0.25;

                mSpritePaint.setAlpha((int)((leftDegree / mLeftDegree) * 255));

                if (leftDegree < 10) {
                    shouldRotateSprite = false;
                    shouldBubbleBreath = true;
                    shouldRippling = true;
                }
            } else {
                mLeftDegree = 0;
            }
        }

        int offsetX = mDrawBubbleSize / 2;
        int offsetY = mDrawBubbleSize / 2;
        mSpriteRotateMatrix.reset();
        mSpriteRotateMatrix.postTranslate(-offsetX, -offsetY);
        mSpriteRotateMatrix.postRotate(mRotateDegree);
        mSpriteRotateMatrix.postTranslate(left + offsetX, top + offsetY);
        canvas.drawBitmap(mBmpSprite, mSpriteRotateMatrix, mSpritePaint);
    }

    private void drawRipple(Canvas canvas, int centerX, int centerY) {
        mRippleRadius += 3;

        float max = mDrawBubbleSize * 0.8f;
        int min = mDrawBubbleSize / 2;

        if (mRippleRadius > max)
            mRippleRadius = min;

        mRipplePaint.setAlpha((int)((1 - mRippleRadius / max) * 255));
        canvas.drawCircle(centerX, centerY, mRippleRadius, mRipplePaint);
    }

    private void drawScanText(Canvas canvas, int centerX, int centerY) {
        int textLeft = centerX - mBmpScanText.getWidth() / 2;
        int textHeight = centerY - mBmpScanText.getHeight() / 2;
        mBmpScanTextMatrix.reset();
        mBmpScanTextMatrix.postTranslate(textLeft, textHeight);
        mBmpScanTextMatrix.postScale(mBubbleBreathScale, mBubbleBreathScale, (float)centerX, (float)centerY);
        canvas.drawBitmap(mBmpScanText, mBmpScanTextMatrix, mScanTextPaint);
    }

    // --- PROGRESS

    private void drawProgressView(Canvas canvas, int left, int top, int centerX, int centerY) {
        drawBubble(canvas, left, top, centerX, centerY);
        drawProgressArc(canvas, left, top, centerX, centerY);
    }

    private int progressControl = 0;

    private void drawProgressArc(Canvas canvas, int left, int top, int centerX, int centerY) {
        float dp_5 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

        left = left + (int)(dp_5 * mBubbleSizeScale);
        top = top + (int)(dp_5 * mBubbleSizeScale);
        int right = centerX * 2 - left;
        int bottom = centerY * 2 - top;
        int degree = 360 * mCurrentProgress / 100;

        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawArc(rectF, -90, degree, false, mProgressPaint);

        int r = centerX - left;
        drawProgressSprite(canvas, r, degree - 90, centerX, centerY);
        drawProgressNum(canvas, centerX, centerY);

        if (mOnShowProgressChangedListener != null) {
            mOnShowProgressChangedListener.onProgress(mCurrentProgress);
        }

        if (mCurrentProgress < mRealProgress) {
            progressControl++;
            if (progressControl % 3 == 0)
                mCurrentProgress++;
            invalidate();
        }
    }

    private void drawProgressSprite(Canvas canvas, int r, int degree, int centerX, int centerY) {
        // x1 = x0 + r * cos(degree * 3.14 /180)
        // y1 = y0 + r * sin(degree * 3.14 /180)

        int x = centerX + (int)(r * Math.cos(degree * Math.PI / 180));
        int y = centerY + (int)(r * Math.sin(degree * Math.PI / 180));

        int spriteWidth = mBmpSprite.getWidth();
        int spriteHeight = mBmpSprite.getHeight();
        canvas.drawBitmap(mBmpSprite, x - spriteWidth / 2, y - spriteHeight / 2, mSpritePaint);
    }

    private void drawProgressNum(Canvas canvas, int centerX, int centerY) {
        Paint.FontMetrics fontMetrics = mProgressNumPaint.getFontMetrics();
        String progressNum = String.valueOf(mCurrentProgress);
        float width = mProgressNumPaint.measureText(progressNum) + 0.5f;
        float height = Math.abs(fontMetrics.ascent) - Math.abs(fontMetrics.descent) + 0.5f;
        canvas.drawText(progressNum, centerX, centerY + height / 2, mProgressNumPaint);
        canvas.drawText("%", centerX + width / 2 + 4, centerY + height / 2 - 2, mProgressUnitPaint);
    }
}

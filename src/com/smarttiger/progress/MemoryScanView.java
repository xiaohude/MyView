package com.smarttiger.progress;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.smarttiger.myview.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MemoryScanView extends View {

    public static final String TAG = "MemoryScanView";
    
    private Context mContext;

    private static final int MAX_DRAWING_ITEM_COUNT = 4;
    private static final int RES_BG = R.drawable.privacy_memory_scan_bounds;
    private static final int RES_AIM = R.drawable.privacy_memory_scan_bg;
    private static final int RES_SCAN = R.drawable.privacy_memory_scan_fg;

    private Bitmap mBmpBg, mBmpAim, mBmpScan;
    private Paint mResPaint;
    private Matrix mRotateScanMatrix;
    private int mBgSize, mAimSize, mScanSize;
    private int mDrawBgSize, mDrawAimSize, mDrawScanSize;
    private float mRotateDegree;
    private boolean shouldScanning = true;
    private Timer mLoadCleanItemTimer;

    private List<ScanItem> mAllCleanItems = new LinkedList<>();
    private List<CleanItem> mDrawingCleanItems = new LinkedList<>();
    private OnCleanCompleteListener mOnCleanCompleteListener;

    public interface OnCleanCompleteListener {
        void onComplete();
    }
    
    private class ScanItem {
    	String name;
    	Bitmap bitmap;
    	public ScanItem(String name) {
    		this.name = name;
    		this.bitmap = getAppIcon(name);
    	}
    }
    public Bitmap getAppIcon(String packname){    
        try {    
             ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packname, 0);     
             Drawable drawable = info.loadIcon(mContext.getPackageManager());   
             Bitmap bitmap = Bitmap.createBitmap(
                     drawable.getIntrinsicWidth(),
                     drawable.getIntrinsicHeight(),
                     drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                     : Bitmap.Config.RGB_565);
             Canvas canvas = new Canvas(bitmap);
             drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
             drawable.draw(canvas);
             return bitmap;
        } catch (NameNotFoundException e) {    
            // TODO Auto-generated catch block    
            e.printStackTrace();    
        }
		return null;    
    }
    private void getTestItems () {
    	
    	ScanItem scanItem = new ScanItem("com.tencent.mm");
    	ScanItem scanItem1 = new ScanItem("com.tencent.mobileqq");
    	ScanItem scanItem2 = new ScanItem("com.smarttiger.myview");
    	
    	mAllCleanItems.add(scanItem);
    	mAllCleanItems.add(scanItem1);
    	mAllCleanItems.add(scanItem2);
    }
    

    private class CleanItem {
        int left, top;
        int alpha, alphaChangedStep;
        boolean stopDrawing;
        Bitmap bitmap;
        Paint paint = new Paint();

        CleanItem(int left, int top, Bitmap bitmap) {
            this.left = left;
            this.top = top;
            this.bitmap = bitmap;
            alpha = 1;
            alphaChangedStep = 1;
            stopDrawing = false;

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setAlpha(alpha);
        }

        void draw(Canvas canvas) {
            if (stopDrawing)
                return;

            canvas.drawBitmap(bitmap, left, top, paint);

            if (alpha >= 255 + 100)
                alphaChangedStep = -1;

            alpha += alphaChangedStep * 4;
            paint.setAlpha(alpha > 255 ? 255 : alpha);

            if (alpha < 1) {
                stopDrawing = true;
                mDrawingCleanItems.remove(this);
                // bitmap.recycle();
            }
        }
    }

    public MemoryScanView(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MemoryScanView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MemoryScanView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLoadCleanItemTimer != null)
            mLoadCleanItemTimer.cancel();
        if (mOnCleanCompleteListener != null)
            mOnCleanCompleteListener = null;
    }

    private void init() {
        initRes();
        initPaint();
        initMatrix();
        
        getTestItems();
    }

    private void initRes() {
        try {
            mBmpBg = BitmapFactory.decodeResource(getResources(), RES_BG);
            mBmpAim = BitmapFactory.decodeResource(getResources(), RES_AIM);
            mBmpScan = BitmapFactory.decodeResource(getResources(), RES_SCAN);

            mDrawBgSize = mBgSize = mBmpBg.getWidth();
            mDrawAimSize = mAimSize = mBmpAim.getWidth();
            mDrawScanSize = mScanSize = mBmpScan.getWidth();
        } catch (Throwable e) {
            Log.e(TAG, e.toString());
        }
    }

    private void initPaint() {
        mResPaint = new Paint();
        mResPaint.setAntiAlias(true);
        mResPaint.setFilterBitmap(true);
    }

    private void initMatrix() {
        mRotateScanMatrix = new Matrix();
    }

    public void stopScanning() {
        shouldScanning = false;
        if (mLoadCleanItemTimer != null)
            mLoadCleanItemTimer.cancel();
        mLoadCleanItemTimer = null;
    }

    public void setOnCleanCompleteListener(OnCleanCompleteListener onCleanCompleteListener) {
        mOnCleanCompleteListener = onCleanCompleteListener;
    }

    public void setCleanItems(List<ScanItem> cleanItems) {
        Log.d(TAG, "setCleanItems: " + cleanItems);

        if (cleanItems == null)
            return;

        mAllCleanItems.clear();
        mAllCleanItems.addAll(cleanItems);
    }

    private boolean addDrawingCleanItem(CleanItem cleanItem) {
        Log.d(TAG, "mDrawingCleanItems size: " + mDrawingCleanItems.size());

        if (mDrawingCleanItems.size() < MAX_DRAWING_ITEM_COUNT) {
            mDrawingCleanItems.add(cleanItem);
            return true;
        }

        return false;
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
        mDrawBgSize = tempSize > mDrawBgSize ? mDrawBgSize : tempSize;

        if (mDrawBgSize == mBgSize)
            return;

        float sizeChangeScale = (float)mDrawBgSize / mBgSize;
        mDrawAimSize = (int)(sizeChangeScale * mAimSize);
        mDrawScanSize = (int)(sizeChangeScale * mScanSize);

        mBmpBg = Bitmap.createScaledBitmap(mBmpBg, mDrawBgSize, mDrawBgSize, true);
        mBmpAim = Bitmap.createScaledBitmap(mBmpAim, mDrawAimSize, mDrawAimSize, true);
        mBmpScan = Bitmap.createScaledBitmap(mBmpScan, mDrawScanSize, mDrawScanSize, true);
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
            size = mBgSize;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int left = getPaddingLeft();
        int top = getPaddingTop();

        drawAimBg(canvas, left, top);
        drawCleanItem(canvas);
        drawRotateScan(canvas, left, top);

        if (shouldScanning)
            invalidate();
    }

    private void drawAimBg(Canvas canvas, int left, int top) {
        if (mBmpBg == null)
            return;

        canvas.drawBitmap(mBmpBg, left, top, mResPaint);

        if (mBmpAim == null)
            return;

        int aimOneSideDeltaSize = (mDrawBgSize - mDrawAimSize) / 2;
        canvas.drawBitmap(mBmpAim, left + aimOneSideDeltaSize, top + aimOneSideDeltaSize, mResPaint);
    }

    private void drawRotateScan(Canvas canvas, int left, int top) {
        mRotateDegree += 4;

        left += (mDrawBgSize - mDrawScanSize) / 2;
        top += (mDrawBgSize - mDrawScanSize) / 2;
        int offsetX = mDrawScanSize / 2;
        int offsetY = mDrawScanSize / 2;
        mRotateScanMatrix.reset();
        mRotateScanMatrix.postTranslate(-offsetX, -offsetY);
        mRotateScanMatrix.postRotate(mRotateDegree);
        mRotateScanMatrix.postTranslate(left + offsetX, top + offsetY);
        canvas.drawBitmap(mBmpScan, mRotateScanMatrix, mResPaint);
    }

    private void drawCleanItem(Canvas canvas) {
        try {
            for (int i = 0; i < mDrawingCleanItems.size(); i++) {
                CleanItem item = mDrawingCleanItems.get(i);
                item.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int temp;

    public void startCleanItemsAnim() {

        if (mLoadCleanItemTimer != null)
            mLoadCleanItemTimer.cancel();

        mLoadCleanItemTimer = new Timer();
        mLoadCleanItemTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "mAllCleanItems size: " + mAllCleanItems.size());
                    if (mAllCleanItems.size() > 0) {
                        if (mDrawingCleanItems.size() < MAX_DRAWING_ITEM_COUNT) {

                            ScanItem item = mAllCleanItems.get(0);

                            Bitmap bitmap = item.bitmap;
                            if (bitmap == null)
                                return;

                            Point bmpDrawingPos = createRandomPoint(temp % MAX_DRAWING_ITEM_COUNT, bitmap.getWidth());
                            CleanItem cleanItem = new CleanItem(bmpDrawingPos.x, bmpDrawingPos.y, bitmap);
                            if (addDrawingCleanItem(cleanItem)) {
                                mAllCleanItems.remove(0);
                                temp++;
                            }
                        }
                    } else {
                        if (mLoadCleanItemTimer != null)
                            mLoadCleanItemTimer.cancel();
                        mLoadCleanItemTimer = null;

//                        TaskHelper.exec(new TaskHelper.UITask() {
//                            @Override
//                            public void callback(Exception e) {
                        		//如果里面有操控界面的，记得需要在UI线程中
                                if (mOnCleanCompleteListener != null)
                                    mOnCleanCompleteListener.onComplete();
//                            }
//                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }, 500, 500);
    }

    private Point createRandomPoint(int index, int bitmapSize) {
        int minLeft = 0, minTop = 0;
        int left, top;
        left = getPaddingLeft();
        switch (index) {
            case 0:
            case 2:
                minLeft = left;
                break;
            case 1:
            case 3:
                minLeft = left + mDrawBgSize / 2;
                break;
        }

        top = getPaddingTop();
        switch (index) {
            case 0:
            case 1:
                minTop = top;
                break;
            case 2:
            case 3:
                minTop = top + mDrawBgSize / 2;
                break;
        }

        left = minLeft + (int)(Math.random() * (mDrawBgSize / 2 - bitmapSize));
        top = minTop + (int)(Math.random() * (mDrawBgSize / 2 - bitmapSize));

        if (isInCircle(left - getPaddingLeft(), top - getPaddingTop(), mDrawBgSize / 2)
                && isInCircle(left - getPaddingLeft() + bitmapSize, top - getPaddingTop(), mDrawBgSize / 2)
                && isInCircle(left - getPaddingLeft(), top - getPaddingTop() + bitmapSize, mDrawBgSize / 2)
                && isInCircle(left - getPaddingLeft() + bitmapSize, top - getPaddingTop() + bitmapSize, mDrawBgSize / 2))
            return new Point(left, top);
        else
            return createRandomPoint(index, bitmapSize);
    }

    private boolean isInCircle(int x, int y, int r) {
        // int centerX = r;
        // int centerY = r;

        x = Math.abs(x - r);
        y = Math.abs(y - r);

        return x * x + y * y < r * r;
    }
}

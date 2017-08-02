package com.smarttiger.progress;

import com.smarttiger.myview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

public class BubbleProgressView extends BubbleBaseView {

    public BubbleProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public BubbleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BubbleProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BubbleProgressView, defStyle, 0);
        mProgressNumSize = a.getDimensionPixelSize(R.styleable.BubbleProgressView_numSize, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics()));

        a.recycle();
        setType(SPRITE_TYPE_PROGRESS);
    }

    public void setProgress(int progress) {
        if (progress > 100)
            progress = 100;

        mRealProgress = progress;
        invalidate();
    }

    public void setOnShowProgressChangedListener(OnShowProgressChangedListener listener) {
        mOnShowProgressChangedListener = listener;
    }
}

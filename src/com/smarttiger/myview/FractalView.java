package com.smarttiger.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * 分形图View
 * */
public class FractalView extends View {
	

    private Paint mPaint;
    private int mCenterX, mCenterY;

	public FractalView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public FractalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取View中心点坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
    }
	
	int depth = 6;
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

        canvas.translate(mCenterX, mCenterY); // 将坐标系移动到画布中央
        canvas.scale(1,-1);                 // 翻转Y轴
		
//		draw1(canvas, 100, 500, 700, 500, depth);
////		400,1020
//		draw1(canvas, 700, 500, 400, 1020, depth);
//		draw1(canvas, 400, 1020, 100, 500, depth);
        
//		draw1(canvas, -300, 0, 300, 0, depth);
//		draw1(canvas, 300, 0, 0, -520, depth);
//		draw1(canvas, 0, -520, -300, 0, depth);
		draw1(canvas, 300, 0, -300, 0, depth);
		draw1(canvas, 0, -520, 300, 0, depth);
		draw1(canvas, -300, 0, 0, -520, depth);
		
		

//		draw2(canvas, 200, 300, 200, depth);
//		draw2(canvas, -100, 100, 200, depth);
	}

	
	public void draw1(Canvas canvas, int x1, int y1, int x2, int y2,int depth) {//科赫曲线   keleyi.com
		canvas.drawLine(x1, y1, x2, y2, mPaint);  
        if (depth<=1)  
            return;  
        else {//得到三等分点  
            double x11 = (x1 * 2  + x2)  / 3;  
            double y11 = (y1 * 2  + y2) / 3;  
  
            double x22 = (x1 + x2 * 2) / 3;  
            double y22 = (y1 + y2 * 2) / 3;  
  
            double x33 = (x11 + x22) / 2 - (y11 - y22) * Math.sqrt(3) / 2;  
            double y33 = (y11 + y22) / 2 - (x22 - x11) * Math.sqrt(3) / 2;  
  
            mPaint.setColor(Color.BLACK);  
            canvas.drawLine((int) x1, (int) y1, (int) x2, (int) y2, mPaint);  
            mPaint.setColor(Color.GREEN);  
            draw1(canvas, (int) x1, (int) y1, (int) x11, (int) y11,depth-1);  
            draw1(canvas, (int) x11, (int) y11, (int) x33, (int) y33,depth-1);  
            draw1(canvas, (int) x22, (int) y22, (int) x2, (int) y2,depth-1);  
            draw1(canvas, (int) x33, (int) y33, (int) x22, (int) y22,depth-1);  
        }  
    }


	public void draw2(Canvas canvas, int x1, int y1, int m,int depth) {//正方形 keleyi.com  
		canvas.drawRect(x1, y1, x1+m, y1+m, mPaint);  
        m = m / 3;  
        if (depth<=1)  
            return;  
        else{  
        double x11 = x1 - 2 * m;  
        double y11 = y1 - 2 * m;  
  
        double x22 = x1 + m;  
        double y22 = y1 - 2 * m;  
  
        double x33 = x1 + 4 * m;  
        double y33 = y1 - 2 * m;  
  
        double x44 = x1 - 2 * m;  
        double y44 = y1 + m;  
  
        double x55 = x1 + 4 * m;  
        double y55 = y1 + m;  
  
        double x66 = x1 - 2 * m;  
        double y66 = y1 + 4 * m;  
  
        double x77 = x1 + m;  
        double y77 = y1 + 4 * m;  
  
        double x88 = x1 + 4 * m;  
        double y88 = y1 + 4 * m;  
  
        draw2(canvas, (int) x11, (int) y11, (int) m,depth-1);  
  
        draw2(canvas, (int) x22, (int) y22, (int) m,depth-1);  
  
        draw2(canvas, (int) x33, (int) y33, (int) m,depth-1);  
  
        draw2(canvas, (int) x44, (int) y44, (int) m,depth-1);  
  
        draw2(canvas, (int) x55, (int) y55, (int) m,depth-1);  
  
        draw2(canvas, (int) x66, (int) y66, (int) m,depth-1);  
  
        draw2(canvas, (int) x77, (int) y77, (int) m,depth-1);  
  
        draw2(canvas, (int) x88, (int) y88, (int) m,depth-1);  
        }  
  
    }
}

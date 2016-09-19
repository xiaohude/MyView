package com.smarttiger.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class RulerView extends View {

	private static float CALIBRATION = 1;//用来校准尺子的基数
	private float CALIBRATION_saved;
	private float xmm_saved;
	
	private float xcm;
	private float xmm;
	private float ruler_length;
	private float ruler_width;
	private PointF mid_point = new PointF(0, 0);
	private Rect rect = new Rect();
	private float angle_rotate = 0;
	private float angle_initial;
	private float angle_saved;
	private float distance_initial;
	private float distance_saved;
	private Boolean MultiToSingle = false;
	private Boolean SingleToMulti = true;
	private PointF mid_point_between_fingers = new PointF();
	private PointF mid_point_between_fingers_down = new PointF();
	private PointF finger_first_down = new PointF();
	private PointF finger_second_down = new PointF();
	private PointF mid_point_saved = new PointF();
	private String MODE = "NONE";

	public RulerView(Context context) {
		super(context, null);
	}
	
	public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		find_pixal(dm);
		ruler_length = 6 * xcm; // 设置一开始为6厘米的尺子
		ruler_width = xcm;
		mid_point.set((float) (ruler_length * 0.5), 0);
		rect = new Rect(0, 0, (int) (ruler_length), (int) ruler_width);
	}
	
	public RulerView(Context context, DisplayMetrics dm) {
		super(context);
		find_pixal(dm);
		ruler_length = 6 * xcm; // 设置一开始为6厘米的尺子
		ruler_width = xcm;
		mid_point.set((float) (ruler_length * 0.5), 0);
		rect = new Rect(0, 0, (int) (ruler_length), (int) ruler_width);
	}
	
	public RulerView(Context context, DisplayMetrics dm,float length) {
		super(context);
		find_pixal(dm);
		ruler_length = length * xcm; // 设置一开始为length厘米的尺子
		ruler_width = xcm;
		mid_point.set((float) (ruler_length * 0.5), 0);
		rect = new Rect(0, 0, (int) (ruler_length), (int) ruler_width);
	}
	public RulerView(Context context, DisplayMetrics dm,PointF point) {
		super(context);
		find_pixal(dm);
		ruler_length = 6 * xcm; // 设置一开始为length厘米的尺子
		ruler_width = xcm;
		mid_point.set(point);
		rect = new Rect(0, 0, (int) (ruler_length), (int) ruler_width);
		renewRect();
	}
	public RulerView(Context context, DisplayMetrics dm,Float length,PointF point) {
		super(context);
		find_pixal(dm);
		ruler_length = length * xcm; // 设置一开始为length厘米的尺子
		ruler_width = xcm;
		mid_point.set(point);
		rect = new Rect(0, 0, (int) (ruler_length), (int) ruler_width);
		renewRect();
	}
	
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setTextSize(30);
		canvas.save();
		canvas.rotate(angle_rotate, mid_point.x, mid_point.y);
//		canvas.drawRect(mid_point.x - ruler_length / 2, mid_point.y,
//				mid_point.x + ruler_length / 2, mid_point.y + ruler_width,
//				paint);
		canvas.drawRect(rect, paint);//画尺子边界
		for (int i = 0; i < ruler_length / xmm; i++) {
			float Left = mid_point.x - ruler_length / 2;
			if (i % 10 == 0 && i != 0) {
				canvas.drawLine(Left + i * xmm, mid_point.y, Left + i * xmm,
						mid_point.y + 50, paint);
				canvas.drawText(Integer.toString(i / 10), Left + i * xmm,
						mid_point.y + 55, paint);
			} else if (i == 0) {
				canvas.drawLine(Left + i * xmm, mid_point.y, Left + i * xmm,
						mid_point.y + 50, paint);
				canvas.drawText(Integer.toString(i / 5) + "cm", Left + i * xmm,
						mid_point.y + 55, paint);
			} else {
				canvas.drawLine(Left + i * xmm, mid_point.y, Left + i * xmm,
						mid_point.y + 30, paint);
			}
		}
		
		canvas.drawRect(mid_point.x + ruler_length/2, mid_point.y, mid_point.x + ruler_length/2 + ruler_width, mid_point.y + ruler_width, paint);
		canvas.drawText("校准",mid_point.x + ruler_length/2 + ruler_width/2, mid_point.y + ruler_width/2,  paint);
		
		canvas.restore();
	}

	
	
	public boolean onTouchEvent(MotionEvent event) {
		PointF touchPoint1;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
		case MotionEvent.ACTION_DOWN:
			touchPoint1 = supposedPoint(new PointF(event.getX(), event.getY()));
			if (rect.contains((int) touchPoint1.x, (int) touchPoint1.y)) {
				MODE = "DRAG";
				finger_first_down.set(event.getX(), event.getY());
				mid_point_saved.set(mid_point);
			} else {
				return false;
			}
			break;
		case MotionEvent.ACTION_POINTER_1_DOWN:
			PointF touchPoint2 = supposedPoint(new PointF(event.getX(1),
					event.getY(1)));
			if (rect.contains((int) touchPoint2.x, (int) touchPoint2.y)) {
				MODE = "ZOOM";
				finger_first_down.set(event.getX(0), event.getY(0));
				finger_second_down.set(event.getX(1), event.getY(1));
				angle_initial = rotation(event);
				angle_saved = angle_rotate;
				SingleToMulti = true;
				distance_initial = distance(event);
				distance_saved = ruler_length;
				CALIBRATION_saved = CALIBRATION;
				mid_point_between_fingers_down.set(
						(event.getX(0) + event.getX(1)) / 2,
						(event.getY(0) + event.getY(1)) / 2);
			} else {
				return false;
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			if (MODE == "DRAG") {
				if (MultiToSingle) {
					finger_first_down.set(event.getX(), event.getY());
					mid_point_saved.set(mid_point);
					MultiToSingle = false;
				}
				mid_point.set(mid_point_saved.x + event.getX()
						- finger_first_down.x, mid_point_saved.y + event.getY()
						- finger_first_down.y);
				renewRect();
			} else if (MODE == "ZOOM") {
				if (SingleToMulti) {
					mid_point_saved.set(mid_point);
					SingleToMulti = false;
				}
				mid_point_between_fingers.set(
						(event.getX(0) + event.getX(1)) / 2,
						(event.getY(0) + event.getY(1)) / 2);
				mid_point.set(mid_point_saved.x + mid_point_between_fingers.x
						- mid_point_between_fingers_down.x, mid_point_saved.y
						+ mid_point_between_fingers.y
						- mid_point_between_fingers_down.y);
				angle_rotate = angle_saved + rotation(event) - angle_initial;
				ruler_length = distance_saved * distance(event) / distance_initial;
				CALIBRATION = CALIBRATION_saved * distance(event) / distance_initial;
				renewRect();
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
				MODE = "NONE";
			break;
		case MotionEvent.ACTION_POINTER_1_UP:
			MultiToSingle = true;
//			MODE = "DRAG";
//			System.out.println(MODE);
//			break;
			MODE = "NONE";
			break;
		}
		return true;
	}

	/** 校准尺子 */
	protected void find_pixal(DisplayMetrics dm) {
		xcm = (float) (dm.xdpi / 2.54) * CALIBRATION; // 单位都是pixal
		xmm = xcm / 10;
		xmm_saved = xmm;
	}

	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	/** 获取两个手指触点之间的距离 */
	private float distance(MotionEvent event) {
		double x = (event.getX(0) - event.getX(1));
		double y = (event.getY(0) - event.getY(1));
		return (float) Math.sqrt(x * x + y * y);
	}

	protected float in360(float angel) {
		if (angel >= 360) {
			do {
				angel -= 360;
			} while (angel < 360);
		} else if (angel < 0) {
			do {
				angel += 360;
			} while (angel > 0);
		}
		return angel;
	}

	protected float distanceToMidPoint(PointF touchPoint) {
		double x = (touchPoint.x - mid_point.x);
		double y = (touchPoint.y - mid_point.y);
		return (float) Math.sqrt(x * x + y * y);
	}

	protected PointF supposedPoint(PointF touchPoint) {
		Float k = new Float(Math.toRadians(angle_rotate));
		PointF point_map = new PointF();
		point_map.x = new Float((touchPoint.x - mid_point.x) * Math.cos(k)
				+ (touchPoint.y - mid_point.y) * Math.sin(k) + mid_point.x);
		point_map.y = new Float(-(touchPoint.x - mid_point.x) * Math.sin(k)
				+ (touchPoint.y - mid_point.y) * Math.cos(k) + mid_point.y);
		return point_map;
	}
	
	protected void renewRect() {
		xmm = xmm_saved *  CALIBRATION;
		
		rect.left = (int) (mid_point.x - ruler_length * 0.5 );
		rect.right = (int) (mid_point.x + ruler_length * 0.5 );
		rect.top = (int) mid_point.y;
		rect.bottom = (int) (mid_point.y + ruler_width);
	}
}

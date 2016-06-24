
package com.smarttiger.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class AnimationUtils {
	/**
	 * 获取旋转动效
	 * @param duration 动画执行时间
	 * @param fromAngle 开始角度
	 * @param toAngle 结束角度
	 * @param isFillAfter 是否停止在随后一帧不恢复到原来位置
	 * @param repeatCount 重复次数 -1为无限重复
	 * @return
	 */
    public static RotateAnimation initRotateAnimation(long duration,
            int fromAngle, int toAngle,
            boolean isFillAfter, int repeatCount) {
        RotateAnimation mLoadingRotateAnimation = new RotateAnimation(fromAngle, toAngle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        LinearInterpolator lirInterpolator = new LinearInterpolator();
        mLoadingRotateAnimation.setInterpolator(lirInterpolator);
        mLoadingRotateAnimation.setDuration(duration);
        mLoadingRotateAnimation.setFillAfter(isFillAfter);
        mLoadingRotateAnimation.setRepeatCount(repeatCount);
        mLoadingRotateAnimation.setRepeatMode(Animation.RESTART);
        return mLoadingRotateAnimation;
    }

    /**
     * 获取旋转动效
     * @param isClockWise 是否为顺时针
     * @param duration 动画执行时间
     * @param isFillAfter 是否停止在随后一帧不恢复到原来位置
     * @param repeatCount 重复次数 -1为无限重复
     * @return
     */
    public static RotateAnimation initRotateAnimation(boolean isClockWise, long duration,
            boolean isFillAfter, int repeatCount) {
        int endAngle;
        if (isClockWise) {
            endAngle = 360;
        } else {
            endAngle = -360;
        }
        RotateAnimation mLoadingRotateAnimation = new RotateAnimation(0, endAngle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        LinearInterpolator lirInterpolator = new LinearInterpolator();
        mLoadingRotateAnimation.setInterpolator(lirInterpolator);
        mLoadingRotateAnimation.setDuration(duration);
        mLoadingRotateAnimation.setFillAfter(isFillAfter);
        mLoadingRotateAnimation.setRepeatCount(repeatCount);
        mLoadingRotateAnimation.setRepeatMode(Animation.RESTART);
        return mLoadingRotateAnimation;
    }

    public static AnimationDrawable initAnimationDrawable(Context context, int[] drawableIds,
            int durationTime, boolean isOneShot) {
        AnimationDrawable mAnimationDrawable = new AnimationDrawable();
        for (int i = 0; i < drawableIds.length; i++) {
            int id = drawableIds[i];
            mAnimationDrawable.addFrame(context.getResources().getDrawable(id), durationTime);
        }
        mAnimationDrawable.setOneShot(isOneShot);
        return mAnimationDrawable;
    }

    /**
	 * 获取旋转动效
	 * @param fromAngle 开始角度
	 * @param toAngle 结束角度
	 * @param duration 动画执行时间
	 * @return
	 */
    public static Animation initAlphaAnimtion(Context context, float fromAlpha, float toAlpha,
            long duration) {
        Animation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(duration);
        return alphaAnimation;
    }

}

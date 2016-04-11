/**
 * RoomControls.java [V 1.0.0]
 * classes:cn.zldemo.touchitem.view.RoomControls
 * Zlang Create at 2015-12-20.下午12:41:12
 */
package org.dync.tv.teameeting.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * cn.zldemo.touchitem.view.RoomControls
 *
 * @author ZLang <br/>
 *         create at 2015-12-20 下午12:41:12
 */
public class RoomControls extends LinearLayout {

    public boolean mAvailable;
    public String TAG = this.getClass().getSimpleName();

    public RoomControls(Context paramContext) {
        super(paramContext);
        init(paramContext, null);
    }

    public RoomControls(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext, paramAttributeSet);
    }

    public RoomControls(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }

    private void init(Context paramContext, AttributeSet paramAttributeSet) {
        this.mAvailable = true;
    }

    public void hide() {
        this.mAvailable = false;
        int measuredWidth = this.getMeasuredWidth();
        Log.e(TAG, "hide: " + measuredWidth);
        animateBottomMarginRight(this, -this.getMeasuredWidth(), 300);
        makeInvisible();
    }

    public void makeInvisible() {
        fadeOut(this, 300L);
    }

    public void show() {
        this.mAvailable = true;
        animateBottomMarginRight(this, 0, 300L);
        makeVisible();
    }

    public void makeVisible() {
        fadeIn(this, 300L, 0L);
    }

    /**
     * 淡出
     *
     * @param paramView
     * @param paramLong1
     * @param paramLong2
     */
    public void fadeIn(View paramView, long paramLong1, long paramLong2) {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = paramView.getAlpha();
        arrayOfFloat[1] = 1.0F;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView, "alpha",
                arrayOfFloat);
        localObjectAnimator.setDuration(paramLong1);
        if (paramLong2 > 0L) {
            localObjectAnimator.setStartDelay(paramLong2);
        }
        localObjectAnimator.setInterpolator(new DecelerateInterpolator());
        localObjectAnimator.start();
        paramView.setVisibility(View.VISIBLE);
    }

    /**
     * 移动
     *
     * @param paramView
     * @param paramInt
     * @param paramLong
     */
    public static void animateBottomMarginRight(final View paramView, int paramInt, long paramLong) {
        final LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) paramView
                .getLayoutParams();
        int[] arrayOfInt = new int[2];
        arrayOfInt[0] = localLayoutParams.rightMargin;
        arrayOfInt[1] = paramInt;
        ValueAnimator localValueAnimator = ValueAnimator.ofInt(arrayOfInt);
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                localLayoutParams.rightMargin = ((Integer) paramAnonymousValueAnimator
                        .getAnimatedValue()).intValue();
                paramView.requestLayout();
            }
        });
        localValueAnimator.setInterpolator(new AccelerateInterpolator());
        localValueAnimator.setDuration(paramLong);
        localValueAnimator.start();
    }

    public static void fadeOut(View paramView, long paramLong) {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = paramView.getAlpha();
        arrayOfFloat[1] = 0.0F;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView, "alpha",
                arrayOfFloat);
        localObjectAnimator.setDuration(paramLong);
        localObjectAnimator.setInterpolator(new DecelerateInterpolator());
        localObjectAnimator.start();
    }

}

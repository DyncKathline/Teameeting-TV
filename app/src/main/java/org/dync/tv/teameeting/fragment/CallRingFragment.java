package org.dync.tv.teameeting.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;

import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.structs.EventType;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallRingFragment extends BaseFragment implements View.OnFocusChangeListener, View.OnClickListener {
    @Bind(R.id.llayout_call_ring)
    public LinearLayout llayoutCallRing;//layout_call_ring.xml
    @Bind(R.id.LoadingHalo1)
    public ImageView LoadingHalo1;
    @Bind(R.id.LoadingHalo2)
    public ImageView LoadingHalo2;
    @Bind(R.id.LoadingHalo3)
    public ImageView LoadingHalo3;
    @Bind(R.id.LoadingHalo4)
    public ImageView LoadingHalo4;
    @Bind(R.id.btn_accept)
    public Button callAccept;
    @Bind(R.id.btn_hungUp)
    public Button callHungUp;

    private boolean isStartAnim = false;//是否关闭动画
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    animset(LoadingHalo2);
                    handler.sendEmptyMessageDelayed(2, 500);
                    break;
                case 2:
                    animset(LoadingHalo3);
                    handler.sendEmptyMessageDelayed(3, 500);
                    break;
                case 3:
                    animset(LoadingHalo4);
                    handler.sendEmptyMessageDelayed(4, 500);
                    break;
                case 4:
                    animset(LoadingHalo1);
                    handler.sendEmptyMessageDelayed(1, 500);
                    break;
            }
        }
    };

    public void animset(View view) {
        if (isStartAnim) {
            Logger.e("TAG", "动画开启中。。。。。");
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 1.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 1.0f);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(2000);
            animatorSet.playTogether(animator1, animator2, animator3);
            animatorSet.start();
        }
    }

    public CallRingFragment() {
        // Required empty public constructor
    }

    @Override
    protected int provideViewLayoutId() {
        return R.layout.fragment_call_ring;
    }

    @Override
    protected void init() {
        startAnim();
        initListener();
    }


    private void initListener() {
        callAccept.setOnClickListener(this);
        callHungUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:

                break;
            case R.id.btn_hungUp:
                stopAnim();
                if (mCallRingListener != null) {
                    mCallRingListener.onClick();
                }
                break;
        }
    }

    public void startAnim() {
        isStartAnim = true;
        animset(LoadingHalo1);
        handler.sendEmptyMessageDelayed(1, 0);
    }

    public void stopAnim() {
        isStartAnim = false;
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.btn_accept:

                break;
            case R.id.btn_hungUp:
                if (mCallRingListener != null) {
                    stopAnim();
                    mCallRingListener.onClick();
                }
                break;
        }
    }

    public interface CallRingListener {
        void onClick();
    }

    private CallRingListener mCallRingListener;

    public void setOnCallRingListener(CallRingListener listener) {
        Logger.e("Listener= " + listener);
        mCallRingListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAnim();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAnim();
    }

    /**
     * EeventBus方法
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_CALL_START:
                startAnim();
                break;
            case MSG_CALL_STOP:
                stopAnim();
                break;
        }
    }

}

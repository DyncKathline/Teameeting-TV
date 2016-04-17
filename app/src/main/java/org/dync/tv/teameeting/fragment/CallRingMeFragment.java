package org.dync.tv.teameeting.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.structs.EventType;
import org.dync.tv.teameeting.utils.RippleBackground;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallRingMeFragment extends BaseFragment implements View.OnFocusChangeListener, View.OnClickListener {
    @Bind(R.id.llayout_call_ring_me)
    public LinearLayout llayoutCallRingMe;//layout_call_ring.xml
    @Bind(R.id.content)
    RippleBackground rippleBackground;
    @Bind(R.id.tv_meeting_call)
    public TextView tvMeetingCall;
    @Bind(R.id.btn_accept)
    public Button btnAccept;
    @Bind(R.id.btn_hungUp)
    public Button btnCallHungUp;

    private MediaPlayer mediaPlayer;

    public CallRingMeFragment() {
        // Required empty public constructor
    }

    @Override
    protected int provideViewLayoutId() {
        return R.layout.fragment_call_ring_me;
    }

    @Override
    protected void init() {
        initListener();
    }


    private void initListener() {
        btnAccept.setOnClickListener(this);
        btnCallHungUp.setOnClickListener(this);
    }

    public void startAnim() {
        rippleBackground.startRippleAnimation();
        callRingStart();
    }

    public void stopAnim() {
        rippleBackground.stopRippleAnimation();
        callRingStop();
    }

    @Override
    public void requestFocus() {
        btnAccept.requestFocus();
//        setIsFocus(true);
        goneLayout(false);
    }

    @Override
    public void goneLayout(boolean gone) {
        if (gone) {
            llayoutCallRingMe.setVisibility(View.GONE);
            stopAnim();
        } else {
            llayoutCallRingMe.setVisibility(View.VISIBLE);
            startAnim();
        }
    }

    public void setPhoneText(String phoneText) {
        tvMeetingCall.setText(phoneText);
    }

    @Override
    public void onClick(View v) {
        Message msg = Message.obtain();
        switch (v.getId()) {
            case R.id.btn_accept:
                if (mCallRingMeListener != null) {
                    msg.what = EventType.MSG_CALL_ME_STOP.ordinal();//分别发送到CallRingFragment、MeetingFragment
                    EventBus.getDefault().post(msg);
                    String phone = tvMeetingCall.getText().toString();
                    mCallRingMeListener.onClickAccept(phone);
                }
                break;
            case R.id.btn_hungUp:
                if (mCallRingMeListener != null) {
                    msg.what = EventType.MSG_CALL_ME_STOP.ordinal();//分别发送到CallRingFragment、MeetingFragment
                    EventBus.getDefault().post(msg);
                    mCallRingMeListener.onClickHungUp();
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.btn_accept:
                Log.e("TAG", "接听");
                break;
            case R.id.btn_hungUp:
                Log.e("TAG", "挂断");
                break;
        }
    }

    public interface CallRingMeListener {
        void onClickHungUp();

        void onClickAccept(String phone);
    }

    private CallRingMeListener mCallRingMeListener;

    public void setOnCallRingListener(CallRingMeListener listener) {
        Logger.e("Listener= " + listener);
        mCallRingMeListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    private void callRingStart() {

        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        mediaPlayer = MediaPlayer.create(mContext, R.raw.incomingcall);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void callRingStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * EeventBus方法
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_CALL_ME_START:
                Log.e(TAG, "onEventMainThread: 暂停");
                startAnim();
                callRingStart();
                break;
            case MSG_CALL_ME_STOP:
                stopAnim();
                callRingStop();
                break;
        }
    }

}

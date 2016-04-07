package org.dync.tv.teameeting.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;
import org.dync.tv.teameeting.fragment.CallRingFragment;
import org.dync.tv.teameeting.fragment.MeetingFragment;
import org.dync.tv.teameeting.structs.EventType;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseMeetingActivity {
    private FragmentManager fm;
    private Fragment mContent;//显示当前的Fragment
    private MeetingFragment meetingFragment;
    private CallRingFragment callRingFragment;
    private String[] tags = new String[2];

    @Override
    protected void onRequesageMsg(ReqSndMsgEntity reqSndMsg) {

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        initListener();
    }

    private void initListener() {
        meetingFragment.setOnMeetingListener(new MeetingFragment.MeetingListener() {
            @Override
            public void onClick(String phone) {
                switchContent(callRingFragment, 1);
            }
        });

        callRingFragment.setOnCallRingListener(new CallRingFragment.CallRingListener() {
            @Override
            public void onClick() {
                switchContent(meetingFragment, 0);
            }
        });

    }

    /**
     * fragment 切换，不会重新加载
     *
     * @param to       显示的Fragment
     * @param position 给要添加的Fragment设置tag
     */
    public void switchContent(Fragment to, int position) {
        if (mContent != to) {
            FragmentTransaction ft = fm.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                ft.hide(mContent).add(R.id.flayout_content, to, tags[position]).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mContent = to;
        }
    }

    @Override
    protected void stateCheck(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            meetingFragment = new MeetingFragment();
            callRingFragment = new CallRingFragment();
            mContent = meetingFragment;
            tags[0] = "0";
            ft.add(R.id.flayout_content, mContent, tags[0]);
            ft.commit();
        } else {
            meetingFragment = (MeetingFragment) fm.findFragmentByTag(tags[0]);
            callRingFragment = (CallRingFragment) fm.findFragmentByTag(tags[1]);
            fm.beginTransaction().show(meetingFragment).hide(callRingFragment).commit();
        }
    }

    /**
     * EeventBus方法
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {

        }
    }

}

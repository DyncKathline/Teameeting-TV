package org.dync.tv.teameeting.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;
import org.dync.tv.teameeting.fragment.CallRingFragment;
import org.dync.tv.teameeting.fragment.CallRingMeFragment;
import org.dync.tv.teameeting.fragment.MeetingFragment;
import org.dync.tv.teameeting.structs.EventType;
import org.dync.tv.teameeting.structs.MeetType;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseMeetingActivity {
    private FragmentManager fm;
    private Fragment mContent;//显示当前的Fragment
    private MeetingFragment mMeetingFragment;
    private CallRingFragment mCallRingFragment;
    private CallRingMeFragment mCallRingMeFragment;
    private String[] tags = new String[3];
    public static int TAG_FRAG_MEETING = 0;
    public static int TAG_FRAG_CALL_ME = 1;
    public static int TAG_FRAG_CALL = 2;

    private String collAnyRtcId;
    private MeetingListEntity meetingListEntity;
    private MeetingListEntity joinMeetistEntity;
    private int meetType = MeetType.MEET_NO_EXIST;  //默认不在会议

    @Bind(R.id.llayout_control)
    LinearLayout lLayoutControl;
    @Bind(R.id.btn_full_screen)
    Button bntFullScreen;
    @Bind(R.id.btn_video_soundon)
    Button btnVideoSoundon;
    @Bind(R.id.btn_hangup)
    Button btnHangup;

    /**
     * 有人进会回调该方法;
     *
     * @param reqSndMsg
     */
    @Override
    protected void onRequesageMsg(ReqSndMsgEntity reqSndMsg) {
        if (mDebug)
            Log.e(TAG, "onRequesageMsg: " + reqSndMsg.toString());
        if (MeetType.MEET_NO_EXIST == meetType) {
            //没有入会
            meetingListEntity = mTVAPP.getMeetingIdtoEntity(reqSndMsg.getRoom());
            switchContent(mMeetingFragment, mCallRingMeFragment, TAG_FRAG_CALL_ME); //打开接通或者取消的按钮
        } else if (MeetType.MEET_EXIST == meetType) {
            if (joinMeetistEntity.getMeetingid().equals(meetingListEntity.getMeetingid())) {

            } else {
                //有人进入其他会议,;
                meetingListEntity = mTVAPP.getMeetingIdtoEntity(reqSndMsg.getRoom());
                switchContent(mMeetingFragment, mCallRingMeFragment, TAG_FRAG_CALL_ME); //打开接通或者取消的按钮
            }
        }
        //switchContent(mMeetingFragment,mCallRingFragment, 1);
        sendPostCall(true);
    }


    private void sendPostCall(boolean isRecenived) {
        Message msg = Message.obtain();
        if (isRecenived) {
            msg.what = EventType.MSG_CALL_ME_START.ordinal();//发送到CallRingMeFragment
        } else {
            msg.what = EventType.MSG_CALL_START.ordinal();
        }

        EventBus.getDefault().post(msg);
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
        mMeetingFragment.setOnMeetingListener(new MeetingFragment.MeetingListener() {
            @Override
            public void onClickCall(String phone) {
                Log.e(TAG, "onClickCall: ----呼叫");
                // 拨号上网.
                enterMeeting(phone);
                switchContent(mMeetingFragment, mCallRingFragment, TAG_FRAG_CALL);
                sendPostCall(false);
            }
        });

        /**
         * 呼叫别人
         */
        mCallRingFragment.setOnCallRingListener(new CallRingFragment.CallRingListener() {
            @Override
            public void onClickHungUp() {
                if (meetType == MeetType.MEET_EXIST) {
                    hideAllContent(); //影藏全部
                } else {
                    switchContent(mCallRingFragment, mMeetingFragment, TAG_FRAG_MEETING);
                }

            }

        });

        /**
         * 他人呼叫自己
         */
        mCallRingMeFragment.setOnCallRingListener(new CallRingMeFragment.CallRingMeListener() {
            @Override
            public void onClickHungUp() {
                if (meetType == MeetType.MEET_EXIST) {
                    hideAllContent(); //影藏全部
                } else {
                    switchContent(mCallRingMeFragment, mMeetingFragment, TAG_FRAG_MEETING);
                }

            }

            @Override
            public void onClickAccept() {

                // switchContent(mMeetingFragment, 0);
                hideAllContent(); //影藏全部
                joinAnyrtcMeet(meetingListEntity);
            }

        });

    }

    /**
     * 加入会议
     *
     * @param meetingListEntity
     */
    private void joinAnyrtcMeet(MeetingListEntity meetingListEntity) {
        if (meetingListEntity != null) {
            boolean join;
            if (meetType == MeetType.MEET_NO_EXIST) {
                //第一次入会
                join = mAnyrtcMeet.Join(meetingListEntity.getAnyrtcid());
            } else {
                //切换会议;
                join = mAnyrtcMeet.SwitchRoom(meetingListEntity.getAnyrtcid());
            }

            if (join == true) {
                meetType = MeetType.MEET_EXIST;
                joinMeetistEntity = meetingListEntity;
            } else {
                meetType = MeetType.MEET_NO_EXIST;
            }
        }
    }

    /**
     * 挂断的方法
     *
     * @param meetingListEntity
     */
    private void destoryJoinMeet(MeetingListEntity meetingListEntity) {
        if (meetType == MeetType.MEET_EXIST) {
            mAnyrtcMeet.SwitchRoom(000000000 + "");
            meetType = MeetType.MEET_NO_EXIST;

        } else {


        }

    }

    /**
     * 切换会议
     *
     * @param meetingListEntity
     */
    private void switchJoinMeet(MeetingListEntity meetingListEntity) {
        mAnyrtcMeet.SwitchRoom(meetingListEntity.getAnyrtcid());
    }

    /**
     * fragment 切换，不会重新加载
     *
     * @param to       显示的Fragment  ifPostition =3 时候要影藏全部
     * @param position 给要添加的Fragment设置tag
     */
    public void switchContent(Fragment from, Fragment to, int position) {
        mContent = from;
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

    /**
     * 影藏全部
     */
    public void hideAllContent() {
        fm = getSupportFragmentManager();
        mMeetingFragment = (MeetingFragment) fm.findFragmentByTag(tags[TAG_FRAG_MEETING]);
        mCallRingMeFragment = (CallRingMeFragment) fm.findFragmentByTag(tags[TAG_FRAG_CALL_ME]);
        mCallRingFragment = (CallRingFragment) fm.findFragmentByTag(tags[TAG_FRAG_CALL]);
        if (mMeetingFragment != null && mCallRingFragment != null) {
            fm.beginTransaction().hide(mMeetingFragment).hide(mCallRingFragment).hide(mCallRingMeFragment).commit();
        }
    }

    @Override
    protected void stateCheck(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mMeetingFragment = new MeetingFragment();
            mCallRingFragment = new CallRingFragment();
            mCallRingMeFragment = new CallRingMeFragment();
            mContent = mMeetingFragment;
            tags[0] = "0";
            ft.add(R.id.flayout_content, mContent, tags[TAG_FRAG_MEETING]);
            tags[1] = "1";
            ft.add(R.id.flayout_content, mCallRingMeFragment, tags[TAG_FRAG_CALL_ME]).hide(mCallRingMeFragment);
            tags[2] = "2";
            ft.add(R.id.flayout_content, mCallRingFragment, tags[TAG_FRAG_CALL]).hide(mCallRingFragment);
            ft.commit();
        } else {
            mMeetingFragment = (MeetingFragment) fm.findFragmentByTag(tags[TAG_FRAG_MEETING]);
            mCallRingFragment = (CallRingFragment) fm.findFragmentByTag(tags[TAG_FRAG_CALL_ME]);
            mCallRingMeFragment = (CallRingMeFragment) fm.findFragmentByTag(tags[TAG_FRAG_CALL]);
            fm.beginTransaction().show(mMeetingFragment).hide(mCallRingFragment).hide(mCallRingMeFragment).commit();
        }
    }


    /**
     * 输入会议号码进入房间
     */
    public void enterMeeting(String meetNumId) {
        /**
         *
         * 判断格式是否合法;
         *
         * 1.获取到房间信息.
         *
         * 2.判断是否可以加入：1.私密 2.当前的会议已经被删除
         *
         * 3.插入列表，并进入房间: 更新列表中的位置
         *
         *  都提示弹出通话动画的窗口
         */

        mNetWork.getMeetingInfo(meetNumId);


    }

    /***
     * 遥控器单机列表进入房间
     *
     * @param position
     */
    public void enterListMeeting(String position) {
        /**
         * 1. 直接进入
         * 2.更新列表中的位置
         */

    }

    /**
     * 获取到会议信息成功
     *
     * @param msg
     */
    private void getMeetingInfoSuccess(Message msg) {

        MeetingListEntity meetingListEntityInfo = mTVAPP.getmMeetingListEntityInfo();
        int usable = meetingListEntityInfo.getMeetenable();
        String meetinId = meetingListEntityInfo.getMeetingid();
        int position = mTVAPP.getMeetingIdPosition(meetinId);
        if (mDebug)
            Log.e(TAG, "getMeetingInfoSuccess: ------position" + position);
        switch (usable) {
            case 0:
                /**
                 * 会议已经被删除；
                 */
                Toast.makeText(mContext, R.string.str_meeting_deleted, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (position < 0) {
                    mNetWork.insertUserMeetingRoom(mTVAPP.getAuthorization(), meetinId);
                } else {
                    //当前列表中存在 直接进入会议;

                    joinMeet(meetingListEntityInfo, position);
                }
                break;
            case 2://private
                if (position < 0) {
                    Toast.makeText(mContext, R.string.str_meeting_privated, Toast.LENGTH_SHORT).show();
                } else {
                    joinMeet(meetingListEntityInfo, position);
                }
                break;
        }

    }

    /**
     * 通过获取到会议室信息以后进入会议
     *
     * @param meetingListEntity
     */
    public void joinMeet(MeetingListEntity meetingListEntity, int position) {
        mTVAPP.addMeetingHeardEntityPosition(position); //提升列表到头部
        joinAnyrtcMeet(meetingListEntity); //进入指定的会议;
        notifyDataSetChanged();  //通知适配器更新数据
    }

    /**
     * 插入会议成功
     */
    private void insertMeetingSuccess() {

        //adapter.notifyDataSetChanged();
        enterStartMeeting(mTVAPP.getmMeetingListEntityInfo());
    }

    private void notifyDataSetChanged() {
        Message msg = Message.obtain();
        msg.what = EventType.MSG_NOTIFY_DATA_CHANGE.ordinal();
        EventBus.getDefault().post(msg);
    }


    /**
     * EeventBus方法
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_RESPONS_ESTR_NULl:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread:请求网络失败 ");
                break;
            case MSG_CALL_START:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: 暂停");
                break;
            case MSG_GET_MEETING_INFO_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_MEETING_INFO_SUCCESS--获取用户列表成功");
                getMeetingInfoSuccess(msg);
                break;

            case MSG_GET_ROOM_LIST_SUCCESS:
                if (mDebug) {
                    Log.e(TAG, "onEventMainThread: --获取列表成功");
                }

                break;

            case MSG_INSERT_USER_MEETING_ROOM_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: --列表成功");
                insertMeetingSuccess();
                break;
            case MSG_UP_DATE_USER_MEETING_JOIN_TIME_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: --更新时间成功");
                //adapter.notifyDataSetChanged();
                break;
        }
    }

}

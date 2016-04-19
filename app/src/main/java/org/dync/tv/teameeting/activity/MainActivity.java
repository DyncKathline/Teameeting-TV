package org.dync.tv.teameeting.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.dync.teameeting.sdkmsgclient.jni.JMClientType;
import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;
import org.dync.tv.teameeting.fragment.BaseFragment;
import org.dync.tv.teameeting.fragment.CallRingFragment;
import org.dync.tv.teameeting.fragment.CallRingMeFragment;
import org.dync.tv.teameeting.fragment.MeetingFragment;
import org.dync.tv.teameeting.structs.EventType;
import org.dync.tv.teameeting.structs.MeetType;
import org.dync.tv.teameeting.view.RoomControls;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseMeetingActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private FragmentManager fm;
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
    private long mExitTime = 0;
    private boolean isMeetType = false;  //判断是否在切会过会议, 也就是说在会议室里面中是否一致在切换会议;  false: 回到主界面  true:还待在会议室中;

    public boolean isLocaVideoFlag = true;
    public boolean isLocaAudioFlag = true;
    public boolean isSwitchRoom = false;  //是否是从一个房间切换到另外一个房间
    private int mPeopleNum = 0;//在会议的人数，默认是1，即就只有自己一个人

    @Bind(R.id.llayout_phone)//展示当前会议id的父控件
            LinearLayout llayoutPhone;
    @Bind(R.id.tv_phone_text)
    TextView tvPhoneText;
    @Bind(R.id.llayout_control)
    RoomControls lLayoutControl;
    @Bind(R.id.btn_audio_soundon)
    Button btnAudioSoundon;
    @Bind(R.id.btn_main_hangup)
    Button btnMainHangup;
    @Bind(R.id.pbar_wait)
    ProgressBar pbarWait;
    @Bind(R.id.rlayout_call_wait)
    RelativeLayout rLayoutWait;

    @Bind(R.id.llayout_focus_videoview)
    LinearLayout llayoutFocusVideView;
    @Bind(R.id.iv_localview)
    ImageView ivLocalView;
    @Bind(R.id.iv_remoteview1)
    ImageView ivRemoteView1;
    @Bind(R.id.iv_remoteview2)
    ImageView ivRemoteView2;
    @Bind(R.id.iv_remoteview3)
    ImageView ivRemoteView3;
    ReqSndMsgEntity mReqSndMsg;
    boolean isExist = false;  // 已经在会议中，别人入会 挂断是否显示隐藏全部

    /**
     * 有人进会回调该方法;
     *
     * @param reqSndMsg
     */
    @Override
    protected void onRequesageMsg(ReqSndMsgEntity reqSndMsg) {
        if (mDebug)
            Log.e(TAG, "onRequesageMsg: " + reqSndMsg.toString());

        if (reqSndMsg.getTags() == JMClientType.MCSENDTAGS_ENTER) {
            //有人入会
            mReqSndMsg = reqSndMsg;
            meetingListEntity = mTVAPP.getMeetingIdtoEntity(reqSndMsg.getRoom());
            enterRoomNumMen();
        } else if (reqSndMsg.getTags() == JMClientType.MCSENDTAGS_LEAVE) {
            /**
             *  打开的呼叫界面
             */
            if (mReqSndMsg != null && reqSndMsg.getFrom().equals(mReqSndMsg.getFrom())) {
                if ((mPeopleNum == 0) && reqSndMsg.getNmem() == 0) {
                    isMeetType = false;
                }
                hungUp(isMeetType);
            }
            leaveRoomNumMen(reqSndMsg);
        }
    }

    /**
     * 有人离开房间
     *
     * @param reqSndMsg
     */
    private void leaveRoomNumMen(ReqSndMsgEntity reqSndMsg) {
        //有人离开
        if (MeetType.MEET_EXIST == meetType) {
            if (reqSndMsg.getNmem() == 0 && reqSndMsg.getTags() == JMClientType.MCSENDTAGS_LEAVE && joinMeetistEntity != null) {
                if (joinMeetistEntity.getMeetingid() == reqSndMsg.getRoom()) {
                    peopleNumZero();
                }
                return;
            }
        }
    }

    /**
     * 有人进入房间
     */
    private void enterRoomNumMen() {
        if (MeetType.MEET_NO_EXIST == meetType) {
            Log.e("TAG", "没有人入会议");
            switchContent(mMeetingFragment, mCallRingMeFragment, TAG_FRAG_CALL_ME); //切换Fragment

            goneLayout(true);
            isExist = false;
            mCallRingMeFragment.setPhoneText(meetingListEntity.getMeetingid());
            mCallRingMeFragment.requestFocus();
            sendPostCall(true);
        } else if (MeetType.MEET_EXIST == meetType) {
            if (joinMeetistEntity != null && meetingListEntity != null && joinMeetistEntity.getMeetingid().equals(meetingListEntity.getMeetingid())) {
                Log.e("TAG", "MeetType.MEET_EXIST == meetType");
                isExist = false;
                return;
            } else {
                //有人进入其他会议
                Log.e("TAG", "有人进入其他会议");
                isExist = true;
                switchContent(mMeetingFragment, mCallRingMeFragment, TAG_FRAG_CALL_ME); //切换Fragment
                goneLayout(true);
                mCallRingMeFragment.setPhoneText(meetingListEntity.getMeetingid());
                mCallRingMeFragment.requestFocus();
            }
        }
        sendPostCall(true);
    }

    /**
     * 当房间中人数为0 的时候, 既房间退出所有人,只剩下自己;
     */
    private void peopleNumZero() {
        switchContent(mCallRingMeFragment, mMeetingFragment, TAG_FRAG_MEETING);
        goneLayout(true);
        mMeetingFragment.requestFocus();
        //切换房间
        destoryJoinMeet();
        if (meetType == MeetType.MEET_EXIST) {
            rLayoutWait.setVisibility(View.GONE);
        }
        ivRemoteView1.setFocusable(false);
        ivRemoteView2.setFocusable(false);
        ivRemoteView3.setFocusable(false);
    }

    @Override
    public void requestFocus() {
        btnAudioSoundon.requestFocus();
//        setIsFocus(true);
        goneLayout(false);
    }

    @Override
    public void goneLayout(boolean gone) {
        if (gone) {
            lLayoutControl.hide();
            lLayoutControl.setVisibility(View.GONE);
            llayoutPhone.setVisibility(View.GONE);
            llayoutFocusVideView.setVisibility(View.GONE);
        } else {
            lLayoutControl.show();
            lLayoutControl.setVisibility(View.VISIBLE);
            llayoutPhone.setVisibility(View.VISIBLE);
            llayoutFocusVideView.setVisibility(View.VISIBLE);
        }
    }

    private void sendPostCall(boolean isRecenived) {
        Message msg = Message.obtain();
        if (isRecenived) {
            msg.what = EventType.MSG_CALL_ME_START.ordinal();//发送到CallRingMeFragment
        } else {
            //msg.what = EventType.MSG_CALL_START.ordinal();
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
        btnMainHangup.setOnClickListener(this);
        btnAudioSoundon.setOnClickListener(this);
        ivLocalView.setOnClickListener(this);
        ivRemoteView1.setOnClickListener(this);
        ivRemoteView2.setOnClickListener(this);
        ivRemoteView3.setOnClickListener(this);

        btnMainHangup.setOnFocusChangeListener(this);
        btnAudioSoundon.setOnFocusChangeListener(this);
        ivLocalView.setOnFocusChangeListener(this);
        ivRemoteView1.setOnFocusChangeListener(this);
        ivRemoteView2.setOnFocusChangeListener(this);
        ivRemoteView3.setOnFocusChangeListener(this);

        mMeetingFragment.setOnMeetingListener(new MeetingFragment.MeetingListener() {

            @Override
            public void onClickCall(String phone, boolean isCallorItem) {
                Log.e(TAG, "onClickCall: ----呼叫");
                enterMeeting(phone);
            }
        });

        /**
         * 呼叫别人
         */
        mCallRingFragment.setOnCallRingListener(new CallRingFragment.CallRingListener() {
            @Override
            public void onClickHungUp() {
//                if (meetType == MeetType.MEET_EXIST && !isExist) {
//                    hideAllContent();
//                } else if (isSwitchRoom) {
//                    hideAllContent();
//                } else {
//                    switchContent(mCallRingFragment, mMeetingFragment, TAG_FRAG_MEETING);
//                    goneLayout(true);
//                    mMeetingFragment.requestFocus();
//                }

            }

        });

        /**
         * 他人呼叫自己
         */
        mCallRingMeFragment.setOnCallRingListener(new CallRingMeFragment.CallRingMeListener() {
            @Override
            public void onClickHungUp() {
                hungUp(isMeetType);
            }

            @Override
            public void onClickAccept(String phone) {
                hideAllContent();
                joinAnyrtcMeet(meetingListEntity);
                goneLayout(false);
                tvPhoneText.setText(phone);
                btnAudioSoundon.requestFocus();
            }

        });

    }

    /**
     * 挂断按钮
     */
    private void hungUp(boolean switchRoom) {
        if (meetType == MeetType.MEET_EXIST && !isExist) {
            hideAllContent();
        } else if (switchRoom) {
            hideAllContent();
            requestFocus();
        } else {
            switchContent(mCallRingMeFragment, mMeetingFragment, TAG_FRAG_MEETING);
            goneLayout(true);
            mMeetingFragment.requestFocus();
            isSwitchRoom = false;
            isMeetType = false;
        }
        notifyDataSetChanged();
    }

    /*private void showVideoViewLayout() {
        lLayoutControl.show();
        btnAudioSoundon.setFocusable(true);
        btnMainHangup.setFocusable(true);
        llayoutFocusVideView.setVisibility(View.VISIBLE);
    }

    private void hideVideViewLayout() {
        lLayoutControl.hide();
        btnAudioSoundon.setFocusable(false);
        btnMainHangup.setFocusable(false);
        llayoutFocusVideView.setVisibility(View.GONE);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        lLayoutControl.hide();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mMeetingFragment != null) {
            mMeetingFragment.initMeetingFragmentLayout();
            goneLayout(true);
        }
    }

    /**
     * 加入会议
     *
     * @param meetingListEntity
     */
    private void joinAnyrtcMeet(MeetingListEntity meetingListEntity) {
        if (meetingListEntity != null) {
            Log.e(TAG, "joinAnyrtcMeet: ");
            boolean join = false;
            if (meetType == MeetType.MEET_NO_EXIST) {
                //第一次入会
                join = mAnyrtcMeet.Join(meetingListEntity.getAnyrtcid());
            } else {
                //切换会议;
                isSwitchRoom = true;
                join = mAnyrtcMeet.SwitchRoom(meetingListEntity.getAnyrtcid());
            }

            if (join == true) {
                isMeetType = true;
                meetType = MeetType.MEET_EXIST;
                goneLayout(false);
                joinMeetistEntity = meetingListEntity;
            } else {
                meetType = MeetType.MEET_NO_EXIST;
            }
        }
    }

    /**
     * 挂断的方法
     */
    private void destoryJoinMeet() {
        if (meetType == MeetType.MEET_EXIST) {
            joinMeetistEntity = null;
            boolean tag = mAnyrtcMeet.SwitchRoom(000000000 + "");
            if (mDebug)
                Log.e(TAG, "destoryJoinMeet: " + tag);
            meetType = MeetType.MEET_EXIST;
            isSwitchRoom = false;
            isMeetType = false;
        } else {

        }
    }


    public void setLocalVideoEnabled() {
        if (isLocaVideoFlag) {
            mAnyrtcMeet.SetLocalVideoEnabled(false);

        } else {

            mAnyrtcMeet.SetLocalVideoEnabled(true);
        }
    }

    /**
     * 设置音频按钮
     */
    public void setLocalAudioEnabled() {
        if (isLocaAudioFlag) {
            mAnyrtcMeet.SetLocalAudioEnabled(false);
            //btnAudioSoundon.setCompoundDrawables(null, getDrawable(R.drawable.btnview_soundoff_icon_selector), null, null);
        } else {
            mAnyrtcMeet.SetLocalAudioEnabled(true);
            //btnAudioSoundon.setCompoundDrawablesRelativeWithIntrinsicBounds(null, getDrawable(R.drawable.btnview_soundon_icon_selector), null, null);
        }
        isLocaAudioFlag = !isLocaAudioFlag;
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
     * fragment 切换，不会重新加载，这里使用commit()方法会出现
     * java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
     * 使用commitAllowingStateLoss()方法替代
     *
     * @param from     要隐藏的Fragment
     * @param to       要显示的Fragment
     * @param position 给要显示的Fragment设置tag
     */
    public void switchContent(BaseFragment from, BaseFragment to, int position) {
        Log.e(TAG, "switchContent");
        if (from != to) {
            FragmentTransaction ft = fm.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                ft.hide(from).add(R.id.flayout_content, to, tags[position]).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
        from.goneLayout(true);
        to.goneLayout(false);
    }

    /**
     * 隐藏全部Fragment
     */
    public void hideAllContent() {
        fm = getSupportFragmentManager();
        mMeetingFragment = (MeetingFragment) fm.findFragmentByTag(tags[TAG_FRAG_MEETING]);
        mCallRingMeFragment = (CallRingMeFragment) fm.findFragmentByTag(tags[TAG_FRAG_CALL_ME]);
        mCallRingFragment = (CallRingFragment) fm.findFragmentByTag(tags[TAG_FRAG_CALL]);
        if (mMeetingFragment != null && mCallRingFragment != null && mCallRingMeFragment != null) {
            fm.beginTransaction().hide(mMeetingFragment).hide(mCallRingFragment).hide(mCallRingMeFragment).commit();
            mMeetingFragment.goneLayout(true);
            mCallRingMeFragment.goneLayout(true);
            mCallRingFragment.goneLayout(true);
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
            tags[0] = "0";
            ft.add(R.id.flayout_content, mMeetingFragment, tags[TAG_FRAG_MEETING]);
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
        enterStartMeeting(mTVAPP.getmMeetingListEntityInfo());
        mTVAPP.addMeetingHeardEntityPosition(position); //更新列表到头部
        joinAnyrtcMeet(meetingListEntity); //进入指定的会议;
        notifyDataSetChanged();  //通知适配器更新数据

        sendPostCall(false);
        hideAllContent();
        goneLayout(false);
        tvPhoneText.setText(meetingListEntity.getMeetingid());
        btnAudioSoundon.requestFocus();
    }

    /**
     * 插入会议成功
     */
    private void insertMeetingSuccess() {

        //adapter.notifyDataSetChanged();

        joinMeet(mTVAPP.getMeetingLists().get(0), 0);


    }

    private void notifyDataSetChanged() {
        Message msg = Message.obtain();
        msg.what = EventType.MSG_NOTIFY_DATA_CHANGE.ordinal();
        EventBus.getDefault().post(msg);
    }


    @Override
    void onPeopleNumChange(int peopleNum) {
        mPeopleNum = peopleNum;
        Log.e(TAG, "onPeopleNumChange: " + peopleNum);
        if (peopleNum > 0) {
            rLayoutWait.setVisibility(View.GONE);
            switch (peopleNum) {

                case 1:
                    ivRemoteView1.setFocusable(true);
                    ivRemoteView2.setFocusable(false);
                    ivRemoteView3.setFocusable(false);
                    break;
                case 2:
                    ivRemoteView1.setFocusable(true);
                    ivRemoteView2.setFocusable(true);
                    ivRemoteView3.setFocusable(false);
                    break;
                case 3:
                    ivRemoteView1.setFocusable(true);
                    ivRemoteView2.setFocusable(true);
                    ivRemoteView3.setFocusable(true);
                    break;
            }
            hideAllContent();
        } else if (peopleNum == 0 && isSwitchRoom) {
            // 切换房间
            isSwitchRoom = false;

        } else {
            // 当人数为0的时候显示
            peopleNumZero();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.exit_once_more, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                mNetWork.signOut(TVAPP.getmTVAPP().getAuthorization());
                destoryJoinMeet();

                this.finish();

                System.exit(0);
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        int[] lic = new int[2];
        switch (v.getId()) {
            case R.id.btn_audio_soundon:
                Log.e(TAG, "onClick: " + "打开或者关闭声音");
                setLocalAudioEnabled();
                break;

            case R.id.btn_main_hangup:
                Log.e(TAG, "onClick: " + "离开");
                goneLayout(true);
                switchContent(mCallRingMeFragment, mMeetingFragment, TAG_FRAG_MEETING); //打开接通或者取消的按钮
                mMeetingFragment.requestFocus();
                destoryJoinMeet();
                break;
            case R.id.iv_localview:
                break;
            case R.id.iv_remoteview1:

                ivRemoteView1.getLocationOnScreen(lic);
                break;
            case R.id.iv_remoteview2:

                ivRemoteView2.getLocationOnScreen(lic);
                break;
            case R.id.iv_remoteview3:
                ivRemoteView3.getLocationOnScreen(lic);
                break;
        }
        Log.e(TAG, "-----onClick-----X= " + x + ",Y= " + y);
        width = ivRemoteView1.getWidth();
        height = ivRemoteView1.getHeight();
        Log.e(TAG, "-----onClick-----X= " + x + ",Y= " + y);
        if (mAnyrtcViews != null) {
            mAnyrtcViews.switchLocaToRomoteScreen(lic[0] + width / 2, lic[1] + height / 2);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.btn_audio_soundon:
                Log.e(TAG, "onFocusChange: " + "打开或者关闭声音");
                break;
            case R.id.btn_main_hangup:
                Log.e(TAG, "onFocusChange: " + "离开");
                break;
            case R.id.iv_localview:
                Log.e(TAG, "onFocusChange: " + "本地的像");
                break;
            case R.id.iv_remoteview1:
                Log.e(TAG, "onFocusChange: " + "别人1的像");
                break;
            case R.id.iv_remoteview2:
                Log.e(TAG, "onFocusChange: " + "别人2的像");
                break;
            case R.id.iv_remoteview3:
                Log.e(TAG, "onFocusChange: " + "别人3的像");
                break;
        }
    }

    /**
     * EeventBus方法
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_RESPONS_ESTR_NULl:
                if (mDebug) {
                    Log.e(TAG, "onEventMainThread: 这是谁发的我不知道");
                }
                break;
            case MSG_RESPONS_ESTR_NULL:
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
                notifyDataSetChanged();
                break;
        }
    }

}

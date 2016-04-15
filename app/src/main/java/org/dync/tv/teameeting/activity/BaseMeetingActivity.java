package org.dync.tv.teameeting.activity;

import android.util.Log;
import android.widget.RelativeLayout;

import org.anyrtc.AnyrtcMeet;
import org.anyrtc.common.MeetEvents;
import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.utils.AnyRTCViewsTV;


/**
 * Created by Xiao_Bailong on 2016/3/30.
 */
public abstract class BaseMeetingActivity extends BaseActivity implements MeetEvents {

    public AnyRTCViewsTV mAnyrtcViews;
    public AnyrtcMeet mAnyrtcMeet;
    private MeetingListEntity mMeetingListEntity;

    /**
     * 进入房间
     *
     * @param meetingListEntity
     */
    public void enterStartMeeting(MeetingListEntity meetingListEntity) {
        mNetWork.updateUserMeetingJointime(mTVAPP.getAuthorization(), meetingListEntity.getMeetingid());
        // Intent intent = new Intent(mContext, MeetingActivity.class);
        // intent.putExtra(ExtraType.EXTRA_MEETING_ENTITY, meetingListEntity);
        // startActivity(intent);
    }


    @Override
    protected void initMeet() {
        mAnyrtcViews = new AnyRTCViewsTV((RelativeLayout) findViewById(R.id.rlayout_videos));
        mAnyrtcMeet = new AnyrtcMeet(this, this);
        mAnyrtcMeet.InitAnyRTCViewEvents(mAnyrtcViews);

        mAnyrtcViews.setOnPeopleChangeListener(peopleChangeListener);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (mAnyrtcMeet != null) {
            mAnyrtcMeet.OnResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        {//* Release RtcClient
            if (mAnyrtcMeet != null) {
                mAnyrtcMeet.Destroy();
                mAnyrtcMeet = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAnyrtcMeet != null) {
            mAnyrtcMeet.OnPause();
        }
    }

    /**
     * 视屏通话时的界面
     *
     * @param s
     */
    @Override
    public void OnRtcJoinMeetOK(String s) {

    }

    @Override
    public void OnRtcJoinMeetFailed(String s, int i, String s1) {

    }

    @Override
    public void OnRtcLeaveMeet(int i) {
        if (mDebug)
            Log.e(TAG, "OnRtcLeaveMeet: +i" + i);
    }


    /**
     * 视频会议人数该改变监听
     */
    AnyRTCViewsTV.onPeopleChangeListener peopleChangeListener = new AnyRTCViewsTV.onPeopleChangeListener() {
        @Override
        public void OnPeopleNumChange(int peopleNum) {
            if (mDebug) {
                Log.e(TAG, "OnPeopleNumChange: -----" + peopleNum);
            }
            onPeopleNumChange(peopleNum);
        }
    };

    abstract void onPeopleNumChange(int peopleNum);

}

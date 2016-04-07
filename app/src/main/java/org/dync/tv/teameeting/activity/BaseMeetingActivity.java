package org.dync.tv.teameeting.activity;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.anyrtc.AnyrtcMeet;
import org.anyrtc.common.MeetEvents;
import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.utils.AnyRTCViews;

import java.io.File;

/**
 * Created by Xiao_Bailong on 2016/3/30.
 */
public abstract class BaseMeetingActivity extends BaseActivity implements MeetEvents {
    private File photoPath; //文件地址
    private SurfaceView mCameraPreview;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean isBackCameraOn = true;

    public AnyrtcMeet mRtclient;
    public AnyRTCViews mAnyrtcViews;
    private MeetingListEntity mMeetingListEntity;

    /**
     * 进入房间
     *
     * @param meetingListEntity
     */
    public void enterStartMeeting(MeetingListEntity meetingListEntity) {
        mNetWork.updateUserMeetingJointime(mTVAPP.getAuthorization(), meetingListEntity.getMeetingid());
       // Intent intent = new Intent(context, MeetingActivity.class);
        //intent.putExtra(ExtraType.EXTRA_MEETING_ENTITY, meetingListEntity);

       // startActivity(intent);
    }


    @Override
    protected void initMeet() {

        ImageView imgCloseVoice = new ImageView(context);
        ImageView imgCloseVideo = new ImageView(context);
        mAnyrtcViews = new AnyRTCViews((RelativeLayout) findViewById(R.id.rlayout_videos), context, imgCloseVoice, imgCloseVideo);
        mRtclient = new AnyrtcMeet(this, this);
        //mRtclient.Join(meetingListEntity.getAnyrtcid());//980988 //800000000014
        mRtclient.InitAnyRTCViewEvents(mAnyrtcViews);
        //mRtclient.SetLocalVideoEnabled(true);
        //mAnyrtcViews.setVideoViewPeopleNumEvent(videoViewPeopleNumEvent);

    }

    public void joinMeet(MeetingListEntity meetingListEntity) {

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

    }


    /**
     * 视频会议人数该改变监听
     */
    AnyRTCViews.VideoViewPeopleNumEvent videoViewPeopleNumEvent = new AnyRTCViews.VideoViewPeopleNumEvent() {
        @Override
        public void OnPeopleNumChange(int peopleNum) {
            //有人进入
            Log.e(TAG, "有人进入了");
            // rLayoutCallWait.setVisibility(View.GONE);
        }
    };

}

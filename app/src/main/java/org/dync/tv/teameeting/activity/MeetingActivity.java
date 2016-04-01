package org.dync.tv.teameeting.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.anyrtc.AnyrtcMeet;
import org.anyrtc.common.MeetEvents;
import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;
import org.dync.tv.teameeting.structs.ExtraType;
import org.dync.tv.teameeting.utils.AnyRTCViews;

import butterknife.Bind;

public class MeetingActivity extends BaseActivity implements MeetEvents {


    public AnyrtcMeet mRtclient;
    @Bind(R.id.rlayout_videos)
    public RelativeLayout rLayoutVideos; //视频根布局
    @Bind(R.id.rlayout_call_wait)
    public RelativeLayout rLayoutCallWait;//呼叫等待
    @Bind(R.id.img_close_video)
    public ImageView imgCloseVideo;
    @Bind(R.id.img_close_voice)
    public ImageView imgCloseVoice;
    public AnyRTCViews mAnyrtcViews;
    private MeetingListEntity mMeetingListEntity;


    /**
     *
     * @param reqSndMsg
     */
    @Override
    protected void onRequesageMsg(ReqSndMsgEntity reqSndMsg) {

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_meeting;
    }

    @Override
    protected void init() {
        //mAnyrtcViews = new AnyRTCViews((RelativeLayout) findViewById(R.id.rLayout_videos));
        initLayout();
        initData();

    }

    private void initLayout() {
        mAnyrtcViews = new AnyRTCViews((RelativeLayout) findViewById(R.id.rlayout_videos), context, imgCloseVoice, imgCloseVideo);
        mRtclient = new AnyrtcMeet(this, this);
        mAnyrtcViews.setVideoViewPeopleNumEvent(videoViewPeopleNumEvent);
    }


    private void initData() {
        formatIntent();
        JoinAnyRTC(mMeetingListEntity.getAnyrtcid());
    }


    private void formatIntent() {
        Intent intent = getIntent();
        mMeetingListEntity = (MeetingListEntity) intent.getSerializableExtra(ExtraType.EXTRA_MEETING_ENTITY);
    }

    protected void JoinAnyRTC(String anyrtcId) {
        mRtclient.Join(anyrtcId);//980988 //800000000014
        mRtclient.InitAnyRTCViewEvents(mAnyrtcViews);
    }

    /**
     * 视频会议人数该改变监听
     */
    AnyRTCViews.VideoViewPeopleNumEvent videoViewPeopleNumEvent = new AnyRTCViews.VideoViewPeopleNumEvent() {
        @Override
        public void OnPeopleNumChange(int peopleNum) {
            //有人进入
            Log.e(TAG, "有人进入了");
            rLayoutCallWait.setVisibility(View.GONE);
        }
    };

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
}

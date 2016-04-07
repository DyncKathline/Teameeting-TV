package org.dync.tv.teameeting;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import org.anyrtc.Anyrtc;
import org.dync.teameeting.sdkmsgclient.msgs.TMMsgSender;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.bean.SelfData;
import org.dync.tv.teameeting.chatmessage.ChatMessageClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiao_Bailong on 2016/3/30.
 */
public class TVAPP extends Application {
    public static boolean mDebug = true;
    private static TVAPP mTVAPP;
    private static SelfData mSelfData;

    private static TMMsgSender mMsgSender; //消息控制
    private static ChatMessageClient mChatMessageClient;
    private static Context context;
    private MeetingListEntity meetingListEntity;
    private MeetingListEntity mMeetingListEntityInfo; //获取到的会议信息
    private List<MeetingListEntity> mMeetingLists = new ArrayList<MeetingListEntity>();

    public TVAPP() {
        mTVAPP = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mTVAPP = this;
        context = this;
        initSDKData();//初始化SDK配置
    }

    private void initSDKData() {
        Anyrtc.InitAnyrtc("13103994", "de095967d87cd6f9a51ec4e3ee9a0ab7", "E7FCkvPeaRBWGIxtO+mTjoJqu+TmqEDRNyi9YyFu82o", "Teameeting");
        mChatMessageClient = new ChatMessageClient(this);
        if (mChatMessageClient != null) {
            mMsgSender = new TMMsgSender(this, mChatMessageClient);
        }
    }

    /**
     * 获取全局变量
     *
     * @return
     */
    public static TVAPP getmTVAPP() {
        if (mTVAPP == null) {
            mTVAPP = new TVAPP();
        }
        return mTVAPP;
    }

    public MeetingListEntity getMeetingListEntity() {
        return meetingListEntity;
    }

    public void setMeetingListEntity(MeetingListEntity meetingListEntity) {
        this.meetingListEntity = meetingListEntity;
    }


    public MeetingListEntity getmMeetingListEntityInfo() {
        return mMeetingListEntityInfo;
    }

    public void setmMeetingListEntityInfo(MeetingListEntity mMeetingListEntityInfo) {
        this.mMeetingListEntityInfo = mMeetingListEntityInfo;
    }

    /**
     * get the device id unique
     *
     * @return the device id
     */
    public String getDevId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static TMMsgSender getMsgSender() {
        return mMsgSender;
    }

    public static ChatMessageClient getmChatMessageClient() {
        return mChatMessageClient;
    }

    public static Context getContext() {
        return context;
    }


    public void setSelfData(SelfData selfData) {
        mSelfData = selfData;
    }

    public SelfData getSelfData() {
        return mSelfData;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getAuthorization() {
        if (mSelfData == null) {
            new NullPointerException("Authorization的签证为空");
        }
        return mSelfData.getAuthorization();
    }
}

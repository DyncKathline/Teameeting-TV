package org.dync.tv.teameeting.activity;

import android.content.Intent;
import android.os.Message;
import android.util.Log;

import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.structs.EventType;
import org.dync.tv.teameeting.structs.ExtraType;

/**
 * Created by Xiao_Bailong on 2016/3/30.
 */
public abstract class BaseMeetingActivity extends BaseActivity {


    /**
     * 进入房间
     *
     * @param meetingListEntity
     */
    public void enterStartMeeting(MeetingListEntity meetingListEntity) {

        Intent intent = new Intent(context, MeetingActivity.class);
        intent.putExtra(ExtraType.EXTRA_MEETING_ENTITY, meetingListEntity);
        startActivity(intent);
    }




    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_RESPONS_ESTR_NULl:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread:请求网络失败 ");
                break;
            default:
                onEventMainThreadAbs(msg);
                break;
        }
    }

    abstract void onEventMainThreadAbs(Message msg);
}

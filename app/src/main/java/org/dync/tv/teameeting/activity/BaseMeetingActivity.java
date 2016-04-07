package org.dync.tv.teameeting.activity;

import android.content.Intent;

import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.structs.ExtraType;

/**
 * Created by Xiao_Bailong on 2016/3/30.
 */
public abstract class BaseMeetingActivity extends BaseActivity {


    /**
     * 进入会议室
     *
     * @param meetingListEntity
     */
    public void enterStartMeeting(MeetingListEntity meetingListEntity) {

        Intent intent = new Intent(context, MeetingActivity.class);
        intent.putExtra(ExtraType.EXTRA_MEETING_ENTITY, meetingListEntity);
        startActivity(intent);
    }
}

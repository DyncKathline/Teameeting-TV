package org.dync.tv.teameeting.http;

import android.util.Log;

import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.structs.BundleType;
import org.dync.tv.teameeting.structs.EventType;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class NetWork {
    private boolean mDebug = TVAPP.mDebug;
    public String TAG = this.getClass().getSimpleName();

    /**
     * 获取用户数据
     *
     * @param meetingid
     * @param joinType
     * @return
     */
    public void getMeetingInfo(final String meetingid, final String joinType) {
        String url = "meeting/getMeetingInfo/" + meetingid;

        HttpContent.get(url, new TmTextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                super.onSuccess(statusCode, code, message, responseString);
                if (mDebug)
                    Log.e(TAG, "onSuccess: getMeetingInfo" + responseString);
                if (code == 200) {
                    try {
                        JSONObject json = new JSONObject(responseString);
                        String info = json.getString("meetingInfo");
                        bundle.putString(BundleType.PUT_STRING_INFO, info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    msg.what = EventType.MSG_GET_MEETING_INFO_SUCCESS.ordinal();

                } else {
                    msg.what = EventType.MSG_GET_MEETING_INFO_FAILED.ordinal();
                }
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }

        });
    }

}


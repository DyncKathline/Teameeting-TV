package org.dync.tv.teameeting.http;

import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.bean.MeetingList;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.structs.BundleType;
import org.dync.tv.teameeting.structs.EventType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;


public class NetWork {
    private boolean mDebug = TVAPP.mDebug;
    public String TAG = this.getClass().getSimpleName();


    /**
     * getRoomLists
     *
     * @param sign
     * @param pageNum
     * @param pageSize
     */
    public void getRoomLists(final String sign, final String pageNum, final String pageSize) {
        String url = "meeting/getRoomList";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                if (mDebug) {
                    Logger.e(responseString);
                }
                if (statusCode == 200) {
                    msg.what = EventType.MSG_GET_ROOM_LIST_SUCCESS.ordinal();
                    MeetingList meetingList = gson.fromJson(responseString, MeetingList.class);
                    if (meetingList != null) {
                        TVAPP.getmTVAPP().setMeetingLists(meetingList.getMeetingList());
                    }
                } else {
                    msg.what = EventType.MSG_GET_ROOM_LIST_FAILED.ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }
        });

    }

    /**
     * 获取用户数据
     *
     * @param meetingid
     * @return
     */
    public void getMeetingInfo(final String meetingid) {
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
                        MeetingListEntity meetingListInfo = gson.fromJson(info, MeetingListEntity.class);
                        TVAPP.getmTVAPP().setmMeetingListEntityInfo(meetingListInfo);
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


    /**
     * insertUserMeetingRoom
     *
     * @param sign
     * @param meetingid
     */

    public void insertUserMeetingRoom(final String sign, final String meetingid) {

        String url = "meeting/insertUserMeetingRoom";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                super.onSuccess(statusCode, code, message, responseString);
                if (mDebug)
                    Log.e(TAG, "onSuccess: insertUserMeetingRoom" + responseString);
                if (code == 200) {
                    TVAPP.getmTVAPP().addMeetingHeardEntity();
                    msg.what = EventType.MSG_INSERT_USER_MEETING_ROOM_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_INSERT_USER_MEETING_ROOM_FAILED
                            .ordinal();
                }
                bundle.putString("meetingid", meetingid);
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }
        });

    }

    /**
     * updateUserMeetingJointime 18
     *
     * @param sign
     * @param meetingid
     */
    public void updateUserMeetingJointime(final String sign, final String meetingid) {

        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingid", meetingid);

        String url = "meeting/updateUserMeetingJointime";
        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                super.onSuccess(statusCode, code, message, responseString);
                if (mDebug)
                    Log.e(TAG, "onSuccess: updateUserMeetingJointime" + responseString);
                if (code == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        long jointime = jsonObject.getLong("jointime");
                        List<MeetingListEntity> meetingLists = TVAPP.getmTVAPP().getMeetingLists();
                        int position = TVAPP.getmTVAPP().getMeetingIdPosition(meetingid);
                        if (position < 0)
                            new NullPointerException("更新房间的位置不对");
                        MeetingListEntity meetingListEntity = meetingLists.get(position);
                        meetingListEntity.setJointime(jointime);
                        meetingLists.remove(position);
                        meetingLists.add(0, meetingListEntity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    msg.what = EventType.MSG_UP_DATE_USER_MEETING_JOIN_TIME_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_UP_DATE_USER_MEETING_JOIN_TIME_FAILED.ordinal();
                }
                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);
            }
        });
    }

    /**
     * signOut
     *
     * @param sign
     */
    public void signOut(final String sign) {
        String url = "users/signout";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                if (mDebug)
                    Log.e(TAG, "onSuccess: signOut" + responseString);
                if (code == 200) {
                    msg.what = EventType.MSG_SIGNOUT_SUCCESS.ordinal();
                } else {
                    msg.what = EventType.MSG_SIGNOUT_FAILED.ordinal();
                }

                bundle.putString("message", message);
                msg.setData(bundle);
                EventBus.getDefault().post(msg);

            }
        });
    }
}


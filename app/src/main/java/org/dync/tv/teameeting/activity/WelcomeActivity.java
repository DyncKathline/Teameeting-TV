package org.dync.tv.teameeting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.bean.MeetingList;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.bean.SelfData;
import org.dync.tv.teameeting.http.HttpContent;
import org.dync.tv.teameeting.http.NetWork;
import org.dync.tv.teameeting.http.TmTextHttpResponseHandler;
import org.dync.tv.teameeting.utils.LocalUserInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {

    private static final int MESSAGE_INIT_SUCCESS = 0X02;
    private static final int MESSAGE_GETLIST_SUCCESS = 0X03;
    private NetWork mNetWork;
    private String mUserid;
    private String mSign;
    private boolean mDebug = TVAPP.mDebug;
    public String TAG = this.getClass().getSimpleName();
    private TVAPP mTVAPP;
    private Context mContext;
    private String mRoomName = "Dync_盒子";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_INIT_SUCCESS:
                    getRoomLists(mTVAPP.getAuthorization(), 1 + "", 20 + "");
                    break;
                case MESSAGE_GETLIST_SUCCESS:
                    initData();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_welcome);
        mTVAPP = TVAPP.getmTVAPP();
        mUserid = mTVAPP.getDevId();
        mSign = mTVAPP.getAuthorization();
        initAppData(mUserid, "2", "2", "2", "TeamMeeting");

    }

    private void initData() {
        String userinfoStr = LocalUserInfo.getInstance(mContext).getUserStr(LocalUserInfo.MEETING_LIST_ENTITY);

        if (userinfoStr.equals("")) {
            applyRoom(mRoomName);
        } else {
            fromJsonMeeting(userinfoStr);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMainActivity();
                }
            }, 2000);
        }
    }


    /**
     * 启动
     */
    private void startMainActivity() {
        //初始化聊天数据
        chatMessageInint();
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    /**
     * 初始化消息内容
     */
    private void chatMessageInint() {
        String uname = mTVAPP.getSelfData().getInformation().getUname();
        if (uname != null) {
            uname = "dync-tv";
        }

        int msg = mTVAPP.getMsgSender().TMInit(mUserid, mTVAPP.getSelfData().getAuthorization(), uname, HttpContent.SERVICE_URL, HttpContent.MSG_SERVICE_POINT);

        if (msg >= 0) {
            if (mDebug) {
                Log.e(TAG, "Chat Message Inint successed");
            }
        } else if (mDebug) {
            Log.e(TAG, "Chat Message Inint failed");
        }
    }

    /**
     * 申请房间
     *
     * @param meetingName 房间名字
     */
    private void applyRoom(String meetingName) {
        String pushable = "1";
        String meetenablde = "1";
        String authorization = mTVAPP.getAuthorization();
        netApplyRoom(authorization, meetingName, "0", "", meetenablde, pushable);
    }


    /**
     * 第一次进入初始化用户数据
     *
     * @param userid
     * @param uactype
     * @param uregtype
     * @param ulogindev
     * @param upushtoken
     */
    public void initAppData(final String userid, final String uactype, final String uregtype, final String ulogindev, final String upushtoken) {
        String url = "users/init";
        RequestParams params = new RequestParams();
        params.put("userid", userid);
        params.put("uactype", uactype);
        params.put("uregtype", uregtype);
        params.put("ulogindev", ulogindev);
        params.put("upushtoken", upushtoken);
        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                /**
                 * 获取 用户数据失败
                 */
            }

            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                super.onSuccess(statusCode, code, message, responseString);
                if (mDebug)
                    Logger.e(responseString);

                if (code == 200) {
                    SelfData selfData = gson.fromJson(responseString, SelfData.class);
                    mTVAPP.setSelfData(selfData);
                    if (mDebug) {
                        Log.i(TAG, "getInformation" + selfData.getInformation().toString());
                    }
                    handler.sendEmptyMessage(MESSAGE_INIT_SUCCESS);
                } else {
                    Log.e(TAG, "onSuccess: 状态码错误+code" + code);
                }
            }
        });

    }


    /**
     * 申请进入房间
     *
     * @param sign
     * @param meetingname
     * @param meetingtype
     * @param meetdesc
     * @param meetenable
     * @param pushable
     */
    public void netApplyRoom(final String sign, final String meetingname,
                             final String meetingtype, final String meetdesc, final String meetenable,
                             final String pushable) {
        String url = "meeting/applyRoom";
        RequestParams params = new RequestParams();
        params.put("sign", sign);
        params.put("meetingname", meetingname);
        params.put("meetingtype", meetingtype);
        params.put("meetdesc", meetdesc);
        params.put("meetenable", meetenable);
        params.put("pushable", pushable);

        HttpContent.post(url, params, new TmTextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDebug)
                    Log.e(TAG, "onFailure: --netApplyRoom------" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                if (mDebug)
                    Logger.e(responseString);
                if (statusCode == 200) {
                    //保存获取列表成功的数据
                    LocalUserInfo.getInstance(mContext).setUserStr(LocalUserInfo.MEETING_LIST_ENTITY, responseString);
                    fromJsonMeeting(responseString);
                    startMainActivity();
                }
            }
        });

    }


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
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mDebug)
                    Log.e(TAG, "onFailure: --getRoomLists------" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, int code, String message, String responseString) {
                if (mDebug)
                    Logger.e(responseString);
                if (statusCode == 200) {
                    MeetingList meetingList = gson.fromJson(responseString, MeetingList.class);
                    if (meetingList != null) {
                        //设置房间列表
                        mTVAPP.setMeetingLists(meetingList.getMeetingList());
                        handler.sendEmptyMessage(MESSAGE_GETLIST_SUCCESS);
                    }
                }
            }
        });

    }

    /**
     * @param responseString
     * @throws JSONException
     */
    private void fromJsonMeeting(String responseString) {
        JSONObject json = null;
        try {
            json = new JSONObject(responseString);

            String meetingInfo = json.getString("meetingInfo");
            MeetingListEntity meeting = new Gson().fromJson(meetingInfo, MeetingListEntity.class);
            meeting.setCreatetime(meeting.getJointime());
            meeting.setOwner(1);
            meeting.setMemnumber(0);
            meeting.setUserid(mTVAPP.getDevId());

            mTVAPP.setMeetingListEntity(meeting);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

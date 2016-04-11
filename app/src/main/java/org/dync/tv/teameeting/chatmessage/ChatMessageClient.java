package org.dync.tv.teameeting.chatmessage;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.dync.teameeting.sdkmsgclient.jni.JMClientHelper;
import org.dync.teameeting.sdkmsgclient.jni.JMClientType;
import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;

import java.util.ArrayList;


/**
 * Created by zhulang on 2016/3/30
 */
public class ChatMessageClient implements JMClientHelper {
    private String TAG = this.getClass().getSimpleName();
    private boolean mDebug = TVAPP.mDebug;
    private ArrayList<ChatMessageObserver> mObServers = new ArrayList<ChatMessageObserver>();
    private Context context;

    public ChatMessageClient(Context context) {
        this.context = context;
    }

    /**
     * regiseter
     *
     * @param observer
     */
    public synchronized void registerObserver(ChatMessageObserver observer) {
        if ((observer != null) && !mObServers.contains(observer)) {
            mObServers.add(observer);
        }
    }

    public synchronized void unregisterObserver(ChatMessageObserver observer) {
        if ((observer != null) && mObServers.contains(observer)) {
            mObServers.remove(observer);
        }
    }

    /**
     * notify
     *
     * @param reqSndMsg
     */
    public synchronized void notifyRequestMessage(ReqSndMsgEntity reqSndMsg) {
        for (ChatMessageObserver observer : mObServers) {
            Log.e(TAG, "notifyRequestMessage: " + 1);
            observer.OnReqSndMsg(reqSndMsg);
        }
    }

    //

    /**
     * implement for JMClientHelper
     */
    @Override
    public void OnSndMsg(String msg) {
        if (mDebug) {
            Logger.e(msg);
        }
        if (msg != null) {
            senMag(msg);
        }
    }

    private void senMag(String msg) {
        Gson gson = new Gson();
        //这里可以接受到入会。
        ReqSndMsgEntity reqSndMsgEntity = gson.fromJson(msg, ReqSndMsgEntity.class);
        if (reqSndMsgEntity.getTags() == JMClientType.MCSENDTAGS_ENTER && !reqSndMsgEntity.getFrom().equals(TVAPP.getmTVAPP().getDevId())) {
            notifyRequestMessage(reqSndMsgEntity);
        }
    }


    @Override
    public void OnGetMsg(String msg) {
        String s = "OnReqGetMsg msg:" + msg;

        if (mDebug) {
            Log.e(TAG, "OnReqGetMsg: " + s);
        }
    }

    @Override
    public void OnMsgServerConnected() {

        if (mDebug) {
            Log.e(TAG, "OnMsgServerConnected: ");
        }
    }

    @Override
    public void OnMsgServerDisconnect() {
        if (mDebug) {
            Log.e(TAG, "OnMsgServerDisconnect: ");
        }
    }

    @Override
    public void OnMsgServerConnectionFailure() {
        if (mDebug) {
            Log.i(TAG, "OnMsgServerConnectionFailure: ");
        }
    }

    @Override
    public void OnMsgServerState(int connStatus) {
        if (mDebug) {
            Log.i(TAG, "OnMsgServerState: " + connStatus);
        }
    }

    public interface ChatMessageObserver {
        void OnReqSndMsg(ReqSndMsgEntity reqSndMsg);
    }
}

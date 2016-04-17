package org.dync.tv.teameeting.http;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
import org.dync.tv.teameeting.structs.EventType;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by zhulang on 2015/12/28 0028.
 */
public class TmTextHttpResponseHandler extends TextHttpResponseHandler {
    String TAG = this.getClass().getSimpleName();

    public Gson gson = null;
    public Bundle bundle;
    public Message msg;

    public TmTextHttpResponseHandler() {
        super("UTF-8");
        gson = new Gson();
        msg = new Message();
        bundle = new Bundle();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        // network or server  problem
        Logger.e("onFailure: "+statusCode+"-----responseString" +responseString);
        msg.what = EventType.MSG_RESPONS_ESTR_NULL.ordinal();
        EventBus.getDefault().post(msg);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        JSONObject jsonObject = null;
        String message = null;
        try {
            jsonObject = new JSONObject(responseString);
            int code = jsonObject.getInt("code");
            message = jsonObject.getString("message");
            this.onSuccess(statusCode, code, message, responseString);
            this.onSuccess(statusCode, code, message, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onSuccess(int statusCode, int code, String message, String responseString) {
        Log.w("TextHttpResponseHandler", "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
    }

    public void onSuccess(int statusCode, int code, String message, JSONObject jsonObject) {
        // Log.w("TextHttpResponseHandler", "onSuccess(int, Header[], JSONArray) was not overriden, but callback was received");
    }

}

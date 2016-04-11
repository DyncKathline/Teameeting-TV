package org.dync.tv.teameeting.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;
import org.dync.tv.teameeting.chatmessage.ChatMessageClient;
import org.dync.tv.teameeting.http.NetWork;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 基础 Activity
 * Created zhulang 2016年3月21日16:39:25.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public boolean mDebug = TVAPP.mDebug;
    public String TAG = this.getClass().getSimpleName();
    private ChatMessageClient mChatMessageClinet;
    public Context mContext;
    public NetWork mNetWork;
    public TVAPP mTVAPP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());
        ButterKnife.bind(this);
        mContext = this;
        EventBus.getDefault().register(this);
        mNetWork = new NetWork();
        mTVAPP = TVAPP.getmTVAPP();
        stateCheck(savedInstanceState);
        init();
        initMeet();
        if (mDebug)
            Log.i(TAG, "onCreate");
        registerObserverClinet();
    }

    /**
     * 状态检测 用于内存不足的时候保证fragment不会重叠
     *
     * @param savedInstanceState
     */
    protected abstract void stateCheck(Bundle savedInstanceState);

    protected abstract void initMeet();

    /**
     * 注册消息接受者
     */
    private void registerObserverClinet() {
        mChatMessageClinet = TVAPP.getmTVAPP().getmChatMessageClient();
        mChatMessageClinet.registerObserver(chatMessageObserver);
    }

    ChatMessageClient.ChatMessageObserver chatMessageObserver = new ChatMessageClient.ChatMessageObserver() {
        @Override
        public void OnReqSndMsg(final ReqSndMsgEntity reqSndMsg) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onRequesageMsg(reqSndMsg);
                    }
                });
            } else {
                onRequesageMsg(reqSndMsg);
            }
        }
    };

    protected abstract void onRequesageMsg(ReqSndMsgEntity reqSndMsg);

    protected abstract int provideContentViewId();

    protected abstract void init();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mDebug)
            Log.i(TAG, "onRestart");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDebug)
            Log.i(TAG, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDebug)
            Log.i(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDebug)
            Log.i(TAG, "onPause");


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDebug)
            Log.i(TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        if (mDebug)
            Log.i(TAG, "onDestroy");

    }


}

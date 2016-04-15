package org.dync.tv.teameeting.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.http.NetWork;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by xiongxuesong-pc on 2016/4/1.
 */
public abstract class BaseFragment extends Fragment {
    public boolean mDebug = TVAPP.mDebug;
    public String TAG = this.getClass().getSimpleName();
    protected Context mContext;
    public NetWork mNetWork;
    public TVAPP mTVAPP;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mDebug)
            Log.i(TAG, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDebug)
            Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(provideViewLayoutId(), container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mContext = getContext();
        mNetWork = new NetWork();
        mTVAPP = TVAPP.getmTVAPP();
        init();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mDebug)
            Log.i(TAG, "onActivityCreated");
    }

    protected abstract int provideViewLayoutId();

    protected abstract void init();

    /**
     * 使布局内的控件获得焦点并聚焦到它身上
     */
    public abstract void requestFocus();

    /**
     * 是否隐藏布局，true为隐藏，即setVisibility(View.GONE)；false为显示，即setVisibility(View.VISIBLE)
     */
    public abstract void goneLayout(boolean gone);

    @Override
    public void onStart() {
        super.onStart();
        if (mDebug)
            Log.i(TAG, "onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDebug)
            Log.i(TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDebug)
            Log.i(TAG, "onPause");


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDebug)
            Log.i(TAG, "onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDebug)
            Log.i(TAG, "onDestroyView");
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDebug)
            Log.i(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDebug)
            Log.i(TAG, "onDetach");
    }
}

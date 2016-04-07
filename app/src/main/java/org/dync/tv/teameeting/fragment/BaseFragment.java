package org.dync.tv.teameeting.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    protected abstract int provideViewLayoutId();

    protected abstract void init();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}

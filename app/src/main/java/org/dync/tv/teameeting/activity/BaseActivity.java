package org.dync.tv.teameeting.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import org.dync.tv.teameeting.TVAPP;

import butterknife.ButterKnife;

/**
 * 基础 Activity
 * Created zhulang 2016年3月21日16:39:25.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private boolean mDebug = TVAPP.mDebug;
    public String TAG = this.getClass().getSimpleName();
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());
        ButterKnife.bind(this);
        context = this;
        init();

        if (mDebug)
            Log.i(TAG, "onCreate");

    }

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
        if (mDebug)
            Log.i(TAG, "onDestroy");

    }
}

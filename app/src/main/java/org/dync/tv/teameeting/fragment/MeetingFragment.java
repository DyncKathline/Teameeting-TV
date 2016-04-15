package org.dync.tv.teameeting.fragment;


import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.anyrtc.AnyrtcMeet;
import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.adapter.RoomListAdapter;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.structs.EventType;

import java.util.List;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingFragment extends BaseFragment implements View.OnFocusChangeListener, View.OnClickListener {
    @Bind(R.id.llayout_meeting)
    public LinearLayout llayoutMeeting;//layout_meeting.xml
    @Bind(R.id.edt_meeting)
    public EditText edtMeetNum;//
    @Bind(R.id.scaleImageView)
    public ImageView scaleImageView;//缩放二维码图片的框
    @Bind(R.id.rlayout_image)
    public RelativeLayout rlayoutImage;//二维码图片
    @Bind(R.id.button1)
    public Button button1;//数字1
    @Bind(R.id.button2)
    public Button button2;//数字2
    @Bind(R.id.button3)
    public Button button3;//数字3
    @Bind(R.id.button4)
    public Button button4;//数字4
    @Bind(R.id.button5)
    public Button button5;//数字5
    @Bind(R.id.button6)
    public Button button6;//数字6
    @Bind(R.id.button7)
    public Button button7;//数字7
    @Bind(R.id.button8)
    public Button button8;//数字8
    @Bind(R.id.button9)
    public Button button9;//数字9
    @Bind(R.id.button10)
    public Button button10;//清除
    @Bind(R.id.button11)
    public Button button11;//数字0
    @Bind(R.id.button12)
    public Button button12;//删除
    @Bind(R.id.button13)
    public Button button13;//呼叫
    @Bind(R.id.rlayout)
    public RelativeLayout rlayout;//tlayout的父控件
    @Bind(R.id.btnllayout)
    public LinearLayout btnllayout;//这些button的父控件
    @Bind(R.id.imageButton)
    public ImageButton imageButton;//光标
    @Bind(R.id.tv_meeting)
    public TextView meetingTextView;//显示会议ID
    @Bind(R.id.listView)
    public ListView listView;//会议列表

    public int imageWidth = 0;//二维码图片的宽
    public int imageHeight = 0;//二维码图片的高
    public int buttonWidth = 0;//button的宽
    public int buttonHeight = 0;//button的高
    public int ibtnWidth = 0;//光标的宽
    public int ibtnHeight = 0;//光标的高
    public int translationX = 0;//跟随button获取焦点移动的X距离
    public int translationY = 0;//跟随button获取焦点移动的Y距离
    public String phone = "";
    private int preOldPosition = -1;
    private int oldPosition = -1;//光标焦点移动下一个焦点时前一个位置，此处与buttonX后面的X数字一致，如“1”，即button1的位置
    private ScaleAnimation scaleAnimation;
    private int duration = 0;//光标移动的时长
    private roomListAdapter adapter;
    public List<MeetingListEntity> mMeetingLists;
    AnyrtcMeet mAnyrtcMeet;


    @Override
    protected int provideViewLayoutId() {
        return R.layout.fragment_meeting;
    }

    @Override
    protected void init() {

        drawBeforeGetSize();
        initListener();
        initData();
    }

    @Override
    public void requestFocus() {
        button1.requestFocus();
//        setIsFocus(true);
        goneLayout(false);
    }

    @Override
    public void goneLayout(boolean gone) {
        if (gone) {
            llayoutMeeting.setVisibility(View.GONE);
        } else {
            llayoutMeeting.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        meetingTextView.setText(TVAPP.getmTVAPP().getMeetingListEntity().getMeetingid());

        mMeetingLists = TVAPP.getmTVAPP().getMeetingLists();
//        mMeetingLists = new ArrayList<>();
//        MeetingListEntity meetingListEntity = new MeetingListEntity();
//        meetingListEntity.setMeetingid("11111111");
//        meetingListEntity.setMeetname("hezi");
//        mMeetingLists.add(meetingListEntity);
        adapter = new roomListAdapter(mMeetingLists, mContext);
        listView.setAdapter(adapter);
        listView.setFocusable(true);
        listView.setOnItemClickListener(listItemListener);
    }

    AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //进入会议;
            MeetingListEntity meeting = mMeetingLists.get(position);
            //单机列表进入会议;
            if (mMeetingListener != null) {
                Log.e(TAG, "onClick: ");
                mMeetingListener.onClickCall(meeting.getMeetingid());
            }

        }
    };

    /**
     * 在画控件之前获取到控件的宽高
     */
    private void drawBeforeGetSize() {
        //提前获取imageview控件的宽
        ViewTreeObserver vto = rlayoutImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlayoutImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageWidth = rlayoutImage.getWidth() + 20;
                imageHeight = rlayoutImage.getHeight();
                Log.i("TAG", "imageWidth= " + imageWidth + "; imageHeight= " + imageHeight);
            }
        });

        //提前获取rlayout控件的宽高
        vto = rlayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                buttonWidth = rlayout.getWidth() / 3;
                buttonHeight = rlayout.getHeight() / 5;
                Log.i("TAG", "buttonWidth= " + buttonWidth + "; buttonHeight= " + buttonHeight);
            }
        });

        //提前获取imageButton控件的宽
        vto = imageButton.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ibtnWidth = imageButton.getWidth();
                ibtnHeight = imageButton.getHeight();
                Log.i("TAG", "ibtnWidth= " + ibtnWidth + "; ibtnHeight= " + ibtnHeight);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int left = (buttonWidth - ibtnWidth) / 2;
                int top = (buttonHeight - ibtnHeight) / 2;
                int right = ibtnWidth;
                int bottom = ibtnHeight;
                lp.setMargins(left, top, right, bottom);
                imageButton.setLayoutParams(lp);
            }
        });
    }

    private void initListener() {
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        button10.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
        button13.setOnClickListener(this);

        rlayoutImage.setOnFocusChangeListener(this);
        button1.setOnFocusChangeListener(this);
        button2.setOnFocusChangeListener(this);
        button3.setOnFocusChangeListener(this);
        button4.setOnFocusChangeListener(this);
        button5.setOnFocusChangeListener(this);
        button6.setOnFocusChangeListener(this);
        button7.setOnFocusChangeListener(this);
        button8.setOnFocusChangeListener(this);
        button9.setOnFocusChangeListener(this);
        button10.setOnFocusChangeListener(this);
        button11.setOnFocusChangeListener(this);
        button12.setOnFocusChangeListener(this);
        button13.setOnFocusChangeListener(this);
        listView.setOnFocusChangeListener(this);
    }

    /**
     * 应用第一次展示此界面，使焦点聚焦到按钮数字1上，并使二维码界面平移到屏幕外面
     */
    public void initMeetingFragmentLayout() {
        requestFocus();
        Log.e("TAG", -imageWidth + "");
        llayoutMeeting.animate().translationX(-imageWidth).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlayout_image:
                break;
            case R.id.button1:
                phone += "1";
                break;
            case R.id.button2:
                phone += "2";
                break;
            case R.id.button3:
                phone += "3";
                break;
            case R.id.button4:
                phone += "4";
                break;
            case R.id.button5:
                phone += "5";
                break;
            case R.id.button6:
                phone += "6";
                break;
            case R.id.button7:
                phone += "7";
                break;
            case R.id.button8:
                phone += "8";
                break;
            case R.id.button9:
                phone += "9";
                break;
            case R.id.button10:
                phone = "";
                break;
            case R.id.button11:
                phone += "0";
                break;
            case R.id.button12:
                if (phone.length() > 0)
                    phone = phone.substring(0, phone.length() - 1);
                break;
            case R.id.button13:
                if (mMeetingListener != null) {
                    Log.e(TAG, "onClick: ");
                    mMeetingListener.onClickCall(phone);
                    //Message msg = Message.obtain();
                    //msg.what = EventType.MSG_CALL_START.ordinal();//分别发送到CallRingFragment、MeetingFragment
                    //EventBus.getDefault().post(msg);
                }
                break;
            case R.id.listView:
                break;

        }
        edtMeetNum.setText(phone);
    }

    public interface MeetingListener {
        void onClickCall(String phone);
    }

    private MeetingListener mMeetingListener;

    public void setOnMeetingListener(MeetingListener listener) {
        Logger.e("Listener= " + listener);
        mMeetingListener = listener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.rlayout_image:
                oldPosition = 0;
                duration = 0;
                if (imageButton != null) {
                    if (hasFocus) {
                        llayoutMeeting.animate().translationX(0).setDuration(200).start();
                        imageButton.setVisibility(View.GONE);
                        button13.setBackgroundResource(R.drawable.button_default);
                        scaleImageView.setVisibility(View.VISIBLE);
                        if (scaleAnimation == null) {
                            scaleAnimation = new ScaleAnimation(0, 1, 0, 1, imageWidth, imageHeight / 2);
                        }
                        scaleAnimation.setDuration(200);
                        scaleImageView.startAnimation(scaleAnimation);
                    } else {
                        llayoutMeeting.animate().translationX(-imageWidth).setDuration(200).start();
                        imageButton.setVisibility(View.VISIBLE);
                        scaleImageView.setVisibility(View.GONE);
                        requestFocus();
                    }
                }
                break;
            case R.id.button1:
                translationX = 0;
                translationY = 0;
                if (oldPosition == 0 || oldPosition == 13) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                preOldPosition = 1;
                oldPosition = 1;
                break;
            case R.id.button2:
                translationX = buttonWidth;
                translationY = 0;
                preOldPosition = 2;
                oldPosition = 2;
                duration = 200;
                break;
            case R.id.button3:
                translationX = buttonWidth * 2;
                translationY = 0;
                if (oldPosition == 14) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                preOldPosition = 3;
                oldPosition = 3;
                break;
            case R.id.button4:
                translationX = 0;
                translationY = buttonHeight;
                if (oldPosition == 0) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                oldPosition = 4;
                break;
            case R.id.button5:
                translationX = buttonWidth;
                translationY = buttonHeight;
                oldPosition = 5;
                duration = 200;
                break;
            case R.id.button6:
                translationX = buttonWidth * 2;
                translationY = buttonHeight;
                if (oldPosition == 14) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                oldPosition = 6;
                break;
            case R.id.button7:
                translationX = 0;
                translationY = buttonHeight * 2;
                if (oldPosition == 0) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                oldPosition = 7;
                break;
            case R.id.button8:
                translationX = buttonWidth;
                translationY = buttonHeight * 2;
                oldPosition = 8;
                duration = 200;
                break;
            case R.id.button9:
                translationX = buttonWidth * 2;
                translationY = buttonHeight * 2;
                if (oldPosition == 14) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                oldPosition = 9;
                break;
            case R.id.button10:
                translationX = 0;
                translationY = buttonHeight * 3;
                if (oldPosition == 0) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                preOldPosition = 10;
                oldPosition = 10;
                break;
            case R.id.button11:
                translationX = buttonWidth;
                translationY = buttonHeight * 3;
                preOldPosition = 11;
                oldPosition = 11;
                duration = 200;
                break;
            case R.id.button12:
                translationX = buttonWidth * 2;
                translationY = buttonHeight * 3;
                if (preOldPosition == 1 || preOldPosition == 2 || preOldPosition == 3 || preOldPosition == 10 || preOldPosition == 11) {
                    duration = 0;
                } else {
                    duration = 200;
                }
                preOldPosition = 12;
                oldPosition = 12;
                break;
            case R.id.button13:
                if (imageButton != null) {
                    if (hasFocus) {
                        imageButton.setVisibility(View.GONE);
                        button13.setBackgroundResource(R.drawable.btn_ok_selector);
                    } else {
                        imageButton.setVisibility(View.VISIBLE);
                        button13.setBackgroundResource(R.drawable.button_default);
                    }
                }
                duration = 0;
                oldPosition = 13;
                break;
            case R.id.listView:
                if (imageButton != null && adapter != null) {
                    if (hasFocus) {
                        imageButton.setVisibility(View.GONE);
                        adapter.hasFocus(true);
                    } else {
                        imageButton.setVisibility(View.VISIBLE);
                        adapter.hasFocus(false);
                    }
                }
                duration = 0;
                oldPosition = 14;
                break;
        }
        Log.e("TAG", "oldPosition= " + oldPosition);
        if (imageButton != null) {
            imageButton.animate().translationX(translationX).translationY(translationY).setDuration(duration).start();
        }
    }

    /**
     * EeventBus方法
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_CALL_START:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: 暂停");
                break;
            case MSG_NOTIFY_DATA_CHANGE:
                //列表数据改变
                mMeetingLists = mTVAPP.getMeetingLists();
                adapter.notifyDataSetChanged();
            case MSG_CALL_STOP:
                requestFocus();
                break;
        }

    }


}

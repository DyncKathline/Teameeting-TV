package org.dync.tv.teameeting.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.dync.tv.teameeting.R;
import org.dync.tv.teameeting.TVAPP;
import org.dync.tv.teameeting.adapter.RoomListAdapter;
import org.dync.tv.teameeting.bean.MeetingListEntity;
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;
import org.dync.tv.teameeting.structs.EventType;

import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseMeetingActivity implements View.OnFocusChangeListener {

    /****************************
     * layout_meeting.xml
     *********************************/
    @Bind(R.id.llayout_meeting)
    public LinearLayout llayoutMeeting;//layout_meeting.xml
    @Bind(R.id.edt_meeting)
    public EditText editMeetNum;//
    @Bind(R.id.scaleImageView)
    public ImageView scaleImageView;//缩放二维码图片的框
    @Bind(R.id.imageView)
    public ImageView imageview;//二维码图片
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
    private RoomListAdapter adapter;
    public List<MeetingListEntity> mMeetingLists;
    /****************************
     * layout_call_ring.xml
     *********************************/
    @Bind(R.id.llayout_call_ring)
    public LinearLayout llayoutCallRing;//layout_call_ring.xml
    @Bind(R.id.LoadingHalo1)
    public ImageView LoadingHalo1;
    @Bind(R.id.LoadingHalo2)
    public ImageView LoadingHalo2;
    @Bind(R.id.LoadingHalo3)
    public ImageView LoadingHalo3;
    @Bind(R.id.LoadingHalo4)
    public ImageView LoadingHalo4;
    @Bind(R.id.btn_accept)
    public Button callAccept;
    @Bind(R.id.btn_hungUp)
    public Button callHungUp;

    private boolean isStartAnim = false;//是否关闭动画
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    animset(LoadingHalo2);
                    handler.sendEmptyMessageDelayed(2, 500);
                    break;
                case 2:
                    animset(LoadingHalo3);
                    handler.sendEmptyMessageDelayed(3, 500);
                    break;
                case 3:
                    animset(LoadingHalo4);
                    handler.sendEmptyMessageDelayed(4, 500);
                    break;
                case 4:
                    animset(LoadingHalo1);
                    handler.sendEmptyMessageDelayed(1, 500);
                    break;
            }
        }
    };

    @Override
    protected void onRequesageMsg(ReqSndMsgEntity reqSndMsg) {

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        drawBeforeGetSize();
        initListener();
        initData();
        Logger.e(TVAPP.getmTVAPP().getMeetingListEntity().toString());
        //使button1主动获取到焦点
        button1.requestFocus();
    }

    private void initData() {
        //获取会议ID
        meetingTextView.setText(TVAPP.getmTVAPP().getMeetingListEntity().getMeetingid());
        //模拟数据
        mMeetingLists = mTVAPP.getMeetingLists();
        adapter = new RoomListAdapter(mMeetingLists, context);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initListener() {
        imageview.setOnFocusChangeListener(this);
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

        callAccept.setOnFocusChangeListener(this);
        callHungUp.setOnFocusChangeListener(this);
    }

    /**
     * 在画控件之前获取到控件的宽高
     */
    private void drawBeforeGetSize() {
        //提前获取imageview控件的宽
        ViewTreeObserver vto = imageview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                imageWidth = imageview.getWidth() + 20;
                imageHeight = imageview.getHeight();
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
                buttonHeight = buttonWidth;
                //buttonHeight = rlayout.getHeight()/6;
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
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
                llayoutCallRing.setVisibility(View.VISIBLE);
                llayoutMeeting.setVisibility(View.GONE);
                isStartAnim = true;
                animset(LoadingHalo1);
                handler.sendEmptyMessageDelayed(1, 0);
                //进入会议室
                //String meetNum = editMeetNum.getText().toString();
                //enterMeeting(meetNum);
                // enterStartMeeting(TVAPP.getmTVAPP().getMeetingListEntity());
                break;
            case R.id.listView:
                break;
            case R.id.btn_accept:

                break;
            case R.id.btn_hungUp:
                llayoutCallRing.setVisibility(View.GONE);
                llayoutMeeting.setVisibility(View.VISIBLE);
                isStartAnim = false;
                handler.removeCallbacksAndMessages(null);
                break;
        }
        editMeetNum.setText(phone);
    }

    /**
     * 输入会议号码进入房间
     */
    public void enterMeeting(String meetNumId) {
        /**
         *
         * 判断格式是否合法;
         *
         * 1.获取到房间信息.
         *
         * 2.判断是否可以加入：1.私密 2.当前的会议已经被删除
         *
         * 3.插入列表，并进入房间: 更新列表中的位置
         *
         *  都提示弹出通话动画的窗口
         */

        mNetWork.getMeetingInfo(meetNumId);


    }

    /***
     * 遥控器单机列表进入房间
     *
     * @param position
     */
    public void enterListMeeting(String position) {
        /**
         * 1. 直接进入
         * 2.更新列表中的位置
         */

    }

    /**
     * 获取到会议信息成功
     *
     * @param msg
     */
    private void getMeetingInfoSuccess(Message msg) {

        MeetingListEntity meetingListEntityInfo = mTVAPP.getmMeetingListEntityInfo();
        int usable = meetingListEntityInfo.getMeetenable();
        String meetinId = meetingListEntityInfo.getMeetingid();
        int position = mTVAPP.getMeetingIdPosition(meetinId);
        if (mDebug)
            Log.e(TAG, "getMeetingInfoSuccess: ------position" + position);
        switch (usable) {
            case 0:
                /**
                 * 会议已经被删除；
                 */
                Toast.makeText(context, R.string.str_meeting_deleted, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (position < 0) {
                    mNetWork.insertUserMeetingRoom(mTVAPP.getAuthorization(), meetinId);
                } else {
                    joinMeet(meetingListEntityInfo);
                }
                break;
            case 2://private
                if (position < 0) {
                    Toast.makeText(context, R.string.str_meeting_privated, Toast.LENGTH_SHORT).show();
                } else {
                    joinMeet(meetingListEntityInfo);
                }
                break;
        }

    }

    /**
     * 插入会议成功
     */
    private void insertMeetingSuccess() {
        adapter.notifyDataSetChanged();
        enterStartMeeting(mTVAPP.getmMeetingListEntityInfo());
    }


    public void animset(View view) {
        if (isStartAnim) {
            Logger.e("TAG", "动画开启中。。。。。");
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 1.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 1.0f);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(2000);
            animatorSet.playTogether(animator1, animator2, animator3);
            animatorSet.start();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.imageView:
                oldPosition = 0;
                duration = 0;
                if (hasFocus) {
                    llayoutMeeting.animate().translationX(imageWidth).setDuration(200).start();
                    imageButton.setVisibility(View.GONE);
                    button13.setBackgroundColor(getResources().getColor(R.color.transparent));
                    scaleImageView.setVisibility(View.VISIBLE);
                    if (scaleAnimation == null) {
                        scaleAnimation = new ScaleAnimation(0, 1, 0, 1, imageWidth, imageHeight / 2);
                    }
                    scaleAnimation.setDuration(200);
                    scaleImageView.startAnimation(scaleAnimation);
                } else {
                    llayoutMeeting.animate().translationX(0).setDuration(200).start();
                    imageButton.setVisibility(View.VISIBLE);
                    scaleImageView.setVisibility(View.GONE);
                    button1.requestFocus();
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
                if (hasFocus) {
                    imageButton.setVisibility(View.GONE);
                    button13.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    imageButton.setVisibility(View.VISIBLE);
                    button13.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                duration = 0;
                oldPosition = 13;
                break;
            case R.id.listView:
                oldPosition = 14;
                duration = 0;
                if (hasFocus) {
                    imageButton.setVisibility(View.GONE);
                } else {
                    imageButton.setVisibility(View.VISIBLE);
                }
                break;
        }
        Logger.e("TAG", "oldPosition= " + oldPosition);
        imageButton.animate().translationX(translationX).translationY(translationY).setDuration(duration).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出
        TVAPP.getmTVAPP().getMsgSender().TMUnin();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * EeventBus方法
     *
     * @param msg
     */
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_RESPONS_ESTR_NULl:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread:请求网络失败 ");
                break;
            case MSG_GET_MEETING_INFO_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_MEETING_INFO_SUCCESS--获取用户列表成功");
                getMeetingInfoSuccess(msg);
                break;

            case MSG_GET_ROOM_LIST_SUCCESS:
                if (mDebug) {
                    Log.e(TAG, "onEventMainThread: --获取列表成功");
                }
                break;

            case MSG_INSERT_USER_MEETING_ROOM_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: --列表成功");
                insertMeetingSuccess();
                break;
            case MSG_UP_DATE_USER_MEETING_JOIN_TIME_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread: --更新时间成功");
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }


}

package org.dync.tv.teameeting.activity;

import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import org.dync.tv.teameeting.bean.ReqSndMsgEntity;
import org.dync.tv.teameeting.structs.EventType;


public class MainActivity extends BaseMeetingActivity implements View.OnFocusChangeListener {

    public LinearLayout llayout;//layout_meeting.xml
    public EditText editText;//
    public ImageView scaleImageView;//缩放二维码图片的框
    public ImageView imageview;//二维码图片
    public Button button1;//数字1
    public Button button2;//数字2
    public Button button3;//数字3
    public Button button4;//数字4
    public Button button5;//数字5
    public Button button6;//数字6
    public Button button7;//数字7
    public Button button8;//数字8
    public Button button9;//数字9
    public Button button10;//清除
    public Button button11;//数字0
    public Button button12;//删除
    public Button button13;//呼叫
    public RelativeLayout rlayout;//tlayout的父控件
    public LinearLayout btnllayout;//这些button的父控件
    public ImageButton imageButton;//光标
    public TextView phoneTextView;//显示会议ID
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


    @Override
    protected void onRequesageMsg(ReqSndMsgEntity reqSndMsg) {
        /**
         * 弹出进会对话框，
         * 确定：进会，取消；呆在主界面
         */
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        initView();
        drawBeforeGetSize();
        initListener();
        initData();
        Logger.e(TVAPP.getmTVAPP().getMeetingListEntity().toString());
        //使button1主动获取到焦点
        button1.requestFocus();
    }

    private void initData() {
        //获取会议ID
        phoneTextView.setText(TVAPP.getmTVAPP().getMeetingListEntity().getMeetingid());
        //模拟数据
        final String[] str_name = new String[]{"jack", "debb", "robin", "kikt"};
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, str_name[position], Toast.LENGTH_SHORT).show();
            }
        });
        //创建ArrayAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_list_item_1, str_name);
        //绑定适配器
        listView.setAdapter(arrayAdapter);
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

        //提前获取button1控件的宽
//        vto = button1.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                button1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                buttonWidth = button1.getWidth();
//                buttonHeight = button1.getHeight();
//                //Log.i("TAG","translationX= "+translationX);
//            }
//        });

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

    /**
     * 初始化控件
     */
    private void initView() {
        llayout = (LinearLayout) findViewById(R.id.llayout);
        scaleImageView = (ImageView) findViewById(R.id.scaleImageView);
        imageview = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.edit);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        button10 = (Button) findViewById(R.id.button10);
        button11 = (Button) findViewById(R.id.button11);
        button12 = (Button) findViewById(R.id.button12);
        button13 = (Button) findViewById(R.id.button13);
        rlayout = (RelativeLayout) findViewById(R.id.rlayout);
        btnllayout = (LinearLayout) findViewById(R.id.btnllayout);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        listView = (ListView) findViewById(R.id.listView);
        phoneTextView = (TextView) findViewById(R.id.tv_phone);
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
                //进入会议室
                enterStartMeeting(TVAPP.getmTVAPP().getMeetingListEntity());
                break;
            case R.id.listView:
                break;
        }
        editText.setText(phone);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.imageView:
                if (hasFocus) {
                    llayout.animate().translationX(imageWidth).setDuration(200).start();
                    imageButton.setVisibility(View.GONE);
                    button13.setBackgroundColor(getResources().getColor(R.color.transparent));
                    scaleImageView.setVisibility(View.VISIBLE);
                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, imageWidth, imageHeight / 2);
                    scale.setDuration(200);
                    scaleImageView.startAnimation(scale);
                } else {
                    llayout.animate().translationX(0).setDuration(200).start();
                    imageButton.setVisibility(View.VISIBLE);
                    scaleImageView.setVisibility(View.GONE);
                    button1.requestFocus();
                }
                break;
            case R.id.button1:
                translationX = 0;
                translationY = 0;
                break;
            case R.id.button2:
                translationX = buttonWidth;
                translationY = 0;
                break;
            case R.id.button3:
                translationX = buttonWidth * 2;
                translationY = 0;
                break;
            case R.id.button4:
                translationX = 0;
                translationY = buttonHeight;
                break;
            case R.id.button5:
                translationX = buttonWidth;
                translationY = buttonHeight;
                break;
            case R.id.button6:
                translationX = buttonWidth * 2;
                translationY = buttonHeight;
                break;
            case R.id.button7:
                translationX = 0;
                translationY = buttonHeight * 2;
                break;
            case R.id.button8:
                translationX = buttonWidth;
                translationY = buttonHeight * 2;
                break;
            case R.id.button9:
                translationX = buttonWidth * 2;
                translationY = buttonHeight * 2;
                break;
            case R.id.button10:
                translationX = 0;
                translationY = buttonHeight * 3;
                break;
            case R.id.button11:
                translationX = buttonWidth;
                translationY = buttonHeight * 3;
                break;
            case R.id.button12:
                translationX = buttonWidth * 2;
                translationY = buttonHeight * 3;
                break;
            case R.id.button13:
                if (hasFocus) {
                    imageButton.setVisibility(View.GONE);
                    button13.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    imageButton.setVisibility(View.VISIBLE);
                    button13.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                break;
            case R.id.listView:
                if (hasFocus) {
                    imageButton.setVisibility(View.GONE);
                } else {
                    imageButton.setVisibility(View.VISIBLE);
                }
                break;
        }
        imageButton.animate().translationX(translationX).translationY(translationY).setDuration(200).start();
    }

    /**
     * EeventBus方法
     *
     * @param msg
     */
    @Override
    public void onEventMainThread(Message msg) {
        switch (EventType.values()[msg.what]) {
            case MSG_RESPONS_ESTR_NULl:
                if (mDebug)
                    Log.e(TAG, "onEventMainThread:请求网络失败 ");
                break;
            case MSG_GET_MEETING_INFO_SUCCESS:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_MEETING_INFO_SUCCESS");
            case MSG_GET_MEETING_INFO_FAILED:
                if (mDebug)
                    Log.e(TAG, "MSG_GET_MEETING_INFO_FAILED");
            default:
                break;
        }

    }
}

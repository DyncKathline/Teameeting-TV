package org.dync.tv.teameeting.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.anyrtc.Anyrtc;
import org.anyrtc.common.AnyRTCViewEvents;
import org.anyrtc.util.AppRTCUtils;
import org.anyrtc.view.PercentFrameLayout;
import org.dync.tv.teameeting.R;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Eric on 2016/3/4.
 */
public class AnyRTCViewsTV implements View.OnTouchListener, AnyRTCViewEvents {
    private static final int SUB_X = 50;
    private static final int SUB_Y = 50;
    private static final int SUB_WIDTH = 50;
    private static final int SUB_HEIGHT = 50;
    private static final int SUB_DIS_X = 10;
    private static final int SUB_DIS_Y = 10;
    private static final int Loc_SUB_HEIGHT = 67;
    private Context context;
    private static int mScreenWidth;
    private static int mScreenHeight;
    private int width = mScreenWidth * SUB_WIDTH / (100 * 3);
    private int height = mScreenHeight * SUB_HEIGHT / (100 * 3);
    private onPeopleChangeListener peopleChangeListener;

    /**
     * 人数改变监听
     */
    public interface onPeopleChangeListener {
        void OnPeopleNumChange(int peopleNum);
    }

    /**
     * 人数改变回掉监听
     *
     * @param videoViewPeopleNumEvent
     */
    public void setOnPeopleChangeListener(onPeopleChangeListener videoViewPeopleNumEvent) {
        peopleChangeListener = videoViewPeopleNumEvent;
    }

    public interface VideoViewEvent {
        void OnScreenSwitch(String strBeforeFullScrnId, String strNowFullScrnId);
    }

    protected static class VideoView {
        public String strPeerId;
        public int index;
        public int x;
        public int y;
        public int w;
        public int h;
        public PercentFrameLayout mLayout = null;

        public SurfaceViewRenderer mView = null;
        public VideoTrack mVideoTrack = null;
        public VideoRenderer mRenderer = null;

        public VideoView(String strPeerId, Context ctx, EglBase eglBase, int index, int x, int y, int w, int h) {
            this.strPeerId = strPeerId;
            this.index = index;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

            mLayout = new PercentFrameLayout(ctx);

            mLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mLayout.setFocusable(true);

            mView = new SurfaceViewRenderer(ctx);

            mView.init(eglBase.getEglBaseContext(), null);
            mView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mLayout.addView(mView);
        }

        public Boolean Fullscreen() {
            return w == 100 || h == 100;
        }

        /**
         * 判断是否点击在当前的像上面
         *
         * @param px
         * @param py
         * @return
         */
        public Boolean Hited(int px, int py) {
            if (!Fullscreen()) {
                int left = x * Anyrtc.gScrnWidth / 100;
                int top = y * Anyrtc.gScrnHeight / 100;
                int right = (x + w) * Anyrtc.gScrnWidth / 100;
                int bottom = (y + h) * Anyrtc.gScrnHeight / 100;
                if ((px >= left && px <= right) && (py >= top && px <= bottom)) {
                    return true;
                }
            }
            return false;
        }

        public void close() {
            mLayout.removeView(mView);
            mView.release();
            mView = null;
            mRenderer = null;
        }

        /**
         * 更新像的布局
         *
         * @param layout
         * @param view
         */
        private void updateVideoLayoutView(PercentFrameLayout layout, SurfaceViewRenderer view) {
            mLayout = layout;
            mView = view;
            if (mVideoTrack != null) {
                mVideoTrack.removeRenderer(mRenderer);
                mRenderer = new VideoRenderer(mView);
                mVideoTrack.addRenderer(mRenderer);
            }
            mView.requestLayout();
            // mLayout.animate().translationX(100).start();

        }


        private boolean voiceFalg;
    }

    private boolean mAutoLayout;
    private EglBase mRootEglBase;
    private RelativeLayout mVideoView;
    private VideoView mLocalRender;
    private HashMap<String, VideoView> mRemoteRenders;
    private Animation mTranslateAnimation;

    public void setAnimation() {
        mTranslateAnimation = new TranslateAnimation(0, 100, 0, 100);
        mTranslateAnimation.setDuration(2000);
        mLocalRender.mView.startAnimation(mTranslateAnimation);
    }

    private Animation mAlphaAnimation;
    private Animation mScaleAnimation;
    private Animation mRotateAnimation;

    public void setRoate() {
        mRotateAnimation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(3000);
        mLocalRender.mView.startAnimation(mRotateAnimation);
    }

    public void setScaleAnimation(View view) {
        view = mLocalRender.mView;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "translationX", 0, 1f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(3000);
        animatorSet.playTogether(animator1, animator2, animator3);
        animatorSet.start();
        //view.startAnimation(mScaleAnimation);
        // mLocalRender.mView.invalidate();
    }

    public AnyRTCViewsTV(RelativeLayout videoView) {
        AppRTCUtils.assertIsTrue(videoView != null);
        mAutoLayout = false;
        mVideoView = videoView;
        mVideoView.setOnTouchListener(this);
        mRootEglBase = EglBase.create();
        mRemoteRenders = new HashMap<>();

    }

    public VideoTrack LocalVideoTrack() {
        return mLocalRender.mVideoTrack;
    }

    private int GetVideoRenderSize() {
        int size = mRemoteRenders.size();
        if (mLocalRender != null) {
            size += 1;
        }
        return size;
    }

    private void SwitchIndex1ToFullscreen(VideoView fullscrnView) {
        AppRTCUtils.assertIsTrue(fullscrnView != null);
        VideoView view1 = null;
        if (mLocalRender != null && mLocalRender.index == 1) {
            view1 = mLocalRender;
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (render.index == 1) {
                    view1 = render;
                    break;
                }
            }
        }
        if (view1 != null) {
            SwitchViewPosition(view1, fullscrnView);
        }
    }

    /**
     * 判断哪一个像是占满整个屏幕的。
     *
     * @return
     */
    private VideoView GetFullScreen() {
        if (mLocalRender != null && mLocalRender.Fullscreen())
            return mLocalRender;
        Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, VideoView> entry = iter.next();
            //* String peerId = entry.getKey();
            VideoView render = entry.getValue();
            if (render.Fullscreen())
                return render;
        }
        return null;
    }


    /**
     * 缩小本地的像50%
     */
    public void lessenLocalRender() {

        int index, x, y, w, h;
        index = mLocalRender.index;
        mLocalRender.x = mLocalRender.x / 2 + SUB_DIS_X;
        mLocalRender.y = mLocalRender.y / 2 + SUB_DIS_Y;
        mLocalRender.w = mLocalRender.w / 2 - SUB_DIS_X;
        mLocalRender.h = mLocalRender.h / 2;
        mLocalRender.mLayout.setPosition(mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h);
        mLocalRender.mView.requestLayout();
    }


    public void propertyValuesHolder(View view) {

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                0, 1f);
        ObjectAnimator.ofPropertyValuesHolder(mLocalRender.mLayout, pvhX, pvhY, pvhZ).setDuration(3000).start();
    }

    /**
     * 缩放指定位置的像
     *
     * @param index
     */
    public void MaxIndexView(int index) {
        VideoView view1 = null;
        if (mLocalRender != null && mLocalRender.index == 1) {
            view1 = mLocalRender;
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (render.index == index) {
                    view1 = render;
                    break;
                }
            }
        }

        int x, y, w, h;
        index = view1.index;
        view1.x = view1.x / 2;
        view1.y = view1.y / 2;
        view1.w = view1.w * 2;
        view1.h = view1.h * 2;
        view1.mLayout.setPosition(view1.x, view1.y, view1.w, view1.h);

    }


    private void SwitchViewPosition(VideoView view1, VideoView view2) {
        AppRTCUtils.assertIsTrue(view1 != null && view2 != null);
        int index, x, y, w, h;
        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;
        view1.index = view2.index;
        view1.x = view2.x;
        view1.y = view2.y;
        view1.w = view2.w;
        view1.h = view2.h;

        view2.index = index;
        view2.x = x;
        view2.y = y;
        view2.w = w;
        view2.h = h;

        PercentFrameLayout layout_a = view1.mLayout;
        SurfaceViewRenderer view_a = view1.mView;
        PercentFrameLayout layout_b = view2.mLayout;
        SurfaceViewRenderer view_b = view2.mView;

        view1.updateVideoLayoutView(layout_b, view_b);
        view2.updateVideoLayoutView(layout_a, view_a);
    }

    /**
     * 进行大小像的切换；
     *
     * @param view1
     * @param fullscrnView
     */
    private void SwitchViewToFullscreen(VideoView view1, VideoView fullscrnView) {
        AppRTCUtils.assertIsTrue(view1 != null && fullscrnView != null);
        int index, x, y, w, h;
        index = view1.index;
        x = view1.x;
        y = view1.y;
        w = view1.w;
        h = view1.h;

        view1.index = fullscrnView.index;
        view1.x = fullscrnView.x;
        view1.y = fullscrnView.y;
        view1.w = fullscrnView.w;
        view1.h = fullscrnView.h;

        fullscrnView.index = index;
        fullscrnView.x = x;
        fullscrnView.y = y;
        fullscrnView.w = w;
        fullscrnView.h = h;

        //
        PercentFrameLayout layout_a = view1.mLayout;
        SurfaceViewRenderer view_a = view1.mView;

        PercentFrameLayout layout_b = fullscrnView.mLayout;
        SurfaceViewRenderer view_b = fullscrnView.mView;

        //进行像的交换；
        view1.updateVideoLayoutView(layout_b, view_b);
        fullscrnView.updateVideoLayoutView(layout_a, view_a);
    }

    public void BubbleSortSubView(VideoView view) {
        if (mLocalRender != null && view.index + 1 == mLocalRender.index) {
            SwitchViewPosition(mLocalRender, view);
        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                VideoView render = entry.getValue();
                if (view.index + 1 == render.index) {
                    SwitchViewPosition(render, view);
                    break;
                }
            }
        }
        if (view.index < mRemoteRenders.size()) {
            BubbleSortSubView(view);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender != null && mLocalRender.Hited(startX, startY)) {
                return true;
            } else {
                //判断是否点击在像的上
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        return true;
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int startX = (int) event.getX();
            int startY = (int) event.getY();
            if (mLocalRender != null && mLocalRender.Hited(startX, startY)) {
                //如果点击的是本地的像
                SwitchViewToFullscreen(mLocalRender, GetFullScreen());
                return true;
            } else {
                Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, VideoView> entry = iter.next();
                    String peerId = entry.getKey();
                    VideoView render = entry.getValue();
                    if (render.Hited(startX, startY)) {
                        //当前的像和
                        SwitchViewToFullscreen(render, GetFullScreen());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 切换像的位置
     *
     * @param startX
     * @param startY
     */
    public void switchLocaToRomoteScreen(int startX, int startY) {
        if (mLocalRender != null && mLocalRender.Hited(startX, startY)) {
            //如果点击的是本地的像
            SwitchViewToFullscreen(mLocalRender, GetFullScreen());

        } else {
            Iterator<Map.Entry<String, VideoView>> iter = mRemoteRenders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, VideoView> entry = iter.next();
                String peerId = entry.getKey();
                VideoView render = entry.getValue();
                if (render.Hited(startX, startY)) {
                    //当前的像和
                    SwitchViewToFullscreen(render, GetFullScreen());

                }
            }
        }
    }

    /**
     * 全屏
     */
    public void maxLocalRenderFullScreen() {
        int index, x, y, w, h;
        index = mLocalRender.index;
        mLocalRender.x = 0;
        mLocalRender.y = 0;
        mLocalRender.w = 100;
        mLocalRender.h = Loc_SUB_HEIGHT;
        mLocalRender.mLayout.setPosition(mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h);
        //mLocalRender.mView.requestLayout();
        PercentFrameLayout layout_a = mLocalRender.mLayout;
        SurfaceViewRenderer view_a = mLocalRender.mView;

        mLocalRender.updateVideoLayoutView(layout_a, view_a);

    }


    /**
     * Implements for AnyRTCViewEvents.
     */
    @Override
    public EglBase GetEglBase() {
        return mRootEglBase;
    }

    @Override
    public void OnRtcOpenRemoteRender(String peerId, VideoTrack remoteTrack) {
        VideoView remoteRender = mRemoteRenders.get(peerId);
        if (remoteRender == null) {
            int size = GetVideoRenderSize();
            if (size == 0) {
                remoteRender = new VideoView(peerId, mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100);
            } else {
                remoteRender = new VideoView(peerId, mVideoView.getContext(), mRootEglBase, size, (size - 1) * 33, Loc_SUB_HEIGHT, 33, 33);
                remoteRender.mView.setZOrderMediaOverlay(true);
            }

            mVideoView.addView(remoteRender.mLayout);

            remoteRender.mLayout.setPosition(remoteRender.x, remoteRender.y, remoteRender.w, remoteRender.h);
            remoteRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            remoteRender.mRenderer = new VideoRenderer(remoteRender.mView);

            remoteRender.mVideoTrack = remoteTrack;
            remoteRender.mVideoTrack.addRenderer(remoteRender.mRenderer);
            mRemoteRenders.put(peerId, remoteRender);
            if (mAutoLayout && mRemoteRenders.size() == 1 && mLocalRender != null) {
                SwitchViewToFullscreen(remoteRender, mLocalRender);
            }
            setScaleAnimation(remoteRender.mView);
            peopleChangeListener.OnPeopleNumChange(mRemoteRenders.size());
        }
    }

    @Override
    public void OnRtcRemoveRemoteRender(String peerId) {
        VideoView remoteRender = mRemoteRenders.get(peerId);
        if (remoteRender != null) {
            remoteRender.mVideoTrack = null;
            if (remoteRender.Fullscreen()) {
                SwitchIndex1ToFullscreen(remoteRender);
            }
            if (mRemoteRenders.size() > 1 && remoteRender.index < mRemoteRenders.size()) {
                BubbleSortSubView(remoteRender);
            }
            remoteRender.close();
            mVideoView.removeView(remoteRender.mLayout);
            mRemoteRenders.remove(peerId);
            remoteRender = null;
            peopleChangeListener.OnPeopleNumChange(mRemoteRenders.size());
        }
    }

    @Override
    public void OnRtcOpenLocalRender(VideoTrack localTrack) {
        int size = GetVideoRenderSize();
        if (size == 0) {
            mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, 0, 0, 0, 100, 100);
        } else {
            mLocalRender = new VideoView("localRender", mVideoView.getContext(), mRootEglBase, size, (100 - size * (SUB_WIDTH + SUB_X)), SUB_Y, SUB_WIDTH, SUB_HEIGHT);
            mLocalRender.mView.setZOrderMediaOverlay(true);
        }
        mLocalRender.mVideoTrack = localTrack;

        ImageView imageView = new ImageView(mVideoView.getContext());
        imageView.setImageResource(R.drawable.video_soundoff_normal);

        RelativeLayout.LayoutParams layoutParamsVoice = new RelativeLayout.LayoutParams(100, 100);
        layoutParamsVoice.leftMargin = ScreenUtils.getScreenWidth(mVideoView.getContext()) - 100;
        layoutParamsVoice.topMargin = 50;
        imageView.setLayoutParams(layoutParamsVoice);

        mVideoView.addView(mLocalRender.mLayout);

        //设置像的位置
        mLocalRender.mLayout.setPosition(mLocalRender.x, mLocalRender.y, mLocalRender.w, mLocalRender.h);
        mLocalRender.mView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        mLocalRender.mRenderer = new VideoRenderer(mLocalRender.mView);
        mLocalRender.mVideoTrack.addRenderer(mLocalRender.mRenderer);
        mVideoView.addView(imageView, layoutParamsVoice);


    }

    @Override
    public void OnRtcRemoveLocalRender() {
        if (mLocalRender != null) {
            mLocalRender.mVideoTrack = null;
            mLocalRender.mRenderer = null;

            mVideoView.removeView(mLocalRender.mLayout);
            mLocalRender = null;
        }
    }

    @Override
    public void OnRtcRemoteAVStatus(String peerId, boolean audioEnable, boolean videoEnable) {

    }
}

package com.wapchief.livertmpandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wapchief.livertmpandroid.views.like.TCHeartLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * @author wapchief
 * @date 2018/3/23
 * 推流
 */

public class RtmpPushActivity extends AppCompatActivity implements ITXLivePushListener{

    @BindView(R.id.video_view)
    TXCloudVideoView mVideoView;
    @BindView(R.id.iv_head_icon)
    ImageView mIvHeadIcon;
    @BindView(R.id.tv_broadcasting_time)
    TextView mTvBroadcastingTime;
    @BindView(R.id.iv_record_ball)
    ImageView mIvRecordBall;
    @BindView(R.id.tv_host_name)
    TextView mTvHostName;
    @BindView(R.id.tv_member_counts)
    TextView mTvMemberCounts;
    @BindView(R.id.rv_user_avatar)
    RecyclerView mRvUserAvatar;
    @BindView(R.id.btn_message_input)
    Button mBtnMessageInput;
    @BindView(R.id.flash_btn)
    Button mFlashBtn;
    @BindView(R.id.switch_cam)
    Button mSwitchCam;
    @BindView(R.id.beauty_btn)
    Button mBeautyBtn;
    @BindView(R.id.view_margin_audio_ctrl)
    TextView mViewMarginAudioCtrl;
    @BindView(R.id.btn_audio_ctrl)
    Button mBtnAudioCtrl;
    @BindView(R.id.btn_log)
    Button mBtnLog;
    @BindView(R.id.btn_close)
    Button mBtnClose;
    @BindView(R.id.tool_bar)
    LinearLayout mToolBar;
    @BindView(R.id.btn_audio_effect)
    Button mBtnAudioEffect;
    @BindView(R.id.btn_audio_close)
    Button mBtnAudioClose;
    @BindView(R.id.audio_plugin)
    LinearLayout mAudioPlugin;
    @BindView(R.id.im_msg_listview)
    ListView mImMsgListview;
    @BindView(R.id.beauty_seekbar)
    SeekBar mBeautySeekbar;
    @BindView(R.id.whitening_seekbar)
    SeekBar mWhiteningSeekbar;
    @BindView(R.id.layoutFaceBeauty)
    LinearLayout mLayoutFaceBeauty;
    @BindView(R.id.heart_layout)
    TCHeartLayout mHeartLayout;
    @BindView(R.id.danmakuView)
    DanmakuView mDanmakuView;
    @BindView(R.id.rl_controllLayer)
    RelativeLayout mRlControllLayer;
    @BindView(R.id.netbusy_tv)
    TextView mNetbusyTv;
    @BindView(R.id.rl_publish_root)
    RelativeLayout mRlPublishRoot;


    String rtmpUrl = "rtmp://3891.livepush.myqcloud.com/live/3891_user_11853ca9_a062?bizid=3891&txSecret=6aa117019b2c2ef5789761f50b3effbd&txTime=5ABE0A75";

    boolean mVideoPublish;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_push);
        ButterKnife.bind(this);
        initView();
    }

    TXLivePusher mLivePusher;
    TXLivePushConfig mLivePushConfig;
    private void initView() {
        mLivePusher     = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoEncodeGop(5);
//        mLivePushConfig.setBeautyFilter(mBeautyLevel, mWhiteningLevel, mRuddyLevel);
        mLivePusher.setConfig(mLivePushConfig);
        startPusherRtmp();
        mVideoPublish = startPusherRtmp();
    }

    @OnClick({R.id.btn_audio_ctrl, R.id.btn_log, R.id.btn_close, R.id.btn_audio_effect, R.id.btn_audio_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_ctrl:
                break;
            case R.id.btn_log:
                break;
            case R.id.btn_close:
                break;
            case R.id.btn_audio_effect:
                break;
            case R.id.btn_audio_close:
                break;
                default:
                    break;
        }
    }


    private static final int VIDEO_SRC_CAMERA = 0;
    private static final int VIDEO_SRC_SCREEN = 1;
    private int              mVideoSrc = VIDEO_SRC_CAMERA;
    private boolean startPusherRtmp(){

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        //水印
        mLivePushConfig.setWatermark(bitmap, 0.02f, 0.05f, 0.2f);

        int customModeType = 0;

//        if (isActivityCanRotation()) {
//            onActivityRotation();
//        }
        mLivePushConfig.setCustomModeType(customModeType);
        mLivePusher.setPushListener(this);
        mLivePushConfig.setPauseImg(300,5);
        mLivePushConfig.setPauseImg(bitmap);
        mLivePushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
        if(mVideoSrc != VIDEO_SRC_SCREEN){
            mLivePushConfig.setFrontCamera(true);
            mLivePushConfig.setBeautyFilter(5, 3, 2);
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.startCameraPreview(mVideoView);
        }
        else{
            mLivePusher.setConfig(mLivePushConfig);
            mLivePusher.startScreenCapture();
        }

        Log.e("rtmpUrl:==========", rtmpUrl.trim().toString());
        mLivePusher.startPusher(rtmpUrl.trim());

//        enableQRCodeBtn(false);

//        mBtnPlay.setBackgroundResource(R.drawable.play_pause);

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mVideoView != null) {
            mVideoView.onResume();
        }

        if (mVideoPublish && mLivePusher != null && mVideoSrc == VIDEO_SRC_CAMERA) {
            mLivePusher.resumePusher();
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mVideoView != null) {
            mVideoView.onPause();
        }

        if (mVideoPublish && mLivePusher != null && mVideoSrc == VIDEO_SRC_CAMERA) {
            mLivePusher.pausePusher();
            mLivePusher.pauseBGM();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopPublishRtmp();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }

//        mRotationObserver.stopObserver();


    }

    @Override
    public void onPushEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}

package com.wapchief.livertmpandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wapchief.livertmpandroid.beautysetting.BeautyDialogFragment;
import com.wapchief.livertmpandroid.utils.BitmapUtils;
import com.wapchief.livertmpandroid.utils.TCUtils;
import com.wapchief.livertmpandroid.views.like.TCHeartLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * @author wapchief
 * @date 2018/3/23
 * 推流
 */

public class RtmpPushActivity extends AppCompatActivity implements ITXLivePushListener,BeautyDialogFragment.OnBeautyParamsChangeListener{

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
        checkPublishPermission();
        initView();
    }
    //推流控制、设置
    TXLivePusher mLivePusher;
    TXLivePushConfig mLivePushConfig;
    //美颜管理
    BeautyDialogFragment mBeautyDialogFragment;
    BeautyDialogFragment.BeautyParams mBeautyParams;
    private void initView() {
        mLivePusher     = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.setVideoEncodeGop(5);
//        mLivePushConfig.setBeautyFilter(mBeautyLevel, mWhiteningLevel, mRuddyLevel);
        mLivePusher.setConfig(mLivePushConfig);
        mVideoPublish = startPusherRtmp();
        //初始化美颜
        mBeautyDialogFragment = new BeautyDialogFragment();
        mBeautyParams = new BeautyDialogFragment.BeautyParams();
        //美颜选择监听
        mBeautyDialogFragment.setBeautyParamsListner(mBeautyParams, this);
    }

    //麦克风开关
    private static boolean MIC_STATE = true;
    private static boolean mFlashOn = false;
    @OnClick({R.id.btn_audio_ctrl, R.id.btn_log, R.id.btn_close,
            R.id.btn_audio_effect, R.id.btn_audio_close,R.id.beauty_btn,
            R.id.switch_cam,R.id.flash_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_ctrl:
                //麦克风开关
                mLivePusher.setMute(MIC_STATE);
                if (MIC_STATE) {
                    mBtnAudioCtrl.setBackgroundResource(R.drawable.mic_disable);
                    MIC_STATE = false;
                } else {
                    mBtnAudioCtrl.setBackgroundResource(R.drawable.mic_normal);
                    MIC_STATE = true;
                }
                break;
            case R.id.btn_log:

                break;
            case R.id.btn_close:
                //关闭推流
                stopPublishRtmp();
                finish();
                break;
            case R.id.btn_audio_effect:
                break;
            case R.id.btn_audio_close:
                break;
            case R.id.beauty_btn:
                //美颜
                if (mBeautyDialogFragment.isAdded()) {

                    mBeautyDialogFragment.dismiss();
                } else {
                    mBeautyDialogFragment.show(getFragmentManager(), "");
                }
                break;
            case R.id.switch_cam:
                //摄像头切换
                mLivePusher.switchCamera();
                break;
            case R.id.flash_btn:
                //闪光灯
                if (!mLivePusher.turnOnFlashLight(!mFlashOn)) {
                    Toast.makeText(getApplicationContext(), "打开闪光灯失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFlashOn = !mFlashOn;
                mFlashBtn.setBackgroundDrawable(mFlashOn ?
                        getResources().getDrawable(R.drawable.icon_flash_pressed) :
                        getResources().getDrawable(R.drawable.icon_flash));

                break;
                default:
                    break;
        }
    }


    private static final int VIDEO_SRC_CAMERA = 0;
    private static final int VIDEO_SRC_SCREEN = 1;
    private int              mVideoSrc = VIDEO_SRC_CAMERA;

    /**启动直播*/
    private boolean startPusherRtmp(){

        //开启相机
        if(mVideoSrc != VIDEO_SRC_SCREEN){
            mVideoView.setVisibility(View.VISIBLE);
        }
        Bitmap bitmap = BitmapUtils.decodeResource(getResources(),R.mipmap.ic_launcher);
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

    /**直播所需权限*/
    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPublishRtmp();
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }

//        mRotationObserver.stopObserver();


    }

    /**开始直播的回调*/
    @Override
    public void onPushEvent(int event, Bundle param) {
        String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);
        String pushEventLog = "receive event: " + event + ", " + msg;
        Log.d(TAG, pushEventLog);
//        if (mLivePusher != null) {
//            mLivePusher.onLogRecord("[event:" + event + "]" + msg + "\n");
//        }
        //错误还是要明确的报一下
        if (event < 0) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            if(event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL || event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL){
                stopPublishRtmp();
            }
        }

        if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {
            stopPublishRtmp();
        }
        else if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            mLivePushConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
//            mBtnHWEncode.setBackgroundResource(R.drawable.quick2);
            mLivePusher.setConfig(mLivePushConfig);
//            mHWVideoEncode = false;
        }
        else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_UNSURPORT) {
            stopPublishRtmp();
        }
        else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_START_FAILED) {
            stopPublishRtmp();
        } else if (event == TXLiveConstants.PUSH_EVT_CHANGE_RESOLUTION) {
            Log.d(TAG, "change resolution to " + param.getInt(TXLiveConstants.EVT_PARAM2) + ", bitrate to" + param.getInt(TXLiveConstants.EVT_PARAM1));
        } else if (event == TXLiveConstants.PUSH_EVT_CHANGE_BITRATE) {
            Log.d(TAG, "change bitrate to" + param.getInt(TXLiveConstants.EVT_PARAM1));
        } else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
//            ++mNetBusyCount;
//            Log.d(TAG, "net busy. count=" + mNetBusyCount);
//            showNetBusyTips();
        } else if (event == TXLiveConstants.PUSH_EVT_START_VIDEO_ENCODER) {
            int encType = param.getInt(TXLiveConstants.EVT_PARAM1);
//            mHWVideoEncode = (encType == 1);
//            mBtnHWEncode.getBackground().setAlpha(mHWVideoEncode ? 255 : 100);
        }
    }

    /**停止推流*/
    private void stopPublishRtmp() {
        mVideoPublish = false;
        mLivePusher.stopBGM();
        mLivePusher.stopCameraPreview(true);
        mLivePusher.stopScreenCapture();
        mLivePusher.setPushListener(null);
        mLivePusher.stopPusher();
        mVideoView.setVisibility(View.GONE);

//        if(mBtnHWEncode != null) {
//            //mHWVideoEncode = true;
//            mLivePushConfig.setHardwareAcceleration(mHWVideoEncode ? TXLiveConstants.ENCODE_VIDEO_HARDWARE : TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
//            mBtnHWEncode.setBackgroundResource(R.drawable.quick);
//            mBtnHWEncode.getBackground().setAlpha(mHWVideoEncode ? 255 : 100);
//        }
//
//        enableQRCodeBtn(true);
//        mBtnPlay.setBackgroundResource(R.drawable.play_start);

        if(mLivePushConfig != null) {
            mLivePushConfig.setPauseImg(null);
        }
    }

    private static final String TAG = "livertmp-Activity";
    @Override
    public void onNetStatus(Bundle bundle) {
        String str = getNetStatusString(bundle);
        Log.e(TAG, "Current status, CPU:"+bundle.getString(TXLiveConstants.NET_STATUS_CPU_USAGE)+
                ", RES:"+bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)+
                ", SPD:"+bundle.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps"+
                ", FPS:"+bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS)+
                ", ARA:"+bundle.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps"+
                ", VRA:"+bundle.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps");
//        if (mLivePusher != null){
//            mLivePusher.onLogRecord("[net state]:\n"+str+"\n");
//        }

    }


    //公用打印辅助函数
    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-8s %-8s %-8s %-8s\n%-14s %-14s %-12s\n%-14s %-14s",
                "CPU:"+status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH)+"*"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:"+status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED)+"Kbps",
                "JIT:"+status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "GOP:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_GOP)+"s",
                "ARA:"+status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE)+"Kbps",
                "QUE:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE)+"|"+status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE),
                "DRP:"+status.getInt(TXLiveConstants.NET_STATUS_CODEC_DROP_CNT)+"|"+status.getInt(TXLiveConstants.NET_STATUS_DROP_SIZE),
                "VRA:"+status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE)+"Kbps",
                "SVR:"+status.getString(TXLiveConstants.NET_STATUS_SERVER_IP),
                "AUDIO:"+status.getString(TXLiveConstants.NET_STATUS_AUDIO_INFO));
        return str;
    }


    /**美颜回调*/
    @Override
    public void onBeautyParamsChange(BeautyDialogFragment.BeautyParams params, int key) {
        switch (key){
            case BeautyDialogFragment.BEAUTYPARAM_BEAUTY:
            case BeautyDialogFragment.BEAUTYPARAM_WHITE:
                if (mLivePusher != null) {
                    mLivePusher.setBeautyFilter(params.mBeautyStyle, params.mBeautyProgress, params.mWhiteProgress, params.mRuddyProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FACE_LIFT:
                if (mLivePusher != null) {
                    mLivePusher.setFaceSlimLevel(params.mFaceLiftProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_BIG_EYE:
                if (mLivePusher != null) {
                    mLivePusher.setEyeScaleLevel(params.mBigEyeProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FILTER:
                if (mLivePusher != null) {
                    mLivePusher.setFilter(TCUtils.getFilterBitmap(getResources(), params.mFilterIdx));
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_MOTION_TMPL:
                if (mLivePusher != null){
                    mLivePusher.setMotionTmpl(params.mMotionTmplPath);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_GREEN:
                if (mLivePusher != null){
                    mLivePusher.setGreenScreenFile(TCUtils.getGreenFileName(params.mGreenIdx));
                }
                break;
            default:
                break;
        }
    }
}

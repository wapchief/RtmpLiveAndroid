package com.wapchief.livertmpandroid;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tencent.avroom.TXCAVRoom;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wapchief.livertmpandroid.utils.TCFrequeControl;
import com.wapchief.livertmpandroid.views.TCInputTextMsgDialog;
import com.wapchief.livertmpandroid.views.like.TCHeartLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * @author wapchief
 * 拉流
 */
public class MainActivity extends AppCompatActivity implements TCInputTextMsgDialog.OnTextSendListener{

//    public final static String URL_RTMP = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    public final static String URL_RTMP = "rtmp://3891.liveplay.myqcloud.com/live/3891_user_11853ca9_a062";


    @BindView(R.id.video_view)
    TXCloudVideoView mVideoView;
    @BindView(R.id.danmaku)
    DanmakuView mDanmaku;
    @BindView(R.id.switch_bt)
    SwitchCompat mSwitchBt;
    @BindView(R.id.bt_orientation)
    TextView mBtOrientation;
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
    ImageView mBtnMessageInput;
    @BindView(R.id.btn_switch_cam)
    Button mBtnSwitchCam;
    @BindView(R.id.btn_linkmic)
    Button mBtnLinkmic;
    @BindView(R.id.btn_share)
    ImageView mBtnShare;
    @BindView(R.id.btn_log)
    ImageView mBtnLog;
    @BindView(R.id.btn_record)
    ImageView mBtnRecord;
    @BindView(R.id.btn_like)
    ImageView mBtnLike;
    @BindView(R.id.btn_back)
    Button mBtnBack;
    @BindView(R.id.tool_bar)
    RelativeLayout mToolBar;
    @BindView(R.id.im_msg_listview)
    ListView mImMsgListview;
    @BindView(R.id.record_progress)
    ProgressBar mRecordProgress;
    @BindView(R.id.close_record)
    ImageView mCloseRecord;
    @BindView(R.id.record)
    ImageView mRecord;
    @BindView(R.id.retry_record)
    ImageView mRetryRecord;
    @BindView(R.id.record_layout)
    RelativeLayout mRecordLayout;
    @BindView(R.id.heart_layout)
    TCHeartLayout mHeartLayout;
    @BindView(R.id.danmakuView)
    DanmakuView mDanmakuView;
    @BindView(R.id.play_btn)
    ImageView mPlayBtn;
    @BindView(R.id.btn_vod_share)
    ImageView mBtnVodShare;
    @BindView(R.id.btn_vod_log)
    ImageView mBtnVodLog;
    @BindView(R.id.btn_vod_back)
    ImageView mBtnVodBack;
    @BindView(R.id.progress_time)
    TextView mProgressTime;
    @BindView(R.id.seekbar)
    SeekBar mSeekbar;
    @BindView(R.id.rl_controllLayer)
    RelativeLayout mRlControllLayer;
    private TXCAVRoom mAVRoom;
    //点赞频率控制
    private TCFrequeControl mLikeFrequeControl;
    /**
     * 播放控制
     */
    private TXLivePlayer mLivePlayer = null;
    /**
     * 弹幕
     */
    DanmakuController mDanmakuController;
    /**
     * inputDialog
     */
    TCInputTextMsgDialog mInputTextMsgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        String sdkver = TXLiveBase.getSDKVersionStr();
        Log.e("MainActivity", "liteav sdk version is : " + sdkver);
        mTvHostName.setText("用户名");
        mBtnLog.setVisibility(View.GONE);
        mBtnShare.setVisibility(View.GONE);
        mBtnRecord.setVisibility(View.GONE);
        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);
        initRtmpPlayer();
        initDanmaKu();
    }

    /**
     * 初始化弹幕
     */
    private void initDanmaKu() {
        mDanmakuController = new DanmakuController(mDanmakuView, this);
        mDanmakuController.initDanmaKu();
        //默认不显示
        mDanmakuView.hide();
        //总开关
        mSwitchBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDanmakuView.show();
                } else {
                    mDanmakuView.hide();
                }
            }
        });
    }

    /**
     * 初始化播放
     */
    private void initRtmpPlayer() {

        //创建 player 对象
        mLivePlayer = new TXLivePlayer(this);
        //关键 player 对象与界面 view
        mLivePlayer.setPlayerView(mVideoView);
        mLivePlayer.startPlay(URL_RTMP, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
//        ScreenOrientation();
    }


    /**
     * 屏幕方向
     */
    LinearLayout.LayoutParams mLayoutParams;
    int mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;

    private void ScreenOrientation() {
//        mViewScreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCurrentRenderRotation == TXLiveConstants.RENDER_ROTATION_PORTRAIT) {
//                    mViewScreen.setText("小窗");
////                    mLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
////                    mVideoRoot.setLayoutParams(mLayoutParams);
//                    mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_LANDSCAPE;
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                } else if (mCurrentRenderRotation == TXLiveConstants.RENDER_ROTATION_LANDSCAPE) {
//                    mViewScreen.setText("全屏");
//                    mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;
////                    mLayoutParams.height = UIUtils.dp2px(200);
////                    mVideoRoot.setLayoutParams(mLayoutParams);
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                }
////                mLivePlayer.setRenderRotation(mCurrentRenderRotation);
//            }
//        });

    }


    /**发送消息弹出框*/
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();
        lp.width = (display.getWidth());
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }


    @OnClick({R.id.btn_log, R.id.btn_record, R.id.btn_like,
            R.id.btn_back, R.id.play_btn, R.id.btn_vod_share,
            R.id.btn_vod_log, R.id.btn_vod_back,R.id.btn_message_input})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_log:
                break;
            case R.id.btn_record:
                break;
            case R.id.btn_like:
                /**点赞*/
                if (mHeartLayout != null) {
                    mHeartLayout.setVisibility(View.VISIBLE);
                    mHeartLayout.addFavor();
                }
                //点赞发送请求限制
                if (mLikeFrequeControl == null) {
                    mLikeFrequeControl = new TCFrequeControl();
                    mLikeFrequeControl.init(2, 1);
                }
                break;
            case R.id.btn_back:
                break;
            case R.id.play_btn:
                break;
            case R.id.btn_vod_share:
                break;
            case R.id.btn_vod_log:
                break;
            case R.id.btn_vod_back:
                break;
            case R.id.btn_message_input:
                //发送一条弹幕
                showInputMsgDialog();
                break;
                default:
                    break;
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDanmakuView.getConfig().setDanmakuMargin(20);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mDanmakuView.getConfig().setDanmakuMargin(40);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }

    }

    /**发送弹幕后的回调*/
    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        if (msg.length() == 0) {

            return;
        }
        //关联弹幕总开关
        if (tanmuOpen) {
            mDanmakuView.show();
            mSwitchBt.setChecked(true);
        }
        //消息显示
        mDanmakuController.addDanmaku(tanmuOpen,msg);
    }
}

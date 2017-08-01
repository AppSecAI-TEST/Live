package com.tonglu.live.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tonglu.live.R;
import com.tonglu.live.adapter.DanmuAdapter;
import com.tonglu.live.adapter.DanmuEntity;
import com.tonglu.live.utils.ToastUtils;
import com.tonglu.live.xdanmu.DanmuContainerView;
import com.tonglu.okhttp.utils.OkLogger;

import java.util.Random;

public class LivePlayerActivity extends Activity implements ITXLivePlayListener, View.OnClickListener {

    private TXLivePlayer mLivePlayer = null;
    private boolean mVideoPlay;
    private TXCloudVideoView mPlayerView;
    private ImageView mLoadingView;
    private boolean mHWDecode = false;
    private LinearLayout mRootView;

    //private ImageView mBtnLog;
    private ImageView mBtnPlay;
    //private ScrollView mScrollView;

    private static final int CACHE_STRATEGY_FAST = 1;  //极速
    private static final int CACHE_STRATEGY_SMOOTH = 2;  //流畅
    private static final int CACHE_STRATEGY_AUTO = 3;  //自动

    private static final float CACHE_TIME_FAST = 1.0f;
    private static final float CACHE_TIME_SMOOTH = 5.0f;

    private static final float CACHE_TIME_AUTO_MIN = 5.0f;
    private static final float CACHE_TIME_AUTO_MAX = 10.0f;

    //public static final int ACTIVITY_TYPE_PUBLISH = 1;
    public static final int ACTIVITY_TYPE_LIVE_PLAY = 2;
    //public static final int ACTIVITY_TYPE_VOD_PLAY = 3;
    //public static final int ACTIVITY_TYPE_LINK_MIC = 4;

    private int mCacheStrategy = 0;
    private ImageView mBtnCacheStrategy;
    private Button mRatioFast;
    private Button mRatioSmooth;
    private Button mRatioAuto;
    private ImageView mBtnStop;
    private LinearLayout mLayoutCacheStrategy;

    //public TextView mLogViewStatus;
    //public TextView mLogViewEvent;
    protected StringBuffer mLogMsg = new StringBuffer("");
    private final int mLogMsgLenLimit = 3000;

    private int mCurrentRenderMode;
    private int mCurrentRenderRotation;

    private boolean mVideoPause = false;
    private int mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private TXLivePlayConfig mPlayConfig;
    private long mStartPlayTS = 0;
    protected int mActivityType;

    protected String mUrlAddress;//直播地址

    DanmuContainerView danmuContainerView;
    public String SEED[] = {"来自武汉的订单", "来自上海的订单", "，来自北京的订单 ~~ 。6不6，", "~~，来自杭州的订单", " 大家好才是真的好"};
    Random random;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.activity_play);

        handleIntent();

        //setRenderMode：铺满or适应
        //RENDER_MODE_FULL_FILL_SCREEN	将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
        //RENDER_MODE_ADJUST_RESOLUTION	将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        mCurrentRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;

        //setRenderRotation：画面旋转
        //RENDER_ROTATION_PORTRAIT	正常播放（Home键在画面正下方）
        //RENDER_ROTATION_LANDSCAPE	画面顺时针旋转270度（Home键在画面正左方）
        mCurrentRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;

        //播放类型
        mActivityType = getIntent().getIntExtra("PLAY_TYPE", ACTIVITY_TYPE_LIVE_PLAY);
        mPlayConfig = new TXLivePlayConfig();
        initView();
    }

    private void handleIntent() {
        final Intent intent = getIntent();
        if (intent.hasExtra("url_address")) {
            mUrlAddress = intent.getStringExtra("url_address");
        } else {
            OkLogger.e("No extras.");
        }
    }


    void initView() {
        //mLogViewEvent = (TextView) findViewById(R.id.logViewEvent);
        //mLogViewStatus = (TextView) findViewById(R.id.logViewStatus);

//------------------------------------------------------------------------------
        random = new Random();
        danmuContainerView = (DanmuContainerView) findViewById(R.id.danmuContainerView);

        DanmuAdapter danmuAdapter = new DanmuAdapter(this);
        danmuContainerView.setAdapter(danmuAdapter);

        danmuContainerView.setSpeed(DanmuContainerView.LOW_SPEED);
        danmuContainerView.setGravity(DanmuContainerView.GRAVITY_TOP);

        handler.postDelayed(runnable, TIME); //每隔1s执行一条弹屏信息
//------------------------------------------------------------------------------

        mRootView = (LinearLayout) findViewById(R.id.root);
        if (mLivePlayer == null) {
            mLivePlayer = new TXLivePlayer(this);
        }

        mPlayerView = (TXCloudVideoView) findViewById(R.id.video_view);
        mPlayerView.disableLog(true);
        mLoadingView = (ImageView) findViewById(R.id.loadingImageView);

        mVideoPlay = false; //默认设置为False
        //mLogViewStatus.setVisibility(View.GONE);
        //mLogViewStatus.setMovementMethod(new ScrollingMovementMethod());
        //mLogViewEvent.setMovementMethod(new ScrollingMovementMethod());
        //mScrollView = (ScrollView) findViewById(R.id.scrollview);
        //mScrollView.setVisibility(View.GONE);

        //点击播放
        mBtnPlay = (ImageView) findViewById(R.id.btnPlay);
        mBtnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OkLogger.e("click playbtn isplay:" + mVideoPlay + " ispause:" + mVideoPause + " playtype:" + mPlayType);
                toPlay();
            }
        });

        toPlay();//播放


        // 为ViewFlipper添加广告条
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.marquee_view);
        View v = View.inflate(this, R.layout.item_super_danmu, null);
        vf.addView(v);
            /*TextView tv1 = (TextView) v.findViewById(R.id.tv_auto_new1);
            tv1.setText("asdfajk阿卡丽手机卡咖啡---"+i);*/



        //停止按钮(不保留最后一帧，直接停止播放)
        mBtnStop = (ImageView) findViewById(R.id.btnStop);
        mBtnStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayRtmp();
                mVideoPlay = false;
                mVideoPause = false;
            }
        });

        //日志Log
        /*mBtnLog = (ImageView) findViewById(R.id.btnLog);
        mBtnLog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLogViewStatus.getVisibility() == View.GONE) {
                    mLogViewStatus.setVisibility(View.VISIBLE);
                    //mScrollView.setVisibility(View.VISIBLE);
                    //mLogViewEvent.setText(mLogMsg);
                    //scroll2Bottom(mScrollView, mLogViewEvent);
                    mBtnLog.setImageResource(R.mipmap.log_hidden);
                } else {
                    mLogViewStatus.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.GONE);
                    mBtnLog.setImageResource(R.mipmap.log_show);
                }
            }
        });*/


        //缓存策略
        mBtnCacheStrategy = (ImageView) findViewById(R.id.btnCacheStrategy);
        mLayoutCacheStrategy = (LinearLayout) findViewById(R.id.layoutCacheStrategy);
        mBtnCacheStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutCacheStrategy.setVisibility(mLayoutCacheStrategy.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        this.setCacheStrategy(CACHE_STRATEGY_AUTO);

        mRatioFast = (Button) findViewById(R.id.radio_btn_fast);
        mRatioFast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LivePlayerActivity.this.setCacheStrategy(CACHE_STRATEGY_FAST);
                mLayoutCacheStrategy.setVisibility(View.GONE);
            }
        });

        mRatioSmooth = (Button) findViewById(R.id.radio_btn_smooth);
        mRatioSmooth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LivePlayerActivity.this.setCacheStrategy(CACHE_STRATEGY_SMOOTH);
                mLayoutCacheStrategy.setVisibility(View.GONE);
            }
        });

        mRatioAuto = (Button) findViewById(R.id.radio_btn_auto);
        mRatioAuto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LivePlayerActivity.this.setCacheStrategy(CACHE_STRATEGY_AUTO);
                mLayoutCacheStrategy.setVisibility(View.GONE);
            }
        });

        View view = mPlayerView.getRootView();
        view.setOnClickListener(this);
    }

    private void toPlay() {
        if (mVideoPlay) {
            if (mVideoPause) {
                mLivePlayer.resume();
                mBtnPlay.setImageResource(R.mipmap.play_pause);
                mRootView.setBackgroundColor(0xff000000);
            } else {
                mLivePlayer.pause();
                mBtnPlay.setImageResource(R.mipmap.play_start);
            }
            mVideoPause = !mVideoPause;

        } else {
            if (startPlayRtmp()) {
                mVideoPlay = !mVideoPlay;
            }
        }
    }

    private int TIME = 1000;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler.postDelayed(this, TIME);
                DanmuEntity danmuEntity = new DanmuEntity();
                danmuEntity.setContent(SEED[random.nextInt(5)]);
                danmuEntity.setType(0);
                danmuEntity.setTime("23:20:11");
                danmuContainerView.addDanmu(danmuEntity);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

//------------------------------------------------------------------------------

    /*public static void scroll2Bottom(final ScrollView scroll, final View inner) {
        if (scroll == null || inner == null) {
            return;
        }
        int offset = inner.getMeasuredHeight() - scroll.getMeasuredHeight();
        if (offset < 0) {
            offset = 0;
        }
        scroll.scrollTo(0, offset);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
            mLivePlayer = null;
        }
        if (mPlayerView != null) {
            mPlayerView.onDestroy();
            mPlayerView = null;
        }
        mPlayConfig = null;

        OkLogger.e("----------------->vrender onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPlayType == TXLivePlayer.PLAY_TYPE_VOD_FLV || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_HLS || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_MP4 || mPlayType == TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO) {
            if (mLivePlayer != null) {
                mLivePlayer.pause();
            }
        } else {
            //stopPlayRtmp();
        }

        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mVideoPlay && !mVideoPause) {
            if (mLivePlayer != null) {
                mLivePlayer.resume();
            }
        }

        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                mLayoutCacheStrategy.setVisibility(View.GONE);
        }
    }

    private boolean checkPlayUrl(final String playUrl) {
        if (TextUtils.isEmpty(playUrl) || (!playUrl.startsWith("http://") && !playUrl.startsWith("https://") && !playUrl.startsWith("rtmp://") && !playUrl.startsWith("/"))) {
            ToastUtils.showLongToastSafe("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式和本地播放方式（绝对路径，如\"/sdcard/test.mp4\"）!");
            return false;
        }

        switch (mActivityType) {
            case ACTIVITY_TYPE_LIVE_PLAY: {
                if (playUrl.startsWith("rtmp://")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
                } else if ((playUrl.startsWith("http://") || playUrl.startsWith("https://")) && playUrl.contains(".flv")) {
                    mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
                } else {
                    ToastUtils.showLongToastSafe("播放地址不合法，直播目前仅支持rtmp,flv播放方式!");
                    return false;
                }
            }
            break;

            default:
                ToastUtils.showLongToastSafe("播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!");
                return false;
        }
        return true;
    }

    /*protected void clearLog() {
        mLogMsg.setLength(0);
        //mLogViewEvent.setText("");
        //mLogViewStatus.setText("");
    }*/

    /*protected void appendEventLog(int event, String message) {
        String str = "receive event: " + event + ", " + message;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        String date = sdf.format(System.currentTimeMillis());
        while (mLogMsg.length() > mLogMsgLenLimit) {
            int idx = mLogMsg.indexOf("\n");
            if (idx == 0)
                idx = 1;
            mLogMsg = mLogMsg.delete(0, idx);
        }
        mLogMsg = mLogMsg.append("\n" + "[" + date + "]" + message);
    }*/

    private boolean startPlayRtmp() {
//          由于iOS AppStore要求新上架的app必须使用https,所以后续腾讯云的视频连接会支持https,
//          但https会有一定的性能损耗,所以android将统一替换会http
//        if (playUrl.startsWith("https://")) {
//            playUrl = "http://" + playUrl.substring(8);
//        }

        if (!checkPlayUrl(mUrlAddress)) {
            return false;
        }

        //clearLog();

        int[] ver = TXLivePlayer.getSDKVersion();
        if (ver != null && ver.length >= 4) {
            mLogMsg.append(String.format("rtmp sdk version:%d.%d.%d.%d ", ver[0], ver[1], ver[2], ver[3]));
            //mLogViewEvent.setText(mLogMsg);
        }
        mBtnPlay.setImageResource(R.mipmap.play_pause);
        mRootView.setBackgroundColor(0xff000000);

        mLivePlayer.setPlayerView(mPlayerView);
        mLivePlayer.setPlayListener(this);

        // 硬件加速在1080p解码场景下效果显著，但细节之处并不如想象的那么美好：
        // (1) 只有 4.3 以上android系统才支持
        // (2) 兼容性我们目前还仅过了小米华为等常见机型，故这里的返回值您先不要太当真
        mLivePlayer.enableHardwareDecode(mHWDecode);
        mLivePlayer.setRenderRotation(mCurrentRenderRotation);
        mLivePlayer.setRenderMode(mCurrentRenderMode);
        //设置播放器缓存策略
        //这里将播放器的策略设置为自动调整，调整的范围设定为1到4s，您也可以通过setCacheTime将播放器策略设置为采用
        //固定缓存时间。如果您什么都不调用，播放器将采用默认的策略（默认策略为自动调整，调整范围为1到4s）
        //mLivePlayer.setCacheTime(5);
        mLivePlayer.setConfig(mPlayConfig);

        int result = mLivePlayer.startPlay(mUrlAddress, mPlayType); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;

        if (result != 0) {
            mBtnPlay.setImageResource(R.mipmap.play_start);
            mRootView.setBackgroundResource(R.mipmap.main_bkg);
            return false;
        }

        //appendEventLog(0, "点击播放按钮！播放类型：" + mPlayType);

        startLoadingAnimation();

        mStartPlayTS = System.currentTimeMillis();
        return true;
    }

    private void stopPlayRtmp() {

        mBtnPlay.setImageResource(R.mipmap.play_start);
        mRootView.setBackgroundResource(R.mipmap.main_bkg);
        stopLoadingAnimation();
        if (mLivePlayer != null) {
            mLivePlayer.setPlayListener(null);
            mLivePlayer.stopPlay(true);
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            stopLoadingAnimation();
            Log.d("AutoMonitor", "PlayFirstRender,cost=" + (System.currentTimeMillis() - mStartPlayTS));
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            stopPlayRtmp();
            mVideoPlay = false;
            mVideoPause = false;

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_LOADING) {
            startLoadingAnimation();
        }

        String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);
       /* appendEventLog(event, msg);
        if (mScrollView.getVisibility() == View.VISIBLE) {
            mLogViewEvent.setText(mLogMsg);
            scroll2Bottom(mScrollView, mLogViewEvent);
        }*/
//        if(mLivePlayer != null){
//            mLivePlayer.onLogRecord("[event:"+event+"]"+msg+"\n");
//        }
        if (event < 0) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            stopLoadingAnimation();
        }
    }

    //公用打印辅助函数
    protected String getNetStatusString(Bundle status) {
        String str = String.format("%-14s %-14s %-12s\n%-14s %-14s %-12s\n%-14s %-14s %-12s\n%-14s %-12s",
                "CPU:" + status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE),
                "RES:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) + "*" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT),
                "SPD:" + status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED) + "Kbps",
                "JIT:" + status.getInt(TXLiveConstants.NET_STATUS_NET_JITTER),
                "FPS:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS),
                "ARA:" + status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE) + "Kbps",
                "QUE:" + status.getInt(TXLiveConstants.NET_STATUS_CODEC_CACHE) + "|" + status.getInt(TXLiveConstants.NET_STATUS_CACHE_SIZE),
                "DRP:" + status.getInt(TXLiveConstants.NET_STATUS_CODEC_DROP_CNT) + "|" + status.getInt(TXLiveConstants.NET_STATUS_DROP_SIZE),
                "VRA:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE) + "Kbps",
                "SVR:" + status.getString(TXLiveConstants.NET_STATUS_SERVER_IP),
                "AVRA:" + status.getInt(TXLiveConstants.NET_STATUS_SET_VIDEO_BITRATE));
        return str;
    }

    @Override
    public void onNetStatus(Bundle status) {
        String str = getNetStatusString(status);
        //mLogViewStatus.setText(str);
        OkLogger.e("Current status, CPU:" + status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE) +
                ", RES:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) + "*" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT) +
                ", SPD:" + status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED) + "Kbps" +
                ", FPS:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS) +
                ", ARA:" + status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE) + "Kbps" +
                ", VRA:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE) + "Kbps");
        //Log.d(TAG, "Current status: " + status.toString());
//        if (mLivePlayer != null){
//            mLivePlayer.onLogRecord("[net state]:\n"+str+"\n");
//        }
    }

    public void setCacheStrategy(int nCacheStrategy) {
        if (mCacheStrategy == nCacheStrategy) return;
        mCacheStrategy = nCacheStrategy;

        switch (nCacheStrategy) {
            case CACHE_STRATEGY_FAST:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_FAST);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_SMOOTH:
                mPlayConfig.setAutoAdjustCacheTime(false);
                mPlayConfig.setCacheTime(CACHE_TIME_SMOOTH);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            case CACHE_STRATEGY_AUTO:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;

            default:
                break;
        }
    }

    private void startLoadingAnimation() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
            ((AnimationDrawable) mLoadingView.getDrawable()).start();
        }
    }

    private void stopLoadingAnimation() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
            ((AnimationDrawable) mLoadingView.getDrawable()).stop();
        }
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 100 || data == null || data.getExtras() == null || TextUtils.isEmpty(data.getExtras().getString("result"))) {
            return;
        }
        String result = data.getExtras().getString("result");
        if (mRtmpUrlView != null) {
            mRtmpUrlView.setText(result);
        }
    }*/
}
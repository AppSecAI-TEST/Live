package com.tonglu.live.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tonglu.live.R;
import com.tonglu.live.callback.JsonCallback;
import com.tonglu.live.danmu.Danmu;
import com.tonglu.live.danmu.DanmuControl;
import com.tonglu.live.manager.CommonBody;
import com.tonglu.live.manager.GenericRequestManager;
import com.tonglu.live.manager.URL;
import com.tonglu.live.model.CheckInfo;
import com.tonglu.live.model.RollListInfo;
import com.tonglu.live.utils.GsonConvertUtil;
import com.tonglu.live.utils.MD5Utils;
import com.tonglu.live.utils.ToastUtils;
import com.tonglu.okhttp.OkHttpUtil;
import com.tonglu.okhttp.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import master.flame.danmaku.controller.IDanmakuView;

public class LivePlayerActivity extends Activity implements ITXLivePlayListener {

    private TXLivePlayer mLivePlayer = null;
    private boolean mVideoPlay;
    private TXCloudVideoView mPlayerView;
    private ImageView mLoadingView;
    private boolean mHWDecode = false;
    private LinearLayout mRootView;

    private static final int CACHE_STRATEGY_AUTO = 3;  //自动

    private static final float CACHE_TIME_FAST = 1.0f;
    private static final float CACHE_TIME_SMOOTH = 5.0f;

    //public static final int ACTIVITY_TYPE_PUBLISH = 1;
    public static final int ACTIVITY_TYPE_LIVE_PLAY = 2;
    //public static final int ACTIVITY_TYPE_VOD_PLAY = 3;
    //public static final int ACTIVITY_TYPE_LINK_MIC = 4;

    private int mCacheStrategy = 0;

    private int mCurrentRenderMode;
    private int mCurrentRenderRotation;

    private boolean mVideoPause = false;
    private int mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private TXLivePlayConfig mPlayConfig;
    private long mStartPlayTS = 0;
    protected int mActivityType;

    protected String mUrlAddress;//直播地址

    private TextView iv_paly_start;

    //-------------------弹幕相关属性
    private DanmuControl mDanmuControl;
    private IDanmakuView mDanmakuView;
    private List<Danmu> danmus = new ArrayList<>();
    private int i = 0;
    private int TIME = 7000;//默认7秒
    private static Handler mHandler;


    //private final int userId = 30224;

    /*String[] avatars = {
            "http://upload.cguoguo.com/upload/2016-02-29/56d45c8f21dac.jpg",
            "http://upload.cguoguo.com/upload/2015-12-22/5678f5f3e64dd.jpg",
            "http://upload.cguoguo.com/upload/def.jpg"
    };*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.activity_play);

        //获取视频播放地址
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

        //OkLogger.e("TIME--------------------------->" + TIME);
        if (mHandler == null) {
            //保证 mHandler 不为空
            mHandler = new Handler();
        }
        mHandler.postDelayed(runnable, TIME); //每隔s执行一条弹屏信息

    }

    private void handleIntent() {
        final Intent intent = getIntent();
        if (intent.hasExtra("url_address")) {
            mUrlAddress = intent.getStringExtra("url_address");
        } else {
            //OkLogger.e("No extras.");
        }
    }


    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                mHandler.postDelayed(this, TIME);

                //TIME = (int) Math.round(Math.random() * (10000 - 400) + 10000);
                TIME = (int) Math.round(Math.random() * (8000 - 1000) + 9000);
                //OkLogger.e("TIME-------------TIME-------------->" + TIME);
                //OkLogger.e("-------------i> " + i);

                //danmus.size() > 0 说明有数据，否则需要请求弹幕信息
                if (danmus.size() > 0) {
                    if (i >= 20) {  //-----大于等于20则清除danmus中的内容
                        danmus.clear();
                        i = 0;
                        //OkLogger.e("----------------------------------danmus.clear()---------------------------->");
                    } else {
                        // 添加弹幕
                        mDanmuControl.addDanmu(danmus.get(i++));
                    }
                } else {
                    CheckStartDM(); //检测弹幕是否开启
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    void initView() {

//------------------------------------------------------------------------------
        mDanmakuView = (IDanmakuView) findViewById(R.id.danmakuView);

        mDanmuControl = new DanmuControl(this); //设置弹幕视图
        mDanmuControl.setDanmakuView(mDanmakuView);
        //mDanmuControl.setUserId(userId);    //设置用户id，区别背景色

//------------------------------------------------------------------------------

        mRootView = (LinearLayout) findViewById(R.id.root);
        if (mLivePlayer == null) {
            mLivePlayer = new TXLivePlayer(this);
        }

        mPlayerView = (TXCloudVideoView) findViewById(R.id.video_view);
        //mPlayerView.disableLog(true);
        mLoadingView = (ImageView) findViewById(R.id.loadingImageView);

        mVideoPlay = false; //默认设置为False


        //点击播放
        iv_paly_start = (TextView) findViewById(R.id.iv_paly_start);
        iv_paly_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_paly_start.setVisibility(View.INVISIBLE);
                toPlay();//播放
            }
        });


        toPlay();//播放

        this.setCacheStrategy(CACHE_STRATEGY_AUTO);
    }


    private void toPlay() {
        if (mVideoPlay) {
            if (mVideoPause) {
                mLivePlayer.resume();
                mRootView.setBackgroundColor(0xff000000);
            } else {
                mLivePlayer.pause();
            }
            mVideoPause = !mVideoPause;
        } else {
            if (startPlayRtmp()) {
                mVideoPlay = !mVideoPlay;
            }
        }
    }

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


        //OkLogger.e("----------------->vrender onDestroy");
        mDanmuControl.destroy();
        if (mHandler != null) {
            mHandler = null;
        }
        OkHttpUtil.getInstance().cancelTag(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        mDanmuControl.pause();
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

        //danmu生命周期
        mDanmuControl.resume();
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

    private boolean startPlayRtmp() {
//          由于iOS AppStore要求新上架的app必须使用https,所以后续腾讯云的视频连接会支持https,
//          但https会有一定的性能损耗,所以android将统一替换会http
//        if (playUrl.startsWith("https://")) {
//            playUrl = "http://" + playUrl.substring(8);
//        }

        if (!checkPlayUrl(mUrlAddress)) {
            return false;
        }

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
            mRootView.setBackgroundResource(R.mipmap.main_bkg);
            iv_paly_start.setVisibility(View.VISIBLE);//重新加载按钮可见
            return false;
        }

        //OkLogger.e("点击播放按钮！播放类型：" + mPlayType);
        startLoadingAnimation();

        mStartPlayTS = System.currentTimeMillis();
        return true;
    }


    private void stopPlayRtmp() {

        //mBtnPlay.setImageResource(R.mipmap.play_start);
        mRootView.setBackgroundResource(R.mipmap.main_bkg);
        iv_paly_start.setVisibility(View.VISIBLE);//重新加载按钮可见

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
            ToastUtils.showLongToastSafe(param.getString(TXLiveConstants.EVT_DESCRIPTION));
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            stopLoadingAnimation();
        }
    }


    @Override
    public void onNetStatus(Bundle status) {
        /*String str = getNetStatusString(status);
        OkLogger.e("Current status, CPU:" + status.getString(TXLiveConstants.NET_STATUS_CPU_USAGE) +
                ", RES:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) + "*" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT) +
                ", SPD:" + status.getInt(TXLiveConstants.NET_STATUS_NET_SPEED) + "Kbps" +
                ", FPS:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_FPS) +
                ", ARA:" + status.getInt(TXLiveConstants.NET_STATUS_AUDIO_BITRATE) + "Kbps" +
                ", VRA:" + status.getInt(TXLiveConstants.NET_STATUS_VIDEO_BITRATE) + "Kbps");*/
    }

    public void setCacheStrategy(int nCacheStrategy) {
        if (mCacheStrategy == nCacheStrategy) return;
        mCacheStrategy = nCacheStrategy;

        switch (nCacheStrategy) {
            case CACHE_STRATEGY_AUTO:
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setMaxAutoAdjustCacheTime(CACHE_TIME_SMOOTH);
                mPlayConfig.setMinAutoAdjustCacheTime(CACHE_TIME_FAST);
                mLivePlayer.setConfig(mPlayConfig);
                break;
        }
    }


    //获取直播列表(默认请求30条弹幕信息)
    private void getRollList() {
        Map<String, String> params = new TreeMap<>();
        params.put("count", "20");
        params.putAll(CommonBody.getInstance().commonBody());
        String jsonParams = GsonConvertUtil.toJson(params);

        OkHttpUtil.<String>post(URL.RollOrderList).upJson(jsonParams).tag(this).execute(new JsonCallback<String>() {
            @Override
            public void onSuccess(Response<String> response) {
                if (!TextUtils.isEmpty(response.body())) {

                    RollListInfo listInfo = GsonConvertUtil.fromJson(response.body(), RollListInfo.class);
                    if (listInfo.isBizSuccess) {
                        if (listInfo.data.size() > 0) {
                            //OkLogger.e("listInfo.data.size()-------------------->" + listInfo.data.size());
                            for (RollListInfo.DataBean dataBean : listInfo.data) {

                                //Danmu danmu1 = new Danmu(0, userId, "Comment", avatars[0], " 我：这是一条弹幕");
                                //Danmu danmu2 = new Danmu(0, 1, "Comment", avatars[1], "楼主：这又是一又是一条弹幕");
                                //Danmu danmu3 = new Danmu(0, 3, "Comment", avatars[2], " 普通：这还是一条弹幕");
                                //danmus.add(danmu1);
                                //danmus.add(danmu2);
                                //danmus.add(danmu3);
                                //Collections.shuffle(danmus);
                                //danmus.remove(0);
                                //danmus.remove(0);

                                Danmu danmu = new Danmu();
                                danmu.id = 0;
                                danmu.userId = dataBean.userId;
                                //danmu.type = "Like";    //赞，无背景
                                danmu.type = "Comment";
                                danmu.imgUrl = dataBean.imgSrc;
                                //最新订单来自 黑河 张***   14 秒前
                                danmu.content = "最新订单来自" + dataBean.city + " " + dataBean.nickName + "，" + dataBean.second + " 秒前";
                                danmus.add(danmu);
                            }
                        }
                    } else {
                        ToastUtils.showLongToastSafe("弹幕信息请求失败！");
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ToastUtils.showLongToastSafe("服务器错误！");
            }
        });
    }


    //检测弹幕是否开启/关闭
    private void CheckStartDM() {
        String jsonParams = GsonConvertUtil.toJson(MD5Utils.commonMD5());
        GenericRequestManager.upJson(URL.CheckStartDM, jsonParams, this, new JsonCallback<String>() {
            @Override
            public void onSuccess(Response<String> response) {

                if (!TextUtils.isEmpty(response.body())) {
                    CheckInfo info = GsonConvertUtil.fromJson(response.body(), CheckInfo.class);
                    if (info.IsSuccess) {
                        if (info.records) {  //弹幕是否开启/关闭
                            getRollList();  //请求弹幕信息
                        } else {
                            //OkLogger.e("----------------------------------弹幕信息关闭---------------------------->");
                        }
                    } else {
                        ToastUtils.showLongToastSafe("请求失败！");
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ToastUtils.showLongToastSafe("请确认服务器是否开启及网络是否连接！");
            }
        });
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

}
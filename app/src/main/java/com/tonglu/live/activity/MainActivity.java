package com.tonglu.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tonglu.live.AppApplication;
import com.tonglu.live.R;
import com.tonglu.live.adapter.DividerItemDecoration;
import com.tonglu.live.adapter.LiveAdapter;
import com.tonglu.live.base.BaseTitleActivity;
import com.tonglu.live.callback.StringDialogCallback;
import com.tonglu.live.manager.GenericRequestManager;
import com.tonglu.live.model.LiveListInfo;
import com.tonglu.live.utils.Constants;
import com.tonglu.live.utils.GsonConvertUtil;
import com.tonglu.live.utils.MD5Utils;
import com.tonglu.live.utils.ToastUtils;
import com.tonglu.live.utils.ValidateUtils;
import com.tonglu.okhttp.model.Response;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends BaseTitleActivity {

    private RecyclerView mRecyclerView;
    private LiveAdapter mLiveAdapter;
    public List<LiveListInfo.RecordsBean> recordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("直播列表");

        mRecyclerView = findView(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mLiveAdapter = new LiveAdapter();

        //获取直播地址
       /* String TestUrl = "rtmp://live.otofuturestore.com/malls/dianneizhibo_lld?auth_key=1502862908-0-0-10a6fabad2fb2b3c2ec01545c06bddc7";
        Intent intent = new Intent(MainActivity.this, LivePlayerActivity.class);
        intent.putExtra("url_address", TestUrl);
        startActivity(intent);*/


        mLiveAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //ToastUtils.showLongToastSafe(position + "");
                //获取直播地址
                String url = recordsList.get(position).urls.get(0).url;
                //OkLogger.e(url);
                Intent intent = new Intent(MainActivity.this, LivePlayerActivity.class);
                intent.putExtra("url_address", url);
                startActivity(intent);
            }
        });

        //判断网络是否可用
        if (ValidateUtils.isNetworkConnected(AppApplication.getInstance())) {
            getLiveList();  //请求直播地址
        } else {
            ToastUtils.showLongToastSafe("请确认网络是否可用!");
        }

        mRecyclerView.setAdapter(mLiveAdapter);

    }


    //获取直播列表
    private void getLiveList() {
        String url = "v/VedioAddress";

        Map<String, String> params = new TreeMap<>();
        params.put("appkey", Constants.APP_KEY_);
        params.put("appsecret", Constants.APP_SECRET);

        //MD5加密
        String signTest = "abc" + Constants.APP_KEY_ + Constants.APP_SECRET + "cba";
        params.put("sign", MD5Utils.MD5(signTest));

        String jsonParams = GsonConvertUtil.toJson(params);

        GenericRequestManager.upJson(url, jsonParams, this, new StringDialogCallback(this) {
            @Override
            public void onSuccess(Response<String> response) {

                if (!TextUtils.isEmpty(response.body())) {
                    LiveListInfo listInfo = GsonConvertUtil.fromJson(response.body(), LiveListInfo.class);
                    if (listInfo.IsSuccess) {
                        if (listInfo.records.size() > 0) {
                            recordsList = listInfo.records;
                            mLiveAdapter.addData(listInfo.records); //展示数据
                        }
                    } else {
                        ToastUtils.showLongToastSafe("请求失败！");
                    }
                    //OkLogger.e("------>" + listInfo);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ToastUtils.showLongToastSafe("请确认服务器是否开启及网络是否连接！");
            }
        });
    }


    private long exitTime = 0;  //退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {    //按向下键
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {   // 按向上键
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) { //按向左键
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {    //按向右键
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 1900) {
                ToastUtils.showShortToastSafe("再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

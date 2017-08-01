package com.tonglu.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tonglu.live.R;
import com.tonglu.live.adapter.LiveAdapter;
import com.tonglu.live.base.BaseTitleActivity;
import com.tonglu.live.callback.StringDialogCallback;
import com.tonglu.live.manager.GenericRequestManager;
import com.tonglu.live.model.LiveListInfo;
import com.tonglu.live.utils.Constants;
import com.tonglu.live.utils.GsonConvertUtil;
import com.tonglu.live.utils.MD5Utils;
import com.tonglu.live.utils.ToastUtils;
import com.tonglu.okhttp.model.Response;
import com.tonglu.okhttp.utils.OkLogger;

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

        mLiveAdapter = new LiveAdapter();
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

        getLiveList();  //请求直播地址

        mRecyclerView.setAdapter(mLiveAdapter);
    }


    private void getLiveList() {
        String url = "v/VedioAddress";

        Map<String, String> params = new TreeMap<>();
        params.put("appkey", Constants.APP_KEY);
        params.put("appsecret", Constants.APP_SECRET);

        //MD5加密
        String signTest = "abc" + Constants.APP_KEY + Constants.APP_SECRET + "cba";
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
                ToastUtils.showLongToastSafe("请确认服务器是否开启！");
            }
        });

    }
}

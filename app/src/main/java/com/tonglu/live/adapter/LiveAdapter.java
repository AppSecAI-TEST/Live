package com.tonglu.live.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tonglu.live.R;
import com.tonglu.live.model.LiveListInfo;

public class LiveAdapter extends BaseQuickAdapter<LiveListInfo.RecordsBean, BaseViewHolder> {

    public LiveAdapter() {
        super(R.layout.layout_livelist);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveListInfo.RecordsBean item) {

        helper.setText(R.id.tv_address, item.vedioName);
        //if (item.urls.get(1).mediaType.equals("http")) {
        helper.setText(R.id.tv_vedio_url_type, "直播格式：" + item.urls.get(1).mediaType);
        //}
        helper.setText(R.id.tv_vedio_url, "地址：" + item.urls.get(1).url);

        /*new Resolution { Id = Resolution.IdOriginal, Name = "原画" },
        new Resolution { Id = "LHD", Name = "高清" },
        new Resolution { Id = "LSD", Name = "标清" },
        new Resolution { Id = "LLD", Name = "流畅" },*/

        switch (item.vedioType) {
            case "Original":
                helper.setText(R.id.tv_vedio_type, "原画");
                break;
            case "LHD":
                helper.setText(R.id.tv_vedio_type, "高清");
                break;
            case "LSD":
                helper.setText(R.id.tv_vedio_type, "标清");
                break;
            case "LLD":
                helper.setText(R.id.tv_vedio_type, "流畅");
                break;
        }
    }
}

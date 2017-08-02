package com.tonglu.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2017-8-2.
 * 描    述：弹幕列表
 * ===========================================
 */


public class RollListInfo implements Serializable {
    private static final long serialVersionUID = -8425033273673870521L;

    public String attachedData;
    public String errCode;
    public String errMsg;
    public String bizErrorMsg;
    public boolean isBizSuccess;
    public List<DataBean> data;

    public static class DataBean{

        public int userId;
        public String imgSrc;
        public String nickName;
        public String city;
        public int second;
        public String createTime;
    }
}

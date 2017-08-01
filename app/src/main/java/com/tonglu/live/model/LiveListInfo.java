package com.tonglu.live.model;

import java.io.Serializable;
import java.util.List;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2017-8-1.
 * 描    述：
 * ===========================================
 */


public class LiveListInfo implements Serializable {

    public boolean IsSuccess;
    public String errormsg;
    public List<RecordsBean> records;

    public static class RecordsBean {

        public String vedioType;
        public String vedioName;
        public List<UrlsBean> urls;

        public static class UrlsBean {
            public String mediaType;
            public String url;

            @Override
            public String toString() {
                return "UrlsBean{" +
                        "mediaType='" + mediaType + '\'' +
                        ", url='" + url + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "RecordsBean{" +
                    "vedioType='" + vedioType + '\'' +
                    ", vedioName='" + vedioName + '\'' +
                    ", urls=" + urls +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LiveListInfo{" +
                "IsSuccess=" + IsSuccess +
                ", errormsg='" + errormsg + '\'' +
                ", records=" + records +
                '}';
    }
}

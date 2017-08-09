package com.tonglu.live.model;

import java.io.Serializable;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2017-8-9.
 * 描    述：
 * ===========================================
 */


public class CheckInfo implements Serializable {
    private static final long serialVersionUID = -7062686345797367415L;

    public boolean IsSuccess;
    public boolean records;
    public String errormsg;

    @Override
    public String toString() {
        return "CheckInfo{" +
                "IsSuccess=" + IsSuccess +
                ", records=" + records +
                ", errormsg='" + errormsg + '\'' +
                '}';
    }
}

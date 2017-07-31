package com.tonglu.live.model;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：17/6/27
 * 描    述：接收服务器返回Json外层基类（包含<T>data）
 * ================================================
 */
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;

    public int totalCount;  //返回的结果个数	只在获取列表信息时才有
    public String errCode;  //系统级错误码
    public String errMsg;   //系统级错误信息
    public String bizErrorMsg;  //业务级错误信息
    public Boolean isBizSuccess; //业务是否成功	用来判断接口是否请求成功
    public boolean isCanInvestment;//是否有门店
    public AttachedDataBean attachedData;   //外层attachedData
    public T data;    //接口返回的业务数据

    @Override
    public String toString() {
        return "BaseResponse{" +
                "totalCount=" + totalCount +
                ", errCode='" + errCode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", bizErrorMsg='" + bizErrorMsg + '\'' +
                ", isBizSuccess=" + isBizSuccess +
                ", isCanInvestment=" + isCanInvestment +
                ", data=" + data +
                '}';
    }

    public static class AttachedDataBean {
        public boolean isMember;
    }
}

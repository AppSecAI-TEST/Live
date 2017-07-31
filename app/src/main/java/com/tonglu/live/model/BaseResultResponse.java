package com.tonglu.live.model;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：17/6/27
 * 描    述：接收服务器返回Json外层基类（不包含<T>data）
 * ================================================
 */
public class BaseResultResponse implements Serializable {

    private static final long serialVersionUID = -1477609349345966116L;

    public int totalCount;  //返回的结果个数	只在获取列表信息时才有
    public String errCode;  //系统级错误码
    public String errMsg;   //系统级错误信息
    public String bizErrorMsg;  //业务级错误信息
    public Boolean isBizSuccess; //业务是否成功	用来判断接口是否请求成功
    public boolean isCanInvestment;//是否有门店

    public BaseResponse.AttachedDataBean attachedData;  //奇葩字段一枚

    public BaseResponse toBaseResponse() {

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.totalCount = totalCount;
        baseResponse.errCode = errCode;
        baseResponse.errMsg = errMsg;
        baseResponse.bizErrorMsg = bizErrorMsg;
        baseResponse.isBizSuccess = isBizSuccess;
        baseResponse.isCanInvestment = isCanInvestment;

        //奇葩字段一枚
        baseResponse.attachedData.isMember = attachedData.isMember;


        return baseResponse;
    }
}

package com.tonglu.live.utils;

import android.view.View;

/**
 * ===========================================
 * 作    者：zhsh
 * 版    本：1.0
 * 创建日期：2016/11/29.
 * 描    述：处理视图（view）相关的方法
 * ===========================================
 */

public class DealViewUtils {
    private DealViewUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 设置按钮的点击状态和背景颜色
     *
     * @param drawableId 背景图片
     * @param isClicked  是否可点击
     */
    public static void buttonState(View view, int drawableId, boolean isClicked) {

        view.setClickable(isClicked);
        view.setBackgroundResource(drawableId);
    }


}

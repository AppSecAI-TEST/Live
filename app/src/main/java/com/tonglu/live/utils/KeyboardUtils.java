package com.tonglu.live.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2016-11-21.
 * 描    述：软键盘工具类
 * ===========================================
 */
public class KeyboardUtils {

    private KeyboardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static final String EXTRA_DEF_KEYBOARDHEIGHT = "DEF_KEYBOARDHEIGHT";
    private static final int DEF_KEYBOARD_HEAGH_WITH_DP = 300;
    private static int sDefKeyboardHeight = -1;

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getDisplayWidthPixels(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getFontHeight(TextView textView) {
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    public static int getDefKeyboardHeight(Context context) {
        if (sDefKeyboardHeight < 0) {
            sDefKeyboardHeight = dip2px(context, DEF_KEYBOARD_HEAGH_WITH_DP);
        }
        int height = PreferenceManager.getDefaultSharedPreferences(context).getInt(EXTRA_DEF_KEYBOARDHEIGHT, 0);
        return sDefKeyboardHeight = height > 0 && sDefKeyboardHeight != height ? height : sDefKeyboardHeight;
    }

    public static void setDefKeyboardHeight(Context context, int height) {
        if (sDefKeyboardHeight != height) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(EXTRA_DEF_KEYBOARDHEIGHT, height).commit();
            KeyboardUtils.sDefKeyboardHeight = height;
        }
    }

    public static boolean isFullScreen(final Activity activity) {
        return (activity.getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    /**
     * 开启软键盘
     * @param et
     */
    public static void openSoftKeyboard(EditText et) {
        if (et != null) {
            et.setFocusable(true);
            et.setFocusableInTouchMode(true);
            et.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(et, 0);
        }
    }

    /**
     * 关闭软键盘
     * @param context
     */
    public static void closeSoftKeyboard(Context context) {
        if (context == null || !(context instanceof Activity) || ((Activity) context).getCurrentFocus() == null) {
            return;
        }
        try{
            View view = ((Activity) context).getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            view.clearFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭软键盘
     * 当使用全屏主题的时候,XhsEmoticonsKeyBoard屏蔽了焦点.关闭软键盘时,直接指定 closeSoftKeyboard(EditView)
     * @param view
     */
    public static void closeSoftKeyboard(View view) {
        if (view == null || view.getWindowToken() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 解决在页面底部置输入框，输入法弹出遮挡部分输入框的问题
     * @param root 页面根元素
     * @param editLayout 被软键盘遮挡的输入框
     */
    public static void controlKeyboardLayout(final View root, final View editLayout) {


        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInVisibleHeigh = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInVisibleHeigh > 100) {
                    int[] location = new int[2];
                    //获取editLayout在窗体的坐标
                    editLayout.getLocationInWindow(location);
                    //计算root滚动高度，使editLayout在可见区域
                    int srollHeight = (location[1] + editLayout.getHeight()) + 2*(editLayout.getHeight())- rect.bottom;
                    root.scrollTo(0, srollHeight);
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }


            }
        };
        root.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
//        if(Build.VERSION.SDK_INT<16){
//            root.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
//        }else{
//            root.getViewTreeObserver().removeOnGlobalLayoutListener();
//        }

}


}

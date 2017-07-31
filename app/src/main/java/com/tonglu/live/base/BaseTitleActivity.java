package com.tonglu.live.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tonglu.live.R;

/**
 * ===========================================
 * 作    者：gao_chun
 * 版    本：1.0
 * 创建日期：2016/11/18.
 * 描    述：自定义顶部标题栏
 * ===========================================
 */
public class BaseTitleActivity extends BaseActivity implements OnClickListener {

    protected TextView mTitleTextView;
    protected Button mBackwardbButton;
    protected Button mForwardButton, mForwardButton2;
    protected FrameLayout mContentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    /**
     * 显示标题栏返回图标、文字
     *
     * @param backwardResid
     * @param show
     */
    protected void showBackwardView(int backwardResid, boolean show) {
        if (mBackwardbButton != null) {
            if (show) {
                mBackwardbButton.setText(backwardResid);
                mBackwardbButton.setVisibility(View.VISIBLE);
            } else {
                mBackwardbButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }


    /**
     * 仅显示标题栏返回图标
     *
     * @param show
     */
    protected void showBackwardView(boolean show) {
        if (mBackwardbButton != null) {
            if (show) {
                mBackwardbButton.setText("");
                mBackwardbButton.setVisibility(View.VISIBLE);
            } else {
                mBackwardbButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }


    /**
     * forward 右侧标题栏 图标 - 文字
     *
     * @param mtext
     * @param drawableId
     * @param leftOrRight 0 表示图标在左边  其他表示在右边
     */
    protected void showForwardView(CharSequence mtext, int drawableId, int leftOrRight) {
        if (mForwardButton != null) {

            mForwardButton.setVisibility(View.VISIBLE);
            mForwardButton.setText(mtext);

            Drawable drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            if (leftOrRight == 0) {
                mForwardButton.setCompoundDrawables(drawable, null, null, null);
            } else {
                mForwardButton.setCompoundDrawables(null, null, drawable, null);
            }

        }// else ignored
    }


    /**
     * 只显示右侧标题栏图标
     *
     * @param drawableId
     */
    protected void showForwardView(int drawableId) {
        if (mForwardButton != null) {

            mForwardButton.setVisibility(View.VISIBLE);

            Drawable drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mForwardButton.setCompoundDrawables(null, null, drawable, null);

        }// else ignored
    }

    /**
     * 显示标题栏右侧文字
     *
     * @param mtext
     */
    protected void showForwardView(CharSequence mtext) {
        if (mForwardButton != null) {

            mForwardButton.setVisibility(View.VISIBLE);
            mForwardButton2.setVisibility(View.GONE);
            mForwardButton.setText(mtext);
            mForwardButton.setCompoundDrawables(null, null, null, null);
        }// else ignored
    }


    //forward,显示图标
    /*protected void showForwardView(boolean show) {
        if (mForwardButton != null) {
            if (show) {
                mForwardButton.setVisibility(View.VISIBLE);
            } else {
                mForwardButton.setVisibility(View.INVISIBLE);
            }
        } // else ignored
    }*/

    /**
     * 设置标题栏右侧显示两个图标
     *
     * @param mDrawable1
     * @param mDrawable2
     */
    protected void showForwardView(int mDrawable1, int mDrawable2) {

        //设置第一个图标位置
        if (mForwardButton != null) {
            mForwardButton.setVisibility(View.VISIBLE);
            Drawable drawable = getResources().getDrawable(mDrawable1);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mForwardButton.setCompoundDrawables(null, null, drawable, null);
        }

        //设置第二个图标位置
        if (mForwardButton2 != null) {
            mForwardButton2.setVisibility(View.VISIBLE);
            Drawable drawable = getResources().getDrawable(mDrawable2);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mForwardButton2.setCompoundDrawables(drawable, null, null, null);
        }
    }


    protected void onBackward(View backwardView) {
        finish();
    }


    protected void onForward(View forwardView) {

    }

    //标题栏最右边的按钮事件回调
    protected void onForwardRight(View forwardView) {

    }

    /**
     * 设置标题
     *
     * @param titleId
     */
    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(titleId);
    }


    /**
     * 设置标题
     *
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }


    /**
     * 设置标题颜色
     *
     * @param textColor
     */
    @Override
    public void setTitleColor(int textColor) {
        mTitleTextView.setTextColor(textColor);
    }


    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(int)
     */
    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }


    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View)
     */
    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View view, LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
    }


    /**
     * 标题栏点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                onBackward(v);
                break;

            case R.id.button_forward:
                onForward(v);
                break;

            case R.id.button_forward2:
                onForwardRight(v);
                break;
            default:
                break;
        }
    }


    private void setupViews() {
        super.setContentView(R.layout.activity_title);
        //mLayoutTitleBar = (RelativeLayout)findViewById(R.id.layout_titlebar);
        mTitleTextView = (TextView) findViewById(R.id.text_title);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        mBackwardbButton = (Button) findViewById(R.id.button_backward);
        mForwardButton = (Button) findViewById(R.id.button_forward);
        mForwardButton2 = (Button) findViewById(R.id.button_forward2);
        //mProgress = (ProgressBar) findViewById(R.id.progress);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

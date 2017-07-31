package com.tonglu.live.xdanmu;

import android.view.View;

public abstract class DanmuConverter<M>{
    public abstract int getSingleLineHeight();
    public abstract View convert(M model);
}

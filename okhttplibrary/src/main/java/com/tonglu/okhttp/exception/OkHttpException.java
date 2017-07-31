package com.tonglu.okhttp.exception;

public class OkHttpException extends Exception {
    private static final long serialVersionUID = -8641198158155821498L;

    public OkHttpException(String detailMessage) {
        super(detailMessage);
    }

    public static OkHttpException UNKNOWN() {
        return new OkHttpException("unknown exception!");
    }

    public static OkHttpException BREAKPOINT_NOT_EXIST() {
        return new OkHttpException("breakpoint file does not exist!");
    }

    public static OkHttpException BREAKPOINT_EXPIRED() {
        return new OkHttpException("breakpoint file has expired!");
    }
}

package com.tencent.mapcompare.util;

/**
 * Created by wangxiaokun on 16/5/18.
 */
public class Log {
    private static final boolean DEBUG = true;

    private static final String DEFAULT_TAG = "wxk";

    private static void log(int level, String tag, String text, Throwable throwable) {
        if (!DEBUG && level != android.util.Log.ERROR) {
            return;
        }
        if (tag == null) {
            tag = DEFAULT_TAG;
        }
        switch (level) {
            case android.util.Log.ERROR:
                android.util.Log.e(tag, text, throwable);
                break;
            case android.util.Log.WARN:
                android.util.Log.w(tag, text, throwable);
                break;
            case android.util.Log.DEBUG:
                android.util.Log.d(tag, text, throwable);
                break;
            case android.util.Log.INFO:
                android.util.Log.i(tag, text, throwable);
                break;
            case android.util.Log.VERBOSE:
                android.util.Log.v(tag, text, throwable);
            break;
        }
    }

    public static void e(String tag, String msg) {
        log(android.util.Log.ERROR, tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        log(android.util.Log.ERROR, tag, msg, throwable);
    }

    public static void e(String msg) {
        log(android.util.Log.ERROR, null, msg, null);
    }

    public static void e(String msg, Throwable throwable) {
        log(android.util.Log.ERROR, null, msg, throwable);
    }

    public static void w(String tag, String msg) {
        log(android.util.Log.WARN, tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable throwable) {
        log(android.util.Log.WARN, tag, msg, throwable);
    }

    public static void w(String msg) {
        log(android.util.Log.WARN, null, msg, null);
    }

    public static void w(String msg, Throwable throwable) {
        log(android.util.Log.WARN, null, msg, throwable);
    }

    public static void d(String tag, String msg) {
        log(android.util.Log.DEBUG, tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable throwable) {
        log(android.util.Log.DEBUG, tag, msg, throwable);
    }

    public static void d(String msg) {
        log(android.util.Log.DEBUG, null, msg, null);
    }

    public static void d(String msg, Throwable throwable) {
        log(android.util.Log.DEBUG, null, msg, throwable);
    }

    public static void i(String tag, String msg) {
        log(android.util.Log.INFO, tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable throwable) {
        log(android.util.Log.INFO, tag, msg, throwable);
    }

    public static void i(String msg) {
        log(android.util.Log.INFO, null, msg, null);
    }

    public static void i(String msg, Throwable throwable) {
        log(android.util.Log.INFO, null, msg, throwable);
    }
    public static void v(String tag, String msg) {
        log(android.util.Log.VERBOSE, tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable throwable) {
        log(android.util.Log.VERBOSE, tag, msg, throwable);
    }

    public static void v(String msg) {
        log(android.util.Log.VERBOSE, null, msg, null);
    }

    public static void v(String msg, Throwable throwable) {
        log(android.util.Log.VERBOSE, null, msg, throwable);
    }
}

package com.tencent.mapcompare.util;

/**
 * Created by wangxiaokun on 16/5/18.
 */
public class Log {
    private static final boolean DEBUG = true;

    private static final String DEFAULT_TAG = "wxk";

    private static void log(char level, String tag, String text, Throwable throwable) {
        if (!DEBUG && level != 'e') {
            return;
        }
        if (tag == null) {
            tag = DEFAULT_TAG;
        }
        switch (level) {
            case 'e':
                android.util.Log.e(tag, text, throwable);
                break;
            case 'w':
                android.util.Log.w(tag, text, throwable);
                break;
            case 'd':
                android.util.Log.d(tag, text, throwable);
                break;
            case 'i':
                android.util.Log.i(tag, text, throwable);
                break;
            case 'v':
                android.util.Log.v(tag, text, throwable);
            break;
        }
    }

    public static void e(String tag, String msg) {
        log('e', tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        log('e', tag, msg, throwable);
    }

    public static void e(String msg) {
        log('e', null, msg, null);
    }

    public static void e(String msg, Throwable throwable) {
        log('e', null, msg, throwable);
    }

    public static void w(String tag, String msg) {
        log('w', tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable throwable) {
        log('w', tag, msg, throwable);
    }

    public static void w(String msg) {
        log('w', null, msg, null);
    }

    public static void w(String msg, Throwable throwable) {
        log('w', null, msg, throwable);
    }

    public static void d(String tag, String msg) {
        log('d', tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable throwable) {
        log('d', tag, msg, throwable);
    }

    public static void d(String msg) {
        log('d', null, msg, null);
    }

    public static void d(String msg, Throwable throwable) {
        log('d', null, msg, throwable);
    }

    public static void i(String tag, String msg) {
        log('i', tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable throwable) {
        log('i', tag, msg, throwable);
    }

    public static void i(String msg) {
        log('i', null, msg, null);
    }

    public static void i(String msg, Throwable throwable) {
        log('i', null, msg, throwable);
    }
    public static void v(String tag, String msg) {
        log('v', tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable throwable) {
        log('v', tag, msg, throwable);
    }

    public static void v(String msg) {
        log('v', null, msg, null);
    }

    public static void v(String msg, Throwable throwable) {
        log('v', null, msg, throwable);
    }
}

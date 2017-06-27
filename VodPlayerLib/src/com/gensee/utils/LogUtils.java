package com.gensee.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志工具类
 */
public class LogUtils {
    /**
     * 日志的开关，当项目上线时，关闭掉开关
     */
    private static boolean isShowing = API.isDebug;

    public static void v(String tag, String text) {
        if (isShowing) {
            Log.v(tag, TextUtils.isEmpty(text) ? "null" : text);
        }
    }

    public static void d(String tag, String text) {
        if (isShowing) {
            Log.d(tag, TextUtils.isEmpty(text) ? "null" : text);
        }
    }

    public static void i(String tag, String text) {
        if (isShowing) {
            Log.i(tag, TextUtils.isEmpty(text) ? "null" : text);
        }
    }

    public static void w(String tag, String text) {
        if (isShowing) {
            Log.w(tag, TextUtils.isEmpty(text) ? "null" : text);
        }
    }

    public static void e(String tag, String text) {
        if (isShowing) {
            Log.e(tag, TextUtils.isEmpty(text) ? "null" : text);
        }
    }
}

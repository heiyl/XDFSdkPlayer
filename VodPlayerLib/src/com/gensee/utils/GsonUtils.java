package com.xdf.studybroad.tool;
/**
 * json解析的工具类
 */

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GsonUtils {

    public static <T> T getEntity(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> List<T> getListEntitys(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static <T> List<T> getListFromJSON(String json, Class<T[]> type) {
        T[] list = new Gson().fromJson(json, type);
        return Arrays.asList(list);
    }


    public static List<String> getStringList(String jsonString) {
        List<String> list = new ArrayList<String>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;

    }

    public static List<Map<String, Object>> listKeyMap(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;
    }


    public static boolean isSuccess(String string) {

        if (TextUtils.isEmpty(string))
            return false;

        try {
            //"status":1,"info":"","result"
            JSONObject obj = new JSONObject(string);

            int status = obj.optInt("status");
            if (status == 1) {

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static String getInfo(String string) {
        String info = "";
        if (TextUtils.isEmpty(string))
            info = "";

        try {
            //"status":1,"info":"","result"
            JSONObject obj = new JSONObject(string);

            info = obj.optString("info");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return info;
    }
}
package com.gensee.player.fragement;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gensee.utils.LogUtils;

import cn.finalteam.okhttpfinal.HttpCycleContext;
import cn.finalteam.okhttpfinal.HttpTaskHandler;

/**
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment implements HttpCycleContext {
    protected final String HTTP_TASK_KEY = "HttpTaskKey_" + hashCode();
    public Activity mActivity;
    public View mRootView;//fragment的根布局
    protected Toast toast;
    protected static String TAG = BaseFragment.class.getSimpleName();

    protected static final String SLID_UP_TYPE = "slid_up_type";
    protected static final int SLID_UP_GROUP = 1;
    protected static final int SLID_UP_ADVISORY = 2;
    protected static final int SLID_UP_HELP = 3;


    //fragment创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity(); //获取Activity
    }

    //初始化fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        mRootView = initViews(); //初始化根布局
        TAG = this.getClass().getSimpleName();
        LogUtils.e(TAG, "onCreateView: " + this.getClass().getSimpleName());
        return mRootView;
    }

    //在Activity创建的时候加载数据
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 网络连接取消
     */
    @Override
    public String getHttpTaskKey() {
        return HTTP_TASK_KEY;
    }

    @Override
    public void onDestroy() {
        HttpTaskHandler.getInstance().removeTask(HTTP_TASK_KEY);
        super.onDestroy();
    }

    public void showToast(String message) {
        if (mActivity != null) {
            if (toast == null) {
                toast = Toast.makeText(mActivity, message, Toast.LENGTH_SHORT);
            } else {
                toast.setText(message);
                toast.setDuration(Toast.LENGTH_SHORT);
            }
            toast.show();
        }
    }
}

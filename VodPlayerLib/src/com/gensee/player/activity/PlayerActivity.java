package com.gensee.player.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gensee.adapter.UserListAdapter;
import com.gensee.common.ServiceType;
import com.gensee.config.ConfigApp;
import com.gensee.entity.InitParam;
import com.gensee.entity.PayInfo;
import com.gensee.entity.PingEntity;
import com.gensee.entity.RewardResult;
import com.gensee.entity.UserInfo;
import com.gensee.fragement.ChatFragment;
import com.gensee.fragement.DocFragment;
import com.gensee.fragement.QaFragment;
import com.gensee.fragement.ViedoFragment;
import com.gensee.fragement.VoteFragment;
import com.gensee.net.AbsRtAction;
import com.gensee.player.OnPlayListener;
import com.gensee.player.Player;
import com.gensee.player.adapter.ViewPagerAdapter;
import com.gensee.playerdemo.LogCatService;
import com.gensee.playerdemo.R;
import com.gensee.taskret.OnTaskRet;
import com.gensee.utils.DensityUtil;
import com.gensee.utils.GenseeLog;
import com.gensee.view.GSVideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播页面
 * 2017/6/9.
 * yulong
 */

public class PlayerActivity extends FragmentActivity implements OnPlayListener,OnTabSelectListener,View.OnClickListener {

    private static final String TAG = "PlayerActivity";
    private SharedPreferences preferences;
    private Player mPlayer;
    private GSVideoView mGSViedoView;

    SlidingTabLayout mStlTab;
    ViewPager mViewPager;

    LinearLayout mStartpage;
    LinearLayout llt_joining;
    LinearLayout llt_state;
    TextView tv_refresh;

    ImageView iv_screen;
    LinearLayout llt_bottom;

    private boolean isFullScreen;

    private ArrayList<String> mTagList = new ArrayList<String>();
    private ArrayList<Fragment> mFragmentList;
    private ViewPagerAdapter mPagerAdapter;

    private RelativeLayout relTip;
    private TextView txtTip;

    private UserListAdapter mChatAdapter;
    private ListView mChatListview;
    private ProgressBar mProgressBar;
    private ChatFragment mChatFragment;
    private DocFragment mDocFragment;
    private ViedoFragment mViedoFragment;
    private QaFragment mQaFragment;
    private VoteFragment mVoteFragment;

    private ServiceType serviceType = ServiceType.TRAINING;

    private boolean bJoinSuccess = false;

    private Intent serviceIntent;

    private AlertDialog dialog;
    private int inviteMediaType;


    String domain = "xdfjt.gensee.com";
    String number = "99310246";
    String account = "";
    String accountPwd = "";
    String joinPwd = "372601";
    String nickName = "fastsdk_test_android";

    interface HANDlER {
        int USERINCREASE = 1;
        int USERDECREASE = 2;
        int USERUPDATE = 3;
        int SUCCESSJOIN = 4;
        int SUCCESSLEAVE = 5;
        int CACHING = 6;
        int CACHING_END = 7;
        int RECONNECTING = 8;
        int CONNECTFAIL = 9;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case HANDlER.USERINCREASE:
                    mChatAdapter.addInfo((UserInfo) (msg.obj));
                    break;
                case HANDlER.USERDECREASE:
                    mChatAdapter.leaveInfo((UserInfo) (msg.obj));
                    break;
                case HANDlER.USERUPDATE:
                    mChatAdapter.addInfo((UserInfo) (msg.obj));
                    break;
                case HANDlER.SUCCESSJOIN:
                    preferences.edit().putString(ConfigApp.PARAMS_DOMAIN, domain)
                            .putString(ConfigApp.PARAMS_NUMBER, number)
                            .putString(ConfigApp.PARAMS_ACCOUNT, account)
                            .putString(ConfigApp.PARAMS_PWD, accountPwd)
                            .putString(ConfigApp.PARAMS_NICKNAME, nickName)
                            .putString(ConfigApp.PARAMS_JOINPWD, joinPwd).commit();
                    bJoinSuccess = true;
                    mStartpage.setVisibility(View.GONE);
                    /*if (mViedoFragment != null) {
                        mViedoFragment.onJoin(bJoinSuccess);
                    }*/
                    break;
                case HANDlER.CONNECTFAIL:
                    llt_joining.setVisibility(View.GONE);
                    llt_state.setVisibility(View.VISIBLE);
                    break;
                case HANDlER.SUCCESSLEAVE:
                    dialog();
                    break;
                case HANDlER.CACHING:
                    showTip(true, "正在缓冲...");
                    relTip.setVisibility(View.VISIBLE);
                    break;
                case HANDlER.CACHING_END:
                    showTip(false, "");
                    break;
                case HANDlER.RECONNECTING:
                    showTip(true, "正在重连...");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ConfigApp.PARAMS_JOINSUCCESS, bJoinSuccess);
        outState.putBoolean(
                ConfigApp.PARAMS_VIDEO_FULLSCREEN,
                getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        if (bJoinSuccess) {
            outState.putString(ConfigApp.PARAMS_DOMAIN, domain);
            outState.putString(ConfigApp.PARAMS_NUMBER, number);
            outState.putString(ConfigApp.PARAMS_ACCOUNT, account);
            outState.putString(ConfigApp.PARAMS_PWD, accountPwd);
            outState.putString(ConfigApp.PARAMS_NICKNAME, nickName);
            outState.putString(ConfigApp.PARAMS_JOINPWD, joinPwd);
            if (serviceType == ServiceType.WEBCAST) {
                outState.putString(ConfigApp.PARAMS_TYPE, ConfigApp.WEBCAST);
            } else if (serviceType == ServiceType.TRAINING) {
                outState.putString(ConfigApp.PARAMS_TYPE, ConfigApp.TRAINING);
            }
        }
    }

    ;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean bJoinSuccess = savedInstanceState
                .getBoolean(ConfigApp.PARAMS_JOINSUCCESS);
        boolean bVideoFullScreen = savedInstanceState
                .getBoolean(ConfigApp.PARAMS_VIDEO_FULLSCREEN);
        if (bVideoFullScreen) {
            videoFullScreen();
        }
        if (bJoinSuccess) {
            String type = (String) savedInstanceState
                    .get(ConfigApp.PARAMS_TYPE);
            if (type.equals(ConfigApp.WEBCAST)) {
                serviceType = ServiceType.WEBCAST;
            } else if (type.equals(ConfigApp.TRAINING)) {
                serviceType = ServiceType.TRAINING;
            }
            initInitParam();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.xdf_player_activity);
        preferences = getPreferences(MODE_PRIVATE);
        startLogService();
        initWidget();
        bindViewPager();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                initInitParam();
            }
        }, 1500);
    }

    private boolean isNumber(String number) {
        try {
            Long.parseLong(number);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initInitParam() {

        llt_joining.setVisibility(View.VISIBLE);
        llt_state.setVisibility(View.GONE);

        InitParam initParam = new InitParam();
        // 设置域名
        initParam.setDomain(domain);
        if (number.length() == 8 && isNumber(number)) {//此判断是为了测试方便，请勿模仿，实际使用时直接使用其中一种设置
            // 设置编号,8位数字字符串，
            initParam.setNumber(number);
        } else {
            // 如果只有直播间id（混合字符串
            // 如：8d5261f20870499782fb74be021a7e49）可以使用setLiveId("")代替setNumber()
            String liveId = number;
            initParam.setLiveId(liveId);

        }
        // 设置站点登录帐号（根据配置可选）
        initParam.setLoginAccount(account);
        // 设置站点登录密码（根据配置可选）
        initParam.setLoginPwd(accountPwd);
        // 设置显示昵称 不能为空,请传入正确的昵称，有显示和统计的作用
        // 设置显示昵称，如果设置为空，请确保
        initParam.setNickName(nickName);
        // 设置加入口令（根据配置可选）
        initParam.setJoinPwd(joinPwd);
        // 设置服务类型，如果站点是webcast类型则设置为ServiceType.ST_CASTLINE，
        // training类型则设置为ServiceType.ST_TRAINING
        initParam.setServiceType(serviceType);
        //如果启用第三方认证，必填项，且要正确有效

//		initParam.setUserId(1000000001);//大于1000000000有效
        //站点 系统设置 的 第三方集成 中直播模块 “认证“  启用时请确保”第三方K值“（你们的k值）的正确性 ；如果没有启用则忽略这个参数
        initParam.setK("");
        initParam.setUserId(1000000001);
        showTip(true, "正在玩命加入...");

        initPlayer(initParam);
    }

    public void initWidget() {
        mStlTab = (SlidingTabLayout)findViewById(R.id.stl_tab);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mStartpage = (LinearLayout) findViewById(R.id.startpage);
        llt_joining = (LinearLayout) findViewById(R.id.llt_joining);
        llt_state = (LinearLayout) findViewById(R.id.llt_state);
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        tv_refresh.setOnClickListener(this);

        iv_screen = (ImageView) findViewById(R.id.iv_screen);
        llt_bottom = (LinearLayout) findViewById(R.id.llt_bottom);
        iv_screen.setOnClickListener(this);


        relTip = (RelativeLayout) findViewById(R.id.rl_tip);
        txtTip = (TextView) findViewById(R.id.tv_tip);

        mPlayer = new Player();

        mGSViedoView = (GSVideoView) findViewById(R.id.impvoteview);
        mPlayer.setGSVideoView(mGSViedoView);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_screen) {
            int orientation = getRequestedOrientation();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    || orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
            } else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
            }
            setRequestedOrientation(orientation);
        }else if(v.getId() == R.id.tv_refresh) {
            initInitParam();
        }
    }

    public void bindViewPager() {

        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        } else {
            mFragmentList.clear();
        }
        mTagList.clear();
        mTagList.add("文档");
        mTagList.add("聊天");
        mFragmentList.add(new DocFragment(mPlayer));
        mFragmentList.add(new ChatFragment(mPlayer));

        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.setList(mTagList);
        mViewPager.setCurrentItem(0);

        mStlTab.setViewPager(mViewPager);
        mStlTab.setOnTabSelectListener(this);
        mStlTab.setCurrentTab(0);
        mStlTab.notifyDataSetChanged();
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    protected void onResume() {
        super.onResume();
//		if (mPlayer != null && bJoinSuccess) {
//			mPlayer.audioSet(false);
//			mPlayer.videoSet(false);
//		}
    }

    @Override
    protected void onPause() {
        super.onPause();
//		if (mPlayer != null && bJoinSuccess) {
//			mPlayer.audioSet(true);
//			mPlayer.videoSet(true);
//		}
    }

    public void initPlayer(InitParam p) {
        mPlayer.join(getApplicationContext(), p, this);
    }

    private void showTip(final boolean isShow, final String tip) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (isShow) {
                    if (relTip.getVisibility() != View.VISIBLE) {
                        relTip.setVisibility(View.VISIBLE);
                    }
                    txtTip.setText(tip);
                } else {
                    relTip.setVisibility(View.GONE);
                }

            }
        });
    }

    /**************************************** OnPlayListener ********************************************/
    @Override
    public void onJoin(int result) {
        GenseeLog.d(TAG, "onJoin result = " + result);
        Log.i(TAG, "onJoin result = " + result);
        String msg = null;
        switch (result) {
            case JOIN_OK:
                msg = "加入成功";
                mHandler.sendEmptyMessage(HANDlER.SUCCESSJOIN);
                break;
            case JOIN_CONNECTING:
                msg = "正在加入";
                break;
            case JOIN_CONNECT_FAILED:
                msg = "连接失败";
                mHandler.sendEmptyMessage(HANDlER.CONNECTFAIL);
                break;
            case JOIN_RTMP_FAILED:
                msg = "连接服务器失败";
                break;
            case JOIN_TOO_EARLY:
                msg = "直播还未开始";
                break;
            case JOIN_LICENSE:
                msg = "人数已满";
                break;
            default:
                msg = "加入返回错误" + result;
                break;
        }
        showTip(false, "");
        toastMsg(msg);
    }

    @Override
    public void onUserJoin(UserInfo info) {
        // 用户加入
        mHandler.sendMessage(mHandler.obtainMessage(HANDlER.USERINCREASE, info));
    }

    @Override
    public void onUserLeave(UserInfo info) {
        // 用户离开
        mHandler.sendMessage(mHandler.obtainMessage(HANDlER.USERDECREASE, info));
    }

    @Override
    public void onUserUpdate(UserInfo info) {
        // 用户更新
        mHandler.sendMessage(mHandler.obtainMessage(HANDlER.USERUPDATE, info));
    }

    @Override
    public void onReconnecting() {
        GenseeLog.d(TAG, "onReconnecting");
        //断线重连
        mHandler.sendEmptyMessage(HANDlER.RECONNECTING);
    }

    @Override
    public void onLeave(int reason) {
        // 当前用户退出
        // bJoinSuccess = false;
        String msg = null;
        switch (reason) {
            case LEAVE_NORMAL:
                msg = "您已经退出直播间";
                break;
            case LEAVE_KICKOUT:
                msg = "您已被踢出直播间";
                mHandler.sendEmptyMessage(HANDlER.SUCCESSLEAVE);
                break;
            case LEAVE_TIMEOUT:
                msg = "连接超时，您已经退出直播间";
                break;
            case LEAVE_CLOSE:
                msg = "直播已经停止";
                break;
            case LEAVE_UNKNOWN:
                msg = "您已退出直播间，请检查网络、直播间等状态";
                break;
            case LEAVE_RELOGIN:
                msg = "被踢出直播间（相同用户在其他设备上加入）";
                break;
            default:
                break;
        }
        if (null != msg) {
            showErrorMsg(msg);
        }
        // if (mPlayer != null) {
        // mPlayer.release(getApplicationContext());
        // }
        // toastMsg(msg);
    }

    /**
     * 缓冲变更
     *
     * @param isCaching true 缓冲/false 缓冲完成
     */
    @Override
    public void onCaching(boolean isCaching) {
        GenseeLog.d(TAG, "onCaching isCaching = " + isCaching);
//		mHandler.sendEmptyMessage(isCaching ? HANDlER.CACHING
//				: HANDlER.CACHING_END);
        toastMsg(isCaching ? "正在缓冲" : "缓冲完成");
    }

    /**
     * 文档切换
     *
     * @param docType 文档类型（ppt、word、txt、png）
     * @param docName 文档名称
     */
    @Override
    public void onDocSwitch(int docType, String docName) {
    }

    /**
     * 视频开始
     */
    @Override
    public void onVideoBegin() {
        GenseeLog.d(TAG, "onVideoBegin");
        toastMsg("视频开始");
    }

    /**
     * 视频结束
     */
    @Override
    public void onVideoEnd() {
        GenseeLog.d(TAG, "onVideoEnd");
        toastMsg("视频已停止");
    }

    /**
     * 音频电频值
     */
    @Override
    public void onAudioLevel(int level) {

    }

    /**
     * 错误响应
     *
     * @param errCode 错误码 请参考文档
     */
    @Override
    public void onErr(int errCode) {
        String msg = null;
        switch (errCode) {
            case AbsRtAction.ErrCode.ERR_DOMAIN:
                msg = "域名domain不正确";
                break;
            case AbsRtAction.ErrCode.ERR_TIME_OUT:
                msg = "请求超时，稍后重试";
                break;
            case AbsRtAction.ErrCode.ERR_SITE_UNUSED:
                msg = "站点不可用，请联系客服或相关人员";
                break;
            case AbsRtAction.ErrCode.ERR_UN_NET:
                msg = "网络不可用，请检查网络连接正常后再试";
                mHandler.sendEmptyMessage(HANDlER.CONNECTFAIL);
                break;
            case AbsRtAction.ErrCode.ERR_SERVICE:
                msg = "service  错误，请确认是webcast还是training";
                break;
            case AbsRtAction.ErrCode.ERR_PARAM:
                msg = "initparam参数不全";
                break;
            case AbsRtAction.ErrCode.ERR_THIRD_CERTIFICATION_AUTHORITY:
                msg = "第三方认证失败";
                break;
            case AbsRtAction.ErrCode.ERR_NUMBER_UNEXIST:
                msg = "编号不存在";
                break;
            case AbsRtAction.ErrCode.ERR_TOKEN:
                msg = "口令错误";
                break;
            case AbsRtAction.ErrCode.ERR_LOGIN:
                msg = "站点登录帐号或登录密码错误";
                break;
            default:
                msg = "错误：errCode = " + errCode;
                break;
        }
        showTip(false, "");
        if (msg != null) {
            toastMsg(msg);
        }
    }


    private void processVideoFragment(FragmentTransaction ft) {
        if (mViedoFragment == null) {
            mViedoFragment = new ViedoFragment(mPlayer);
            ft.add(R.id.fragement_update, mViedoFragment);
        } else {
            ft.show(mViedoFragment);
        }

        if (null != mViedoFragment) {
            mViedoFragment.setVideoViewVisible(true);
        }
    }

    private void processQaFragment(FragmentTransaction ft) {
        if (mQaFragment == null) {
            mQaFragment = new QaFragment(mPlayer);
            ft.add(R.id.fragement_update, mQaFragment);
        } else {
            ft.show(mQaFragment);
        }
    }

    private void processVoteFragment(FragmentTransaction ft) {
        if (mVoteFragment == null) {
            mVoteFragment = new VoteFragment(mPlayer);
            ft.add(R.id.fragement_update, mVoteFragment);
        } else {
            ft.show(mVoteFragment);
        }
    }

    private void processDocFragment(FragmentTransaction ft) {
        if (mDocFragment == null) {
            mDocFragment = new DocFragment(mPlayer);
            ft.add(R.id.fragement_update, mDocFragment);
        } else {
            ft.show(mDocFragment);
        }

        if (null != mViedoFragment) {
            mViedoFragment.setVideoViewVisible(false);
        }
    }

    private void processChatFragment(FragmentTransaction ft) {
        if (mChatFragment == null) {
            mChatFragment = new ChatFragment(mPlayer);
            ft.add(R.id.fragement_update, mChatFragment);
        } else {
            ft.show(mChatFragment);
        }
    }

    public void hideFragment(FragmentTransaction ft) {

        if (mViedoFragment != null) {
            ft.hide(mViedoFragment);
        }
        if (mVoteFragment != null) {
            ft.hide(mVoteFragment);
        }
        if (mChatFragment != null) {
            ft.hide(mChatFragment);
        }
        if (mQaFragment != null) {
            ft.hide(mQaFragment);
        }
        if (mDocFragment != null) {
            ft.hide(mDocFragment);
        }
    }


    public void exit() {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(mHomeIntent);
    }

    protected void dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
        builder.setMessage("你已经被踢出");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                finish();
                // onFinshAll();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void dialogLeave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
        builder.setMessage("确定离开");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // release();
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // mPlayer.leave();
                // mPlayer.release();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (bJoinSuccess) {
            dialogLeave();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        stopLogService();
        if (null != mChatAdapter) {
            mChatAdapter.clear();
        }
        releasePlayer();
        // onFinshAll();
        super.onDestroy();
    }

    private void releasePlayer() {
        if (null != mPlayer && bJoinSuccess) {
            mPlayer.leave();
            mPlayer.release(this);
            bJoinSuccess = false;
        }

    }

    private void showErrorMsg(final String sMsg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        PlayerActivity.this);
                builder.setTitle("提示");
                builder.setMessage(sMsg);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        finish();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

    }

    private void toastMsg(final String msg) {
        if (msg != null) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), msg,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void startLogService() {
        serviceIntent = new Intent(this, LogCatService.class);
        startService(serviceIntent);
    }

    private void stopLogService() {
        if (null != serviceIntent) {
            stopService(serviceIntent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoFullScreen();
        } else {
            videoNormalScreen();
        }
    }

    private void videoFullScreen() {
        llt_bottom.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = mGSViedoView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mGSViedoView.setLayoutParams(params);
    }

    private void videoNormalScreen() {
        llt_bottom.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = mGSViedoView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = DensityUtil.dip2px(this,281);
        mGSViedoView.setLayoutParams(params);
    }

    @Override
    public void onPublish(boolean isPlaying) {
        toastMsg(isPlaying ? "直播（上课）中" : "直播暂停（下课）");
    }

    @Override
    public void onPageSize(int pos, int w, int h) {
        //文档开始显示
        toastMsg("文档分辨率 w = " + w + " h = " + h);
    }

    /**
     * 直播主题
     */
    @Override
    public void onSubject(String subject) {
        GenseeLog.d(TAG, "onSubject subject = " + subject);

    }

    /**
     * 在线人数
     *
     * @param total
     */
    public void onRosterTotal(int total) {
        GenseeLog.d(TAG, "onRosterTotal total = " + total);
    }

    /**
     * 系统广播消息
     */
    @Override
    public void onPublicMsg(long userId, String msg) {
        GenseeLog.d(TAG, "广播消息：" + msg);
        toastMsg("广播消息：" + msg);
    }

    // int INVITE_AUIDO = 1;//only audio
    // int INVITE_VIDEO = 2;//only video
    // int INVITE_MUTI = 3;//both audio and video
    @Override
    public void onInvite(final int type, final boolean isOpen) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                postInvite(type, isOpen);
            }
        });
    }

    private void postInvite(int type, boolean isOpen) {
        if (isOpen) {
            inviteMediaType = type;
            String media = "音频";
            if (type == INVITE_AUIDO) {
                media = "音频";
            } else if (type == INVITE_VIDEO) {
                media = "视频";
            } else {
                media = "音视频";
            }
            if (dialog == null) {
                dialog = new AlertDialog.Builder(this)
                        .setPositiveButton("接受",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        accept(true);
                                    }
                                })
                        .setNegativeButton("拒绝",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        accept(false);
                                    }
                                }).create();
            }
            dialog.setMessage("老师邀请你打开" + media);
            dialog.show();
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            accept(false);
        }
    }

    private void accept(boolean isAccept) {
        mPlayer.openMic(this, isAccept, null);
    }

    @Override
    public void onMicNotify(int notify) {
        GenseeLog.d(TAG, "onMicNotify notify = " + notify);
        switch (notify) {
            case MicNotify.MIC_COLSED:
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        mViedoFragment.onMicColesed();
                    }
                });
                mPlayer.inviteAck(inviteMediaType, false, null);
                break;
            case MicNotify.MIC_OPENED:
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mViedoFragment.onMicOpened(inviteMediaType);
                    }
                });
                mPlayer.inviteAck(inviteMediaType, true, null);

                break;
            case MicNotify.MIC_OPEN_FAILED:
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        toastMsg("麦克风打开失败，请重试并允许程序打开麦克风");
                    }
                });
                mPlayer.openMic(this, false, null);
                mPlayer.inviteAck(inviteMediaType, false, null);
                break;

            default:
                break;
        }
    }

    @Override
    public void onLiveText(String language, String text) {
        toastMsg("文字直播\n语言：" + language + "\n内容：" + text);
    }

    @Override
    public void onLottery(int cmd, String info) {
        //cmd 1:start, 2: stop, 3: abort
        toastMsg("抽奖\n指令：" + (cmd == 1 ? "开始" : (cmd == 2 ? "结束" : "取消"))
                + "\n结果：" + info);
    }

    @Override
    public void onRollcall(final int timeOut) {
        mHandler.post(new Runnable() {
            private AlertDialog dialog = null;
            private int itimeOut;

            private void rollcallAck(final boolean isAccept) {
                mHandler.removeCallbacks(this);
                mPlayer.rollCallAck(isAccept, new OnTaskRet() {

                    @Override
                    public void onTaskRet(boolean arg0, int arg1, String arg2) {
                        toastMsg(arg0 ? (isAccept ? "本次签到成功" : "您本次未签到") : "操作失败");
                    }
                });
            }

            @Override
            public void run() {
                if (dialog == null) {
                    this.itimeOut = timeOut;
                    dialog = new AlertDialog.Builder(PlayerActivity.this).setMessage("").setPositiveButton("签到", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rollcallAck(true);
                        }
                    }).setCancelable(false).create();
                    dialog.show();
                }
                dialog.setMessage("点名倒计时剩余秒数：" + itimeOut);
                itimeOut--;
                if (itimeOut < 0) {
                    dialog.dismiss();
                    rollcallAck(false);
                } else {
                    mHandler.postDelayed(this, 1000);
                }
            }
        });

    }

    @Override
    public void onFileShare(int cmd, String fileName, String fileUrl) {
        //cmd:1:add, 2: remove
        //TODO 应用层根据需要进行界面显示后可以调用  player的
    }

    @Override
    public void onFileShareDl(int ret, String fileUrl, String filePath) {

    }

    @Override
    public void onVideoSize(int width, int height, boolean iaAs) {
        GenseeLog.d(TAG, "onVideoSize");
        toastMsg("onVideoSize width = " + width + " height = " + height + " isAs = " + iaAs);
    }

    @Override
    public void onModuleFocus(int arg0) {

    }

    @Override
    public void onScreenStatus(boolean isAs) {
        toastMsg("onScreenStatus isAs = " + isAs);
    }

    @Override
    public void onIdcList(List<PingEntity> arg0) {
//        mViedoFragment.onIdcList(arg0);
    }

    @Override
    public void onCameraNotify(int notify) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGotoPay(PayInfo info) {
        //用info.getOrderInfo()作为支付参数调用支付宝进行支付流程;
    }

    @Override
    public void onRedBagTip(RewardResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRewordEnable(boolean arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onThirdVote(String arg0) {
        // TODO Auto-generated method stub

    }
}

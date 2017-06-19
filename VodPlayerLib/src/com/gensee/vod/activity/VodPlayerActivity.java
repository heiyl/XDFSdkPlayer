package com.gensee.vod.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gensee.R;
import com.gensee.common.ServiceType;
import com.gensee.entity.ChatMsg;
import com.gensee.entity.DocInfo;
import com.gensee.entity.InitParam;
import com.gensee.entity.QAMsg;
import com.gensee.entity.VodObject;
import com.gensee.media.PlaySpeed;
import com.gensee.media.VODPlayer;
import com.gensee.player.adapter.ViewPagerAdapter;
import com.gensee.player.LogCatService;
import com.gensee.taskret.OnTaskRet;
import com.gensee.utils.DensityUtil;
import com.gensee.utils.GenseeLog;
import com.gensee.utils.StringUtil;
import com.gensee.view.GSVideoView;
import com.gensee.vod.VodSite;
import com.gensee.vod.VodSite.OnVodListener;
import com.gensee.vod.fragment.VodChapterFragment;
import com.gensee.vod.fragment.VodChatHistoryFragment;
import com.gensee.vod.fragment.VodDocFragment;
import com.gensee.vod.model.ChapterInfo;

import java.util.ArrayList;
import java.util.List;


public class VodPlayerActivity extends FragmentActivity implements OnClickListener, OnVodListener,SeekBar.OnSeekBarChangeListener,VODPlayer.OnVodPlayListener,OnTabSelectListener {

    private String TAG = "VodPlayerActivity";

    MyOrientoinListener myOrientoinListener;
    private VodSite vodSite;

    //初始化的参数
    private String domain = "xdfjt.gensee.com";
    private String number = "65494859";
    private String account = "";
    private String accPwd = "";
    private String nickName = "android";
    private String vodPwd = "547282";
    private ServiceType serviceType = ServiceType.TRAINING;

    private String vodId;


    private VODPlayer mVodPlayer;
    private GSVideoView gsVideoView;
    private SeekBar mSeekBarPlayViedo;
    private TextView mNowTimeTextview;
    private TextView mAllTimeTextView;
    private ImageView mPauseScreenplay;

    private LinearLayout llt_bottom;
    private ImageView iv_screen;

    private int lastPostion = 0;


    SlidingTabLayout mStlTab;
    ViewPager mViewPager;

    VodDocFragment vodDocFragment;
    VodChapterFragment vodChapterFragment;
    VodChatHistoryFragment vodChatHistoryFragment;

    private ArrayList<String> mTagList = new ArrayList<String>();
    private ArrayList<Fragment> mFragmentList;
    private ViewPagerAdapter mPagerAdapter;

    public interface RESULT {
        int ON_GET_VODOBJ = 100;
        int ON_GET_CHATLIST = 101;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESULT.ON_GET_VODOBJ:
                    vodId = (String) msg.obj;
                    vodSite.getChatHistory(vodId, 1);//请求获取聊天历史
                    initPlayer();
                    break;
                case RESULT.ON_GET_CHATLIST://返回聊天历史集合
                    List<ChatMsg> list = (List<ChatMsg>) msg.obj;
                    vodChatHistoryFragment.setData(list);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        myOrientoinListener = new MyOrientoinListener(this);
        boolean autoRotateOn = (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
        //检查系统是否开启自动旋转
        if (autoRotateOn) {
            myOrientoinListener.enable();
        }

        setContentView(R.layout.xdf_voidplayer_activity);
        startLogService();

		/*
         * 代理使用，如果app有自己的代理，调用setTcpProxy， 然后在IGSOLProxy的ip和端口回调中返回相应的代理ip和代理端口，
		 * 没有代理则无需调用。此函数任何时候都可以调用。
		 */
		/*
		 * VodSite.setTcpProxy(new IProxy() {
		 * 
		 * @Override public int getProxyPort(int port) { // 返回代理端口 return port;
		 * }
		 * 
		 * @Override public String getProxyIP(String ip) { // 返回代理ip地址 return
		 * ip; } });
		 */

        /**
         * 优先调用进行组件加载，有条件的情况下可以放到application启动时候的恰当时机调用
         */
        VodSite.init(this, new OnTaskRet() {

            @Override
            public void onTaskRet(boolean arg0, int arg1, String arg2) {
                // TODO Auto-generated method stub

            }
        });
        initViews();
        bindViewPager();
        initParams();
    }

    private void initViews() {
        gsVideoView = (GSVideoView)findViewById(R.id.impvoteview);
        mStlTab = (SlidingTabLayout)findViewById(R.id.stl_tab);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);

        lastPostion = getPreferences(MODE_PRIVATE).getInt("lastPos", 0);
        mSeekBarPlayViedo = (SeekBar) findViewById(R.id.seekbarpalyviedo);
        mPauseScreenplay = (ImageView) findViewById(R.id.pauseresumeplay);
        mNowTimeTextview = (TextView) findViewById(R.id.palynowtime);
        mAllTimeTextView = (TextView) findViewById(R.id.palyalltime);

        mSeekBarPlayViedo.setOnSeekBarChangeListener(this);

        mPauseScreenplay.setOnClickListener(this);

        llt_bottom = (LinearLayout) findViewById(R.id.llt_bottom);
        iv_screen = (ImageView) findViewById(R.id.iv_screen);
        iv_screen.setOnClickListener(this);
        mVodPlayer = new VODPlayer();
        mVodPlayer.setGSVideoView(gsVideoView);


    }

    public void bindViewPager() {
        vodDocFragment = new VodDocFragment(mVodPlayer);
        vodChapterFragment = new VodChapterFragment(mVodPlayer);
        vodChatHistoryFragment = new VodChatHistoryFragment(mVodPlayer);

        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        } else {
            mFragmentList.clear();
        }
        mTagList.clear();
        mTagList.add("文档");
        mTagList.add("课节");
        mTagList.add("聊天");
        mFragmentList.add(vodDocFragment);
        mFragmentList.add(vodChapterFragment);
        mFragmentList.add(vodChatHistoryFragment);

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

    public void initParams() {
        domain = "xdfjt.gensee.com";
        number = "65494859";
        account = "";
        accPwd = "";
        nickName = "android";
        vodPwd = "547282";

        // initParam的构造
        InitParam initParam = new InitParam();
        // domain 域名
        initParam.setDomain(domain);
        //8个数字的字符串为编号
        if (number.length() == 8) {
            // 点播编号 （不是点播id）
            initParam.setNumber(number);
        } else {
            // 设置点播id，和点播编号对应，两者至少要有一个有效才能保证成功
            String liveId = number;
            initParam.setLiveId(liveId);
        }
        // 站点认证帐号 ，后台启用需要登录时必填，没启用时可以不填
        initParam.setLoginAccount(account);
        // 站点认证密码，后台启用需要登录时必填
        initParam.setLoginPwd(accPwd);
        // 点播口令，后台启用密码保护时必填且要正确填写
        initParam.setVodPwd(vodPwd);
        // 昵称 用于统计和显示，必填
        initParam.setNickName(nickName);
        // 服务类型（站点类型）
        // webcast - ST_CASTLINE
        // training - ST_TRAINING
        // meeting - ST_MEETING
        initParam.setServiceType(serviceType);
        //站点 系统设置 的 第三方集成 中直播模块 “认证“  启用时请确保”第三方K值“（你们的k值）的正确性 ；如果没有启用则忽略这个参数
        initParam.setK("");
        vodSite = new VodSite(this);
        vodSite.setVodListener(this);
        vodSite.getVodObject(initParam);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时取消监听
        myOrientoinListener.disable();
    }

    /********************* OnVodListener **************************/


    /**
     * 聊天记录 getChatHistory 响应 vodId 点播id list 聊天记录
     */
    @Override
    public void onChatHistory(String vodId, List<ChatMsg> list, int pageIndex, boolean more) {
        GenseeLog.d(TAG, "onChatHistory vodId = " + vodId + " " + list);
        mHandler.sendMessage(mHandler
                .obtainMessage(RESULT.ON_GET_CHATLIST, list));
        // ChatMsg msg;
        // msg.getContent();//消息内容
        // msg.getSenderId();//发送者用户id
        // msg.getSender();//发送者昵称
        // msg.getTimeStamp();//发送时间，单位毫秒
    }

    /**
     * 问答记录 getQaHistory 响应 list 问答记录 vodId 点播id
     */
    @Override
    public void onQaHistory(String vodId, List<QAMsg> list, int pageIndex, boolean more) {
        GenseeLog.d(TAG, "onQaHistory vodId = " + vodId + " " + list);
        if (more) {
            // 如果还有更多的历史，还可以继续获取记录（pageIndex+1）页的记录
        }
        // QAMsg msg;
        // msg.getQuestion();//问题
        // msg.getQuestId();//问题id
        // msg.getQuestOwnerId();//提问人id
        // msg.getQuestOwnerName();//提问人昵称
        // msg.getQuestTimgstamp();//提问时间 单位毫秒
        //
        // msg.getAnswer();//回复的内容
        // msg.getAnswerId();//“本条回复”的id 不是回答者的用户id
        // msg.getAnswerOwner();//回复人的昵称
        // msg.getAnswerTimestamp();//回复时间 单位毫秒
    }


    /**
     * 获取点播详情
     */
    @Override
    public void onVodDetail(VodObject vodObj) {
        GenseeLog.d(TAG, "onVodDetail " + vodObj);
        if (vodObj != null) {
            vodObj.getDuration();// 时长
            vodObj.getEndTime();// 录制结束时间 始于1970的utc时间毫秒数
            vodObj.getStartTime();// 录制开始时间 始于1970的utc时间毫秒数
            vodObj.getStorage();// 大小 单位为Byte
        }
    }

    @Override
    public void onVodErr(final int errCode) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String msg = getErrMsg(errCode);
                if (!"".equals(msg)) {
                    Toast.makeText(VodPlayerActivity.this, msg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    /**
     * getVodObject的响应，vodId 接下来可以下载后播放
     */
    @Override
    public void onVodObject(String vodId) {
        GenseeLog.d(TAG, "onVodObject vodId = " + vodId);
//		vodSite.getChatHistory(vodId, 1);//获取聊天历史记录，起始页码1
//		vodSite.getQaHistory(vodId, 1);//获取问答历史记录，起始页码1

        mHandler.sendMessage(mHandler
                .obtainMessage(RESULT.ON_GET_VODOBJ, vodId));
    }

    /**
     * 错误码处理
     *
     * @param errCode 错误码
     * @return 错误码文字表示内容
     */
    private String getErrMsg(int errCode) {
        String msg = "";
        switch (errCode) {
            case ERR_DOMAIN:
                msg = "domain 不正确";
                break;
            case ERR_TIME_OUT:
                msg = "超时";
                break;
            case ERR_SITE_UNUSED:
                msg = "站点不可用";
                break;
            case ERR_UN_NET:
                msg = "无网络请检查网络连接";
                break;
            case ERR_DATA_TIMEOUT:
                msg = "数据过期";
                break;
            case ERR_SERVICE:
                msg = "请检查填写的serviceType";
                break;
            case ERR_PARAM:
                msg = "请检查参数";
                break;
            /*case ERR_UN_INVOKE_GETOBJECT:
                msg = "请先调用getVodObject";
                break;*/
            case ERR_VOD_INTI_FAIL:
                msg = "调用getVodObject失败";
                break;
            case ERR_VOD_NUM_UNEXIST:
                msg = "点播编号不存在或点播不存在";
                break;
            case ERR_VOD_PWD_ERR:
                msg = "点播密码错误";
                break;
            case ERR_VOD_ACC_PWD_ERR:
                msg = "登录帐号或登录密码错误";
                break;
            case ERR_UNSURPORT_MOBILE:
                msg = "不支持移动设备";
                break;

            default:
                break;
        }
        return msg;
    }


    private void startLogService() {
        startService(new Intent(this, LogCatService.class));
    }

    private void stopLogService() {
        stopService(new Intent(this, LogCatService.class));
    }


    //点播播放-------------------------------------------

    private static final int DURITME = Toast.LENGTH_LONG;
    private static final String DURATION = "DURATION";
    private boolean isTouch = false;
    private int VIEDOPAUSEPALY = 0;
    private int speedItem = 0;


    interface MSG {
        int MSG_ON_INIT = 1;
        int MSG_ON_STOP = 2;
        int MSG_ON_POSITION = 3;
        int MSG_ON_VIDEOSIZE = 4;
        int MSG_ON_PAGE = 5;
        int MSG_ON_SEEK = 6;
        int MSG_ON_AUDIOLEVEL = 7;
        int MSG_ON_ERROR = 8;
        int MSG_ON_PAUSE = 9;
        int MSG_ON_RESUME = 10;
    }

    protected Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG.MSG_ON_INIT:

                    int max = msg.getData().getInt(DURATION);
                    mSeekBarPlayViedo.setMax(max);
                    max = max / 1000;
                    GenseeLog.i(TAG, "MSG_ON_INIT duration = " + max);
                    mAllTimeTextView.setText(getTime(max));
                    mVodPlayer.seekTo(lastPostion);
                    mPauseScreenplay.setImageResource(R.drawable.icon_pause);
                    if(vodChapterFragment != null && msg.obj != null) {
                        vodChapterFragment.setData(msg.obj);
                    }
                    break;
                case MSG.MSG_ON_STOP:

                    break;
                case MSG.MSG_ON_VIDEOSIZE:

                    break;
                case MSG.MSG_ON_PAGE:
                    int position = (Integer)msg.obj;
                    break;
                case MSG.MSG_ON_PAUSE:
                    VIEDOPAUSEPALY = 1;
                    mPauseScreenplay.setImageResource(R.drawable.icon_play);
                    break;
                case MSG.MSG_ON_RESUME:
                    VIEDOPAUSEPALY = 0;
                    mPauseScreenplay.setImageResource(R.drawable.icon_pause);
                    break;
                case MSG.MSG_ON_POSITION:
                    if (isTouch) {
                        return;
                    }
                case MSG.MSG_ON_SEEK:
                    isTouch = false;
                    int anyPosition = (Integer) msg.obj;
                    mSeekBarPlayViedo.setProgress(anyPosition);
                    anyPosition = anyPosition / 1000;
                    mNowTimeTextview.setText(getTime(anyPosition));
                    break;
                case MSG.MSG_ON_AUDIOLEVEL:

                    break;
                case MSG.MSG_ON_ERROR:

                    rePlay();

                    int errorCode = (Integer) msg.obj;
                    switch (errorCode) {
                        case ERR_PAUSE:
                            Toast.makeText(getApplicationContext(), "暂停失败", DURITME)
                                    .show();
                            break;
                        case ERR_PLAY:
                            Toast.makeText(getApplicationContext(), "播放失败", DURITME)
                                    .show();
                            break;
                        case ERR_RESUME:
                            Toast.makeText(getApplicationContext(), "恢复失败", DURITME)
                                    .show();
                            break;
                        case ERR_SEEK:
                            Toast.makeText(getApplicationContext(), "进度变化失败", DURITME)
                                    .show();
                            break;
                        case ERR_STOP:
                            Toast.makeText(getApplicationContext(), "停止失败", DURITME)
                                    .show();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private void rePlay() {
        boolean ret = mVodPlayer.stop();
        mSeekBarPlayViedo.setMax(0);
        isTouch = false;
        String vodIdOrLocalPath = getVodIdOrLocalPath();
        //重新播放时  将速度恢复为正常速度，如果要保持上一次速度播放，设置为上一次速度
        speedItem = 0;
        mVodPlayer.setSpeed(PlaySpeed.SPEED_NORMAL, null);
        mVodPlayer.setGSVideoView(gsVideoView);
        mVodPlayer.play(vodIdOrLocalPath, this, "",false);
    }

    private String getTime(int time) {
        return String.format("%02d", time / 3600) + ":"
                + String.format("%02d", time % 3600 / 60) + ":"
                + String.format("%02d", time % 3600 % 60);
    }


    private void initPlayer() {

        String vodIdOrLocalPath = getVodIdOrLocalPath();
        if (vodIdOrLocalPath == null) {
            Toast.makeText(this, "路径不对", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mVodPlayer == null) {
            mVodPlayer = new VODPlayer();
            mVodPlayer.setGSVideoView(gsVideoView);
            mVodPlayer.play(vodIdOrLocalPath, this, "",false);
        }else {
            mVodPlayer.play(vodIdOrLocalPath, this, "",false);
        }
    }

    private String getVodIdOrLocalPath() {
        String localpath = getIntent().getStringExtra("play_path");
        GenseeLog.d(TAG, "path = " + localpath + " vodId = " + vodId);
        String vodIdOrLocalPath = null;
        if (!StringUtil.isEmpty(localpath)) {
            vodIdOrLocalPath = localpath;
        } else if (!StringUtil.isEmpty(vodId)) {
            vodIdOrLocalPath = vodId;
        }
        return vodIdOrLocalPath;
    }

    @Override
    public void onInit(int result, boolean haveVideo, int duration,
                       List<DocInfo> docInfos) {
        if (lastPostion >= duration-1000) {
            lastPostion = 0;
        }
        Message message = new Message();
        message.what = MSG.MSG_ON_INIT;
        message.obj = docInfos;
        Bundle bundle = new Bundle();
        bundle.putInt(DURATION, duration);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onPlayStop() {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_STOP, 0));
    }

    @Override
    public void onPosition(int position) {
        GenseeLog.d(TAG, "onPosition pos = " + position);
        lastPostion = position;
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_POSITION,
                position));
    }

    @Override
    public void onVideoSize(int position, int videoWidth, int videoHeight) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_VIDEOSIZE, 0));
    }


    @Override
    public void onSeek(int position) {
        myHandler.sendMessage(myHandler
                .obtainMessage(MSG.MSG_ON_SEEK, position));
    }

    @Override
    public void onAudioLevel(int level) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_AUDIOLEVEL,
                level));
    }

    @Override
    public void onError(int errCode) {
        myHandler.sendMessage(myHandler
                .obtainMessage(MSG.MSG_ON_ERROR, errCode));
    }

    @Override
    public void onPlayPause() {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_PAUSE, 0));
    }

    @Override
    public void onPlayResume() {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_RESUME, 0));
    }



    @Override
    public void onPageSize(int position, int w, int h) {
        //文档翻页切换，开始显示
        myHandler.sendMessage(myHandler
                .obtainMessage(MSG.MSG_ON_PAGE, position));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouch = true;

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (null != mVodPlayer) {
            int pos = seekBar.getProgress();
            GenseeLog.d(TAG, "onStopTrackingTouch pos = " + pos);
            mVodPlayer.seekTo(pos);

        }

    }

    @Override
    public void onClick(View currenView) {
        if (currenView.getId() == R.id.stopveidoplay) {
            boolean ret = mVodPlayer.stop();
            mSeekBarPlayViedo.setMax(0);
            Toast.makeText(this, ret ? "操作成功" : "操作失败", DURITME).show();
        } else if (currenView.getId() == R.id.replayvedioplay) {
            isTouch = false;
            String vodIdOrLocalPath = getVodIdOrLocalPath();
            if (vodIdOrLocalPath == null) {
                Toast.makeText(this, "路径不对", Toast.LENGTH_SHORT).show();
                return;
            }
            //重新播放时  将速度恢复为正常速度，如果要保持上一次速度播放，设置为上一次速度
            speedItem = 0;
            mVodPlayer.setSpeed(PlaySpeed.SPEED_NORMAL, null);
            mVodPlayer.setGSVideoView(gsVideoView);
            mVodPlayer.play(vodIdOrLocalPath, this, "",false);

        } else if (currenView.getId() == R.id.pauseresumeplay) {
            if (VIEDOPAUSEPALY == 0) {
                mVodPlayer.pause();
            } else if (VIEDOPAUSEPALY == 1) {
                mVodPlayer.resume();
            }
        } else if(currenView.getId() == R.id.speed){
            switchSpeed();
        }else if(currenView.getId() == R.id.iv_screen) {
            int orientation = getRequestedOrientation();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    || orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
            } else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
            }
            setRequestedOrientation(orientation);
        }
    }

    /**
     * 变速播放
     */
    private void switchSpeed() {
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(R.array.speeds, speedItem,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                speedItem = which;
                                PlaySpeed ps = PlaySpeed.SPEED_NORMAL;
                                switch (which) {
                                    case 0:
                                        ps = PlaySpeed.SPEED_NORMAL;
                                        break;
                                    case 1:
                                        ps = PlaySpeed.SPEED_125;
                                        break;
                                    case 2:
                                        ps = PlaySpeed.SPEED_150;
                                        break;
                                    case 3:
                                        ps = PlaySpeed.SPEED_175;
                                        break;
                                    case 4:
                                        ps = PlaySpeed.SPEED_200;
                                        break;
                                    case 6:
                                        ps = PlaySpeed.SPEED_250;
                                        break;
                                    case 7:
                                        ps = PlaySpeed.SPEED_300;
                                        break;
                                    case 8:
                                        ps = PlaySpeed.SPEED_350;
                                        break;
                                    case 9:
                                        ps = PlaySpeed.SPEED_400;
                                        break;

                                    default:
                                        break;
                                }
                                mVodPlayer.setSpeed(ps, null);
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    private void stopPlay() {
        if (mVodPlayer != null) {
            mVodPlayer.stop();
        }
    }

    private void release() {
        stopPlay();
        if (mVodPlayer != null) {
            mVodPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        stopLogService();
        release();
        super.onBackPressed();
    }

    @Override
    public void onCaching(boolean isCatching) {
        // TODO Auto-generated method stub

    }

    private class ChapterListAdapter extends BaseAdapter {
        private List<ChapterInfo> pageList;
        private int selectedPosition = 0;

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
//            lvChapterList.setSelection(position);
        }

        public ChapterListAdapter() {
            pageList = new ArrayList<ChapterInfo>();
        }

        public void notifyData(List<ChapterInfo> pageList) {
            this.pageList.clear();
            this.pageList.addAll(pageList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public Object getItem(int position) {
            return pageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.doc_list_item_ly, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.init((ChapterInfo) getItem(position), position);
            return convertView;
        }

        private class ViewHolder {
            private TextView tvChapter;
            private TextView tvTitle;
            private TextView tvTime;
            private LinearLayout lyChapter;

            private String getChapterTime(long time) {
                return String.format("%02d", time / (3600 * 1000))
                        + ":"
                        + String.format("%02d", time % (3600 * 1000)
                        / (60 * 1000))
                        + ":"
                        + String.format("%02d", time % (3600 * 1000)
                        % (60 * 1000) / 1000);
            }

            public ViewHolder(View view) {
                tvChapter = (TextView) view.findViewById(R.id.chapter_title);
                tvTitle = (TextView) view.findViewById(R.id.doc_title);
                tvTime = (TextView) view.findViewById(R.id.chapter_time);
                lyChapter = (LinearLayout) view.findViewById(R.id.chapter_ly);
            }

            public void init(ChapterInfo chapterInfo, int position) {
                tvChapter.setText(chapterInfo.getPageTitle());
                tvTime.setText(getChapterTime(chapterInfo.getPageTimeStamp()));
                tvTitle.setText(chapterInfo.getDocName());

                if (selectedPosition == position) {
                    lyChapter.setBackgroundResource(R.color.red);
                } else {
                    lyChapter.setBackgroundResource(R.color.transparent);
                }
            }
        }

    }

    @Override
    public void onVideoStart() {

    }

    @Override
    public void onChat(List<ChatMsg> arg0) {
        //ChatMsg msg = chatMsgs.get(0);
        // msg.getRichText()//富文本
        // msg.getSender()//发送者名称
        // msg.getSenderId()//发送者id
        // msg.getContent()//纯文本
        // msg.getTimeStamp()//相对与播放开始的时间  单位毫秒
    }

    @Override
    public void onDocInfo(List<DocInfo> arg0) {
        // TODO Auto-generated method stub

    }

    /**
     *  msg 的时候，id代表要删除的聊天消息id，user的时候，代表用户id，强转为long型后进行用户id匹配删除该id说有的聊天消息
     * @param type msg(CHATCENSOR_MSG) / user(CHATCENSOR_USER)
     * @param id   msgId/userId
     */
    @Override
    public void onChatCensor(String type, String id) {

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
        ViewGroup.LayoutParams params = gsVideoView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        gsVideoView.setLayoutParams(params);
    }

    private void videoNormalScreen() {
        llt_bottom.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = gsVideoView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = DensityUtil.dip2px(this,281);
        gsVideoView.setLayoutParams(params);
    }


    /**
     * 手机的旋转监听类
     */
    class MyOrientoinListener extends OrientationEventListener {

        public MyOrientoinListener(Context context) {
            super(context);
        }

        public MyOrientoinListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            Log.d(TAG, "orention" + orientation);
            int screenOrientation = getResources().getConfiguration().orientation;
            if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    Log.d(TAG, "设置竖屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } else if (orientation > 225 && orientation < 315) { //设置横屏
                Log.d(TAG, "设置横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
                Log.d(TAG, "反向横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            } /*else if (orientation > 135 && orientation < 225) {
                Log.d(TAG, "反向竖屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
            }*/
        }
    }


}

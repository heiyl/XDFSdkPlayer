package com.gensee.player.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.R;
import com.gensee.player.model.PlGroup;
import com.gensee.player.model.PlResponse;
import com.gensee.utils.API;
import com.gensee.utils.Util;
import com.gensee.view.EditTextWithDel;
import com.gensee.view.SlideUp;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.Response;


/**
 * wuxu@xdf.cn
 */
public class PlSlidUpActivity extends Activity implements View.OnClickListener, IWXAPIEventHandler {

    protected ImageView iv_close;
    protected SlideUp slideUp;
    protected View dim;
    protected View rl_slideView;
    /**
     * 当前页面是否可关闭
     */
    protected boolean canClose = false;
    private View rl_root;
    private TextView tv_group_title;
    private TextView tv_group_msg;
    private ImageView iv_group_qrcode;

    private Button btn_commit;

    private View ll_group, ll_advisory;

    private EditTextWithDel et_user_phone;

    private static final String SLID_UP_TYPE = "slid_up_type";
    private static final int SLID_UP_GROUP = 1;
    private static final int SLID_UP_ADVISORY = 2;
    private static final int SLID_UP_HELP = 3;

    private PlGroup.Group mGroup = null;
    private String userName = "wuxu";
    private String mobile = "13581525298";
    private String courseId = "4344";
    private String lessonId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_slid_up);

        initData();
        initView();
        bindListener();
        bindData();
    }

    private void bindData() {
        if (mGroup != null) {
            tv_group_title.setText(mGroup.getGroup_number());
            tv_group_msg.setText(mGroup.getGroup_desc());
//            iv_group_qrcode.setText(mGroup.getGroup_number());
        }
    }


    private void initData() {
        mGroup = (PlGroup.Group) getIntent().getExtras().getSerializable("group");
        regToWX();
    }

    private void initView() {
        iv_close = (ImageView) findViewById(R.id.iv_close);
        rl_slideView = findViewById(R.id.rl_slideView);
        dim = findViewById(R.id.dim);
        rl_root = findViewById(R.id.rl_root);

        slideUp = new SlideUp.Builder(rl_slideView)
                .withListeners(new SlideUp.Listener() {
                    @Override
                    public void onSlide(float percent) {

                        dim.setAlpha(1 - (percent / 100));
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                            if (canClose) {
                                PlSlidUpActivity.this.finish();
                            }
                        }
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .build();

        slideUp.setHideKeyboardWhenDisplayed(true);

        openViewHandler.sendMessageDelayed(openViewHandler.obtainMessage(0), 300);

        rl_root.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent));

        tv_group_title = (TextView) findViewById(R.id.tv_group_title);
        tv_group_msg = (TextView) findViewById(R.id.tv_group_msg);
        iv_group_qrcode = (ImageView) findViewById(R.id.iv_group_qrcode);

        btn_commit = (Button) findViewById(R.id.btn_commit);
        ll_group = findViewById(R.id.ll_group);
        ll_advisory = findViewById(R.id.ll_advisory);

        et_user_phone = (EditTextWithDel) findViewById(R.id.et_user_phone);

        int type = getIntent().getExtras().getInt(SLID_UP_TYPE);

        setViewType(type);
    }

    private void setViewType(int type) {
        ll_group.setVisibility(type == SLID_UP_GROUP ? View.VISIBLE : View.GONE);
        ll_advisory.setVisibility(type == SLID_UP_ADVISORY ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.iv_close) {
            finish();
        } else if (id == R.id.btn_commit) {

            String userPhone = et_user_phone.getText().toString().trim();
            if (TextUtils.isEmpty(userPhone)) {
                Toast.makeText(PlSlidUpActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.isPhone(userPhone))
            {
                Toast.makeText(PlSlidUpActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            commitAdvisory(userPhone);
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        if (slideUp != null) {
            slideUp.hide();
        }
    }

    private Handler openViewHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (0 == msg.what) {
                slideUp.show();
                canClose = true;
            }
        }
    };

    private static final String WX_APP_ID = "wx8a15cbb7c58a948d";
    private IWXAPI wxapi;
    private static final int THUMB_SIZE = 75;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    private void regToWX() {
        wxapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        wxapi.registerApp(WX_APP_ID);
    }

    private void bindListener() {
        iv_close.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        iv_group_qrcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (!wxapi.isWXAppInstalled()) {//是否安装了微信
                    Toast.makeText(PlSlidUpActivity.this, "尚未安装微信，请先安装微信", Toast.LENGTH_SHORT).show();
                    return false;
                }
                sendImg();
                return false;
            }
        });
    }

    /**
     * 课程咨询
     */
    private void commitAdvisory(String userPhone) {

        RequestParams params = new RequestParams();
        params.addFormDataPart("userName", userName);
        params.addFormDataPart("mobile", userPhone);
        params.addFormDataPart("courseId", courseId);
        params.addFormDataPart("lessonId", lessonId);
        params.addFormDataPart("appKey", API.ESTUDY_APP_KEY);
        params.addFormDataPart("channelID", API.ESTUDY_CHANNEL_ID);
        Log.e("commitAdvisory ", API.COMMIT_PHONE_NUMBER_URL + "?" + params);

        HttpRequest.get(API.COMMIT_PHONE_NUMBER_URL, params, new BaseHttpRequestCallback<PlResponse>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);
                Log.e("PlSlidUpActivity", response);
            }

            @Override
            protected void onSuccess(PlResponse resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    if (resp.getStatus()) {
                        Toast.makeText(PlSlidUpActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PlSlidUpActivity.this, resp.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PlSlidUpActivity.this, "提交失败，请稍候重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                Toast.makeText(PlSlidUpActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        wxapi.handleIntent(intent, this);
    }

    // ΢�ŷ������󵽵�����Ӧ��ʱ����ص����÷���
    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    // ������Ӧ�÷��͵�΢�ŵ�����������Ӧ�������ص����÷���
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        Toast.makeText(this, "baseresp.getType = " + resp.getType(), Toast.LENGTH_SHORT).show();

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    //TODO    发送图片至微信好友   缺测试数据
    private void sendImg() {
//        String imagePath = SDCARD_ROOT + "/test.png";
//        WXImageObject imgObj = new WXImageObject();
//        imgObj.setImagePath(imagePath);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.default_qrcode);

        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        msg.description = "图片描述";

//        Bitmap thumbBmp = BitmapFactory.decodeFile(imagePath);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = com.gensee.utils.Util.bmpToByteArray(thumbBmp, true);
        msg.title = "abc-title";
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "img" + String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        wxapi.sendReq(req);
    }

}

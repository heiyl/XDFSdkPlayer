package com.gensee.player.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gensee.R;
import com.gensee.player.model.PlResponse;
import com.gensee.utils.GsonUtils;
import com.gensee.view.SlideUp;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * wuxu@xdf.cn
 */
public class PlSlidUpActivity extends Activity implements View.OnClickListener , IWXAPIEventHandler {

    protected ImageView iv_close;
    protected SlideUp slideUp;
    protected View dim;
    protected View rl_slideView;
    /**
     * 当前页面是否可关闭
     */
    protected boolean canClose = false;
    private View rl_root;

    private Button btn_commit;

    private View ll_group, ll_advisory;
    private ImageView iv_qrcode;

    private static final String SLID_UP_TYPE = "slid_up_type";
    private static final int SLID_UP_GROUP = 1;
    private static final int SLID_UP_ADVISORY = 2;
    private static final int SLID_UP_HELP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_slid_up);

        initData();
        initView();
        bindListener();
    }

    private void initData() {
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
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

        iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);

        btn_commit = (Button) findViewById(R.id.btn_commit);

        ll_group = findViewById(R.id.ll_group);
        ll_advisory = findViewById(R.id.ll_advisory);

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
            commitAdvisory();
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

    private static final String WX_APP_ID = "";
    private IWXAPI wxapi;

    private void regToWX()
    {
        wxapi = WXAPIFactory.createWXAPI(this,WX_APP_ID,true);
        wxapi.registerApp(WX_APP_ID);
    }

    private void bindListener() {
        iv_close.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        iv_qrcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(PlSlidUpActivity.this, "分享至微信", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private final String COMMIT_ADVISORY_URL = "http://estudy.staff.xdf.cn/api.php/CourseNote/index?appKey=CE804942A6D34511BBF4A935E0F7BF11&channelID=1006";
    private String userName;
    private String mobile;
    private String courseId;
    private String lessonId;

    private void commitAdvisory() {

        RequestParams params = new RequestParams();
        params.addFormDataPart("userName", "wuxu");
        params.addFormDataPart("mobile", "13581525298");
        params.addFormDataPart("courseId", "1");
        params.addFormDataPart("lessonId", "2");
        Log.e("commitAdvisory ", COMMIT_ADVISORY_URL + "?" + params);

        HttpRequest.get(COMMIT_ADVISORY_URL, params, new BaseHttpRequestCallback() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);

                PlResponse resp = GsonUtils.getEntity(response, com.gensee.player.model.PlResponse.class);
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
                Log.e("PlSlidUpActivity", response);
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
}

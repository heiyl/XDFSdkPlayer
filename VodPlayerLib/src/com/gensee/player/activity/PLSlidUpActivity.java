package com.gensee.player.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gensee.playerdemo.R;
import com.gensee.view.SlideUp;

/**
 * wuxu@xdf.cn
 */
public class PlSlidUpActivity extends Activity implements View.OnClickListener {

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

    private View ll_group, ll_advisory;//, ll_help;

    private static final String SLID_UP_TYPE = "slid_up_type";
    private static final int SLID_UP_GROUP = 1;
    private static final int SLID_UP_ADVISORY = 2;
    private static final int SLID_UP_HELP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_slid_up);

        initView();
        bindListener();
    }

    private void initView() {
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

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


        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);


        ll_group = findViewById(R.id.ll_group);
        ll_advisory = findViewById(R.id.ll_advisory);
//        ll_help = findViewById(R.id.ll_help);

        int type = getIntent().getExtras().getInt(SLID_UP_TYPE);

        setViewType(type);
    }

    private void setViewType(int type) {
        ll_group.setVisibility(type == SLID_UP_GROUP ? View.VISIBLE : View.GONE);
        ll_advisory.setVisibility(type == SLID_UP_ADVISORY ? View.VISIBLE : View.GONE);
//        ll_help.setVisibility(type == SLID_UP_HELP ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.iv_close) {
            finish();
        } else if (id == R.id.btn_commit) {
            Toast.makeText(PlSlidUpActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
            finish();
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

    private void bindListener() {
    }
}

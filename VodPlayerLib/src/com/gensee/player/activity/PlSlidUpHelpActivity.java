package com.gensee.player.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.gensee.R;
import com.gensee.view.SlideUp;

public class PlSlidUpHelpActivity extends PlSlidUpActivity {

    private View rl_slideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pl_slid_up_help);

        rl_slideView = findViewById(R.id.rl_slideView);
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
                                PlSlidUpHelpActivity.this.finish();
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

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.iv_close) {
            finish();
        }
    }

}

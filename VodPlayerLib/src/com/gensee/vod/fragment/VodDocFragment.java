package com.gensee.vod.fragment;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gensee.R;
import com.gensee.media.VODPlayer;
import com.gensee.pdu.AnnoFreepenEx;
import com.gensee.pdu.AnnoPointerEx;
import com.gensee.player.fragement.BaseFragment;
import com.gensee.view.GSDocViewGx;

@SuppressLint("ValidFragment")
public class VodDocFragment extends BaseFragment {

    private VODPlayer mPlayer;
    private View mView;
    private GSDocViewGx mGlDocView;

    public VodDocFragment(VODPlayer player) {

        this.mPlayer = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initView();
        bindListener();
        return mView;
    }

    private void initView() {
        mView = View.inflate(mActivity, R.layout.imdoc, null);
        mGlDocView = (GSDocViewGx) mView.findViewById(R.id.imGlDocView);
        mPlayer.setGSDocViewGx(mGlDocView);

        mGlDocView.setBackgroundColor(getResources().getColor(R.color.app_color_backgroud));

        AnnoFreepenEx.setFreepenExDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.freepen_ex)));
        AnnoPointerEx.setPointerCircleDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_pointer)));
        AnnoPointerEx.setPointerCrossDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_arrow)));
    }

    private void bindListener() {
    }

}

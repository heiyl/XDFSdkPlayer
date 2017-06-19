package com.gensee.vod.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gensee.R;
import com.gensee.media.VODPlayer;
import com.gensee.player.Player;
import com.gensee.view.GSDocViewGx;

@SuppressLint("ValidFragment")
public class VodDocFragment extends Fragment {

	private VODPlayer mPlayer;
	private View mView;
	//private GSDocView mGSDocView;
	private GSDocViewGx mGlDocView;

	public VodDocFragment(VODPlayer player) {

		this.mPlayer = player;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mView = inflater.inflate(R.layout.imdoc, null);
		mGlDocView = (GSDocViewGx) mView.findViewById(R.id.imGlDocView);
		mPlayer.setGSDocViewGx(mGlDocView);
//		AnnoFreepenEx.setFreepenExDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.freepen_ex)));
//		AnnoPointerEx.setPointerCircleDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_pointer)));
//		AnnoPointerEx.setPointerCrossDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_arrow)));
		return mView;

	}
}

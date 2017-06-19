package com.gensee.player.fragement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gensee.pdu.AnnoFreepenEx;
import com.gensee.pdu.AnnoPointerEx;
import com.gensee.player.Player;
import com.gensee.player.activity.PlSlidUpActivity;
import com.gensee.player.activity.PlSlidUpHelpActivity;
import com.gensee.playerdemo.R;
import com.gensee.view.GSDocViewGx;

@SuppressLint("ValidFragment")
public class DocFragment extends Fragment {

	private Player mPlayer;
	private View mView;
	//private GSDocView mGSDocView;
	private GSDocViewGx mGlDocView;

	private LinearLayout mLlGroup;
	private LinearLayout mLlAdvisory;
	private LinearLayout mLlHelp;

	private static final String SLID_UP_TYPE = "slid_up_type";
	private static final int SLID_UP_GROUP = 1;
	private static final int SLID_UP_ADVISORY = 2;
	private static final int SLID_UP_HELP = 3;

	public DocFragment(Player player) {

		this.mPlayer = player;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.imdoc, null);
		mGlDocView = (GSDocViewGx) mView.findViewById(R.id.imGlDocView);
		mPlayer.setGSDocViewGx(mGlDocView);
		AnnoFreepenEx.setFreepenExDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.freepen_ex)));
		AnnoPointerEx.setPointerCircleDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_pointer)));
		AnnoPointerEx.setPointerCrossDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_arrow)));
		initView();
		bindListener();
		return mView;
	}

	private void initView() {
		mLlGroup = (LinearLayout) mView.findViewById(R.id.ll_group);
		mLlAdvisory = (LinearLayout) mView.findViewById(R.id.ll_advisory);
		mLlHelp = (LinearLayout) mView.findViewById(R.id.ll_help);
	}

	private void bindListener() {
		mLlGroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), PlSlidUpActivity.class);
				intent.putExtra(SLID_UP_TYPE, SLID_UP_GROUP);
				startActivity(intent);
			}
		});

		mLlAdvisory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), PlSlidUpActivity.class);
				intent.putExtra(SLID_UP_TYPE, SLID_UP_ADVISORY);
				startActivity(intent);
			}
		});

		mLlHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), PlSlidUpHelpActivity.class);
				intent.putExtra(SLID_UP_TYPE, SLID_UP_HELP);
				startActivity(intent);
			}
		});
	}
}

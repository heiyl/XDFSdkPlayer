package com.gensee.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gensee.entity.UserInfo;
import com.gensee.player.Player;
import com.gensee.playerdemo.R;
import com.gensee.view.CustomGSImplChatView;
import com.gensee.view.GSImplChatView;

@SuppressLint("ValidFragment")
public class ChatFragment extends Fragment {

	private Player mPlayer;
	private CustomGSImplChatView mGSImplChatView;
	private View mView;
	private UserInfo mUserInfo;

	public ChatFragment(Player player) {

		this.mPlayer = player;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.imchat, null);
		mGSImplChatView = (CustomGSImplChatView) mView.findViewById(R.id.impchatview);
		mGSImplChatView.setPlayer(mPlayer);
	//	mGSImplChatView.setChatTarget(mUserInfo);
//		mPlayer.setGSChatView(mGSImplChatView);
		return mView;
	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
	}

	public void setChatPerson(UserInfo userInfo) {
		
		mUserInfo = userInfo;
		if (mGSImplChatView != null) {
			// mGSImplChatView.setChatTarget(userInfo);
			mPlayer.setGSChatView(mGSImplChatView);
		}
	}

}

package com.gensee.fragement;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.entity.PingEntity;
//import com.gensee.entity.Reward;
import com.gensee.player.Player;
import com.gensee.player.VideoRate;
import com.gensee.playerdemo.R;
import com.gensee.view.GSVideoView;

@SuppressLint("ValidFragment")
public class ViedoFragment extends Fragment implements OnClickListener {

	private Player mPlayer;
	private View mView;
	private GSVideoView mGSViedoView;
	private TextView txtVideo, txtAudio,txtMic,txtHand,txtIdc,txtReword;
	private Spinner spinnerRate;
	private Runnable handRun = null;
	private List<PingEntity>idcs;
	public ViedoFragment(Player player) {
		this.mPlayer = player;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.imviedo, null);
		txtVideo = (TextView) mView.findViewById(R.id.txtVideo);
		txtAudio = (TextView) mView.findViewById(R.id.txtAudio);
		txtMic = (TextView) mView.findViewById(R.id.txtMic);
		txtHand = (TextView) mView.findViewById(R.id.txtHand);
		txtHand.setText("举手");
		
		spinnerRate = (Spinner) mView.findViewById(R.id.spinnerRate);
		initRateSelectView();
		
		txtIdc =  (TextView) mView.findViewById(R.id.txtIdc);
		txtReword =  (TextView) mView.findViewById(R.id.txtReword);
		
		mGSViedoView = (GSVideoView) mView.findViewById(R.id.imvideoview);
//		mGSViedoView.setRenderMode(RenderMode.RM_FILL_CENTER_CROP);
		mGSViedoView.setOnClickListener(this);
		txtVideo.setOnClickListener(this);
		txtAudio.setOnClickListener(this);
		txtMic.setOnClickListener(this);
		txtHand.setOnClickListener(this);
		txtIdc.setOnClickListener(this);
		txtReword.setOnClickListener(this);

		mPlayer.setGSVideoView(mGSViedoView);
		return mView;
	}
	
	public void onJoin(boolean isJoined) {
		if(txtAudio != null){
			txtAudio.setEnabled(isJoined);
			txtVideo.setEnabled(isJoined);
			txtHand.setEnabled(isJoined);
			txtIdc.setEnabled(isJoined);
			txtReword.setEnabled(isJoined);
		}
	}
	

	public void setVideoViewVisible(boolean bVisible) {
		if (isAdded()) {
			if (bVisible) {
				mGSViedoView.setVisibility(View.VISIBLE);
			} else {
				mGSViedoView.setVisibility(View.GONE);
			}
		}
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imvideoview) {
			int orientation = getActivity().getRequestedOrientation();
			if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
					|| orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
			} else {
				orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
			}
			getActivity().setRequestedOrientation(orientation);

		} else if (v.getId() == R.id.txtAudio) {
			if (mPlayer != null) {
				// isSelect 代表关闭状态，默认非关闭状态 即false
				if (v.isSelected()) {
					mPlayer.audioSet(false);
					v.setSelected(false);
					txtAudio.setText(R.string.audio_close);

				} else {
					mPlayer.audioSet(true);
					v.setSelected(true);
					txtAudio.setText(R.string.audio_open);
				}
			}

		} else if (v.getId() == R.id.txtVideo) {
			if (mPlayer != null) {
				// isSelect 代表关闭状态，默认非关闭状态 即false
				if (v.isSelected()) {
					mPlayer.videoSet(false);
					v.setSelected(false);
					txtVideo.setText(R.string.video_close);

				} else {
					mPlayer.videoSet(true);
					v.setSelected(true);
					txtVideo.setText(R.string.video_open);
				}
			}

		} else if (v.getId() == R.id.txtMic) {
			if (mPlayer != null) {
				mPlayer.openMic(getActivity(), false, null);
				mPlayer.inviteAck((Integer) v.getTag(), false, null);
			}
		} else if (v.getId() == R.id.txtHand) {
			if (handRun != null) {
				txtHand.removeCallbacks(handRun);
			}
			if (!v.isSelected()) {
				mPlayer.handUp(true, null);
				txtHand.setSelected(true);
				handRun = new Runnable() {
					private int time = 60;

					@Override
					public void run() {
						txtHand.setText("举手(" + time + ")");
						time--;
						if (time < 0) {
							handDown();
						} else {
							txtHand.postDelayed(this, 1000);
						}
					}
				};
				txtHand.post(handRun);
			} else {
				handDown();
			}
		} else if (v.getId() == R.id.txtIdc) {
			int size = idcs == null ? 0 : idcs.size();
			if (size > 0) {
				CharSequence[] sequences = new CharSequence[size];
				String curIdc = mPlayer.getCurIdc();
				int itemindex = -1;
				for (int i = 0; i < size; i++) {
					sequences[i] = idcs.get(i).getName() + "(" + idcs.get(i).getDescription() + ")";
					if (idcs.get(i).getIdcId().equals(curIdc)) {
						itemindex = i;
					}
				}

				alert(sequences, itemindex, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPlayer.setIdcId(idcs.get(which).getCode(), null);
						dialog.dismiss();
					}
				});
			}
		}
	}
	
	private void handDown(){
		txtHand.setText("举手");
		mPlayer.handUp(false, null);
		txtHand.setSelected(false);
	}

	public void onMicColesed() {
		txtMic.setVisibility(View.GONE);
	}

	public void onMicOpened(int inviteMediaType) {
		txtMic.setTag(inviteMediaType);
		txtMic.setVisibility(View.VISIBLE);
	}
	
	private void initRateSelectView(){
		List<String> list = new ArrayList<String>();
		list.add(getString(R.string.video_rate_nor));
		list.add(getString(R.string.video_rate_low));

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_layout, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRate.setAdapter(adapter);
		spinnerRate.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(mPlayer != null){
					switch (arg2) {
					case 0:
						mPlayer.switchRate(VideoRate.RATE_NORMAL, null);
						break;
					case 1:
						mPlayer.switchRate(VideoRate.RATE_LOW, null);
						break;
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	private void alert(CharSequence []items,int checkedItem,DialogInterface.OnClickListener listener){
		new AlertDialog.Builder(getActivity()).setSingleChoiceItems(items, checkedItem, listener).create().show();
	}

	public void onIdcList(List<PingEntity> arg0) {
		this.idcs = arg0;
	}
}
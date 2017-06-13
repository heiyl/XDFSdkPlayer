//package com.gensee.playerdemo;
//
//import java.util.List;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.view.View;
//
//import com.gensee.common.ServiceType;
//import com.gensee.entity.ChatMsg;
//import com.gensee.entity.DocInfo;
//import com.gensee.entity.InitParam;
//import com.gensee.entity.PingEntity;
//import com.gensee.entity.QAMsg;
//import com.gensee.entity.UserInfo;
//import com.gensee.entity.VodObject;
//import com.gensee.media.GSOLPlayer.OnOLPlayListener;
//import com.gensee.media.VODPlayer;
//import com.gensee.player.OnPlayListener;
//import com.gensee.player.Player;
//import com.gensee.player.OnPlayListener.MicNotify;
//import com.gensee.utils.GenseeLog;
//import com.gensee.videoparam.IVideoCoreInterface;
//import com.gensee.videoparam.VideoParam;
//import com.gensee.view.GSVideoView;
//import com.gensee.view.ILocalVideoView;
//import com.gensee.view.GSVideoView.RenderMode;
//import com.gensee.view.LocalVideoViewEx;
//import com.gensee.vod.VodSite;
//import com.gensee.vod.VodSite.OnVodListener;
//
//public class VideoActivity extends Activity implements IVideoCoreInterface{
//
//	protected static final String TAG = "VideoActivity";
//	private GSVideoView videoView;
//	private LocalVideoViewEx localVideoViewEx;
//	private 	Player mPlayer;
//	
//	private int inviteMediaType;
//	private AlertDialog dialog;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.video_frame_layout);
//		videoView = (GSVideoView) findViewById(R.id.gsVideoView);
//		videoView.setRenderMode(RenderMode.RM_FILL_CENTER_CROP);
////		videoView.setVisibility(View.GONE);
//		localVideoViewEx = (LocalVideoViewEx) findViewById(R.id.localvideoview);
////		localVideoViewEx.setOrientation(ILocalVideoView.ORIENTATION_PORTRAIT);
////		localVideoViewEx.setHardEncode(true);
//		
//		InitParam p = new InitParam();
////		p.setDomain("wx.gensee.com");
////		p.setNumber("61596074");
////		p.setJoinPwd("019772");
////		p.setNickName("sxh123");
////		p.setServiceType(ServiceType.TRAINING);
//		
//		p.setDomain("192.168.1.106");
//		p.setNumber("25937761");
//		p.setJoinPwd("333333");
//		p.setNickName("Android Attendee");
//		p.setServiceType(ServiceType.WEBCAST);
//		
//		castInit(p);
////		p.setDomain("192.168.1.164");
////		p.setNumber("61596074");
////		p.setLiveId("0dea6e2ea2334f1580446f6ec25f806b");
////		p.setJoinPwd("019772");
////		p.setNickName("sxh123");
////		p.setServiceType(ServiceType.WEBCAST);
////		vodInit(p);
//		findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				VideoParam p = new VideoParam(1280,720 , 15, 1);
//				localVideoViewEx.open(p, VideoActivity.this);
//			}
//		});
//	}
//	
//	private void castInit(InitParam initParam){
//		mPlayer = new Player();
//		mPlayer.setGSVideoView(videoView);
//		mPlayer.join(this, initParam, new OnPlayListener() {
//			
//			@Override
//			public void onVideoEnd() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onVideoBegin() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onUserUpdate(UserInfo arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onUserLeave(UserInfo arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onUserJoin(UserInfo arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onSubject(String arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onRosterTotal(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onRollcall(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onReconnecting() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPublish(boolean arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPublicMsg(long arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPageSize(int arg0, int arg1, int arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			
//			@Override
//			public void onLottery(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onLiveText(String arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onLeave(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onJoin(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onInvite(final int type, final boolean isOpen) {
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						postInvite(3, isOpen);
//					}
//				});
//			}
//			
//			private void postInvite(int type, boolean isOpen){
//				if (isOpen) {
//					inviteMediaType = type;
//					String media = "音频";
//					if (type == INVITE_AUIDO) {
//						media = "音频";
//					} else if (type == INVITE_VIDEO) {
//						media = "视频";
//					} else {
//						media = "音视频";
//					}
//					if (dialog == null) {
//						dialog = new AlertDialog.Builder(VideoActivity.this)
//								.setPositiveButton("接受",
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(DialogInterface dialog,
//													int which) {
//												accept(true);
//											}
//										})
//								.setNegativeButton("拒绝",
//										new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(DialogInterface dialog,
//													int which) {
//												accept(false);
//											}
//										}).create();
//					}
//					dialog.setMessage("老师邀请你打开" + media);
//					dialog.show();
//				} else {
//					if (dialog != null && dialog.isShowing()) {
//						dialog.dismiss();
//					}
//					accept(false);
//				}
//			}
//			
//			private void accept(boolean isAccept){
//				mPlayer.openMic(VideoActivity.this, isAccept, null);
//			}
//
//			@Override
//			public void onMicNotify(int notify) {
//				GenseeLog.d(TAG, "onMicNotify notify = " + notify);
//				switch (notify) {
//				case MicNotify.MIC_COLSED:
////					runOnUiThread(new Runnable() {
////						
////						@Override
////						public void run() {
////							
////							mViedoFragment.onMicColesed();
////						}
////					});
//					mPlayer.inviteAck(inviteMediaType, false, null);
//					break;
//				case MicNotify.MIC_OPENED:
////					runOnUiThread(new Runnable() {
////						
////						@Override
////						public void run() {
////							mViedoFragment.onMicOpened(inviteMediaType);
////						}
////					});
//					mPlayer.inviteAck(inviteMediaType, true, null);
//					
//					break;
//				case MicNotify.MIC_OPEN_FAILED:
////					runOnUiThread(new Runnable() {
////						
////						@Override
////						public void run() {
////							toastMsg("麦克风打开失败，请重试并允许程序打开麦克风");
////						}
////					});
//					mPlayer.openMic(VideoActivity.this, false, null);
//					mPlayer.inviteAck(inviteMediaType, false, null);
//					break;
//
//				default:
//					break;
//				}
//			}
//			
//			@Override
//			public void onFileShareDl(int arg0, String arg1, String arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onFileShare(int arg0, String arg1, String arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onErr(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onDocSwitch(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onCaching(boolean arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAudioLevel(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onVideoSize(int arg0, int arg1, boolean arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onModuleFocus(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onScreenStatus(boolean arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onIdcList(List<PingEntity> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//	}
//	
//	
//	private void vodInit(InitParam initParam){
//		VodSite site = new VodSite(this);
//		site.setVodListener(new OnVodListener() {
//			
//			@Override
//			public void onVodObject(String arg0) {
//				vod(arg0);
//			}
//			
//			@Override
//			public void onVodErr(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onVodDetail(VodObject arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onQaHistory(String arg0, List<QAMsg> arg1, int arg2,
//					boolean arg3) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onChatHistory(String arg0, List<ChatMsg> arg1, int arg2,
//					boolean arg3) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//
//		site.getVodObject(initParam);
//	}
//	private void vod(String vodId){
//		final VODPlayer player = new VODPlayer();
//		player.setGSVideoView(videoView);
//		player.play(vodId, new OnOLPlayListener() {
//			
//			@Override
//			public void onVideoStart() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onVideoSize(int arg0, int arg1, int arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onSeek(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPosition(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPlayStop() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPlayResume() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPlayPause() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onPageSize(int arg0, int arg1, int arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onInit(int arg0, boolean arg1, int arg2, List<DocInfo> arg3) {
//				player.seekTo(108 * 60 * 1000);
//			}
//			
//			@Override
//			public void onError(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onChat(List<ChatMsg> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onCaching(boolean arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAudioLevel(int arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onDocInfo(List<DocInfo> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		}, null,false);
//	}
//	
//	
//	
//	
//	
//	
//
//	@Override
//	public void setVideoSize(Context c, int width, int height) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int getCameraId() {
//		// TODO Auto-generated method stub
//		return 1;
//	}
//
//	@Override
//	public void saveCameraId(int cameraId) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int sendVideoData(byte[] data, VideoParam videoParam) {
////		return mPlayer.onVideoData(data,videoParam);
//		return 0;
//	}
//}

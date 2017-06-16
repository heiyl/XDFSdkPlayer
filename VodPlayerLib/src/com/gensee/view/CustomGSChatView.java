package com.gensee.view;

/**
 * XDFSdkPlayer
 * 2017/6/14.
 * yulong
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.adapter.AbsGridViewAvatarAdapter;
import com.gensee.adapter.ChatViewPageAdapter;
import com.gensee.adapter.SelectAvatarInterface;
import com.gensee.chat.gif.SpanResource;
import com.gensee.entity.ChatMsg;
import com.gensee.entity.UserInfo;
import com.gensee.entity.chat.AbsChatMessage;
import com.gensee.entity.chat.PrivateMessage;
import com.gensee.entity.chat.PublicMessage;
import com.gensee.entity.chat.SysMessage;
import com.gensee.holder.chat.impl.AbstractChatImpl;
import com.gensee.holder.chat.impl.MsgQueue;
import com.gensee.holder.chat.impl.MsgQueue.OnPublicChatHolderListener;
import com.gensee.player.Player;
import com.gensee.taskret.OnTaskRet;
import com.gensee.utils.GenseeLog;
import com.gensee.utils.ThreadPool;
import com.gensee.view.AbsChatToPopView.InterfaceSelarctorName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CustomGSChatView extends GSChatView{
    private TextView tvChatTo;
    private TextView btnSend;
    private ImageButton btnExpression;
    protected ChatEditText chatEditText;
    private LinearLayout lyExpression;
    private LinearLayout lyPageIndex;
    private ViewPager mViewPage;
    private int nCountPerPage;
    private UserInfo receiveUserInfo;
    private AtomicBoolean isMute;
    private AtomicBoolean isRoomMute;
    private AbsChatToPopView mPopWindowsChatView;
    private List<UserInfo> mList;
    private static final int ADD_PUBLIC_USER_TYPE = 0;
    private static final int ADD_PRIVATE_USER_TYPE = 1;
    private static final int LEAVE_USER_TYPE = 2;
    private static final int RECONNECTION_ROOM_TYPE = 3;
    private static final String TAG = "CustomGSChatView";
    private CustomGSChatView.PublicChatLv mPublicChatLv;
    private CustomGSChatView.SelfChatLv mSelfChatLv;
    private CustomGSChatView.ChatImpl mChatImpl;
    Handler myHandler;

    public CustomGSChatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isMute = new AtomicBoolean(false);
        this.isRoomMute = new AtomicBoolean(false);
        this.myHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 3:
                        CustomGSChatView.this.updateUsers((UserInfo)null, 3);
                        break;
                    case 2000:
                        CustomGSChatView.this.updateUsers((UserInfo)msg.obj, 0);
                        break;
                    case 2001:
                        CustomGSChatView.this.updateUsers((UserInfo)msg.obj, 1);
                        break;
                    case 2002:
                        CustomGSChatView.this.updateUsers((UserInfo)msg.obj, 2);
                }

                super.handleMessage(msg);
            }
        };
    }

    public CustomGSChatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomGSChatView(Context context) {
        this(context, (AttributeSet)null);
    }

    public void onClick(View v) {
        if(v.getId() == this.getSendBtnId()) {
            this.sendChatMsg(this.receiveUserInfo == null);
        } else if(v.getId() == this.getExpressionBtnId()) {
            this.expression();
        } else if(v.getId() == this.getQuerySelfTvId()) {
            this.select();
        } else if(v.getId() == this.getTvChatToId()) {
            this.selectChatTo();
        }

    }

    private void expression() {
        if(this.lyExpression.getVisibility() == 8) {
            this.lyExpression.setVisibility(0);
        } else {
            this.lyExpression.setVisibility(8);
        }

    }

    private void updateUsers(UserInfo newUser, int nType) {
        UserInfo selectUser;
        if(nType == 2003) {
            selectUser = (UserInfo)this.mList.get(0);
            this.mList.clear();
            this.mList.add(selectUser);
            this.updateChatTo();
        } else {
            if(newUser != null) {
                selectUser = null;
                Iterator var5 = this.mList.iterator();

                while(var5.hasNext()) {
                    UserInfo user = (UserInfo)var5.next();
                    if(user.getUserId() == newUser.getUserId()) {
                        selectUser = user;
                        break;
                    }
                }

                if(nType == 0) {
                    if(selectUser == null) {
                        this.mList.add(newUser);
                        this.updateChatTo();
                    }
                } else if(nType == 1) {
                    if(selectUser != null) {
                        this.mList.remove(selectUser);
                    }

                    this.mList.add(1, newUser);
                    this.updateChatTo();
                } else if(nType == 2 && selectUser != null) {
                    this.mList.remove(selectUser);
                    this.updateChatTo();
                }
            }

        }
    }

    private void updateChatTo() {
        if(this.mPopWindowsChatView != null && this.mPopWindowsChatView.isShowing()) {
            this.mPopWindowsChatView.updateUserPopWindow(this.tvChatTo);
        }

    }

    private void selectChatTo() {
        if(this.mPopWindowsChatView == null) {
            this.mPopWindowsChatView = this.createChatToPopView(this.getRootView(), this, this.mList);
        }

        if(this.mPopWindowsChatView.isShowing()) {
            this.mPopWindowsChatView.updateUserPopWindow(this.tvChatTo);
        } else {
            this.mPopWindowsChatView.showPopWindow(this.tvChatTo);
        }

    }

    public void sendToChatOther(int postion) {
        if(postion == 0) {
            this.receiveUserInfo = null;
            this.tvChatTo.setText(this.getContext().getResources().getString(this.getChatPublicTvId()));
        } else {
            UserInfo mUserInfo = (UserInfo)this.mList.get(postion);
            this.receiveUserInfo = mUserInfo;
            this.tvChatTo.setText(mUserInfo.getName());
        }

    }

    public void selectStatus(boolean bTrue) {
        this.tvChatTo.setSelected(bTrue);
    }

    public void updateChatTarget(final UserInfo chatTo, final boolean bLeave) {
        this.post(new Runnable() {
            public void run() {
                if(CustomGSChatView.this.receiveUserInfo != null && CustomGSChatView.this.receiveUserInfo.getUserId() == chatTo.getUserId()) {
                    if(!bLeave) {
                        CustomGSChatView.this.receiveUserInfo.update(chatTo);
                    } else {
                        CustomGSChatView.this.receiveUserInfo = null;
                    }
                }

            }
        });
    }

    protected void sendChatMsg(boolean isPublic) {
        if(!this.isMute.get()) {
            if(this.isRoomMute.get()) {
                if(isPublic) {
                    this.sendPublicMsg(true);
                } else {
                    this.sendPrivateMsg(true);
                }
            } else if(isPublic) {
                this.sendPublicMsg(false);
            } else {
                this.sendPrivateMsg(false);
            }

            this.chatEditText.setText("");
            if(this.lyExpression.getVisibility() != 8) {
                this.lyExpression.setVisibility(8);
            }

            this.hideSoftInputmethod(this.getContext());
        }
    }

    private boolean checkMsgInfo(UserInfo selfInfo, String content) {
        if(selfInfo == null) {
            Toast.makeText(this.getContext(), this.getSelfInfoNullId(), 3000).show();
            return false;
        } else if(this.receiveUserInfo != null && this.receiveUserInfo.getUserId() == selfInfo.getUserId()) {
            Toast.makeText(this.getContext(), this.getChatToSelfStrId(), 3000).show();
            return false;
        } else if("".equals(content)) {
            Toast.makeText(this.getContext(), this.getSendMsgNotNullId(), 3000).show();
            return false;
        } else {
            return true;
        }
    }

    protected void sendPublicMsg(boolean onlyShowLocal) {
        if(getPlayer() == null) {
            GenseeLog.d("CustomGSChatView", "sendPublicMsg");
        } else {
            UserInfo selfInfo = getPlayer().getSelfInfo();
            String content = this.onGetChatText(this.chatEditText.getChatText());
            if(this.checkMsgInfo(selfInfo, content)) {
                String richText = this.onGetRichText(this.chatEditText.getRichText());
                long senderId = selfInfo.getUserId();
                String senderName = selfInfo.getName();
                int chatId = selfInfo.getChatId();
                int role = selfInfo.getRole();
                String msgId = UUID.randomUUID().toString();
                final ChatMsg msg = new ChatMsg(content, richText, 0, msgId);
                msg.setSender(senderName);
                msg.setSenderId(senderId);
                msg.setSenderRole(role);
                msg.setChatId(chatId);
                if(onlyShowLocal) {
                    this.onChatWithPublic(msg);
                } else {
                    getPlayer().chatToPublic(msg, new OnTaskRet() {
                        public void onTaskRet(boolean ret, int id, String desc) {
                            if(ret) {
                                CustomGSChatView.this.onChatWithPublic(msg);
                            }

                        }
                    });
                }

            }
        }
    }

    protected String onGetChatText(String srcMsg) {
        return srcMsg;
    }

    protected String onGetRichText(String srcRichText) {
        return srcRichText;
    }

    protected void sendPrivateMsg(boolean onlyShowLocal) {
        if(getPlayer() == null) {
            GenseeLog.d("CustomGSChatView", "sendPrivateMsg");
        } else {
            UserInfo selfInfo = getPlayer().getSelfInfo();
            final String content = this.onGetChatText(this.chatEditText.getChatText());
            if(this.checkMsgInfo(selfInfo, content)) {
                final String richText = this.onGetRichText(this.chatEditText.getRichText());
                final long receiverId = this.receiveUserInfo.getUserId();
                final String receiverName = this.receiveUserInfo.getName();
                String msgId = UUID.randomUUID().toString();
                if(onlyShowLocal) {
                    this.onChatToPerson(receiverId, receiverName, content, richText);
                } else {
                    ChatMsg msg = new ChatMsg(content, richText, 2, msgId);
                    msg.setSender(selfInfo.getName());
                    msg.setSenderId(selfInfo.getUserId());
                    msg.setSenderRole(selfInfo.getRole());
                    msg.setChatId(this.receiveUserInfo.getChatId());
                    this.getPlayer().chatToPersion(msg, new OnTaskRet() {
                        public void onTaskRet(boolean ret, int id, String desc) {
                            if(ret) {
                                CustomGSChatView.this.onChatToPerson(receiverId, receiverName, content, richText);
                            }

                        }
                    });
                }

            }
        }
    }

    private void select() {
        if(this.getPlayer() != null && this.mPublicChatLv != null && this.mSelfChatLv != null) {
            TextView tvPublicQuerySelf = this.mPublicChatLv.tvQuerySelf;
            TextView tvSelfQuerySelf = this.mSelfChatLv.tvQuerySelf;
            tvPublicQuerySelf.setSelected(!tvPublicQuerySelf.isSelected());
            tvSelfQuerySelf.setSelected(!tvSelfQuerySelf.isSelected());
            this.mSelfChatLv.show(tvSelfQuerySelf.isSelected());
            if(tvSelfQuerySelf.isSelected()) {
                final UserInfo self = this.getPlayer().getSelfInfo();
                if(self != null) {
                    ThreadPool.getInstance().execute(new Runnable() {
                        public void run() {
                            MsgQueue.getIns().getSelfLatestMsg(self.getUserId());
                        }
                    });
                }
            } else {
                MsgQueue.getIns().getMsgList();
                MsgQueue.getIns().resetSelfList();
            }

        }
    }

    protected void initView(View view) {
        this.tvChatTo = (TextView)view.findViewById(this.getTvChatToId());
        this.tvChatTo.setOnClickListener(this);
        this.btnSend = (TextView) this.findViewById(this.getSendBtnId());
        this.btnSend.setOnClickListener(this);
        this.btnExpression = (ImageButton)this.findViewById(this.getExpressionBtnId());
        this.btnExpression.setOnClickListener(this);
        this.chatEditText = (ChatEditText)this.findViewById(this.getChatEditId());
        this.mPublicChatLv = new CustomGSChatView.PublicChatLv();
        this.mPublicChatLv.initView(view);
        MsgQueue.getIns().setOnPublicChatHolderListener(this.mPublicChatLv);
        this.mChatImpl = new CustomGSChatView.ChatImpl();
        this.mSelfChatLv = new CustomGSChatView.SelfChatLv();
        this.mSelfChatLv.initView(view);
        this.mList = new ArrayList();
        UserInfo mUserInfo = new UserInfo();
        mUserInfo.setUserId(-1000L);
        mUserInfo.setName(this.getContext().getResources().getString(this.getChatPublicTvId()));
        this.mList.add(mUserInfo);
        this.mPopWindowsChatView = this.createChatToPopView(this.getRootView(), this, this.mList);
        this.initExpressionLayout();
    }

    public void hideSoftInputmethod(Context context) {
        Activity activity = (Activity)context;
        if(activity.getCurrentFocus() != null) {
            InputMethodManager im = (InputMethodManager)activity.getSystemService("input_method");
            im.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), 2);
        }

    }

    private void initExpressionLayout() {
        this.lyExpression = (LinearLayout)this.findViewById(this.getExpressionLyId());
        this.lyPageIndex = (LinearLayout)this.findViewById(this.getExpressionIndexLyId());
        int nCount = SpanResource.getBrowMap(this.getContext()).keySet().toArray().length;
        if(nCount % 18 == 0) {
            this.nCountPerPage = nCount / 18;
        } else {
            this.nCountPerPage = nCount / 18 + 1;
        }

        ArrayList pagerList = new ArrayList();

        for(int mChatViewPageAdapter = 0; mChatViewPageAdapter < this.nCountPerPage * 2; ++mChatViewPageAdapter) {
            View mPagerView = LayoutInflater.from(this.getContext()).inflate(this.getExpressionPagerId(), (ViewGroup)null);
            if(mChatViewPageAdapter < this.nCountPerPage) {
                ImageView index = new ImageView(this.getContext());
                if(mChatViewPageAdapter == 0) {
                    index.setBackgroundResource(this.getExpressionIndexSelectIvId());
                } else {
                    index.setBackgroundResource(this.getExpressionIndexUnSelectIvId());
                }

                LayoutParams mGridViewAvatarAdapter = new LayoutParams(-2, -2);
                mGridViewAvatarAdapter.setMargins(0, 10, 10, 10);
                index.setLayoutParams(mGridViewAvatarAdapter);
                this.lyPageIndex.addView(index);
            }

            int var9 = mChatViewPageAdapter % this.nCountPerPage;
            AbsGridViewAvatarAdapter var10 = this.getGvAvatarAdapter(this.getContext(), this, var9 * 18, 18 - var9 * 18);
            GridView mGridView = (GridView)mPagerView.findViewById(this.getExpressionGvId());
            mGridView.setAdapter(var10);
            pagerList.add(mPagerView);
        }

        ChatViewPageAdapter var8 = new ChatViewPageAdapter(pagerList);
        this.mViewPage = (ViewPager)this.findViewById(this.getExpressionVpId());
        this.mViewPage.setVisibility(0);
        this.mViewPage.setAdapter(var8);
        this.mViewPage.setCurrentItem(pagerList.size() * 100);
        this.mViewPage.setOnPageChangeListener(this);
    }

    public void selectAvatar(String sAvatar, Drawable resId) {
        this.chatEditText.getText().insert(this.chatEditText.getSelectionStart(), SpanResource.convetToSpan(sAvatar.toString(), this.getContext()));
    }

    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int arg0) {
        for(int i = 0; i < this.lyPageIndex.getChildCount(); ++i) {
            ImageView mImageView = (ImageView)this.lyPageIndex.getChildAt(i);
            if(i == arg0 % this.nCountPerPage) {
                mImageView.setBackgroundResource(this.getExpressionIndexSelectIvId());
            } else {
                mImageView.setBackgroundResource(this.getExpressionIndexUnSelectIvId());
            }
        }

    }

    public void onMute(boolean isMute) {
        this.isMute.set(isMute);
        if(!this.isRoomMute.get()) {
            this.onSysmessage(isMute?this.getChatDisableStrId():this.getChatEnableStrId());
        }

    }

    public void onRoomMute(boolean isMute) {
        this.isRoomMute.set(isMute);
        if(!this.isMute.get()) {
            this.onSysmessage(isMute?this.getChatDisableStrId():this.getChatEnableStrId());
        }

    }

    private void onSysmessage(int resStrId) {
        String sText = this.getContext().getResources().getString(resStrId);
        SysMessage message = new SysMessage();
        message.setRich(sText);
        message.setText(sText);
        message.setTime(Calendar.getInstance().getTimeInMillis());
        this.mChatImpl.updateMessage(message);
    }

    public void onChatWithPerson(ChatMsg msg) {
        if(msg != null) {
            UserInfo self = getPlayer().getSelfInfo();
            if(self != null) {
                long userId = msg.getSenderId();
                String sSendName = msg.getSender();
                int senderRole = msg.getSenderRole();
                String text = msg.getContent();
                String rich = msg.getRichText();
                int onChatID = msg.getChatId();
                GenseeLog.d("CustomGSChatView OnChatWithPersion userId = " + userId + " sSendName = " + sSendName + " rich = " + rich + " text = " + text);
                UserInfo newUser = new UserInfo(userId, sSendName, -1, onChatID);
                this.myHandler.sendMessage(this.myHandler.obtainMessage(2001, newUser));
                PrivateMessage privateMessage = new PrivateMessage();
                privateMessage.setReceiveName(self.getName());
                privateMessage.setReceiveUserId(self.getUserId());
                privateMessage.setRich(rich);
                privateMessage.setText(text);
                privateMessage.setSendUserId(userId);
                privateMessage.setSendUserName(sSendName);
                privateMessage.setTime(Calendar.getInstance().getTimeInMillis());
                privateMessage.setSenderRole(senderRole);
                privateMessage.setId(msg.getId());
                this.mChatImpl.updateMessage(privateMessage);
            } else {
                GenseeLog.d("CustomGSChatView OnChatWithPersion getselfIno is null");
            }

        }
    }

    public void onChatToPerson(long receiveUserId, String sReceiveName, String text, String rich) {
        UserInfo self = getPlayer().getSelfInfo();
        if(self != null) {
            GenseeLog.d("CustomGSChatView OnChatToPerson receiveUserId = " + receiveUserId + " sReceiveName = " + sReceiveName + " rich = " + rich + " text = " + text);
            PrivateMessage privateMessage = new PrivateMessage();
            privateMessage.setReceiveName(sReceiveName);
            privateMessage.setReceiveUserId(receiveUserId);
            privateMessage.setRich(rich);
            privateMessage.setText(text);
            privateMessage.setSendUserId(self.getUserId());
            privateMessage.setSendUserName(self.getName());
            privateMessage.setTime(Calendar.getInstance().getTimeInMillis());
            privateMessage.setSenderRole(self.getRole());
            this.mChatImpl.updateMessage(privateMessage);
        } else {
            GenseeLog.d("CustomGSChatView OnChatWithPersion getselfIno is null");
        }

    }

    public void onChatWithPublic(ChatMsg msg) {
        if(msg != null) {
            long userId = msg.getSenderId();
            String sSendName = msg.getSender();
            int senderRole = msg.getSenderRole();
            String text = msg.getContent();
            String rich = msg.getRichText();
            int onChatID = msg.getChatId();
            UserInfo self;
            if(msg.getSenderId() != this.getSelfId()) {
                self = new UserInfo(userId, sSendName, -1, onChatID);
                this.myHandler.sendMessage(this.myHandler.obtainMessage(2000, self));
            }

            self = getPlayer().getSelfInfo();
            if(self != null) {
                GenseeLog.d("CustomGSChatView OnChatWithPublic userId = " + userId + " sSendName = " + sSendName + " rich = " + rich + " text = " + text);
                PublicMessage publicMessage = new PublicMessage();
                publicMessage.setRich(rich);
                publicMessage.setText(text);
                publicMessage.setSendUserId(userId);
                publicMessage.setSendUserName(sSendName);
                publicMessage.setTime(Calendar.getInstance().getTimeInMillis());
                publicMessage.setSenderRole(senderRole);
                publicMessage.setId(msg.getId());
                this.mChatImpl.updateMessage(publicMessage);
            } else {
                GenseeLog.d("CustomGSChatView OnChatWithPersion getselfIno is null");
            }

        }
    }

    public void onChatcensor(final String type, final String id) {
        ThreadPool.getInstance().execute(new Runnable() {
            public void run() {
                try {
                    if("user".equals(type)) {
                        MsgQueue.getIns().removeMsg(Long.parseLong(id), "");
                    } else {
                        MsgQueue.getIns().removeMsg(0L, id);
                    }
                } catch (Exception var2) {
                    ;
                }

            }
        });
    }

    public void onPublish(boolean isPlaying) {
        int resId = isPlaying?this.getPublishPlayingStrId():this.getPublishPauseStrId();
        this.onSysmessage(resId);
    }

    public void onReconnection() {
        this.myHandler.sendMessage(this.myHandler.obtainMessage(2003));
    }

    public long getSelfId() {
        UserInfo self = getPlayer() == null?null:getPlayer().getSelfInfo();
        return self != null?self.getUserId():-1L;
    }

    protected abstract int getSendBtnId();

    protected abstract int getExpressionBtnId();

    protected abstract int getChatEditId();

    protected abstract int getChatLvId();

    protected abstract int getSelfChatLvId();

    protected abstract int getSelfChatRlId();

    protected abstract int getChatLvHeadViewId();

    protected abstract int getQuerySelfTvId();

    protected abstract int getExpressionLyId();

    protected abstract int getExpressionIndexLyId();

    protected abstract int getExpressionPagerId();

    protected abstract int getExpressionIndexSelectIvId();

    protected abstract int getExpressionIndexUnSelectIvId();

    protected abstract int getExpressionGvId();

    protected abstract int getExpressionVpId();

    protected abstract int getSendMsgNotNullId();

    protected abstract int getSelfInfoNullId();

    protected abstract int getRelTipId();

    protected abstract int getRelTipStrId();

    protected abstract int getTvTipId();

    protected abstract CustomAbsChatAdapter getChatAdapter(Context var1);

    protected abstract AbsGridViewAvatarAdapter getGvAvatarAdapter(Context var1, SelectAvatarInterface var2, int var3, int var4);

    protected abstract int getChatToSelfStrId();

    protected abstract int getTvChatToId();

    protected abstract int getChatPublicTvId();

    protected abstract AbsChatToPopView createChatToPopView(View var1, InterfaceSelarctorName var2, List<UserInfo> var3);

    protected abstract int getPublishPauseStrId();

    protected abstract int getPublishPlayingStrId();

    protected abstract int getChatDisableStrId();

    protected abstract int getChatEnableStrId();

    protected abstract Player getPlayer();

    public void release() {
        this.mChatImpl.release();
        this.isMute.set(false);
        this.isRoomMute.set(false);
    }

    private class ChatImpl extends AbstractChatImpl {
        public ChatImpl() {
        }
    }

    protected class PublicChatLv extends ChatLvView implements OnPublicChatHolderListener {
        private TextView tvQuerySelf;

        protected PublicChatLv() {
        }

        protected void initView(View view) {
            super.initView(view);
            View headerView = LayoutInflater.from(CustomGSChatView.this.getContext()).inflate(CustomGSChatView.this.getChatLvHeadViewId(), (ViewGroup)null);
            this.lvChat.addHeaderView(headerView);
            this.adapter = CustomGSChatView.this.getChatAdapter(CustomGSChatView.this.getContext());
            ((CustomAbsChatAdapter)this.adapter).setOnChatAdapterListener(CustomGSChatView.this);
            this.lvChat.setAdapter(this.adapter);
            this.tvQuerySelf = (TextView)headerView.findViewById(CustomGSChatView.this.getQuerySelfTvId());
            this.tvQuerySelf.setOnClickListener(CustomGSChatView.this);
            this.tvQuerySelf.setSelected(false);
        }

        protected void refresh() {
            ThreadPool.getInstance().execute(new Runnable() {
                public void run() {
                    MsgQueue.getIns().onMessageFresh();
                }
            });
        }

        protected void loadMore() {
            ThreadPool.getInstance().execute(new Runnable() {
                public void run() {
                    MsgQueue.getIns().onMessageLoadMore();
                }
            });
        }

        protected int getLvId() {
            return CustomGSChatView.this.getChatLvId();
        }

        public boolean isSelfLvBottom() {
            return CustomGSChatView.this.mSelfChatLv != null && CustomGSChatView.this.mSelfChatLv.getLvBottom();
        }

        public void refreshSelfMsg(List<AbsChatMessage> msgList, boolean bLatest) {
            if(CustomGSChatView.this.mSelfChatLv != null) {
                CustomGSChatView.this.mSelfChatLv.refreshMsg(10004, msgList, bLatest);
            }

        }

        public void onPullSelfMsg(List<AbsChatMessage> msgList, boolean bLatest) {
            if(CustomGSChatView.this.mSelfChatLv != null) {
                CustomGSChatView.this.mSelfChatLv.refreshMsg(10005, msgList, bLatest);
            }

        }

        public void onLoadSelfMsg(List<AbsChatMessage> msgList, boolean bLatest) {
            if(CustomGSChatView.this.mSelfChatLv != null) {
                CustomGSChatView.this.mSelfChatLv.refreshMsg(10006, msgList, bLatest);
            }

        }

        public boolean isLvBottom() {
            return this.getLvBottom();
        }

        public void onCancelMsg(List<AbsChatMessage> msgList, boolean bLatest) {
            this.refreshMsg(10001, msgList, bLatest);
        }

        public void refreshMsg(List<AbsChatMessage> msgList, boolean bLatest) {
            this.refreshMsg(10000, msgList, bLatest);
        }

        public void onPullMsg(List<AbsChatMessage> msgList, boolean bLatest) {
            this.refreshMsg(10002, msgList, bLatest);
        }

        public void onLoadMsg(List<AbsChatMessage> msgList, boolean bLatest) {
            this.refreshMsg(10003, msgList, bLatest);
        }

        private void refreshMsg(int what, List<AbsChatMessage> msgList, boolean bLatest) {
            Message message = new Message();
            message.obj = msgList;
            message.what = what;
            Bundle bundle = new Bundle();
            bundle.putBoolean("LATEST", bLatest);
            message.setData(bundle);
            this.sendMessage(message);
        }

        public void onNewMsgCount(int nMsgCount) {
        }
    }

    protected class SelfChatLv extends ChatLvView {
        private TextView tvQuerySelf;
        private RelativeLayout rlQuerySelf;

        protected SelfChatLv() {
        }

        protected void initView(View view) {
            super.initView(view);
            View headerView = LayoutInflater.from(CustomGSChatView.this.getContext()).inflate(CustomGSChatView.this.getChatLvHeadViewId(), (ViewGroup)null);
            this.lvChat.addHeaderView(headerView);
            this.adapter = CustomGSChatView.this.getChatAdapter(CustomGSChatView.this.getContext());
            ((CustomAbsChatAdapter)this.adapter).setOnChatAdapterListener(CustomGSChatView.this);
            this.lvChat.setAdapter(this.adapter);
            this.tvQuerySelf = (TextView)headerView.findViewById(CustomGSChatView.this.getQuerySelfTvId());
            this.tvQuerySelf.setOnClickListener(CustomGSChatView.this);
            this.tvQuerySelf.setSelected(false);
            this.rlQuerySelf = (RelativeLayout)view.findViewById(CustomGSChatView.this.getSelfChatRlId());
        }

        protected void show(boolean bVisible) {
            this.rlQuerySelf.setVisibility(bVisible?0:8);
        }

        public void refreshMsg(int what, List<AbsChatMessage> msgList, boolean bLatest) {
            Message message = new Message();
            message.obj = msgList;
            message.what = what;
            Bundle bundle = new Bundle();
            bundle.putBoolean("LATEST", bLatest);
            message.setData(bundle);
            this.sendMessage(message);
        }

        public void refresh() {
            ThreadPool.getInstance().execute(new Runnable() {
                public void run() {
                    UserInfo selfInfo = getPlayer().getSelfInfo();
                    MsgQueue.getIns().onSelfMessageFresh(selfInfo == null?-1L:selfInfo.getUserId());
                }
            });
        }

        protected void loadMore() {
            ThreadPool.getInstance().execute(new Runnable() {
                public void run() {
                    UserInfo selfInfo = getPlayer().getSelfInfo();
                    MsgQueue.getIns().onSelfMessageLoadMore(selfInfo == null?-1L:selfInfo.getUserId());
                }
            });
        }

        protected int getLvId() {
            return CustomGSChatView.this.getSelfChatLvId();
        }
    }
}
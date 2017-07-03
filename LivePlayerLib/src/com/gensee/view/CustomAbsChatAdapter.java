package com.gensee.view;


import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gensee.R;
import com.gensee.adapter.AbsChatAdapter;
import com.gensee.entity.chat.AbsChatMessage;
import com.gensee.entity.chat.PrivateMessage;
import com.gensee.entity.chat.PublicMessage;
import com.gensee.entity.chat.SysMessage;
import com.gensee.utils.DateUtils;
import com.gensee.utils.LogUtils;

import java.util.HashMap;

public abstract class CustomAbsChatAdapter extends AbsChatAdapter {
    private Context mContext;
    private final HashMap<String, Integer> iconMap = new HashMap<String, Integer>();
    private final int[] userIconId = {R.drawable.head01, R.drawable.head02, R.drawable.head03, R.drawable.head04, R.drawable.head05};
    private long getSelfId() {
        return this.onChatAdapterListener != null ? this.onChatAdapterListener.getSelfId() : -1L;
    }

    public CustomAbsChatAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    protected abstract class CustomAbsChatViewHolder extends AbsChatViewHolder implements OnClickListener {
        private TextView mChatNameText;
        private TextView mChatTimeText;
        private MyTextViewEx mMyTextViewEx;
        private long mCurrentTime;
        private ImageView mIcon;

        public CustomAbsChatViewHolder(View view) {
            super(view);
        }

        public void initView(View view) {
            this.mIcon = (ImageView) view.findViewById(this.getChatIconId());
            this.mChatNameText = (TextView) view.findViewById(this.getChatNameEdtid());
            this.mChatTimeText = (TextView) view.findViewById(this.getChatTimeTvid());
            this.mMyTextViewEx = (MyTextViewEx) view.findViewById(this.getChatContentTvId());
        }

        public void initValue(final int positon) {
            final AbsChatMessage mAbsChatMessage = (AbsChatMessage) CustomAbsChatAdapter.this.getItem(positon);
            String name = "";
            if (mAbsChatMessage instanceof SysMessage) {
                String mPublicMessage = CustomAbsChatAdapter.this.mContext.getResources().getString(this.getSysMsgTipId());
                /*int mString = mPublicMessage.length();
                mPublicMessage = mPublicMessage + ((AbsChatMessage)CustomAbsChatAdapter.this.getItem(positon)).getRich();
                SpannableStringBuilder num = new SpannableStringBuilder(mPublicMessage);
                num.setSpan(new ForegroundColorSpan(CustomAbsChatAdapter.this.mContext.getResources().getColor(this.getSysMsgColorId())), 0, mString, 33);*/
                this.mChatNameText.setText(mPublicMessage);
                this.mMyTextViewEx.setBackgroundResource(R.drawable.chat_receiver_teacher_bg);
                this.onTimeTextView(positon);
                name = mPublicMessage;
//                this.mChatTimeText.setText(num);
            } else if (mAbsChatMessage instanceof PrivateMessage) {
                this.onTimeTextViewGoneVis();
                PrivateMessage mPublicMessage1 = (PrivateMessage) mAbsChatMessage;
                int mSendNameSize1;
                String mString1;
                if (mPublicMessage1.getSendUserId() == CustomAbsChatAdapter.this.getSelfId()) {
                    mString1 = CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChatmeTipStrId());
                    mSendNameSize1 = mString1.length();
                } else {
                    mString1 = mPublicMessage1.getSendUserName();
                    mSendNameSize1 = mString1.length();
                }

                int mReceiveNameSize1;
                if (mPublicMessage1.getReceiveUserId() == CustomAbsChatAdapter.this.getSelfId()) {
                    mString1 = mString1 + " " + CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChattoStrId()) + " " + CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChatmeTipStrId()) + " " + CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChatsayStrId());
                    mReceiveNameSize1 = CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChatmeTipStrId()).length();
                } else {
                    mString1 = mString1 + " " + CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChattoStrId()) + " " + mPublicMessage1.getReceiveName() + " " + CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChatsayStrId());
                    mReceiveNameSize1 = mPublicMessage1.getReceiveName().length();
                }

                int num1 = (" " + CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChattoStrId()) + " ").length();
                SpannableStringBuilder style = new SpannableStringBuilder(mString1);
                style.setSpan(new ForegroundColorSpan(CustomAbsChatAdapter.this.mContext.getResources().getColor(this.getSysMsgColorId())), 0, mSendNameSize1, 33);
                style.setSpan(new ForegroundColorSpan(CustomAbsChatAdapter.this.mContext.getResources().getColor(this.getSysMsgColorId())), mSendNameSize1 + num1, mSendNameSize1 + num1 + mReceiveNameSize1, 33);
                this.mChatNameText.setText(style);
                this.onTimeTextView(positon);
                name = style.toString();
            } else if (mAbsChatMessage instanceof PublicMessage) {
                this.onTimeTextViewGoneVis();
                PublicMessage mPublicMessage2 = (PublicMessage) mAbsChatMessage;
                if (mPublicMessage2.getSendUserId() == CustomAbsChatAdapter.this.getSelfId()) {
                    name = CustomAbsChatAdapter.this.mContext.getResources().getString(this.getChatmeTipStrId());
                    this.mChatNameText.setText(name);
                    this.mMyTextViewEx.setBackgroundResource(R.drawable.chat_receiver_self_bg);
                } else {
                    name = mPublicMessage2.getSendUserName();
                    this.mChatNameText.setText(name);
                    AbsChatMessage item = (AbsChatMessage) CustomAbsChatAdapter.this.getItem(positon);
                    if (item.getSenderRole() == 1 || item.getSenderRole() == 4 || item.getSenderRole() == 7) {//组织者:1  嘉宾：4  观看者：8  web观看者：16
                        this.mMyTextViewEx.setBackgroundResource(R.drawable.chat_receiver_teacher_bg);
                    } else {
                        this.mMyTextViewEx.setBackgroundResource(R.drawable.chat_receiver_other_bg);
                    }
                }
                this.onTimeTextView(positon);
            }

            if (!TextUtils.isEmpty(name)) {
                if (name.contains("系统消息")) {
                    mIcon.setBackgroundResource(R.drawable.system80px);
                } else {
                    Integer index = iconMap.get(name);
                    if (index != null) {
                        mIcon.setBackgroundResource(userIconId[index]);
                    } else {
                        Integer iconIndex = getResourceIndex(name);
                        iconMap.put(name, iconIndex);
                        mIcon.setBackgroundResource(userIconId[iconIndex]);
                    }
                }
            }
        }

        private int getResourceIndex(String name) {

            String hashCode = name.hashCode() + "";
            int length = hashCode.length();
            String lastCode = hashCode.substring(length - 1, length);
            int code = Integer.parseInt(lastCode);
            int resId = 0;
            switch (code) {
                case 0:
                case 5:
                    resId = 0;
                    break;
                case 1:
                case 6:
                    resId = 1;
                    break;
                case 2:
                case 7:
                    resId = 2;
                    break;
                case 3:
                case 8:
                    resId = 3;
                    break;
                case 4:
                case 9:
                    resId = 4;
                    break;
            }
            return resId;
        }

        public void onTimeTextView(int positon) {
            this.mCurrentTime = ((AbsChatMessage) CustomAbsChatAdapter.this.getItem(positon)).getTime() / 1000L;
            String chatTime = DateUtils.timeFormate(mCurrentTime);
            this.mChatTimeText.setText(chatTime);
            LogUtils.e("chatTime", "--- time----" + mCurrentTime);
//            this.mChatTimeText.setText(String.format("%02d", new Object[]{Long.valueOf((this.mCurrentTime / 3600L % 24L + 8L) % 24L)}) + ":" + String.format("%02d", new Object[]{Long.valueOf(this.mCurrentTime % 3600L / 60L)}) + ":" + String.format("%02d", new Object[]{Long.valueOf(this.mCurrentTime % 3600L % 60L)}));
            this.mMyTextViewEx.setRichText(((AbsChatMessage) CustomAbsChatAdapter.this.getItem(positon)).getRich());
        }

        public void onTimeTextViewGoneVis() {
            this.mChatNameText.setVisibility(View.VISIBLE);
            this.mChatTimeText.setVisibility(View.VISIBLE);
            this.mMyTextViewEx.setVisibility(View.VISIBLE);
        }

        public void onClick(View v) {
            CustomAbsChatAdapter.this.notifyDataSetChanged();
        }

        protected abstract int getChatNameEdtid();

        protected abstract int getChatTimeTvid();

        protected abstract int getChatContentTvId();

        protected abstract int getChatSysTvId();

        protected abstract int getChatSysDelIvId();

        protected abstract int getSysMsgTipId();

        protected abstract int getSysMsgColorId();

        protected abstract int getChatmeTipStrId();

        protected abstract int getChattoStrId();

        protected abstract int getChatsayStrId();

        protected abstract int getChatIconId();
    }
}

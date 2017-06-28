package com.gensee.vod.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gensee.R;
import com.gensee.entity.ChatMsg;
import com.gensee.media.VODPlayer;
import com.gensee.player.fragement.BaseFragment;
import com.gensee.utils.DateUtils;
import com.gensee.view.MyTextViewEx;
import com.gensee.widget.CustomShapeImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ValidFragment")
public class VodChatHistoryFragment extends BaseFragment {

    private VODPlayer mPlayer;
    private View mView;
    private ListView lvChapterList;
    private ChapterListAdapter chapterListAdapter;

    private List<ChatMsg> chatMsgs;

    public VodChatHistoryFragment(VODPlayer player) {

        this.mPlayer = player;
        chapterListAdapter = new ChapterListAdapter();
        chatMsgs = new ArrayList<ChatMsg>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.xdf_vodplayer_chapter_fragment, null);
        lvChapterList = (ListView) mView.findViewById(R.id.doc_lv);
        lvChapterList.setAdapter(chapterListAdapter);
        chapterListAdapter.notifyData(chatMsgs);
        return mView;
    }

    public void setData(List<ChatMsg> chatMsgs) {
        this.chatMsgs = chatMsgs;
        if (null != chapterListAdapter) {
            chapterListAdapter.notifyData(chatMsgs);
        }
    }

    private final int[] userIconId = {R.drawable.head01, R.drawable.head02, R.drawable.head03, R.drawable.head04};

    private class ChapterListAdapter extends BaseAdapter {
        private List<ChatMsg> pageList;
        private int selectedPosition = 0;
        private HashMap<String, Integer> iconMap = new HashMap<>();

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
            lvChapterList.setSelection(position);
        }

        public ChapterListAdapter() {
            pageList = new ArrayList<ChatMsg>();
        }

        public void notifyData(List<ChatMsg> pageList) {
            this.pageList.clear();
            this.pageList.addAll(pageList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public Object getItem(int position) {
            return pageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.xdf_player_chat_listitem_layout, null);//xdf_player_chat_listitem_layout.xml  ,chat_listitem_layout
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.init((ChatMsg) getItem(position), position);
            return convertView;
        }

        private class ViewHolder {
            private TextView chatnametext;
            private MyTextViewEx chatcontexttextview;
            private TextView chattimetext;
            private CustomShapeImage mIcon;

            private String getChapterTime(long time) {
                return DateUtils.timeFormate(time);
            }

            public ViewHolder(View view) {
                chatnametext = (TextView) view.findViewById(R.id.chatnametext);
                chatcontexttextview = (MyTextViewEx) view.findViewById(R.id.chatcontexttextview);
                chatcontexttextview.setVisibility(View.VISIBLE);
                chattimetext = (TextView) view.findViewById(R.id.chattimetext);
                mIcon = (CustomShapeImage) view.findViewById(R.id.iv_photo);
            }

            public void init(ChatMsg item, int position) {
                String name = item.getSender();
                chatnametext.setText(name);
                chattimetext.setText(DateUtils.timeFormate(item.getTimeStamp()));
                chatcontexttextview.setRichText(item.getRichText());
                if (!TextUtils.isEmpty(name)) {
                    if (name.contains("系统消息")) {
                        mIcon.setBackgroundResource(R.drawable.system80px);
                    } else {
                        Integer index = iconMap.get(name);
                        if (index != null) {
                            mIcon.setBackgroundResource(userIconId[index]);
                        } else {
                            Integer iconIndex = position % 4;
                            iconMap.put(name, iconIndex);
                            mIcon.setBackgroundResource(userIconId[iconIndex]);
                        }
                    }
                }
            }
        }
    }
}

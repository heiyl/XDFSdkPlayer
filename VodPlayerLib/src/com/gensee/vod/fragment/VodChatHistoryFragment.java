package com.gensee.vod.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gensee.entity.ChatMsg;
import com.gensee.media.VODPlayer;
import com.gensee.playerdemo.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class VodChatHistoryFragment extends Fragment {

    private VODPlayer mPlayer;
    private View mView;
    private ListView lvChapterList;
    private ChapterListAdapter chapterListAdapter;

    private List<ChatMsg> chatMsgs;

    public VodChatHistoryFragment(VODPlayer player) {

        this.mPlayer = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.xdf_vodplayer_chapter_fragment, null);
        lvChapterList = (ListView) mView.findViewById(R.id.doc_lv);

        chapterListAdapter = new ChapterListAdapter();
        chatMsgs = new ArrayList<ChatMsg>();
        lvChapterList.setAdapter(chapterListAdapter);

        return mView;

    }

    public void setData(List<ChatMsg> chatMsgs) {
        if (null != chapterListAdapter) {
            this.chatMsgs = chatMsgs;
            chapterListAdapter.notifyData(chatMsgs);
        }
    }

    private class ChapterListAdapter extends BaseAdapter {
        private List<ChatMsg> pageList;
        private int selectedPosition = 0;

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
                        R.layout.chat_listitem_layout, null);
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
            private TextView chatcontexttextview;
            private TextView chattimetext;

            private String getChapterTime(long time) {
                return String.format("%02d", time / (3600 * 1000))
                        + ":"
                        + String.format("%02d", time % (3600 * 1000)
                        / (60 * 1000))
                        + ":"
                        + String.format("%02d", time % (3600 * 1000)
                        % (60 * 1000) / 1000);
            }

            public ViewHolder(View view) {
                chatnametext = (TextView) view.findViewById(R.id.chatnametext);
                chatcontexttextview = (TextView) view.findViewById(R.id.chatcontexttextview);
                chatcontexttextview.setVisibility(View.VISIBLE);
                chattimetext = (TextView) view.findViewById(R.id.chattimetext);
            }

            public void init(ChatMsg chapterInfo, int position) {
                chatnametext.setText(chapterInfo.getSender());
                chattimetext.setText(getChapterTime(chapterInfo.getTimeStamp()));
                chatcontexttextview.setText(chapterInfo.getContent());
            }
        }

    }
}

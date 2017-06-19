package com.gensee.vod.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gensee.entity.DocInfo;
import com.gensee.entity.PageInfo;
import com.gensee.media.VODPlayer;
import com.gensee.playerdemo.R;
import com.gensee.vod.model.ChapterInfo;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class VodChapterFragment extends Fragment {

    private VODPlayer mPlayer;
    private View mView;
    private ListView lvChapterList;
    private ChapterListAdapter chapterListAdapter;

    private List<ChapterInfo> chapterList;

    public VodChapterFragment(VODPlayer player) {

        this.mPlayer = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.xdf_vodplayer_chapter_fragment, null);
        lvChapterList = (ListView) mView.findViewById(R.id.doc_lv);

        chapterListAdapter = new ChapterListAdapter();
        chapterList = new ArrayList<ChapterInfo>();
        lvChapterList.setAdapter(chapterListAdapter);
        lvChapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ChapterInfo chapterInfo = chapterList.get(position);
                if (null != mPlayer) {
                    mPlayer.seekTo(chapterInfo.getPageTimeStamp());
                }
            }
        });

        return mView;

    }

    public void setData(Object o) {
        if (null != chapterListAdapter) {
            chapterList.clear();
            if (null != o) {
                List<DocInfo> docInfoList = (List<DocInfo>) o;
                for (DocInfo docInfo : docInfoList) {
                    List<PageInfo> pageInfoList = docInfo.getPages();
                    if (null != pageInfoList && pageInfoList.size() > 0) {
                        for (PageInfo pageInfo : pageInfoList) {

                            ChapterInfo chapterInfo = new ChapterInfo();
                            chapterInfo.setDocId(docInfo.getDocId());
                            chapterInfo
                                    .setDocName(docInfo.getDocName());
                            chapterInfo.setDocPageNum(docInfo
                                    .getPageNum());
                            chapterInfo.setDocType(docInfo.getType());

                            chapterInfo.setPageTimeStamp(pageInfo
                                    .getTimeStamp());
                            chapterInfo.setPageTitle(pageInfo
                                    .getTitle());
                            chapterList.add(chapterInfo);
                        }
                    }
                }

            }
            chapterListAdapter.notifyData(chapterList);
        }
    }

    private class ChapterListAdapter extends BaseAdapter {
        private List<ChapterInfo> pageList;
        private int selectedPosition = 0;

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
            lvChapterList.setSelection(position);
        }

        public ChapterListAdapter() {
            pageList = new ArrayList<ChapterInfo>();
        }

        public void notifyData(List<ChapterInfo> pageList) {
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
                        R.layout.doc_list_item_ly, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.init((ChapterInfo) getItem(position), position);
            return convertView;
        }

        private class ViewHolder {
            private TextView tvChapter;
            private TextView tvTitle;
            private TextView tvTime;
            private LinearLayout lyChapter;

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
                tvChapter = (TextView) view.findViewById(R.id.chapter_title);
                tvTitle = (TextView) view.findViewById(R.id.doc_title);
                tvTime = (TextView) view.findViewById(R.id.chapter_time);
                lyChapter = (LinearLayout) view.findViewById(R.id.chapter_ly);
            }

            public void init(ChapterInfo chapterInfo, int position) {
                tvChapter.setText(chapterInfo.getPageTitle());
                tvTime.setText(getChapterTime(chapterInfo.getPageTimeStamp()));
                tvTitle.setText(chapterInfo.getDocName());

                if (selectedPosition == position) {
                    lyChapter.setBackgroundResource(R.color.red);
                } else {
                    lyChapter.setBackgroundResource(R.color.transparent);
                }
            }
        }

    }
}

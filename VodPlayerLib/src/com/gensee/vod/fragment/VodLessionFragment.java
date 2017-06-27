package com.gensee.vod.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.R;
import com.gensee.entity.DocInfo;
import com.gensee.entity.PageInfo;
import com.gensee.media.VODPlayer;
import com.gensee.player.fragement.BaseFragment;
import com.gensee.utils.API;
import com.gensee.utils.LogUtils;
import com.gensee.vod.model.ChapterInfo;
import com.gensee.vod.model.PlLive;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.Response;

@SuppressLint("ValidFragment")
public class VodChapterCourseFragment extends BaseFragment {

    private VODPlayer mPlayer;
    private View mView;
    private ListView lvChapterList;
    private ChapterListAdapter chapterListAdapter;

    private List<PlLive.Lesson> chapterList;
    private Activity mActivity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    public VodChapterCourseFragment(VODPlayer player) {

        this.mPlayer = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LogUtils.e(TAG, "onCreateView: " + this.getClass().getSimpleName());
        mView = inflater.inflate(R.layout.xdf_vodplayer_chapter_fragment, null);
        lvChapterList = (ListView) mView.findViewById(R.id.doc_lv);

        chapterListAdapter = new ChapterListAdapter();
        chapterList = new ArrayList<PlLive.Lesson>();
        lvChapterList.setAdapter(chapterListAdapter);
        lvChapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                ChapterInfo chapterInfo = chapterList.get(position);
//                if (null != mPlayer) {
//                    mPlayer.seekTo(chapterInfo.getPageTimeStamp());
//                }
            }
        });
        loadData();
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

    /**
     * 获取课程列表
     */
    private void loadData() {

        RequestParams params = new RequestParams();
//        params.addFormDataPart("courseId", courseId);
//        params.addFormDataPart("appKey", API.ESTUDY_APP_KEY);
//        params.addFormDataPart("channelID", API.ESTUDY_CHANNEL_ID);
        params.addFormDataPart("courseId", "4139");
        params.addFormDataPart("appKey", "CE804942A6D34511BBF4A935E0F7BF11");
        params.addFormDataPart("channelID", "1006");
        Log.e(TAG, API.COURSE_LIST_URL + "?" + params);

        HttpRequest.get(API.COURSE_LIST_URL, params, new BaseHttpRequestCallback<PlLive>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);
                Log.e(TAG, response);
            }

            @Override
            protected void onSuccess(PlLive resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    if (resp.getStatus()) {
                        Toast.makeText(mActivity, "提交成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, resp.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mActivity, "提交失败，请稍候重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ChapterListAdapter extends BaseAdapter {
        private List<PlLive.Lesson> pageList;
        private int selectedPosition = 0;

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
            lvChapterList.setSelection(position);
        }

        public ChapterListAdapter() {
            pageList = new ArrayList<PlLive.Lesson>();
        }

        public void notifyData(List<PlLive.Lesson> pageList) {
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

            viewHolder.init((PlLive.Lesson) getItem(position), position);
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

            public void init(PlLive.Lesson lesson, int position) {
                tvChapter.setText(lesson.getLessonName());
//                tvTime.setText(getChapterTime(chapterInfo.getPageTimeStamp()));
                tvTime.setText(lesson.getBeginTime() + "--"+lesson.getEndTime());
//                tvTitle.setText(chapterInfo.getDocName());
                if (selectedPosition == position) {
                    lyChapter.setBackgroundResource(R.color.white);
                } else {
                    lyChapter.setBackgroundResource(R.color.transparent);
                }
            }
        }

    }

}

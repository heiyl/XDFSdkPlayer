package com.gensee.vod.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gensee.R;
import com.gensee.media.VODPlayer;
import com.gensee.player.fragement.BaseFragment;
import com.gensee.utils.API;
import com.gensee.utils.DateUtils;
import com.gensee.utils.LogUtils;
import com.gensee.vod.model.PlLive;
import com.gensee.vod.model.VodItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.Response;

@SuppressLint("ValidFragment")
public class VodLessionFragment extends BaseFragment {

    private VODPlayer mPlayer;
    private View mView;
    private ListView mListView;
    private LessionListAdapter mLessionAdapter;
    private List<PlLive.Lesson> mDataList;
    private Activity mActivity;
    private TextView mLessionTime;

    private VodItemClickListener vodItemClickListener;

    public void setVodItemClickListener(VodItemClickListener vodItemClickListener) {
        this.vodItemClickListener = vodItemClickListener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    public VodLessionFragment(VODPlayer player) {

        this.mPlayer = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initViews();
        loadData();
        return mView;
    }

    View mHeaderView;

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mView = inflater.inflate(R.layout.xdf_vod_lession_fragment, null);
        mListView = (ListView) mView.findViewById(R.id.doc_lv);

        mHeaderView = inflater.inflate(R.layout.xdf_vod_lession_header_view, mListView, false);
        mLessionTime = (TextView) mHeaderView.findViewById(R.id.tv_lession_time);
        mListView.addHeaderView(mHeaderView);

        mLessionAdapter = new LessionListAdapter();
        mDataList = new ArrayList<PlLive.Lesson>();
        mListView.setAdapter(mLessionAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (vodItemClickListener != null) {
                    PlLive.Lesson lesson = (PlLive.Lesson) parent.getAdapter().getItem(position);
                    vodItemClickListener.onItemClickListener(lesson);
                    mLessionAdapter.setSelectedPosition(position);
                }
            }
        });
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
        LogUtils.e(TAG, API.COURSE_LIST_URL + "?" + params);

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
            public void onSuccess(PlLive resp) {
                super.onSuccess(resp);
                LogUtils.e(TAG, resp.toString());
                if (resp != null) {
                    if (resp.getStatus()) {
                        PlLive live = resp.getResult();
                        if (live != null) {
                            String liveTime = live.getLiveTime();
                            int lessionCount = live.getLessonCount();
                            String liveDes = liveTime;
                            if (lessionCount > 0) {
                                liveDes = liveTime + "  共 " + lessionCount + " 课时";
                                mLessionTime.setText(liveDes);
                            } else {
                                mListView.removeHeaderView(mHeaderView);
                            }
                            ArrayList<PlLive.Lesson> lessons = live.getLessonList();
                            if (lessons != null && lessons.size() > 0) {
                                mDataList = lessons;
                                mLessionAdapter.setDataList(mDataList);
                            } else {

                            }
                        }

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

    private class LessionListAdapter extends BaseAdapter {
        private List<PlLive.Lesson> pageList;
        private int selectedPosition = 0;

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
            mListView.setSelection(position);
        }

        public LessionListAdapter() {
            pageList = new ArrayList<PlLive.Lesson>();
        }

        public void setDataList(List<PlLive.Lesson> pageList) {
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
                        R.layout.vod_lession_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.init((PlLive.Lesson) getItem(position), position);
            return convertView;
        }

        private class ViewHolder {
            private View ll_item;
            private TextView tv_time;
            private TextView tv_index;
            private TextView tv_title;

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
                ll_item = view.findViewById(R.id.ll_item);
                tv_time = (TextView) view.findViewById(R.id.tv_time);
                tv_index = (TextView) view.findViewById(R.id.tv_index);
                tv_title = (TextView) view.findViewById(R.id.tv_title);
            }

            public void init(PlLive.Lesson lesson, int position) {
                if (lesson == null) return;
                tv_index.setText(++position + "");
                String beginTime = lesson.getBeginTime();
                String endTime = lesson.getEndTime();

                Long bTime = System.currentTimeMillis();
                Long eTime = bTime;
                if (!TextUtils.isEmpty(beginTime)) {
                    bTime = Long.parseLong(beginTime);
                }
                if (!TextUtils.isEmpty(beginTime)) {
                    eTime = Long.parseLong(endTime);
                }
                tv_title.setText(lesson.getLessonName());
                tv_time.setText(DateUtils.getMonthDate(bTime) + " - " + DateUtils.getHourDate(eTime));
//                if (selectedPosition == position) {
//                    ll_item.setBackgroundResource(R.color.app_color_secondary);
//                } else {
//                    ll_item.setBackgroundResource(R.color.white);
//                }
            }
        }
    }
}

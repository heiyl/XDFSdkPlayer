package com.gensee.player.fragement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gensee.R;
import com.gensee.pdu.AnnoFreepenEx;
import com.gensee.pdu.AnnoPointerEx;
import com.gensee.player.Player;
import com.gensee.player.activity.PlSlidUpActivity;
import com.gensee.player.activity.PlSlidUpHelpActivity;
import com.gensee.player.model.PlGroup;
import com.gensee.utils.API;
import com.gensee.utils.LogUtils;
import com.gensee.view.GSDocViewGx;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.Headers;
import okhttp3.Response;

import static com.gensee.R.id.v_sp_line_group;

@SuppressLint("ValidFragment")
public class DocFragment extends BaseFragment {

    private Player mPlayer;
    private View mView, mSpLine;
    private GSDocViewGx mGlDocView;

    private LinearLayout mLlGroup;
    private LinearLayout mLlAdvisory;
    private LinearLayout mLlHelp;
    private View mLLEmpty;

    private static final String SLID_UP_TYPE = "slid_up_type";
    private static final int SLID_UP_GROUP = 1;
    private static final int SLID_UP_ADVISORY = 2;
    private static final int SLID_UP_HELP = 3;

    private String mCourseId = "";
    private PlGroup.Group mGroup = null;

    public DocFragment(Player player) {

        this.mPlayer = player;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = View.inflate(mActivity, R.layout.xdf_doc_fragment, null);
        initData();
        initView();
        bindListener();
        getGroupInfo();
        return mView;
    }

    public void initData() {
        Intent intent = mActivity.getIntent();
        mCourseId = intent.getStringExtra("courseId");
    }

    private void initView() {

        mGlDocView = (GSDocViewGx) mView.findViewById(R.id.imGlDocView);
        mGlDocView.setBackgroundColor(getResources().getColor(R.color.app_color_backgroud));
        mPlayer.setGSDocViewGx(mGlDocView);
        AnnoFreepenEx.setFreepenExDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.freepen_ex)));
        AnnoPointerEx.setPointerCircleDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_pointer)));
        AnnoPointerEx.setPointerCrossDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.anno_arrow)));

        mLlGroup = (LinearLayout) mView.findViewById(R.id.ll_group);
        mLlAdvisory = (LinearLayout) mView.findViewById(R.id.ll_advisory);
        mLlHelp = (LinearLayout) mView.findViewById(R.id.ll_help);
        mSpLine = mView.findViewById(v_sp_line_group);
        mLLEmpty = mView.findViewById(R.id.ll_empty);
    }

    private void bindListener() {
        mLlGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mActivity, PlSlidUpActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("group", mGroup);
                intent.putExtra(SLID_UP_TYPE, SLID_UP_GROUP);
                intent.putExtras(data);
                mActivity.startActivity(intent);
            }
        });

        mLlAdvisory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PlSlidUpActivity.class);
                intent.putExtra(SLID_UP_TYPE, SLID_UP_ADVISORY);
                mActivity.startActivity(intent);
            }
        });

        mLlHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PlSlidUpHelpActivity.class);
                intent.putExtra(SLID_UP_TYPE, SLID_UP_HELP);
                mActivity.startActivity(intent);
            }
        });
    }

    /**
     * 老师或组织者进行文档切换时通知。docName 文档名称。
     * docType 文档类型,(tip:docType=0 代表文档关闭)
     */
    public void onDocSwitch(int docType, String docName) {
        mLLEmpty.setVisibility(docType == 0 ? View.VISIBLE : View.GONE);
        mGlDocView.setVisibility(docType == 0 ? View.GONE : View.VISIBLE);

        LogUtils.e(TAG, "onDocSwitch  docType--- " + docType + "--docName---" + docName);
    }

    private void getGroupInfo() {

        if (TextUtils.isEmpty(mCourseId)) return;
        RequestParams params = new RequestParams(this);
        params.addFormDataPart("courseId", mCourseId);
        params.addFormDataPart("appKey", API.ESTUDY_APP_KEY);
        params.addFormDataPart("channelID", API.ESTUDY_CHANNEL_ID);
        LogUtils.e("getGroupInfo ", API.GROUP_INTO_URL + "?" + params);

        HttpRequest.post(API.GROUP_INTO_URL, params, new BaseHttpRequestCallback<PlGroup>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            protected void onSuccess(PlGroup resp) {
                super.onSuccess(resp);
                if (resp != null) {
                    if (resp.getStatus()) {
                        if (resp.getResult() != null && resp.getResult().size() > 0) {
                            //有群组信息
                            mGroup = resp.getResult().get(0);
                            mLlGroup.setVisibility(View.VISIBLE);
                            mSpLine.setVisibility(View.VISIBLE);
                        } else {
                            //无群组信息
                            mLlGroup.setVisibility(View.GONE);
                            mSpLine.setVisibility(View.GONE);
                        }
                    } else {
                        //无群组信息
                        mLlGroup.setVisibility(View.GONE);
                        mSpLine.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), resp.getInfo(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //无群组信息
                    mLlGroup.setVisibility(View.GONE);
                    mSpLine.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "提交失败，请稍候重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponse(Response httpResponse, String response, Headers headers) {
                super.onResponse(httpResponse, response, headers);

                LogUtils.e("PlSlidUpActivity", response);
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

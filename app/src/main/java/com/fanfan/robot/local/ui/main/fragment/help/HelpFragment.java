package com.fanfan.robot.local.ui.main.fragment.help;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.adapter.HelpAdapter;
import com.fanfan.robot.local.app.common.base.MainFragment;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.model.VoiceBean;
import com.robot.seabreeze.log.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HelpFragment extends MainFragment {

    public static HelpFragment newInstance() {
        Bundle bundle = new Bundle();
        HelpFragment helpFragment = new HelpFragment();
        helpFragment.setArguments(bundle);
        return helpFragment;
    }

    @BindView(R.id.recycler_view)
    RecyclerView recyclerVoice;

    private VoiceDBManager mVoiceDBManager;

    private List<VoiceBean> voiceBeanList = new ArrayList<>();

    private HelpAdapter helpAdapter;

//    private int mCurrentPos;
//    private boolean isClick;

    @Override
    protected int getLayoutId() {
        return R.layout.help_fragment;
    }

    @Override
    protected void initEventAndData() {
        mVoiceDBManager = new VoiceDBManager();

        initSimpleAdapter();

        loadData();
    }

    private void initSimpleAdapter() {
        helpAdapter = new HelpAdapter(voiceBeanList);
        helpAdapter.openLoadAnimation();
        helpAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                isClick = true;
                refVoice(voiceBeanList.get(position));
            }
        });

        recyclerVoice.setAdapter(helpAdapter);
        recyclerVoice.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerVoice.setItemAnimator(new DefaultItemAnimator());
    }


    public void loadData() {
        voiceBeanList = mVoiceDBManager.loadAll();
        if (voiceBeanList != null && voiceBeanList.size() > 0) {
            helpAdapter.replaceData(voiceBeanList);
//            mCurrentPos = 0;
        }
    }

    public void refVoice(VoiceBean voiceBean) {

        jumpFragment(0, voiceBean);
    }
}

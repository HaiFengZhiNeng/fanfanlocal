package com.fanfan.robot.local.ui.main.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.common.base.MainFragment;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.model.VoiceBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends MainFragment {

    @BindView(R.id.tv_show)
    TextView tvShow;
    @BindView(R.id.tv_tip1)
    TextView tvTip1;
    @BindView(R.id.tv_tip2)
    TextView tvTip2;
    @BindView(R.id.tv_tip3)
    TextView tvTip3;
    @BindView(R.id.display)
    RelativeLayout display;
    @BindView(R.id.rejoin)
    LinearLayout rejoin;

    public static HomeFragment newInstance() {
        Bundle bundle = new Bundle();
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    private VoiceDBManager mVoiceDBManager;

    private List<VoiceBean> voiceBeanList;
    private int max;

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initEventAndData() {

        loadData();
    }

    private void showRejoin() {
        mHandler.removeCallbacks(runnable);
        rejoin.setVisibility(View.VISIBLE);
        display.setVisibility(View.GONE);
    }


    public void showDisplay() {
        mHandler.postDelayed(runnable, 5000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rejoin.setVisibility(View.GONE);
            display.setVisibility(View.VISIBLE);
        }
    };

    public void loadData() {
        mVoiceDBManager = new VoiceDBManager();
        voiceBeanList = mVoiceDBManager.loadAll();
        max = voiceBeanList.size();
    }

    public void refVoice(VoiceBean voiceBean, Set<VoiceBean> keyBeans) {
        showRejoin();

        tvShow.setText(voiceBean.getVoiceAnswer());

        if (keyBeans != null && keyBeans.size() > 0) {

            getKeyBeans(keyBeans);
        } else {

            Random random = new Random();
            List<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < 3; i++) {

                getanInt(random, indexes);
            }
            Collections.sort(indexes);

            if (indexes.size() == 3) {
                tvTip1.setText(voiceBeanList.get(indexes.get(0)).getShowTitle());
                tvTip2.setText(voiceBeanList.get(indexes.get(1)).getShowTitle());
                tvTip3.setText(voiceBeanList.get(indexes.get(2)).getShowTitle());
            }
        }

        stopEvery();
        addSpeakAnswer(voiceBean.getVoiceAnswer());
    }

    private void getKeyBeans(Set<VoiceBean> keyBeans) {
        if (keyBeans.size() >= 3) {
            VoiceBean[] voiceBeans = keyBeans.toArray(new VoiceBean[keyBeans.size()]);

            tvTip1.setText(voiceBeans[2].getShowTitle());
            tvTip2.setText(voiceBeans[1].getShowTitle());
            tvTip3.setText(voiceBeans[0].getShowTitle());
        } else {
            do {
                Random random = new Random();
                int anInt = random.nextInt(max);
                VoiceBean voiceBean = voiceBeanList.get(anInt);
                keyBeans.add(voiceBean);
            } while (keyBeans.size() < 3);

            getKeyBeans(keyBeans);
        }
    }


    private void getanInt(Random random, List<Integer> indexes) {
        int anInt = random.nextInt(max);
        if (!indexes.contains(anInt)) {
            indexes.add(anInt);
        } else {
            getanInt(random, indexes);
        }
    }


    @OnClick({R.id.tv_tip1, R.id.tv_tip2, R.id.tv_tip3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_tip1:
                tipResult(tvTip1);
                break;
            case R.id.tv_tip2:
                tipResult(tvTip2);
                break;
            case R.id.tv_tip3:
                tipResult(tvTip3);
                break;
        }
    }

    private void tipResult(TextView tvTip) {
        String tip = tvTip.getText().toString();
        onRecognResult(tip);
    }

}

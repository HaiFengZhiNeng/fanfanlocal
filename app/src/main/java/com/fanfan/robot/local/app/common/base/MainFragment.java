package com.fanfan.robot.local.app.common.base;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.ui.main.MainActivity;
import com.fanfan.robot.local.ui.main.fragment.home.HomeFragment;

import java.util.Objects;

public abstract class MainFragment extends BaseFragment {

    protected OnBackToFirstListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBackToFirstListener) {
            mListener = (OnBackToFirstListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBackToFirstListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressedSupport() {
        if (this instanceof HomeFragment) {   // 如果是 第一个Fragment 则退出app
            return super.onBackPressedSupport();
        } else {                                    // 如果不是,则回到第一个Fragment
            mListener.onBackToFirstFragment();
            return true;
        }
    }


    public interface OnBackToFirstListener {
        void onBackToFirstFragment();
    }

    public void stopEvery() {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.stopEvery();
        }
    }

    public void addSpeakAnswer(String messageContent) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.addSpeakAnswer(messageContent);
        }
    }

    public void onCompleted() {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.onCompleted();
        }
    }

    public void onRecognResult(String result) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.onRecognResult(result);
        }
    }

    public void jumpFragment(int position, VoiceBean voiceBean) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.jumpFragment(position, voiceBean);
        }
    }


    public void showToast(String msg) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.showToast(msg);
        }
    }
}

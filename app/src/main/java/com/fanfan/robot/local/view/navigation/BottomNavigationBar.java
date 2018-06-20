package com.fanfan.robot.local.view.navigation;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.model.BottomNavigationItem;

import java.util.ArrayList;

public class BottomNavigationBar extends FrameLayout {


    ArrayList<BottomNavigationItem> mBottomNavigationItems = new ArrayList<>();
    ArrayList<BottomNavigationTab> mBottomNavigationTabs = new ArrayList<>();

    private int mSelectedPosition = -1;
    private int mFirstSelectedPosition = 0;
    private OnTabSelectedListener mTabSelectedListener;

    private LinearLayout mTabContainer;

    private int mAnimationDuration = 200;

    public BottomNavigationBar(@NonNull Context context) {
        this(context, null);
    }

    public BottomNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parentView = inflater.inflate(R.layout.bottom_navigation_bar, this, true);
        mTabContainer = parentView.findViewById(R.id.bottom_navigation_bar);
    }

    public BottomNavigationBar addItem(BottomNavigationItem item) {
        mBottomNavigationItems.add(item);
        return this;
    }


    public void initialise() {
        if (!mBottomNavigationItems.isEmpty()) {
            mTabContainer.removeAllViews();

            for (BottomNavigationItem currentItem : mBottomNavigationItems) {
                BottomNavigationTab bottomNavigationTab = new BottomNavigationTab(getContext());
                setUpTab(bottomNavigationTab, currentItem);
            }
            selectTabInternal(mFirstSelectedPosition);
        }
    }

    public BottomNavigationBar setTabSelectedListener(OnTabSelectedListener tabSelectedListener) {
        this.mTabSelectedListener = tabSelectedListener;
        return this;
    }


    public void selectTab(int newPosition) {
        selectTabInternal(newPosition);
    }

    private void setUpTab(BottomNavigationTab bottomNavigationTab, BottomNavigationItem currentItem) {
        bottomNavigationTab.setPosition(mBottomNavigationItems.indexOf(currentItem));

        bottomNavigationTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationTab bottomNavigationTabView = (BottomNavigationTab) v;
                selectTabInternal(bottomNavigationTabView.getPosition());
            }
        });

        mBottomNavigationTabs.add(bottomNavigationTab);

        bottomNavigationTab.setNavigation(currentItem.getTitle(), currentItem.getIcon(getContext()));

        bottomNavigationTab.initialise();

        mTabContainer.addView(bottomNavigationTab);
    }

    private void selectTabInternal(int newPosition) {
        int oldPosition = mSelectedPosition;
        if (mSelectedPosition != newPosition) {
            if (mSelectedPosition != -1)
                mBottomNavigationTabs.get(mSelectedPosition).unSelect(mAnimationDuration);
            mBottomNavigationTabs.get(newPosition).select(mAnimationDuration);
            mSelectedPosition = newPosition;
        }

        sendListenerCall(oldPosition, newPosition);
    }

    private void sendListenerCall(int oldPosition, int newPosition) {
        if (mTabSelectedListener != null) {
            if (oldPosition == newPosition) {
                mTabSelectedListener.onTabReselected(newPosition);
            } else {
                mTabSelectedListener.onTabSelected(newPosition);
                if (oldPosition != -1) {
                    mTabSelectedListener.onTabUnselected(oldPosition);
                }
            }
        }
    }


    public interface OnTabSelectedListener {

        void onTabSelected(int position);

        void onTabUnselected(int position);

        void onTabReselected(int position);
    }
}
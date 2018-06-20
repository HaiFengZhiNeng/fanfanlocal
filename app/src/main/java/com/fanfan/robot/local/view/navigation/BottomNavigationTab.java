package com.fanfan.robot.local.view.navigation;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.robot.local.R;

public class BottomNavigationTab extends FrameLayout {

    protected int mPosition;
//    protected int mActiveColor = getResources().getColor(R.color.active_color);
//    protected int mInActiveColor = getResources().getColor(R.color.inactive_color);

    protected Drawable mCompactIcon;
    protected String mLabel;

    View containerView;
    TextView labelView;
    ImageView iconView;

    public BottomNavigationTab(Context context) {
        super(context);
        init();
    }

    public BottomNavigationTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNavigationTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomNavigationTab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.item_bottom_navigation, this, true);
        containerView = view.findViewById(R.id.bottom_navigation_container);
        labelView = view.findViewById(R.id.bottom_navigation_title);
        iconView = view.findViewById(R.id.bottom_navigation_icon);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setNavigation(String label, Drawable icon) {
        mLabel = label;
        mCompactIcon = DrawableCompat.wrap(icon);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public void select(int animationDuration) {
        ValueAnimator animator = ValueAnimator.ofInt(containerView.getPaddingTop(), 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                containerView.setPadding(containerView.getPaddingLeft(),
                        (Integer) valueAnimator.getAnimatedValue(),
                        containerView.getPaddingRight(),
                        containerView.getPaddingBottom());
            }
        });
        animator.setDuration(animationDuration);
        animator.start();

        iconView.setSelected(true);
    }

    public void unSelect(int animationDuration) {
        ValueAnimator animator = ValueAnimator.ofInt(containerView.getPaddingTop(), 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                containerView.setPadding(containerView.getPaddingLeft(),
                        (Integer) valueAnimator.getAnimatedValue(),
                        containerView.getPaddingRight(),
                        containerView.getPaddingBottom());
            }
        });
        animator.setDuration(animationDuration);
        animator.start();

        iconView.setSelected(false);
    }

    public void initialise() {
        iconView.setSelected(false);
//        DrawableCompat.setTintList(mCompactIcon, new ColorStateList(
//                new int[][]{
//                        new int[]{android.R.attr.state_selected}, //1
//                        new int[]{-android.R.attr.state_selected}, //2
//                        new int[]{}
//                },
//                new int[]{
//                        mActiveColor, //1
//                        mInActiveColor, //2
//                        mInActiveColor //3
//                }
//        ));
        iconView.setImageDrawable(mCompactIcon);
        labelView.setText(mLabel);
    }


}

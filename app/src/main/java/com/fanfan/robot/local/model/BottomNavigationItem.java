package com.fanfan.robot.local.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.fanfan.robot.local.R;

public class BottomNavigationItem {

    protected int mIconResource;

    protected String mTitle;

    public BottomNavigationItem(@DrawableRes int mIconResource, @NonNull String mTitle) {
        this.mIconResource = mIconResource;
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, this.mIconResource);
    }

}

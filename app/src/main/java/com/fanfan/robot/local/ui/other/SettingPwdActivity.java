package com.fanfan.robot.local.ui.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.RobotInfo;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.model.VoiceBean;
import com.robot.seabreeze.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置setting密码
 */
public class SettingPwdActivity extends BaseActivity {

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, SettingPwdActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.originalpwdWrapper)
    TextInputLayout originalpwdWrapper;
    @BindView(R.id.set_pwdWrapper)
    TextInputLayout setpwdWrapper;
    @BindView(R.id.determine)
    Button determine;
    @BindView(R.id.span1)
    View span1;
    @BindView(R.id.span2)
    View span2;

    private String password;

    private String originalpwd;
    private String setpwd;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_pwd;
    }

    @Override
    protected void initData() {
        initToolbar();
        password = RobotInfo.getInstance().getPassword();
        if (password.equals("")) {
            originalpwdWrapper.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        setTitle("");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerKeyboardListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterKeyboardListener();
    }

    @OnClick({R.id.determine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.determine:
                hideKeyboard();
                setpwd = setpwdWrapper.getEditText().getText().toString();
                if (setpwd.isEmpty()) {
                    setpwdWrapper.setError("更改密码不能为空!");
                    break;
                }
                if (!validateSetpwd(setpwd)) {
                    setpwdWrapper.setError("请输入6~10位密码!");
                    break;
                }
                if (!password.equals("")) {
                    originalpwd = originalpwdWrapper.getEditText().getText().toString();
                    if (originalpwd.isEmpty()) {
                        originalpwdWrapper.setError("原始密码不能为空!");
                        break;
                    }
                    if (!validateSetpwd(originalpwd)) {
                        originalpwdWrapper.setError("请输入6~10位密码!");
                        break;
                    }
                    if (setpwd.equals(originalpwd)) {
                        setpwdWrapper.setError("请输入不同的密码");
                        break;
                    }
                    if (!originalpwd.equals(password)) {
                        originalpwdWrapper.setError("原始密码不正确");
                        break;
                    }
                }
                originalpwdWrapper.setEnabled(false);
                setpwdWrapper.setErrorEnabled(false);
                RobotInfo.getInstance().setPassword(setpwd);
                finish();
                break;
        }
    }

    private boolean validateSetpwd(String username) {
        if (username.length() > 5 && username.length() < 11) {
            return true;
        }
        return false;
    }


    private void registerKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("onGlobalLayout");
                if (isKeyboardShown(rootView)) {
                    Log.e("软键盘弹起");
                    span1.setVisibility(View.GONE);
                    span2.setVisibility(View.GONE);
                } else {
                    Log.e("软键盘未弹起");
                    span1.setVisibility(View.INVISIBLE);
                    span2.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void unRegisterKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(null);
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

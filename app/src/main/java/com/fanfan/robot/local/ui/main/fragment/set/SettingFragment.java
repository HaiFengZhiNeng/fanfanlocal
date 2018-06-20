package com.fanfan.robot.local.ui.main.fragment.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.app.common.base.MainFragment;
import com.fanfan.robot.local.model.Check;
import com.fanfan.robot.local.service.callback.JsonCallback;
import com.fanfan.robot.local.service.stragry.SpecialType;
import com.fanfan.robot.local.ui.other.FileImportActivity;
import com.fanfan.robot.local.ui.other.ImportActivity;
import com.fanfan.robot.local.ui.other.SettingPwdActivity;
import com.fanfan.robot.local.ui.voice.DataVoiceActivity;
import com.fanfan.robot.local.utils.DialogUtils;
import com.fanfan.robot.local.utils.GsonUtil;
import com.fanfan.robot.local.utils.system.AppUtil;
import com.fanfan.robot.local.view.NumberProgressBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.base.Request;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.task.XExecutor;
import com.robot.seabreeze.log.Log;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SettingFragment extends MainFragment {

    public static final String BASE_URL = "http://47.104.142.138";

    public static SettingFragment newInstance() {
        Bundle bundle = new Bundle();
        SettingFragment settingFragment = new SettingFragment();
        settingFragment.setArguments(bundle);
        return settingFragment;
    }

    @BindView(R.id.add_voice)
    RelativeLayout addVoice;
    @BindView(R.id.tv_cur_code)
    TextView tvCurCode;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.iv_update)
    ImageView ivUpdate;
    @BindView(R.id.rl_update)
    RelativeLayout rlUpdate;
    @BindView(R.id.iv_setpwd)
    ImageView ivSetpwd;
    @BindView(R.id.rl_setpwd)
    RelativeLayout rlSetpwd;
    @BindView(R.id.import_layout)
    RelativeLayout importLayout;
    @BindView(R.id.file_layout)
    RelativeLayout rlFile;
    @BindView(R.id.pbProgress)
    NumberProgressBar pbProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initEventAndData() {
        pbProgress.setVisibility(View.GONE);

        tvCurCode.setText(String.format("当前版本  v %s", AppUtil.getVersionName(getActivity())));

        //从数据库中恢复数据
        List<Progress> progressList = DownloadManager.getInstance().getAll();
        OkDownload.restore(progressList);
    }


    @OnClick({R.id.add_voice, R.id.rl_update, R.id.rl_setpwd, R.id.import_layout, R.id.file_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_voice:
                DataVoiceActivity.newInstance(getActivity());
                break;
            case R.id.rl_update:
                checkUpdate();
                break;
            case R.id.rl_setpwd:
                SettingPwdActivity.newInstance(getActivity());
                break;
            case R.id.import_layout:
                ImportActivity.newInstance(getActivity());
                break;
            case R.id.file_layout:
                FileImportActivity.newInstance(getActivity());
                break;
        }
    }

    public void refSetting(SpecialType specialType) {
        switch (specialType) {
            case LocalSpeech:
                DataVoiceActivity.newInstance(getActivity());
                break;
            case LocalImport:
                ImportActivity.newInstance(getActivity());
                break;
            case FileImport:
                FileImportActivity.newInstance(getActivity());
                break;
            case SetPassword:
                SettingPwdActivity.newInstance(getActivity());
                break;
        }
    }

    private Check mCheck;

    private void checkUpdate() {
        OkGo.<Check>get(BASE_URL + "/robot/check_update.php")//
                .tag(this)
                .params("type", 3)
                .execute(new JsonCallback<Check>(Check.class) {
                    @Override
                    public void onSuccess(Response<Check> response) {
                        Check check = response.body();
                        mCheck = check;
                        Log.e(check);
                        if (check.getCode() == 0) {
                            Check.CheckBean appVerBean = check.getCheck();
                            int curVersion = AppUtil.getVersionCode(getActivity());
                            int newversioncode = appVerBean.getVersionCode();

                            if (curVersion < newversioncode) {
                                DialogUtils.showBasicNoTitleDialog(getActivity(), "版本更新", "暂不更新", "更新",
                                        new DialogUtils.OnNiftyDialogListener() {
                                            @Override
                                            public void onClickLeft() {

                                            }

                                            @Override
                                            public void onClickRight() {
                                                startLoad();
                                            }
                                        });
                            } else {
                                showToast("暂时没有检测到新版本");
                            }
                        }
                    }

                    @Override
                    public void onError(Response<Check> response) {
                        super.onError(response);
                        int code = response.code();
                        String message = response.message();
                        Log.e("code : " + code + " , message : " + message);
                        showToast("暂时没有检测到新版本");
                    }
                });
    }

    private void startLoad() {
        pbProgress.setVisibility(View.VISIBLE);

        String url = BASE_URL + "/files/" + mCheck.getCheck().getAppName();

        OkDownload.request(url, OkGo.<File>get(url))//
                .save()//
                .register(new DownloadListener("") {
                    @Override
                    public void onStart(Progress progress) {
                        showToast("开始下载啦");
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        Log.i("downloadProgress : " + progress);
                        pbProgress.setMax(10000);
                        pbProgress.setProgress((int) (progress.fraction * 10000));
                    }

                    @Override
                    public void onError(Progress progress) {
                        Log.e("onError : " + progress);
                        showFailDialog();
                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
                        Log.e("onFinish : " + progress);
                        showToast("下载完成啦");
                        pbProgress.setVisibility(View.GONE);
                        AppUtil.installNormal(getActivity(), file);
                    }

                    @Override
                    public void onRemove(Progress progress) {

                    }
                })//
                .start();

    }

    private void showFailDialog() {
        DialogUtils.showBasicNoTitleDialog(getActivity(), getString(R.string.download_fail_retry), "取消", "确定",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {

                    }

                    @Override
                    public void onClickRight() {
                        startLoad();
                    }
                });
    }

}

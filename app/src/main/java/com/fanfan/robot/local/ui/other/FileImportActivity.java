package com.fanfan.robot.local.ui.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.NovelApp;
import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.db.manager.LocalDBManager;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.listener.base.recog.GrammerUtils;
import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.utils.DialogUtils;
import com.fanfan.robot.local.utils.GsonUtil;
import com.fanfan.robot.local.utils.LoadDataUtils;
import com.fanfan.robot.local.utils.system.AppUtil;
import com.fanfan.robot.local.utils.system.FileUtil;
import com.hankcs.hanlp.HanLP;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.robot.seabreeze.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class FileImportActivity extends BaseActivity {

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, FileImportActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_sys_excel)
    TextView tvSysExcel;
    @BindView(R.id.tv_choose)
    TextView tvChoose;
    @BindView(R.id.tv_import)
    TextView tvImport;

    private VoiceDBManager mVoiceDBManager;
    private String workPath;

    private LocalDBManager mLocalDBManager;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_file_import;
    }

    @Override
    protected void initData() {
        initToolbar();

        mVoiceDBManager = new VoiceDBManager();
        mLocalDBManager = new LocalDBManager();

        recognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    recognizer = GrammerUtils.getRecognizer(FileImportActivity.this, recognizer);
                }
                Log.e("初始化失败，错误码：" + code);
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        setTitle("");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick({R.id.tv_import, R.id.tv_choose})
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_choose:
                String[] items = getXlsFileName();
                if (items != null && items.length > 0) {
                    new MaterialDialog.Builder(this)
                            .title("选择需要导入的文件")
                            .items(items)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                    workPath = FileUtil.getPublicDownloadDir() + File.separator + text;
                                    tvSysExcel.setVisibility(View.VISIBLE);
                                    tvSysExcel.setText(workPath);
                                }
                            })
                            .show();
                } else {
                    showToast("没有excel批量导入表");
                }
                break;
            case R.id.tv_import:
                if (workPath == null || workPath.equals("")) {
                    showToast("请先选择excel批量导入表");
                } else {
                    addExcel();
                }
                break;
        }
    }

    private String[] getXlsFileName() {

        List<String> fileList = new ArrayList<>();

        File fileDownload = new File(FileUtil.getPublicDownloadDir());

        File[] listFiles = fileDownload.listFiles();

        for (int i = 0; i < listFiles.length; i++) {
            File file = listFiles[i];
            if (file.isFile() && file.getName().endsWith(".xls")) {
                fileList.add(file.getName());
            }
        }

        return fileList.toArray(new String[fileList.size()]);
    }

    @SuppressLint("CheckResult")
    private void addExcel() {

        tvChoose.setEnabled(false);
        tvImport.setEnabled(false);

        showProgressDialog();

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                List<VoiceBean> voiceBeanList = LoadDataUtils.loadExcelVoiceBean(workPath);
                if (voiceBeanList != null) {

                    mVoiceDBManager.deleteAll();
                    //插入数据库中
                    if (mVoiceDBManager.insertList(voiceBeanList)) {
                        String lexiconContents = LoadDataUtils.getLexiconContents(FileImportActivity.this, mVoiceDBManager, mLocalDBManager);
                        e.onNext(lexiconContents);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        tvChoose.setEnabled(true);
                        tvImport.setEnabled(true);

                        dismissProgressDialog();

                        recognizer.buildGrammar(GrammerUtils.GRAMMAR_BNF, s, new GrammarListener() {
                            @Override
                            public void onBuildFinish(String s, SpeechError speechError) {
                                if (speechError == null) {
                                    showToast("更新成功");
                                    finish();
                                } else {
                                    Log.e(speechError);
                                }
                            }
                        });
                    }
                });
    }

}

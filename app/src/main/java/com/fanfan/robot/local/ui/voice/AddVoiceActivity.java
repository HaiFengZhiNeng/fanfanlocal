package com.fanfan.robot.local.ui.voice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.NovelApp;
import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.db.manager.LocalDBManager;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.listener.base.recog.GrammerUtils;
import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.ui.other.FileImportActivity;
import com.fanfan.robot.local.utils.GsonUtil;
import com.fanfan.robot.local.utils.LoadDataUtils;
import com.fanfan.robot.local.utils.system.AppUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.robot.seabreeze.log.Log;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android on 2018/1/8.
 */

public class AddVoiceActivity extends BaseActivity {

    @BindView(R.id.et_question_wrapper)
    TextInputLayout etQuestionWrapper;
    @BindView(R.id.et_question)
    EditText etQuestion;
    @BindView(R.id.et_content_wrapper)
    TextInputLayout etContentWrapper;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.span)
    View span;
    @BindView(R.id.span3)
    View span3;
    @BindView(R.id.determine)
    Button determine;

    public static final String VOICE_ID = "voiceId";
    public static final String RESULT_CODE = "voice_title_result";
    public static final int ADD_VOICE_REQUESTCODE = 225;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, AddVoiceActivity.class);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void newInstance(Activity context, long id, int requestCode) {
        Intent intent = new Intent(context, AddVoiceActivity.class);
        intent.putExtra(VOICE_ID, id);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private long saveLocalId;

    private VoiceDBManager mVoiceDBManager;

    private VoiceBean voiceBean;

    private LocalDBManager mLocalDBManager;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_voice;
    }

    @Override
    protected void initData() {

        initToolbar();

        saveLocalId = getIntent().getLongExtra(VOICE_ID, -1);

        mVoiceDBManager = new VoiceDBManager();
        mLocalDBManager = new LocalDBManager();

        if (saveLocalId != -1) {

            voiceBean = mVoiceDBManager.selectByPrimaryKey(saveLocalId);
            etQuestionWrapper.getEditText().setText(voiceBean.getShowTitle());
            etContentWrapper.getEditText().setText(voiceBean.getVoiceAnswer());
        } else {
            voiceBean = new VoiceBean();
        }

        recognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    recognizer = GrammerUtils.getRecognizer(AddVoiceActivity.this, recognizer);
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

    @Override
    protected void onStart() {
        super.onStart();
        registerKeyboardListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterKeyboardListener();
    }

    @OnClick({R.id.determine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.determine:
                hideKeyboard();

                if (isEmpty(etQuestionWrapper)) {
                    showToast("问题不能为空！");
                } else if (isEmpty(etContentWrapper)) {
                    showToast("答案不能为空！");
                } else if (getText(etQuestionWrapper).length() > 20) {
                    showToast("输入 20 字以内");
                    break;
                } else {
                    if (saveLocalId == -1) {
                        List<VoiceBean> been = mVoiceDBManager.queryVoiceByQuestion(getText(etQuestionWrapper));
                        if (!been.isEmpty()) {
                            showToast("请不要添加相同的问题！");
                        } else {
                            voiceIsexit();
                        }
                    } else {
                        voiceIsexit();
                    }
                }
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void voiceIsexit() {

        etContentWrapper.setErrorEnabled(false);
        etQuestionWrapper.setErrorEnabled(false);

        showProgressDialog();

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {

                insertVoiceBean();

                String lexiconContents = LoadDataUtils.getLexiconContents(AddVoiceActivity.this, mVoiceDBManager, mLocalDBManager);

                e.onNext(lexiconContents);

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        dismissProgressDialog();

                        recognizer.buildGrammar(GrammerUtils.GRAMMAR_BNF, s, new GrammarListener() {
                            @Override
                            public void onBuildFinish(String s, SpeechError speechError) {
                                if (speechError == null) {
                                    Intent intent = new Intent();
                                    intent.putExtra(RESULT_CODE, saveLocalId);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    showToast(s);
                                }
                            }
                        });
                    }
                });

    }

    private void insertVoiceBean() {
        String showTitle = getText(etQuestionWrapper);
        String answer = getText(etContentWrapper);
        voiceBean.setSaveTime(System.currentTimeMillis());
        voiceBean.setShowTitle(showTitle);
        voiceBean.setVoiceAnswer(answer);

        List<String> keywordList = HanLP.extractKeyword(getText(etQuestionWrapper), 5);
        if (keywordList != null && keywordList.size() > 0) {
            Log.e(keywordList);
            voiceBean.setKeyword(GsonUtil.GsonString(keywordList));

            for (int j = 0; j < keywordList.size(); j++) {
                String key = keywordList.get(j);
                if (j == 0) {
                    voiceBean.setKey1(key);
                } else if (j == 1) {
                    voiceBean.setKey2(key);
                } else if (j == 2) {
                    voiceBean.setKey3(key);
                } else if (j == 4) {
                    voiceBean.setKey4(key);
                }
            }

        } else {
            voiceBean.setKey1(showTitle);
        }

        if (saveLocalId == -1) {//直接添加
            saveLocalId = mVoiceDBManager.insertForId(voiceBean);
        } else {//更新
            voiceBean.setId(saveLocalId);
            mVoiceDBManager.update(voiceBean);
        }
    }

    private boolean isEmpty(TextView textView) {
        return textView.getText().toString().trim().equals("") || textView.getText().toString().trim().equals("");
    }

    private boolean isEmpty(TextInputLayout inputLayout) {
        return inputLayout.getEditText().getText().toString().trim().equals("")
                || inputLayout.getEditText().getText().toString().trim().equals("");
    }

    private String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    private String getText(TextInputLayout inputLayout) {
        return inputLayout.getEditText().getText().toString().trim();
    }


    private void registerKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("onGlobalLayout");
                if (isKeyboardShown(rootView)) {
                    Log.e("软键盘弹起");
                    span.setVisibility(View.GONE);
                    span3.setVisibility(View.GONE);
                } else {
                    Log.e("软键盘未弹起");
                    span.setVisibility(View.INVISIBLE);
                    span3.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }


    private void unRegisterKeyboardListener() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(null);
    }


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

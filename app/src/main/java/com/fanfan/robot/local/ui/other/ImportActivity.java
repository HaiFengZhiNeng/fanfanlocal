package com.fanfan.robot.local.ui.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.adapter.ImportAdapter;
import com.fanfan.robot.local.app.NovelApp;
import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.db.manager.LocalDBManager;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.listener.base.recog.GrammerUtils;
import com.fanfan.robot.local.model.Channel;
import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.ui.voice.AddVoiceActivity;
import com.fanfan.robot.local.utils.GsonUtil;
import com.fanfan.robot.local.utils.LoadDataUtils;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ImportActivity extends BaseActivity {

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, ImportActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private VoiceDBManager mVoiceDBManager;
    private LocalDBManager mLocalDBManager;

//    private String[] localVoiceQuestion;
//    private String[] localVoiceAnswer;

    private List<VoiceBean> mVoiceBeanList;
    private List<LocalBean> mLocalBeanList;

    //    private List<Channel> mDatas = new ArrayList<>();
    private ImportAdapter mAdapter;

    private SpeechRecognizer recognizer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import;
    }

    @Override
    protected void initData() {
        initToolbar();

        mVoiceDBManager = new VoiceDBManager();
        mLocalDBManager = new LocalDBManager();

        showProgressDialog();

        Observable<List<LocalBean>> local = loadLocal();
        Observable<List<VoiceBean>> voice = loadVoice();

        setAdapter(local, voice);

        recognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    recognizer = GrammerUtils.getRecognizer(ImportActivity.this, recognizer);
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

    private Observable<List<VoiceBean>> loadVoice() {

        return Observable.create(new ObservableOnSubscribe<List<VoiceBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<VoiceBean>> e) throws Exception {

                List<VoiceBean> voiceBeanList = LoadDataUtils.loadVoiceBean();

                e.onNext(voiceBeanList);

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    private Observable<List<LocalBean>> loadLocal() {
        return Observable.create(new ObservableOnSubscribe<List<LocalBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<LocalBean>> e) throws Exception {

                List<LocalBean> localBeans = LoadDataUtils.loadLocalBean(ImportActivity.this);

                e.onNext(localBeans);

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @SuppressLint("CheckResult")
    private void setAdapter(Observable<List<LocalBean>> local, Observable<List<VoiceBean>> voice) {
        Observable.zip(local, voice, new BiFunction<List<LocalBean>, List<VoiceBean>, List<Channel>>() {
            @Override
            public List<Channel> apply(List<LocalBean> localBeanList, List<VoiceBean> voiceBeanList) throws Exception {

                List<Channel> channels = new ArrayList<>();

                Channel voiceChannel = new Channel();
                voiceChannel.setItemtype(Channel.TYPE_TITLE);
                voiceChannel.setChannelName("本地语音");
                channels.add(voiceChannel);

                if (voiceBeanList.size() > 0) {
                    for (VoiceBean bean : voiceBeanList) {
                        Channel channel = new Channel();
                        channel.setItemtype(Channel.TYPE_CONTENT);
                        channel.setChannelName(bean.getShowTitle());
                        channels.add(channel);
                    }
                } else {
                    Channel channel = new Channel();
                    channel.setItemtype(Channel.TYPE_CONTENT);
                    channel.setChannelName("请手动添加");
                    channels.add(channel);
                }

                Channel localChannel = new Channel();
                localChannel.setItemtype(Channel.TYPE_TITLE);
                localChannel.setChannelName("本地导航");
                channels.add(localChannel);

                if (localBeanList.size() > 0) {
                    for (LocalBean bean : localBeanList) {
                        Channel channel = new Channel();
                        channel.setItemtype(Channel.TYPE_CONTENT);
                        channel.setChannelName(bean.getShowTitle());
                        channels.add(channel);
                    }
                } else {
                    Channel channel = new Channel();
                    channel.setItemtype(Channel.TYPE_CONTENT);
                    channel.setChannelName("请手动添加");
                    channels.add(channel);
                }

                mVoiceBeanList = voiceBeanList;
                mLocalBeanList = localBeanList;
                return channels;
            }
        })
                .subscribe(new Consumer<List<Channel>>() {
                    @Override
                    public void accept(List<Channel> channels) throws Exception {

                        dismissProgressDialog();

                        mAdapter = new ImportAdapter(channels);
                        GridLayoutManager manager = new GridLayoutManager(ImportActivity.this, 3);
                        mRecyclerView.setLayoutManager(manager);
                        mRecyclerView.setAdapter(mAdapter);
                        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                int itemViewType = mAdapter.getItemViewType(position);
                                return itemViewType == Channel.TYPE_CONTENT ? 1 : 3;
                            }
                        });

                    }
                });
    }

    @NonNull
    private LocalBean getLocalBean(String title, String detail, LatLng latLng) {
        LocalBean localBean = new LocalBean();
        localBean.setSaveTime(System.currentTimeMillis());
        localBean.setShowTitle(title);
        localBean.setShowDetail(detail);
        localBean.setLat(latLng.latitude);
        localBean.setLng(latLng.longitude);
        return localBean;
    }

    @OnClick({R.id.tv_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_import:

                showProgressDialog();

                mVoiceDBManager.deleteAll();
                mVoiceDBManager.insertList(mVoiceBeanList);
                mLocalDBManager.deleteAll();
                mLocalDBManager.insertList(mLocalBeanList);

                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {

                        String lexiconContents = LoadDataUtils.getLexiconContents(ImportActivity.this, mVoiceDBManager, mLocalDBManager);

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
                                            showToast("更新成功");
                                            finish();
                                        } else {
                                            Log.e(speechError);
                                        }
                                    }
                                });
                            }
                        });
                break;
        }
    }

}

package com.fanfan.robot.local.ui.main;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.adapter.MultiAdapter;
import com.fanfan.robot.local.app.RobotInfo;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.app.common.base.MainFragment;
import com.fanfan.robot.local.app.common.base.support.SupportFragment;
import com.fanfan.robot.local.db.manager.LocalDBManager;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.listener.base.local.IRecogListener;
import com.fanfan.robot.local.listener.base.recog.LocalListener;
import com.fanfan.robot.local.listener.base.recog.local.MyRecognizerLocal;
import com.fanfan.robot.local.listener.base.synthesizer.EarListener;
import com.fanfan.robot.local.listener.base.synthesizer.ISynthListener;
import com.fanfan.robot.local.listener.base.synthesizer.local.MySynthesizerLocal;
import com.fanfan.robot.local.model.BottomNavigationItem;
import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.MultiBean;
import com.fanfan.robot.local.model.SerialBean;
import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.model.local.Asr;
import com.fanfan.robot.local.service.CameraSerivice;
import com.fanfan.robot.local.service.SerialService;
import com.fanfan.robot.local.service.event.CameraForActivityEvent;
import com.fanfan.robot.local.service.event.SerialForServiceEvent;
import com.fanfan.robot.local.service.event.SerialForActivityEvent;
import com.fanfan.robot.local.service.stragry.SpecialType;
import com.fanfan.robot.local.service.stragry.TranficCalculator;
import com.fanfan.robot.local.service.stragry.local.BackStrategy;
import com.fanfan.robot.local.service.stragry.local.HomeStrategy;
import com.fanfan.robot.local.service.stragry.local.SetStrategy;
import com.fanfan.robot.local.service.stragry.local.StopStrategy;
import com.fanfan.robot.local.service.stragry.local.SureStrategy;
import com.fanfan.robot.local.ui.main.fragment.help.HelpFragment;
import com.fanfan.robot.local.ui.main.fragment.home.HomeFragment;
import com.fanfan.robot.local.ui.main.fragment.location.LocationFragment;
import com.fanfan.robot.local.ui.main.fragment.set.SettingFragment;
import com.fanfan.robot.local.utils.GsonUtil;
import com.fanfan.robot.local.utils.camera.CameraUtils;
import com.fanfan.robot.local.view.navigation.BottomNavigationBar;
import com.robot.seabreeze.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.facedetect.DetectionBasedTracker;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements MainFragment.OnBackToFirstListener, BottomNavigationBar.OnTabSelectedListener,
        SurfaceHolder.Callback, Camera.PreviewCallback {

    public static final int DELAY_MILLIS = 25 * 1000;

    @BindView(R.id.navigation_bar)
    BottomNavigationBar navigationBar;
    @BindView(R.id.ic_logo)
    ImageView icLogo;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.recycler_list)
    RecyclerView recyclerVoice;
    @BindView(R.id.left_layout)
    RelativeLayout leftLayout;
    @BindView(R.id.surface_view)
    SurfaceView surfaceView;

    private boolean quit;

    private List<SupportFragment> mFragments = new ArrayList<>();

    private String[] titles = {"首页", "咨询位置", "更多问题", "设置"};


    private MyRecognizerLocal myRecognizer;
    private MySynthesizerLocal mySynthesizer;

    private int mPosition;

    private MultiAdapter multiAdapter;

    private VoiceDBManager mVoiceDBManager;
    private LocalDBManager mLocalDBManager;

    private List<LocalBean> localBeanList;
    private List<MultiBean> mData = new ArrayList<>();

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            initTime();
        }
    };


    //camera
    private Camera mCamera;

    private SurfaceHolder mHolder;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private int previewWidth = 320;
    private int previewHeight = 240;
    private int pictureWidth = 640;
    private int pictureHeight = 480;

    private boolean isPreviewing = false;

    private int orientionOfCamera;

    private int count;
    private boolean isPreviewFrame;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        mVoiceDBManager = new VoiceDBManager();
        mLocalDBManager = new LocalDBManager();

        initTime();

        initFragment();

        initBottomNavigationItem();

        initRecog();

        initSimpleAdapter();

        surfaceView.setVisibility(View.GONE);
        mHolder = surfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);

        mHandler.post(runnable);
        mHandler.post(previewRunnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            count = 0;
            mHandler.postDelayed(runnable, 2000);
        }
    };

    public void eliminate() {
        surfaceView.setVisibility(View.VISIBLE);
        count = 0;
        isPreviewFrame = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String format = df.format(System.currentTimeMillis());
        tvTime.setText(format);
        mHandler.postDelayed(timeRunnable, 1000);
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(LocationFragment.newInstance());
        mFragments.add(HelpFragment.newInstance());
        mFragments.add(SettingFragment.newInstance());

        loadMultipleRootFragment(R.id.act_container, 0, mFragments.get(0), mFragments.get(1), mFragments.get(2), mFragments.get(3));
    }

    private void initBottomNavigationItem() {
        BottomNavigationItem homeItem = new BottomNavigationItem(R.drawable.ic_home, titles[0]);

        final BottomNavigationItem locationItem = new BottomNavigationItem(R.drawable.ic_location, titles[1]);

        BottomNavigationItem helpItem = new BottomNavigationItem(R.drawable.ic_help, titles[2]);

        BottomNavigationItem settingItem = new BottomNavigationItem(R.drawable.ic_setting, titles[3]);

        navigationBar.addItem(homeItem)
                .addItem(locationItem)
                .addItem(helpItem)
                .addItem(settingItem)
                .initialise();

        navigationBar.setTabSelectedListener(this);

    }

    private void loadLocal() {
        localBeanList = mLocalDBManager.loadAll();
        if (localBeanList != null && localBeanList.size() > 0) {
            ArrayMap<Integer, List<LocalBean>> arrayMap = new ArrayMap<>();

            for (LocalBean localBean : localBeanList) {
                int type = localBean.getType();
                if (arrayMap.containsKey(type)) {
                    List<LocalBean> localBeans = arrayMap.get(type);
                    localBeans.add(localBean);
                } else {
                    List<LocalBean> localBeans = new ArrayList<>();
                    localBeans.add(localBean);
                    arrayMap.put(type, localBeans);
                }
            }

            Iterator<Integer> iterator = arrayMap.keySet().iterator();
            while (iterator.hasNext()) {
                int type = iterator.next();
                List<LocalBean> localBeans = arrayMap.get(type);

                multiAdapter.addData(type);

                for (LocalBean localBean : localBeans) {
                    multiAdapter.addData(localBean);
                }
            }

            refLocal(localBeanList.get(0));
        }
    }

    private void initRecog() {
        ISynthListener iSynthListener = new EarListener() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                showDisplay();
                MainActivity.this.onCompleted();
            }
        };
        mySynthesizer = new MySynthesizerLocal(this, iSynthListener);

        IRecogListener iRecogListener = new LocalListener() {

            @Override
            public void onAsrLocalFinalResult(String key1, String key2, String key3, String key4) {
                super.onAsrLocalFinalResult(key1, key2, key3, key4);
                onRecognResult(key1, key2, key3, key4);
            }

            @Override
            public void onAsrLocalDegreeLow(Asr local, int degree) {
                super.onAsrLocalDegreeLow(local, degree);
                onCompleted();
            }

            @Override
            public void onAsrFinishError(int errorCode, String errorMessage) {
                super.onAsrFinishError(errorCode, errorMessage);
                onCompleted();
            }
        };
        myRecognizer = new MyRecognizerLocal(this, iRecogListener);

    }

    private void initSimpleAdapter() {

        multiAdapter = new MultiAdapter(mData);
        multiAdapter.openLoadAnimation();
        multiAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MultiBean multiBean = multiAdapter.getData().get(position);
                if (multiBean.getItemType() == MultiBean.TYPE_VOICE) {

                    refVoice(multiBean.getVoiceBean());
                } else if (multiBean.getItemType() == MultiBean.TYPE_LOCAL_DETAIL) {
                    refLocal(multiBean.getLocalBean());
                }
            }
        });

        recyclerVoice.setAdapter(multiAdapter);
        recyclerVoice.setLayoutManager(new LinearLayoutManager(this));
        recyclerVoice.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onBackPressedSupport() {
        if (!quit) {
            showToast("再按一次退出程序");
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                }
            }, 2000);
            quit = true;
        } else {
            super.onBackPressed();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void judgeCipher() {
        stopEvery();
        String password = RobotInfo.getInstance().getPassword();
        if (password.equals("")) {
            showFragment(3);
        } else {
            showSetDialog(password);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        surfaceView.setVisibility(View.GONE);

        mHandler.removeCallbacks(previewRunnable);
        mHandler.postDelayed(previewRunnable, DELAY_MILLIS);
    }

    private String mInput;

    private void showSetDialog(final String password) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.title_setting_pwd)
                .inputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER)
                .negativeText(R.string.cancel)
                .positiveText(R.string.confirm)
                .inputRange(6, 10)
                .alwaysCallInputCallback()
                .input(getString(R.string.input_hint_pwd), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        mInput = String.valueOf(input);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onCompleted();
                        navigationBar.selectTab(0);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (password.equals(mInput)) {
                            showFragment(3);
                        } else {
                            showToast("密码错误");
                            onCompleted();
                            navigationBar.selectTab(0);
                        }
                    }
                })
                .build();
        materialDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return false;
            }
        });
        materialDialog.setCancelable(false);
        materialDialog.show();
    }

    private void showFragment(int position) {

        navigationBar.selectTab(position);
        mPosition = position;
        showHideFragment(mFragments.get(position));
        stopEvery();
        onCompleted();
    }


    private void refVoice(VoiceBean voiceBean) {

        if (mPosition == 0) {

            multiAdapter.addData(voiceBean);
            recyclerVoice.scrollToPosition(multiAdapter.getData().size() - 1);

            Set<VoiceBean> keybeans = likeVoiceByKeyword(voiceBean);

            HomeFragment homeFragment = (HomeFragment) mFragments.get(mPosition);
            homeFragment.refVoice(voiceBean, keybeans);
        } else if (mPosition == 2) {
            HelpFragment helpFragment = (HelpFragment) mFragments.get(mPosition);
            helpFragment.refVoice(voiceBean);
        } else {
            onCompleted();
        }
    }


    private void refLocal(LocalBean localBean) {
        if (mPosition == 1) {
            LocationFragment locationFragment = (LocationFragment) mFragments.get(mPosition);
            locationFragment.refLocal(localBean);
        } else {
            onCompleted();
        }
    }

    private void showDisplay() {
        if (mPosition == 0) {
            HomeFragment homeFragment = (HomeFragment) mFragments.get(mPosition);
            homeFragment.showDisplay();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mySynthesizer.onResume();
        myRecognizer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mySynthesizer.onPause();
        myRecognizer.onPause();

    }

    @Override
    protected void onDestroy() {

        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacks(previewRunnable);

        closeCamera();

        stopService(new Intent(this, SerialService.class));

        super.onDestroy();

        myRecognizer.release();
        mySynthesizer.release();
    }

    @Override
    public void onBackToFirstFragment() {
        navigationBar.selectTab(0);
    }


    public void onRecognResult(String key1, String key2, String key3, String key4) {

        TranficCalculator calculator = new TranficCalculator();

        SpecialType myType = calculator.specialLocal(key1, new BackStrategy());
        if (SpecialType.NoSpecial != myType) {
            onRecognResultBack();
            return;
        }
        myType = calculator.specialLocal(key1, new HomeStrategy());
        if (SpecialType.NoSpecial != myType) {
            onRecognResultHome(myType);
            return;
        }
        myType = calculator.specialLocal(key1, new SetStrategy());
        if (SpecialType.NoSpecial != myType) {
            onRecognResultSet(myType);
            return;
        }
        myType = calculator.specialLocal(key1, new StopStrategy());
        if (SpecialType.NoSpecial != myType) {
            stopEvery();
            return;
        }
        myType = calculator.specialLocal(key1, new SureStrategy());
        if (SpecialType.NoSpecial != myType) {
            onRecognResultSure(myType);
            return;
        }
        if (mPosition == 0 || mPosition == 2) {
            List<VoiceBean> voiceBeans = mVoiceDBManager.queryWhereOr(key1, key2, key3, key4);
            if (voiceBeans != null && voiceBeans.size() > 0) {
                VoiceBean itemData = null;
                if (voiceBeans.size() == 1) {
                    itemData = voiceBeans.get(voiceBeans.size() - 1);
                } else {
                    itemData = voiceBeans.get(new Random().nextInt(voiceBeans.size()));
                }
                refVoice(itemData);
            } else {
                onCompleted();
            }
        } else if (mPosition == 1) {
            List<LocalBean> localBeans = mLocalDBManager.queryLikeLocalByQuestion(key1);
            if (localBeans != null && localBeans.size() > 0) {
                LocalBean itemData = null;
                if (localBeans.size() == 1) {
                    itemData = localBeans.get(localBeans.size() - 1);
                } else {
                    itemData = localBeans.get(new Random().nextInt(localBeans.size()));
                }
                int index = 0;
                for (int i = 0; i < localBeanList.size(); i++) {
                    String showTitle = localBeanList.get(i).getShowTitle();
                    if (showTitle.equals(itemData.getShowTitle())) {
                        index = i;
                    }
                }
                recyclerVoice.scrollToPosition(index);
                refLocal(itemData);
            } else {
                onCompleted();
            }
        }

    }

    public void onRecognResult(String result) {

        if (mPosition == 0 || mPosition == 2) {
            List<VoiceBean> voiceBeans = mVoiceDBManager.queryWhereOr(result);
            if (voiceBeans != null && voiceBeans.size() > 0) {
                VoiceBean itemData = null;
                if (voiceBeans.size() == 1) {
                    itemData = voiceBeans.get(voiceBeans.size() - 1);
                } else {
                    itemData = voiceBeans.get(new Random().nextInt(voiceBeans.size()));
                }

                refVoice(itemData);
            } else {
                onCompleted();
            }
        }
    }

    private Set<VoiceBean> likeVoiceByKeyword(VoiceBean voiceBean) {

        Set<VoiceBean> voiceBeans = new HashSet<>();
        voiceBeans.add(voiceBean);

        String keyword = voiceBean.getKeyword();
        List<String> keywords = GsonUtil.GsonToList(keyword, String.class);

        if (keywords != null && keywords.size() > 0) {

            for (String key : keywords) {
                voiceBeans.addAll(mVoiceDBManager.queryWhereOr(key));
            }
        }
        voiceBeans.remove(voiceBean);
        return voiceBeans;
    }


    private void onRecognResultBack() {
        //返回
        onCompleted();
    }

    private void onRecognResultHome(SpecialType specialType) {
        //点击下面四个按钮
        switch (specialType) {
            case HomePage:
                onTabSelected(0);
                break;
            case ConsultingPosition:
                onTabSelected(1);
                break;
            case MoreProblems:
                onTabSelected(2);
                break;
            case SetUp:
                onTabSelected(3);
                break;
        }
    }

    private void onRecognResultSet(SpecialType specialType) {
        //设置里面
        if (mPosition == 3) {
            SettingFragment settingFragment = (SettingFragment) mFragments.get(mPosition);
            settingFragment.refSetting(specialType);
        }
    }

    private void onRecognResultSure(SpecialType specialType) {
        //确定，取消
        onCompleted();
    }

    public void onCompleted() {
        myRecognizer.start();
    }

    public void stopEvery() {
        mySynthesizer.stop();
        myRecognizer.stop();
    }

    public void addSpeakAnswer(String messageContent) {
        mySynthesizer.speak(messageContent);

        mHandler.removeCallbacks(previewRunnable);
        mHandler.postDelayed(previewRunnable, DELAY_MILLIS);
    }

    Runnable previewRunnable = new Runnable() {
        @Override
        public void run() {
            eliminate();
        }
    };


    public void jumpFragment(int position, VoiceBean voiceBean) {

        showFragment(position);
        navigationBar.selectTab(position);

        refVoice(mVoiceDBManager.selectByPrimaryKey(voiceBean.getId()));
    }

    @Override
    public void onTabSelected(int position) {


        mData.clear();
        multiAdapter.replaceData(mData);

        if (position == 0) {
            showFragment(position);
            HomeFragment rxSupportFragment = (HomeFragment) mFragments.get(position);
            rxSupportFragment.loadData();
        } else if (position == 1) {

            showFragment(position);
            loadLocal();
        } else if (position == 2) {
            HelpFragment rxSupportFragment = (HelpFragment) mFragments.get(position);
            rxSupportFragment.loadData();
            showFragment(position);
        } else if (position == 3) {
            judgeCipher();
        }

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(SerialForActivityEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            onDataReceiverd(serialBean);
        } else {
            Log.e("ReceiveEvent error");
        }
    }

    private void distinguish() {
        surfaceView.setVisibility(View.GONE);
        stopEvery();
        addSpeakAnswer("欢迎光临");
    }

    private void onDataReceiverd(SerialBean serialBean) {
        int iBaudRate = serialBean.getBaudRate();
        String motion = serialBean.getMotion();
        if (iBaudRate == SerialService.VOICE_BAUDRATE) {

            if (motion.toString().contains("WAKE UP!")) {
                stopEvery();
                showDisplay();
                onCompleted();
                receiveMotion(SerialService.VOICE_BAUDRATE, "BEAM 0\n\r");//0
                surfaceView.setVisibility(View.GONE);
            }
        }
    }

    private void receiveMotion(int type, String motion) {
        SerialBean serialBean = new SerialBean();
        serialBean.setBaudRate(type);
        serialBean.setMotion(motion);
        SerialForServiceEvent serialEvent = new SerialForServiceEvent("");
        serialEvent.setEvent(200, serialBean);
        EventBus.getDefault().post(serialEvent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
        doStartPreview();
        mHandler.postDelayed(previewRunnable, DELAY_MILLIS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void openCamera() {
        if (null != mCamera) {
            return;
        }
        if (Camera.getNumberOfCameras() == 1) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
            closeCamera();
            return;
        }
    }

    public void closeCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    public void doStartPreview() {
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                List<Camera.Size> pictures = parameters.getSupportedPictureSizes();
                //图片质量
                if (pictures.size() > 1) {
                    Iterator<Camera.Size> itor = pictures.iterator();
                    while (itor.hasNext()) {
                        Camera.Size curPicture = itor.next();
                        Log.i("curPicture : " + "w : " + curPicture.width + " , h : " + curPicture.height);
                    }
                }
                pictureWidth = pictures.get(0).width;
                pictureHeight = pictures.get(0).height;
                parameters.setPictureSize(pictureWidth, pictureHeight);

                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains("continuous-video")) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                parameters.setPreviewFormat(ImageFormat.NV21);

                List<Camera.Size> previews = parameters.getSupportedPreviewSizes();
                // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
                if (previews.size() > 1) {
                    Iterator<Camera.Size> itor = previews.iterator();
                    while (itor.hasNext()) {
                        Camera.Size curPreview = itor.next();
                        Log.i("curPreview : " + "w : " + curPreview.width + " , h : " + curPreview.height);
                    }
                }
//                previewWidth = previews.get(0).width;
//                previewHeight = previews.get(0).height;
                parameters.setPreviewSize(previewWidth, previewHeight);

                parameters.set("jpeg-quality", 85);//设置照片质量

                mCamera.setParameters(parameters);

                // 设置显示的偏转角度，大部分机器是顺时针90度，某些机器需要按情况设置
                orientionOfCamera = CameraUtils.getInstance().getCameraDisplayOrientation(this, mCameraId);

                mCamera.setDisplayOrientation(orientionOfCamera);
                mCamera.startPreview();
                mCamera.setPreviewCallback(this);

                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();//开启预览
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPreviewing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

        Camera.Size size = camera.getParameters().getPreviewSize();
        YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, size.width, size.height), 80, baos);
        byte[] byteArray = baos.toByteArray();

        Bitmap previewBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        int width = previewBitmap.getWidth();
        int height = previewBitmap.getHeight();

        Matrix matrix = new Matrix();

        FaceDetector detector = null;
        Bitmap faceBitmap = null;

        detector = new FaceDetector(previewBitmap.getWidth(), previewBitmap.getHeight(), 10);
        int oriention = 360 - orientionOfCamera;
        if (oriention == 360) {
            oriention = 0;
        }
        switch (oriention) {
            case 0:
                detector = new FaceDetector(width, height, 10);
                matrix.postRotate(0.0f, width / 2, height / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
            case 90:
                detector = new FaceDetector(height, width, 1);
                matrix.postRotate(-270.0f, height / 2, width / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
            case 180:
                detector = new FaceDetector(width, height, 1);
                matrix.postRotate(-180.0f, width / 2, height / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
            case 270:
                detector = new FaceDetector(height, width, 1);
                matrix.postRotate(-90.0f, height / 2, width / 2);
                faceBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, width, height, matrix, true);
                break;
        }

        Bitmap copyBitmap = faceBitmap.copy(Bitmap.Config.RGB_565, true);

        FaceDetector.Face[] faces = new FaceDetector.Face[10];
        int faceNumber = detector.findFaces(copyBitmap, faces);
        if (faceNumber > 0) {

            Log.e("isPreviewFrame " + isPreviewFrame + "   count :" + count);

            if (!isPreviewFrame) {
                return;
            }

            count++;

            if (count == 2) {

                isPreviewFrame = false;
                distinguish();
            }

        } else {
            Log.i("camera no face");
        }

        copyBitmap.recycle();
        faceBitmap.recycle();
        previewBitmap.recycle();
    }

}

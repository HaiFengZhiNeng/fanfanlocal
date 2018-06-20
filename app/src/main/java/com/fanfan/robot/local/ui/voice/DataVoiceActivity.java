package com.fanfan.robot.local.ui.voice;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.adapter.VoiceDataAdapter;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.db.manager.VoiceDBManager;
import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 语音数据页面
 */
public class DataVoiceActivity extends BaseActivity {

    @BindView(R.id.ptr_framelayout)
    PtrFrameLayout mPtrFrameLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, DataVoiceActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private VoiceDBManager mVoiceDBManager;

    private List<VoiceBean> voiceBeanList = new ArrayList<>();

    private VoiceDataAdapter voiceDataAdapter;

    private int updatePosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_voice;
    }

    @Override
    protected void initData() {

        initToolbar();
        initAdapter();
        loadData();
    }

    private void loadData() {
        voiceBeanList = mVoiceDBManager.loadAll();
        mPtrFrameLayout.refreshComplete();
        if (voiceBeanList != null && voiceBeanList.size() > 0) {
            isNuEmpty();
            voiceDataAdapter.replaceData(voiceBeanList);
        } else {
            isEmpty();
        }
    }

    private void initAdapter() {
        mPtrFrameLayout.disableWhenHorizontalMove(true);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, recyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, 200);
            }
        });

        voiceDataAdapter = new VoiceDataAdapter(voiceBeanList);
        voiceDataAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showNeutralNotitleDialog(position);
                return false;
            }
        });
        voiceDataAdapter.openLoadAnimation();

        recyclerView.setAdapter(voiceDataAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mVoiceDBManager = new VoiceDBManager();
        updatePosition = -1;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_white, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                AddVoiceActivity.newInstance(this, AddVoiceActivity.ADD_VOICE_REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddVoiceActivity.ADD_VOICE_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra(AddVoiceActivity.RESULT_CODE, -1);
                if (id != -1) {
                    isNuEmpty();
                    VoiceBean bean = mVoiceDBManager.selectByPrimaryKey(id);
                    if (updatePosition == -1) {
                        if (voiceBeanList.size() == 0) {
                            isNuEmpty();
                        }
                        voiceBeanList.add(bean);
                        voiceDataAdapter.addData(bean);
                    } else {
                        voiceBeanList.remove(updatePosition);
                        voiceBeanList.add(updatePosition, bean);
                        voiceDataAdapter.remove(updatePosition);
                        voiceDataAdapter.addData(updatePosition, bean);
                        updatePosition = -1;
                    }
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(this, "选择您要执行的操作", "删除所有",
                "删除此条", "修改此条", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mVoiceDBManager.deleteAll()) {
                            voiceBeanList.clear();
                            voiceDataAdapter.replaceData(voiceBeanList);
                        }
                    }

                    @Override
                    public void negativeText() {
                        if (mVoiceDBManager.delete(voiceBeanList.get(position))) {
                            voiceBeanList.remove(position);
                            voiceDataAdapter.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        updatePosition = position;
                        VoiceBean voiceBean = voiceBeanList.get(updatePosition);
                        AddVoiceActivity.newInstance(DataVoiceActivity.this, voiceBean.getId(), AddVoiceActivity.ADD_VOICE_REQUESTCODE);
                    }
                });
    }

    protected void isEmpty() {
        RelativeLayout rlLayout = findViewById(R.id.rl_empty);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        if (rlLayout != null && tvEmpty != null) {
            rlLayout.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    protected void isNuEmpty() {
        RelativeLayout rlLayout = findViewById(R.id.rl_empty);
        TextView tvEmpty = findViewById(R.id.tv_empty);
        if (rlLayout != null && tvEmpty != null) {
            rlLayout.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

}

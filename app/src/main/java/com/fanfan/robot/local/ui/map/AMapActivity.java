package com.fanfan.robot.local.ui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.offlinemap.OfflineMapActivity;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.utils.map.AMapUtil;
import com.fanfan.robot.local.utils.map.PoiOverlay;
import com.robot.seabreeze.log.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;

public class AMapActivity extends BaseActivity implements SearchView.OnQueryTextListener, PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener {

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.map_view)
    MapView mapView;

    private AMap aMap;
    public static final int ZOOM = 15;

    private SearchView mSearchView;
    private SearchView.SearchAutoComplete mSearchAutoComplete;

    private String keyWord;
    private String cityCode = "武汉";

    // 当前页面，从0开始计数
    private int currentPage = 0;
    // Poi查询条件类
    private PoiSearch.Query query;
    // POI搜索
    private PoiSearch poiSearch;
    // poi返回的结果
    private PoiResult poiResult;


    private MaterialDialog progDialog = null;// 搜索时进度条

    private PopupWindow popupWindow;
    private boolean popIsshow;

    private RecyclerView mRecyclerView;
    private BaseQuickAdapter baseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        initMap();
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_map;
    }

    @Override
    protected void initData() {
        initToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchAutoComplete.isShown()) {
                    closeImm();
                } else {
                    finish();
                }
//                startActivity(new Intent(AMapActivity.this, OfflineMapActivity.class));
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        //通过MenuItem得到SearchView
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint("搜索相关地点");

        try {
            Class cls = Class.forName("android.support.v7.widget.SearchView");
            Field field = cls.getDeclaredField("mSearchSrcTextView");
            field.setAccessible(true);
            TextView tv = (TextView) field.get(mSearchView);
            Class[] clses = cls.getDeclaredClasses();
            for (Class cls_ : clses) {
                if (cls_.toString().endsWith("android.support.v7.widget.SearchView$SearchAutoComplete")) {
                    Class targetCls = cls_.getSuperclass().getSuperclass().getSuperclass().getSuperclass();
                    Field cuosorIconField = targetCls.getDeclaredField("mCursorDrawableRes");
                    cuosorIconField.setAccessible(true);
                    cuosorIconField.set(tv, R.drawable.cursor_color);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        //设置输入框提示文字样式
        mSearchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        mSearchAutoComplete.setTextColor(getResources().getColor(android.R.color.background_light));
        mSearchAutoComplete.setTextSize(14);
        //设置触发查询的最少字符数（默认2个字符才会触发查询）
        mSearchAutoComplete.setThreshold(1);
        //设置搜索框有字时显示叉叉，无字时隐藏叉叉
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);
        mSearchView.setIconifiedByDefault(false);
        //修改搜索框控件间的间隔（这样只是为了更加接近网易云音乐的搜索框）
        LinearLayout search_edit_frame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search_edit_frame.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 10;
        search_edit_frame.setLayoutParams(params);

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImm();
                mSearchView.setIconifiedByDefault(false);
            }
        });

        closeImm();
        return super.onCreateOptionsMenu(menu);
    }

    private void openImm() {
        tvToolbar.setVisibility(View.GONE);

        mSearchView.requestFocusFromTouch();
        mSearchView.setFocusable(true);
        mSearchView.setFocusableInTouchMode(true);
        mSearchView.requestFocus();
    }

    private void closeImm() {
        try {
            mSearchAutoComplete.setText("");
            Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
            method.setAccessible(true);
            method.invoke(mSearchView);
            mSearchView.clearFocus();
            mSearchView.setIconifiedByDefault(true);

            tvToolbar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!AMapUtil.IsEmptyOrNullString(keyWord)) {
            doSearchQuery();
            closeImm();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        keyWord = newText.trim();
        if (!AMapUtil.IsEmptyOrNullString(keyWord)) {
            InputtipsQuery inputquery = new InputtipsQuery(keyWord, "");
            Inputtips inputTips = new Inputtips(this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
        return false;
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
//        mBusResultLayout.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);
        dismissPopWindow();
        showProgressDialog("正在搜索:\n" + keyWord);// 显示进度框
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyWord, "", cityCode);
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String message) {
        if (progDialog == null) {
            progDialog = new MaterialDialog.Builder(this)
                    .title("请稍后")
                    .content(message)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
        } else {
            progDialog.setTitle(message);
            progDialog.show();
        }
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            // 搜索poi的结果
            if (result != null && result.getQuery() != null) {
                // 是否是同一条
                if (result.getQuery().equals(query)) {
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页, 取得第一页的poiitem数据，页数从数字0开始
                    List<PoiItem> poiItems = poiResult.getPois();
                    // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    List<SuggestionCity> cities = poiResult.getSearchSuggestionCitys();
                    if (poiItems != null && poiItems.size() > 0) {
                        Log.e("搜索结果为 ： " + poiItems.size() + " 条");
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (cities != null && cities.size() > 0) {
                        String infomation = "推荐城市\n";
                        for (int i = 0; i < cities.size(); i++) {
                            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:" + cities.get(i).getCityCode()
                                    + "城市编码:" + cities.get(i).getAdCode() + "\n";
                        }
                        showToast(infomation);
                    } else {
                        showToast(R.string.no_result);
                    }
                }
            } else {
                showToast(R.string.no_result);
            }
        } else {
            showToast(rCode);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        // 正确返回
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            Log.e("onGetInputtips : " + tipList.size());
            if (popIsshow) {
                setAdapter(tipList);
            } else {
                showPopupWindow();
                setAdapter(tipList);
            }
        } else {
            showToast(rCode);
        }
    }

    private void showPopupWindow() {
        popIsshow = true;
        View contentView = LayoutInflater.from(AMapActivity.this).inflate(R.layout.map_pop_list_layout, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.pop_recycler_view);
        baseAdapter = null;
        int xPos = 100;

        popupWindow = new PopupWindow(contentView, Constants.displayWidth - (2 * xPos),
                Constants.displayHeight / 2, true);

        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        ColorDrawable dw = new ColorDrawable(00000000);
        popupWindow.setBackgroundDrawable(dw);
//        backgroundAlpha(0.5f);// 设置背景半透明
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(line, xPos, 0);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popIsshow = false;
            }
        });
    }

    private void setAdapter(final List<Tip> tipList) {
        if (baseAdapter == null) {
            baseAdapter = new BaseQuickAdapter<Tip, BaseViewHolder>(R.layout.route_inputs, tipList) {
                @Override
                protected void convert(BaseViewHolder helper, Tip item) {
                    helper.setText(R.id.tv_route, item.getName().trim());
                }
            };
            baseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Tip tip = tipList.get(position);
                    keyWord = tip.getName().trim();
                    if (!AMapUtil.IsEmptyOrNullString(keyWord)) {
                        doSearchQuery();
                        closeImm();
                    }
                }
            });
            baseAdapter.isFirstOnly(false); //设置不仅是首次填充数据时有动画,以后上下滑动也会有动画
            baseAdapter.openLoadAnimation();
            mRecyclerView.setAdapter(baseAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        } else {
            baseAdapter.replaceData(tipList);
        }
    }

    private void dismissPopWindow() {
        if (popIsshow) {
            popIsshow = false;
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    }


}

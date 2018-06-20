package com.fanfan.robot.local.ui.main.fragment.location;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.common.base.MainFragment;
import com.fanfan.robot.local.model.LocalBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 此电脑\荣耀Waterplay\内部存储\amap\data_v6\map\a173
 */
public class LocationFragment extends MainFragment implements AMap.InfoWindowAdapter {

    private MapView mapView;

    public static LocationFragment newInstance() {
        Bundle bundle = new Bundle();
        LocationFragment locationFragment = new LocationFragment();
        locationFragment.setArguments(bundle);
        return locationFragment;
    }

    private AMap aMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.location_fragment;
    }

    @Override
    protected void initEventAndData() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setInfoWindowAdapter(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void changeCamera(LocalBean localBean) {

        LatLng latLng = new LatLng(localBean.getLat(), localBean.getLng());


        aMap.clear();

        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(latLng)
                .draggable(true);
        Marker marker = aMap.addMarker(markerOption);
        marker.setObject(localBean);
        marker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 16, 30, 30));
        aMap.moveCamera(cameraUpdate);
    }


    public void refLocal(LocalBean localBean) {

        changeCamera(localBean);

        onCompleted();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        LocalBean localBean = (LocalBean) marker.getObject();
        View infoWindow = LayoutInflater.from(getActivity()).inflate(R.layout.pop_layout, null);

        TextView title = infoWindow.findViewById(R.id.title);
        title.setText(localBean.getShowTitle());
        TextView detail = infoWindow.findViewById(R.id.detail);
        detail.setText(localBean.getShowDetail());
        TextView telephone = infoWindow.findViewById(R.id.telephone);
        telephone.setText(localBean.getTelephone());
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

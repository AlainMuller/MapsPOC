package fr.alainmuller.mapspoc.both;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.mapapi.map.MapView;

public class BaiduMapView implements IMapView {
    @NonNull
    private final MapView mMapView;

    @Nullable
    private BaiduMap mMap;

    public BaiduMapView (@NonNull MapView mapView) {
        mMapView = mapView;
    }

    @NonNull
    @Override
    public View getView() {
        return mMapView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        if (mMap != null) {
            mMap.dispose();
            mMap = null;
        }
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void getMapAsync(@NonNull final IOnMapReadyCallback callback) {
        //to avoid calling the callBack directly
        mMapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMap == null) mMap = new BaiduMap(mMapView.getMap());
                callback.onMapReady(mMap);
            }
        }, 100);
    }
}

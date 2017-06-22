package fr.alainmuller.mapspoc.both;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;

public class GoogleMapView implements IMapView {
    @NonNull
    private final MapView mMapView;
    @Nullable
    private fr.alainmuller.mapspoc.both.GoogleMap mMap;

    public GoogleMapView(@NonNull MapView mapView) {
        mMapView = mapView;
    }

    @NonNull
    @Override
    public View getView() {
        return mMapView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
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
        mMap = null;
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void getMapAsync(@NonNull final IOnMapReadyCallback callback) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (mMap == null) mMap = new fr.alainmuller.mapspoc.both.GoogleMap(googleMap);
                callback.onMapReady(mMap);
            }
        });
    }
}

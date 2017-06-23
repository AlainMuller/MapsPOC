package fr.alainmuller.mapspoc.both;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import fr.alainmuller.mapspoc.MapsActivity;
import fr.alainmuller.mapspoc.R;

/**
 * Created by jvermet on 22/06/2017.
 */

public class BaiduMapActivity extends AbsMapActivity {

    private PatternView mPatternView;
    private MapLayout mMapLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu);

        mMapLayout = (MapLayout) findViewById(R.id.layout_map);
        mPatternView = (PatternView) findViewById(R.id.pattern);

        mMapView = MapViewFactory.create(mMapLayout, false);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new IOnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull IMap map) {
                mIMap = map;
                mPatternView.setMap(map);

                map.setOnCameraChangeListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        mPatternView.updateGeofencingOverlay();
                    }
                });

                moveToUFO();
            }
        });
    }

}
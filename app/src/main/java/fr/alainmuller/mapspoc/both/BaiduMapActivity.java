package fr.alainmuller.mapspoc.both;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import fr.alainmuller.mapspoc.MapsActivity;
import fr.alainmuller.mapspoc.PatternView;
import fr.alainmuller.mapspoc.R;

/**
 * Created by jvermet on 22/06/2017.
 */

public class BaiduMapActivity extends Activity {

    private PatternView mPatternView;
    MapLayout mMapLayout;
    IMapView mMapView;

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
                mPatternView.setMap(map);

                map.setOnCameraChangeListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        mPatternView.updateGeofencingOverlay();
                    }
                });

                CameraPosition cameraPosition = new CameraPosition.Builder().target(MapsActivity.UFO).zoom(15).build();
                map.moveCamera(cameraPosition);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
}
package fr.alainmuller.mapspoc.both;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import fr.alainmuller.mapspoc.MapsActivity;
import fr.alainmuller.mapspoc.R;

/**
 * Created by jvermet on 23/06/2017.
 */

public abstract class AbsMapActivity extends Activity {

    public static final LatLng HOME = new LatLng(48.116050, -1.602749);
    public static final LatLng UFO = new LatLng(48.116242, -1.604080);
    public static final int GEOFENCE_LIMIT_METERS = 2000;

    protected IMapView mMapView;
    protected IMap mIMap;
    protected PatternView mPatternView;
    protected MapLayout mMapLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parrot_maps);

        mMapLayout = (MapLayout) findViewById(R.id.layout_map);
        mPatternView = (PatternView) findViewById(R.id.pattern);

        mMapView = MapViewFactory.create(mMapLayout, isGoogleMap());
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new IOnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final IMap map) {
                mIMap = map;
                mPatternView.setMap(map);
                mPatternView.setGeofencingRadiusMeters(GEOFENCE_LIMIT_METERS);
                mPatternView.setCenterLocation(HOME);

                map.setOnCameraChangeListener(new com.google.android.gms.maps.GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        mPatternView.updateGeofencingOverlay();
                    }
                });

                moveToUFO();
                addHomeMarker();
                addMarkerDragListener();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    protected void addHomeMarker() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home);
        mIMap.addMarker(HOME, bitmap, 0, true);
    }

    protected void moveToUFO() {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(MapsActivity.HOME).zoom(14).build();
        mIMap.moveCamera(cameraPosition);
    }

    protected void checkMarkerDragPosition(LatLng markerPosition) {
        if (isMarkerDragAllowed(markerPosition)) {
            mPatternView.setGrayPattern();
        } else {
            mPatternView.setRedPattern();
        }
    }

    private boolean isMarkerDragAllowed(LatLng markerPosition) {
        float[] distance = new float[1];
        Location.distanceBetween(markerPosition.latitude, markerPosition.longitude, HOME.latitude, HOME.longitude, distance);
        return distance[0] <= GEOFENCE_LIMIT_METERS;
    }

    protected void addMarkerDragListener() {
        mIMap.setOnMarkerDragListener(new OnMarkerDragListener() {
            private LatLng mOriginalPosition;
            @Override
            public void onMarkerDragStart(IMarker marker) {
                mOriginalPosition = marker.getPosition();
            }

            @Override
            public void onMarkerDrag(IMarker marker) {
                checkMarkerDragPosition(marker.getPosition());
            }

            @Override
            public void onMarkerDragEnd(IMarker marker) {
                if (!isMarkerDragAllowed(marker.getPosition())) {
                    marker.setPosition(mOriginalPosition);
                    checkMarkerDragPosition(mOriginalPosition);
                }
            }
        });
    }

    protected abstract boolean isGoogleMap();

}
package fr.alainmuller.mapspoc.both;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.WeakHashMap;

import fr.alainmuller.mapspoc.MapsActivity;
import fr.alainmuller.mapspoc.R;

/**
 * Created by jvermet on 23/06/2017.
 */

public abstract class AbsMapActivity extends Activity {

    public static final int HOME_ID = 1;
    public static final int UFO_ID = 2;
    public static final int USER_ID = 3;

    public static final LatLng HOME = new LatLng(48.116050, -1.602749);
    public static final LatLng USER = new LatLng(48.116242, -1.623080);
    public static final LatLng UFO = new LatLng(48.115485, -1.602958);
    public static final int GEOFENCE_LIMIT_METERS = 200;

    protected IMapView mMapView;
    protected IMap mIMap;
    protected PatternView mPatternView;
    protected MapLayout mMapLayout;
    protected HashMap<IMarker, Integer> mMarkers = new HashMap<>();

    private LatLng mHomePosition = HOME;
    private LatLng mUfoPosition = UFO;
    private LatLng mUserPosition = USER;

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
                mIMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                mPatternView.setMap(map);
                mPatternView.setGeofencingRadiusMeters(GEOFENCE_LIMIT_METERS);
                mPatternView.setCenterLocation(HOME);

                map.setOnCameraChangeListener(new com.google.android.gms.maps.GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        updateGeofence();
                    }
                });

                addUFOMarker();
                centerView();
                addHomeMarker();
                addMarkerDragListener();
                addDroneMarker();
                addUserMarker();
                updateCamera();

                onMapLoaded();
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

    protected void addUFOMarker() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aeronef);
        mIMap.addMarker(UFO, bitmap, false, 100, 0.5f, 0.7f);
    }

    protected void addHomeMarker() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home);
        IMarker marker = mIMap.addMarker(HOME, bitmap, true);
        marker.setId(HOME_ID);
    }

    protected void addDroneMarker() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aeronef);
        IMarker marker = mIMap.addMarker(UFO, bitmap, true);
        marker.setId(UFO_ID);
    }

    protected void addUserMarker() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        IMarker marker = mIMap.addMarker(USER, bitmap, true);
        marker.setId(USER_ID);
    }

    protected void centerView() {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(MapsActivity.HOME).zoom(17.2f).build();
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
                if (marker.getId() == HOME_ID) {
                    mOriginalPosition = marker.getPosition();
                }
            }

            @Override
            public void onMarkerDrag(IMarker marker) {
                if (marker.getId() == HOME_ID) {
                    checkMarkerDragPosition(marker.getPosition());
                }
            }

            @Override
            public void onMarkerDragEnd(IMarker marker) {
                LatLng position = marker.getPosition();
                int id = marker.getId();
                if (id == HOME_ID) {
                    if (!isMarkerDragAllowed(marker.getPosition())) {
                        marker.setPosition(mOriginalPosition);
                        checkMarkerDragPosition(mOriginalPosition);
                        return;
                    }
                    mHomePosition = position;
                } else if (id == UFO_ID) {
                    mUfoPosition = position;
                } else if (id == USER_ID) {
                    mUserPosition = position;
                }
                updateCamera();
            }
        });
    }

    private void updateCamera() {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(mHomePosition)
                .include(getOppositePositionFromUFO(mHomePosition))
                .include(mUfoPosition)
                .include(mUserPosition)
                .include(getOppositePositionFromUFO(mUserPosition))
                .build();
        mIMap.moveCamera(latLngBounds, 100);
        updateGeofence();
    }

    private LatLng getOppositePositionFromUFO(LatLng latLng) {
        double latitude = mUfoPosition.latitude + (mUfoPosition.latitude - latLng.latitude);
        double longitude = mUfoPosition.longitude + (mUfoPosition.longitude - latLng.longitude);
        return new LatLng(latitude, longitude);
    }

    private void updateGeofence() {
        mPatternView.updateGeofencingOverlay();
    }

    protected abstract boolean isGoogleMap();

    protected void onMapLoaded() {

    }

}
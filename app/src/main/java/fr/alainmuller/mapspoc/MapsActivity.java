package fr.alainmuller.mapspoc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.golovin.googlemapmask.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

import fr.alainmuller.mapspoc.both.BaiduMapActivity;
import fr.alainmuller.mapspoc.both.GoogleMapActivity;
import fr.alainmuller.mapspoc.both.PatternView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, SensorEventListener {

    private static final String LOG_TAG = MapsActivity.class.getSimpleName();

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    // Location constants
    public static final LatLng HOME = new LatLng(48.116050, -1.602749);
    public static final LatLng UFO = new LatLng(48.116242, -1.604080);
    public static final LatLng USER = new LatLng(48.115485, -1.602958);
    public static final int GEOFENCE_LIMIT_METERS = 200;

    private GoogleMap mMap;
    private Marker mPositionMarker;

    private PatternView mPatternView;
    SupportMapFragment mapFragment;
    private Polyline mRTHLine;

    private BitmapDescriptor mBitmapUserOriented;

    private LocationManager mLocationManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Sensor mMagnetometerSensor;

    private float[] mGravity;
    private float[] mGeomagnetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mPatternView = (PatternView) findViewById(R.id.pattern);

        findViewById(R.id.google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, GoogleMapActivity.class));
            }
        });
        findViewById(R.id.baidu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, BaiduMapActivity.class));
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mBitmapUserOriented = BitmapDescriptorFactory.fromResource(R.drawable.user_oriented);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Enabling MyLocation Layer of Google Map
        if (checkLocationPermission()) {
            turnOnMyLocation();
            turnOnMyOrientation();
        }

        // Add a (draggable) marker in Rennes and move the camera
        mMap.addMarker(new MarkerOptions().position(HOME)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                .anchor(0.5f, 0.5f)
                .draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HOME, 17.2f));

        // Adjust RTH line when HOME marker is moved
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // Do nothing
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                Log.d(LOG_TAG, "onMarkerDragEnd");
                // TODO : handle geofencing (reset marker if out of geofence barrier)
                // Uncomment to center map on new HOME position
                // mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                if (mRTHLine != null && !mRTHLine.getPoints().isEmpty() && mRTHLine.isVisible()) {
                    // Replace end position of RTH line
                    final List<LatLng> positions = mRTHLine.getPoints();
                    positions.set(mRTHLine.getPoints().size() - 1, arg0.getPosition());
                    mRTHLine.setPoints(positions);
                    Log.d(LOG_TAG, "onMarkerDrag : " + Arrays.toString(mRTHLine.getPoints().toArray()));
                }
            }
        });

        // Add a polygon with a hole
        googleMap.addPolygon(MapHelper.createPolygonWithCircle(this, HOME, 0.2f));

        // Add flying object marker
        mMap.addMarker(new MarkerOptions().position(UFO)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.aeronef))
                .anchor(0.5f, 0.7f)
                .rotation(100));

        // Add RTH line between flying object and home
        mRTHLine = mMap.addPolyline(new PolylineOptions().add(UFO, HOME));
        MapHelper.stylePolyline(mRTHLine, true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        turnOnMyLocation();
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                // Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    /*
     * *********************************************************************************************
     * PRIVATE METHODS
     * *********************************************************************************************
     */

    private void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final long duration = 200;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    private void turnOnMyLocation() {
        Log.d(LOG_TAG, "turnOnMyLocation");

        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        final Criteria criteria = new Criteria();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final String locationProvider = mLocationManager.getBestProvider(criteria, false);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        final Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            onLocationChanged(location);
        }
    }

    private void turnOnMyOrientation() {
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /*
     * *********************************************************************************************
     * LOCATION LISTENER METHODS
     * *********************************************************************************************
     */

    @Override
    public void onLocationChanged(final Location location) {
        Log.d(LOG_TAG, "onLocationChanged");
        if (mPositionMarker == null) {
            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(mBitmapUserOriented)
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(location.getLatitude(), location
                                    .getLongitude())));
        }

        animateMarker(mPositionMarker, location);
    }

    @Override
    public void onStatusChanged(final String s, final int i, final Bundle bundle) {
        // Nothing to do
        Log.d(LOG_TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(final String s) {
        // Nothing to do
        Log.d(LOG_TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(final String s) {
        // Nothing to do
        Log.d(LOG_TAG, "onProviderDisabled");
    }

    /*
     * *********************************************************************************************
     * SENSOR EVENT LISTENER METHODS
     * *********************************************************************************************
     */

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                final float azimut = orientation[0];
                if (mPositionMarker != null) {
                    mPositionMarker.setRotation((float) (azimut * (180 / Math.PI)));
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // Nothing to do
    }
}

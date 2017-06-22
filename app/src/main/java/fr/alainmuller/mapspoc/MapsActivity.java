package fr.alainmuller.mapspoc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.golovin.googlemapmask.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Location constants
    private static final LatLng HOME = new LatLng(48.116050, -1.602749);
    private static final LatLng UFO = new LatLng(48.116242, -1.604080);

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a (draggable) marker in Rennes and move the camera
        mMap.addMarker(new MarkerOptions().position(HOME)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                .draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HOME, 17.2f));

        // Add a polygon with a hole
        googleMap.addPolygon(MapHelper.createPolygonWithCircle(this, HOME, 0.2f));

        // Add flying object marker
        mMap.addMarker(new MarkerOptions().position(UFO)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.aeronef))
                .rotation(100));

        // Add RTH line between flying object and home
        Polyline polyline = mMap.addPolyline(new PolylineOptions().add(UFO, HOME));
        MapHelper.stylePolyline(polyline, true);
    }
}

package fr.alainmuller.mapspoc.both;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import fr.alainmuller.mapspoc.MapsActivity;
import fr.alainmuller.mapspoc.R;

/**
 * Created by jvermet on 23/06/2017.
 */

public class AbsMapActivity extends Activity {

    public static final LatLng HOME = new LatLng(48.116050, -1.602749);
    public static final LatLng UFO = new LatLng(48.116242, -1.604080);

    protected IMapView mMapView;
    protected IMap mIMap;

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
        CameraPosition cameraPosition = new CameraPosition.Builder().target(MapsActivity.UFO).zoom(14).build();
        mIMap.moveCamera(cameraPosition);
    }

}

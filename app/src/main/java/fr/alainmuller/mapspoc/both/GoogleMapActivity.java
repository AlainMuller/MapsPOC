package fr.alainmuller.mapspoc.both;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import fr.alainmuller.mapspoc.R;

/**
 * Created by jvermet on 22/06/2017.
 */

public class GoogleMapActivity extends AbsMapActivity {

    @Override
    protected boolean isGoogleMap() {
        return true;
    }

    @Override
    protected void onMapLoaded() {
        super.onMapLoaded();
    }

}
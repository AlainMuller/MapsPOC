package fr.alainmuller.mapspoc.both;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.BaiduMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jvermet on 22/06/2017.
 */

public class BaiduMapActivity extends AbsMapActivity {

    @Override
    protected boolean isGoogleMap() {
        return false;
    }
}
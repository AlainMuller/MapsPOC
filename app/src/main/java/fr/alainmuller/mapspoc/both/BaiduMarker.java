package fr.alainmuller.mapspoc.both;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.baidu.mapapi.map.Marker;
import com.google.android.gms.maps.model.LatLng;

/*package*/ class BaiduMarker implements IMarker {

    public static final String EXTRA_ID = "extra_id";

    @NonNull
    private final Marker mMarker;

    public BaiduMarker(@NonNull Marker marker) {
        mMarker = marker;
    }

    @Override
    public void setPosition(@NonNull LatLng latlng) {
        mMarker.setPosition(ConvertUtils.convert(latlng));
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return ConvertUtils.convert(mMarker.getPosition());
    }

    @Override
    public void setAnchor(float anchorU, float anchorV) {
        mMarker.setAnchor(anchorU, anchorV);
    }

    @Override
    public void setRotation(float angle) {
        mMarker.setRotate(angle);
    }

    @Override
    public void remove() {
        mMarker.remove();
    }

    @Override
    public void setId(int id) {
        Bundle extras = new Bundle();
        extras.putInt(EXTRA_ID, id);
        mMarker.setExtraInfo(extras);
    }

    @Override
    public int getId() {
        Bundle extras = mMarker.getExtraInfo();
        return extras.getInt(EXTRA_ID, -1);
    }

}

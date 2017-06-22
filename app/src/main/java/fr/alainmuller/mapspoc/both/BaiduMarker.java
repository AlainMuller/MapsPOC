package fr.alainmuller.mapspoc.both;

import android.support.annotation.NonNull;

import com.baidu.mapapi.map.Marker;
import com.google.android.gms.maps.model.LatLng;

/*package*/ class BaiduMarker implements IMarker {
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
}

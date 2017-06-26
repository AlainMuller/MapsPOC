package fr.alainmuller.mapspoc.both;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/*package*/ class GoogleMarker implements IMarker {
    @NonNull
    private final Marker mMarker;

    public GoogleMarker(@NonNull Marker marker) {
        mMarker = marker;
    }

    @Override
    public void setPosition(@NonNull LatLng latlng) {
        mMarker.setPosition(latlng);
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return mMarker.getPosition();
    }

    @Override
    public void setAnchor(float anchorU, float anchorV) {
        mMarker.setAnchor(anchorU, anchorV);
    }

    @Override
    public void setRotation(float angle) {
        mMarker.setRotation(angle);
    }

    @Override
    public void remove() {
        mMarker.remove();
    }

    @Override
    public void setId(int id) {
        mMarker.setTag(id);
    }

    @Override
    public int getId() {
        return (int) mMarker.getTag();
    }
}

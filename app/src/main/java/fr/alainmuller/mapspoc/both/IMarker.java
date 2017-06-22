package fr.alainmuller.mapspoc.both;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public interface IMarker {
    void setPosition(@NonNull LatLng latlng);
    @NonNull LatLng getPosition();

    void setAnchor(float anchorU, float anchorV);
    void setRotation(float angle);

    void remove();
}

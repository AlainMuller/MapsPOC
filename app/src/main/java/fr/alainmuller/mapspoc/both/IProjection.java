package fr.alainmuller.mapspoc.both;

import android.graphics.Point;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public interface IProjection {
    @NonNull
    LatLng fromScreenLocation(@NonNull Point point);

    @NonNull
    Point toScreenLocation(@NonNull LatLng latLng);
}

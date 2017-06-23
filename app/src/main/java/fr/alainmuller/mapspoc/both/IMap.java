package fr.alainmuller.mapspoc.both;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public interface IMap {
    //mapLoad callback may not be triggered (Google Map)
    boolean isMapLoadMayBeFailed();

    void setMyLocationEnabled(boolean enable);
    void setBuildingsEnabled(boolean enable);
    void setTrafficEnabled(boolean enable);
    void setIndoorEnabled(boolean enable);
    void setOnMapLoadedCallback(@Nullable GoogleMap.OnMapLoadedCallback callback);
    void setAllGesturesEnabled(boolean enable);
    void setPadding(int left, int top, int right, int bottom);
    void setMapType(int type);

    void setOnMapClickListener(@NonNull GoogleMap.OnMapClickListener listener);
    void setOnMapLongClickListener(@NonNull GoogleMap.OnMapLongClickListener listener);
    void setOnCameraChangeListener(@NonNull GoogleMap.OnCameraMoveListener listener);
    void setOnMarkerDragListener(@NonNull OnMarkerDragListener listener);

    int getMapType();
    @NonNull IProjection getProjection();
    @NonNull CameraPosition getCameraPosition();
    void moveCamera(@NonNull CameraPosition cameraPosition);
    void moveCamera(double latitude, double longitude);
    void moveCamera(@NonNull LatLngBounds bounds, int padding);

    void snapshot(@NonNull GoogleMap.SnapshotReadyCallback callback);
    @NonNull LatLngBounds getBound();

    @NonNull IMarker addMarker(@NonNull LatLng position, @NonNull Bitmap icon, float rotation, boolean draggable);
    void addPolyline(boolean geodesic, float width, @NonNull List<LatLng> positions, @NonNull List<Integer> colors);

}

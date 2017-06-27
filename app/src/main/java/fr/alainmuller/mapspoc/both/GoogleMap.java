package fr.alainmuller.mapspoc.both;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.golovin.googlemapmask.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/*package*/ class GoogleMap implements IMap {
    private static final String LOG_TAG = GoogleMap.class.getSimpleName();
    @NonNull
    private final com.google.android.gms.maps.GoogleMap mMap;

    private Polyline mPolyline;

    public GoogleMap(@NonNull com.google.android.gms.maps.GoogleMap map) {
        mMap = map;
    }

    @Override
    public boolean isMapLoadMayBeFailed() {
        return true;
    }

    @Override
    public void setMyLocationEnabled(boolean enable) {
        mMap.setMyLocationEnabled(enable);
    }

    @Override
    public void setBuildingsEnabled(boolean enable) {
        mMap.setBuildingsEnabled(enable);
    }

    @Override
    public void setTrafficEnabled(boolean enable) {
        mMap.setTrafficEnabled(false);
    }

    @Override
    public void setIndoorEnabled(boolean enable) {
        mMap.setIndoorEnabled(enable);
    }

    @Override
    public void setOnMapLoadedCallback(@Nullable com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback callback) {
        mMap.setOnMapLoadedCallback(callback);
    }

    @Override
    public void setAllGesturesEnabled(boolean enable) {
        mMap.getUiSettings().setAllGesturesEnabled(enable);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mMap.setPadding(left, top, right, bottom);
    }

    @Override
    public void setMapType(int type) {
        mMap.setMapType(type);
    }

    @Override
    public void setOnMapClickListener(@NonNull com.google.android.gms.maps.GoogleMap.OnMapClickListener listener) {
        mMap.setOnMapClickListener(listener);
    }

    @Override
    public void setOnMapLongClickListener(@NonNull com.google.android.gms.maps.GoogleMap.OnMapLongClickListener listener) {
        mMap.setOnMapLongClickListener(listener);
    }

    @Override
    public void setOnCameraChangeListener(@NonNull com.google.android.gms.maps.GoogleMap.OnCameraMoveListener listener) {
        mMap.setOnCameraMoveListener(listener);
    }

    @Override
    public void setOnMarkerDragListener(@NonNull final OnMarkerDragListener listener) {
        mMap.setOnMarkerDragListener(new com.google.android.gms.maps.GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                listener.onMarkerDragStart(new GoogleMarker(marker));
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                listener.onMarkerDrag(new GoogleMarker(marker));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                listener.onMarkerDragEnd(new GoogleMarker(marker));
            }
        });
    }

    @Override
    public int getMapType() {
        return mMap.getMapType();
    }

    @NonNull
    @Override
    public IProjection getProjection() {
        final Projection proj = mMap.getProjection();
        return new IProjection() {
            @NonNull
            @Override
            public LatLng fromScreenLocation(@NonNull Point point) {
                return proj.fromScreenLocation(point);
            }

            @NonNull
            @Override
            public Point toScreenLocation(@NonNull LatLng latLng) {
                return proj.toScreenLocation(latLng);
            }
        };
    }

    @NonNull
    @Override
    public CameraPosition getCameraPosition() {
        return mMap.getCameraPosition();
    }

    @Override
    public void moveCamera(@NonNull CameraPosition cameraPosition) {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void moveCamera(double latitude, double longitude) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
    }

    @Override
    public void moveCamera(@NonNull LatLngBounds bounds, int padding) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    @Override
    public void snapshot(@NonNull com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback callback) {
        mMap.snapshot(callback);
    }

    @NonNull
    @Override
    public LatLngBounds getBound() {
        return mMap.getProjection().getVisibleRegion().latLngBounds;
    }

    @NonNull
    @Override
    public IMarker addMarker(@NonNull LatLng position, @NonNull Bitmap icon, boolean draggable, float rotation, float anchorX, float anchorY) {
        MarkerOptions markOpts = new MarkerOptions();
        markOpts.position(position).icon(BitmapDescriptorFactory.fromBitmap(icon))
                .anchor(anchorX, anchorY)
                .rotation(rotation)
                .draggable(draggable);
        Log.d(LOG_TAG, "rotation = " + markOpts.getRotation());
        Marker marker = mMap.addMarker(markOpts);
        return new GoogleMarker(marker);
    }

    @NonNull
    @Override
    public IMarker addMarker(@NonNull LatLng position, @NonNull Bitmap icon, boolean draggable) {
        MarkerOptions markOpts = new MarkerOptions();
        markOpts.position(position).icon(BitmapDescriptorFactory.fromBitmap(icon))
                .anchor(0.5f, 0.5f)
                .rotation(0)
                .draggable(draggable);
        Marker marker = mMap.addMarker(markOpts);
        return new GoogleMarker(marker);
    }

    @Override
    public void addPolyline(boolean geodesic, float width, @NonNull List<LatLng> positions, @NonNull List<Integer> colors) {
        if (positions.size() > 1 && colors.size() >= positions.size() - 1) {
            for (int i = 0; i < positions.size() - 1; i++) {
                PolylineOptions line = new PolylineOptions().width(width).geodesic(geodesic);
                line.add(positions.get(i)).add(positions.get(i + 1)).color(colors.get(i));
                mMap.addPolyline(line);
            }
        }
    }


    @Override
    public void addStyledPolyline(boolean geodesic, float width, @NonNull List<LatLng> positions, @NonNull List<Integer> colors) {
        if (positions.size() > 1 && colors.size() >= positions.size() - 1) {
            for (int i = 0; i < positions.size() - 1; i++) {
                PolylineOptions line = new PolylineOptions().width(width).geodesic(geodesic);
                line.add(positions.get(i)).add(positions.get(i + 1)).color(colors.get(i));
                mPolyline = mMap.addPolyline(line);
                MapHelper.stylePolyline(mPolyline, true);
            }
        }
    }

    @Override
    public void updateStyledPolyline(@NonNull List<LatLng> positions) {
        mPolyline.setPoints(positions);
    }
}

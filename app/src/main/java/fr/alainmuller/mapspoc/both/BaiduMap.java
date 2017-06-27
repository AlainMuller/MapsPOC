package fr.alainmuller.mapspoc.both;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.golovin.googlemapmask.MapHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/*package*/ class BaiduMap implements IMap {
    private static final String LOG_TAG = BaiduMap.class.getSimpleName();
    @NonNull
    private final com.baidu.mapapi.map.BaiduMap mMap;
    @NonNull
    private final Handler mHandler;
    @NonNull
    private final BaiduProjection mProjection;

    @Nullable
    private GoogleMap.OnCameraMoveListener mCameraChangeListener;

    public BaiduMap(@NonNull com.baidu.mapapi.map.BaiduMap map) {
        mMap = map;
        mHandler = new Handler();
        mProjection = new BaiduProjection();
        mMap.setOnMapStatusChangeListener(new BaiduCameraChangeListener());
        mMap.setTrafficEnabled(false);
    }

    @Override
    public boolean isMapLoadMayBeFailed() {
        return false;
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
        mMap.setTrafficEnabled(enable);
    }

    @Override
    public void setIndoorEnabled(boolean enable) {
        mMap.setIndoorEnable(enable);
    }

    @Override
    public void setOnMapLoadedCallback(@Nullable final GoogleMap.OnMapLoadedCallback callback) {
        if (callback == null) {
            mMap.setOnMapLoadedCallback(null);
        } else {
            mMap.setOnMapLoadedCallback(new com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    callback.onMapLoaded();
                }
            });
        }
    }

    @Override
    public void setAllGesturesEnabled(boolean enable) {
        mMap.getUiSettings().setAllGesturesEnabled(enable);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mMap.setViewPadding(left, top, right, bottom);
    }

    @Override
    public void setMapType(int type) {
        switch (type) {
            case GoogleMap.MAP_TYPE_NONE:
                mMap.setMapType(com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NONE);
                break;
            case GoogleMap.MAP_TYPE_SATELLITE:
                mMap.setMapType(com.baidu.mapapi.map.BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case GoogleMap.MAP_TYPE_NORMAL:
            default:
                mMap.setMapType(com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    @Override
    public void setOnMapClickListener(@NonNull final GoogleMap.OnMapClickListener listener) {


        mMap.setOnMapClickListener(new com.baidu.mapapi.map.BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                listener.onMapClick(ConvertUtils.convert(latLng));
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                listener.onMapClick(ConvertUtils.convert(mapPoi.getPosition()));
                return true;
            }
        });
    }

    @Override
    public void setOnMapLongClickListener(@NonNull final GoogleMap.OnMapLongClickListener listener) {
        mMap.setOnMapLongClickListener(new com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                listener.onMapLongClick(ConvertUtils.convert(latLng));
            }
        });
    }

    @Override
    public void setOnCameraChangeListener(@NonNull final GoogleMap.OnCameraMoveListener listener) {
        mCameraChangeListener = listener;
    }

    @Override
    public void setOnMarkerDragListener(@NonNull final OnMarkerDragListener listener) {
        mMap.setOnMarkerDragListener(new com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                listener.onMarkerDrag(new BaiduMarker(marker));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                listener.onMarkerDragEnd(new BaiduMarker(marker));
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                listener.onMarkerDragStart(new BaiduMarker(marker));
            }
        });
    }

    @Override
    public int getMapType() {
        switch (mMap.getMapType()) {
            case com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NONE:
                return GoogleMap.MAP_TYPE_NONE;
            case com.baidu.mapapi.map.BaiduMap.MAP_TYPE_SATELLITE:
                return GoogleMap.MAP_TYPE_SATELLITE;
            case com.baidu.mapapi.map.BaiduMap.MAP_TYPE_NORMAL:
            default:
                return GoogleMap.MAP_TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public IProjection getProjection() {
        if (mProjection.mProjection == null) {
            mProjection.mProjection = mMap.getProjection();
        }
        return mProjection;
    }

    @NonNull
    @Override
    public CameraPosition getCameraPosition() {
        MapStatus mapStatus = mMap.getMapStatus();
        return ConvertUtils.convert(mapStatus);
    }

    @Override
    public void moveCamera(@NonNull final CameraPosition cameraPosition) {
        //to avoid calling the callBack directly
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ConvertUtils.convert(cameraPosition.target)).zoom(cameraPosition.zoom + 1.8f).overlook(-cameraPosition.tilt).rotate(cameraPosition.bearing);
                mMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });
    }

    @Override
    public void moveCamera(final double latitude, final double longitude) {
        //to avoid calling the callBack directly
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            }
        });
    }

    @Override
    public void moveCamera(@NonNull final LatLngBounds bounds, int padding) {
        //to avoid calling the callBack directly
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                com.baidu.mapapi.model.LatLngBounds.Builder builder = new com.baidu.mapapi.model.LatLngBounds.Builder().include(ConvertUtils.convert(bounds.southwest)).include(ConvertUtils.convert(bounds.northeast));
                mMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
            }
        });
    }

    @Override
    public void snapshot(@NonNull final GoogleMap.SnapshotReadyCallback callback) {
        mMap.snapshot(new com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if (!bitmap.isMutable()) {
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.recycle();
                    callback.onSnapshotReady(mutableBitmap);
                } else {
                    callback.onSnapshotReady(bitmap);
                }
            }
        });
    }

    @NonNull
    @Override
    public LatLngBounds getBound() {
        com.baidu.mapapi.model.LatLngBounds bound = mMap.getMapStatus().bound;
        return new LatLngBounds(ConvertUtils.convert(bound.southwest), ConvertUtils.convert(bound.northeast));
    }

    @NonNull
    @Override
    public IMarker addMarker(@NonNull com.google.android.gms.maps.model.LatLng position, @NonNull Bitmap icon, boolean draggable, float rotation, float anchorX, float anchorY) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ConvertUtils.convert(position))
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .anchor(anchorX, anchorY)
                .rotate(360.0f - rotation) // Baidu maps rotate counter clockwise T_T
                .draggable(draggable);
        Log.d(LOG_TAG, "rotation = " + markerOptions.getRotate());
        Marker marker = (Marker) mMap.addOverlay(markerOptions);
        return new BaiduMarker(marker);
    }

    @NonNull
    @Override
    public IMarker addMarker(@NonNull com.google.android.gms.maps.model.LatLng position, @NonNull Bitmap icon, boolean draggable) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ConvertUtils.convert(position))
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .anchor(0.5f, 0.5f)
                .rotate(0)
                .draggable(draggable);
        Marker marker = (Marker) mMap.addOverlay(markerOptions);
        return new BaiduMarker(marker);
    }

    @Override
    public void addPolyline(boolean geodesic, float width, @NonNull List<com.google.android.gms.maps.model.LatLng> positions, @NonNull List<Integer> colors) {
        List<LatLng> list = new ArrayList<>(positions.size());
        for (com.google.android.gms.maps.model.LatLng latLng : positions) {
            list.add(ConvertUtils.convert(latLng));
        }
        PolylineOptions options = new PolylineOptions().colorsValues(colors).points(list).width((int)width);
        mMap.addOverlay(options);
    }


    @Override
    public void addStyledPolyline(boolean geodesic, float width, @NonNull List<com.google.android.gms.maps.model.LatLng> positions, @NonNull List<Integer> colors) {
        List<LatLng> list = new ArrayList<>(positions.size());
        for (com.google.android.gms.maps.model.LatLng latLng : positions) {
            list.add(ConvertUtils.convert(latLng));
        }

        PolylineOptions options = new PolylineOptions().colorsValues(colors).points(list).width((int) width).dottedLine(true);
        mMap.addOverlay(options);
    }


    /*package*/ void dispose() {
        mCameraChangeListener = null;
        mHandler.removeCallbacksAndMessages(null);
        mMap.clear();
    }

    private static class BaiduProjection implements IProjection {
        @Nullable
        private Projection mProjection;

        @NonNull
        @Override
        public com.google.android.gms.maps.model.LatLng fromScreenLocation(@NonNull Point point) {
            if (mProjection != null) {
                return ConvertUtils.convert(mProjection.fromScreenLocation(point));
            } else {
                return new com.google.android.gms.maps.model.LatLng(0, 0);
            }
        }

        @NonNull
        @Override
        public Point toScreenLocation(@NonNull com.google.android.gms.maps.model.LatLng latLng) {
            if (mProjection != null) {
                return mProjection.toScreenLocation(ConvertUtils.convert(latLng));
            } else {
                return new Point();
            }
        }
    }

    private class BaiduCameraChangeListener implements com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback, com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener {
        @Override
        public void onMapDrawFrame(GL10 gl10, MapStatus mapStatus) {
            onStateChanged();
        }

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {
            onStateChanged();
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            onStateChanged();
        }

        private void onStateChanged() {
            if (mMap.getProjection() != null) {
                mHandler.removeCallbacks(mCameraChangedRunnable);
                mHandler.post(mCameraChangedRunnable);
            }
        }

        @NonNull
        private final Runnable mCameraChangedRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCameraChangeListener != null) {
                    MapStatus mapStatus = mMap.getMapStatus();
                    if (mapStatus != null) {
                        mCameraChangeListener.onCameraMove();
                    }
                }
            }
        };
    }
}

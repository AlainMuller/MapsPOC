package fr.alainmuller.mapspoc.both;

import android.support.annotation.NonNull;

import com.baidu.mapapi.map.MapStatus;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/*package*/ class ConvertUtils {

    @NonNull
    public static CameraPosition convert(@NonNull MapStatus mapStatus) {
        float tilt = -mapStatus.overlook;
        float bearing = mapStatus.rotate;
        return new CameraPosition(convert(mapStatus.target), mapStatus.zoom, tilt, bearing);
    }

    @NonNull
    public static LatLng convert(@NonNull com.baidu.mapapi.model.LatLng latLng) {
        return new LatLng(latLng.latitude, latLng.longitude);
    }

    @NonNull
    public static com.baidu.mapapi.model.LatLng convert(@NonNull LatLng latLng) {
        return new com.baidu.mapapi.model.LatLng(latLng.latitude, latLng.longitude);
    }
}

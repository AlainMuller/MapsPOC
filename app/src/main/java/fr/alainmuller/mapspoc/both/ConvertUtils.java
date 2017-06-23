package fr.alainmuller.mapspoc.both;

import android.graphics.Point;
import android.location.Location;
import android.support.annotation.NonNull;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    public static int metersToEquatorPixels(IMap map, LatLng base, float meters) {
        final double OFFSET_LON = 0.5d;

        Location baseLoc = new Location("");
        baseLoc.setLatitude(base.latitude);
        baseLoc.setLongitude(base.longitude);

        Location dest = new Location("");
        dest.setLatitude(base.latitude);
        dest.setLongitude(base.longitude + OFFSET_LON);

        double degPerMeter = OFFSET_LON / baseLoc.distanceTo(dest); // 1m は何度？
        double lonDistance = meters * degPerMeter; // m を度に変換

        IProjection proj = map.getProjection();
        Point basePt = proj.toScreenLocation(base);
        Point destPt = proj.toScreenLocation(new LatLng(base.latitude, base.longitude + lonDistance));

        return Math.abs(destPt.x - basePt.x);
    }
}

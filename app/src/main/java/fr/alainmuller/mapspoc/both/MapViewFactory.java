package fr.alainmuller.mapspoc.both;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.google.android.gms.maps.MapView;

public class MapViewFactory {

    @NonNull
    public static IMapView create(@NonNull ViewGroup mapViewLayout, boolean isGoogleMap) {
        return create(mapViewLayout, -1, isGoogleMap);
    }

    /**
     * create a MapView and add it to parent viewGroup
     * @param mapViewLayout the map view layout (parent viewGroup)
     * @param index insert index (child view index)
     * @return the Map View
     */
    @NonNull
    public static IMapView create(@NonNull ViewGroup mapViewLayout, int index, boolean isGoogleMap) {
        final Context context = mapViewLayout.getContext();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        IMapView retVal;

//        String countryCode = CoreManager.getInstance().getSmartLocationManager().getCountryCode();
//        boolean isChina = Locale.CHINA.getCountry().equalsIgnoreCase(countryCode);

        if (isGoogleMap) { // use google map
            MapView mapView = new MapView(context);

            mapView.setLayoutParams(lp);
            retVal = new GoogleMapView(mapView);
        } else { //use baidu map
//            Init Baidu map SDK
            SDKInitializer.initialize(context.getApplicationContext());
            //disable zoom control button
            BaiduMapOptions options = new BaiduMapOptions().zoomControlsEnabled(false).mapStatus(getBaiduMapInitStatus());
            com.baidu.mapapi.map.MapView mapView = new com.baidu.mapapi.map.MapView(context, options);
            mapView.setClickable(true);
            mapView.setLayoutParams(lp);
            retVal = new BaiduMapView(mapView);
        }
        //add map view to the layout
        mapViewLayout.addView(retVal.getView(), index);
        return retVal;
    }

    /**
     * set same init status like Google Map
     * (by default, baidu map show the city Beijin)
     */
    @NonNull
    private static MapStatus getBaiduMapInitStatus() {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(new LatLng(0, 0)).zoom(4);
        return builder.build();
    }
}

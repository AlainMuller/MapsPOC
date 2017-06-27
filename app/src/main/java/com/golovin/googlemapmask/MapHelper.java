package com.golovin.googlemapmask;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.alainmuller.mapspoc.R;

public final class MapHelper {
    /**
     * In kilometers.
     */
    private static final int EARTH_RADIUS = 6371;

    // Dashed line styling
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final int POLYLINE_STROKE_WIDTH_PX = 8;
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DASH);


    private MapHelper() {
        //no instance
    }

    // =============================================================================================================================
    // PUBLIC METHODS
    // =============================================================================================================================

    public static PolygonOptions createPolygonWithCircle(Context context, LatLng center, float radius) {

        return new PolygonOptions()
                .fillColor(ContextCompat.getColor(context, R.color.grey_500_transparent))
                .strokeColor(ContextCompat.getColor(context, android.R.color.black))
                .addAll(createOuterBounds())
                .addHole(createHole(center, radius))
                .strokeWidth(2);
    }


    /**
     * Styles the polyline, based on type.
     *
     * @param polyline The polyline object that needs styling.
     */
    public static void stylePolyline(final Polyline polyline, final boolean isConnected) {
        // Use a round cap at the start of the line.
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(isConnected ? 0xff00ff8a : 0xffff0000);
        polyline.setJointType(JointType.ROUND);
        polyline.setPattern(PATTERN_POLYLINE_DOTTED);
    }


    // =============================================================================================================================
    // PRIVATE METHODS
    // =============================================================================================================================

    private static List<LatLng> createOuterBounds() {
        final float delta = 0.01f;

        return new ArrayList<LatLng>() {{
            add(new LatLng(90 - delta, -180 + delta));
            add(new LatLng(0, -180 + delta));
            add(new LatLng(-90 + delta, -180 + delta));
            add(new LatLng(-90 + delta, 0));
            add(new LatLng(-90 + delta, 180 - delta));
            add(new LatLng(0, 180 - delta));
            add(new LatLng(90 - delta, 180 - delta));
            add(new LatLng(90 - delta, 0));
            add(new LatLng(90 - delta, -180 + delta));
        }};
    }

    private static Iterable<LatLng> createHole(LatLng center, float radius) {
        int points = 100; // number of corners of inscribed polygon

        double radiusLatitude = Math.toDegrees(radius / (float) EARTH_RADIUS);
        double radiusLongitude = radiusLatitude / Math.cos(Math.toRadians(center.latitude));

        List<LatLng> result = new ArrayList<>(points);

        double anglePerCircleRegion = 2 * Math.PI / points;

        for (int i = 0; i < points; i++) {
            double theta = i * anglePerCircleRegion;
            double latitude = center.latitude + (radiusLatitude * Math.sin(theta));
            double longitude = center.longitude + (radiusLongitude * Math.cos(theta));

            result.add(new LatLng(latitude, longitude));
        }

        return result;
    }
}
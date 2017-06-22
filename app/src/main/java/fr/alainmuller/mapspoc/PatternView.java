package fr.alainmuller.mapspoc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import fr.alainmuller.mapspoc.both.IMap;
import fr.alainmuller.mapspoc.both.IProjection;

/**
 * Created by jvermet on 22/06/2017.
 */

public class PatternView extends View {

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Rect mRect;
    Paint mGeofencingPaint;
    Paint mHolePaint;
    IMap mMap;
    private float mZoomLevel;
    private int mGeofencingRadius;

    public PatternView(Context context) {
        super(context);
        init();
    }

    public PatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pattern_red);
        mGeofencingPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        Shader mShader1 = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mGeofencingPaint.setShader(mShader1);

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if ((w == oldw && h == oldh) || (w <= 0 || h <= 0)) return;

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) return;

        mRect = new Rect(0, 0, getWidth(), getHeight());
        mBitmap.eraseColor(Color.TRANSPARENT);

        if (mMap != null) {
            mCanvas.drawRect(mRect, mGeofencingPaint);

            IProjection projection = mMap.getProjection();
            Point point = projection.toScreenLocation(MapsActivity.UFO);

            float newZoom = mMap.getCameraPosition().zoom;
            if (mZoomLevel != newZoom) {
                mZoomLevel = newZoom;
                mGeofencingRadius = metersToEquatorPixels(mMap, MapsActivity.UFO, 2000);
            }
            mCanvas.drawCircle(point.x, point.y, mGeofencingRadius, mHolePaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
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

    public void updateGeofencingOverlay() {
        invalidate();
    }

    public void setMap(IMap map) {
        mMap = map;
    }

}
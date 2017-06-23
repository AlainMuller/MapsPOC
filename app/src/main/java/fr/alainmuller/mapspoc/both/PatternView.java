package fr.alainmuller.mapspoc.both;

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
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import fr.alainmuller.mapspoc.R;

/**
 * Created by jvermet on 22/06/2017.
 */

public class PatternView extends View {

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Rect mRect;
    private Paint mGeofencingPaint;
    private Paint mHolePaint;
    private IMap mMap;
    private float mZoomLevel;
    private int mGeofencingRadiusMeters;
    private int mGeofencingRadiusPixels;
    private LatLng mCenterLocation;
    private int mPatternResId;

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
        mGeofencingPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        setGrayPattern();

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

        mRect = new Rect(0, 0, getWidth(), getHeight());
        mBitmap.eraseColor(Color.TRANSPARENT);

        if (mMap != null) {
            mCanvas.drawRect(mRect, mGeofencingPaint);

            IProjection projection = mMap.getProjection();
            Point point = projection.toScreenLocation(mCenterLocation);

            float newZoom = mMap.getCameraPosition().zoom;
            if (mZoomLevel != newZoom) {
                mZoomLevel = newZoom;
                mGeofencingRadiusPixels = ConvertUtils.metersToEquatorPixels(mMap, mCenterLocation, mGeofencingRadiusMeters);
            }
            mCanvas.drawCircle(point.x, point.y, mGeofencingRadiusPixels, mHolePaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    public void updateGeofencingOverlay() {
        invalidate();
    }

    public void setMap(IMap map) {
        mMap = map;
    }

    public void setCenterLocation(LatLng centerLocation) {
        mCenterLocation = centerLocation;
        invalidate();
    }

    public void setRedPattern() {
        setPattern(R.drawable.pattern_red);
    }

    public void setGrayPattern() {
        setPattern(R.drawable.pattern);
    }

    private void setPattern(@DrawableRes int patternResId) {
        if (mPatternResId == patternResId) return;
        mPatternResId = patternResId;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), patternResId);
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mGeofencingPaint.setShader(shader);
        invalidate();
    }

    public void setGeofencingRadiusMeters(int geofencingRadiusMeters) {
        mGeofencingRadiusMeters = geofencingRadiusMeters;
    }
}
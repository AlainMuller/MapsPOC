package fr.alainmuller.mapspoc.both;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapLayout extends FrameLayout {
    @Nullable
    private OnMapUserActionListener mOnMapUserActionListener;

    @Nullable
    private OnMapLayoutSizeChangedListener mOnMapLayoutSizeChangedListener;

    public MapLayout(Context context) {
        this(context, null);
    }

    public MapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnMapUserActionListener(@Nullable OnMapUserActionListener onMapUserActionListener) {
        mOnMapUserActionListener = onMapUserActionListener;
    }

    public void setOnMapLayoutSizeChangedListener(@Nullable OnMapLayoutSizeChangedListener onMapLayoutSizeChangedListener) {
        mOnMapLayoutSizeChangedListener = onMapLayoutSizeChangedListener;
        if (mOnMapLayoutSizeChangedListener != null && getWidth() != 0 && getHeight() != 0) {
            mOnMapLayoutSizeChangedListener.onSizeChanged(getWidth(), getHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mOnMapLayoutSizeChangedListener != null) {
            mOnMapLayoutSizeChangedListener.onSizeChanged(w, h);
        }
    }

    //
    // Google Map events are caught here, as the GoogleMap class doesn't provide all the listeners
    // needed for the flightplan touch features.
    //
    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        boolean eventHandled = false;

        if (mOnMapUserActionListener != null) {
            final int action = event.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    eventHandled = mOnMapUserActionListener.onMapTouched((int) event.getX(), (int) event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    eventHandled = mOnMapUserActionListener.onMapMotion((int) event.getX(), (int) event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    mOnMapUserActionListener.onMapReleased();
                    break;
            }
        }
        return eventHandled || super.dispatchTouchEvent(event);
    }


}

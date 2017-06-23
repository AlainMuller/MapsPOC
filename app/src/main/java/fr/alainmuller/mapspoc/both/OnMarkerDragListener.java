package fr.alainmuller.mapspoc.both;

public interface OnMarkerDragListener {
    void onMarkerDragStart(IMarker marker);

    void onMarkerDrag(IMarker marker);

    void onMarkerDragEnd(IMarker marker);
}
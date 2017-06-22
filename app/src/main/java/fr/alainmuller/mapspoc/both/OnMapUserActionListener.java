package fr.alainmuller.mapspoc.both;

public interface OnMapUserActionListener {
    boolean onMapTouched(int x, int y);

    boolean onMapMotion(int x, int y);

    void onMapReleased();
}

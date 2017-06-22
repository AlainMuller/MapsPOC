package fr.alainmuller.mapspoc.both;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public interface IMapView {
    @NonNull
    View getView();

    void onCreate(@Nullable Bundle savedInstanceState);
    void onResume();
    void onPause();
    void onDestroy();
    void onLowMemory();
    void onSaveInstanceState(@NonNull Bundle outState);
    void getMapAsync(@NonNull IOnMapReadyCallback callback);
}

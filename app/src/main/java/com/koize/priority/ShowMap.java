package com.koize.priority;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

public class ShowMap extends AppCompatActivity implements OnMapReadyCallback {
    private TextView currentLatLon;
    private TextView locationLatLon;
    private double locationLat;
    private double locationLon;
    private double currentLat;
    private double currentLon;
    String eventTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        currentLatLon = findViewById(R.id.gmap_current_lat_long);
        locationLatLon = findViewById(R.id.gmap_location_lat_long);
        GPSTracker gpsTracker = new GPSTracker(ShowMap.this);
        currentLat = gpsTracker.getLatitude();
        currentLon = gpsTracker.getLongitude();
        currentLatLon.setText("Current Location: " + currentLat + ", " + currentLon);
        locationLat = getIntent().getDoubleExtra("lat", 0);
        locationLon = getIntent().getDoubleExtra("lon", 0);
        eventTitle = getIntent().getStringExtra("title");
        locationLatLon.setText("Event Location: " + eventTitle + ": " + locationLat + ", " + locationLon);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng currentLocation = new LatLng(currentLat, currentLon);
        map.addMarker(new MarkerOptions()
                .position(currentLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_channelnew))
                .title("Your location"));

        LatLng selectedLocation = new LatLng(locationLat, locationLon);
        map.addMarker(new MarkerOptions()
                .position(selectedLocation)
                .title("Event location: " + eventTitle ));
        map.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation));


    }
    public void onMyLocationClick(@NonNull Location location) {

    }

    public boolean onMyLocationButtonClick() {

        return false;
    }
}
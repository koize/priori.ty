package com.koize.priority;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

public class GetMap extends AppCompatActivity implements OnMapReadyCallback {
    private TextView currentLatLon;
    private TextView locationLatLon;
    private double locationLat;
    private double locationLon;
    private double currentLat;
    private double currentLon;
    private Chip getCurrentLocation;
    private Chip save;
    private Chip cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        currentLatLon = findViewById(R.id.gmap_current_lat_long);
        getCurrentLocation = findViewById(R.id.gmap_get_current_location);
        GPSTracker gpsTracker = new GPSTracker(GetMap.this);
        currentLat = gpsTracker.getLatitude();
        currentLon = gpsTracker.getLongitude();
        currentLatLon.setText("Current Location: " + currentLat + ", " + currentLon);
        getCurrentLocation.setOnClickListener(v -> {
            currentLat = gpsTracker.getLatitude();
            currentLon = gpsTracker.getLongitude();
            currentLatLon.setText("Current Location: " + currentLat + ", " + currentLon);
        });
        save = findViewById(R.id.gmap_save);
        save.setOnClickListener(v -> {

        });
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
                .title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make((findViewById(android.R.id.content)), "Please enable location permission", Snackbar.LENGTH_LONG).show();
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this::onMyLocationButtonClick);
        map.setOnMyLocationClickListener(this::onMyLocationClick);

    }
    public void onMyLocationClick(@NonNull Location location) {

    }

    public boolean onMyLocationButtonClick() {

        return false;
    }
}